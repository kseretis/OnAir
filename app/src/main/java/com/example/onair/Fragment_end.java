package com.example.onair;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Fragment_end extends Fragment {

    ViewHolder viewHolder;
    private ArrayList<Flight> list;
    public static final String TAG = "Fragment_end";
    private String  destination_airport, arrive_time, arrive_date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_end, container, false);

        Log.i(TAG, "started");

        list = (ArrayList<Flight>) getArguments().getSerializable("outbound_list");
        viewHolder = new ViewHolder();

        // initialize widgets
        viewHolder.arrive_time_detail = (TextView) view.findViewById(R.id.arrive_time_end);
        viewHolder.destination_airport_detail = (TextView) view.findViewById(R.id.destination_airport_end);
        viewHolder.arrive_date = (TextView) view.findViewById(R.id.arrive_date_end);

        // take data
        take_data_from_last_flight();

        // set data
        viewHolder.destination_airport_detail.setText(destination_airport);
        viewHolder.arrive_time_detail.setText(arrive_time);
        viewHolder.arrive_date.setText(arrive_date);

        return view;
    }

    public static class ViewHolder{
        TextView  destination_airport_detail, arrive_time_detail, arrive_date;
    }

    public void take_data_from_last_flight(){
        //airports
        destination_airport = list.get(list.size() - 1).getDestination_airport();

        //arrive
        arrive_time = list.get(list.size() - 1).getArrives_at().substring(11);
        arrive_date = list.get(list.size() - 1).getArrives_at().substring(0,10);
    }
}
