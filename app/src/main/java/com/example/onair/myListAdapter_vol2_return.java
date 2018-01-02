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

public class myListAdapter_vol2_return extends ArrayAdapter<Itinerary> {

    private Context activityContext;
    private ArrayList<Itinerary> list;
    private ArrayList<Flight> flights_togo, flights_return;
    public static final String TAG = "ListView";
    private String depart_time, origin_airport, arrive_time, destination_airport, direct, the_airline,
            depart_time_return, origin_airport_return, arrive_time_return, destination_airport_return, direct_return;
    private HashMap<String, String> airlines;
    private String[] airlines_codes = getContext().getResources().getStringArray(R.array.airline_codes);
    private String[] airlines_names = getContext().getResources().getStringArray(R.array.airline_names);

    public myListAdapter_vol2_return(Context context, ArrayList<Itinerary> list){
        super(context, R.layout.my_list_adapter_vol2, list);
        this.activityContext = context;
        this.list = list;
        airlines = new HashMap<>();
        for(int i=0; i<airlines_codes.length; i++){
            airlines.put(airlines_codes[i], airlines_names[i]);
        }
        Log.i(TAG, list.size() +"<---adapter list.size");
        for(Itinerary iti: list){
            Log.i(TAG, iti.getTotal_price()+"");
        }

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(view == null) {
            view = LayoutInflater.from(activityContext).inflate(R.layout.my_list_adapter_vol2, null);
            viewHolder = new ViewHolder();

            //cast 1
            viewHolder.departureTime = (TextView) view.findViewById(R.id.departureTime_togo);
            viewHolder.origin_airport = (TextView) view.findViewById(R.id.origin_airport_togo);
            viewHolder.arriveTime = (TextView) view.findViewById(R.id.arriveTime_togo);
            viewHolder.destination_airport = (TextView) view.findViewById(R.id.destination_airport_togo);
            viewHolder.direct = (TextView) view.findViewById(R.id.direct_togo);
            viewHolder.price = (TextView) view.findViewById(R.id.price_return);

            //cast 2
            viewHolder.departureTime_return = (TextView) view.findViewById(R.id.returnTime_return);
            viewHolder.origin_airport_return = (TextView) view.findViewById(R.id.origin_airport_return);
            viewHolder.arriveTime_return = (TextView) view.findViewById(R.id.arriveTime_return);
            viewHolder.destination_airport_return = (TextView) view.findViewById(R.id.destination_airport_return);
            viewHolder.direct_return = (TextView) view.findViewById(R.id.direct_return);
            viewHolder.airline_name = (TextView) view.findViewById(R.id.airline_name_vol2);

            draw_data_from_flights(getItem(position));

            //fill 1
            viewHolder.departureTime.setText(depart_time);
            viewHolder.origin_airport.setText(origin_airport);
            viewHolder.arriveTime.setText(arrive_time);
            viewHolder.destination_airport.setText(destination_airport);
            viewHolder.direct.setText(direct);

            //fill 2
            viewHolder.departureTime_return.setText(depart_time_return);
            viewHolder.origin_airport_return.setText(origin_airport_return);
            viewHolder.arriveTime_return.setText(arrive_time_return);
            viewHolder.destination_airport_return.setText(destination_airport_return);
            viewHolder.direct_return.setText(direct_return);

            //extras
            viewHolder.airline_name.setText(the_airline);
            viewHolder.price.setText(getItem(position).getTotal_price());
        }
        else
            viewHolder = (ViewHolder) view.getTag();

        return view;
    }

    public static class ViewHolder{
        TextView origin_airport, destination_airport, departureTime, arriveTime, direct, price, airline_name;
        TextView origin_airport_return, destination_airport_return, departureTime_return, arriveTime_return, direct_return;
    }

    public void draw_data_from_flights(Itinerary item){
        flights_togo = new ArrayList<>(list.get(item.getOutbound_list().size()).getOutbound_list());
        flights_return = new ArrayList<>(list.get(item.getInbound_list().size()).getInbound_list());

        // set airline name at all flights
        String the_airline_togo = set_airline_name(flights_togo);
        String the_airline_return = set_airline_name(flights_return);

        if(the_airline_togo == the_airline_return)
            the_airline = flights_togo.get(0).getAirline_name();
        else
            the_airline = "Combination of airlines";

        //direct or no
        direct = direct_or_no(flights_togo);
        direct_return = direct_or_no(flights_return);

        //to go
        depart_time = flights_togo.get(0).getDeparts_at().substring(11);
        origin_airport = flights_togo.get(0).getOrigin_airport();
        arrive_time = flights_togo.get(flights_togo.size() - 1).getArrives_at().substring(11);
        destination_airport = flights_togo.get(flights_togo.size() - 1).getDestination_airport();

        //return
        depart_time_return = flights_return.get(0).getDeparts_at().substring(11);
        origin_airport_return = flights_return.get(0).getOrigin_airport();
        arrive_time_return = flights_return.get(flights_return.size() - 1).getArrives_at().substring(11);
        destination_airport_return = flights_return.get(flights_return.size() - 1).getDestination_airport();
    }

    public String set_airline_name(ArrayList<Flight> flights){
        ArrayList<String> temp_airlines_list = new ArrayList<>();

        //set airline name at all flights
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
                return temp_airlines_list.get(0);
            else
                return "Combination of airlines";
        }else
            if(duplicate_set.size() == 1)
                return temp_airlines_list.get(0);
            else
                return "Combination of airlines";
    }

    public String direct_or_no(ArrayList<Flight> flights){

        if(flights.size() == 1)
            return "Direct";
        else
            return "Stops: " + (flights.size() - 1) ;
    }
}
