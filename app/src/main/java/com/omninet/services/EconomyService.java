package com.omninet.services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import java.util.concurrent.*;

public class EconomyService extends Service {

    private double balance      = 0.0;
    private double miningRate   = 0.000031;
    private double totalEarned  = 0.0;
    private long   activeNodes  = 1;
    private boolean isGateway   = false;
    private int    battery      = 100;

    private final IBinder binder = new EconomyBinder();
    private ScheduledExecutorService scheduler;

    public class EconomyBinder extends Binder {
        public EconomyService getService() {
            return EconomyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::tick, 1, 1, TimeUnit.SECONDS);
    }

    private void tick() {
        if (battery < 15) return;
        double reward = miningRate * calculateScore();
        balance      += reward;
        totalEarned  += reward;
    }

    private double calculateScore() {
        double score = 0.5; // Temel puan
        if (isGateway) score += 0.3;
        if (activeNodes > 5) score += 0.2;
        return Math.min(1.0, score);
    }

    public double getBalance()      { return balance; }
    public double getMiningRate()   { return miningRate * calculateScore(); }
    public double getTotalEarned()  { return totalEarned; }
    public long   getActiveNodes()  { return activeNodes; }

    public boolean send(String targetId, double amount) {
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }

    public void onActiveNodesChanged(long count) {
        activeNodes = count;
        miningRate  = 3170.979198 / Math.max(1, count);
    }

    public void onGatewayActive(boolean active) {
        isGateway = active;
    }

    public void onBatteryChanged(int pct) {
        battery = pct;
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (scheduler != null) scheduler.shutdownNow();
        super.onDestroy();
    }
}
