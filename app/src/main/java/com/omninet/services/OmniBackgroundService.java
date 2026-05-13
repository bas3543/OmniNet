package com.omninet.services;

import android.app.*;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Message;
import com.omninet.network.NumberManager;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Executors;

public class OmniBackgroundService extends Service {

    private static final String TAG           = "OmniMesh";
    private static final String CHANNEL_ID    = "OmniNetService";
    private static final int    NOTIF_ID      = 1;
    private static final int    MANUFACTURER_ID = 0x4F4E;

    public static final String ACTION_SEND_MESSAGE = "com.omninet.SEND_MESSAGE";

    private BluetoothAdapter      btAdapter;
    private BluetoothLeScanner    bleScanner;
    private BluetoothLeAdvertiser bleAdvertiser;

    private String  myNumber;
    private int     connectedNodes = 0;
    private boolean hasInternet    = false;
    private boolean bleAvailable   = false;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIF_ID, buildNotification("Başlatılıyor..."));

        myNumber = NumberManager.getOrCreate(this);

        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bm != null) {
            btAdapter = bm.getAdapter();
            if (btAdapter != null && btAdapter.isEnabled()) {
                bleAvailable  = true;
                bleScanner    = btAdapter.getBluetoothLeScanner();
                bleAdvertiser = btAdapter.getBluetoothLeAdvertiser();
                startBLEScan();
                startPresenceAdvertise();
            }
        }

        handler.post(statusLoop);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_SEND_MESSAGE.equals(intent.getAction())) {
            String target  = intent.getStringExtra("target");
            byte[] payload = intent.getByteArrayExtra("payload");
            String msgId   = intent.getStringExtra("msg_id");
            if (target != null && payload != null) {
                sendViaBLE(target, payload, msgId);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(statusLoop);
        stopBLEScan();
        stopPresenceAdvertise();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    // BLE Scan
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord record = result.getScanRecord();
            if (record == null) return;
            byte[] mfData = record.getManufacturerSpecificData(MANUFACTURER_ID);
            if (mfData == null || mfData.length == 0) return;
            parseIncoming(mfData);
        }
    };

    private void startBLEScan() {
        if (bleScanner == null) return;
        try {
            ScanFilter filter = new ScanFilter.Builder()
                .setManufacturerData(MANUFACTURER_ID, new byte[0], new byte[0])
                .build();
            ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
            bleScanner.startScan(Collections.singletonList(filter), settings, scanCallback);
            Log.d(TAG, "BLE scan başladı");
        } catch (SecurityException e) {
            Log.e(TAG, "BLE scan izin hatası", e);
        }
    }

    private void stopBLEScan() {
        if (bleScanner == null) return;
        try { bleScanner.stopScan(scanCallback); } catch (Exception ignored) {}
    }

    // BLE Advertise (varlık bildirimi)
    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override public void onStartSuccess(AdvertiseSettings s) { Log.d(TAG, "Advertise OK"); }
        @Override public void onStartFailure(int e) { Log.e(TAG, "Advertise hata: " + e); }
    };

    private void startPresenceAdvertise() {
        if (bleAdvertiser == null) return;
        try {
            JSONObject presence = new JSONObject();
            presence.put("t", "PRESENCE");
            presence.put("from", myNumber);
            byte[] data = safeBytes(presence.toString());

            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false).setTimeout(0).build();

            AdvertiseData advData = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addManufacturerData(MANUFACTURER_ID, data).build();

            bleAdvertiser.startAdvertising(settings, advData, advertiseCallback);
        } catch (SecurityException e) {
            Log.e(TAG, "Advertise izin hatası", e);
        } catch (Exception e) {
            Log.e(TAG, "Advertise hatası", e);
        }
    }

    private void stopPresenceAdvertise() {
        if (bleAdvertiser == null) return;
        try { bleAdvertiser.stopAdvertising(advertiseCallback); } catch (Exception ignored) {}
    }

    // Mesaj gönder
    private void sendViaBLE(String target, byte[] payloadBytes, String msgId) {
        if (!bleAvailable || bleAdvertiser == null) return;
        try {
            JSONObject pkt = new JSONObject();
            pkt.put("t", "MSG");
            pkt.put("from", myNumber);
            pkt.put("to", target);
            pkt.put("id", msgId != null ? msgId : UUID.randomUUID().toString());
            pkt.put("body", new String(payloadBytes, StandardCharsets.UTF_8));

            byte[] raw = safeBytes(pkt.toString());

            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false).setTimeout(3000).build();

            AdvertiseData advData = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addManufacturerData(MANUFACTURER_ID, raw).build();

            bleAdvertiser.startAdvertising(settings, advData, new AdvertiseCallback() {
                @Override public void onStartSuccess(AdvertiseSettings s) {
                    Log.d(TAG, "MSG gönderildi → " + target);
                    connectedNodes = Math.max(connectedNodes, 1);
                    broadcastStatus();
                }
                @Override public void onStartFailure(int err) {
                    Log.e(TAG, "MSG advertise hata: " + err);
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "BLE izin hatası", e);
        } catch (Exception e) {
            Log.e(TAG, "Gönderme hatası", e);
        }
    }

    // Gelen paketi işle
    private void parseIncoming(byte[] raw) {
        try {
            JSONObject pkt = new JSONObject(new String(raw, StandardCharsets.UTF_8));
            String type = pkt.optString("t", "");
            String from = pkt.optString("from", "");

            if ("PRESENCE".equals(type) && !from.isEmpty() && !from.equals(myNumber)) {
                connectedNodes = Math.max(connectedNodes, 1);
                broadcastStatus();
                Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        OmniDatabase.get(this).contactDao()
                            .updateOnlineStatus(from, true, System.currentTimeMillis());
                    } catch (Exception ignored) {}
                });

            } else if ("MSG".equals(type)) {
                String to   = pkt.optString("to", "");
                String id   = pkt.optString("id", "");
                String body = pkt.optString("body", "");

                if (!myNumber.equals(to) || body.isEmpty()) return;

                Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        if (!id.isEmpty() && OmniDatabase.get(this).messageDao().getById(id) != null)
                            return;

                        Message msg = new Message();
                        msg.msgId      = id.isEmpty() ? UUID.randomUUID().toString() : id;
                        msg.threadId   = from;
                        msg.fromNumber = from;
                        msg.toNumber   = myNumber;
                        msg.clearText  = body;
                        msg.timestamp  = System.currentTimeMillis();
                        msg.type       = Message.TYPE_TEXT;
                        msg.sent       = true;
                        msg.delivered  = true;
                        msg.read       = false;
                        msg.hopCount   = 1;

                        OmniDatabase.get(this).messageDao().insert(msg);

                        handler.post(() -> {
                            Intent broadcast = new Intent("com.omninet.MESSAGE");
                            broadcast.putExtra("from", from);
                            sendBroadcast(broadcast);
                        });

                        showMessageNotification(from, body);
                    } catch (Exception e) {
                        Log.e(TAG, "Mesaj kayıt hatası", e);
                    }
                });
            }
        } catch (Exception ignored) {}
    }

    private final Runnable statusLoop = new Runnable() {
        @Override public void run() {
            broadcastStatus();
            handler.postDelayed(this, 10_000);
        }
    };

    private void broadcastStatus() {
        Intent i = new Intent("com.omninet.MESH_STATUS");
        i.putExtra("node_count", connectedNodes);
        i.putExtra("has_internet", hasInternet);
        sendBroadcast(i);
        updateNotification();
    }

    private void updateNotification() {
        String status = bleAvailable
            ? "⬡ " + connectedNodes + " düğüm" + (hasInternet ? " · 🌐" : "")
            : "⬡ Bluetooth kapalı";
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
            .notify(NOTIF_ID, buildNotification(status));
    }

    private void showMessageNotification(String from, String body) {
        String name = from;
        try {
            com.omninet.data.models.Contact c =
                OmniDatabase.get(this).contactDao().getByNumber(from);
            if (c != null) name = c.displayName;
        } catch (Exception ignored) {}

        final String displayName = name;
        handler.post(() -> {
            Notification n = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(displayName)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true).build();
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify((int) System.currentTimeMillis(), n);
        });
    }

    private byte[] safeBytes(String json) {
        byte[] b = json.getBytes(StandardCharsets.UTF_8);
        if (b.length <= 26) return b;
        byte[] out = new byte[26];
        System.arraycopy(b, 0, out, 0, 26);
        return out;
    }

    private Notification buildNotification(String status) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OmniNet")
            .setContentText(status)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true).build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, "OmniNet Mesh", NotificationManager.IMPORTANCE_MIN);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }
}
