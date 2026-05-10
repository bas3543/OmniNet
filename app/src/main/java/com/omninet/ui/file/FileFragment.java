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

        // Başlık
        TextView header = new TextView(getContext());
        header.setText("Dosya Gönder");
        header.setTextColor(0xFFE6EDF3);
        header.setTextSize(18);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(0, 0, 0, 20);
        layout.addView(header);

        // Dosya seç kartı
        LinearLayout selectCard = new LinearLayout(getContext());
        selectCard.setOrientation(LinearLayout.VERTICAL);
        selectCard.setGravity(android.view.Gravity.CENTER);
        selectCard.setPadding(40, 60, 40, 60);
        android.graphics.drawable.GradientDrawable cardBg =
            new android.graphics.drawable.GradientDrawable();
        cardBg.setColor(0xFF161B22);
        cardBg.setCornerRadius(24f);
        cardBg.setStroke(2, 0xFF30363D);
        selectCard.setBackground(cardBg);

        TextView tvIcon = new TextView(getContext());
        tvIcon.setText("📁");
        tvIcon.setTextSize(48);
        tvIcon.setGravity(android.view.Gravity.CENTER);
        selectCard.addView(tvIcon);

        TextView tvHint = new TextView(getContext());
        tvHint.setText("Dosya seçmek için tıkla");
        tvHint.setTextColor(0xFF6E7681);
        tvHint.setTextSize(14);
        tvHint.setGravity(android.view.Gravity.CENTER);
        tvHint.setPadding(0, 16, 0, 8);
        selectCard.addView(tvHint);

        TextView tvSub = new TextView(getContext());
        tvSub.setText("Resim, video, ses, belge");
        tvSub.setTextColor(0xFF484F58);
        tvSub.setTextSize(12);
        tvSub.setGravity(android.view.Gravity.CENTER);
        selectCard.addView(tvSub);

        Button btnSelect = new Button(getContext());
        btnSelect.setText("Dosya Seç");
        btnSelect.setTextColor(0xFFFFFFFF);
        btnSelect.setTextSize(14);
        android.graphics.drawable.GradientDrawable btnBg =
            new android.graphics.drawable.GradientDrawable();
        btnBg.setColor(0xFF238636);
        btnBg.setCornerRadius(20f);
        btnSelect.setBackground(btnBg);
        LinearLayout.LayoutParams btnP =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnP.setMargins(0, 24, 0, 0);
        btnSelect.setLayoutParams(btnP);
        btnSelect.setPadding(60, 20, 60, 20);

        // Demo: dosya seçilmiş gibi göster
        btnSelect.setOnClickListener(v -> showFileSelected(layout));
        selectCard.addView(btnSelect);

        LinearLayout.LayoutParams cardParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 24);
        selectCard.setLayoutParams(cardParams);
        layout.addView(selectCard);

        // Son gönderilen dosyalar
        TextView recentHeader = new TextView(getContext());
        recentHeader.setText("SON GÖNDERİLENLER");
        recentHeader.setTextColor(0xFF6E7681);
        recentHeader.setTextSize(11);
        recentHeader.setPadding(0, 0, 0, 12);
        layout.addView(recentHeader);

        layout.addView(buildFileItem("rapor_2024.pdf", "2.4 MB", "Uğur'a", "✓✓ Teslim edildi", 0xFF2EA043));
        layout.addView(buildFileItem("foto_mesh.jpg", "1.8 MB", "Merve'ye", "✓✓ Teslim edildi", 0xFF2EA043));
        layout.addView(buildFileItem("ses_kaydı.mp3", "4.2 MB", "Kerem'e", "⬡ İletiliyor...", 0xFFD29922));

        return scroll;
    }

    private void showFileSelected(LinearLayout layout) {
        // Alıcı seç dialog
        String[] contacts = {"Kerem", "Merve", "Uğur", "Selin", "Bahar"};
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Alıcı Seç")
            .setItems(contacts, (dialog, which) -> {
                String receiver = contacts[which];
                showTransferProgress(layout, receiver);
            })
            .show();
    }

    private void showTransferProgress(LinearLayout layout, String receiver) {
        LinearLayout progressCard = new LinearLayout(getContext());
        progressCard.setOrientation(LinearLayout.VERTICAL);
        progressCard.setPadding(32, 28, 32, 28);
        }
