package com.omninet.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.omninet.services.OmniBackgroundService;
import com.omninet.services.EconomyService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        Intent mesh = new Intent(context, OmniBackgroundService.class);
        Intent econ = new Intent(context, EconomyService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(mesh);
        } else {
            context.startService(mesh);
        }
        context.startService(econ);
    }
}
