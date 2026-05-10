package com.omninet.ui.file;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class FileFragment extends Fragment {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle saved) {

        ScrollView scroll = new ScrollView(getContext());
        scroll.setBackgroundColor(0xFF0D1117);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(28, 20, 28, 80);
        scroll.addView(layout);

        TextView header = new TextView(getContext());
        header.setText("Dosya Gönder");
        header.setTextColor(0xFFE6EDF3);
        header.setTextSize(18);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(0, 0, 0, 20);
        layout.addView(header);

        LinearLayout selectCard = new LinearLayout(getContext());
        selectCard.setOrientation(LinearLayout.VERTICAL);
        selectCard.setGravity(android.view.Gravity.CENTER);
        selectCard.setPadding(40, 60, 40, 60);
        android.graphics.drawable.GradientDrawable cardBg = new android.graphics.drawable.GradientDrawable();
        cardBg.setColor(0xFF161B22);
        cardBg.setCornerRadius(24f);
        cardBg.setStroke(2, 0xFF30363D);
        selectCard.setBackground(cardBg);

        TextView tvIcon = new TextView(getContext());
        tvIcon.setText("📁");
        tvIcon.setTextSize(48);
        tvIcon.setGravity(android.view.Gravity.CENTER);
        selectCard.addView(tvIcon);

        Button btnSelect = new Button(getContext());
        btnSelect.setText("Dosya Seç");
        btnSelect.setOnClickListener(v -> showFileSelected(layout));
        selectCard.addView(btnSelect);

        layout.addView(selectCard);

        // Son gönderilenler
        layout.addView(buildFileItem("rapor_2024.pdf", "2.4 MB", "Uğur'a", "✓✓ Teslim edildi", 0xFF2EA043));
        
        return scroll;
    }

    private void showFileSelected(LinearLayout layout) {
        String[] contacts = {"Kerem", "Merve", "Uğur"};
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Alıcı Seç")
            .setItems(contacts, (dialog, which) -> {
                showTransferProgress(layout, contacts[which]);
            })
            .show();
    }

    private void showTransferProgress(LinearLayout layout, String receiver) {
        // İlerleme çubuğu kodları buraya gelebilir
    }

    private View buildFileItem(String name, String size, String to, String status, int color) {
        TextView tv = new TextView(getContext());
        tv.setText(name + " - " + status);
        tv.setTextColor(color);
        return tv;
    }
}
