package com.example.onair;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> autoCompleteDropDownList_forLocationAirport = new ArrayList<>();
    ArrayList<String> autoCompleteDropDownList_forDestinationAirport = new ArrayList<>();
    public static final String TAG = "MainActivity";

    // widget
    private Button ok_button_in_dialog, ok_button_at_extra;
    private EditText departureDate, returnDate;
    private AutoCompleteTextView locationfield_in_dialog, destinationfield_in_dialog;
    private EditText  locationfield, destinationfield;
    private Switch nonstop_flight;
    private TextView progressTextview;
    private ImageView swap_image_button, clearDepartureDateField, clearReturnDateField;
    private LinearLayout show_more_layout, clear_filter_layout, search_for_flights_layout;
    public Spinner spinner_travel_class, adutlsnumber;
    public SeekBar seekBar_max_price;

    private int departure_year, departure_month, departure_day, return_year, return_month, return_day;
    private int d_DIALOG_ID = 0, d_DIALOG_ID2 = 1;
    private String departure_month_String, departure_day_String,
                return_day_String, return_month_String;
    String value_of_max_price = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        show_more_layout = (LinearLayout) findViewById(R.id.show_more_layout);
        clear_filter_layout = (LinearLayout) findViewById(R.id.clear_filter_layout);
        search_for_flights_layout = (LinearLayout) findViewById(R.id.search_for_flights_layout);

        castingWidgets();   //cast widgets
        Swap_location_and_destination_field();  //swap location and destination field
        Clear_Dates();  //clear dates
        DecimalFormat formatter = new DecimalFormat("00");
        firstDateShowAtField(); //Για το dialog βγάζει σαν πρώτη φορά την σημερινή ημερομίνια
        departureDate.setHint(departure_day +"/"+ formatter.format(departure_month + 1 )+"/"+ departure_year);

        //Άνοιγμα ημερολόγιου και επιλογή μέρας
        showDialogOnButtonClickForDates();

        //pop up dialog for airport, drop down lists
        locationfield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationClickListener();
            }
        });
        destinationfield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationClickListener();
            }
        });

        //pop up dialog for extra choices
        popupDialogForExtraChoices();

        //clear preferences;
        clear_filter();

        search_for_flights_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call_Next_Activity();
            }
        });
    }

    public void locationClickListener(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View alertview = getLayoutInflater().inflate(R.layout.airports_fields_dialog, null);

        //casting
        locationfield_in_dialog = (AutoCompleteTextView) alertview.findViewById(R.id.locationfield_in_dialog);
        destinationfield_in_dialog = (AutoCompleteTextView) alertview.findViewById(R.id.destinationfield_in_dialog);
        ok_button_in_dialog = (Button) alertview.findViewById(R.id.ok_button_in_dialog);

        builder.setView(alertview);
        final AlertDialog showdialog = builder.create();
        showdialog.show();

        //set previous text
        locationfield_in_dialog.setText(locationfield.getText().toString());
        destinationfield_in_dialog.setText(destinationfield.getText().toString());

        locationfield_in_dialog.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ξεκινάει να κάνει http request αφου συμπληρωθούν 2 και παραπάνω ψηφία
                int textFiledCharacterCounter = locationfield_in_dialog.getText().toString().length();

                if(textFiledCharacterCounter > 1){
                    Log.i(getClass().toString(), locationfield_in_dialog.toString() +" - "+ textFiledCharacterCounter + "");
                    new conversionMethod(locationfield_in_dialog, autoCompleteDropDownList_forLocationAirport).execute();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        destinationfield_in_dialog.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ξεκινάει να κάνει http request αφου συμπληρωθούν 2 και παραπάνω ψηφία
                int textFiledCharacterCounter = destinationfield_in_dialog.getText().toString().length();

                if(textFiledCharacterCounter > 1){
                    Log.i(getClass().toString(), destinationfield_in_dialog.toString() +" - "+ textFiledCharacterCounter + "");
                    new conversionMethod(destinationfield_in_dialog, autoCompleteDropDownList_forDestinationAirport).execute();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        ok_button_in_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationfield.setText(locationfield_in_dialog.getText().toString());
                destinationfield.setText(destinationfield_in_dialog.getText().toString());
                showdialog.dismiss();
            }
        });
    }

    public void destinationClickListener(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View alertview = getLayoutInflater().inflate(R.layout.airports_fields_dialog, null);

        //casting
        locationfield_in_dialog = (AutoCompleteTextView) alertview.findViewById(R.id.locationfield_in_dialog);
        destinationfield_in_dialog = (AutoCompleteTextView) alertview.findViewById(R.id.destinationfield_in_dialog);
        ok_button_in_dialog = (Button) alertview.findViewById(R.id.ok_button_in_dialog);

        builder.setView(alertview);
        final AlertDialog showdialog = builder.create();
        showdialog.show();

        ///set previous text
        locationfield_in_dialog.setText(locationfield.getText().toString());
        destinationfield_in_dialog.setText(destinationfield.getText().toString());

        locationfield_in_dialog.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ξεκινάει να κάνει http request αφου συμπληρωθούν 2 και παραπάνω ψηφία
                int textFiledCharacterCounter = locationfield_in_dialog.getText().toString().length();

                if(textFiledCharacterCounter > 1){
                    Log.i(getClass().toString(), locationfield_in_dialog.toString() +" - "+ textFiledCharacterCounter + "");
                    new conversionMethod(locationfield_in_dialog, autoCompleteDropDownList_forLocationAirport).execute();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        destinationfield_in_dialog.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ξεκινάει να κάνει http request αφου συμπληρωθούν 2 και παραπάνω ψηφία
                int textFiledCharacterCounter = destinationfield_in_dialog.getText().toString().length();

                if(textFiledCharacterCounter > 1){
                    Log.i(getClass().toString(), destinationfield_in_dialog.toString() +" - "+ textFiledCharacterCounter + "");
                    new conversionMethod(destinationfield_in_dialog, autoCompleteDropDownList_forDestinationAirport).execute();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        ok_button_in_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationfield.setText(locationfield_in_dialog.getText().toString());
                destinationfield.setText(destinationfield_in_dialog.getText().toString());
                showdialog.dismiss();
            }
        });
    }

    public void popupDialogForExtraChoices(){

        show_more_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View extraview = getLayoutInflater().inflate(R.layout.extra_choises, null);

                nonstop_flight = (Switch) extraview.findViewById(R.id.idswitch);
                spinner_travel_class = (Spinner) extraview.findViewById(R.id.spinner_travel_class);
                progressTextview = (TextView) extraview.findViewById(R.id.progressTextview);
                seekBar_max_price = (SeekBar) extraview.findViewById(R.id.seekBarForPrice);
                adutlsnumber = (Spinner) extraview.findViewById(R.id.adultsnumber);
                ok_button_at_extra = (Button) extraview.findViewById(R.id.ok_button_at_extra) ;

                //shared preferences check nonstop
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

                // prefs adapter for travel class
                String prefs_travel_class = sharedPreferences.getString("travel_class", null);
                ArrayAdapter<CharSequence> SPadapter = ArrayAdapter.createFromResource(
                        MainActivity.this, R.array.travel_class, R.layout.drop_down_extra);
                spinner_travel_class.setAdapter(SPadapter);

                if(prefs_travel_class != null){
                    String[] travel_class_string_array = getResources().getStringArray(R.array.travel_class);
                    for(int pos = 0; pos<travel_class_string_array.length; pos++)
                        if(travel_class_string_array[pos].equals(prefs_travel_class))
                            spinner_travel_class.setSelection(pos);
                }

                // prefs adapter for adults number
                String prefs_adults_number = sharedPreferences.getString("adults_number", null);
                ArrayAdapter<CharSequence> wdadapter = ArrayAdapter.createFromResource(
                        MainActivity.this,  R.array.numbers, R.layout.drop_down_extra );
                adutlsnumber.setAdapter(wdadapter);
                if(prefs_adults_number != null) {
                    String[] adults_number_string_array = getResources().getStringArray(R.array.numbers);
                    for(int pos = 0; pos<adults_number_string_array.length; pos++)
                        if(adults_number_string_array[pos].equals(prefs_adults_number))
                            adutlsnumber.setSelection(pos);
                }

                // prefs non stop
                String prefs_non_stop = sharedPreferences.getString("nonstop", null);
                Boolean prefs_non_stop_bool = false;
                if(prefs_non_stop != null)
                    if(Boolean.valueOf(prefs_non_stop))
                        prefs_non_stop_bool = true;

                Log.i("bool", String.valueOf(prefs_non_stop_bool));
                nonstop_flight.setChecked(prefs_non_stop_bool);

                //create and editor to re-write
                final SharedPreferences.Editor editor = sharedPreferences.edit();

                seekBar_max_price.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    public void onStartTrackingTouch(SeekBar seekBar){
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if(progress_value == 50){
                            progressTextview.setText("none");
                            value_of_max_price = null;
                        }
                        else{
                            progressTextview.setText(progress_value +"");
                            value_of_max_price = String.valueOf(progress_value);
                        }
                    }
                });

                builder.setView(extraview);
                final AlertDialog showdialog = builder.create();
                showdialog.show();

                // ok return button
                ok_button_at_extra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editor.putString("travel_class", spinner_travel_class.getSelectedItem().toString());
                        editor.putString("adults_number", adutlsnumber.getSelectedItem().toString());
                        editor.putString("nonstop", String.valueOf(nonstop_flight.isChecked()));
                        editor.putString("max_price", value_of_max_price);

                        Log.i("shrd", spinner_travel_class.getSelectedItem().toString() +" / "+
                                adutlsnumber.getSelectedItem().toString() +" / " + progressTextview.getText().toString()
                                    + " / " + String.valueOf(nonstop_flight.isChecked()));
                        Toast.makeText(MainActivity.this, "Travel class: "+ spinner_travel_class.getSelectedItem().toString()
                                                                    +"\nAdults: "+ adutlsnumber.getSelectedItem().toString()
                                                                    +"\nDirect: "+ String.valueOf(nonstop_flight.isChecked()
                                                                    +"\nMax price: "+  progressTextview.getText().toString()),
                                                                Toast.LENGTH_LONG).show();

                        editor.commit();
                        showdialog.dismiss();
                    }
                });
            }
        });

    }

    public void clear_filter(){

        clear_filter_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("travel_class", null);
                editor.putString("adults_number", null);
                editor.putString("nonstop", null);
                editor.putString("max_price", null);
                editor.commit();

                Log.i(TAG, "Users preferences cleared");
                Toast.makeText(MainActivity.this, "Filter is clear!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void castingWidgets() {
        locationfield = (EditText) findViewById(R.id.locationfield);
        destinationfield = (EditText) findViewById(R.id.destinationfield);
        swap_image_button = (ImageView) findViewById(R.id.swap_image_button);
        departureDate = (EditText) findViewById(R.id.departureDate);
        clearDepartureDateField = (ImageView) findViewById(R.id.removeDate1);
        returnDate = (EditText) findViewById(R.id.returnDate);
        clearReturnDateField = (ImageView) findViewById(R.id.removeDate2);
    }

    public void firstDateShowAtField() {
        final Calendar cal = Calendar.getInstance();
        departure_year = cal.get(Calendar.YEAR);
        departure_month = cal.get(Calendar.MONTH);
        departure_day = cal.get(Calendar.DAY_OF_MONTH);
        return_year = cal.get(Calendar.YEAR);
        return_month = cal.get(Calendar.MONTH);
        return_day = cal.get(Calendar.DAY_OF_MONTH);
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

    public void Call_Next_Activity(){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date_one = null, date_two = null;
        try{
            date_one = format.parse(departureDate.getText().toString());
            if(!returnDate.getText().toString().equals("")){
                date_two = format.parse(returnDate.getText().toString());
                Log.i("de-dates", date_one.toString() + " -- " + date_two.toString());
            }
            Log.i("de-date", date_one.toString());
        }
        catch (ParseException  e){
            Log.e(TAG, "error at dates formats");
        }

        if (locationfield.getText().toString().equals("") || destinationfield.getText().toString().equals("") || departureDate.getText().toString().equals("")){
            Log.i(TAG, "Empty required fields");
            Toast.makeText(getApplicationContext(), "Fill the required fields!", Toast.LENGTH_LONG).show();
        }
        else{
            //εαν το κουμπι για την επιστροφη έχει το αρχικο κειμενο τοτε κανει τα παρακατω και ψάχνει πτησεις χωρις επιστροφη
            if (returnDate.getText().toString().equals("")) {
                Intent intent = new Intent(MainActivity.this, Http_Request_Activity.class);
                intent.putExtra("loctown", locationfield.getText().toString());
                intent.putExtra("destown", destinationfield.getText().toString());
                intent.putExtra("d_year", departure_year);
                intent.putExtra("d_month", departure_month_String);
                intent.putExtra("d_day", departure_day_String);

                startActivity(intent);
            }
            //αλλιως αμα έχει βαλει ο χρηστης μια ημερομηνια επιστροφης ψαχνει πτησεις και για επιστροφη αφου ελένξει την ορθοτητα της
            else if(date_one.after(date_two)) {
                Log.i(TAG, "Departure date is after return date");
                Toast.makeText(getApplicationContext(), "Departure date is after return date!", Toast.LENGTH_LONG).show();
            }
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
    }

    public Dialog onCreateDialog(int id){
        if(id == d_DIALOG_ID)
            return new DatePickerDialog(this, daypickerlistener, departure_year, departure_month, departure_day);
        else if(id == d_DIALOG_ID2){
            if(!departureDate.getText().toString().equals("")){
                return new DatePickerDialog(this, daypickerlistener2, departure_year, departure_month, departure_day);
                // if departureDate is completed then this calenadar starting day is the day that the user choose to leave
            }
            return new DatePickerDialog(this, daypickerlistener2, return_year, return_month, return_day);
        }
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
                    departure_day = day;
                    departure_day_String = formatter.format(departure_day);
                    String temp_date = departure_day_String +"/"+ departure_month_String +"/"+ departure_year;
                    departureDate.setText(temp_date);
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
                    return_day = day;
                    return_day_String = formatter.format(return_day);
                    String temp_date = return_day_String +"/"+ return_month_String +"/"+ return_year ;
                    returnDate.setText(temp_date);
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
            Toast.makeText(getApplicationContext(), "Activity reloaded", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        }
        return super.onOptionsItemSelected(item);
    }


    public class conversionMethod extends AsyncTask<Void, Void, Void> {
        JSONArray parentObject;
        boolean notFound = false;
        AutoCompleteTextView textViewinConvensionMethod;
        ArrayList<String> theListinConvensionMethod;

        public conversionMethod(AutoCompleteTextView autoCompleteItem, ArrayList<String> theList) {
            this.textViewinConvensionMethod = autoCompleteItem;
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
                    R.layout.drop_down, theListinConvensionMethod);
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

