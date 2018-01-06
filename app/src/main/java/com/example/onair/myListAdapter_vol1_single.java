package com.example.onair;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class myListAdapter_vol1_single extends ArrayAdapter<Itinerary> {

    private Context activityContext;
    private ArrayList<Itinerary> list;
    private ArrayList<Flight> flights;
    public static final String TAG = "ListView_single";
    private String depart_time, arrive_time, direct, the_airline, origin_airport, destination_airport;
    private HashMap<String, String> airlines;
    private String[] airlines_codes = getContext().getResources().getStringArray(R.array.airline_codes);
    private String[] airlines_names = getContext().getResources().getStringArray(R.array.airline_names);

    public myListAdapter_vol1_single(Context context, ArrayList<Itinerary> list){
        super(context, R.layout.my_list_adapter_vol1, list);
        this.activityContext = context;
        this.list = list;
        airlines = new HashMap<>();
        for(int i = 0; i< airlines_codes.length; i++){
            airlines.put(airlines_codes[i], airlines_names[i]);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(view == null){
            view = LayoutInflater.from(activityContext).inflate(R.layout.my_list_adapter_vol1, null);
            viewHolder = new ViewHolder();

            viewHolder.departureTime = (TextView) view.findViewById(R.id.departureTime);
            viewHolder.origin_airport = (TextView) view.findViewById(R.id.origin_airport);
            viewHolder.arriveTime = (TextView) view.findViewById(R.id.arriveTime);
            viewHolder.destination_airport = (TextView) view.findViewById(R.id.destination_airport);
            viewHolder.direct = (TextView) view.findViewById(R.id.direct);
            viewHolder.price = (TextView) view.findViewById(R.id.price);
            viewHolder.airline_name = (TextView) view.findViewById(R.id.airline_name);

            draw_data_from_flights(position);

            viewHolder.departureTime.setText(depart_time);
            viewHolder.origin_airport.setText(origin_airport);
            viewHolder.arriveTime.setText(arrive_time);
            viewHolder.destination_airport.setText(destination_airport);
            viewHolder.airline_name.setText(the_airline);
            viewHolder.direct.setText(direct);
            viewHolder.price.setText(list.get(position).getTotal_price());

            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        return view;
    }

    public static class ViewHolder{
        TextView  departureTime, origin_airport, arriveTime, destination_airport, direct, price, airline_name ;
    }

    public void draw_data_from_flights(int position) {
        flights = list.get(position).getOutbound_list();
        ArrayList<String> temp_airlines_list = new ArrayList<>();

        // set airline name at all flights
        for(int pos=0; pos<flights.size(); pos++) {
            flights.get(pos).setAirline_name(airlines.get(flights.get(pos).getOperating_airline()));
            temp_airlines_list.add(flights.get(pos).getAirline_name());
            Log.i("airline name added", flights.get(pos).getAirline_name() + "");
        }

        //combination of airlines or just one airline
        HashSet<String> duplicate_set = new HashSet<String>(temp_airlines_list);
        Log.i("duplicates ", duplicate_set.size() +" / "+ temp_airlines_list.size() +" / "+ flights.size());
        if(duplicate_set.size() < temp_airlines_list.size()){
            //there are duplicates
            if(duplicate_set.size() == 1)
                the_airline = temp_airlines_list.get(0);
            else if(duplicate_set.size() == 2)
                the_airline = temp_airlines_list.get(0) + " & " + temp_airlines_list.get(1);
            else
                the_airline = "Combination of airlines";
        }else
            if(duplicate_set.size() == 1)
                the_airline = temp_airlines_list.get(0);
            else if(duplicate_set.size() == 2)
                the_airline = temp_airlines_list.get(0) + " & " + temp_airlines_list.get(1);
            else
                the_airline = "Combination of airlines";

        if(flights.size() == 1)
            direct = "Direct";
        else
            direct = "Stops: " + (flights.size() - 1) ;

        depart_time = flights.get(0).getDeparts_at().substring(11);
        origin_airport = flights.get(0).getOrigin_airport();
        arrive_time = flights.get(flights.size() - 1).getArrives_at().substring(11);
        destination_airport = flights.get(flights.size() - 1).getDestination_airport();
    }
}
