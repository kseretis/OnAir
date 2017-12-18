package com.example.onair;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
    String labelGo = null , labelDestination = null;

    ArrayList<String> autoCompleteDropDownList_forLocationAirport = new ArrayList<String>();
    ArrayList<String> autoCompleteDropDownList_forDestinationAirport = new ArrayList<String>();

    // widget
    public Button SearchForFlightsBUTTON, sin, plin;
    public EditText departureDate, returnDate, adutlsnumber;
    public AutoCompleteTextView locationfield, destinationfield;
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

        //drop down list for airports
        dropdownlistMethod();

        //set search button enable
        setSearchButtonEnabled();



        SearchForFlightsBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call_Next_Activity();
            }
        });

    }

    public void setSearchButtonEnabled(){
        if (locationfield.equals("") || destinationfield.equals("") || departureDate.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "One of the required fields is empty", Toast.LENGTH_LONG).show();
            SearchForFlightsBUTTON.setEnabled(true);
        }
    }


    public void dropdownlistMethod(){

        locationfield.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ξεκινάει να κάνει http request αφου συμπληρωθούν 2 και παραπάνω ψηφία
                int textFiledCharacterCounter = locationfield.getText().toString().length();

                if(textFiledCharacterCounter > 1){
                    Log.i(getClass().toString(), locationfield.toString() +" - "+ textFiledCharacterCounter + "");
                    new conversionMethod(locationfield, autoCompleteDropDownList_forLocationAirport).execute();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        destinationfield.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ξεκινάει να κάνει http request αφου συμπληρωθούν 2 και παραπάνω ψηφία
                int textFiledCharacterCounter = destinationfield.getText().toString().length();

                if(textFiledCharacterCounter > 1){
                    Log.i(getClass().toString(), destinationfield.toString() +" - "+ textFiledCharacterCounter + "");
                    new conversionMethod(destinationfield, autoCompleteDropDownList_forDestinationAirport).execute();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }



    public void castingWidgets(){
        SearchForFlightsBUTTON = (Button) findViewById(R.id.button);
        sin = (Button) findViewById(R.id.sin);
        locationfield = (AutoCompleteTextView) findViewById(R.id.locationfield);
        destinationfield = (AutoCompleteTextView) findViewById(R.id.destinationfield);
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

    public void Call_Next_Activity(){

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


    public class conversionMethod extends AsyncTask<Void, Void, Void> {
        JSONArray parentObject;
        boolean notFound = false;
        AutoCompleteTextView textViewinConvensionMethod;
        ArrayList<String> theListinConvensionMethod;

        public conversionMethod(AutoCompleteTextView AtextViewParam, ArrayList<String> theList) {
            this.textViewinConvensionMethod = AtextViewParam;
            this.theListinConvensionMethod = theList;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                final String baseUrl = "https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?";
                final String apiKeyParam = "apikey";
                final String termParam = "term";

                // under construction
                Uri buildUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(apiKeyParam, BuildConfig.LOW_FARE_FLIGHTS_API_KEY)
                        .appendQueryParameter(termParam, textViewinConvensionMethod.getText().toString())
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

                // fill autocomplete drop down list

                theListinConvensionMethod.clear();
                for(int i=0; i<parentObject.length(); i++){
                    JSONObject in = parentObject.getJSONObject(i);
                    String value = in.getString("value");
                    String label = in.getString("label");
                    String compined_String = value +" - "+ label;
                    theListinConvensionMethod.add(compined_String);
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

            ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.support_simple_spinner_dropdown_item, theListinConvensionMethod);
            textViewinConvensionMethod.setAdapter(dropDownAdapter);

            textViewinConvensionMethod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    textViewinConvensionMethod.setText(parent.getItemAtPosition(position).toString().substring(0,3));
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

