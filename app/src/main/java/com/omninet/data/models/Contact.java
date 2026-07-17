package com.omninet.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "contacts")
public class Contact implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String phoneNumber;
    public String displayName;
    public String profilePicture;
    public String lastSeen;
    public boolean isOnline;
    public String status;
    public long createdAt;
    public long updatedAt;

    public Contact() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isOnline = false;
    }

    public Contact(String phoneNumber, String displayName) {
        this();
        this.phoneNumber = phoneNumber;
        this.displayName = displayName;
    }
}
