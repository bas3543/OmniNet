package com.omninet.data.models;

import androidx.room.*;

@Entity(tableName = "messages")
public class Message {

    @PrimaryKey
    @NonNull
    public String msgId;

    public String threadId;      // Karşı tarafın omniNumber'ı
    public String fromNumber;    // Gönderenin +777 numarası
    public String toNumber;      // Alıcının +777 numarası
    public String clearText;     // Mesaj içeriği
    public byte[] encrypted;     // Şifreli ham veri
    public String mediaPath;     // Dosya yolu (varsa)
    public String mediaType;     // "image","audio","video","file"
    public long   timestamp;     // Gönderilme zamanı
    public boolean sent;         // Gönderildi mi
    public boolean delivered;    // Teslim edildi mi
    public boolean read;         // Okundu mu
    public int     hopCount;     // Kaç cihazdan geçti
    public String  type;         // "text","voice","image","file"

    public static final String TYPE_TEXT  = "text";
    public static final String TYPE_VOICE = "voice";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_FILE  = "file";

    public Message() {}

    public static Message createText(String from, String to, String text) {
        Message m = new Message();
        m.msgId      = java.util.UUID.randomUUID().toString();
        m.threadId   = to;
        m.fromNumber = from;
        m.toNumber   = to;
        m.clearText  = text;
        m.timestamp  = System.currentTimeMillis();
        m.type       = TYPE_TEXT;
        m.sent       = false;
        m.delivered  = false;
        m.read       = false;
        m.hopCount   = 0;
        return m;
    }

    public boolean isOutgoing(String myNumber) {
        return fromNumber != null && fromNumber.equals(myNumber);
    }

    public String getTimeString() {
        java.text.SimpleDateFormat sdf =
            new java.text.SimpleDateFormat("HH:mm",
                java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }

    public String getStatusIcon() {
        if (!sent)      return "🕐";
        if (!delivered) return "✓";
        if (!read)      return "✓✓";
        return "✓✓";
    }
}
