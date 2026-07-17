package com.omninet.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "statuses")
public class Status implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String userId;
    public String userName;
    public String userAvatar;
    public String mediaUrl;
    public String statusText;
    public long createdAt;
    public long expiresAt;
    public boolean isViewed;
    public String viewedBy;

    public Status() {
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = createdAt + (24 * 60 * 60 * 1000);
        this.isViewed = false;
    }

    public Status(String userId, String userName, String mediaUrl) {
        this();
        this.userId = userId;
        this.userName = userName;
        this.mediaUrl = mediaUrl;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
