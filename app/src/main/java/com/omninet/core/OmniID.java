package com.omninet.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.UUID;

public class OmniID {

    private static final String PREFS    = "omni_identity";
    private static final String KEY_ID   = "omni_id";
    private static final String KEY_NAME = "nickname";

    private static String cachedId = null;

    public static String getOrGenerate(Context ctx) {
        if (cachedId != null) return cachedId;

        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String saved = prefs.getString(KEY_ID, null);

        if (saved != null) {
            cachedId = saved;
            return saved;
        }

        String raw = generateId(ctx);
        cachedId = raw;
        prefs.edit().putString(KEY_ID, raw).apply();
        return raw;
    }

    private static String generateId(Context ctx) {
        try {
            String fingerprint = Build.FINGERPRINT + Build.SERIAL + Build.BOARD;
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(fingerprint.getBytes());
            digest.update(salt);
            byte[] hash = digest.digest();
            return "omni_" + base58Encode(hash).substring(0, 12);
        } catch (Exception e) {
            return "omni_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }
    }

    private static final String BASE58 =
        "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    private static String base58Encode(byte[] input) {
        StringBuilder sb = new StringBuilder();
        java.math.BigInteger num = new java.math.BigInteger(1, input);
        java.math.BigInteger base = java.math.BigInteger.valueOf(58);
        while (num.compareTo(java.math.BigInteger.ZERO) > 0) {
            java.math.BigInteger[] dr = num.divideAndRemainder(base);
            sb.insert(0, BASE58.charAt(dr[1].intValue()));
            num = dr[0];
        }
        return sb.toString();
    }

    public static String getNickname(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                  .getString(KEY_NAME, "Anonim");
    }

    public static void setNickname(Context ctx, String name) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
           .edit().putString(KEY_NAME, name).apply();
    }

    public static String get() {
        return cachedId != null ? cachedId : "omni_unknown";
    }
}
