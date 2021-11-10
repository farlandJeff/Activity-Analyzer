package com.sru464.activityanalyzer.stats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/*
    This Battery Manager class is allowing us to pull direct data from the battery. From the
    capacity of the battery, whether it's charging or now, and even the voltage.

    To obtain this data from the Battery Manager, we need to use the Broadcast Receiver class.
    Everything inside the onReceive function happens as the app is launched.
*/

public class BatManager extends BroadcastReceiver {

    // [16]
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            // Setting the graphs with the appropriate data according to the user's preferences.
            GraphData.setCurrentVoltage(intent.getIntExtra(String.valueOf(BatteryManager.EXTRA_VOLTAGE), -1));
            GraphData.setCurrentAmps(intent.getIntExtra(String.valueOf(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW), -1));

            // [4] [7]
            int BATTERY_CURRENT_STATUS = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int BATTERY_CURRENT_LEVEL = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int BATTERY_SCALE = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int BATTERY_PERCENT = BATTERY_CURRENT_LEVEL * 100 / BATTERY_SCALE;
        }

    }

}

