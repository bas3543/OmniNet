package com.omninet.ui.call;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.omninet.R;
import com.omninet.data.models.Call;

public class CallActivity extends AppCompatActivity {
    private TextView contactName, callDuration, callStatus;
    private ImageButton endCallButton, muteButton, speakerButton;
    private Call currentCall;
    private long callStartTime;
    private boolean isCallActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        contactName = findViewById(R.id.contactName);
        callDuration = findViewById(R.id.callDuration);
        callStatus = findViewById(R.id.callStatus);
        endCallButton = findViewById(R.id.endCallButton);
        muteButton = findViewById(R.id.muteButton);
        speakerButton = findViewById(R.id.speakerButton);

        String name = getIntent().getStringExtra("contactName");
        String callType = getIntent().getStringExtra("callType");
        contactName.setText(name);
        callStatus.setText(callType != null && callType.equals("voice") ? "Voice Call" : "Video Call");

        callStartTime = System.currentTimeMillis();
        setupButtons();
        startDurationTimer();
    }

    private void setupButtons() {
        endCallButton.setOnClickListener(v -> endCall());
        muteButton.setOnClickListener(v -> toggleMute());
        speakerButton.setOnClickListener(v -> toggleSpeaker());
    }

    private void startDurationTimer() {
        new Thread(() -> {
            while (isCallActive) {
                long duration = System.currentTimeMillis() - callStartTime;
                long seconds = duration / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                String timeStr = String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
                runOnUiThread(() -> callDuration.setText(timeStr));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void toggleMute() {
    }

    private void toggleSpeaker() {
    }

    private void endCall() {
        isCallActive = false;
        finish();
    }
}
