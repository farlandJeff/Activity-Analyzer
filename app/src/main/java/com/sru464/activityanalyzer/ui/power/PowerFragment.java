package com.sru464.activityanalyzer.ui.power;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sru464.activityanalyzer.R;
import com.sru464.activityanalyzer.UserPreferences;
import com.sru464.activityanalyzer.stats.GraphData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PowerFragment extends Fragment {

    private PowerViewModel powerViewModel;

    public static String appTitle , appTime;
    public static Drawable appIcon;
    private final Handler graphHandler = new Handler();
    private Runnable graphTimer;
    private LineGraphSeries<DataPoint> graphSeries;
    private Button snipUsageButton;

    private List<Double> powerData = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final NavController navController = NavHostFragment.findNavController(this);
        powerViewModel = new ViewModelProvider(this).get(PowerViewModel.class);
        View v = inflater.inflate(R.layout.fragment_power, container, false);
        final TextView textView = v.findViewById(R.id.text_power);

        powerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // [9]
        // Creating the graph in the Power tab and customizing it's aesthetics
        final GraphView graph = (GraphView) v.findViewById(R.id.graphusage);
        graphSeries = new LineGraphSeries<>(generateData());
        graph.addSeries(graphSeries);
        graph.getViewport().setScalableY(true);
        graph.getGridLabelRenderer().setTextSize(20);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setTextSize(30);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(6000);
        graphSeries.setTitle("Battery");
        graphSeries.setColor(Color.rgb(0,102,0));
        graphSeries.setThickness(10);
        graphSeries.setDrawBackground(true);
        graphSeries.setBackgroundColor(Color.argb(60,0,153,0));
        graphSeries.setDrawDataPoints(true);

        // Declaring the TextViews for the application names
        TextView title1 = v.findViewById(R.id.App1_Name);
        TextView title2 = v.findViewById(R.id.App2_Name);
        TextView title3 = v.findViewById(R.id.App3_Name);
        TextView title4 = v.findViewById(R.id.App4_Name);
        TextView title5 = v.findViewById(R.id.App5_Name);
        TextView[] textboxes = {title1, title2, title3, title4, title5};

        // Declaring the ImageViews for the application icons
        ImageView i1 = v.findViewById(R.id.App1_Pic);
        ImageView i2 = v.findViewById(R.id.App2_Pic);
        ImageView i3 = v.findViewById(R.id.App3_Pic);
        ImageView i4 = v.findViewById(R.id.App4_Pic);
        ImageView i5 = v.findViewById(R.id.App5_Pic);
        ImageView[] imageboxes = {i1, i2, i3, i4, i5};

        // Declaring the TextViews for the time each application has been used
        TextView time1 = v.findViewById(R.id.battery_percentage_app1);
        TextView time2 = v.findViewById(R.id.battery_percentage_app2);
        TextView time3 = v.findViewById(R.id.battery_percentage_app3);
        TextView time4 = v.findViewById(R.id.battery_percentage_app4);
        TextView time5 = v.findViewById(R.id.battery_percentage_app5);
        TextView[] timeboxes = {time1, time2, time3, time4, time5};

        // [23]
        // Initializing the Usage Stats of the user
        UsageStatsManager usm = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);

        // [6]
        // Creating the beginning time of the Calendar
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.DATE, 1);
        beginCal.set(Calendar.MONTH, 9);
        beginCal.set(Calendar.YEAR, 2020);

        // Creating the ending time of the calendar (a month)
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.DATE, 1);
        endCal.set(Calendar.MONTH, 10);
        endCal.set(Calendar.YEAR, 2020);

        // Creating a vector of UsageStats based on the initial Usage Stats, that checks the usage
        // every day of the month and intervals between months. This will give us all the apps that
        // have been used, whether in the background or by the user.
        // The currApp below is to assist in the logic of obtaining the most used apps.
        // [24]
        List<UsageStats> apps = usm.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        UsageStats currApp;

        // Creating a PackageManager and an ApplicationInfo to get the data we need from the applications
        // that the user uses.
        // [14]
        final PackageManager pManager = getActivity().getPackageManager();
        ApplicationInfo app;

        // Below contains the logic to list the most used apps of the user.
        if (apps.size() > 0) {
            UsageStats[] stats = new UsageStats[5];

            for (int i = 0; i < apps.size(); i++) {
                currApp = apps.get(i);

                // [14]
                // Only works if it has been used outside of the background
                if (currApp.getTotalTimeVisible() > 0) {
                    // To obtain the actual name of the application from the package
                    try {
                        app = pManager.getApplicationInfo(currApp.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        app = null;
                    }

                    if (app != null) { // If the app has a name ...
                        // Checks previous apps to see if current app has more time.
                        for (int j = 0; j < stats.length; j++) {
                            if (stats[j] != null && stats[j].getTotalTimeVisible() < currApp.getTotalTimeVisible()) {
                                UsageStats holder = stats[j];

                                for (int k = j; k < stats.length - 1; k++) {
                                    UsageStats temp = stats[k + 1];
                                    stats[k + 1] = holder;
                                    holder = temp;
                                }

                                stats[j] = currApp;
                            } else if (stats[j] == null) {
                                stats[j] = currApp;
                            }

                            j = stats.length;
                        }

                        // [13]
                        // Replaces the icons
                        for (int j = 0; j < stats.length; j++) {
                            Drawable currIcon = null;
                            CharSequence currLabel = null;

                            if (stats[j] != null) {
                                try {
                                    ApplicationInfo anApp = pManager.getApplicationInfo(stats[j].getPackageName(), 0);
                                    currIcon = pManager.getApplicationIcon(anApp);
                                    currLabel = pManager.getApplicationLabel(anApp);
                                } catch (PackageManager.NameNotFoundException e) {}
                            }

                            // Replaces the time
                            if (currIcon != null || currLabel != null) {
                                imageboxes[j].setImageDrawable(currIcon);
                                textboxes[j].setText(currLabel);

                                long totalTime = stats[j].getTotalTimeVisible() + stats[j].getTotalTimeInForeground();

                                // Milliseconds
                                // < 60,000 = seconds only
                                // 60,000 > && < 3,600,000 = minutes only
                                // > 3,600,000 = hour & minutes

                                if (totalTime < 60000) {
                                    // seconds only
                                    timeboxes[j].setText("<1min");
                                }
                                else if (totalTime >= 60000 && totalTime < 3600000) {
                                    // minutes only
                                    timeboxes[j].setText(totalTime / 1000 + "min");
                                }
                                else {
                                    // hours & minutes
                                    timeboxes[j].setText(TimeUnit.MILLISECONDS.toHours(totalTime) + " hr " + (TimeUnit.MILLISECONDS.toMinutes(totalTime) % 60) + " min ");
                                }

                            }

                        }
                    }
                }
            }
        }

        snipUsageButton = (Button) v.findViewById(R.id.snapbuttonusage);

        snipUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph.takeSnapshotAndShare(getActivity(),"Usage","Power Usage Snapshot");
            }
        });

        //Calling SetTopApp
        setTopApp(textboxes[0].getText().toString(), timeboxes[0].getText().toString(), imageboxes[0].getDrawable());


        return v;
    }

    public void setTopApp(String appTitleHome, String appTimeHome, Drawable appIconHome) {
        appTitle= appTitleHome;
        appTime = appTimeHome;
        appIcon = appIconHome;
    }

    @Override
    public void onResume() {
        super.onResume();
        // [9]
        graphTimer = new Runnable() {
            @Override
            public void run() {
                graphSeries.resetData(generateData());
                graphHandler.postDelayed(this, UserPreferences.getThreadInterval());
            }
        };
        graphHandler.postDelayed(graphTimer, UserPreferences.getThreadInterval());
    }

    @Override
    public void onPause() {
        // [9]
        graphHandler.removeCallbacks(graphTimer);
        super.onPause();
    }

    private DataPoint[] generateData() {
        int count = powerData.size();
        DataPoint[] values = new DataPoint[count];

        if (powerData.size() >= 45) {
            powerData.remove(0);
        }

        //double num = rand.nextDouble() * 0.3; // Use with emulator
        double num;
        switch(UserPreferences.getUnitType()) {
            case 0:
                num = GraphData.getCurrentVoltage();
                break;
            case 1:
                num = GraphData.getCurrentAmps();
                break;
            case 2:
                num = GraphData.getCurrentAmps() * GraphData.getCurrentVoltage();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + UserPreferences.getUnitType());
        }

        powerData.add(num);

        for (int i = 0; i < count; i++) {
            double x = i;
            double y = powerData.get(i);
            DataPoint v = new DataPoint(x,y);
            values[i] = v;
        }

        return values;
    }


    /* // Original from Graphing Library
    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i = 0; i < count; i++) {
            double x = i;
            double f = rand.nextDouble() * 0.15 + 0.3;
            double y = Math.sin(i * f + 2) + rand.nextDouble() * 0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
     */
}