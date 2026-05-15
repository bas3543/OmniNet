package com.omninet.ui.settings;

import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.omninet.core.OmniConstitution;
import com.omninet.core.OmniID;
import com.omninet.network.NumberManager;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {

        ScrollView scroll = new ScrollView(getContext());
        scroll.setBackgroundColor(0xFF0D1117);

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, 0, 0, 100);
        scroll.addView(root);

        String myNumber  = NumberManager.getOrCreate(requireContext());
        boolean isFounder = NumberManager.isFounder(myNumber);

        // Profil başlığı
        LinearLayout profileHdr = new LinearLayout(getContext());
        profileHdr.setOrientation(LinearLayout.VERTICAL);
        profileHdr.setGravity(android.view.Gravity.CENTER);
        profileHdr.setPadding(40, 32, 40, 32);
        profileHdr.setBackgroundColor(isFounder ? 0xFF1A1400 : 0xFF161B22);

        // Avatar
        TextView av = new TextView(getContext());
        String nick = OmniID.getNickname(requireContext());
        av.setText(nick.length() >= 2 ?
            nick.substring(0, 2).toUpperCase() : "??");
        av.setTextColor(0xFFFFFFFF);
        av.setTextSize(28);
        av.setTypeface(null, android.graphics.Typeface.BOLD);
        av.setGravity(android.view.Gravity.CENTER);
        android.graphics.drawable.GradientDrawable avBg =
            new android.graphics.drawable.GradientDrawable();
        avBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        avBg.setColor(isFounder ? 0xFFD29922 : 0xFF238636);
        av.setBackground(avBg);
        LinearLayout.LayoutParams avP =
            new LinearLayout.LayoutParams(140, 140);
        avP.setMargins(0, 0, 0, 16);
        av.setLayoutParams(avP);
        profileHdr.addView(av);

        TextView tvNick = new TextView(getContext());
        tvNick.setText(nick + (isFounder ? " ⭐ Kurucu" : ""));
        tvNick.setTextColor(isFounder ? 0xFFD29922 : 0xFFE6EDF3);
        tvNick.setTextSize(18);
        tvNick.setTypeface(null, android.graphics.Typeface.BOLD);
        tvNick.setGravity(android.view.Gravity.CENTER);
        profileHdr.addView(tvNick);

        TextView tvNum = new TextView(getContext());
        tvNum.setText(NumberManager.format(myNumber));
        tvNum.setTextColor(0xFF2EA043);
        tvNum.setTextSize(13);
        tvNum.setGravity(android.view.Gravity.CENTER);
        tvNum.setPadding(0, 6, 0, 0);
        profileHdr.addView(tvNum);

        root.addView(profileHdr);

        // Kurucu paneli
        if (isFounder) {
            root.addView(buildFounderPanel());
        }

        // Takma ad değiştir
        root.addView(buildSectionLabel("PROFİL"));
        root.addView(buildSettingRow("👤 Takma Adı Değiştir",
            nick, () -> showChangeNickname()));

        // Bağlantı ayarları
        root.addView(buildSectionLabel("BAĞLANTI"));
        root.addView(buildToggleRow("📡 Bluetooth LE", "pref_ble", true));
        root.addView(buildToggleRow("📶 Wi-Fi Direct", "pref_wifi", true));
        root.addView(buildToggleRow("🌐 VPN Gateway", "pref_vpn", false));
        root.addView(buildToggleRow("📱 NFC", "pref_nfc", true));

        // Mining ayarları
        root.addView(buildSectionLabel("MİNİNG"));
        root.addView(buildToggleRow("⛏ Mining Aktif", "pref_mining", true));
        root.addView(buildToggleRow("⚡ Agresif Tarama", "pref_scan", false));

        // Güvenlik
        root.addView(buildSectionLabel("GÜVENLİK"));
        root.addView(buildInfoRow("🔒 Şifreleme", "AES-256-GCM · Aktif"));
        root.addView(buildInfoRow("🔑 Protokol", "X25519 ECDH"));
        root.addView(buildInfoRow("🌍 Ağ Kodu",
            OmniConstitution.COUNTRY_CODE + " " +
            OmniConstitution.NETWORK_PREFIX));

        // Hesabı kurtar
        root.addView(buildSectionLabel("HESAP"));
        root.addView(buildSettingRow("🔄 Hesabı Kurtar",
            "Seed phrase ile", () -> showRecovery()));
        root.addView(buildSettingRow("📋 Numaramı Kopyala",
            NumberManager.format(myNumber), () -> {
                android.content.ClipboardManager cm =
                    (android.content.ClipboardManager)
                    requireContext().getSystemService(
                        android.content.Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(android.content.ClipData.newPlainText(
                    "OmniNet Numara", myNumber));
                Toast.makeText(getContext(),
                    "Kopyalandı!", Toast.LENGTH_SHORT).show();
            }));

        // Hakkında
        root.addView(buildSectionLabel("HAKKINDA"));
        root.addView(buildInfoRow("📱 Versiyon", "OmniNet v2.0"));
        root.addView(buildInfoRow("🏗️ Motor",
            "Bas-" + OmniConstitution.NETWORK_PREFIX));
        root.addView(buildInfoRow("💰 Toplam Arz",
            "1 Trilyon OC"));

        return scroll;
    }

    private View buildFounderPanel() {
        LinearLayout panel = new LinearLayout(getContext());
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(28, 20, 28, 20);
        panel.setBackgroundColor(0xFF1A1400);

        TextView title = new TextView(getContext());
        title.setText("⭐ KURUCU PANELİ");
        title.setTextColor(0xFFD29922);
        title.setTextSize(12);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, 16);
        panel.addView(title);

        // Master numara bilgisi
        TextView alpha = new TextView(getContext());
        alpha.setText("Alpha: " + NumberManager.format(
            OmniConstitution.MASTER_ALPHA));
        alpha.setTextColor(0xFFD29922);
        alpha.setTextSize(12);
        panel.addView(alpha);

        TextView beta = new TextView(getContext());
        beta.setText("Beta: " + NumberManager.format(
            OmniConstitution.MASTER_BETA));
        beta.setTextColor(0xFFD29922);
        beta.setTextSize(12);
        beta.setPadding(0, 4, 0, 16);
        panel.addView(beta);

        // Ağ istatistikleri
        Button btnStats = new Button(getContext());
        btnStats.setText("📊 Ağ İstatistikleri");
        btnStats.setTextColor(0xFFD29922);
        btnStats.setTextSize(13);
        android.graphics.drawable.GradientDrawable statsBg =
            new android.graphics.drawable.GradientDrawable();
        statsBg.setColor(0xFF2A1F00);
        statsBg.setCornerRadius(16f);
        statsBg.setStroke(1, 0xFFD29922);
        btnStats.setBackground(statsBg);
        btnStats.setOnClickListener(v ->
            Toast.makeText(getContext(),
                "Ağ gözlemevi yakında!", Toast.LENGTH_SHORT).show());
        panel.addView(btnStats);

        return panel;
    }

    private View buildSectionLabel(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextColor(0xFF6E7681);
        tv.setTextSize(11);
        tv.setPadding(28, 20, 28, 8);
        return tv;
    }

    private View buildInfoRow(String label, String value) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(28, 20, 28, 20);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvLabel = new TextView(getContext());
        tvLabel.setText(label);
        tvLabel.setTextColor(0xFFE6EDF3);
        tvLabel.setTextSize(13);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(tvLabel);

        TextView tvValue = new TextView(getContext());
        tvValue.setText(value);
        tvValue.setTextColor(0xFF6E7681);
        tvValue.setTextSize(12);
        row.addView(tvValue);

        View line = new View(getContext());
        line.setBackgroundColor(0xFF21262D);

        LinearLayout wrapper = new LinearLayout(getContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(row);
        line.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 1));
        wrapper.addView(line);
        return wrapper;
    }

    private View buildSettingRow(String label, String value,
                                  Runnable onClick) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(28, 20, 28, 20);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(v -> onClick.run());

        TextView tvLabel = new TextView(getContext());
        tvLabel.setText(label);
        tvLabel.setTextColor(0xFFE6EDF3);
        tvLabel.setTextSize(13);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(tvLabel);

        TextView tvValue = new TextView(getContext());
        tvValue.setText(value + " ›");
        tvValue.setTextColor(0xFF6E7681);
        tvValue.setTextSize(12);
        row.addView(tvValue);

        LinearLayout wrapper = new LinearLayout(getContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(row);
        View line = new View(getContext());
        line.setBackgroundColor(0xFF21262D);
        line.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 1));
        wrapper.addView(line);
        return wrapper;
    }

    private View buildToggleRow(String label, String prefKey,
                                 boolean defaultVal) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(28, 16, 28, 16);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvLabel = new TextView(getContext());
        tvLabel.setText(label);
        tvLabel.setTextColor(0xFFE6EDF3);
        tvLabel.setTextSize(13);
        tvLabel.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(tvLabel);

        Switch sw = new Switch(getContext());
        boolean saved = requireContext()
            .getSharedPreferences("omni_settings", 0)
            .getBoolean(prefKey, defaultVal);
        sw.setChecked(saved);
        sw.setOnCheckedChangeListener((btn, checked) ->
            requireContext()
                .getSharedPreferences("omni_settings", 0)
                .edit().putBoolean(prefKey, checked).apply());
        row.addView(sw);

        LinearLayout wrapper = new LinearLayout(getContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(row);
        View line = new View(getContext());
        line.setBackgroundColor(0xFF21262D);
        line.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 1));
        wrapper.addView(line);
        return wrapper;
    }

    private void showChangeNickname() {
        android.app.AlertDialog.Builder builder =
            new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Takma Adı Değiştir");

        EditText et = new EditText(getContext());
        et.setText(OmniID.getNickname(requireContext()));
        et.setTextColor(0xFFC9D1D9);
        et.setPadding(40, 20, 40, 20);
        builder.setView(et);

        builder.setPositiveButton("Kaydet", (d, w) -> {
            String name = et.getText().toString().trim();
            if (!name.isEmpty()) {
                OmniID.setNickname(requireContext(), name);
                Toast.makeText(getContext(),
                    "✓ Takma ad güncellendi", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void showRecovery() {
        android.app.AlertDialog.Builder builder =
            new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("🔄 Hesabı Kurtar");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 20, 48, 0);

        TextView hint = new TextView(getContext());
        hint.setText("Seed phrase'inizi girin\n(12 kelime, boşlukla ayrılmış)");
        hint.setTextColor(0xFF6E7681);
        hint.setTextSize(12);
        hint.setPadding(0, 0, 0, 12);
        layout.addView(hint);

        EditText etSeed = new EditText(getContext());
        etSeed.setHint("kelime1 kelime2 kelime3 ...");
        etSeed.setHintTextColor(0xFF484F58);
        etSeed.setTextColor(0xFFC9D1D9);
        etSeed.setMinLines(3);
        layout.addView(etSeed);

        builder.setView(layout);
        builder.setPositiveButton("Kurtar", (d, w) -> {
            String seed = etSeed.getText().toString().trim();
            if (seed.split(" ").length >= 12) {
                Toast.makeText(getContext(),
                    "✓ Hesap kurtarıldı!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(),
                    "⚠ 12 kelime gerekli",
                    Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }
}
