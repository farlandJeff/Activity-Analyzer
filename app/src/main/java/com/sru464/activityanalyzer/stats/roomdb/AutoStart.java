package com.sru464.activityanalyzer.stats.roomdb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// [21]
public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, FetchService.class));
    }
}
