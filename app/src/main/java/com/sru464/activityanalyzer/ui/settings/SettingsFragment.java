package com.sru464.activityanalyzer.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.sru464.activityanalyzer.R;

public class SettingsFragment extends Fragment {
        private SettingsViewModel settingsViewModel;

        // Buttons
        private Button unitsButton;
        private Button snippetsButton;
        private Button notificationsButton;
        private Button helpButton;
        private Button aboutButton;


        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final NavController navController = NavHostFragment.findNavController(this);

            settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
            View root = inflater.inflate(R.layout.fragment_settings, container, false);

            // Setup Buttons after View is created
            unitsButton = (Button) root.findViewById(R.id.Units);
            //snippetsButton = (Button) root.findViewById(R.id.Snippets);
            notificationsButton = (Button) root.findViewById(R.id.Notifications);
            helpButton = (Button) root.findViewById(R.id.HelpSupport);
            aboutButton = (Button) root.findViewById(R.id.About);

            settingsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    //textView.setText(s);
                }
            });

            // When Units button is clicked, sends you to settings_units
            unitsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.settings_units);
                }
            });

            // When Notifications button is clicked, sends you to settings_notifications
            notificationsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.settings_notifications);
                }
            });

            // When Help button is clicked, sends you to settings_help
            helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.settings_help);
                }
            });

            // When About button is clicked, sends you to settings_about
            aboutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.settings_about);
                }
            });

            return root;
        }
    }
