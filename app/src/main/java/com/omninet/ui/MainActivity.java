package com.omninet.ui;

import android.content.*;
import android.os.*;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.omninet.R;
import com.omninet.services.EconomyService;
import com.omninet.ui.chat.ChatsFragment;
import com.omninet.ui.feed.FeedFragment;
import com.omninet.ui.radar.RadarFragment;
import com.omninet.ui.browser.BrowserFragment;
import com.omninet.ui.wallet.WalletFragment;
import com.omninet.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private EconomyService economyService;
    private boolean        economyBound = false;
    private TextView       tvMeshStatus;
    private TextView       tvBalancePill;
    private Handler        handler = new Handler(Looper.getMainLooper());

    private final ServiceConnection economyConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            economyService = ((EconomyService.EconomyBinder) service).getService();
            economyBound   = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            economyBound = false;
        }
    };

    private final Runnable updateRunner = new Runnable() {
        @Override public void run() {
            updateStatusBar();
            handler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMeshStatus  = findViewById(R.id.tv_mesh_status);
        tvBalancePill = findViewById(R.id.tv_balance_pill);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            int id = item.getItemId();
            if (id == R.id.nav_chats)    f = new ChatsFragment();
            else if (id == R.id.nav_feed)     f = new FeedFragment();
            else if (id == R.id.nav_radar)    f = new RadarFragment();
            else if (id == R.id.nav_browser)  f = new BrowserFragment();
            else if (id == R.id.nav_wallet)   f = new WalletFragment();
            else if (id == R.id.nav_settings) f = new SettingsFragment();
            if (f == null) return false;
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit();
            return true;
        });

        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_chats);
        }

        Intent econIntent = new Intent(this, EconomyService.class);
        bindService(econIntent, economyConn, BIND_AUTO_CREATE);

        registerReceiver(meshReceiver,
            new IntentFilter("com.omninet.MESH_STATUS"));
    }

    private void updateStatusBar() {
        if (economyBound && tvBalancePill != null) {
            tvBalancePill.setText(String.format(
                "⛏ %.2f OC", economyService.getBalance()));
        }
    }

    private final BroadcastReceiver meshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            int nodes = intent.getIntExtra("node_count", 0);
            boolean internet = intent.getBooleanExtra("has_internet", false);
            if (tvMeshStatus != null) {
                tvMeshStatus.setText("⬡ " + nodes + " düğüm" +
                    (internet ? " · 🌐 İnternet" : ""));
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(updateRunner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateRunner);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (economyBound) unbindService(economyConn);
        try { unregisterReceiver(meshReceiver); } catch (Exception ignored) {}
    }
}
