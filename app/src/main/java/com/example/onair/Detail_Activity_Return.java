package com.example.onair;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.xml.datatype.Duration;

public class Detail_Activity_Return extends AppCompatActivity {
    private NonScrollListView non_scroll_list_to_go, non_scroll_list_return ;
    private Button button;
    private TextView refundable, seatsRemaining, OriginToDestination, destinationToOrigin;
    ArrayList<HashMap<String, String>> detailArrayListToGo = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> detailArrayListReturn = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> LABEL_LIST = new HashMap<String, String>();
    private String storeCurrency, labelGo = null, labelDestination = null;
    int NUMBER_OF_ADULTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_detail_activity);

        NonScrollListView non_scroll_list_to_go = (NonScrollListView) findViewById(R.id.listForMultiDetail);
        NonScrollListView non_scroll_list_return = (NonScrollListView) findViewById(R.id.listForMultiDetail2);
        button = (Button) findViewById(R.id.buttonprice);
        refundable = (TextView) findViewById(R.id.refundable);
        seatsRemaining = (TextView) findViewById(R.id.SeatsRemaining);
        OriginToDestination = (TextView) findViewById(R.id.OriginToDestination);
        destinationToOrigin = (TextView) findViewById(R.id.destinationToOrigin);

        // μορφη ωρας
        SimpleDateFormat defaultFormat = new SimpleDateFormat("HH:mm");

        detailArrayListToGo = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("detailsToGo");
        for (int i = 0; i < detailArrayListToGo.size(); i++) {

            HashMap<String, String> temp = detailArrayListToGo.get(i);
            HashMap<String, String> tempForChange = new HashMap<>();
            tempForChange.put("origin_airport", temp.get("origin_airport"));
            tempForChange.put("destination_airport", temp.get("destination_airport"));
            tempForChange.put("departs_at_time", temp.get("departs_at_time"));
            tempForChange.put("departs_at_day", temp.get("departs_at_day"));
            tempForChange.put("arrives_at_time", temp.get("arrives_at_time"));
            tempForChange.put("arrives_at_day", temp.get("arrives_at_day"));

            // Για να συμπληρώσει αυτα τα 2 πεδία καλει την συναρτηση Check_if_the_city_already_exists_in_the_list
            tempForChange.put("LABEL_GO", Check_if_the_city_already_exists_in_the_list(temp.get("origin_airport")));
            tempForChange.put("LABEL_DEST", Check_if_the_city_already_exists_in_the_list(temp.get("destination_airport")));

            /* υπολογίζει την διάρκεια πτήσεης. Αρχικα υπολογίζει την διαφορά απο την ώρα
             * σε milliseconds στην συνέχεια τα μετατρέπει σε λεπτα και με την Math.abs κρατά
             * το απολυτο γιατι σε κάποιες περιπτωσεις οπως departs_at_time = 23.15, arrives_at_time = 02.25
             * το αποτελεσμα στο duration εβγαινε μείον*/
            Long duration = null;
            String timeString1 = temp.get("departs_at_time");
            String timeString2 = temp.get("arrives_at_time");
            String substr1 = timeString1.substring(0,2);
            String substr2 = timeString2.substring(0,2);

            //ελέγχει εάν η ώρα ειναι 00 βγανει λαθος το αποτέλεσμα. ετσι το μετατρεπει προσωρινα σε 24
            if(substr1.equals("00"))
                timeString1 = "24" + timeString1.substring(2);
            if(substr2.equals("00"))
                timeString2 = "24" + timeString2.substring(2);

            try {
                Date time1 = defaultFormat.parse(timeString1);
                Date time2 = defaultFormat.parse(timeString2);
                duration = Math.abs((time2.getTime() - time1.getTime()) / (1000*60));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tempForChange.put("duration", "Duration: "+ duration/60 +"h "+ duration%60 +"m");
            tempForChange.put("marketing_airline", "Marketing airline: " + temp.get("marketing_airline"));
            tempForChange.put("operating_airline", "Operating airline: " + temp.get("operating_airline"));
            tempForChange.put("aircraft", "Flight number: " + temp.get("flight_number") + ", Aircraft: " + temp.get("aircraft"));
            tempForChange.put("travel_class", "Travel class: " + temp.get("travel_class"));
            tempForChange.put("booking_code", "Booking code: " + temp.get("booking_code"));
            tempForChange.put("refundable", "Refundable: " + temp.get("refundable"));
            tempForChange.put("change_penalties", "Change penalties: " + temp.get("change_penalties"));
            tempForChange.put("seats_remaining", "Seats remaining: " + temp.get("seats_remaining"));
            tempForChange.put("total_price", "Total price: " + temp.get("total_price"));
            tempForChange.put("tax", "tax: " + temp.get("tax"));
            detailArrayListToGo.set(i, tempForChange);
        }

        detailArrayListReturn = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("detailsReturn");
        for (int i = 0; i < detailArrayListReturn.size(); i++) {

            HashMap<String, String> temp = detailArrayListReturn.get(i);
            HashMap<String, String> tempForChange = new HashMap<>();
            tempForChange.put("origin_airport", temp.get("origin_airport"));
            tempForChange.put("destination_airport", temp.get("destination_airport"));
            tempForChange.put("departs_at_time", temp.get("departs_at_time"));
            tempForChange.put("departs_at_day", temp.get("departs_at_day"));
            tempForChange.put("arrives_at_time", temp.get("arrives_at_time"));
            tempForChange.put("arrives_at_day", temp.get("arrives_at_day"));

            // Για να συμπληρώσει αυτα τα 2 πεδία καλει την συναρτηση Check_if_the_city_already_exists_in_the_list
            tempForChange.put("LABEL_GO", Check_if_the_city_already_exists_in_the_list(temp.get("origin_airport")));
            tempForChange.put("LABEL_DEST", Check_if_the_city_already_exists_in_the_list(temp.get("destination_airport")));

            /* υπολογίζει την διάρκεια πτήσεης. Αρχικα υπολογίζει την διαφορά απο την ώρα
             * σε milliseconds στην συνέχεια τα μετατρέπει σε λεπτα και με την Math.abs κρατά
             * το απολυτο γιατι σε κάποιες περιπτωσεις οπως departs_at_time = 23.15, arrives_at_time = 02.25
             * το αποτελεσμα στο duration εβγαινε μείον*/
            Long duration = null;
            String timeString1 = temp.get("departs_at_time");
            String timeString2 = temp.get("arrives_at_time");
            String substr1 = timeString1.substring(0,2);
            String substr2 = timeString2.substring(0,2);

            //ελέγχει εάν η ώρα ειναι 00 βγανει λαθος το αποτέλεσμα. ετσι το μετατρεπει προσωρινα σε 24
            if(substr1.equals("00"))
                timeString1 = "24" + timeString1.substring(2);
            if(substr2.equals("00"))
                timeString2 = "24" + timeString2.substring(2);

            try {
                Date time1 = defaultFormat.parse(timeString1);
                Date time2 = defaultFormat.parse(timeString2);
                duration = Math.abs((time2.getTime() - time1.getTime()) / (1000*60));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tempForChange.put("duration", "Duration: "+ duration/60 +"h "+ duration%60 +"m");
            tempForChange.put("marketing_airline", "Marketing airline: " + temp.get("marketing_airline"));
            tempForChange.put("operating_airline", "Operating airline: " + temp.get("operating_airline"));
            tempForChange.put("aircraft", "Flight number: " + temp.get("flight_number") + ", Aircraft: " + temp.get("aircraft"));
            tempForChange.put("travel_class", "Travel class: " + temp.get("travel_class"));
            tempForChange.put("booking_code", "Booking code: " + temp.get("booking_code"));
            tempForChange.put("refundable", "Refundable: " + temp.get("refundable"));
            tempForChange.put("change_penalties", "Change penalties: " + temp.get("change_penalties"));
            tempForChange.put("seats_remaining", "Seats remaining: " + temp.get("seats_remaining"));
            tempForChange.put("total_price", "Total price: " + temp.get("total_price"));
            tempForChange.put("tax", "tax: " + temp.get("tax"));
            detailArrayListReturn.set(i, tempForChange);
        }

        storeCurrency = getIntent().getStringExtra("currency");
        labelGo = getIntent().getStringExtra("labelGo");
        labelDestination = getIntent().getStringExtra("labelDestination");
        NUMBER_OF_ADULTS = getIntent().getIntExtra("adults", 0);

        // ΤΙΤΛΟΣ
        if(NUMBER_OF_ADULTS > 1)
            setTitle(NUMBER_OF_ADULTS +" persons");
        else
            setTitle(NUMBER_OF_ADULTS +" person");

        String sub1 = labelGo.substring(0, labelGo.indexOf(" "));
        String sub2 = labelDestination.substring(0, labelDestination.indexOf(" "));

        OriginToDestination.setText(sub1 +" - "+ sub2);
        destinationToOrigin.setText(sub2 +" - "+ sub1);
        button.setText(detailArrayListToGo.get(0).get("total_price"));
        refundable.setText(detailArrayListToGo.get(0).get("refundable"));
        seatsRemaining.setText(detailArrayListToGo.get(0).get("seats_remaining"));

        // String και int
        String[] Keys_from_ArrayList = new String[]{
                "origin_airport", "LABEL_GO", "departs_at_time","departs_at_day", "marketing_airline", "aircraft", "travel_class", "duration",
                "LABEL_DEST", "destination_airport", "arrives_at_time", "arrives_at_day"};
        int[] IDs_for_print = new int[]{R.id.originD, R.id.LABEL_GO, R.id.departureTimeD, R.id.departuredayD, R.id.Marketing_airlineD, R.id.flight_number_and_aircraftD, R.id.travel_classD
                , R.id.durationD, R.id.LABEL_DEST, R.id.destinationD, R.id.arriveTimeD, R.id.arriveDayD};

        // Adapter's και πέρασμα στα XML
        ListAdapter adapter = new SimpleAdapter(
                Detail_Activity_Return.this, detailArrayListToGo, R.layout.detail_list, Keys_from_ArrayList, IDs_for_print);
        non_scroll_list_to_go.setAdapter(adapter);

        ListAdapter adapter2 = new SimpleAdapter(
                Detail_Activity_Return.this, detailArrayListReturn, R.layout.detail_list, Keys_from_ArrayList, IDs_for_print);
        non_scroll_list_return.setAdapter(adapter2);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    /* Η παρακάτω μέθοδος ελέγχει εάν στο hashmap LABEL_LIST υπάρχει το label της πολής, ενα υπαρχει το πέρνει και το
    * περνάει στο πεδίο  αλλιως καλεί την Find_city_from_IATA_code για να κάνει το request και να παρει το label της πολης
    *  Με Αυτήν την διαδικασία γλυτώνουμε χρόνο γιατι κάνει λιγότερα request*/
    public String Check_if_the_city_already_exists_in_the_list(String value){
        String AIRPORT_CODE = value;
        String city_label = null;
        if(!LABEL_LIST.containsKey(AIRPORT_CODE)){
            try {
                city_label = new Find_city_from_IATA_code().execute(AIRPORT_CODE).get();
                LABEL_LIST.put(AIRPORT_CODE, city_label);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else
            city_label = LABEL_LIST.get(AIRPORT_CODE);

        return city_label;
    }

    public class Find_city_from_IATA_code extends AsyncTask<String, String, String> {
        JSONArray parentObject;
        String label;
        boolean notFound = false;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String IATA_CODE = params[0];
            try {
                final String link = "https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?apikey=4JiBVoAAA8rLiuEAZPrkbaIxXkBohGZt&origin&term=" + IATA_CODE;
                URL url = new URL(link);
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
                parentObject = new JSONArray(finalJSon);

                //Σε περίπτωση που δεν βρεθει η πολη ή έχει γραφτει λαθος με το παρακατω if θα πεταξει exception
                if(parentObject.length() == 0) {
                    String toThrowException = parentObject.getJSONObject(0).getString("toThrowException");
                }
                /* σε περίπτωση που το array έχει παραπάνω απο ένα αντικείμενο μέσα σημαίνει ότι
                 * ο προορισμός που διάλεξε ο χρήστης έχει παραπάνω απο ένα αεροδρόμιο*/
                if(parentObject.length()>1){
                    for(int i=0; i<parentObject.length(); i++){
                        JSONObject in = parentObject.getJSONObject(i);
                        String value = in.getString("value");
                        if(IATA_CODE.equals(value)){
                            label = in.getString("label");
                        }

                    }
                }
                else {
                    for (int i = 0; i < parentObject.length(); i++) {
                        JSONObject in = parentObject.getJSONObject(i);
                        label = in.getString("label");
                    }
                }
                return label;
            } catch (IOException e) {
                e.printStackTrace();
                notFound = true;
            } catch (JSONException e) {
                e.printStackTrace();
                notFound = true;
            }
            return null;
        }
    }
}



