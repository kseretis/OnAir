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

public class myListAdapter extends ArrayAdapter<Itinerary> {

    private Context activityContext;
    private ArrayList<Itinerary> list;
    private ArrayList<Flight> flights;
    public static final String TAG = "ListView";
    private String origin, desti, depart_time, depart_day, direct;

    public myListAdapter(Context context, ArrayList<Itinerary> list){
        super(context, R.layout.list_item, list);
        this.activityContext = context;
        this.list = list;
        this.origin = null;
        this.desti = null;
        this.depart_time = null;
        this.depart_day = null;
        this.direct = null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(view == null){
            view = LayoutInflater.from(activityContext).inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder();

            viewHolder.origin = (TextView) view.findViewById(R.id.origin);
            viewHolder.destination = (TextView) view.findViewById(R.id.destination);
            viewHolder.departureTime = (TextView) view.findViewById(R.id.departureTime);
            viewHolder.departureday = (TextView) view.findViewById(R.id.departureday);
            viewHolder.direct = (TextView) view.findViewById(R.id.direct);
            viewHolder.price = (TextView) view.findViewById(R.id.price);

            draw_data_from_flights(position);

            viewHolder.origin.setText(origin);
            viewHolder.destination.setText(desti);
            viewHolder.departureTime.setText(depart_time);
            viewHolder.departureday.setText(depart_day);
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
        TextView origin, destination, departureTime, departureday, direct, price;
    }

    public void draw_data_from_flights(int position){
        flights = list.get(position).getOutbound_list();

        if(flights.size() == 1) {
            origin = flights.get(0).getOrigin_airport();
            desti = flights.get(0).getDestination_airport();
            direct = "Direct";
        }
        else {
            origin = flights.get(0).getOrigin_airport();
            desti = flights.get(flights.size() - 1).getDestination_airport();
            direct = "Stops: " + (flights.size() - 1) ;
        }

        depart_time = flights.get(0).getDeparts_at().substring(11);
        depart_day = flights.get(0).getDeparts_at().substring(0,10);
    }
}
