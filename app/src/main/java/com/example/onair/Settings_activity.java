package com.example.onair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import java.util.List;

public class Settings_activity extends PreferenceActivity
                implements Preference.OnPreferenceChangeListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_general);

        Preference currencyPreference = findPreference(getString(R.string.key_currency));
        currencyPreference.setOnPreferenceChangeListener(this);
        onPreferenceChange(currencyPreference, PreferenceManager
                                .getDefaultSharedPreferences(currencyPreference.getContext())
                                .getString(currencyPreference.getKey(), ""));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        String sharedPreferenceCurrencyValue = null;

        if(preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex >= 0){
                preference.setSummary(listPreference.getEntries()[prefIndex]);
                Log.i(getClass().toString(), listPreference.getEntries()[prefIndex].toString());
                sharedPreferenceCurrencyValue = listPreference.getEntries()[prefIndex].toString();
            }


        }
        sharedPreferencesMethod(sharedPreferenceCurrencyValue);
        return true;
    }

    public void sharedPreferencesMethod(String value){
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currency", value);
        Log.i(getClass().toString(), value);
        editor.commit();
        Toast.makeText(this, value + " selected", Toast.LENGTH_SHORT).show();
    }

}
