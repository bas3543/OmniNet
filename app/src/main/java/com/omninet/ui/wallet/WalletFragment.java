package com.omninet.ui.wallet;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.omninet.economy.TickEngine;
import com.omninet.network.NumberManager;
import com.omninet.services.EconomyService;

public class WalletFragment extends Fragment {

    private EconomyService economyService;
    private boolean bound = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    private TextView tvBalance;
    private TextView tvRate;
    private TextView tvUptime;
    private TextView tvTotal;
    private TextView tvNodes;
    private TextView tvNumber;
    private LinearLayout txList;

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            economyService = ((EconomyService.EconomyBinder) service)
                .getService();
            bound = true;

            // Gerçek zamanlı tick dinle
            economyService.setTickListener((balance, rate, uptime) -> {
                if (!isAdded()) return;
                updateUI(balance, rate, uptime);
            });
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {

        ScrollView scroll = new ScrollView(getContext());
        scroll.setBackgroundColor(0xFF0D1117);

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 20, 28, 100);
        scroll.addView(root);

        // Başlık
        TextView header = new TextView(getContext());
        header.setText("OmniCoin Cüzdanı");
        header.setTextColor(0xFFE6EDF3);
        header.setTextSize(18);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(0, 0, 0, 20);
        root.addView(header);

        // Ana bakiye kartı
        LinearLayout balCard = buildCard();
        root.addView(balCard);

        tvNumber = new TextView(getContext());
        tvNumber.setText("Numaran: " + NumberManager.format(
            NumberManager.getOrCreate(requireContext())));
        tvNumber.setTextColor(0xFF2EA043);
        tvNumber.setTextSize(11);
        tvNumber.setPadding(0, 0, 0, 16);
        balCard.addView(tvNumber);

        TextView tvBalLabel = new TextView(getContext());
        tvBalLabel.setText("Toplam Bakiye");
        tvBalLabel.setTextColor(0xFF6E7681);
        tvBalLabel.setTextSize(12);
        balCard.addView(tvBalLabel);

        tvBalance = new TextView(getContext());
        tvBalance.setText("0.000000 OC");
        tvBalance.setTextColor(0xFF2EA043);
        tvBalance.setTextSize(30);
        tvBalance.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBalance.setPadding(0, 4, 0, 4);
        balCard.addView(tvBalance);

        TextView tvUsd = new TextView(getContext());
        tvUsd.setText("≈ $0.00 USD · Henüz listelenmedi");
        tvUsd.setTextColor(0xFF484F58);
        tvUsd.setTextSize(11);
        tvUsd.setPadding(0, 0, 0, 16);
        balCard.addView(tvUsd);

        // Mining bar
        LinearLayout mineBar = new LinearLayout(getContext());
        mineBar.setOrientation(LinearLayout.HORIZONTAL);
        mineBar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        mineBar.setPadding(20, 16, 20, 16);
        android.graphics.drawable.GradientDrawable mineBg =
            new android.graphics.drawable.GradientDrawable();
        mineBg.setColor(0xFF0F3D1F);
        mineBg.setCornerRadius(16f);
        mineBar.setBackground(mineBg);

        LinearLayout mineInfo = new LinearLayout(getContext());
        mineInfo.setOrientation(LinearLayout.VERTICAL);
        mineInfo.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        tvRate = new TextView(getContext());
        tvRate.setText("⛏ +0.000000 OC/sn");
        tvRate.setTextColor(0xFF2EA043);
        tvRate.setTextSize(13);
        tvRate.setTypeface(null, android.graphics.Typeface.BOLD);
        mineInfo.addView(tvRate);

        tvNodes = new TextView(getContext());
        tvNodes.setText("1 aktif düğüm");
        tvNodes.setTextColor(0xFF6E7681);
        tvNodes.setTextSize(11);
        tvNodes.setPadding(0, 4, 0, 0);
        mineInfo.addView(tvNodes);

        mineBar.addView(mineInfo);

        TextView tvActive = new TextView(getContext());
        tvActive.setText("AKTİF");
        tvActive.setTextColor(0xFFFFFFFF);
        tvActive.setTextSize(10);
        tvActive.setTypeface(null, android.graphics.Typeface.BOLD);
        tvActive.setPadding(16, 8, 16, 8);
        android.graphics.drawable.GradientDrawable activeBg =
            new android.graphics.drawable.GradientDrawable();
        activeBg.setColor(0xFF238636);
        activeBg.setCornerRadius(12f);
        tvActive.setBackground(activeBg);
        mineBar.addView(tvActive);

