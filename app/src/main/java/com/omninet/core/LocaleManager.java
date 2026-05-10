package com.omninet.core;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import java.util.Locale;

public class LocaleManager {

    public static Context setLocale(Context ctx) {
        Locale locale = getSavedLocale(ctx);
        return updateResources(ctx, locale);
    }

    private static Locale getSavedLocale(Context ctx) {
        String saved = ctx.getSharedPreferences("omni_prefs", Context.MODE_PRIVATE)
                          .getString("language", null);
        if (saved != null && !saved.isEmpty()) return new Locale(saved);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return ctx.getResources().getConfiguration().getLocales().get(0);
        } else {
            return ctx.getResources().getConfiguration().locale;
        }
    }

    private static Context updateResources(Context ctx, Locale locale) {
        Locale.setDefault(locale);
        Configuration config = new Configuration(ctx.getResources().getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            return ctx.createConfigurationContext(config);
        } else {
            config.locale = locale;
            ctx.getResources().updateConfiguration(config,
                ctx.getResources().getDisplayMetrics());
            return ctx;
        }
    }

    public static void setLanguage(Context ctx, String langCode) {
        ctx.getSharedPreferences("omni_prefs", Context.MODE_PRIVATE)
           .edit().putString("language", langCode).apply();
    }
}
