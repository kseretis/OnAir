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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class Details_activity_with_return extends AppCompatActivity {

    public static String storeCurrency;
    public static final String TAG = "Details_activity_return";
    private String origin_name, destination_name;
    Itinerary itinerary;
    TextView from_to, from_to_return, refundable, penalty;
    Button buy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_with_return);

        // shared preferences currency
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        storeCurrency = sharedPreferences.getString("currency", "");

        // set activity name
        setTitle("Details");

        // cast
        from_to = (TextView) findViewById(R.id.from_to_togo);
        from_to_return = (TextView) findViewById(R.id.from_to_return);
        refundable = (TextView) findViewById(R.id.refundable_return);
        penalty = (TextView) findViewById(R.id.penalty_return);
        buy = (Button) findViewById(R.id.buy);

        // get extra from intent
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        itinerary = (Itinerary) bundle.getSerializable("itinerary");

        //set airports name
        try {
            origin_name = new call_api_for_cities(itinerary.getOutbound_list().get(0).getOrigin_airport()).execute().get();
            Log.i(TAG, origin_name +"");
            destination_name = new call_api_for_cities(itinerary.getOutbound_list().get(
                    itinerary.getOutbound_list().size() -1).getDestination_airport()).execute().get();
            Log.i(TAG, destination_name +"");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        from_to.setText(origin_name + " - " + destination_name);
        from_to_return.setText(destination_name + " - " + origin_name);

        Moves();
    }

    public void Moves(){
        // PART 1
        // create bundle to parse data to fragments
        Bundle fragment_bundle_start = new Bundle();
        fragment_bundle_start.putSerializable("outbound_list", itinerary.getOutbound_list());

        // create first fragment (start)
        Fragment_start fragment_start = new Fragment_start();
        fragment_start.setArguments(fragment_bundle_start);

        // replace first frame layout
        getSupportFragmentManager().beginTransaction().replace(
                R.id.start_part_frame_layout_togo, fragment_start).commit();

        // create last fragment (end)
        Fragment_end fragment_end = new Fragment_end();
        fragment_end.setArguments(fragment_bundle_start);

        // replace last frame layout
        getSupportFragmentManager().beginTransaction().replace(
                R.id.last_part_frame_layour_togo, fragment_end).commit();

        if(itinerary.getOutbound_list().size() != 1 ){

            // create bundle to parse list and position to fragment
            Bundle fragment_bundle_stops = new Bundle();
            fragment_bundle_stops.putSerializable("outbound_list", itinerary.getOutbound_list());
            fragment_bundle_stops.putInt("list_position", 1);

            // create middle fragment (stops)
            Fragment_stops fragment_stops = new Fragment_stops();
            fragment_stops.setArguments(fragment_bundle_stops);

            //replace middle frame layout
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.mid_part_frame_layour_togo, fragment_stops).commit();

            if(itinerary.getOutbound_list().size() == 3){

                // create bundle to parse list and position to fragment if list has more than 2 stops
                Bundle fragment_bundle_stops2 = new Bundle();
                fragment_bundle_stops2.putSerializable("outbound_list", itinerary.getOutbound_list());
                fragment_bundle_stops2.putInt("list_position", 2);

                //create second mid part frame layout if needed
                Fragment_stops fragment_stops2 = new Fragment_stops();
                fragment_stops2.setArguments(fragment_bundle_stops2);

                //replace middle 2 frame layout
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.mid_part_2_frame_layour_togo, fragment_stops2).commit();
            }
        }

        // PART 2
        // create bundle to parse data to fragments
        Bundle fragment_bundle_start_return = new Bundle();
        fragment_bundle_start_return.putSerializable("outbound_list", itinerary.getInbound_list());

        // create first fragment (start)
        Fragment_start fragment_start_return = new Fragment_start();
        fragment_start_return.setArguments(fragment_bundle_start_return);

        // replace first frame layout
        getSupportFragmentManager().beginTransaction().replace(
                R.id.start_part_frame_layout_return, fragment_start_return).commit();

        // create last fragment (end)
        Fragment_end fragment_end_return = new Fragment_end();
        fragment_end_return.setArguments(fragment_bundle_start_return);

        // replace last frame layout
        getSupportFragmentManager().beginTransaction().replace(
                R.id.last_part_frame_layour_return, fragment_end_return).commit();

        if(itinerary.getInbound_list().size() != 1 ){

            // create bundle to parse list and position to fragment
            Bundle fragment_bundle_stops_return = new Bundle();
            fragment_bundle_stops_return.putSerializable("outbound_list", itinerary.getInbound_list());
            fragment_bundle_stops_return.putInt("list_position", 1);

            // create middle fragment (stops)
            Fragment_stops fragment_stops = new Fragment_stops();
            fragment_stops.setArguments(fragment_bundle_stops_return);

            //replace middle frame layout
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.mid_part_frame_layour_return, fragment_stops).commit();

            if(itinerary.getInbound_list().size() == 3){

                // create bundle to parse list and position to fragment if list has more than 2 stops
                Bundle fragment_bundle_stops2_return = new Bundle();
                fragment_bundle_stops2_return.putSerializable("outbound_list", itinerary.getInbound_list());
                fragment_bundle_stops2_return.putInt("list_position", 2);

                //create second mid part frame layout if needed
                Fragment_stops fragment_stops2 = new Fragment_stops();
                fragment_stops2.setArguments(fragment_bundle_stops2_return);

                //replace middle 2 frame layout
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.mid_part_2_frame_layour_return, fragment_stops2).commit();
            }
        }
        // set price and refundable
        if(itinerary.getRefundable())
            refundable.setText("YES");
        else
            refundable.setText("NO");

        refundable.setText("NO");

        if(itinerary.getChange_penalties())
            penalty.setText("YES");
        else
            penalty.setText("NO");

        buy.setAllCaps(false);
        buy.setText("Buy now! " + itinerary.getTotal_price());
    }

    public class call_api_for_cities extends AsyncTask<Void, Void, String> {
        String iata_code;
        String name;

        public call_api_for_cities(String iata_code) {
            this.iata_code = iata_code;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                final String baseUrl = "https://gist.githubusercontent.com/tdreyno/4278655/raw/7b0762c09b519f40397e4c3e100b097d861f5588/airports.json";

                Uri buildUri = Uri.parse(baseUrl).buildUpon().build();

                Log.i(getClass().toString(), buildUri.toString());
                URL url = new URL(buildUri.toString());

                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJSon = buffer.toString();
                JSONArray parentArray = new JSONArray(finalJSon);
                Log.i("iata code: ", iata_code);
                for(int i=0; i<parentArray.length(); i++){
                    JSONObject item = parentArray.getJSONObject(i);
                    String code = item.getString("code");
                    if(code.equals(iata_code)){
                        name = item.getString("city");
                        break;
                    }
                }
                Log.i(TAG, "city name: " + name);
            }
            catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
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
