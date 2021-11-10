package com.sru464.activityanalyzer.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.sru464.activityanalyzer.R;
import com.sru464.activityanalyzer.UserPreferences;


public class settings_units extends Fragment {

    private SettingsViewModel settingsViewModel;
    SwitchCompat switchCompat;
    SharedPreferences sharedPrefs;
    Spinner spinner;
    Spinner spinner2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final NavController navController = NavHostFragment.findNavController(this);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        View v = inflater.inflate(R.layout.fragment_settings_units, container, false);

        // [18]
        // Finding Switch ID, Shared Preferences, and Initializing Nightmode Variable
        switchCompat = (SwitchCompat) v.findViewById(R.id.switch_dark);
        sharedPrefs = getActivity().getSharedPreferences(UserPreferences.getPreferencesFileName(), 0);

        // [17]
        boolean nightMode = sharedPrefs.getBoolean("night_mode", false);

        // If nightMode == true, it enables night mode
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchCompat.setChecked(true);
        }

        // [10] [11] [17] [18]
        // Checks the switch in real-time to see if it is checked or not.
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switchCompat.setChecked(true);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("night_mode", true);
                    editor.apply();
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switchCompat.setChecked(false);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("night_mode", false);
                    editor.apply();
                }
            }
        });

        // [1]
        // Electrical Units Drop-down Box (Spinner)
        spinner = (Spinner) v.findViewById(R.id.unit_spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(),
                R.array.spinner1_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Time Interval Drop-down Box (Spinner)
        spinner2 = (Spinner) v.findViewById(R.id.unit_spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(v.getContext(),
                R.array.spinner2_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        // Spinner 1 Shared Preference Receiver
        int unitsValue = sharedPrefs.getInt("UNIT_TYPE", 0);
        if (unitsValue != 0) {
            spinner.setSelection(unitsValue);
        }

        // [1]
        // Spinner 1 (Electrical Units) calls this when there is a change with the slider
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When something is selected, it saves it to shared preferences
                String selected = spinner.getSelectedItem().toString();
                SharedPreferences.Editor spinEditor = sharedPrefs.edit();

                // It also gets sent to the UserPreferences
                switch (selected) {
                    case "By Wattage":
                        UserPreferences.setUnitType(2);
                        break;
                    case "By Voltage":
                        UserPreferences.setUnitType(0);
                        break;
                    case "By Ampere":
                        UserPreferences.setUnitType(1);
                        break;
                }

                spinEditor.putInt("UNIT_TYPE", spinner.getSelectedItemPosition());
                spinEditor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // [17]
        // Spinner 2 Shared Preference Receiver
        int timeValue = sharedPrefs.getInt("UNIT_TIME_INTERVAL", 10);
        if (timeValue != 10) {
            spinner2.setSelection(timeValue);
        }

        // [1]
        // Spinner 2 (Time Intervals) calls this when there is a change with the slider
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // [17]
                // When something is selected, it saves it to shared preferences
                String selected = spinner2.getSelectedItem().toString();
                SharedPreferences.Editor spinEditor2 = sharedPrefs.edit();

                // Also sent to UserPreferences
                switch (selected) {
                    case "5 seconds":
                        UserPreferences.setThreadInterval(5);
                        break;
                    case "10 seconds":
                        UserPreferences.setThreadInterval(10);
                        break;
                    case "30 seconds":
                        UserPreferences.setThreadInterval(30);
                        break;
                    case "60 seconds":
                        UserPreferences.setThreadInterval(60);
                        break;
                    case "300 seconds":
                        UserPreferences.setThreadInterval(300);
                        break;
                }

                spinEditor2.putInt("UNIT_TIME_INTERVAL", spinner2.getSelectedItemPosition());
                spinEditor2.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return v;
    }
}

