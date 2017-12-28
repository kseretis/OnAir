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
import java.util.List;

public class myListAdapter_vol3 extends ArrayAdapter<Flight>{

    private Context activityContext;
    private ArrayList<Flight> list;
    private Flight flight;
    public static final String TAG = "ListView";
    private String origin_airport, departure_time, destination_airport, arrive_time,
            airline_name, flight_number, aircraft, travel_class, departure_date, arrive_date;

    public myListAdapter_vol3(Context context, ArrayList<Flight> list) {
        super(context, R.layout.detail_listview, list);
        this.activityContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
       ViewHolder viewHolder;

       if(view == null){
           view = LayoutInflater.from(activityContext).inflate(R.layout.detail_listview, null);
           viewHolder = new ViewHolder();

           //cast widgets
           viewHolder.origin_airport_detail = (TextView) view.findViewById(R.id.origin_airport_detail);
           viewHolder.departure_time_detail = (TextView) view.findViewById(R.id.departure_time_detail);
           viewHolder.destination_airport_detail = (TextView) view.findViewById(R.id.destination_airport_detail);
           viewHolder.arrive_time_detail = (TextView) view.findViewById(R.id.arrive_time_detail);
           viewHolder.airline_name = (TextView) view.findViewById(R.id.airline_name_detail);
           viewHolder.flight_number = (TextView) view.findViewById(R.id.flight_number_detail);
           viewHolder.aircraft = (TextView) view.findViewById(R.id.aircraft_detail);
           viewHolder.travel_class = (TextView) view.findViewById(R.id.travel_class_detail);
           viewHolder.departure_date = (TextView) view.findViewById(R.id.departure_date_detail);
           viewHolder.arrive_date = (TextView) view.findViewById(R.id.arrive_date_detail);

           //take data
           take_data_from_list(position);

           // set values to widget
           viewHolder.origin_airport_detail.setText(origin_airport);
           viewHolder.departure_time_detail.setText(departure_time);
           viewHolder.departure_date.setText(departure_date);
           viewHolder.destination_airport_detail.setText(destination_airport);
           viewHolder.arrive_time_detail.setText(arrive_time);
           viewHolder.arrive_date.setText(arrive_date);
           viewHolder.airline_name.setText( airline_name);
           viewHolder.flight_number.setText(flight_number);
           viewHolder.aircraft.setText(aircraft);
           viewHolder.travel_class.setText(travel_class);

           view.setTag(viewHolder);
       }
       else
           viewHolder = (ViewHolder) view.getTag();
        return view;
    }

    public static class ViewHolder{
        TextView origin_airport_detail, departure_time_detail, destination_airport_detail, arrive_time_detail;
        TextView airline_name, flight_number, aircraft, travel_class, departure_date, arrive_date;
    }

    public void take_data_from_list(int position){
        //airports
        origin_airport = list.get(position).getOrigin_airport();
        destination_airport = list.get(position).getDestination_airport();

        //departure
        departure_time = list.get(position).getDeparts_at().substring(11);
        departure_date = list.get(position).getDeparts_at().substring(0,10);

        //arrive
        arrive_time = list.get(position).getArrives_at().substring(11);
        arrive_date = list.get(position).getArrives_at().substring(0,10);

        //airline name
        airline_name = list.get(position).getAirline_name();

        //more
        //airline_name = list.get(position).get
        flight_number = list.get(position).getFlight_number();
        aircraft = list.get(position).getAircraft();
        travel_class = list.get(position).getTravel_class();
    }
}
