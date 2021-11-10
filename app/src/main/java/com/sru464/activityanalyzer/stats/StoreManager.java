package com.sru464.activityanalyzer.stats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sru464.activityanalyzer.MainActivity;
import com.sru464.activityanalyzer.stats.roomdb.VoltageDatabase;
import com.sru464.activityanalyzer.stats.roomdb.VoltageStamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class StoreManager extends BroadcastReceiver {
    /* Notes
     * StoreManager: https://developer.android.com/reference/android/os/storage/StorageManager
     * https://developer.android.com/training/data-storage
     * Room (Database): https://developer.android.com/training/data-storage/room
     * Calendar: https://developer.android.com/reference/java/util/Calendar.html
     * RoomDB: https://medium.com/mindorks/using-room-database-android-jetpack-675a89a0e942
     */

    // Variables
    private static int FETCH_TICK = 0;
    private static MainActivity activity;

    public static void sendObj(MainActivity item) {
        activity = item;
    }

    // Database
    VoltageDatabase appDB = VoltageDatabase.getInstance(activity);

    // [16]
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            FETCH_TICK++;
            if (FETCH_TICK == 5) {
                Calendar cal = Calendar.getInstance();
                int currHour = cal.get(Calendar.HOUR_OF_DAY);
                VoltageStamp volt = new VoltageStamp(currHour, GraphData.getCurrentVoltage(), GraphData.getCurrentAmps());
                appDB.voltageDao().insertStamp(volt);
                FETCH_TICK = 0;
            }
        }
    }
}
