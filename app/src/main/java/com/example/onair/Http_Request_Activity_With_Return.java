package com.example.onair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Http_Request_Activity_With_Return extends AppCompatActivity {
    private ProgressDialog progressDialog ;
    private boolean exception  = false;

    private int departure_year, return_year;
    private String departure_month_String, departure_day_String, return_day_String, return_month_String;

    private String originAirport_forAPI, destinationAirport_forAPI;
    private String departureDate_forAPI, returnDate_forAPI, nonStop_forAPI, storeCurrency, travelClass_forAPI, maxPrice_forAPI, adults_forAPI;
    public static final String TAG = "Http_Request_Activity_w";

    // arraylist for all flights separately and for itinerary
    ArrayList<Flight> allFlights_theList = new ArrayList<>();
    ArrayList<Itinerary> the_list_of_itineraries = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.with__return_activity_http__request_);

        listView = (ListView) findViewById(R.id.listReturn);

        // ProgressDialog όσο τα αποτελέσματα φορτώνουν
        progressDialog = new ProgressDialog(Http_Request_Activity_With_Return.this);
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
        return_year = getIntent().getIntExtra("r_year", 0);
        return_month_String = getIntent().getStringExtra("r_month");
        return_day_String = getIntent().getStringExtra("r_day");

        //get currency from sharedpreferences from settings and main activity
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        storeCurrency = sharedPreferences.getString("currency", "");
        Log.i(getClass().toString(), "this is ---->>>" +  storeCurrency);
        SharedPreferences sharedPreferencesFromMain = getSharedPreferences("ExtraChoices", Context.MODE_PRIVATE);
        travelClass_forAPI = sharedPreferencesFromMain.getString("travel_class", "");
        adults_forAPI = sharedPreferencesFromMain.getString("adults_number", "");
        nonStop_forAPI = sharedPreferencesFromMain.getString("nonstop", "");
        maxPrice_forAPI = sharedPreferencesFromMain.getString("max_price", "");
        setTitle(departureDate_forAPI);

        //Βάζει την ημερομινία σε σωστή μορφή
        departureDate_forAPI = departure_year + "-" + departure_month_String + "-" + departure_day_String;
        returnDate_forAPI = return_year + "-" + return_month_String + "-" + return_day_String;

        // set title
        setTitle("adults: " + adults_forAPI + "| " + travelClass_forAPI);

        new JSONTaskR().execute();
    }

    public class JSONTaskR extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                final String baseUrl = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";
                final String originParam = "origin";
                final String destinationParam = "destination";
                final String departureDateParam = "departure_date";
                final String returnDateParam = "return_date";
                final String adultsParam = "adults";
                final String currencyParam = "currency";
                final String travelClassParam = "travel_class";
                final String maxPriceParam = "max_price";
                final String nonStopParam = "nonstop";
                final String ApiKeyParam = "apikey";

                Uri buildUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(originParam, originAirport_forAPI)
                        .appendQueryParameter(destinationParam, destinationAirport_forAPI)
                        .appendQueryParameter(departureDateParam, departureDate_forAPI)
                        .appendQueryParameter(returnDateParam, returnDate_forAPI)
                        .appendQueryParameter(adultsParam, adults_forAPI)
                        .appendQueryParameter(currencyParam, storeCurrency)
                        .appendQueryParameter(travelClassParam, travelClass_forAPI)
                        .appendQueryParameter(nonStopParam, nonStop_forAPI)
                        .appendQueryParameter(ApiKeyParam, BuildConfig.LOW_FARE_FLIGHTS_API_KEY)
                        .build();

                Log.i(getClass().toString(), buildUri.toString());

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

                String currency = parentObject.getString("currency");
                JSONArray results = parentObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {

                    // itineraries list without price and taxes
                    ArrayList<Itinerary> itineraries_without_price = new ArrayList<>();

                    JSONObject outside = results.getJSONObject(i);
                    JSONArray itineraries = outside.getJSONArray("itineraries");

                    for (int j = 0; j < itineraries.length(); j++) {

                        //class for a single flight
                        Flight to_go_flight, return_flight;

                        // cast itinerary
                        Itinerary itinerary = new Itinerary();

                        JSONObject outbound = itineraries.getJSONObject(j).getJSONObject("outbound");
                        JSONArray flights_togo_JSON = outbound.getJSONArray("flights");
                        for (int k = 0; k < flights_togo_JSON.length(); k++) {

                            to_go_flight = new Flight();

                            JSONObject inside = flights_togo_JSON.getJSONObject(k);
                            to_go_flight.setDeparts_at(inside.getString("departs_at"));
                            to_go_flight.setArrives_at(inside.getString("arrives_at"));

                            JSONObject origin = inside.getJSONObject("origin");
                            to_go_flight.setOrigin_airport(origin.getString("airport"));
                            Log.i("first ", to_go_flight.getOrigin_airport());

                            JSONObject destination = inside.getJSONObject("destination");
                            to_go_flight.setDestination_airport(destination.getString("airport"));
                            to_go_flight.setMarketing_airline(inside.getString("marketing_airline"));
                            to_go_flight.setOperating_airline(inside.getString("operating_airline"));
                            to_go_flight.setFlight_number(inside.getString("flight_number"));
                            to_go_flight.setAircraft(inside.getString("aircraft"));

                            JSONObject booking_info = inside.getJSONObject("booking_info");
                            to_go_flight.setTravel_class(booking_info.getString("travel_class"));
                            to_go_flight.setBooking_code(booking_info.getString("booking_code"));
                            to_go_flight.setSeats_remaining(booking_info.getInt("seats_remaining"));

                            // αποθήκευση στην ArrayList
                            allFlights_theList.add(to_go_flight);

                            // add flight to arraylist<Flight> from class Itinerary
                            itinerary.Outbound_list_adder(to_go_flight);
                        }

                        JSONObject inbound = itineraries.getJSONObject(j).getJSONObject("inbound");
                        JSONArray flights_return_JSON = inbound.getJSONArray("flights");
                        for (int k = 0; k < flights_return_JSON.length(); k++) {

                            return_flight = new Flight();

                            JSONObject inside = flights_return_JSON.getJSONObject(k);
                            return_flight.setDeparts_at(inside.getString("departs_at"));
                            return_flight.setArrives_at(inside.getString("arrives_at"));

                            JSONObject origin = inside.getJSONObject("origin");
                            return_flight.setOrigin_airport(origin.getString("airport"));
                            Log.i("sec ", return_flight.getOrigin_airport());

                            JSONObject destination = inside.getJSONObject("destination");
                            return_flight.setDestination_airport(destination.getString("airport"));
                            return_flight.setMarketing_airline(inside.getString("marketing_airline"));
                            return_flight.setOperating_airline(inside.getString("operating_airline"));
                            return_flight.setFlight_number(inside.getString("flight_number"));
                            return_flight.setAircraft(inside.getString("aircraft"));

                            JSONObject booking_info = inside.getJSONObject("booking_info");
                            return_flight.setTravel_class(booking_info.getString("travel_class"));
                            return_flight.setBooking_code(booking_info.getString("booking_code"));
                            return_flight.setSeats_remaining(booking_info.getInt("seats_remaining"));

                            //αποθήκευση στην ArrayList
                            allFlights_theList.add(return_flight);

                            //add flight to arraylist<Flight> from class Itinerary
                            itinerary.Inbound_list_adder(return_flight);
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
                        Log.i("mesa mesa", iti.getTotal_price() +"");

                        //add itinerary to the list
                        the_list_of_itineraries.add(iti);
                    }
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                exception = true;
            } catch (IOException ex) {
                ex.printStackTrace();
                exception = true;
            } catch (JSONException e) {
                e.printStackTrace();
                exception = true;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //TO-DO καινουργια μεθοδο για exceptions αναλυτικα για το καθενα
            if(exception){
                Toast.makeText(getApplicationContext(), "ERROR Occurred", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Http_Request_Activity_With_Return.this , MainActivity.class));
            }

            //new custom list adapter
            myListAdapter_vol2_return adapter = new myListAdapter_vol2_return(listView.getContext(), the_list_of_itineraries);
            listView.setAdapter(adapter);
            Log.i(TAG, the_list_of_itineraries.size() +" <---before adapter list size");

            // Με το που περάστουν τα αποτελέσματα στον adapter και εμφανιστουν και στην οθονη ακυρώνεται το progressDialog
            progressDialog.cancel();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int Location, long l) {
                    Intent intent = new Intent(getApplicationContext(), Details_activity_with_return.class);
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
        if(storeCurrency.equals("USD")) {
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