        balCard.addView(mineBar);

        // İstatistik grid
        LinearLayout statsRow = new LinearLayout(getContext());
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams statsP =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        statsP.setMargins(0, 16, 0, 0);
        statsRow.setLayoutParams(statsP);

        LinearLayout totalCard = buildStatCard("Toplam Kazanılan");
        tvTotal = new TextView(getContext());
        tvTotal.setText("0.000000 OC");
        tvTotal.setTextColor(0xFF2EA043);
        tvTotal.setTextSize(14);
        tvTotal.setTypeface(null, android.graphics.Typeface.BOLD);
        totalCard.addView(tvTotal);
        LinearLayout.LayoutParams tcP =
            new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tcP.setMargins(0, 0, 8, 0);
        totalCard.setLayoutParams(tcP);
        statsRow.addView(totalCard);

        LinearLayout uptimeCard = buildStatCard("Ağda Süre");
        tvUptime = new TextView(getContext());
        tvUptime.setText("00:00:00");
        tvUptime.setTextColor(0xFF58A6FF);
        tvUptime.setTextSize(14);
        tvUptime.setTypeface(null, android.graphics.Typeface.BOLD);
        uptimeCard.addView(tvUptime);
        uptimeCard.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        statsRow.addView(uptimeCard);

        root.addView(statsRow);

        // İşlem butonları
        LinearLayout btns = new LinearLayout(getContext());
        btns.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams btnRowP =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnRowP.setMargins(0, 16, 0, 16);
        btns.setLayoutParams(btnRowP);

        Button btnSend = buildActionBtn("↑ Gönder", 0xFF238636);
        btnSend.setOnClickListener(v -> showSendDialog());
        LinearLayout.LayoutParams sp =
            new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        sp.setMargins(0, 0, 8, 0);
        btnSend.setLayoutParams(sp);
        btns.addView(btnSend);

        Button btnRecv = buildActionBtn("↓ Al", 0xFF21262D);
        btnRecv.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        btnRecv.setOnClickListener(v -> showReceiveDialog());
        btns.addView(btnRecv);

        root.addView(btns);

        // Son işlemler başlığı
        TextView txHeader = new TextView(getContext());
        txHeader.setText("İŞLEM GEÇMİŞİ");
        txHeader.setTextColor(0xFF6E7681);
        txHeader.setTextSize(11);
        txHeader.setPadding(0, 0, 0, 12);
        root.addView(txHeader);

        // İşlem listesi
        txList = new LinearLayout(getContext());
        txList.setOrientation(LinearLayout.VERTICAL);
        android.graphics.drawable.GradientDrawable txBg =
            new android.graphics.drawable.GradientDrawable();
        txBg.setColor(0xFF161B22);
        txBg.setCornerRadius(20f);
        txBg.setStroke(1, 0xFF21262D);
        txList.setBackground(txBg);
        txList.setClipToOutline(true);
        root.addView(txList);

        addTxRow("⛏ Mining Başladı", "Ağa katıldın", "+0.000000 OC", true);

