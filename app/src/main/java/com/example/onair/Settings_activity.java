package com.example.onair;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Settings_activity extends AppCompatActivity {

    TextView currencyClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currencyClick = (TextView) findViewById(R.id.currencyClick);
        onSettingClick();
    }

    private void onSettingClick(){

        currencyClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alert pop up dialog
                AlertDialog.Builder mbuild = new AlertDialog.Builder(Settings_activity.this);
                View mview = getLayoutInflater().inflate(R.layout.popup_radio_group, null);


                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.currencyRadioGroup);
                radioGroup = new RadioGroup(Settings_activity.this);
                String[] curr = getResources().getStringArray(R.array.nomismata);

                for(int i=0; i< curr.length; i++) {
                    RadioButton button = new RadioButton(Settings_activity.this);
                    Log.i(getClass().toString(), curr[i]);
                    button.setText("" + curr[i]);
                    radioGroup.addView(button);

                }


                //pre-checked 0
                //  int radio_button_Id = radioGroup.getChildAt(0).getId();
                //radioGroup.check( radio_button_Id );

                radioGroup.setOnCheckedChangeListener(
                        new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                // TODO shared preferences for currency
                            }
                        }
                );

                mbuild.setView(mview);
                AlertDialog dialog = mbuild.create();
                dialog.show();
            }
        });

    }

}
