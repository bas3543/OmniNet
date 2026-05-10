package com.omninet.ui.radar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class RadarFragment extends Fragment {

    private Handler handler = new Handler(Looper.getMainLooper());
    private RadarView radarView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {

        ScrollView scroll = new ScrollView(getContext());
        scroll.setBackgroundColor(0xFF0D1117);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(28, 20, 28, 80);
        scroll.addView(layout);

        // Başlık
        TextView header = new TextView(getContext());
        header.setText("Mesh Radar");
        header.setTextColor(0xFFE6EDF3);
        header.setTextSize(18);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(0, 0, 0, 16);
        layout.addView(header);

        // İstatistik kartları
        LinearLayout stats = new LinearLayout(getContext());
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams statsParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stats.setLayoutParams(statsParams);
        stats.setPadding(0, 0, 0, 16);

        stats.addView(buildStatCard("Düğüm", "7", 0xFF2EA043));
        stats.addView(buildStatCard("Gateway", "Aktif", 0xFF58A6FF));
        stats.addView(buildStatCard("Gecikme", "~95ms", 0xFFE6EDF3));

        layout.addView(stats);

        // Radar görünümü
        radarView = new RadarView(getContext());
        LinearLayout.LayoutParams radarParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 600);
        radarView.setLayoutParams(radarParams);
        layout.addView(radarView);

        // Legend
        layout.addView(buildLegend());

        // Düğüm listesi başlığı
        TextView listHeader = new TextView(getContext());
        listHeader.setText("BAĞLI DÜĞÜMLER");
        listHeader.setTextColor(0xFF6E7681);
        listHeader.setTextSize(11);
        listHeader.setPadding(0, 24, 0, 12);
        layout.addView(listHeader);

        // Düğümler
        layout.addView(buildNode("Kerem", "BLE 5.0 · -62 dBm · Direkt", 0xFF2EA043, "Mesaj", 1));
        layout.addView(buildNode("Ali K. 🌐 Gateway", "WiFi Direct · İnternet paylaşıyor", 0xFFF85149, "VPN", 1));
        layout.addView(buildNode("Merve", "WiFi Direct · 2 atlama", 0xFF58A6FF, "Mesaj", 2));
        layout.addView(buildNode("Uğur", "BLE · 2 atlama · Kerem üzerinden", 0xFF58A6FF, "Mesaj", 2));
        layout.addView(buildNode("Bilinmeyen-4F2A", "3 atlama · Anahtar değişimi bekleniyor", 0xFFD29922, "Ekle", 3));

        return scroll;
    }

    private View buildStatCard(String label, String value, int valueColor) {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(20, 20, 20, 20);

        android.graphics.drawable.GradientDrawable bg =
            new android.graphics.drawable.GradientDrawable();
        bg.setColor(0xFF161B22);
        bg.setCornerRadius(20f);
        bg.setStroke(1, 0xFF21262D);
        card.setBackground(bg);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,
            ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        p.setMargins(0, 0, 12, 0);
        card.setLayoutParams(p);

        TextView tvVal = new TextView(getContext());
        tvVal.setText(value);
        tvVal.setTextColor(valueColor);
        tvVal.setTextSize(20);
        tvVal.setTypeface(null, android.graphics.Typeface.BOLD);
        tvVal.setGravity(android.view.Gravity.CENTER);

        TextView tvLbl = new TextView(getContext());
        tvLbl.setText(label);
        tvLbl.setTextColor(0xFF6E7681);
        tvLbl.setTextSize(11);
        tvLbl.setGravity(android.view.Gravity.CENTER);

        card.addView(tvLbl);
        card.addView(tvVal);
        return card;
    }

    private View buildLegend() {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 12, 0, 0);
        row.addView(buildLegendItem("Direkt", 0xFF2EA043));
        row.addView(buildLegendItem("2 atlama", 0xFF58A6FF));
        row.addView(buildLegendItem("3+ atlama", 0xFFD29922));
        row.addView(buildLegendItem("Gateway", 0xFFF85149));
        return row;
    }

    private View buildLegendItem(String label, int color) {
        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setPadding(0, 0, 24, 0);

        View dot = new View(getContext());
        android.graphics.drawable.GradientDrawable dotBg =
            new android.graphics.drawable.GradientDrawable();
        dotBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        dotBg.setColor(color);
        dot.setBackground(dotBg);
        dot.setLayoutParams(new LinearLayout.LayoutParams(16, 16));

        TextView tv = new TextView(getContext());
        tv.setText("  " + label);
        tv.setTextColor(0xFF6E7681);
        tv.setTextSize(11);

        item.addView(dot);
        item.addView(tv);
        return item;
    }

    private View buildNode(String name, String detail, int dotColor,
                            String btnText, int hops) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, 20, 0, 20);

        android.graphics.drawable.GradientDrawable divBg =
            new android.graphics.drawable.GradientDrawable();
        divBg.setColor(0x00000000);
        divBg.setStroke(0, 0xFF21262D);

        // Renk noktası
        View dot = new View(getContext());
        android.graphics.drawable.GradientDrawable dotBg =
            new android.graphics.drawable.GradientDrawable();
        dotBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        dotBg.setColor(dotColor);
        dot.setBackground(dotBg);
        LinearLayout.LayoutParams dotP = new LinearLayout.LayoutParams(20, 20);
        dotP.setMargins(0, 0, 20, 0);
        dot.setLayoutParams(dotP);

        // Bilgi
        LinearLayout info = new LinearLayout(getContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0,
            ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(getContext());
        tvName.setText(name);
        tvName.setTextColor(0xFFE6EDF3);
        tvName.setTextSize(13);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDetail = new TextView(getContext());
        tvDetail.setText(detail);
        tvDetail.setTextColor(0xFF6E7681);
        tvDetail.setTextSize(11);
        tvDetail.setPadding(0, 4, 0, 0);

        info.addView(tvName);
        info.addView(tvDetail);

        // Buton
        Button btn = new Button(getContext());
        btn.setText(btnText);
        btn.setTextColor(0xFF2EA043);
        btn.setTextSize(11);
        android.graphics.drawable.GradientDrawable btnBg =
            new android.graphics.drawable.GradientDrawable();
        btnBg.setColor(0xFF0F3D1F);
        btnBg.setCornerRadius(16f);
        btnBg.setStroke(1, 0xFF238636);
        btn.setBackground(btnBg);
        btn.setPadding(20, 8, 20, 8);
        LinearLayout.LayoutParams btnP = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        btn.setLayoutParams(btnP);
        btn.setOnClickListener(v ->
            android.widget.Toast.makeText(getContext(),
                name + " ile bağlantı kuruluyor...",
                android.widget.Toast.LENGTH_SHORT).show());

        row.addView(dot);
        row.addView(info);
        row.addView(btn);

        // Alt çizgi
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
