package com.omninet.services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import com.omninet.economy.TickEngine;

public class EconomyService extends Service {

    private TickEngine tickEngine;
    private final IBinder binder = new EconomyBinder();

    public class EconomyBinder extends Binder {
        public EconomyService getService() {
            return EconomyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tickEngine = new TickEngine(this);
        tickEngine.start();

        // Pil izle
        registerReceiver(new android.content.BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context ctx,
                                   Intent intent) {
                int level = intent.getIntExtra(
                    BatteryManager.EXTRA_LEVEL, 100);
                int scale = intent.getIntExtra(
                    BatteryManager.EXTRA_SCALE, 100);
                int pct = (int)(level * 100f / scale);
                tickEngine.onBatteryChanged(pct);
            }
        }, new android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public double getBalance()     { return tickEngine.getBalance(); }
    public double getMiningRate()  { return tickEngine.getCurrentRate(); }
    public double getTotalEarned() { return tickEngine.getTotalEarned(); }
    public long   getActiveNodes() { return tickEngine.getActiveNodes(); }
    public long   getUptime()      { return tickEngine.getUptime(); }

    public boolean send(double amount) {
        return tickEngine.send(amount);
    }

    public void onPacketRelayed(int bytes) {
        tickEngine.onPacketRelayed(bytes);
    }

    public void onGatewayActive(boolean active) {
        tickEngine.onGatewayChanged(active);
    }

    public void onActiveNodesChanged(long count) {
        tickEngine.onNodesChanged(count);
    }

    public void setTickListener(TickEngine.TickListener l) {
        tickEngine.setListener(l);
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        tickEngine.stop();
        super.onDestroy();
    }
}
