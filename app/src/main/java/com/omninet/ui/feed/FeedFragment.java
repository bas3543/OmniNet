package com.omninet.ui.feed;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class FeedFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {

        ScrollView scroll = new ScrollView(getContext());
        scroll.setBackgroundColor(0xFF0D1117);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 0, 0, 80);
        scroll.addView(layout);

        // Başlık
        TextView header = new TextView(getContext());
        header.setText("Mesh Feed");
        header.setTextColor(0xFFE6EDF3);
        header.setTextSize(18);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(42, 30, 42, 20);
        layout.addView(header);

        // Post 1
        layout.addView(buildPost("Kerem", "KE", 0xFF238636,
            "Mesh ağı üzerinden bağlandım! İnternet olmadan haberleşiyoruz 🔒",
            "⬡ 1 atlama · 5 dk önce", 24, 8));

        // Post 2
        layout.addView(buildPost("Merve", "ME", 0xFF1F6FEB,
            "OmniNet ile ilk sesli görüşmemi yaptım. Kalite harika! 🎙️",
            "⬡ 2 atlama · 14 dk önce", 31, 12));

        // Post 3
        layout.addView(buildPost("Uğur", "UG", 0xFFD29922,
            "Gateway moduna geçtim, ağa internet sağlıyorum 🌐 Herkese açık!",
            "⬡ 3 atlama · 1 saat önce", 18, 5));

        // Post 4
        layout.addView(buildPost("Selin", "SE", 0xFFF0997B,
            "Dosya transferi tamamlandı ✅ 50MB mesh üzerinden 8 saniyede!",
            "⬡ 2 atlama · 2 saat önce", 42, 17));

        // Post 5
        layout.addView(buildPost("Bahar", "BA", 0xFFAFA9EC,
            "OmniCoin mining başladı ⛏️ Şu an +0.000031 OC/sn kazanıyorum!",
            "⬡ 1 atlama · 3 saat önce", 56, 23));

        return scroll;
    }

    private View buildPost(String name, String initials, int color,
                            String text, String meta, int likes, int comments) {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(0xFF161B22);

        // Kenar boşluğu
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(28, 0, 28, 20);
        card.setLayoutParams(cardParams);

        // Yuvarlak köşe için arka plan
        android.graphics.drawable.GradientDrawable bg =
            new android.graphics.drawable.GradientDrawable();
        bg.setColor(0xFF161B22);
        bg.setCornerRadius(28f);
        bg.setStroke(1, 0xFF21262D);
        card.setBackground(bg);
        card.setPadding(32, 28, 32, 20);

        // Üst satır — avatar + isim
        LinearLayout top = new LinearLayout(getContext());
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Avatar
        TextView av = new TextView(getContext());
        av.setText(initials);
        av.setTextColor(0xFFFFFFFF);
        av.setTextSize(14);
        av.setTypeface(null, android.graphics.Typeface.BOLD);
        av.setGravity(android.view.Gravity.CENTER);
        android.graphics.drawable.GradientDrawable avBg =
            new android.graphics.drawable.GradientDrawable();
        avBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        avBg.setColor(color);
        av.setBackground(avBg);
        LinearLayout.LayoutParams avParams = new LinearLayout.LayoutParams(80, 80);
        av.setLayoutParams(avParams);

        // İsim + meta
        LinearLayout nameBlock = new LinearLayout(getContext());
        nameBlock.setOrientation(LinearLayout.VERTICAL);
        nameBlock.setPadding(24, 0, 0, 0);

        TextView tvName = new TextView(getContext());
        tvName.setText(name);
        tvName.setTextColor(0xFFE6EDF3);
        tvName.setTextSize(13);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvMeta = new TextView(getContext());
        tvMeta.setText(meta);
        tvMeta.setTextColor(0xFF6E7681);
        tvMeta.setTextSize(11);

        nameBlock.addView(tvName);
        nameBlock.addView(tvMeta);

        top.addView(av);
        top.addView(nameBlock);
        card.addView(top);

        // İçerik
        TextView tvText = new TextView(getContext());
        tvText.setText(text);
        tvText.setTextColor(0xFFC9D1D9);
        tvText.setTextSize(13);
        tvText.setPadding(0, 20, 0, 20);
        tvText.setLineSpacing(4, 1);
        card.addView(tvText);

        // Ayraç
        View divider = new View(getContext());
        divider.setBackgroundColor(0xFF21262D);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 1));
        card.addView(divider);

        // Alt satır — beğeni + yorum
        LinearLayout actions = new LinearLayout(getContext());
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setPadding(0, 16, 0, 0);
        actions.setGravity(android.view.Gravity.CENTER_VERTICAL);

        final TextView tvLike = new TextView(getContext());
        tvLike.setText("♥  " + likes);
        tvLike.setTextColor(0xFF6E7681);
        tvLike.setTextSize(13);
        tvLike.setPadding(0, 0, 40, 0);
        tvLike.setClickable(true);
        final int[] likeCount = {likes};
        final boolean[] liked = {false};
        tvLike.setOnClickListener(v -> {
            if (!liked[0]) {
                likeCount[0]++;
                tvLike.setTextColor(0xFFF85149);
            } else {
                likeCount[0]--;
                tvLike.setTextColor(0xFF6E7681);
            }
            liked[0] = !liked[0];
            tvLike.setText("♥  " + likeCount[0]);
        });

        TextView tvComment = new TextView(getContext());
        tvComment.setText("💬  " + comments + " yorum");
        tvComment.setTextColor(0xFF6E7681);
        tvComment.setTextSize(13);
        tvComment.setPadding(0, 0, 40, 0);

        TextView tvShare = new TextView(getContext());
        tvShare.setText("↗  Yay");
        tvShare.setTextColor(0xFF6E7681);
        tvShare.setTextSize(13);

        actions.addView(tvLike);
        actions.addView(tvComment);
        actions.addView(tvShare);
        card.addView(actions);

        return card;
    }
}
