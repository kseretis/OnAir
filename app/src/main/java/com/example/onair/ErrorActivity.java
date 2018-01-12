package com.example.onair;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

    }

    @Override
    public void onBackPressed() {
        // back button changed
        Intent setIntent = new Intent(ErrorActivity.this, MainActivity.class);
        startActivity(setIntent);
    }
}
