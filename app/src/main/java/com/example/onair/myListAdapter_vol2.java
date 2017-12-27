package com.example.onair;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

public class myListAdapter_vol2 extends ArrayAdapter<Itinerary> {

    private Context activityContext;
    private ArrayList<Itinerary> list;
    private ArrayList<Flight> flights_togo, flights_return;
    public static final String TAG = "ListView";
    private String depart_time, origin_airport, arrive_time, destination_airport, direct, the_airline,
            depart_time_return, origin_airport_return, arrive_time_return, destination_airport_return, direct_return;
    private HashMap<String, String> airlines;
    private String[] airlines_codes = getContext().getResources().getStringArray(R.array.airline_codes);
    private String[] airlines_names = getContext().getResources().getStringArray(R.array.airline_names);

    public myListAdapter_vol2(Context context, ArrayList<Itinerary> list){
        super(context, R.layout.list_item_with_return, list);
        this.activityContext = context;
        this.list = list;
        airlines = new HashMap<>();
        for(int i=0; i<airlines_codes.length; i++){
            airlines.put(airlines_codes[i], airlines_names[i]);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(view == null){
            view = LayoutInflater.from(activityContext).inflate(R.layout.list_item_with_return, null);
            viewHolder = new ViewHolder();

            //cast 1
            viewHolder.departureTime = (TextView) view.findViewById(R.id.departureTime_togo);
            viewHolder.origin_airport = (TextView) view.findViewById(R.id.origin_airport_togo);
            viewHolder.arriveTime = (TextView) view.findViewById(R.id.arriveTime_togo);
            viewHolder.destination_airport = (TextView) view.findViewById(R.id.destination_airport_togo);
            viewHolder.direct = (TextView) view.findViewById(R.id.direct_togo);
            viewHolder.price = (TextView) view.findViewById(R.id.price_return);

            //cast 2
            viewHolder.departureTime_return = (TextView) view.findViewById(R.id.returnTime_return);
            viewHolder.origin_airport_return = (TextView) view.findViewById(R.id.origin_airport_return);
            viewHolder.arriveTime_return = (TextView) view.findViewById(R.id.arriveTime_return);
            viewHolder.destination_airport_return = (TextView) view.findViewById(R.id.destination_airport_return);
            viewHolder.direct_return = (TextView) view.findViewById(R.id.direct_return);
            viewHolder.airline_name = (TextView) view.findViewById(R.id.airline_name_vol2);

            draw_data_from_flights(position);

            //fill 1
            viewHolder.departureTime.setText(depart_time);
            viewHolder.origin_airport.setText(origin_airport);
            viewHolder.arriveTime.setText(arrive_time);
            viewHolder.destination_airport.setText(destination_airport);
            viewHolder.direct.setText(direct);
            viewHolder.price.setText(list.get(position).getTotal_price());

            //fill 2
            viewHolder.departureTime_return.setText(depart_time_return);
            viewHolder.origin_airport_return.setText(origin_airport_return);
            viewHolder.arriveTime_return.setText(arrive_time_return);
            viewHolder.destination_airport_return.setText(destination_airport_return);
            viewHolder.direct_return.setText(direct_return);
            viewHolder.airline_name.setText(the_airline);

            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        return view;
    }

    public static class ViewHolder{
        TextView origin_airport, destination_airport, departureTime, arriveTime, direct, price, airline_name;
        TextView origin_airport_return, destination_airport_return, departureTime_return, arriveTime_return, direct_return;
    }

    public void draw_data_from_flights(int position){
        flights_togo = list.get(position).getOutbound_list();
        flights_return = list.get(position).getInbound_list();

        String airline1 = null;
        String airline2 = null;
        // find airline name
        // fill the airline textview
        if(flights_togo.size() == 1)
            airline1 = airlines.get(flights_togo.get(0).getOperating_airline());
        else
            if (!flights_togo.get(0).getOperating_airline().equals(flights_togo.get(1).getOperating_airline()))
                airline1 = "Combination of airlines";


        // find the airline name for return
        if(flights_return.size() == 1)
            airline2 = airlines.get(flights_return.get(0).getOperating_airline());
        else
            if (!flights_return.get(0).getOperating_airline().equals(flights_return.get(1).getOperating_airline()))
                airline2 = "Combination of airlines";


        if(airline1 == airline2)
            the_airline = airline1;
        else
            the_airline = "Combination of airlines";

        if(flights_togo.size() == 1)
            direct = "Direct";
        else
            direct = "Stops: " + (flights_togo.size() - 1) ;

        if(flights_return.size() == 1)
            direct_return = "Direct";
        else
            direct_return = "Stops: " + (flights_return.size() - 1) ;

        //to go
        depart_time = flights_togo.get(0).getDeparts_at().substring(11);
        origin_airport = flights_togo.get(0).getOrigin_airport();
        arrive_time = flights_togo.get(flights_togo.size() - 1).getArrives_at().substring(11);
        destination_airport = flights_togo.get(flights_togo.size() - 1).getDestination_airport();

        //return
        depart_time_return = flights_return.get(0).getDeparts_at().substring(11);
        origin_airport_return = flights_return.get(0).getOrigin_airport();
        arrive_time_return = flights_return.get(flights_return.size() - 1).getArrives_at().substring(11);
        destination_airport_return = flights_return.get(flights_return.size() - 1).getDestination_airport();
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
}
