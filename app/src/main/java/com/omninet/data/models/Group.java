package com.omninet.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "groups")
public class Group implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String groupName;
    public String groupDescription;
    public String groupIcon;
    public String groupAdmin;
    public String members;
    public long createdAt;
    public long updatedAt;
    public boolean isMuted;

    public Group() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isMuted = false;
    }

    public Group(String groupName, String groupAdmin) {
        this();
        this.groupName = groupName;
        this.groupAdmin = groupAdmin;
    }
}
