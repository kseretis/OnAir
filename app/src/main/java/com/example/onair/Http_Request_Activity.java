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
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class Http_Request_Activity extends AppCompatActivity {
    private ProgressDialog progressDialog ;
    private boolean exception = false;
    String labelGo, labelDestination;

    private int departure_year;
    private String departure_month_String, departure_day_String;
    private String originAirport_forAPI, destinationAirport_forAPI;
    private String departureDate_forAPI, nonStop_forAPI, storeCurrency, travelClass_forAPI, maxPrice_forAPI, adults_forAPI;
    ArrayList<HashMap<String, String>> theList;
    ArrayList<HashMap<String, String>> onlyForPrint;
    HashMap<String, String> AIRPORT_LIST = new HashMap<String, String>();

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
       /* progressDialog = new ProgressDialog(Http_Request_Activity.this);
        progressDialog.setTitle("Searching for results...");
        progressDialog.setMessage("Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();*/

        listView = (ListView) findViewById(R.id.list);

        //Πέρνει της πληροφορίες απο το πρωτο activity
        originAirport_forAPI = getIntent().getStringExtra("loctown");
        destinationAirport_forAPI = getIntent().getStringExtra("destown");
        departure_year = getIntent().getIntExtra("d_year", 0);
        departure_month_String = getIntent().getStringExtra("d_month");
        departure_day_String = getIntent().getStringExtra("d_day");
        labelGo = getIntent().getStringExtra("labelGo");
        labelDestination = getIntent().getStringExtra("labelDestination");

        //get currency from sharedpreferences from settings and main activity
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        storeCurrency = sharedPreferences.getString("currency", "");
        Log.i(getClass().toString(), "this is ---->>>" +  storeCurrency);
        SharedPreferences sharedPreferencesFromMain = getSharedPreferences("ExtraChoices", Context.MODE_PRIVATE);
        travelClass_forAPI = sharedPreferencesFromMain.getString("travel_class", "");
        adults_forAPI = sharedPreferencesFromMain.getString("adults_number", "");
        nonStop_forAPI = sharedPreferencesFromMain.getString("nonstop", "");
        maxPrice_forAPI = sharedPreferencesFromMain.getString("max_price", "");

        //Βάζει την ημερομινία σε σωστή μορφή
        departureDate_forAPI = departure_year + "-" + departure_month_String + "-" + departure_day_String;

        new JSONTask().execute();
    }

    public void findAirLine() {
        for(int i=0; i<theList.size(); i++){
            String mar = null, ope = null;
            HashMap<String, String> find = new HashMap<>(theList.get(i));
            String marketing = find.get("marketing_airline");
            String operating = find.get("operating_airline");

            try {
                mar = new findAirline().execute(marketing).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            try {
                ope = new findAirline().execute(operating).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            find.put("marketing_airline", mar);
            find.put("operating_airline", ope);
            theList.set(i, find);
        }
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

                Uri buildUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(originParam, originAirport_forAPI)
                        .appendQueryParameter(destinationParam, destinationAirport_forAPI)
                        .appendQueryParameter(departureDateParam, departureDate_forAPI)
                        .appendQueryParameter(adultsParam, adults_forAPI)
                        .appendQueryParameter(currencyParam, storeCurrency)
                        .appendQueryParameter(travelClassParam, travelClass_forAPI)
                        .appendQueryParameter(nonStopParam, nonStop_forAPI)
                        .appendQueryParameter(ApiKeyParam, BuildConfig.LOW_FARE_FLIGHTS_API_KEY)
                        .build();

                // check if there is a max price or not
                if(!maxPrice_forAPI.equals("none"))
                    buildUri.buildUpon().appendQueryParameter(maxPriceParam, maxPrice_forAPI).build();  // δεν φαίνεται να μπαίνει εδω

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

                    //class itinerary
                    Itinerary itinerary = new Itinerary();

                    //class Flight
                    Flight flight;

                    JSONObject outside = results.getJSONObject(i);
                    JSONArray itineraries = outside.getJSONArray("itineraries");

                    for (int j = 0; j < itineraries.length(); j++) {
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

                            //αποθήκευση στην ArrayList
                            allFlights_theList.add(flight);

                            //add flight to arraylist<Flight> from class Itinerary
                            itinerary.Outbound_list_adder(flight);
                        }
                    }
                    // append more data to itinerary object class
                    JSONObject fare = outside.getJSONObject("fare");
                    itinerary.setTotal_price(fare.getString("total_price"));

                    JSONObject price_per_adult = fare.getJSONObject("price_per_adult");
                    itinerary.setTotal_fare(price_per_adult.getString("total_fare"));
                    itinerary.setTax(price_per_adult.getString("tax"));

                    JSONObject restrictions = fare.getJSONObject("restrictions");
                    itinerary.setRefundable(restrictions.getBoolean("refundable"));
                    itinerary.setChange_penalties(restrictions.getBoolean("change_penalties"));

                    //add itinerary to the list
                    the_list_of_itineraries.add(itinerary);
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

            if(exception){
                Toast.makeText(getApplicationContext(), "ERROR Occurred", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Http_Request_Activity.this , MainActivity.class));
            }

            //custom list adapter
            myListAdapter adapter = new myListAdapter(listView.getContext(), the_list_of_itineraries);
            listView.setAdapter(adapter);

            // Με το που περάστουν τα αποτελέσματα στον adapter και εμφανιστουν και στην οθονη ακυρώνεται το progressDialog
         //   progressDialog.cancel();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int Location, long l) {

                    /* στην λίστα onlyForPrint στην θεση location αποθηκεύεται τπ σημειο που βρίσκονται τα δεδομένα στην λίστα
                     * theList. Στην onlyForPrint βρίσκονται ΜΟΝΟ τα στοιχεία που θα εμφανιστούν "συμμαζεμένα"στην οθόνη του χρήστη.
                     * και στην theList έχει αναλυτικά τις πληροφορίες για κάθε πτηση*/
                    int counter = Integer.valueOf(onlyForPrint.get(Location).get("location"));
                    ArrayList<HashMap<String, String>> arrayListToGo = new ArrayList<HashMap<String, String>>();
                    Boolean firstFlight = true;
                    do{
                        if(!firstFlight)
                            counter++;
                        /* Ελέγχει εάν το μηκος του πεδίου που είναι αποθηκευμενος ο κωδικός της αεροπορικής εταιρίας είναι μκροτερος ή ισος του 2
                         * Εάν ναι μπαινει στην if και κανει τα απαραίτητα request για να βρει το ονομα της εταιρίας, έτσι την επόμενη φορά που
                         * θα πατηθεί η ίδια πτηση το πεδίου με την αεροπορική εταιρία θα είναι ήδη γεμάτο και δεν θα χρειαστεί να ξανα-κάνει τα
                         * request. Αυτό το if κάνει τον αλγόριθμο πιο γρήγορα μετα την δευτερη φορά που θα πατηθει η ιδια επιλογη */
                        if(theList.get(counter).get("marketing_airline").length() <= 2) {
                        /* Για τον λογο ότι το κλειδί που διαθέτουμε δεν επιτρεπει παραπανω παο 60 request το λεπτο σε καθε κλικ του χρηστη
                         * για να δει αναλυτικοτερα τις πληροφοριες στέλνει εκεινη την ωρα τα request για να βρει τις αεροπορικες εταιριες
                         * Στην περιπτωση που ειχαμε την δυνατοτηα για απεριοριστα request, σε ένα αλλαο thread θα έψαχνε παραλληλα της
                         * εταιριες όσο ο χρηστης θα εβλεπε την αρχική λιστα */
                            HashMap<String, String> test = new HashMap<String, String>(theList.get(counter));
                            String marketing = test.get("marketing_airline");
                        /* Με την επιλογή ενος στοιχείου απο την λίστα ανοιγει ενα αλλο activity στο οποίο στέλνονται πληροφοριες και πριν απο
                         * στέλνοντε κάποια request για να βρεθούν οι αεροπορικές εταιρείες. Στην περίπτωση που έχουμε κάποιο δρομολόγιο με αρκετές
                         * πτήσεις τα request είναι πολλά με αποτέλεσμα να καθυστερή η εφαρμογή μας. Για αυτο αποθηκέυει στην μεταβλητη Previous_Flight_Airline
                         * τον κωδικο της εταιρείας για πρωτη φορά και κάνει request. Στην δεύτερη πτήση θα ελένξει εάν ο κωδικός της εταιρείας είναι ίδιος
                         * με αυτόν της προηγούμενης πτήσης, εάν ναι αποθηκεέυι το ίδιο ονομα εάν όχι κανει ξανα request. Με αυτόν τον τρόπο μπορούμε να
                         * γλυτώσουμε χρόνο σε περίπτωση που υπάρχουν πτήσεις σε δρομλόγια με κοινές αεροπορικές εταιρείες.*/
                            if (!AIRPORT_LIST.containsKey(marketing)) {
                                try {
                                    String mar = new findAirline().execute(marketing).get();
                                    AIRPORT_LIST.put(marketing, mar);
                                    test.put("marketing_airline", mar);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                test.put("marketing_airline", AIRPORT_LIST.get(marketing));

                            theList.set(counter, test);
                        }
                        firstFlight = false;
                        arrayListToGo.add(theList.get(counter));
                    }while(!theList.get(counter).get("destination_airport").equals(destinationAirport_forAPI));

                    Intent intent = new Intent(Http_Request_Activity.this, Detail_Activity.class);
                    intent.putExtra("detailsToGo", arrayListToGo);
                    intent.putExtra("currency", storeCurrency);
                    intent.putExtra("labelGo", labelGo);
                    intent.putExtra("labelDestination", labelDestination);
                    intent.putExtra("adults", adults_forAPI);
                    startActivity(intent);
                }
            });
        }
    }

    public class findAirline extends AsyncTask<String, String, String> {
        String name;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String param = params[0];

            try {
                final String baseUrl = "https://iatacodes.org/api/v6/airlines?";
                final String apiKeyParam = "api_key";
                final String codeParam = "code";
                Uri buildUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(apiKeyParam, "01b77c00-91c5-4417-ba5f-0e940713aea1")
                        .appendQueryParameter(codeParam, param).build();

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
                JSONArray response = parentObject.getJSONArray("response");
                for(int i=0; i<response.length(); i++){
                    JSONObject ob = response.getJSONObject(i);
                    name = ob.getString("name");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return name;
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

