package com.sru464.activityanalyzer.ui.history;

import android.content.res.ColorStateList;
import android.graphics.Color;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sru464.activityanalyzer.MainActivity;
import com.sru464.activityanalyzer.R;
import com.sru464.activityanalyzer.UserPreferences;
import com.sru464.activityanalyzer.stats.GraphData;
import com.sru464.activityanalyzer.stats.roomdb.VoltageDatabase;
import com.sru464.activityanalyzer.stats.roomdb.VoltageStamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    TextView txthistory;
    private Button lowbutton;
    private Button mediumbutton;
    private Button highbutton;
    private Button weekbutton;
    private Button monthbutton;
    private Button yearbutton;
    private Button gotounits;
    private Button snipHistoryButton;

    // History Graph
    private LineGraphSeries<DataPoint> graphSeries;
    private GraphView historyGraph;
    private VoltageDatabase appDB;


    /*
    // History Graph
    private LineGraphSeries<DataPoint> graphSeries;
    private GraphView historyGraph;
    private VoltageDatabase appDB;

    */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final NavController navController = NavHostFragment.findNavController(this);
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        final TextView textView = v.findViewById(R.id.text_history);

        historyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //Setup for buttons
        txthistory=(TextView) v.findViewById(R.id.UsageText);
        lowbutton= (Button) v.findViewById(R.id.lowbutton);
        mediumbutton= (Button) v.findViewById(R.id.mediumbutton);
        highbutton= (Button) v.findViewById(R.id.highbutton);
        weekbutton= (Button) v.findViewById(R.id.weekButtonHistory);
        monthbutton= (Button) v.findViewById(R.id.monthButtonHistory);
        yearbutton= (Button) v.findViewById(R.id.yearButtonHistory);
        gotounits= (Button) v.findViewById(R.id.gotounits);

        /*
        // History Graph
        appDB = VoltageDatabase.getInstance(getActivity());
        historyGraph = (GraphView) v.findViewById(R.id.graphhistory);
        //graphUpdater(0); // 0 - low, 1 - medium, 2 - high
         */

        // [9]
        // History Graph
        appDB = VoltageDatabase.getInstance(getActivity());
        historyGraph = (GraphView) v.findViewById(R.id.graphhistory);
        graphUpdater(0); // 0 - low, 1 - medium, 2 - high

        //Test Buttons
        lowbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                //graphUpdater(0);

                //Change Color of Low Button keep rest gray
                lowbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#129304")));
                mediumbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                highbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));

                //Update TextView
                txthistory.setText("The graph above has been updated. You are now viewing when your usage was considered low.");
            }
        });
        mediumbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //graphUpdater(1);

                //Change Color of Medium Button keep rest gray
                lowbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                mediumbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6C3E")));
                highbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));


                //Update TextView
                txthistory.setText("The graph above has been updated. You are now viewing when your usage was considered medium.");
            }
        });
        highbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //graphUpdater(2);

                //Change Color of High Button keep rest gray
                lowbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                mediumbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                highbutton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A81E1E")));


                //Update TextView
                txthistory.setText("The graph above has been updated. You are now viewing when your usage was considered high.");

            }
        });
        weekbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        monthbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        yearbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        gotounits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.settings_units);
            }
        });

        snipHistoryButton = (Button) v.findViewById(R.id.snapbuttonhistory);

        snipHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphView graphUsage = (GraphView) getActivity().findViewById(R.id.graphhistory);
                graphUsage.takeSnapshotAndShare(getActivity(),"Usage","History Usage Snapshot");
            }
        });


        return v;
    }

    // [9]
    private void graphUpdater(final int powerType) {
        new Thread(new Runnable() {
            public void run() {
                /*
                for(int i = 0; i < 15; i++) {
                    VoltageStamp volt = new VoltageStamp(rand.nextInt(15) + 1, GraphData.getCurrentVoltage(), GraphData.getCurrentAmps());
                    appDB.voltageDao().insertStamp(volt);
                }
                 */


                //List<VoltageStamp> history = appDB.voltageDao().collectHistory(GraphData.getLow(powerType), GraphData.getHigh(powerType));
                List<VoltageStamp> history = appDB.voltageDao().getAll();
                graphSeries = new LineGraphSeries<>(generateData(history));
                //graphSeries = new LineGraphSeries<>(generateData2());
                //historyGraph.addSeries(graphSeries);

            }
        }).start();
    }


    private DataPoint[] generateData(List<VoltageStamp> history) {
        DataPoint[] values = new DataPoint[history.size()];

        for (int i = 0; i < history.size(); i++) {
            double x = i;
            double y;
            VoltageStamp tempStamp = history.get(i);
            switch(UserPreferences.getUnitType()) {
                case 0:
                    y = tempStamp.getVolt();
                    break;
                case 1:
                    y = tempStamp.getAmp();
                    break;
                case 2:
                    y = tempStamp.getAmp() * tempStamp.getVolt();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + UserPreferences.getUnitType());
            }

            DataPoint v = new DataPoint(x,y);
            values[i] = v;
        }

        return values;
    }

    private DataPoint[] generateData2() {
        int count = 10;
        DataPoint[] values = new DataPoint[count];

        for (int i = 0; i < count; i++) {
            double x = i;
            double y = Math.random();
            DataPoint v = new DataPoint(x,y);
            values[i] = v;
        }

        return values;
    }

}