package com.example.onair;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Fragment_end extends Fragment {

    Itinerary itinerary;
    ViewHolder viewHolder;
    private ArrayList<Flight> list;
    private String  destination_airport, arrive_time, arrive_date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_end, container, false);

        itinerary = (Itinerary) getArguments().getSerializable("itinerary");
        viewHolder = new ViewHolder();

        viewHolder.arrive_time_detail = (TextView) view.findViewById(R.id.arrive_time_end);
        viewHolder.destination_airport_detail = (TextView) view.findViewById(R.id.destination_airport_end);
        viewHolder.arrive_date = (TextView) view.findViewById(R.id.arrive_date_end);

        Log.i("iti", itinerary.getOutbound_list().get(0).getAirline_name());

        list = itinerary.getOutbound_list();
        //take data
        take_data_from_list(0);
        //take data

        viewHolder.destination_airport_detail.setText(destination_airport);
        viewHolder.arrive_time_detail.setText(arrive_time);
        viewHolder.arrive_date.setText(arrive_date);

        return view;
    }

    public static class ViewHolder{
        TextView  destination_airport_detail, arrive_time_detail, arrive_date;
    }

    public void take_data_from_list(int position){
        //airports
        destination_airport = list.get(position).getDestination_airport();

        //arrive
        arrive_time = list.get(position).getArrives_at().substring(11);
        arrive_date = list.get(position).getArrives_at().substring(0,10);

    }
}
