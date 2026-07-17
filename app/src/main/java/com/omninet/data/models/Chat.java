package com.omninet.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "chats")
public class Chat implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String chatName;
    public String chatType;
    public String lastMessage;
    public long lastMessageTime;
    public String participants;
    public String groupIcon;
    public int unreadCount;
    public long createdAt;

    public Chat() {
        this.createdAt = System.currentTimeMillis();
        this.unreadCount = 0;
        this.lastMessageTime = System.currentTimeMillis();
    }

    public Chat(String chatName, String chatType) {
        this();
        this.chatName = chatName;
        this.chatType = chatType;
    }

    public String getLastMessagePreview() {
        if (lastMessage == null || lastMessage.isEmpty()) {
            return "No messages yet";
        }
        return lastMessage.length() > 50 ? lastMessage.substring(0, 50) + "..." : lastMessage;
    }

    public String getFormattedTime() {
        long diff = System.currentTimeMillis() - lastMessageTime;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d";
        if (hours > 0) return hours + "h";
        if (minutes > 0) return minutes + "m";
        return "now";
    }
}
