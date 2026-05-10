package com.omninet.ui.browser;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class BrowserFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFF0D1117);
        layout.setGravity(android.view.Gravity.CENTER);

        TextView tv = new TextView(getContext());
        tv.setText("OmniNet Tarayıcı");
        tv.setTextColor(0xFF58A6FF);
        tv.setTextSize(18);
        tv.setGravity(android.view.Gravity.CENTER);

        TextView tv2 = new TextView(getContext());
        tv2.setText("Mesh proxy hazırlanıyor...");
        tv2.setTextColor(0xFF6E7681);
        tv2.setTextSize(13);
        tv2.setGravity(android.view.Gravity.CENTER);
        tv2.setPadding(0, 8, 0, 0);

        layout.addView(tv);
        layout.addView(tv2);
        return layout;
    }
}
