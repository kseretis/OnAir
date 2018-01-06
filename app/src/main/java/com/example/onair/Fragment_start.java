package com.example.onair;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Fragment_start extends Fragment {

    ViewHolder viewHolder;
    private ArrayList<Flight> list;
    public static final String TAG = "Fragment_start";
    private String origin_airport, departure_time,  airline_name, flight_number, aircraft,
            travel_class, departure_date, seats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_start, container, false);

        Log.i(TAG, "started");

        list = (ArrayList<Flight>) getArguments().getSerializable("outbound_list");
        viewHolder = new ViewHolder();

        viewHolder.origin_airport_detail = (TextView) view.findViewById(R.id.origin_airport_start);
        viewHolder.departure_time_detail = (TextView) view.findViewById(R.id.departure_time_start);
        viewHolder.departure_date = (TextView) view.findViewById(R.id.departure_date_start);

        viewHolder.airline_name = (TextView) view.findViewById(R.id.airline_name_start);
        viewHolder.flight_number = (TextView) view.findViewById(R.id.flight_number_start);
        viewHolder.aircraft = (TextView) view.findViewById(R.id.aircraft_start);
        viewHolder.travel_class = (TextView) view.findViewById(R.id.travel_class_start);
        viewHolder.seats = (TextView) view.findViewById(R.id.seats_remaining_start);

        //take data
        take_data_from_first_flight(0);

        // set values to widget
        viewHolder.origin_airport_detail.setText(origin_airport);
        viewHolder.departure_time_detail.setText(departure_time);
        viewHolder.departure_date.setText(departure_date);

        // extras
        viewHolder.airline_name.setText(airline_name);
        viewHolder.flight_number.setText(flight_number);
        viewHolder.aircraft.setText(aircraft);
        viewHolder.travel_class.setText(travel_class);
        viewHolder.seats.setText(seats);

        // Inflate the layout for this fragment
        return view;
    }

    public static class ViewHolder{
        TextView origin_airport_detail, departure_time_detail, airline_name, flight_number, aircraft,
                        travel_class, departure_date, seats;
    }

    public void take_data_from_first_flight(int position){
        //airports
        origin_airport = list.get(position).getOrigin_airport();

        //departure
        departure_time = list.get(position).getDeparts_at().substring(11);
        departure_date = list.get(position).getDeparts_at().substring(0,10);

        //airline name
        airline_name = list.get(position).getAirline_name();

        //more
        //airline_name = list.get(position).get
        flight_number = list.get(position).getFlight_number();
        aircraft = list.get(position).getAircraft();
        travel_class = list.get(position).getTravel_class();
        seats = String.valueOf(list.get(position).getSeats_remaining());
    }
}
