package com.sru464.activityanalyzer.stats.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// [5]
@Dao
public interface VoltageDao {
    @Query("SELECT * FROM volts")
    List<VoltageStamp> getAll();

    @Query("SELECT * FROM volts WHERE volt BETWEEN :low AND :high")
    List<VoltageStamp> collectHistory(int low, int high);

    @Insert
    void insertStamp(VoltageStamp volt);

    @Delete
    void delete(VoltageStamp voltageStamp); // UserPreference option (1w, 1m, 3m, 5m, 1y, dnd [Do not delete])
}
