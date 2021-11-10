package com.sru464.activityanalyzer.stats.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// [5]
@Database(entities = VoltageStamp.class, exportSchema = false, version = 1)
public abstract class VoltageDatabase extends RoomDatabase {
    private static final String DB_LABEL = "voltage_db";
    private static VoltageDatabase instance;

    public static synchronized VoltageDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), VoltageDatabase.class, DB_LABEL)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract VoltageDao voltageDao();
}
