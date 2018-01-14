package com.example.onair;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Fragment_stops extends Fragment {

    ViewHolder viewHolder;
    private ArrayList<Flight> list;
    private int list_position;
    public static final String TAG = "Fragment_stops";
    private String origin_airport, departure_time, destination_airport, arrive_time,
            airline_name, flight_number, aircraft, travel_class, departure_date, arrive_date,
            previous_destination_airport, previous_arrive_time, previous_arrive_date, seats, waiting_time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_stops, container, false);

        Log.i(TAG, "started");

        list = (ArrayList<Flight>) getArguments().getSerializable("outbound_list");
        list_position = getArguments().getInt("list_position");
        viewHolder = new ViewHolder();

        // initialize widgets
        viewHolder.origin_airport_detail = (TextView) view.findViewById(R.id.origin_airport_stops);
        viewHolder.departure_time_detail = (TextView) view.findViewById(R.id.departure_time_stops);
        viewHolder.departure_date = (TextView) view.findViewById(R.id.departure_date_stops);

        viewHolder.airline_name = (TextView) view.findViewById(R.id.airline_name_stops);
        viewHolder.flight_number = (TextView) view.findViewById(R.id.flight_number_stops);
        viewHolder.aircraft = (TextView) view.findViewById(R.id.aircraft_stops);
        viewHolder.travel_class = (TextView) view.findViewById(R.id.travel_class_stops);
        viewHolder.seats = (TextView) view.findViewById(R.id.seats_remaining_stop);
        viewHolder.waiting_time = (TextView) view.findViewById(R.id.waiting_time);

        // previous
        viewHolder.previous_destination_airport = (TextView) view.findViewById(R.id.destination_airport_start);
        viewHolder.previous_arrive_time = (TextView) view.findViewById(R.id.arrive_time_start);
        viewHolder.previous_arrive_date = (TextView) view.findViewById(R.id.arrive_date_start);

        //take data
        take_data_for_stops();

        // set values to widget
        viewHolder.origin_airport_detail.setText(origin_airport);
        viewHolder.departure_time_detail.setText(departure_time);
        viewHolder.departure_date.setText(departure_date);

        //previus
        viewHolder.previous_destination_airport.setText(previous_destination_airport);
        viewHolder.previous_arrive_time.setText(previous_arrive_time);
        viewHolder.previous_arrive_date.setText(previous_arrive_date);

        // extras
        viewHolder.airline_name.setText(airline_name);
        viewHolder.flight_number.setText(flight_number);
        viewHolder.aircraft.setText(aircraft);
        viewHolder.travel_class.setText(travel_class);
        viewHolder.seats.setText(seats);
        viewHolder.waiting_time.setText(waiting_time);

        // Inflate the layout for this fragment
        return view;
    }

    public static class ViewHolder{
        TextView origin_airport_detail, departure_time_detail, previous_destination_airport, previous_arrive_time, previous_arrive_date;
        TextView airline_name, flight_number, aircraft, travel_class, departure_date, seats, waiting_time;
    }

    public void take_data_for_stops(){
        //airports
        origin_airport = list.get(list_position).getOrigin_airport();
        destination_airport = list.get(list_position).getDestination_airport();
        previous_destination_airport = list.get(list_position -1).getDestination_airport();

        //departure
        departure_time = list.get(list_position).getDeparts_at().substring(11);
        SimpleDateFormat formatter_before = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter_after = new SimpleDateFormat("dd-MM-yyyy");
        String depart_date_after = null, arrive_date_after = null, prev_depart_date_after = null;
        try{
            depart_date_after = formatter_after.format(formatter_before.parse(list.get(list_position).getDeparts_at().substring(0,10)));
            arrive_date_after = formatter_after.format(formatter_before.parse(list.get(list_position).getArrives_at().substring(0,10)));
            prev_depart_date_after = formatter_after.format(formatter_before.parse(list.get(list_position -1).getArrives_at().substring(0,10)));
        }catch (ParseException e){
        }
        departure_date = depart_date_after;

        //arrive
        arrive_time = list.get(list_position).getArrives_at().substring(11);
        arrive_date = arrive_date_after;
        previous_arrive_time = list.get(list_position -1).getArrives_at().substring(11);
        previous_arrive_date = prev_depart_date_after;

        //airline name
        airline_name = list.get(list_position).getAirline_name();

        //more
        //airline_name = list.get(position).get
        flight_number = list.get(list_position).getFlight_number();
        aircraft = list.get(list_position).getAircraft();
        travel_class = list.get(list_position).getTravel_class();
        seats = String.valueOf(list.get(list_position).getSeats_remaining());

        // calculate the waiting time
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date1 = simpleDateFormat.parse(previous_arrive_time);
            Date date2 = simpleDateFormat.parse(departure_time);

            // at minutes
            Long tempDate = (date2.getTime() - date1.getTime()) / 1000 / 60;
            // hours and minutes
            Long hours = Math.abs(tempDate / 60);
            Long minutes = Math.abs(tempDate % 60);
            waiting_time = hours + " hours & " + minutes + " minutes";
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
