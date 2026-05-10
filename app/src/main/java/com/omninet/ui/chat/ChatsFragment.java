package com.omninet.ui.chat;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ChatsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {
        // Geçici basit görünüm
        TextView tv = new TextView(getContext());
        tv.setText("Sohbetler yükleniyor...");
        tv.setTextColor(0xFFE6EDF3);
        tv.setTextSize(16);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setBackgroundColor(0xFF0D1117);
        return tv;
    }
}
