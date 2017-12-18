package com.example.onair;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static String link;
    String loctown, destown, LOCAL_TOWN_REQUEST, DESTINATION_TOWN_REQUEST;
    String labelGo = null , labelDestination = null;
    ArrayList<HashMap<String, String>> OriginsuggestedAirports;
    ArrayList<HashMap<String, String>> DestinationsuggestedAirports;

    String[] origin_airports_for_view, origin_airports_for_use;
    String[] destination_airports_for_view, destination_airports_for_use;

    // widget
    public Button SearchForFlightsBUTTON, CheckFieldsBUTTON, sin, plin;
    public EditText locationfield, destinationfield, departureDate, returnDate, adutlsnumber;
    public Switch aSwitch;
    public TextView progressTextview;
    public ImageView swap_image_button, clearDepartureDateField, clearReturnDateField;
    public Spinner spinner;
    public SeekBar seekBar;

    int departure_year,departure_month,departure_day, d_DIALOG_ID = 0;
    String departure_day_String, departure_month_String;
    int return_year,return_month, return_day, d_DIALOG_ID2 = 1;
    String return_day_String, return_month_String;
    String IfswitchIsChecked = "";
    String storeCurrency;
    int NUMBER_OF_ADULTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updatePreferences();

        castingWidgets();   //cast widgets
        Swap_location_and_destination_field();  //swap location and destination field
        Clear_Dates();  //clear dates
        SpinnerWidget();    //Spinner & Seek bar & switch
        SeekBarWidget();    //Spinner & Seek bar & switch
        SwitchWidget();     //Spinner & Seek bar & switch
        firstDateShowAtField(); //Για το dialog βγάζει σαν πρώτη φορά την σημερινή ημερομίνια
        departureDate.setHint("*Departure date: "+ departure_day +"/"+ departure_month +"/"+ departure_year);
        adults();   //επιλογη ατομων

        //Άνοιγμα ημερολόγιου και επιλογή μέρας
        showDialogOnButtonClickForDates();
        SearchForFlightsBUTTON.setEnabled(false);




        CheckFieldsBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckFields();
            }
        });

        SearchForFlightsBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call_Next_Activity();
            }
        });

    }

    public void castingWidgets(){
        SearchForFlightsBUTTON = (Button) findViewById(R.id.button);
        CheckFieldsBUTTON = (Button) findViewById(R.id.button2);
        sin = (Button) findViewById(R.id.sin);
        locationfield = (EditText) findViewById(R.id.locationfield);
        destinationfield = (EditText) findViewById(R.id.destinationfield);
        swap_image_button = (ImageView) findViewById(R.id.swap_image_button);
        departureDate = (EditText) findViewById(R.id.departureDate);
        clearDepartureDateField = (ImageView) findViewById(R.id.removeDate1);
        returnDate = (EditText) findViewById(R.id.returnDate);
        clearReturnDateField = (ImageView) findViewById(R.id.removeDate2) ;
        aSwitch = (Switch) findViewById(R.id.idswitch);
        spinner = (Spinner) findViewById(R.id.spinner);
        progressTextview = (TextView) findViewById(R.id.progressTextview);
        seekBar = (SeekBar) findViewById(R.id.seekBarForPrice);
        plin = (Button) findViewById(R.id.plin);
        adutlsnumber = (EditText) findViewById(R.id.adultsnumber);
    }

    public void firstDateShowAtField(){
        final Calendar cal = Calendar.getInstance();
        departure_year = cal.get(Calendar.YEAR);
        departure_month = cal.get(Calendar.MONTH);
        departure_day = cal.get(Calendar.DAY_OF_MONTH);
        return_year = cal.get(Calendar.YEAR);
        return_month = cal.get(Calendar.MONTH);
        return_day = cal.get(Calendar.DAY_OF_MONTH);
    }

    private void updatePreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        storeCurrency = sharedPreferences.getString("currency", "");
        Log.i(getClass().toString(), storeCurrency + " selected currency!!!!!!!!!!!!!!!!!!!");
    }

    // Κανει Swap τα πεδία των αεροδρομιων
    public void Swap_location_and_destination_field() {
        swap_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp_save = locationfield.getText().toString();
                locationfield.setText(destinationfield.getText().toString());
                destinationfield.setText(temp_save);
            }
        });
    }

    // Σβήνει το περιεχομενο των πεδιων για τις ημερομηνίες
    public void Clear_Dates(){
        clearDepartureDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departureDate.setText("");
            }
        });
        clearReturnDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnDate.setText("");
            }
        });
    }

    public void SwitchWidget(){
       if(aSwitch.isChecked())
           IfswitchIsChecked = "TRUE";
       else
           IfswitchIsChecked = "FALSE";

    }


    public void SpinnerWidget() {
        ArrayAdapter<CharSequence> SPadapter = ArrayAdapter.createFromResource(this, R.array.travel_class, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(SPadapter);
    }

    public void SeekBarWidget(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress_value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int MIN = 50;
                progressTextview.setText(MIN + progress +"");
                progress_value = MIN + progress;
                if(progress_value == 50)
                    progressTextview.setText("none");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(progress_value == 50)
                    progressTextview.setText("none");
                else
                    progressTextview.setText(progress_value +"");
            }
        });
    }

    public void adults(){
        plin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NUMBER_OF_ADULTS = Integer.parseInt(adutlsnumber.getText().toString());
                NUMBER_OF_ADULTS --;
                adutlsnumber.setText(NUMBER_OF_ADULTS +"");
            }
        });
        sin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NUMBER_OF_ADULTS = Integer.parseInt(adutlsnumber.getText().toString());
                NUMBER_OF_ADULTS ++;
                adutlsnumber.setText(NUMBER_OF_ADULTS +"");
            }
        });
    }

    public void CheckFields(){
        // loctown & destown χρησιμοποιουνται για την μετατροπη της πολης στον κωδικου αεροδρομιου
        loctown = locationfield.getText().toString();
        destown = destinationfield.getText().toString();

        //εάν η πολη που εβαλε ο χρηστης έχει κενο το αντικαταστει με %20 γιατι δημιουεγούσε προβλημα με το request
        if(loctown.contains(" "))
            LOCAL_TOWN_REQUEST = loctown.replace(" ", "%20");
        else
            LOCAL_TOWN_REQUEST = loctown;
        if(destown.contains(" "))
            DESTINATION_TOWN_REQUEST = destown.replace(" ", "%20");
        else
            DESTINATION_TOWN_REQUEST = destown;

        if (loctown.equals("") || destown.equals(""))
            Toast.makeText(getApplicationContext(), "Please fill the required fields", Toast.LENGTH_LONG).show();
        else {
            new conversionOrigin().execute();
            new conversionDestination().execute();
            SearchForFlightsBUTTON.setEnabled(true);
        }
    }
    public void Call_Next_Activity(){
        //ελέγχει εάν τα απαραίτητα πεδία είναι συμπληρωμένα για να συνεχίσει
        if (locationfield.equals("") || destinationfield.equals("") || departureDate.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(), "One of the required fields is empty", Toast.LENGTH_LONG).show();
        else{
            //εαν το κουμπι για την επιστροφη έχει το αρχικο κειμενο τοτε κανει τα παρακατω και ψάχνει πτησεις χωρις επιστροφη
            if(returnDate.getText().toString().equals("")){
                Intent intent = new Intent(MainActivity.this, Http_Request_Activity.class);
                intent.putExtra("loctown", locationfield.getText().toString());
                intent.putExtra("destown", destinationfield.getText().toString());
                intent.putExtra("d_year", departure_year);
                intent.putExtra("d_month", departure_month_String);
                intent.putExtra("d_day", departure_day_String);
                intent.putExtra("nonstop", IfswitchIsChecked);
                intent.putExtra("labelGo", labelGo);
                intent.putExtra("labelDestination", labelDestination);
                intent.putExtra("adults", NUMBER_OF_ADULTS);
                if(!spinner.getSelectedItem().equals("Economy"))
                    intent.putExtra("travel_class", spinner.getSelectedItem().toString());
                if(!progressTextview.getText().equals("none"))
                    intent.putExtra("max_price", progressTextview.getText().toString());
                else
                    intent.putExtra("max_price", "none");

                startActivity(intent);
            }
            //αλλιως αμα έχει βαλει ο χρηστης μια ημερομηνια επιστροφης ψαχνει πτησεις και για επιστροφη
            else{
                Intent intent = new Intent(MainActivity.this, Http_Request_Activity_With_Return.class);
                intent.putExtra("loctown", locationfield.getText().toString());
                intent.putExtra("destown", destinationfield.getText().toString());
                intent.putExtra("d_year", departure_year);
                intent.putExtra("d_month", departure_month_String);
                intent.putExtra("d_day", departure_day_String);
                intent.putExtra("r_year", return_year);
                intent.putExtra("r_month", return_month_String);
                intent.putExtra("r_day", return_day_String);
                intent.putExtra("nonstop", IfswitchIsChecked);
                intent.putExtra("labelGo", labelGo);
                intent.putExtra("labelDestination", labelDestination);
                intent.putExtra("adults", NUMBER_OF_ADULTS);
                if(!spinner.getSelectedItem().equals("Economy"))
                    intent.putExtra("travel_class", spinner.getSelectedItem().toString());
                if(!progressTextview.getText().equals("none"))
                    intent.putExtra("max_price", progressTextview.getText().toString());
                else
                    intent.putExtra("max_price", "none");

                startActivity(intent);
            }
        }
    }

    //ΗΜΕΡΟΜΗΝΙΑ ΑΝΑΧΩΡΗΣΗΣ ΚΑΙ ΕΠΙΣΤΡΟΦΗΣ
    public void showDialogOnButtonClickForDates(){
        departureDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(d_DIALOG_ID);
                    }
                }
        );
        returnDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(d_DIALOG_ID2);
                    }
                }
        );

        SearchForFlightsBUTTON.setEnabled(false);
    }

    public Dialog onCreateDialog(int id){
        if(id == d_DIALOG_ID)
            return new DatePickerDialog(this, daypickerlistener, departure_year, departure_month, departure_day);
        else if(id == d_DIALOG_ID2)
            return new DatePickerDialog(this, daypickerlistener2, return_year, return_month, return_day);
        return null;
    }
    private DatePickerDialog.OnDateSetListener daypickerlistener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    NumberFormat formatter = new DecimalFormat("00");
                    departure_year = year;
                    departure_month = month+ 1;
                    departure_month_String = formatter.format(departure_month);
                    departure_day = 0 + day;
                    departure_day_String = formatter.format(departure_day);
                    departureDate.setText(departure_day_String +"/"+ departure_month_String +"/"+ departure_year);
                }
            };
    private DatePickerDialog.OnDateSetListener daypickerlistener2 =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    NumberFormat formatter = new DecimalFormat("00");
                    return_year = year;
                    return_month = month+ 1;
                    return_month_String = formatter.format(return_month);
                    return_day = 0 + day;
                    return_day_String = formatter.format(return_day);
                    returnDate.setText(return_day_String +"/"+ return_month_String +"/"+ return_year);
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menucurrency ) {
            Intent intent = new Intent(getApplicationContext(), Settings_activity.class);
            onPause();;
            startActivity(intent);
            return true;
        }
        else if(id == R.id.menurestart){
            Toast.makeText(getApplicationContext(), "Temporary Disabled", Toast.LENGTH_SHORT).show();
            /*Intent intent = getIntent();
            startActivity(intent);
            return true;*/
        }
        return super.onOptionsItemSelected(item);
    }


    public class conversionOrigin extends AsyncTask<Void, Void, Void> {
        String Origin_value;
        JSONArray parentObject;
        boolean notFound = false;
        @Override
        protected Void doInBackground(Void... voids) {
            OriginsuggestedAirports = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                final String baseUrl = "https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?";
                final String apiKeyParam = "apikey";
                final String termParam = "term";

                // under construction
                Uri buildUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(apiKeyParam, BuildConfig.LOW_FARE_FLIGHTS_API_KEY)
                        .appendQueryParameter(termParam, LOCAL_TOWN_REQUEST)
                        .build();
                Log.i(this.getClass().getSimpleName(), buildUri.toString());

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
                parentObject = new JSONArray(finalJSon);

                //Σε περίπτωση που δεν βρεθει η πολη ή έχει γραφτει λαθος με το παρακατω if θα πεταξει exception
                if(parentObject.length() == 0) {
                    String toThrowException = parentObject.getJSONObject(0).getString("toThrowException");
                }
                /* σε περίπτωση που το array έχει παραπάνω απο ένα αντικείμενο μέσα σημαίνει ότι
                 * ο προορισμός που διάλεξε ο χρήστης έχει παραπάνω απο ένα αεροδρόμιο*/
                if(parentObject.length()>1){
                    for(int i=0; i<parentObject.length(); i++){
                        HashMap<String, String> table = new HashMap<>();
                        JSONObject in = parentObject.getJSONObject(i);
                        String value = in.getString("value");
                        String label = in.getString("label");
                        table.put("value", value);
                        table.put("label", label);
                        OriginsuggestedAirports.add(table);
                    }
                }
                else {
                    for (int i = 0; i < parentObject.length(); i++) {
                        JSONObject in = parentObject.getJSONObject(i);
                        Origin_value = in.getString("value");
                        labelGo = in.getString("label");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                notFound = true;
            } catch (JSONException e) {
                e.printStackTrace();
                notFound = true;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            /* εάν έχει ένα αεροδρόμιο τότε απλα θέτει στο πεδίο κειμένου τον κωδικό του αεροδρομίου*/
            if(parentObject.length() == 1){
                locationfield.setText(Origin_value);
            }
            //Σε περιπτωση που δεν βρεθει η πολη το notFound θα γινει true και δεν θα μπει παρακατω
            else if(!notFound){
                /* αλλιώς αποθηκεύει σε μια λίστα τα (_only_for_view) τα ονόματα των αεροδρομιων
                 * και σε μια άλλη τους κωδικούς (for_use). Βγαίνουν στο πεδίο κειμένου τα ονομάτα
                 * για να επιλέξη ο χρηστης αλλα σε εμάς τα ονόματα είναι αχρηστα χρειαζόμαστε μονο
                 * τους κωδικούς τους, οποτε με την setOnItemClickListener κρατάμε μονο τους κωδικους*/
                origin_airports_for_view = new String[OriginsuggestedAirports.size()];
                origin_airports_for_use = new String[OriginsuggestedAirports.size()];
                for (int i = 0; i < OriginsuggestedAirports.size(); i++){
                    origin_airports_for_view[i] = OriginsuggestedAirports.get(i).get("label");
                    origin_airports_for_use[i] = OriginsuggestedAirports.get(i).get("value");
                }
                dialog dia = new dialog(loctown, origin_airports_for_view, origin_airports_for_use);
                dia.setCancelable(false);
                dia.show(getFragmentManager(), "dia");
            }
            else {
                //θα μπει εδω και θα ξανα-αρχισει την mainactivity
                Toast.makeText(getApplicationContext(), "City \""+ loctown +"\" didn't found. Try again!", Toast.LENGTH_LONG).show();
                locationfield.setText("");
            }
        }
    }
    public class conversionDestination extends AsyncTask<Void, Void, Void> {
        String Destination_Value;
        JSONArray parentObject;
        boolean notFound = false;
        @Override
        protected Void doInBackground(Void... voids) {
            DestinationsuggestedAirports = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                final String baseUrl = "https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?";
                final String apiKeyParam = "apikey";
                final String termParam = "term";

                // under construction
                Uri buildUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(apiKeyParam, BuildConfig.LOW_FARE_FLIGHTS_API_KEY)
                        .appendQueryParameter(termParam, DESTINATION_TOWN_REQUEST)
                        .build();
                Log.i(this.getClass().getSimpleName(), buildUri.toString());

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
                parentObject = new JSONArray(finalJSon);

                //Σε περίπτωση που δεν βρεθει η πολη ή έχει γραφτει λαθος με το παρακατω if θα πεταξει exception
                if(parentObject.length() == 0) {
                    String toThrowException = parentObject.getJSONObject(0).getString("toThrowException");
                }
                /* σε περίπτωση που το array έχει παραπάνω απο ένα αντικείμενο μέσα σημαίνει ότι
                 * ο προορισμός που διάλεξε ο χρήστης έχει παραπάνω απο ένα αεροδρόμιο*/
                if(parentObject.length()>1){
                    for(int i=0; i<parentObject.length(); i++){
                        HashMap<String, String> table = new HashMap<>();
                        JSONObject in = parentObject.getJSONObject(i);
                        String value = in.getString("value");
                        String label = in.getString("label");
                        table.put("value",value);
                        table.put("label", label);
                        DestinationsuggestedAirports.add(table);
                    }
                }
                else {
                    for (int i = 0; i < parentObject.length(); i++) {
                        JSONObject in = parentObject.getJSONObject(i);
                        Destination_Value = in.getString("value");
                        labelDestination = in.getString("label");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                notFound = true;
            } catch (JSONException e) {
                e.printStackTrace();
                notFound = true;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            /* εάν έχει ένα αεροδρόμιο τότε απλα θέτει στο πεδίο κειμένου τον κωδικό του αεροδρομίου*/
            if(parentObject.length() == 1) {
                destinationfield.setText(Destination_Value);
            }
            //Σε περιπτωση που δεν βρεθει η πολη το notFound θα γινει true και δεν θα μπει παρακατω
            else if(!notFound){
                /* αλλιώς αποθηκεύει σε μια λίστα τα (_only_for_view) τα ονόματα των αεροδρομιων
                 * και σε μια άλλη τους κωδικούς (for_use). Βγαίνουν στο πεδίο κειμένου τα ονομάτα
                 * για να επιλέξη ο χρηστης αλλα σε εμάς τα ονόματα είναι αχρηστα χρειαζόμαστε μονο
                 * τους κωδικούς τους, οποτε με την setOnItemClickListener κρατάμε μονο τους κωδικους*/
                destination_airports_for_view = new String[DestinationsuggestedAirports.size()];
                destination_airports_for_use = new String[DestinationsuggestedAirports.size()];
                for (int i = 0; i < DestinationsuggestedAirports.size(); i++){
                    destination_airports_for_view[i] = DestinationsuggestedAirports.get(i).get("label");
                    destination_airports_for_use[i] = DestinationsuggestedAirports.get(i).get("value");
                }
                dialog dia = new dialog(destown, destination_airports_for_view, destination_airports_for_use);
                dia.setCancelable(false);
                dia.show(getFragmentManager(), "dia");
            }
            else {
                //θα μπει εδω και θα ξανα-αρχισει την mainactivity
                Toast.makeText(getApplicationContext(), "City \""+ destown +"\" didn't found. Try again!", Toast.LENGTH_LONG).show();
                destinationfield.setText("");
            }
        }
    }
    public class dialog extends DialogFragment{
        String town, saveClick, saveClickName;
        String airportsList_for_view[], airportsList_for_use[];

        public dialog(String town, String[] airportsList_for_view, String[] airportsList_for_use){
            this.town = town;
            this.airportsList_for_view = airportsList_for_view;
            this.airportsList_for_use = airportsList_for_use;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(town+"'s airports. Please choose one").setSingleChoiceItems(airportsList_for_view, -1,
                                                new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int click) {
                    saveClick = airportsList_for_use[click];
                    saveClickName = airportsList_for_view[click];
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    /* στην περιπτωση που η πολη που στάλθηκε ως παραμετρος με το κάλεσμα της μεθοδου ειναι
                     * ιδια με αυτη που πληκτρολογήθηκε απο τον χρηστη στο πεδιο της αναχωρησης τοτε
                     * βαζει σε αυτο τον κωδικο του αεροδρομιου που επελεξε. Αλλιώς σημαίνει ότι
                     * η πόλη αυτη γράφτηκε στο πεδιο του προορισμου και βάζει τον κωδικο εκει */
                    if(loctown.equals(town)) {
                        locationfield.setText(saveClick);
                        labelGo = saveClickName;
                    }
                    else {
                        destinationfield.setText(saveClick);
                        labelDestination = saveClickName;
                    }
                }
            });
            return builder.create();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

