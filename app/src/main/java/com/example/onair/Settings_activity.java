package com.example.onair;

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

        if(preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex >= 0)
                preference.setSummary(listPreference.getEntries()[prefIndex]);
        }

        return true;
    }
}
