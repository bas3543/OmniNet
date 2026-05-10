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
        android.graphics.drawable.GradientDrawable bg =
            new android.graphics.drawable.GradientDrawable();
        bg.setColor(0xFF161B22);
        bg.setCornerRadius(24f);
        bg.setStroke(1, 0xFF238636);
        progressCard.setBackground(bg);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 16);
        progressCard.setLayoutParams(p);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText("📤  dosya.pdf → " + receiver);
        tvTitle.setTextColor(0xFFE6EDF3);
        tvTitle.setTextSize(13);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        progressCard.addView(tvTitle);

        TextView tvStatus = new TextView(getContext());
        tvStatus.setText("⬡ Mesh üzerinden gönderiliyor...");
        tvStatus.setTextColor(0xFF6E7681);
        tvStatus.setTextSize(11);
        tvStatus.setPadding(0, 8, 0, 12);
        progressCard.addView(tvStatus);

        ProgressBar progressBar = new ProgressBar(getContext(),
            null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 16));
        progressCard.addView(progressBar);

        TextView tvPercent = new TextView(getContext());
        tvPercent.setText("0%");
        tvPercent.setTextColor(0xFF2EA043);
        tvPercent.setTextSize(12);
        tvPercent.setPadding(0, 8, 0, 0);
        progressCard.addView(tvPercent);

        layout.addView(progressCard, 1);

        // Simüle et
        final int[] progress = {0};
        handler.post(new Runnable() {
            @Override public void run() {
                if (!isAdded()) return;
                progress[0] += 5;
                progressBar.setProgress(progress[0]);
                tvPercent.setText(progress[0] + "%");
                if (progress[0] < 100) {
                    handler.postDelayed(this, 150);
                } else {
                    tvStatus.setText("✓ Teslim edildi · AES-256 şifreli");
                    tvStatus.setTextColor(0xFF2EA043);
                    tvPercent.setText("Tamamlandı!");
                }
            }
        });
    }

    private View buildFileItem(String name, String size, String to,
                                String status, int statusColor) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, 16, 0, 16);

        TextView tvIcon = new TextView(getContext());
        String emoji = name.endsWith(".pdf") ? "📄" :
                       name.endsWith(".jpg") ? "🖼️" :
                       name.endsWith(".mp3") ? "🎵" : "📁";
        tvIcon.setText(emoji);
        tvIcon.setTextSize(24);
        tvIcon.setPadding(0, 0, 20, 0);
        row.addView(tvIcon);

        LinearLayout info = new LinearLayout(getContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(getContext());
        tvName.setText(name);
        tvName.setTextColor(0xFFE6EDF3);
        tvName.setTextSize(13);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDetail = new TextView(getContext());
        tvDetail.setText(size + " · " + to);
        tvDetail.setTextColor(0xFF6E7681);
        tvDetail.setTextSize(11);
        tvDetail.setPadding(0, 4, 0, 0);

        TextView tvStat = new TextView(getContext());
        tvStat.setText(status);
        tvStat.setTextColor(statusColor);
        tvStat.setTextSize(11);
        tvStat.setPadding(0, 2, 0, 0);

        info.addView(tvName);
        info.addView(tvDetail);
        info.addView(tvStat);
        row.addView(info);

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
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
