package com.omninet.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "calls")
public class Call implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String callerId;
    public String callerName;
    public String calleeId;
    public String calleeName;
    public String callType;
    public long startTime;
    public long endTime;
    public String status;
    public long duration;

    public Call() {
    }

    public Call(String callerId, String callerName, String calleeId, String calleeName, String callType) {
        this.callerId = callerId;
        this.callerName = callerName;
        this.calleeId = calleeId;
        this.calleeName = calleeName;
        this.callType = callType;
        this.startTime = System.currentTimeMillis();
        this.status = "completed";
    }

    public String getDurationFormatted() {
        if (duration == 0) return "0s";
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        if (hours > 0) return hours + "h " + (minutes % 60) + "m";
        if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
        return seconds + "s";
    }
}
