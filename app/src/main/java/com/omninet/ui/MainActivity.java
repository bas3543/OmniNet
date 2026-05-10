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
import com.omninet.ui.wallet.WalletFragment;
import com.omninet.ui.settings.SettingsFragment;
import com.omninet.ui.feed.FeedFragment;
import com.omninet.ui.radar.RadarFragment;
import com.omninet.ui.browser.BrowserFragment;
import com.omninet.ui.call.CallFragment;
import com.omninet.ui.file.FileFragment;

public class MainActivity extends AppCompatActivity {

    private EconomyService economyService;
    private boolean economyBound = false;
    private TextView tvMeshStatus;
    private TextView tvBalancePill;
    private Handler handler = new Handler(Looper.getMainLooper());

    private final ServiceConnection economyConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            economyService = ((EconomyService.EconomyBinder) service).getService();
            economyBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            economyBound = false;
        }
    };

    private final Runnable updateRunner = new Runnable() {
        @Override public void run() {
            if (economyBound && tvBalancePill != null) {
                tvBalancePill.setText(String.format("%.2f OC",
                    economyService.getBalance()));
            }
            handler.postDelayed(this, 2000);
        }
    };

    private final BroadcastReceiver meshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            int nodes = intent.getIntExtra("node_count", 0);
            boolean internet = intent.getBooleanExtra("has_internet", false);
            if (tvMeshStatus != null) {
                tvMeshStatus.setText("⬡ " + nodes + " düğüm" +
                    (internet ? " · 🌐" : ""));
            }
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
            if      (id == R.id.nav_chats)    f = new ChatsFragment();
            else if (id == R.id.nav_wallet)   f = new WalletFragment();
            else if (id == R.id.nav_settings) f = new SettingsFragment();
            if (f == null) return false;
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f).commit();
            return true;
        });

        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_chats);
        }

        findViewById(R.id.fab_main).setOnClickListener(v -> showPlusMenu());

        Intent econIntent = new Intent(this, EconomyService.class);
        bindService(econIntent, economyConn, BIND_AUTO_CREATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(meshReceiver,
                new IntentFilter("com.omninet.MESH_STATUS"),
                Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(meshReceiver,
                new IntentFilter("com.omninet.MESH_STATUS"));
        }
    }

    public void openFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, f)
            .addToBackStack(null)
            .commit();
    }

    private void showPlusMenu() {
        String[] items = {
            "📡  Feed (Mesh Sosyal)",
            "🎯  Radar (Ağ Haritası)",
            "🌐  Tarayıcı",
            "📞  Sesli Görüşme",
            "📹  Görüntülü Görüşme",
            "📁  Dosya Gönder",
            "🔑  NFC Kimlik Doğrula",
            "📊  Ağ İstatistikleri"
        };

        new android.app.AlertDialog.Builder(this)
            .setTitle("OmniNet Özellikler")
            .setItems(items, (dialog, which) -> {
                switch (which) {
                    case 0:
                        openFragment(new FeedFragment());
                        break;
                    case 1:
                        openFragment(new RadarFragment());
                        break;
                    case 2:
                        openFragment(new BrowserFragment());
                        break;
                    case 3:
                        openFragment(CallFragment.newInstance(
                            "Kerem", "VOICE"));
                        break;
                    case 4:
                        openFragment(CallFragment.newInstance(
                            "Kerem", "VIDEO"));
                        break;
                   case 5:
                        openFragment(new FileFragment());
                        break;
                    default:
                        android.widget.Toast.makeText(this,
                            items[which] + " yakında!",
                            android.widget.Toast.LENGTH_SHORT).show();
                }
            })
            .show();
    }

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
