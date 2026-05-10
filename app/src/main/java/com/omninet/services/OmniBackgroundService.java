package com.omninet.services;

import android.app.*;
import android.content.Intent;
import android.os.*;
import androidx.core.app.NotificationCompat;

public class OmniBackgroundService extends Service {

    private static final String CHANNEL_ID = "OmniNetService";
    private static final int    NOTIF_ID   = 1;

    public static final String ACTION_SEND_MESSAGE = "com.omninet.SEND_MESSAGE";

    private int connectedNodes = 0;
    private boolean hasInternet = false;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIF_ID, buildNotification("Mesh ağı başlatılıyor..."));
        startMeshDiscovery();
    }

    private void startMeshDiscovery() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    // Mesh keşif döngüsü
                    broadcastMeshStatus();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "MeshDiscovery").start();
    }

    private void broadcastMeshStatus() {
        Intent i = new Intent("com.omninet.MESH_STATUS");
        i.putExtra("node_count", connectedNodes);
        i.putExtra("has_internet", hasInternet);
        sendBroadcast(i);
        updateNotification();
    }

    private void updateNotification() {
        String status = "⬡ " + connectedNodes + " düğüm" +
            (hasInternet ? " · 🌐 İnternet" : " · Mesh only");
        NotificationManager nm =
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIF_ID, buildNotification(status));
    }

    private Notification buildNotification(String status) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OmniNet")
            .setContentText(status)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, "OmniNet", NotificationManager.IMPORTANCE_MIN);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
