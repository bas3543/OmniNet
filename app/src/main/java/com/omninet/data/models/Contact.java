package com.omninet.data.models;

import androidx.room.*;

@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey
    @NonNull
    public String omniNumber;      // +777 3543 XXXX XXXX

    public String displayName;     // Görünen ad
    public String omniId;          // Mesh node ID
    public byte[] publicKey;       // Şifreleme anahtarı
    public long   firstSeen;       // İlk görülme zamanı
    public long   lastSeen;        // Son görülme zamanı
    public int    hopDistance;     // Mesh mesafesi
    public boolean isOnline;       // Şu an çevrimiçi mi
    public boolean isFounder;      // Kurucu mu
    public String avatarColor;     // Profil rengi (#238636 gibi)
    public String initials;        // Baş harfler (AY gibi)
    public String status;          // Durum mesajı

    public Contact() {}

    public Contact(@NonNull String omniNumber, String displayName) {
        this.omniNumber  = omniNumber;
        this.displayName = displayName;
        this.firstSeen   = System.currentTimeMillis();
        this.lastSeen    = System.currentTimeMillis();
        this.initials    = makeInitials(displayName);
        this.avatarColor = pickColor(omniNumber);
        this.isFounder   = com.omninet.network.NumberManager.isFounder(omniNumber);
    }

    private String makeInitials(String name) {
        if (name == null || name.isEmpty()) return "??";
        String[] parts = name.trim().split(" ");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) +
                    parts[1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private String pickColor(String number) {
        String[] colors = {
            "#238636", "#1F6FEB", "#D29922",
            "#F0997B", "#AFA9EC", "#2EA098"
        };
        int idx = Math.abs(number.hashCode()) % colors.length;
        return colors[idx];
    }
}
