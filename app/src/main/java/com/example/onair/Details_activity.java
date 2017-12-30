package com.example.onair;

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
import android.widget.ListView;
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
import java.net.MalformedURLException;
import java.net.URL;

public class Details_activity extends AppCompatActivity {
    ViewHolder viewHolder;
    public static String storeCurrency;
    public static final String TAG = "Details_activity";
    private String origin_name, destination_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);
        viewHolder = new ViewHolder();

        // shared preferences currency
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        storeCurrency = sharedPreferences.getString("currency", "");

        // set activity name
        setTitle("Details");

        // cast
        viewHolder.from_to = (TextView) findViewById(R.id.from_to);
        viewHolder.buy = (Button) findViewById(R.id.buy);

        // get extra from intent
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        viewHolder.itinerary = (Itinerary) bundle.getSerializable("itinerary");

        //set airports name
        new call_api_for_cities(viewHolder.itinerary.getOutbound_list().get(0).getOrigin_airport()).execute();
        Log.i(TAG, origin_name +"");
        new call_api_for_cities(viewHolder.itinerary.getOutbound_list().get(
                                    viewHolder.itinerary.getOutbound_list().size() -1).getDestination_airport());
        Log.i(TAG, destination_name +"");

        viewHolder.from_to.setText(origin_name + " - " + destination_name);

       // create bundle to parse data to fragments
        Bundle fragment_bundle_start = new Bundle();
        fragment_bundle_start.putSerializable("outbound_list", viewHolder.itinerary.getOutbound_list());

        // create first fragment (start)
        Fragment_start fragment_start = new Fragment_start();
        fragment_start.setArguments(fragment_bundle_start);

        // replace first frame layout
        getSupportFragmentManager().beginTransaction().replace(
                R.id.start_part_frame_layout, fragment_start).commit();

        // create last fragment (end)
        Fragment_end fragment_end = new Fragment_end();
        fragment_end.setArguments(fragment_bundle_start);

        // replace last frame layout
        getSupportFragmentManager().beginTransaction().replace(
                R.id.last_part_frame_layour, fragment_end).commit();

       if(viewHolder.itinerary.getOutbound_list().size() != 1 ){

           // create bundle to parse list and position to fragment
           Bundle fragment_bundle_stops = new Bundle();
           fragment_bundle_stops.putSerializable("outbound_list", viewHolder.itinerary.getOutbound_list());
           fragment_bundle_stops.putInt("list_position", 1);

           // create middle fragment (stops)
           Fragment_stops fragment_stops = new Fragment_stops();
           fragment_stops.setArguments(fragment_bundle_stops);

           //replace middle frame layout
           getSupportFragmentManager().beginTransaction().replace(
                   R.id.mid_part_frame_layour, fragment_stops).commit();

           if(viewHolder.itinerary.getOutbound_list().size() == 3){

               // create bundle to parse list and position to fragment if list has more than 2 stops
               Bundle fragment_bundle_stops2 = new Bundle();
               fragment_bundle_stops2.putSerializable("outbound_list", viewHolder.itinerary.getOutbound_list());
               fragment_bundle_stops2.putInt("list_position", 2);

               //create second mid part frame layout if needed
               Fragment_stops fragment_stops2 = new Fragment_stops();
               fragment_stops2.setArguments(fragment_bundle_stops2);

               //replace middle 2 frame layout
               getSupportFragmentManager().beginTransaction().replace(
                       R.id.mid_part_2_frame_layour, fragment_stops2).commit();
           }
       }
        // set price
        viewHolder.buy.setText(viewHolder.itinerary.getTotal_price());
    }

    public static class ViewHolder{
        TextView from_to;
        Button buy;
        Itinerary itinerary;
    }

    public class call_api_for_cities extends AsyncTask<Void, Void, String> {
        String iata_code;

        public call_api_for_cities(String iata_code) {
            this.iata_code = iata_code;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                final String baseUrl = "https://iatacodes.org/api/v6/cities?";
                final String ApiKeyParam = "api_key";
                final String codeParam = "code";

                Uri buildUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(ApiKeyParam, BuildConfig.iata_api_for_cities)
                        .appendQueryParameter(codeParam, iata_code).build();

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

                JSONObject request = parentObject.getJSONObject("request");
                String lang = request.getString("lang");
                Log.i(TAG, lang +"");

                JSONArray response = parentObject.getJSONArray("response");
                JSONObject inside = response.getJSONObject(0);
                origin_name = inside.getString("name");
                Log.i(TAG, origin_name + "");
            }
            catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
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
