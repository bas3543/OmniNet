package com.omninet.network;

import android.content.Context;
import android.content.SharedPreferences;
import com.omninet.core.OmniConstitution;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * +777 3543 XXXX XXXX numara sistemi
 * Her cihaza benzersiz OmniNet numarası atar
 */
public class NumberManager {

    private static final String PREFS      = "omni_number";
    private static final String KEY_NUMBER = "my_number";
    private static final String KEY_HASH   = "founder_hash";

    private static String cachedNumber = null;

    /**
     * Cihaza özgü +777 numarası üret veya mevcut olanı döndür
     */
    public static String getOrCreate(Context ctx) {
        if (cachedNumber != null) return cachedNumber;

        SharedPreferences prefs = ctx.getSharedPreferences(
            PREFS, Context.MODE_PRIVATE);
        String saved = prefs.getString(KEY_NUMBER, null);

        if (saved != null) {
            cachedNumber = saved;
            return saved;
        }

        String number = generate(ctx);
        prefs.edit().putString(KEY_NUMBER, number).apply();
        cachedNumber = number;
        return number;
    }

    /**
     * +777 3543 XXXXXXXX formatında numara üret
     */
    private static String generate(Context ctx) {
        try {
            // Cihaz parmak izi
            String fingerprint = android.os.Build.FINGERPRINT +
                                 android.os.Build.SERIAL +
                                 android.os.Build.BOARD;

            // Rastgele tuz
            byte[] salt = new byte[8];
            new SecureRandom().nextBytes(salt);

            // SHA-256 ile 8 haneli suffix üret
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(fingerprint.getBytes());
            md.update(salt);
            byte[] hash = md.digest();

            // İlk 4 byte'ı sayıya çevir
            long num = 0;
            for (int i = 0; i < 4; i++) {
                num = (num << 8) | (hash[i] & 0xFF);
            }
            // 8 haneli suffix
            String suffix = String.format("%08d", Math.abs(num) % 100000000L);

            String candidate = OmniConstitution.COUNTRY_CODE +
                               OmniConstitution.NETWORK_PREFIX +
                               suffix;

            // Kurucu numaralarıyla çakışma kontrolü
            if (candidate.equals(OmniConstitution.MASTER_ALPHA) ||
                candidate.equals(OmniConstitution.MASTER_BETA)) {
                return generate(ctx); // Yeniden üret
            }

            return candidate;

        } catch (Exception e) {
            // Fallback
            return OmniConstitution.COUNTRY_CODE +
                   OmniConstitution.NETWORK_PREFIX +
                   String.format("%08d", new SecureRandom().nextInt(99999999));
        }
    }

    /**
     * Kurucu doğrulama — TC hash ile
     */
    public static boolean verifyFounder(Context ctx, String tc, String schoolId) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((tc + schoolId + OmniConstitution.FOUNDER_NICK).getBytes());
            byte[] hash = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            String computed = sb.toString();

            // Daha önce kaydedilmiş hash ile karşılaştır
            SharedPreferences prefs = ctx.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
            String saved = prefs.getString(KEY_HASH, null);

            if (saved == null) {
                // İlk kurulum — hash'i kaydet
                prefs.edit().putString(KEY_HASH, computed).apply();
                return true;
            }

            return saved.equals(computed);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Numara formatla: +777 3543 1234 5678
     */
    public static String format(String raw) {
        if (raw == null || raw.length() < 4) return raw;
        // +777 3543 XXXX XXXX
        if (raw.startsWith("+777") && raw.length() >= 16) {
            return raw.substring(0, 4) + " " +
                   raw.substring(4, 8) + " " +
                   raw.substring(8, 12) + " " +
                   raw.substring(12);
        }
        return raw;
    }

    /**
     * Bu numara kurucu numarası mı?
     */
    public static boolean isFounder(String number) {
        return OmniConstitution.MASTER_ALPHA.equals(number) ||
               OmniConstitution.MASTER_BETA.equals(number);
    }

    public static void clear() {
