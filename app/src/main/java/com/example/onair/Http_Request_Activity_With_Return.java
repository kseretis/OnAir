package com.example.onair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class Http_Request_Activity_With_Return extends AppCompatActivity {
    private ListView listView;
    private ProgressDialog progressDialog ;
    private static String urlString;
    private boolean exception  = false;
    String labelGo, labelDestination;

    private int departure_year, return_year;
    private String departure_month_String, departure_day_String, return_day_String, return_month_String;;
    private String locationAirport, destinationAirport;
    private String mera_anaxwrishs, mera_epistrofhs, storeCurrency, nonstop, travel_class, max_price ;
    ArrayList<HashMap<String, String>> theListReturn;
    ArrayList<HashMap<String, String>> theListToGo;
    ArrayList<HashMap<String, String>> onlyForPrintReturn;
    HashMap<String, String> AIRPORT_LIST = new HashMap<String, String>();
    private int NUMBER_OF_ADULTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.with__return_activity_http__request_);

        // ProgressDialog όσο τα αποτελέσματα φορτώνουν
        progressDialog = new ProgressDialog(Http_Request_Activity_With_Return.this);
        progressDialog.setTitle("Searching for results...");
        progressDialog.setMessage("Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        listView = (ListView) findViewById(R.id.listReturn);
        theListToGo = new ArrayList<HashMap<String, String>>();
        theListReturn = new ArrayList<HashMap<String, String>>();
        onlyForPrintReturn = new ArrayList<HashMap<String, String>>();

        //Πέρνει της πληροφορίες απο το πρωτο activity
        locationAirport = getIntent().getStringExtra("loctown");
        destinationAirport = getIntent().getStringExtra("destown");
        departure_year = getIntent().getIntExtra("d_year", 0);
        departure_month_String = getIntent().getStringExtra("d_month");
        departure_day_String = getIntent().getStringExtra("d_day");
        return_year = getIntent().getIntExtra("r_year", 0);
        return_month_String = getIntent().getStringExtra("r_month");
        return_day_String = getIntent().getStringExtra("r_day");
        nonstop = getIntent().getStringExtra("nonstop");
        storeCurrency = getIntent().getStringExtra("currency");
        labelGo = getIntent().getStringExtra("labelGo");
        labelDestination = getIntent().getStringExtra("labelDestination");
        NUMBER_OF_ADULTS = getIntent().getIntExtra("adults", 0);
        travel_class = getIntent().getStringExtra("travel_class");
        max_price = getIntent().getStringExtra("max_price");

        //τιτλος
        if(NUMBER_OF_ADULTS > 1)
            setTitle(locationAirport + " <-> " + destinationAirport +" | "+ NUMBER_OF_ADULTS + " persons");
        else
            setTitle(locationAirport + " <-> " + destinationAirport +" | "+ NUMBER_OF_ADULTS + " person");

        //Βάζει την ημερομινία σε σωστή μορφή
        mera_anaxwrishs = departure_year + "-" + departure_month_String + "-" + departure_day_String;
        mera_epistrofhs = return_year + "-" + return_month_String + "-" + return_day_String;

        //καλει την παρακατω μεθοδο για να διαμορφώσει το url
        configuration_URL_STRING();

        //https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?apikey=4JiBVoAAA8rLiuEAZPrkbaIxXkBohGZt&origin=SKG&destination=ATH&departure_date=2016-12-30

        new JSONTaskR().execute();
    }
    public void configuration_URL_STRING(){
        urlString = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?apikey=4JiBVoAAA8rLiuEAZPrkbaIxXkBohGZt&origin="
                + locationAirport + "&destination=" + destinationAirport + "&departure_date=" + mera_anaxwrishs + "&return_date=" + mera_epistrofhs;
        urlString += ""+nonstop;
        if(!storeCurrency.equals("USD"))
            urlString += "&currency="+ storeCurrency;
        if(NUMBER_OF_ADULTS > 1)
            urlString += "&adults=" + NUMBER_OF_ADULTS+"";
        if(travel_class != null)
            urlString += "&travel_class="+ travel_class;
        if(!max_price.equals("none"))
            urlString += "&max_price="+ max_price;
    }

    public class JSONTaskR extends AsyncTask<Void, Void, Void> {
        public String currency = null;
        private String departs_at, arrives_at, origin_airport, destination_airport, marketing_airline, operating_airline,
                flight_number, aircraft, travel_class, booking_code, total_price, total_fare, tax;
        int seats_remaining;
        boolean refundable, change_penalties;
        String refundableString, change_penaltiesString;

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);

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
                int flightsLengthToGo = 0;
                int flightsLengthReturn = 0;
                JSONObject parentObject = new JSONObject(finalJSon);

                currency = parentObject.getString("currency");
                JSONArray results = parentObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject outside = results.getJSONObject(i);
                    JSONArray itineraries = outside.getJSONArray("itineraries");
                    /* Με την μορφη JSON που πέρνουμε τα δεδομένα απο το http request υπάρχουν
                     * αρκετά δρομολόγια συνεχόμενα με κοινά χαρακτηριστικά, όπως η τιμή, ο φόρος,
                     * το εάν είναι επιστρέψιμο το εισιτήριο και οι κρατήσεις(ποινές) απο την επιστροφή
                     * του εισιτήριο. Η παρακάτω ακέραια μεταβλητή (inineraries_with_the_same_price)
                     * μετράει πόσες συνεχόμενες πτήσεις έχουν κοινά τα παραπάνω χαρακτηριστικά */
                    for (int j = 0; j < itineraries.length(); j++) {
                        JSONObject testttt = itineraries.getJSONObject(j);
                        JSONObject outbound = testttt.getJSONObject("outbound");
                        JSONArray flightsToGo = outbound.getJSONArray("flights");
                        for (int k = 0; k < flightsToGo.length(); k++) {
                            JSONObject inside = flightsToGo.getJSONObject(k);
                            departs_at = inside.getString("departs_at");
                            arrives_at = inside.getString("arrives_at");
                            JSONObject origin = inside.getJSONObject("origin");
                            origin_airport = origin.getString("airport");
                            JSONObject destination = inside.getJSONObject("destination");
                            destination_airport = destination.getString("airport");
                            marketing_airline = inside.getString("marketing_airline");
                            operating_airline = inside.getString("operating_airline");
                            flight_number = inside.getString("flight_number");
                            aircraft = inside.getString("aircraft");
                            JSONObject booking_info = inside.getJSONObject("booking_info");
                            travel_class = booking_info.getString("travel_class");
                            booking_code = booking_info.getString("booking_code");
                            seats_remaining = booking_info.getInt("seats_remaining");

                            //προσωρινή αποθήκευση τον στοιχείων σε hashmap
                            HashMap<String, String> temp = new HashMap<>();

                            temp.put("origin_airport", origin_airport);
                            temp.put("destination_airport", destination_airport);
                            temp.put("departs_at_day", departs_at.substring(0, 10)); //Αποθηκεύει την ημερομηνία
                            temp.put("departs_at_time", departs_at.substring(11)); //Αποθηκεύει την ωρα
                            temp.put("arrives_at_day", arrives_at.substring(0, 10));
                            temp.put("arrives_at_time", arrives_at.substring(11));
                            temp.put("marketing_airline", marketing_airline);
                            temp.put("operating_airline", operating_airline);
                            temp.put("flight_number", flight_number);
                            temp.put("aircraft", aircraft);
                            temp.put("travel_class", travel_class);
                            temp.put("booking_code", booking_code);
                            temp.put("seats_remaining", seats_remaining + "");

                            //αποθήκευση στην ArrayList
                            theListToGo.add(temp);
                        }
                        flightsLengthToGo = flightsToGo.length();
                        JSONObject inbound = testttt.getJSONObject("inbound");
                        JSONArray flightsReturn = inbound.getJSONArray("flights");
                        for (int k = 0; k < flightsReturn.length(); k++) {
                            JSONObject inside = flightsReturn.getJSONObject(k);
                            departs_at = inside.getString("departs_at");
                            arrives_at = inside.getString("arrives_at");
                            JSONObject origin = inside.getJSONObject("origin");
                            origin_airport = origin.getString("airport");
                            JSONObject destination = inside.getJSONObject("destination");
                            destination_airport = destination.getString("airport");
                            marketing_airline = inside.getString("marketing_airline");
                            operating_airline = inside.getString("operating_airline");
                            flight_number = inside.getString("flight_number");
                            aircraft = inside.getString("aircraft");
                            JSONObject booking_info = inside.getJSONObject("booking_info");
                            travel_class = booking_info.getString("travel_class");
                            booking_code = booking_info.getString("booking_code");
                            seats_remaining = booking_info.getInt("seats_remaining");

                            //προσωρινή αποθήκευση τον στοιχείων σε hashmap
                            HashMap<String, String> temp = new HashMap<>();

                            temp.put("origin_airport", origin_airport);
                            temp.put("destination_airport", destination_airport);
                            temp.put("departs_at_day", departs_at.substring(0, 10)); //Αποθηκεύει την ημερομινία
                            temp.put("departs_at_time", departs_at.substring(11)); //Αποθηκεύει την ωρα
                            temp.put("arrives_at_day", arrives_at.substring(0, 10));
                            temp.put("arrives_at_time", arrives_at.substring(11));
                            temp.put("marketing_airline", marketing_airline);
                            temp.put("operating_airline", operating_airline);
                            temp.put("flight_number", flight_number);
                            temp.put("aircraft", aircraft);
                            temp.put("travel_class", travel_class);
                            temp.put("booking_code", booking_code);
                            temp.put("seats_remaining", seats_remaining + "");

                            //αποθήκευση στην ArrayList
                            theListReturn.add(temp);
                        }
                        flightsLengthReturn = flightsReturn.length();
                    }
                    /* Μπορέι να περάσουν πολλές σερί πτήσεις χώρις να αποηκεύεται η τιμή τους
                     * η παρακάτω for αντικαθιστά τα ανανεωμένα hashmap στη λίστα με τα κοινά
                     * χαρακτηριστικά που έχουν οι πτήσεις */

                    JSONObject fare = outside.getJSONObject("fare");
                    total_price = fare.getString("total_price");
                    JSONObject price_per_adult = fare.getJSONObject("price_per_adult");
                    total_fare = price_per_adult.getString("total_fare");
                    tax = price_per_adult.getString("tax");
                    JSONObject restrictions = fare.getJSONObject("restrictions");
                    refundable = restrictions.getBoolean("refundable");
                    if (refundable)
                        refundableString = "Yes";
                    else
                        refundableString = "No";
                    change_penalties = restrictions.getBoolean("change_penalties");
                    if (change_penalties)
                        change_penaltiesString = "Yes";
                    else
                        change_penaltiesString = "No";

                    /* έαν ο αριθμός του flightLenght είναι 1 σημαίνει ότι οι πτήσεις είναι απευθείας
                     * και ξεκινάει να βάζει την τιμη και όλα τα παρακάτω σε κάθε μία πτήσει ξεχωριστά */
                    if (flightsLengthToGo == 1) {
                        for (int p = itineraries.length(); p > 0; p--) {
                            HashMap<String, String> cost = new HashMap<>(theListToGo.get(theListToGo.size() - p));
                            cost.put("total_price", total_price);
                            cost.put("total_fare", total_fare);
                            cost.put("tax", tax);
                            cost.put("refundable", refundableString);
                            cost.put("change_penalties", change_penaltiesString);
                            theListToGo.set(theListToGo.size() - p, cost);
                            HashMap<String, String> forprint = new HashMap<>();
                            forprint.put("origin", locationAirport );
                            forprint.put("destination", destinationAirport);
                            forprint.put("directGO", "Direct");
                            forprint.put("depart_time", "Time: " + cost.get("departs_at_time"));
                            forprint.put("departs_day", "(" + cost.get("departs_at_day") + ")");
                            forprint.put("Price",  cost.get("total_price") );
                            forprint.put("locationToGo", theListToGo.size() -p + ""); //αποθηκεύει σε ποιο σημειο της theList βρισκονται οι πληροφοριες

                            if(flightsLengthReturn == 1){
                                HashMap<String, String> edit = new HashMap<>(theListReturn.get(theListReturn.size() - p));
                                forprint.put("originReturn", destinationAirport);
                                forprint.put("destinationReturn", locationAirport);
                                forprint.put("directReturn","Direct");
                                forprint.put("depart_timeReturn", "Time: " + edit.get("departs_at_time"));
                                forprint.put("departs_dayReturn", "(" + edit.get("departs_at_day") + ")");
                                forprint.put("locationReturn", theListReturn.size() -p + "");
                            }
                            else if(flightsLengthReturn > 1){
                                int u = itineraries.length() * flightsLengthReturn;
                                HashMap<String, String> edit = new HashMap<>(theListReturn.get(theListReturn.size() - u));
                                forprint.put("originReturn", destinationAirport);
                                forprint.put("destinationReturn", locationAirport);
                                forprint.put("directReturn","Stops: "+ (flightsLengthReturn-1)+"");
                                forprint.put("depart_timeReturn", "Time: " + edit.get("departs_at_time"));
                                forprint.put("departs_dayReturn", "(" + edit.get("departs_at_day") + ")");
                                forprint.put("locationReturn", theListReturn.size() -u + "");
                            }
                            onlyForPrintReturn.add(forprint);
                        }
                    }
                    /* εάν όμως το flightLenght είναι μεγαλύτερο απο 1 τοτε αυτό σημαίνει οτι για να συμπληρωθεί
                     * το δρομολόγιο πρεπει να γίνουν παραπάνω πτήσεις, οπότε τοποθετεί τα παρακάτω στοιχεία
                     * σε μία απο τις πτήσεις του δρομολογίου*/
                    else if (flightsLengthToGo > 1) {
                        for (int n = itineraries.length() * flightsLengthToGo; n > 0; n -= flightsLengthToGo) {
                            HashMap<String, String> cost = new HashMap<>(theListToGo.get(theListToGo.size() - n));
                            cost.put("total_price", total_price);
                            cost.put("total_fare", total_fare);
                            cost.put("tax", tax);
                            cost.put("refundable", refundableString);
                            cost.put("change_penalties", change_penaltiesString);
                            theListToGo.set(theListToGo.size() - n, cost);
                            HashMap<String, String> forprint = new HashMap<>();
                            forprint.put("origin", locationAirport );
                            forprint.put("destination", destinationAirport);
                            forprint.put("directGO", "Stops: "+ (flightsLengthToGo-1)+"");
                            forprint.put("depart_time", "Time: " + cost.get("departs_at_time"));
                            forprint.put("departs_day", "(" + cost.get("departs_at_day") + ")");
                            forprint.put("Price",  cost.get("total_price") );
                            forprint.put("locationToGo", theListToGo.size() -n + ""); //αποθηκεύει σε ποιο σημειο της theList βρισκονται οι πληροφοριες

                            if(flightsLengthReturn == 1){
                                HashMap<String, String> edit = new HashMap<>(theListReturn.get(theListReturn.size() - n));
                                forprint.put("originReturn", destinationAirport);
                                forprint.put("destinationReturn", locationAirport);
                                forprint.put("directReturn","Direct");
                                forprint.put("depart_timeReturn", "Time: " + edit.get("departs_at_time"));
                                forprint.put("departs_dayReturn", "(" + edit.get("departs_at_day") + ")");
                                forprint.put("locationReturn", theListReturn.size() -n + "");
                            }
                            else if(flightsLengthReturn > 1){
                                int u = itineraries.length() * flightsLengthReturn;
                                HashMap<String, String> edit = new HashMap<>(theListReturn.get(theListReturn.size() - u));
                                forprint.put("originReturn", destinationAirport);
                                forprint.put("destinationReturn", locationAirport);
                                forprint.put("directReturn","Stops: "+ (flightsLengthReturn-1)+"");
                                forprint.put("depart_timeReturn", "Time: " + edit.get("departs_at_time"));
                                forprint.put("departs_dayReturn", "(" + edit.get("departs_at_day") + ")");
                                forprint.put("locationReturn", theListReturn.size() -u + "");
                            }
                            onlyForPrintReturn.add(forprint);
                        }
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

            ListAdapter adapter = new SimpleAdapter(
                    Http_Request_Activity_With_Return.this, onlyForPrintReturn,
                    R.layout.list_item_with_return, new String[]{
                    "origin", "destination", "depart_time", "departs_day", "originReturn", "destinationReturn","depart_timeReturn", "departs_dayReturn",
                    "directGO", "directReturn", "Price"},
                    new int[]{R.id.originR, R.id.destinationR, R.id.departureTimeR, R.id.departuredayR, R.id.originReturn, R.id.destinationReturn,
                            R.id.returnTimeR, R.id.returndayR, R.id.directdepart, R.id.directReturn, R.id.priceR});
            listView.setAdapter(adapter);

            // Με το που περάστουν τα αποτελέσματα στον adapter και εμφανιστουν και στην οθονη ακυρώνεται το progressDialog
            progressDialog.cancel();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int Location, long l) {
                   /* στην λίστα onlyForPrint στην θεση location αποθηκεύεται τπ σημειο που βρίσκονται τα δεδομένα στην λίστα
                     * theList. Στην onlyForPrint βρίσκονται ΜΟΝΟ τα στοιχεία που θα εμφανιστούν "συμμαζεμένα"στην οθόνη του χρήστη.
                     * και στην theList έχει αναλυτικά τις πληροφορίες για κάθε πτηση
                     */
                    int counter = Integer.valueOf(onlyForPrintReturn.get(Location).get("locationToGo"));;
                    ArrayList<HashMap<String, String>> arrayListToGo = new ArrayList<HashMap<String, String>>();
                    boolean firstFlight = true;
                    String Previous_Flight_Airline = null;
                    //η παρακάτω διαδικασία βάζει στην λίστα αρ την/τις πτήσεις για αναχώρηση
                    do{
                        if(!firstFlight)
                            counter++;
                        /* Ελέγχει εάν το μηκος του πεδίου που είναι αποθηκευμενος ο κωδικός της αεροπορικής εταιρίας είναι μκροτερος ή ισος του 2
                         * Εάν ναι μπαινει στην if και κανει τα απαραίτητα request για να βρει το ονομα της εταιρίας, έτσι την επόμενη φορά που
                         * θα πατηθεί η ίδια πτηση το πεδίου με την αεροπορική εταιρία θα είναι ήδη γεμάτο και δεν θα χρειαστεί να ξανα-κάνει τα
                         * request. Αυτό το if κάνει τον αλγόριθμο πιο γρήγορα μετα την δευτερη φορά που θα πατηθει η ιδια επιλογη */
                        if(theListToGo.get(counter).get("marketing_airline").length() <=2 ) {
                        /* Για τον λογο ότι το κλειδί που διαθέτουμε δεν επιτρεπει παραπανω παο 60 request το λεπτο σε καθε κλικ του χρηστη
                         * για να δει αναλυτικοτερα τις πληροφοριες στέλνει εκεινη την ωρα τα request για να βρει τις αεροπορικες εταιριες
                         * Στην περιπτωση που ειχαμε την δυνατοτηα για απεριοριστα request, σε ένα αλλαο thread θα έψαχνε παραλληλα της
                         * εταιριες όσο ο χρηστης θα εβλεπε την αρχική λιστα */
                            HashMap<String, String> Temporary_Hash = new HashMap<String, String>(theListToGo.get(counter));
                            String marketing = Temporary_Hash.get("marketing_airline");
                        /* Με την επιλογή ενος στοιχείου απο την λίστα ανοιγει ενα αλλο activity στο οποίο στέλνονται πληροφοριες και πριν απο
                         * στέλνοντε κάποια request για να βρεθούν οι αεροπορικές εταιρείες. Στην περίπτωση που έχουμε κάποιο δρομολόγιο με αρκετές
                         * πτήσεις τα request είναι πολλά με αποτέλεσμα να καθυστερή η εφαρμογή μας. Για αυτο αποθηκέυει στην μεταβλητη Previous_Flight_Airline
                         * τον κωδικο της εταιρείας για πρωτη φορά και κάνει request. Στην δεύτερη πτήση θα ελένξει εάν ο κωδικός της εταιρείας είναι ίδιος
                         * με αυτόν της προηγούμενης πτήσης, εάν ναι αποθηκεέυι το ίδιο ονομα εάν όχι κανει ξανα request. Με αυτόν τον τρόπο μπορούμε να
                         * γλυτώσουμε χρόνο σε περίπτωση που υπάρχουν πτήσεις σε δρομλόγια με κοινές αεροπορικές εταιρείες.*/
                            if (!AIRPORT_LIST.containsKey(marketing)) {
                                Previous_Flight_Airline = marketing;
                                try {
                                    String mar = new findAirline().execute(marketing).get();
                                    AIRPORT_LIST.put(marketing, mar);
                                    Temporary_Hash.put("marketing_airline", mar);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Temporary_Hash.put("marketing_airline", AIRPORT_LIST.get(marketing));

                            theListToGo.set(counter, Temporary_Hash);
                        }
                        firstFlight = false;
                        arrayListToGo.add(theListToGo.get(counter));
                    }while(!theListToGo.get(counter).get("destination_airport").equals(destinationAirport));

                    counter = Integer.valueOf(onlyForPrintReturn.get(Location).get("locationReturn"));
                    ArrayList<HashMap<String, String>> arrayListReturn = new ArrayList<HashMap<String, String>>();
                    firstFlight = true;
                    Previous_Flight_Airline = null;
                    //Η παρακάτω διαδικασία συμπληρώνει στη λιστα ar την/τις πτησεις για την επιστροφη
                    do{
                        if(!firstFlight)
                            counter++;
                        /* Ελέγχει εάν το μηκος του πεδίου που είναι αποθηκευμενος ο κωδικός της αεροπορικής εταιρίας είναι μκροτερος ή ισος του 2
                         * Εάν ναι μπαινει στην if και κανει τα απαραίτητα request για να βρει το ονομα της εταιρίας, έτσι την επόμενη φορά που
                         * θα πατηθεί η ίδια πτηση το πεδίου με την αεροπορική εταιρία θα είναι ήδη γεμάτο και δεν θα χρειαστεί να ξανα-κάνει τα
                         * request. Αυτό το if κάνει τον αλγόριθμο πιο γρήγορα μετα την δευτερη φορά που θα πατηθει η ιδια επιλογη */
                        if(theListReturn.get(counter).get("marketing_airline").length() <= 2) {
                            HashMap<String, String> Temporary_Hash = new HashMap<String, String>(theListReturn.get(counter));
                            String marketing = Temporary_Hash.get("marketing_airline");
                        /* Με την επιλογή ενος στοιχείου απο την λίστα ανοιγει ενα αλλο activity στο οποίο στέλνονται πληροφοριες και πριν απο
                         * στέλνοντε κάποια request για να βρεθούν οι αεροπορικές εταιρείες. Στην περίπτωση που έχουμε κάποιο δρομολόγιο με αρκετές
                         * πτήσεις τα request είναι πολλά με αποτέλεσμα να καθυστερή η εφαρμογή μας. Για αυτο αποθηκέυει στην μεταβλητη Previous_Flight_Airline
                         * τον κωδικο της εταιρείας για πρωτη φορά και κάνει request. Στην δεύτερη πτήση θα ελένξει εάν ο κωδικός της εταιρείας είναι ίδιος
                         * με αυτόν της προηγούμενης πτήσης, εάν ναι αποθηκεέυι το ίδιο ονομα εάν όχι κανει ξανα request. Με αυτόν τον τρόπο μπορούμε να
                         * γλυτώσουμε χρόνο σε περίπτωση που υπάρχουν πτήσεις σε δρομλόγια με κοινές αεροπορικές εταιρείες.*/
                            if (firstFlight) {
                                Previous_Flight_Airline = marketing;
                                try {
                                    String mar = new findAirline().execute(marketing).get();
                                    AIRPORT_LIST.put(marketing, mar);
                                    Temporary_Hash.put("marketing_airline", mar);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Temporary_Hash.put("marketing_airline", AIRPORT_LIST.get(marketing));

                            theListReturn.set(counter, Temporary_Hash);
                        }
                        firstFlight = false;
                        arrayListReturn.add(theListReturn.get(counter));
                    }while(!theListReturn.get(counter).get("destination_airport").equals(locationAirport));

                    Intent intent = new Intent(Http_Request_Activity_With_Return.this, Detail_Activity_Return.class);
                    intent.putExtra("detailsToGo", arrayListToGo);
                    intent.putExtra("detailsReturn", arrayListReturn);
                    intent.putExtra("currency", storeCurrency);
                    intent.putExtra("PREVIOUS_ACTIVITY", this.getClass().getSimpleName());
                    intent.putExtra("labelGo", labelGo);
                    intent.putExtra("labelDestination", labelDestination);
                    intent.putExtra("adults", NUMBER_OF_ADULTS);
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
                String urlForAirlines = "https://iatacodes.org/api/v6/airlines?api_key=01b77c00-91c5-4417-ba5f-0e940713aea1&code=" +param;
                URL url = new URL(urlForAirlines);
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
