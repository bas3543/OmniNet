package com.omninet.ui.call;

import android.media.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CallFragment extends Fragment {

    private boolean callActive = false;
    private boolean muted = false;
    private boolean speaker = false;
    private long callStartTime;
    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView tvDuration;
    private String peerName;
    private String callType; // "VOICE" veya "VIDEO"

    public static CallFragment newInstance(String peerName, String callType) {
        CallFragment f = new CallFragment();
        Bundle args = new Bundle();
        args.putString("peer_name", peerName);
        args.putString("call_type", callType);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {

        if (getArguments() != null) {
            peerName = getArguments().getString("peer_name", "Bilinmeyen");
            callType = getArguments().getString("call_type", "VOICE");
        }

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0D1117);
        root.setGravity(android.view.Gravity.CENTER);
        root.setPadding(40, 60, 40, 60);

        // Avatar
        TextView av = new TextView(getContext());
        av.setText(peerName != null && peerName.length() >= 2 ?
            peerName.substring(0, 2).toUpperCase() : "??");
        av.setTextColor(0xFFFFFFFF);
        av.setTextSize(36);
        av.setTypeface(null, android.graphics.Typeface.BOLD);
        av.setGravity(android.view.Gravity.CENTER);
        android.graphics.drawable.GradientDrawable avBg =
            new android.graphics.drawable.GradientDrawable();
        avBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        avBg.setColor(0xFF238636);
        av.setBackground(avBg);
        LinearLayout.LayoutParams avParams =
            new LinearLayout.LayoutParams(200, 200);
        avParams.setMargins(0, 0, 0, 40);
        av.setLayoutParams(avParams);
        root.addView(av);

        // İsim
        TextView tvName = new TextView(getContext());
        tvName.setText(peerName);
        tvName.setTextColor(0xFFE6EDF3);
        tvName.setTextSize(24);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setGravity(android.view.Gravity.CENTER);
        root.addView(tvName);

        // Durum
        TextView tvStatus = new TextView(getContext());
        tvStatus.setText("Bağlanıyor...");
        tvStatus.setTextColor(0xFF6E7681);
        tvStatus.setTextSize(14);
        tvStatus.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams statusParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        statusParams.setMargins(0, 8, 0, 0);
        tvStatus.setLayoutParams(statusParams);
        root.addView(tvStatus);

        // Mesh bilgisi
        TextView tvMesh = new TextView(getContext());
        tvMesh.setText("⬡ Mesh üzerinden · AES-256 şifreli");
        tvMesh.setTextColor(0xFF2EA043);
        tvMesh.setTextSize(12);
        tvMesh.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams meshParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        meshParams.setMargins(0, 8, 0, 60);
        tvMesh.setLayoutParams(meshParams);
        root.addView(tvMesh);

        // Süre
        tvDuration = new TextView(getContext());
        tvDuration.setText("00:00");
        tvDuration.setTextColor(0xFF2EA043);
        tvDuration.setTextSize(32);
        tvDuration.setTypeface(null, android.graphics.Typeface.BOLD);
        tvDuration.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams durParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        durParams.setMargins(0, 0, 0, 60);
        tvDuration.setLayoutParams(durParams);
        root.addView(tvDuration);

        // Kontrol butonları
        LinearLayout controls = new LinearLayout(getContext());
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(android.view.Gravity.CENTER);
        controls.setPadding(0, 0, 0, 40);

        // Sessiz
        Button btnMute = buildCircleBtn("🎙️", 0xFF21262D);
        btnMute.setOnClickListener(v -> {
            muted = !muted;
            btnMute.setText(muted ? "🔇" : "🎙️");
            btnMute.setBackgroundColor(muted ? 0xFF30363D : 0xFF21262D);
            android.widget.Toast.makeText(getContext(),
                muted ? "Sessiz" : "Ses açık",
                android.widget.Toast.LENGTH_SHORT).show();
        });

        // Hoparlör
        Button btnSpeaker = buildCircleBtn("🔈", 0xFF21262D);
        btnSpeaker.setOnClickListener(v -> {
