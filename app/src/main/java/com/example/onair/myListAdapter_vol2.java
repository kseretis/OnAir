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

public class myListAdapter_vol2 extends ArrayAdapter<Itinerary> {

    private Context activityContext;
    private ArrayList<Itinerary> list;
    private ArrayList<Flight> flights_togo, flights_return;
    public static final String TAG = "ListView";
    private String origin, desti, depart_time, depart_day, direct,
            origin_return, desti_return, depart_time_return, getDepart_day_return, direct_return;

    public myListAdapter_vol2(Context context, ArrayList<Itinerary> list){
        super(context, R.layout.list_item_with_return, list);
        this.activityContext = context;
        this.list = list;
        this.origin = null;
        this.desti = null;
        this.depart_time = null;
        this.depart_day = null;
        this.direct = null;
        this.origin_return = null;
        this.desti_return = null;
        this.depart_time_return = null;
        this.getDepart_day_return = null;
        this.direct_return = null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(view == null){
            view = LayoutInflater.from(activityContext).inflate(R.layout.list_item_with_return, null);
            viewHolder = new ViewHolder();

            //cast 1
            viewHolder.origin = (TextView) view.findViewById(R.id.origin_togo);
            viewHolder.destination = (TextView) view.findViewById(R.id.destination_togo);
            viewHolder.departureTime = (TextView) view.findViewById(R.id.departureTime_togo);
            viewHolder.departureDay = (TextView) view.findViewById(R.id.departureDay_togo);
            viewHolder.direct = (TextView) view.findViewById(R.id.direct_togo);
            viewHolder.price = (TextView) view.findViewById(R.id.price_return);

            //cast 2
            viewHolder.origin_return = (TextView) view.findViewById(R.id.origin_return);
            viewHolder.destination_return = (TextView) view.findViewById(R.id.destination_return);
            viewHolder.departureTime_return = (TextView) view.findViewById(R.id.returnTime_return);
            viewHolder.departureDay_return = (TextView) view.findViewById(R.id.returnDay_return);
            viewHolder.direct_return = (TextView) view.findViewById(R.id.direct_return);

            draw_data_from_flights(position);

            //fill 1
            viewHolder.origin.setText(origin);
            viewHolder.destination.setText(desti);
            viewHolder.departureTime.setText(depart_time);
            viewHolder.departureDay.setText(depart_day);
            viewHolder.direct.setText(direct);
            viewHolder.price.setText(list.get(position).getTotal_price());

            //fill 2
            viewHolder.origin_return.setText(origin_return);
            viewHolder.destination_return.setText(desti_return);
            viewHolder.departureTime_return.setText(depart_time_return);
            viewHolder.departureDay_return.setText(getDepart_day_return);
            viewHolder.direct_return.setText(direct_return);

            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        return view;
    }

    public static class ViewHolder{
        TextView origin, destination, departureTime, departureDay, direct, price;
        TextView origin_return, destination_return, departureTime_return, departureDay_return, direct_return;
    }

    public void draw_data_from_flights(int position){
        flights_togo = list.get(position).getOutbound_list();
        flights_return = list.get(position).getInbound_list();

        if(flights_togo.size() == 1) {
            origin = flights_togo.get(0).getOrigin_airport();
            desti = flights_togo.get(0).getDestination_airport();
            direct = "Direct";
        }
        else {
            origin = flights_togo.get(0).getOrigin_airport();
            desti = flights_togo.get(flights_togo.size() - 1).getDestination_airport();
            direct = "Stops: " + (flights_togo.size() - 1) ;
        }

        if(flights_return.size() == 1) {
            origin_return = flights_return.get(0).getOrigin_airport();
            desti_return = flights_return.get(0).getDestination_airport();
            direct_return = "Direct";
        }
        else {
            origin_return = flights_return.get(0).getOrigin_airport();
            desti_return = flights_return.get(flights_return.size() - 1).getDestination_airport();
            direct_return = "Stops: " + (flights_return.size() - 1) ;
        }

        depart_time = flights_togo.get(0).getDeparts_at().substring(11);
        depart_day = flights_togo.get(0).getDeparts_at().substring(0,10);
        depart_time_return = flights_return.get(0).getDeparts_at().substring(11);
        getDepart_day_return = flights_return.get(0).getDeparts_at().substring(0,10);
    }
}
