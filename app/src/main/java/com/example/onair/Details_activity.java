package com.example.onair;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Details_activity extends AppCompatActivity {
    ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);
        viewHolder = new ViewHolder();

        // cast
        viewHolder.from_to = (TextView) findViewById(R.id.from_to);
       // viewHolder.listView = (ListView) findViewById(R.id.listview_detail);
        viewHolder.buy = (Button) findViewById(R.id.buy);

        // get extra from intent
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        viewHolder.itinerary = (Itinerary) bundle.getSerializable("itinerary");

        //set airports
        viewHolder.from_to.setText(viewHolder.itinerary.getOutbound_list().get(0).getOrigin_airport() +"-"+
                viewHolder.itinerary.getOutbound_list().get(viewHolder.itinerary.getOutbound_list().size() -1).getDestination_airport());

       // create bundle to parse data to fragments
        Bundle fragment_bundle = new Bundle();
        fragment_bundle.putSerializable("itinerary", viewHolder.itinerary);

        // create first fragment (start)
        Fragment_start fragment_start = new Fragment_start();
        fragment_start.setArguments(fragment_bundle);

        // replace first frame layout
        getSupportFragmentManager().beginTransaction().replace(
                R.id.first_part_frame_layout, fragment_start).commit();

        // create last fragment (end)
        Fragment_end fragment_end = new Fragment_end();
        fragment_end.setArguments(fragment_bundle);

        // replace last frame layout
        getSupportFragmentManager().beginTransaction().replace(
                R.id.last_part_frame_layour, fragment_end).commit();

       if(viewHolder.itinerary.getOutbound_list().size() != 1 ){

           // create middle fragment (stops)
           Fragment_stops fragment_stops = new Fragment_stops();
           fragment_stops.setArguments(fragment_bundle);

           //replace middle frame layout
           getSupportFragmentManager().beginTransaction().replace(
                   R.id.second_part_frame_layour, fragment_stops).commit();
       }

        // set price
        viewHolder.buy.setText(viewHolder.itinerary.getTotal_price());
    }

    public static class ViewHolder{
        TextView from_to;
        ListView listView;
        Button buy;
        Itinerary itinerary;
    }


}
