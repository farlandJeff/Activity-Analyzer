package com.sru464.activityanalyzer.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;

import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.sru464.activityanalyzer.R;
import com.sru464.activityanalyzer.UserPreferences;
import com.sru464.activityanalyzer.ui.power.PowerFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    //Score
    TextView score;
    int scoreActual = 0;
    TextView memoryPercent;


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final NavController navController = NavHostFragment.findNavController(this);
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        //Setup for buttons
        score = v.findViewById(R.id.score_home);
        //Button
        ImageButton refreshButton = v.findViewById(R.id.refresh_button_home);
        memoryPercent = v.findViewById(R.id.memory_percentage);

        //Refresh Page
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Relaunches App
                getActivity().finish();
                startActivity(getActivity().getIntent());

            }
        });

        //Grabbing Used Memory and Free Memory in Bytes
        long freeBytesMemory = Runtime.getRuntime().totalMemory();
        long usedBytesMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        //Convert Bytes to Gb
        long freeMemory = freeBytesMemory / (1024*1024);
        long usedMemory = usedBytesMemory / (1024*1024);

        //Change to next (1024 *1024 *1024) Mb-> Gb;
        //Convert to Percentage for Progress Bars((long)((float) Needed when dividing long by long)
        final long percentage = (long)((float)usedMemory / freeMemory * 100);

        //Display Percent Under Memory Progress Bar
        memoryPercent.setText(percentage+"%");

        // [2]
        //Change Progress Bar
        final ProgressBar progressBarMem;
        progressBarMem = (ProgressBar) v.findViewById(R.id.progress_memory);

        //Round Number to nearest whole number
        final int roundedPercent= (int) Math.rint(percentage);

        //Thread to Set Progress Bar
        new Thread(new Runnable() {
            public void run() {
                //waitMethod();
                progressBarMem.setProgress (roundedPercent);
            }
        }).start();

        // [3]
        //Grabbing Realtime free space and used space
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        long bytesUsed = (stat.getBlockCountLong() - stat.getAvailableBlocksLong()) * stat.getBlockSizeLong();
        //Convert Bytes to GB
        long gbAvailable = bytesAvailable / (1024 * 1024* 1024);
        long gbUsed = bytesUsed / (1024 *1024* 1024);

        // [8]
        //Setup Pie Chart & Display
        AnyChartView anyChartView = v.findViewById(R.id.storageChart);
        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        //Debug
        //data.add(new ValueDataEntry("Used Memory", usedMemory));
        //data.add(new ValueDataEntry("Free Memory", freeMemory));
        data.add(new ValueDataEntry("Used Space", gbUsed));
        data.add(new ValueDataEntry("Free Space", gbAvailable));
        pie.data(data);
        anyChartView.setChart(pie);


        //Call and Set Score
        createScore(gbAvailable,gbUsed,percentage);

        //TOP_APP
        TextView titleHome = v.findViewById(R.id.title_top_app_home);
        TextView titleTime = v.findViewById(R.id.top_app_percentage);
        ImageView titlePic = v.findViewById(R.id.AppHome_Pic);


        //TOP_APP will be blank upon opening
        //if not null display Icon,Title,Time
        PowerFragment App = new PowerFragment();
        if(App.appTitle != null) {
            titleHome.setText(App.appTitle);
            titleTime.setText(App.appTime);
            titlePic.setImageDrawable(App.appIcon);
        }
        //if null set template text
        else{
            titleHome.setText("Go to");
            titleTime.setText("Power Usage");
            titlePic.setImageResource(R.drawable.ic_baseline_warning_24);
        }

        // [4] [7]
        // Receiver to check battery status to display it on home.
        final BroadcastReceiver broadcastReceiverBattery = new BroadcastReceiver() {
            final boolean[] alreadyDisplayed = {false, false, false, false,false,false,false,false};
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            public void onReceive(Context context, Intent intent) {

                TextView batStatus = v.findViewById(R.id.title_battery_home);
                TextView percentageLabel = v.findViewById(R.id.battery_percentage_home);
                ImageView batteryImage = v.findViewById(R.id.batteryImage);

                String action = intent.getAction();

                if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {

                    // Status
                    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                    int health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
                    String output = "";

                    // Depending on the status, it will display a different message.
                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_FULL:
                            output = "Full";

                            //Add to Score
                            if (!alreadyDisplayed[0])
                            {
                                scoreActual = scoreActual +10;
                                score.setText(scoreActual+"");
                            }
                            alreadyDisplayed[0]= true;
                            break;
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            output = "Charging";

                            //Add to Score
                            if (!alreadyDisplayed[1])
                            {
                                scoreActual = scoreActual +10;
                                score.setText(scoreActual+"");
                            }
                            alreadyDisplayed[1]= true;
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                            output = "Discharging";

                            break;
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                            output = "Not charging";

                            break;
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            output = "Unknown";

                            //Subtract from Score
                            if (!alreadyDisplayed[2])
                            {
                                scoreActual = scoreActual -10;
                                score.setText(scoreActual+"");
                            }
                            alreadyDisplayed[2]= true;
                            break;
                    }
                    batStatus.setText(output);

                    switch (health){
                        case BatteryManager.BATTERY_HEALTH_OVERHEAT:

                            //Subtract from Score
                            if (!alreadyDisplayed[3])
                            {
                                scoreActual = scoreActual -10;
                                score.setText(scoreActual+"");
                            }
                            alreadyDisplayed[3]= true;

                        case BatteryManager.BATTERY_HEALTH_COLD:

                            //Subtract from Score
                            if (!alreadyDisplayed[4])
                            {
                                scoreActual = scoreActual -10;
                                score.setText(scoreActual+"");
                            }
                            alreadyDisplayed[4]= true;

                        case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:

                            //Subtract from Score
                            if (!alreadyDisplayed[5])
                            {
                                scoreActual = scoreActual -10;
                                score.setText(scoreActual+"");
                            }
                            alreadyDisplayed[5]= true;
                    }


                    // Percentage
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int batPercentage = level * 100 / scale;
                    percentageLabel.setText(batPercentage+"%");


                    // Set Images Based on Battery Life
                    Resources res = context.getResources();

                    if (batPercentage >= 91) {
                        batteryImage.setImageDrawable(res.getDrawable(R.drawable.battery_100));

                        if (!alreadyDisplayed[6])
                        {
                            scoreActual = scoreActual +15;
                            score.setText(scoreActual+"");
                        }
                        alreadyDisplayed[6]= true;

                    } else if (90 >= batPercentage && batPercentage >= 61) {
                        batteryImage.setImageDrawable(res.getDrawable(R.drawable.battery_80));

                    } else if (60 >= batPercentage && batPercentage >= 41) {
                        batteryImage.setImageDrawable(res.getDrawable(R.drawable.battery_60));

                    } else if (40 >= batPercentage && batPercentage >= 21) {
                        batteryImage.setImageDrawable(res.getDrawable(R.drawable.battery_40));

                    }
                    else if (20 >= batPercentage && batPercentage >= 3) {
                        batteryImage.setImageDrawable(res.getDrawable(R.drawable.battery_20));

                        //Subtract from Score
                        if (!alreadyDisplayed[7])
                        {
                            scoreActual = scoreActual -10;
                            score.setText(scoreActual+"");
                        }
                        alreadyDisplayed[7]= true;
                    }
                    else {
                        batteryImage.setImageDrawable(res.getDrawable(R.drawable.battery_0));
                    }

                }
            }

        };
        getActivity().registerReceiver(broadcastReceiverBattery, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // [17]
        //Changing DarkMode Theme Background for Pie Chart/Saved Pref
        Context con = getActivity();
        SharedPreferences sp = con.getSharedPreferences(UserPreferences.getPreferencesFileName(), Context.MODE_PRIVATE);
        boolean isEnabled = sp.getBoolean("night_mode", false);

        if (isEnabled) {
            pie.background().fill("#303030");
        } else {
            pie.background().fill("#fafafa");
        }
        return v;
    }


    @SuppressLint("SetTextI18n")
    public void createScore(long gbAvailable, long gbUsed, long percentage) {

        //Change Score for HardDrive and Memory

        //HardDrive Space Score
        if (gbAvailable > gbUsed)
        {
            scoreActual = scoreActual + 50;
            score.setText(scoreActual+"");
        } else if (gbAvailable < gbUsed) {
            scoreActual = scoreActual - 20;
            score.setText(scoreActual+"");
        }
        //Memory Score
        if (percentage<60){
            scoreActual = scoreActual +25;
            score.setText(scoreActual+"");
        }
        else if (percentage>60){
            scoreActual = scoreActual -10;
            score.setText(scoreActual+"");
        }

    }

}