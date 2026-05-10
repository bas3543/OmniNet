package com.omninet.ui.feed;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class FeedFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {
        TextView tv = new TextView(getContext());
        tv.setText("Mesh Feed yükleniyor...");
        tv.setTextColor(0xFFE6EDF3);
        tv.setTextSize(16);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setBackgroundColor(0xFF0D1117);
        return tv;
    }
}
