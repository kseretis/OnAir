package com.example.onair;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Fragment_stops extends Fragment {

    Itinerary itinerary;
    ViewHolder viewHolder;
    private ArrayList<Flight> list;
    private String origin_airport, departure_time, destination_airport, arrive_time,
            airline_name, flight_number, aircraft, travel_class, departure_date, arrive_date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_stops, container, false);

        itinerary = (Itinerary) getArguments().getSerializable("itinerary");
        viewHolder = new ViewHolder();

        viewHolder.origin_airport_detail = (TextView) view.findViewById(R.id.origin_airport_stops);
        viewHolder.departure_time_detail = (TextView) view.findViewById(R.id.departure_time_stops);
        viewHolder.departure_date = (TextView) view.findViewById(R.id.departure_date_stops);

        viewHolder.airline_name = (TextView) view.findViewById(R.id.airline_name_stops);
        viewHolder.flight_number = (TextView) view.findViewById(R.id.flight_number_stops);
        viewHolder.aircraft = (TextView) view.findViewById(R.id.aircraft_stops);
        viewHolder.travel_class = (TextView) view.findViewById(R.id.travel_class_stops);

       Log.i("iti", itinerary.getOutbound_list().get(0).getAirline_name());




        list = itinerary.getOutbound_list();
        //take data
        take_data_from_list(0);


        // set values to widget
        viewHolder.origin_airport_detail.setText(origin_airport);
        viewHolder.departure_time_detail.setText(departure_time);
        viewHolder.departure_date.setText(departure_date);

        // extras
        viewHolder.airline_name.setText(airline_name);
        viewHolder.flight_number.setText(flight_number);
        viewHolder.aircraft.setText(aircraft);
        viewHolder.travel_class.setText(travel_class);



        // Inflate the layout for this fragment
        return view;
    }

    public static class ViewHolder{
        TextView origin_airport_detail, departure_time_detail, destination_airport_detail, arrive_time_detail;
        TextView airline_name, flight_number, aircraft, travel_class, departure_date, arrive_date;
        ImageView location_icon;
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
