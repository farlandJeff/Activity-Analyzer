package com.sru464.activityanalyzer.stats;

/*
    This class contains all the necessary data for the graphs and appropriate methods to alter the
    graph based on this data. Particularly voltage and amps of the battery.
 */

public class GraphData {
    private static int CURRENT_VOLTAGE;
    private static int CURRENT_AMPS;

    private static int[] graphLow = {1000, 2000, 3000};
    private static int[] graphHigh = {3000, 4000, 5000};

    // Setter and getter for Voltage
    public static void setCurrentVoltage(int volt) {
        CURRENT_VOLTAGE = volt;
    }
    public static int getCurrentVoltage() {
        return CURRENT_VOLTAGE;
    }

    // Setter and getter for Amps
    public static void setCurrentAmps(int amp) {
        CURRENT_AMPS = amp;
    }
    public static int getCurrentAmps() { return CURRENT_AMPS; }

    public static int getLow(int type) { return graphLow[type]; }

    public static int getHigh(int type) { return graphHigh[type]; }

}
