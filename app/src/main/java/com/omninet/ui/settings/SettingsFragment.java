package com.omninet.ui.settings;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.omninet.core.OmniID;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFF0D1117);
        layout.setPadding(24, 24, 24, 24);

        String nodeId   = OmniID.getOrGenerate(requireContext());
        String nickname = OmniID.getNickname(requireContext());

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText("Ayarlar");
        tvTitle.setTextColor(0xFFE6EDF3);
        tvTitle.setTextSize(20);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setPadding(0, 0, 0, 24);

        TextView tvId = new TextView(getContext());
        tvId.setText("OmniNet ID: " + nodeId);
        tvId.setTextColor(0xFF6E7681);
        tvId.setTextSize(12);
        tvId.setPadding(0, 0, 0, 8);

        TextView tvNick = new TextView(getContext());
        tvNick.setText("Takma Ad: " + nickname);
        tvNick.setTextColor(0xFF2EA043);
        tvNick.setTextSize(14);
        tvNick.setPadding(0, 0, 0, 16);

        TextView tvEncrypt = new TextView(getContext());
        tvEncrypt.setText("🔒 AES-256-GCM Şifreleme: Aktif");
        tvEncrypt.setTextColor(0xFF2EA043);
        tvEncrypt.setTextSize(13);
        tvEncrypt.setPadding(0, 0, 0, 8);

        TextView tvMesh = new TextView(getContext());
        tvMesh.setText("⬡ Mesh Ağı: BLE + Wi-Fi Direct");
        tvMesh.setTextColor(0xFF58A6FF);
        tvMesh.setTextSize(13);
        tvMesh.setPadding(0, 0, 0, 8);

        TextView tvMining = new TextView(getContext());
        tvMining.setText("⛏ Mining: Aktif");
        tvMining.setTextColor(0xFF2EA043);
        tvMining.setTextSize(13);

        layout.addView(tvTitle);
        layout.addView(tvId);
        layout.addView(tvNick);
        layout.addView(tvEncrypt);
        layout.addView(tvMesh);
        layout.addView(tvMining);

        return layout;
    }
}
