package com.sru464.activityanalyzer.stats.roomdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

// [5]
@Entity(tableName = "volts")
public class VoltageStamp {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "hour")
    private int hour; // 0 - 24 (easier to save, calculate before displaying on a 12-hr clock

    @ColumnInfo(name = "volt")
    private int volt;

    @ColumnInfo(name = "amp")
    private int amp;

    public VoltageStamp(int id, int hour, int volt, int amp) {
        this.id = id;
        this.hour = hour;
        this.volt = volt;
        this.amp = amp;
    }

    @Ignore
    public VoltageStamp(int hour, int volt, int amp) {
        this.hour = hour;
        this.volt = volt;
        this.amp = amp;
    }

    public int getId() { return id; }

    public int getHour() { return hour; }

    public int getVolt() {
        return volt;
    }

    public int getAmp() {
        return amp;
    }
}
