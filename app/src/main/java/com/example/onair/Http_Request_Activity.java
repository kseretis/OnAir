package com.example.onair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class Http_Request_Activity extends AppCompatActivity {

    private ProgressDialog progressDialog ;

    private int departure_year;
    private String departure_month_String, departure_day_String;
    private String originAirport_forAPI, destinationAirport_forAPI;
    private String departureDate_forAPI, nonStop_forAPI, storeCurrency, travelClass_forAPI, maxPrice_forAPI, adults_forAPI;
    public static final String TAG = "Http_Request_Activity";

    // arraylist for all flights separately and for itinerary
    ArrayList<Flight> allFlights_theList = new ArrayList<>();
    ArrayList<Itinerary> the_list_of_itineraries = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http__request_);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.list);

        // ProgressDialog όσο τα αποτελέσματα φορτώνουν
        progressDialog = new ProgressDialog(Http_Request_Activity.this);
        progressDialog.setTitle("Searching for results...");
        progressDialog.setMessage("Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Πέρνει της πληροφορίες απο το πρωτο activity
        originAirport_forAPI = getIntent().getStringExtra("loctown");
        destinationAirport_forAPI = getIntent().getStringExtra("destown");
        departure_year = getIntent().getIntExtra("d_year", 0);
        departure_month_String = getIntent().getStringExtra("d_month");
        departure_day_String = getIntent().getStringExtra("d_day");
        setTitle(departureDate_forAPI);

        //get currency from sharedpreferences from settings and main activity
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        storeCurrency = sharedPreferences.getString("currency", null);
        travelClass_forAPI = sharedPreferences.getString("travel_class", null);
        adults_forAPI = sharedPreferences.getString("adults_number", null);
        nonStop_forAPI = sharedPreferences.getString("nonstop", null);
        maxPrice_forAPI = sharedPreferences.getString("max_price", null);

        //Βάζει την ημερομινία σε σωστή μορφή
        departureDate_forAPI = departure_year + "-" + departure_month_String + "-" + departure_day_String;

        // set title
        setTitle("adults: " + adults_forAPI + "| " + travelClass_forAPI);

        new JSONTask().execute();
    }

    public class JSONTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                final String baseUrl = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";
                final String originParam = "origin";
                final String destinationParam = "destination";
                final String departureDateParam = "departure_date";
                final String adultsParam = "adults";
                final String currencyParam = "currency";
                final String travelClassParam = "travel_class";
                final String maxPriceParam = "max_price";
                final String nonStopParam = "nonstop";
                final String ApiKeyParam = "apikey";

                Uri.Builder buildUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(originParam, originAirport_forAPI)
                        .appendQueryParameter(destinationParam, destinationAirport_forAPI)
                        .appendQueryParameter(departureDateParam, departureDate_forAPI)
                        .appendQueryParameter(ApiKeyParam, BuildConfig.LOW_FARE_FLIGHTS_API_KEY);

                if(storeCurrency != null)
                    buildUri.appendQueryParameter(currencyParam, storeCurrency);
                if(travelClass_forAPI != null)
                    buildUri.appendQueryParameter(travelClassParam, travelClass_forAPI);
                if(adults_forAPI != null)
                    buildUri.appendQueryParameter(adultsParam, adults_forAPI);
                if(nonStop_forAPI != null)
                    buildUri.appendQueryParameter(nonStopParam, nonStop_forAPI);
                if(maxPrice_forAPI != null)
                    buildUri.appendQueryParameter(maxPriceParam, maxPrice_forAPI);

                Log.i(TAG, buildUri.toString());

                URL url = new URL(buildUri.toString());

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJSon = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJSon);

                JSONArray results = parentObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {

                    // itineraries list without price and taxes
                    ArrayList<Itinerary> itineraries_without_price = new ArrayList<>();

                    JSONObject outside = results.getJSONObject(i);
                    JSONArray itineraries = outside.getJSONArray("itineraries");

                    for (int j = 0; j < itineraries.length(); j++) {

                        //class Flight
                        Flight flight;

                        // cast itinerary
                        Itinerary itinerary = new Itinerary();

                        JSONObject outbound = itineraries.getJSONObject(j).getJSONObject("outbound");
                        JSONArray flights = outbound.getJSONArray("flights");
                        for (int k = 0; k < flights.length(); k++) {

                            flight = new Flight();

                            JSONObject inside = flights.getJSONObject(k);
                            flight.setDeparts_at(inside.getString("departs_at"));
                            flight.setArrives_at(inside.getString("arrives_at"));

                            JSONObject origin = inside.getJSONObject("origin");
                            flight.setOrigin_airport(origin.getString("airport"));

                            JSONObject destination = inside.getJSONObject("destination");
                            flight.setDestination_airport(destination.getString("airport"));
                            flight.setMarketing_airline(inside.getString("marketing_airline"));
                            flight.setOperating_airline(inside.getString("operating_airline"));
                            flight.setFlight_number(inside.getString("flight_number"));
                            flight.setAircraft(inside.getString("aircraft"));

                            JSONObject booking_info = inside.getJSONObject("booking_info");
                            flight.setTravel_class(booking_info.getString("travel_class"));
                            flight.setBooking_code(booking_info.getString("booking_code"));
                            flight.setSeats_remaining(booking_info.getInt("seats_remaining"));

                            // αποθήκευση στην ArrayList
                            allFlights_theList.add(flight);

                            // add flight to arraylist<Flight> from class Itinerary
                            itinerary.Outbound_list_adder(flight);
                        }
                        itineraries_without_price.add(itinerary);
                    }

                    JSONObject fare = outside.getJSONObject("fare");
                    String total_price = fare.getString("total_price");
                    JSONObject price_per_adult = fare.getJSONObject("price_per_adult");
                    String total_fare = price_per_adult.getString("total_fare");
                    String tax = price_per_adult.getString("tax");
                    JSONObject restrictions = fare.getJSONObject("restrictions");
                    Boolean refundable = restrictions.getBoolean("refundable");
                    Boolean change_penalties = restrictions.getBoolean("change_penalties");
                    for(Itinerary iti: itineraries_without_price){
                        // append more data to itinerary object class
                        iti.setTotal_price(total_price);
                        iti.setTotal_fare(total_fare);
                        iti.setTax(tax);
                        iti.setRefundable(refundable);
                        iti.setChange_penalties(change_penalties);

                        //add itinerary to the list
                        the_list_of_itineraries.add(iti);
                    }

                }
            } catch (MalformedURLException ex) {
                Log.i(TAG, "MalformedURLException exception");
            } catch (IOException ex) {
                Log.i(TAG, "IO exception. No results found");
                Intent error_intent = new Intent(Http_Request_Activity.this, ErrorActivity.class);
                startActivity(error_intent);
            } catch (JSONException e) {
                Log.i(TAG, "JSON exception");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Log.i(TAG, "size: " + the_list_of_itineraries.size() +"");

            //custom list adapter
            myListAdapter_vol1_single adapter = new myListAdapter_vol1_single(listView.getContext(), the_list_of_itineraries);
            listView.setAdapter(adapter);

            // Με το που περάστουν τα αποτελέσματα στον adapter και εμφανιστουν και στην οθονη ακυρώνεται το progressDialog
            progressDialog.cancel();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int Location, long l) {
                    Intent intent = new Intent(getApplicationContext(), Details_activity.class);
                    Bundle bundle = new Bundle();

                    bundle.putSerializable("itinerary", the_list_of_itineraries.get(Location));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(storeCurrency == null || storeCurrency.equals("USD")){
            getMenuInflater().inflate(R.menu.menu1, menu);
            return true;
        }
        else if(storeCurrency.equals("EUR")) {
            getMenuInflater().inflate(R.menu.menu2, menu);
            return true;
        }
        else if(storeCurrency.equals("GBP")) {
            getMenuInflater().inflate(R.menu.menu3, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.dollar)
            Toast.makeText(getApplicationContext(), "All prices shown are in Dollar (USD)", Toast.LENGTH_LONG).show();
        else if(id == R.id.euro)
            Toast.makeText(getApplicationContext(), "All prices shown are in Euro (EUR)", Toast.LENGTH_LONG).show();
        else if(id == R.id.pounds)
            Toast.makeText(getApplicationContext(), "All prices shown are in Pounds (GBP)", Toast.LENGTH_LONG).show();
        return super.onOptionsItemSelected(item);
    }
}

