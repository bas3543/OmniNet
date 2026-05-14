package com.omninet.economy;

import android.content.Context;
import android.content.SharedPreferences;
import com.omninet.core.OmniConstitution;
import java.util.concurrent.*;

public class TickEngine {

    private static final String PREFS       = "omni_wallet";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_TOTAL   = "total_earned";
    private static final String KEY_UPTIME  = "uptime_seconds";

    private final Context context;
    private final ScheduledExecutorService scheduler;

    private double balance      = 0;
    private double totalEarned  = 0;
    private long   uptimeSeconds = 0;
    private long   activeNodes  = 1;
    private boolean isGateway   = false;
    private int    battery      = 100;
    private long   bytesRelayed = 0;

    private TickListener listener;

    public interface TickListener {
        void onTick(double balance, double rate, long uptime);
    }

    public TickEngine(Context context) {
        this.context   = context;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        loadSaved();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::tick, 1, 1, TimeUnit.SECONDS);
    }

    private void tick() {
        // Emisyon bitti mi?
        if (System.currentTimeMillis() >= OmniConstitution.EMISSION_END) return;

        // Pil çok düşük — dur
        if (battery < OmniConstitution.MIN_BATTERY) return;

        // Katkı skoru hesapla
        double score = calculateScore();

        // Bu tick için ödül
        double globalTick = OmniConstitution.TICK_EMISSION;
        double nodeShare  = globalTick / Math.max(1, activeNodes);
        double reward     = nodeShare * score;

        balance      += reward;
        totalEarned  += reward;
        uptimeSeconds++;

        // Her 10 saniyede kaydet
        if (uptimeSeconds % 10 == 0) save();

        // UI güncelle
        if (listener != null) {
            double finalReward = reward;
            android.os.Handler h = new android.os.Handler(
                android.os.Looper.getMainLooper());
            h.post(() -> listener.onTick(balance, finalReward, uptimeSeconds));
        }
    }

    private double calculateScore() {
        double score = 0;

        // Veri taşıma %45
        double dataScore = Math.min(1.0, bytesRelayed / (1024.0 * 1024.0));
        score += dataScore * OmniConstitution.WEIGHT_RELAY;

        // Uptime %30
        double uptimeScore = Math.min(1.0, uptimeSeconds / 3600.0);
        score += uptimeScore * OmniConstitution.WEIGHT_UPTIME;

        // Güven %15 — başlangıçta orta
        score += 0.5 * OmniConstitution.WEIGHT_TRUST;

        // Gateway %10
        if (isGateway) score += OmniConstitution.WEIGHT_GATEWAY;

        return Math.max(0.1, Math.min(1.0, score));
    }

    private void save() {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BALANCE, String.valueOf(balance))
            .putString(KEY_TOTAL,   String.valueOf(totalEarned))
            .putLong(KEY_UPTIME,    uptimeSeconds)
            .apply();
    }

    private void loadSaved() {
        SharedPreferences p = context.getSharedPreferences(
            PREFS, Context.MODE_PRIVATE);
        try {
            balance      = Double.parseDouble(p.getString(KEY_BALANCE, "0"));
            totalEarned  = Double.parseDouble(p.getString(KEY_TOTAL,   "0"));
            uptimeSeconds = p.getLong(KEY_UPTIME, 0);
        } catch (Exception e) {
            balance = totalEarned = 0;
        }
    }

    public boolean send(double amount) {
        if (balance < amount) return false;
        balance -= amount;
        save();
        return true;
    }

    public void onPacketRelayed(int bytes)      { bytesRelayed += bytes; }
    public void onGatewayChanged(boolean active){ isGateway = active; }
    public void onNodesChanged(long count)      { activeNodes = Math.max(1, count); }
    public void onBatteryChanged(int pct)       { battery = pct; }
    public void setListener(TickListener l)     { listener = l; }

    public double getBalance()     { return balance; }
    public double getTotalEarned() { return totalEarned; }
    public long   getUptime()      { return uptimeSeconds; }
    public long   getActiveNodes() { return activeNodes; }
    public double getCurrentRate() {
        return (OmniConstitution.TICK_EMISSION /
            Math.max(1, activeNodes)) * calculateScore();
    }

    public void stop() {
        save();
        scheduler.shutdownNow();
    }
}
