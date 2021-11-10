package com.sru464.activityanalyzer;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;
import com.sru464.activityanalyzer.stats.BatManager;
import com.sru464.activityanalyzer.stats.GraphData;
import com.sru464.activityanalyzer.stats.StoreManager;
import com.sru464.activityanalyzer.stats.roomdb.FetchService;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private BatManager batManager = new BatManager();
    private IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_power, R.id.nav_history, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // [17]
        // Initialize the Shared Preferences
        SharedPreferences sp = getSharedPreferences(UserPreferences.getPreferencesFileName(), Context.MODE_PRIVATE);

        // Receive NightMode preference on app startup
        boolean isEnabled = sp.getBoolean("night_mode", false);
        if (isEnabled) {
            // [10]
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // [17]
        // Set UserPreferences with the Shared Preferences
        // Unit Time set
        int unit_time = sp.getInt("UNIT_TIME_INTERVAL", 10);
        UserPreferences.setThreadInterval(unit_time);

        // Unit Type set
        int unit_type = sp.getInt("UNIT_TYPE", 0);
        UserPreferences.setUnitType(unit_type);

        // Notification Level set
        int notification_level = sp.getInt("NOTIFICATION_LEVEL", 0);
        UserPreferences.setNotificationLevel(notification_level);

        // Send the activity to the StoreManager
        StoreManager.sendObj(this);

        // Getting Battery and Usage notification preferences
        boolean isEnabled2 = sp.getBoolean("battery_check", false);
        boolean isEnabled3 = sp.getBoolean("usage_check", false);

        //Already Displayed Notifications
        final boolean[] alreadyDisplayedNotification = {false, false, false, false, false};

        // [4] [7] [20]
        // If notifications for battery is checked
        if (isEnabled2) {
            BroadcastReceiver broadcastReceiverBattery = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //Used for Cold,Overheat and Unspecified Behavior
                    int health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
                    //Used for Low battery notifications
                    int integerBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

                    //If Battery gets below 20% and not displayed yet
                    if (integerBatteryLevel < 20 && !alreadyDisplayedNotification[0]) {

                        //Creating Notification
                        //Must use context
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Low Battery Notification")
                                .setSmallIcon(R.drawable.ic_outline_battery_alert_24)
                                .setContentTitle("Battery Usage")
                                .setContentText("Your Battery is running low, plug in your device soon")
                                //Multiline Notifications
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Your Battery is running low, plug in your device soon"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        //Use context instead of "this"
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                        managerCompat.notify(2, builder.build());
                        alreadyDisplayedNotification[0] =true;
                    }
                    else if(health == BatteryManager.BATTERY_HEALTH_OVERHEAT && !alreadyDisplayedNotification[1]){
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Overheating Battery Notification")
                                .setSmallIcon(R.drawable.ic_outline_whatshot_24)
                                .setContentTitle("OVERHEATING")
                                .setContentText("Your Battery has begun to overheat, refrain from using your device")
                                //Multiline Notifications
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Your Battery has begun to overheat, refrain from using your device"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        //Use context instead of "this"
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                        managerCompat.notify(4, builder.build());
                        alreadyDisplayedNotification[1] =true;
                    }
                    else if(health == BatteryManager.BATTERY_HEALTH_COLD && !alreadyDisplayedNotification[2]){
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Cold Battery Notification")
                                .setSmallIcon(R.drawable.cold)
                                .setContentTitle("Cold Battery")
                                .setContentText("Your Battery is cold, it will drain faster and performance will suffer. Relocate to a warmer location")
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Your Battery is cold, it will drain faster and performance will suffer. Relocate to a warmer location"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        //Use context instead of "this"
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                        managerCompat.notify(3, builder.build());
                        alreadyDisplayedNotification[2] =true;
                    }
                    else if(health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE && !alreadyDisplayedNotification[3]){
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Unspecified Battery Notification")
                                .setSmallIcon(R.drawable.ic_baseline_warning_24)
                                .setContentTitle("Battery Failing")
                                .setContentText("We've detected an Unspecified Failure, your battery may be damaged")
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("We've detected an Unspecified Failure, your battery may be damaged"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        //Use context instead of "this"
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                        managerCompat.notify(5, builder.build());
                        alreadyDisplayedNotification[3] =true;
                    }
                }
            };
            registerReceiver(broadcastReceiverBattery, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        }

        // [16] [20]
        // If notifications for usage is checked
        if (isEnabled3) {
            BroadcastReceiver broadcastReceiverBattery = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //Change once Graph is Fixed
                    if (GraphData.getCurrentVoltage() < 6000 && !alreadyDisplayedNotification[4]) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Usage Notification")
                                .setSmallIcon(R.drawable.ic_baseline_data_usage_24)
                                .setContentTitle("High Usage")
                                .setContentText("Your usage is very high, consider closing applications")

                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                        //Use getContext instead of "this"
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                        managerCompat.notify(1, builder.build());
                        alreadyDisplayedNotification[4] =true;
                    }
                }
            };
            registerReceiver(broadcastReceiverBattery, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

        // [15]
        if (!isAccessGranted()) { // Taken from https://stackoverflow.com/questions/38686632/how-to-get-usage-access-permission-programmatically

            AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme)

                    .setTitle("Usage Access Permission")
                    .setMessage("Unfortunately we cannot give you a full experience without having Usage Access.")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent,0);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.dismiss();
                        }
                    })
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .create();

            //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();

        }

        // [21]
        // start Fetch Service
        startService(new Intent(this, FetchService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // [16]
    // Resumes the broadcast receiver for the battery information
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batManager, intentFilter);
    }

    // [16]
    // Pauses the broadcast receiver for the battery information
    @Override
    protected void onPause() {
        registerReceiver(batManager, intentFilter);
        super.onPause();
    }

    // [15]
    // Function checks to see if the user has the appropriate permissions for the app for it to
    // properly check battery status and activity.
    private boolean isAccessGranted() { // Taken from https://stackoverflow.com/questions/38686632/how-to-get-usage-access-permission-programmatically
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
