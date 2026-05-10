package com.omninet.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.omninet.services.OmniBackgroundService;
import com.omninet.services.EconomyService;

public class OmniApp extends Application {

    private static OmniApp instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        OmniID.getOrGenerate(this);

        startOmniServices();
    }

    private void startOmniServices() {
        Intent mesh = new Intent(this, OmniBackgroundService.class);
        Intent econ = new Intent(this, EconomyService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(mesh);
        } else {
            startService(mesh);
        }
        startService(econ);
    }

    public static OmniApp getInstance() {
        return instance;
    }
}
