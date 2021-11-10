package com.sru464.activityanalyzer.stats.roomdb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sru464.activityanalyzer.MainActivity;
import com.sru464.activityanalyzer.stats.GraphData;

import java.util.Random;

// [21]
public class FetchService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            public void run() {
                Random rand = new Random();
                VoltageDatabase appDB = VoltageDatabase.getInstance(getApplicationContext());
                VoltageStamp volt = new VoltageStamp(rand.nextInt(24), 100, 25);
                appDB.voltageDao().insertStamp(volt);
            }
        });

        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // Run this Service every hour.
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
          alarm.RTC_WAKEUP,
          System.currentTimeMillis() + (1000 * 30),
          PendingIntent.getService(this, 0, new Intent(this, FetchService.class), 0)
        );
    }
}
