package com.sru464.activityanalyzer.ui.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.sru464.activityanalyzer.R;
import com.sru464.activityanalyzer.UserPreferences;

/*
    The Notification page contains notification settings that allows the user to pick whether or not
    they want to receive notifications from the app. This involves usage and battery alerts.
*/

public class settings_notifications extends Fragment {

    private SettingsViewModel settingsViewModel;
    SharedPreferences sharedPrefs;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final NavController navController = NavHostFragment.findNavController(this);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        final View v = inflater.inflate(R.layout.fragment_settings_notifications, container, false);

        // [17] [19]
        // Initializing SharedPreferences and finding CheckBox IDs
        sharedPrefs = getActivity().getSharedPreferences(UserPreferences.getPreferencesFileName(), 0);
        final CheckBox usage = (CheckBox) v.findViewById(R.id.usagecheckBox);
        final CheckBox battery = (CheckBox) v.findViewById(R.id.batterycheckBox);

        // Checking to see if usageCheck is enabled
        final boolean usageCheck = sharedPrefs.getBoolean("usage_check", false);
        if (usageCheck) {
            usage.setChecked(true);
        }

        // Notification Channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Usage Notification", "Usage Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel2 = new NotificationChannel("Low Battery Notification", "Low Battery Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel3 = new NotificationChannel("Cold Battery Notification", "Cold Battery Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel4 = new NotificationChannel("Overheating Battery Notification", "Overheating Battery Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channel5 = new NotificationChannel("Unspecified Battery Notification", "Unspecified Battery Notification", NotificationManager.IMPORTANCE_DEFAULT);
            // Register the channel with the system
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            //Channel For Usage
            notificationManager.createNotificationChannel(channel);
            //Channel For Low Battery
            notificationManager.createNotificationChannel(channel2);
            //Channel for Cold Battery
            notificationManager.createNotificationChannel(channel3);
            //Channel for Overheated Battery
            notificationManager.createNotificationChannel(channel4);
            //Channel for Unspecified Battery
            notificationManager.createNotificationChannel(channel5);
        }

        //https://stackoverflow.com/questions/5393651/action-battery-low-not-being-fired-from-manifest
        //https://developer.android.com/training/monitoring-device-state/battery-monitoring#java

        // [12] [4]
        // Changing preferences based on whether the CheckBox is clicked or not
        usage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        usage.setChecked(true);
                        // [17]
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        //If checked save
                        editor.putBoolean("usage_check", true);

                        //Creating Notification
                        //Must use getContext in Fragments
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "Usage Notification")
                                .setSmallIcon(R.drawable.ic_baseline_data_usage_24)
                                .setContentTitle("High Usage")
                                .setContentText("Your usage is very high, consider closing applications")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        //Use getContext instead of "this"
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getContext());
                        managerCompat.notify(1, builder.build());

                        editor.apply();
                }

                else {
                    usage.setChecked(false);
                    // [17]
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("usage_check", false);
                    editor.apply();
                }
            }
        });

        // Same functionality as previous CheckBox
        boolean batteryCheck = sharedPrefs.getBoolean("battery_check", false);
        if (batteryCheck) {
            battery.setChecked(true);
        }

        // When the battery checkbox is changes, this method is called
        battery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // This saves the user's choice to the user preferences and saves it
                if (isChecked) {
                    battery.setChecked(true);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    //If checked save
                    editor.putBoolean("battery_check", true);
                    editor.apply();

                }

                else {
                    battery.setChecked(false);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("battery_check", false);
                    editor.apply();
                }
            }
        });

        return v;
    }
}

