package com.sru464.activityanalyzer;

public class UserPreferences {
    /*
        UserPreferences class is used to save and make changes to the app based on the options that
        the user picks in the settings. It pulls the data from the Shared Preferences of the user
        and uses that data to change specific aspects of the app, like the Thread Level or
        Notification Level.
    */

    // StoreManager Storage_Manager = new StoreManager();

    // Variables
    private static int UNIT_TIME_INTERVAL; // 5s, 10s, 30s, 60s, 300s
    private static int NOTIFICATION_LEVEL; // none, critical, all
    private static int UNIT_TYPE; // 0 = volts, 1 = amps, 2 = watts
    // Potential killing other apps
    // App not closed notifications


    // preferences file name
    private static final String PREFERENCES_FILE_NAME = "ACTIVITY_ANALYZER_PREFERENCES";

    // Get Functions
    public static int getThreadInterval() { return (UNIT_TIME_INTERVAL * 1000); }
    public static int getNotificationLevel() { return NOTIFICATION_LEVEL; }
    public static String getPreferencesFileName() { return PREFERENCES_FILE_NAME; }
    public static int getUnitType() { return UNIT_TYPE; }

    // Set Functions
    public static void setThreadInterval(int newValue) { UNIT_TIME_INTERVAL = newValue; }
    public static void setNotificationLevel(int newValue) { NOTIFICATION_LEVEL = newValue; }
    public static void setUnitType(int newValue) {UNIT_TYPE = newValue; }


}