        return scroll;
    }

    private void updateUI(double balance, double rate, long uptime) {
        if (tvBalance != null)
            tvBalance.setText(String.format("%.6f OC", balance));
        if (tvRate != null)
            tvRate.setText(String.format("⛏ +%.8f OC/sn", rate));
        if (tvTotal != null && bound)
            tvTotal.setText(String.format("%.6f OC",
                economyService.getTotalEarned()));
        if (tvNodes != null && bound)
            tvNodes.setText(economyService.getActiveNodes() +
                " aktif düğüm");
        if (tvUptime != null) {
            long h = uptime / 3600;
            long m = (uptime % 3600) / 60;
            long s = uptime % 60;
            tvUptime.setText(String.format("%02d:%02d:%02d", h, m, s));
        }
    }

    private void showSendDialog() {
        android.app.AlertDialog.Builder builder =
            new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("OC Gönder");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 20, 48, 0);

        EditText etTo = new EditText(getContext());
        etTo.setHint("+777 3543 XXXX XXXX");
        etTo.setHintTextColor(0xFF484F58);
        etTo.setTextColor(0xFFC9D1D9);
        layout.addView(etTo);

        EditText etAmt = new EditText(getContext());
        etAmt.setHint("Miktar (OC)");
        etAmt.setHintTextColor(0xFF484F58);
        etAmt.setTextColor(0xFFC9D1D9);
        etAmt.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL |
            android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etAmt);

        builder.setView(layout);
        builder.setPositiveButton("Gönder", (dialog, which) -> {
            if (!bound) return;
            String to  = etTo.getText().toString().trim();
            String amt = etAmt.getText().toString().trim();
            if (to.isEmpty() || amt.isEmpty()) return;
            try {
                double amount = Double.parseDouble(amt);
                if (economyService.send(amount)) {
                    addTxRow("↑ Gönderildi", to,
                        "-" + amt + " OC", false);
                    Toast.makeText(getContext(),
                        "✓ Gönderildi!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),
                        "Yetersiz bakiye!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(),
                    "Geçersiz miktar", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void showReceiveDialog() {
        String myNumber = NumberManager.getOrCreate(requireContext());
        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("OC Al")
            .setMessage("Numaranı paylaş:\n\n" +
                NumberManager.format(myNumber) + "\n\n" +
                "Bu numarayı karşı tarafa ver, sana OC göndersin.")
            .setPositiveButton("Tamam", null)
            .show();
    }

    private void addTxRow(String title, String detail,
                           String amount, boolean positive) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(24, 20, 24, 20);

        LinearLayout info = new LinearLayout(getContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(title);
        tvTitle.setTextColor(0xFFE6EDF3);
        tvTitle.setTextSize(13);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(tvTitle);

        TextView tvDetail = new TextView(getContext());
        tvDetail.setText(detail);
        tvDetail.setTextColor(0xFF6E7681);
        tvDetail.setTextSize(11);
        tvDetail.setPadding(0, 3, 0, 0);
        info.addView(tvDetail);

        row.addView(info);

        TextView tvAmt = new TextView(getContext());
        tvAmt.setText(amount);
        tvAmt.setTextColor(positive ? 0xFF2EA043 : 0xFFF85149);
        tvAmt.setTextSize(13);
        tvAmt.setTypeface(null, android.graphics.Typeface.BOLD);
        row.addView(tvAmt);

        txList.addView(row);

        View line = new View(getContext());
        line.setBackgroundColor(0xFF21262D);
        line.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 1));
        txList.addView(line);
    }

    private LinearLayout buildCard() {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(28, 28, 28, 28);
        android.graphics.drawable.GradientDrawable bg =
            new android.graphics.drawable.GradientDrawable();
        bg.setColor(0xFF161B22);
        bg.setCornerRadius(24f);
        bg.setStroke(1, 0xFF21262D);
        card.setBackground(bg);
        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, 16);
        card.setLayoutParams(p);
        return card;
    }

    private LinearLayout buildStatCard(String label) {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 16, 20, 16);
        android.graphics.drawable.GradientDrawable bg =
            new android.graphics.drawable.GradientDrawable();
        bg.setColor(0xFF161B22);
        bg.setCornerRadius(16f);
        bg.setStroke(1, 0xFF21262D);
        card.setBackground(bg);

        TextView lbl = new TextView(getContext());
        lbl.setText(label);
        lbl.setTextColor(0xFF6E7681);
        lbl.setTextSize(11);
        lbl.setPadding(0, 0, 0, 6);
        card.addView(lbl);

        return card;
    }

    private Button buildActionBtn(String text, int color) {
        Button btn = new Button(getContext());
        btn.setText(text);
        btn.setTextColor(0xFFFFFFFF);
        btn.setTextSize(14);
        btn.setTypeface(null, android.graphics.Typeface.BOLD);
        btn.setPadding(0, 24, 0, 24);
        android.graphics.drawable.GradientDrawable bg =
            new android.graphics.drawable.GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(20f);
        if (color == 0xFF21262D) {
            bg.setStroke(1, 0xFF30363D);
        }
        btn.setBackground(bg);
        return btn;
    }

    @Override
    public void onStart() {
        super.onStart();
        requireContext().bindService(
            new Intent(getContext(), EconomyService.class),
            conn, android.content.Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound) {
            economyService.setTickListener(null);
            requireContext().unbindService(conn);
            bound = false;
        }
    }
}
