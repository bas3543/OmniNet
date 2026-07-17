package com.omninet.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "messages",
        foreignKeys = {
                @ForeignKey(entity = Chat.class,
                        parentColumns = "id",
                        childColumns = "chatId",
                        onDelete = ForeignKey.CASCADE)
        })
public class Message implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int chatId;
    public String senderId;
    public String senderName;
    public String messageText;
    public String mediaUrl;
    public String mediaType;
    public long timestamp;
    public boolean isRead;
    public String status;
    public String replyToMessageId;

    public Message() {
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.status = "sent";
    }

    public Message(int chatId, String senderId, String senderName, String messageText) {
        this();
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.messageText = messageText;
        this.mediaType = "text";
    }

    public String getTimeFormatted() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }
}
