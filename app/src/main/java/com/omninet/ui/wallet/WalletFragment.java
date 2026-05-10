package com.omninet.ui.wallet;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.omninet.services.EconomyService;

public class WalletFragment extends Fragment {

    private EconomyService economyService;
    private boolean        bound = false;
    private Handler        handler = new Handler(Looper.getMainLooper());
    private TextView       tvBalance;
    private TextView       tvRate;

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            economyService = ((EconomyService.EconomyBinder) service).getService();
            bound = true;
            startUpdates();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xFF0D1117);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setPadding(32, 32, 32, 32);

        TextView title = new TextView(getContext());
        title.setText("OmniCoin Cüzdanı");
        title.setTextColor(0xFF6E7681);
        title.setTextSize(13);
        title.setGravity(android.view.Gravity.CENTER);

        tvBalance = new TextView(getContext());
        tvBalance.setText("0.000000 OC");
        tvBalance.setTextColor(0xFF2EA043);
        tvBalance.setTextSize(28);
        tvBalance.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBalance.setGravity(android.view.Gravity.CENTER);
        tvBalance.setPadding(0, 8, 0, 4);

        tvRate = new TextView(getContext());
        tvRate.setText("⛏ +0.000000 OC/sn");
        tvRate.setTextColor(0xFF6E7681);
        tvRate.setTextSize(13);
        tvRate.setGravity(android.view.Gravity.CENTER);

        layout.addView(title);
        layout.addView(tvBalance);
        layout.addView(tvRate);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getContext(), EconomyService.class);
        requireContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
        if (bound) requireContext().unbindService(conn);
    }

    private void startUpdates() {
        handler.post(new Runnable() {
            @Override public void run() {
                if (!bound || !isAdded()) return;
                if (tvBalance != null)
                    tvBalance.setText(String.format("%.6f OC",
                        economyService.getBalance()));
                if (tvRate != null)
                    tvRate.setText(String.format("⛏ +%.6f OC/sn",
                        economyService.getMiningRate()));
                handler.postDelayed(this, 1000);
            }
        });
    }
}
