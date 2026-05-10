package com.omninet.core;

import android.app.Application;
import android.content.Context;

public class OmniApp extends Application {

    private static OmniApp instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        OmniID.getOrGenerate(this);
    }

    public static OmniApp getInstance() {
        return instance;
    }
}
