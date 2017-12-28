package com.example.onair;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class Detail_activity2 extends AppCompatActivity {
    ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity2);
        viewHolder = new ViewHolder();

        // cast
        viewHolder.from_to = (TextView) findViewById(R.id.from_to);
        viewHolder.listView = (ListView) findViewById(R.id.listview_detail);
        viewHolder.buy = (Button) findViewById(R.id.buy);

        // get extra from intent
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        viewHolder.itinerary = (Itinerary) bundle.getSerializable("itinerary");

        //set
        viewHolder.from_to.setText(viewHolder.itinerary.getOutbound_list().get(0).getOrigin_airport() +"-"+
                viewHolder.itinerary.getOutbound_list().get(viewHolder.itinerary.getOutbound_list().size() -1).getDestination_airport());
        viewHolder.buy.setText(viewHolder.itinerary.getTotal_price());

        myListAdapter_vol3 adapter = new myListAdapter_vol3(getApplicationContext(), viewHolder.itinerary.getOutbound_list());
        viewHolder.listView.setAdapter(adapter);


    }

    public static class ViewHolder{
        TextView from_to;
        ListView listView;
        Button buy;
        Itinerary itinerary;
    }


}
