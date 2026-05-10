package com.omninet.services;

import android.content.Intent;
import android.net.VpnService;
import android.os.*;

public class OmniVpnService extends VpnService {

    private ParcelFileDescriptor vpnInterface;
    private volatile boolean running = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        String mode = intent.getStringExtra("mode");

        if ("GATEWAY".equals(mode)) {
            startGatewayMode();
        } else {
            startClientMode(intent.getStringExtra("gateway"));
        }

        return START_STICKY;
    }

    private void startClientMode(String gateway) {
        try {
            Builder builder = new Builder();
            builder.setSession("OmniNet VPN");
            builder.addAddress("10.88.0.1", 24);
            builder.addRoute("0.0.0.0", 0);
            builder.addDnsServer("1.1.1.1");
            builder.setMtu(1400);
            builder.addDisallowedApplication(getPackageName());
            vpnInterface = builder.establish();
            running = true;
        } catch (Exception e) {
            android.util.Log.e("VPN", "Client mode failed", e);
        }
    }

    private void startGatewayMode() {
        try {
            Builder builder = new Builder();
            builder.setSession("OmniNet Gateway");
            builder.addAddress("10.88.0.254", 16);
            builder.addRoute("10.88.0.0", 16);
            builder.setMtu(1400);
            builder.addDisallowedApplication(getPackageName());
            vpnInterface = builder.establish();
            running = true;
        } catch (Exception e) {
            android.util.Log.e("VPN", "Gateway mode failed", e);
        }
    }

    @Override
    public void onDestroy() {
        running = false;
        if (vpnInterface != null) {
            try { vpnInterface.close(); } catch (Exception ignored) {}
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
