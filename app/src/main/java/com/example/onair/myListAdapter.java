package com.example.onair;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class myListAdapter extends ArrayAdapter<Itinerary> {

    private Context activityContext;
    private ArrayList<Itinerary> list;
    private ArrayList<Flight> flights;
    public static final String TAG = "ListView";
    private String depart_time, arrive_time, direct, the_airline, origin_airport, destination_airport;
    private HashMap<String, String> airlines;
    private String[] airlines_codes = getContext().getResources().getStringArray(R.array.airline_codes);
    private String[] airlines_names = getContext().getResources().getStringArray(R.array.airline_names);

    public myListAdapter(Context context, ArrayList<Itinerary> list){
        super(context, R.layout.list_item, list);
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
            view = LayoutInflater.from(activityContext).inflate(R.layout.list_item, null);
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

        // find airline name
        // fill the airline textview
        if(flights.size() == 1)
            the_airline = airlines.get(flights.get(0).getOperating_airline());
        else
            if(!flights.get(0).getOperating_airline().equals(flights.get(1).getOperating_airline()))
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
