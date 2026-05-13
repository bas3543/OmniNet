package com.omninet.ui.chat;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.omninet.R;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Contact;
import com.omninet.data.models.Message;
import com.omninet.network.NumberManager;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private Contact contact;
    private String myNumber;
    private LinearLayout msgList;
    private EditText etMessage;
    private ScrollView scrollView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private BroadcastReceiver msgReceiver;

    public static void start(Activity activity, Contact contact) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("number", contact.omniNumber);
        intent.putExtra("name", contact.displayName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myNumber = NumberManager.getOrCreate(this);

        String number = getIntent().getStringExtra("number");
        String name   = getIntent().getStringExtra("name");

        // Kişiyi DB'den yükle
        Executors.newSingleThreadExecutor().execute(() -> {
            contact = OmniDatabase.get(this).contactDao().getByNumber(number);
            if (contact == null) {
                contact = new Contact(number, name != null ? name : number);
            }
            handler.post(this::buildUI);
        });
    }

    private void buildUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0D1117);
        root.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));

        // Üst bar
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(0xFF161B22);
        topBar.setPadding(20, 20, 20, 20);
        topBar.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Geri butonu
        Button btnBack = new Button(this);
        btnBack.setText("←");
        btnBack.setTextColor(0xFF58A6FF);
        btnBack.setTextSize(20);
        btnBack.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnBack.setPadding(0, 0, 16, 0);
        btnBack.setOnClickListener(v -> finish());
        topBar.addView(btnBack);

        // Avatar
        TextView av = new TextView(this);
        av.setText(contact.initials != null ? contact.initials : "??");
        av.setTextColor(0xFFFFFFFF);
        av.setTextSize(14);
        av.setTypeface(null, android.graphics.Typeface.BOLD);
        av.setGravity(android.view.Gravity.CENTER);
        android.graphics.drawable.GradientDrawable avBg =
            new android.graphics.drawable.GradientDrawable();
        avBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        try {
            avBg.setColor(android.graphics.Color.parseColor(
                contact.avatarColor != null ? contact.avatarColor : "#238636"));
        } catch (Exception e) {
            avBg.setColor(0xFF238636);
        }
        av.setBackground(avBg);
        LinearLayout.LayoutParams avP = new LinearLayout.LayoutParams(72, 72);
        avP.setMargins(0, 0, 16, 0);
        av.setLayoutParams(avP);
        topBar.addView(av);

        // İsim + numara
        LinearLayout nameBlock = new LinearLayout(this);
        nameBlock.setOrientation(LinearLayout.VERTICAL);
        nameBlock.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(this);
        tvName.setText(contact.displayName +
            (contact.isFounder ? " ⭐" : ""));
        tvName.setTextColor(0xFFE6EDF3);
        tvName.setTextSize(15);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        nameBlock.addView(tvName);

        TextView tvNum = new TextView(this);
        tvNum.setText(NumberManager.format(contact.omniNumber));
        tvNum.setTextColor(0xFF2EA043);
        tvNum.setTextSize(11);
        nameBlock.addView(tvNum);

        topBar.addView(nameBlock);

        // Arama butonu
        Button btnCall = new Button(this);
        btnCall.setText("📞");
        btnCall.setTextSize(18);
        btnCall.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnCall.setOnClickListener(v ->
            Toast.makeText(this, "Aranıyor: " +
                contact.displayName, Toast.LENGTH_SHORT).show());
        topBar.addView(btnCall);

        root.addView(topBar);

        // Mesh durum şeridi
        TextView tvMesh = new TextView(this);
        tvMesh.setText("⬡ Mesh · AES-256 şifreli · " +
            (contact.hopDistance > 0 ?
                contact.hopDistance + " atlama" : "Direkt"));
        tvMesh.setTextColor(0xFF2EA043);
        tvMesh.setTextSize(10);
        tvMesh.setPadding(28, 8, 28, 8);
        tvMesh.setBackgroundColor(0xFF0F3D1F);
        root.addView(tvMesh);

        // Mesaj listesi
        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        scrollView.setBackgroundColor(0xFF0D1117);

        msgList = new LinearLayout(this);
        msgList.setOrientation(LinearLayout.VERTICAL);
        msgList.setPadding(20, 16, 20, 16);
        scrollView.addView(msgList);
        root.addView(scrollView);

        // Giriş çubuğu
        LinearLayout inputBar = new LinearLayout(this);
        inputBar.setOrientation(LinearLayout.HORIZONTAL);
        inputBar.setBackgroundColor(0xFF161B22);
        inputBar.setPadding(16, 12, 16, 12);
        inputBar.setGravity(android.view.Gravity.CENTER_VERTICAL);

        etMessage = new EditText(this);
        etMessage.setHint("Şifreli mesaj yaz...");
        etMessage.setHintTextColor(0xFF484F58);
        etMessage.setTextColor(0xFFC9D1D9);
        etMessage.setTextSize(13);
        etMessage.setPadding(24, 20, 24, 20);
        etMessage.setMaxLines(4);
        etMessage.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
            android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        android.graphics.drawable.GradientDrawable inputBg =
            new android.graphics.drawable.GradientDrawable();
        inputBg.setColor(0xFF21262D);
        inputBg.setCornerRadius(24f);
        inputBg.setStroke(1, 0xFF30363D);
        etMessage.setBackground(inputBg);
        etMessage.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        inputBar.addView(etMessage);

        // Gönder butonu
        Button btnSend = new Button(this);
        btnSend.setText("↑");
        btnSend.setTextColor(0xFFFFFFFF);
        btnSend.setTextSize(18);
        btnSend.setTypeface(null, android.graphics.Typeface.BOLD);
        android.graphics.drawable.GradientDrawable sendBg =
            new android.graphics.drawable.GradientDrawable();
        sendBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        sendBg.setColor(0xFF238636);
        btnSend.setBackground(sendBg);
        LinearLayout.LayoutParams sendP =
            new LinearLayout.LayoutParams(100, 100);
        sendP.setMargins(12, 0, 0, 0);
        btnSend.setLayoutParams(sendP);
        btnSend.setOnClickListener(v -> sendMessage());
        inputBar.addView(btnSend);

        root.addView(inputBar);
        setContentView(root);

        // Mesajları yükle
        loadMessages();

        // Gelen mesajları dinle
        msgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                String from = intent.getStringExtra("from");
                if (contact.omniNumber.equals(from)) {
                    loadMessages();
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(msgReceiver,
                new IntentFilter("com.omninet.MESSAGE"),
                Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(msgReceiver,
                new IntentFilter("com.omninet.MESSAGE"));
        }

        // Okundu işaretle
        Executors.newSingleThreadExecutor().execute(() ->
            OmniDatabase.get(this).messageDao()
                .markAllRead(contact.omniNumber));
    }

    private void loadMessages() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Message> messages = OmniDatabase.get(this)
                .messageDao().getThreadSync(contact.omniNumber);

            handler.post(() -> {
                msgList.removeAllViews();
                if (messages.isEmpty()) {
                    TextView empty = new TextView(this);
                    empty.setText("Henüz mesaj yok.\nMesaj göndererek başla!");
                    empty.setTextColor(0xFF6E7681);
                    empty.setTextSize(13);
                    empty.setGravity(android.view.Gravity.CENTER);
                    empty.setPadding(0, 60, 0, 0);
                    msgList.addView(empty);
                } else {
                    for (Message msg : messages) {
                        msgList.addView(buildMsgBubble(msg));
                    }
                }
                // En alta kaydır
                scrollView.post(() ->
                    scrollView.fullScroll(View.FOCUS_DOWN));
            });
        });
    }

    private View buildMsgBubble(Message msg) {
        boolean isOut = msg.isOutgoing(myNumber);

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setGravity(isOut ?
            android.view.Gravity.END : android.view.Gravity.START);
        LinearLayout.LayoutParams wP = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        wP.setMargins(0, 4, 0, 4);
        wrapper.setLayoutParams(wP);

        TextView bubble = new TextView(this);
        bubble.setText(msg.clearText);
        bubble.setTextColor(isOut ? 0xFFAFF5C3 : 0xFFC9D1D9);
        bubble.setTextSize(13);
        bubble.setPadding(28, 20, 28, 20);
        bubble.setLineSpacing(4, 1);

        android.graphics.drawable.GradientDrawable bubbleBg =
            new android.graphics.drawable.GradientDrawable();
        bubbleBg.setColor(isOut ? 0xFF1A4731 : 0xFF21262D);
        if (isOut) {
            bubbleBg.setCornerRadii(new float[]{
                28, 28, 28, 28, 28, 28, 4, 4});
        } else {
            bubbleBg.setCornerRadii(new float[]{
                28, 28, 28, 28, 4, 4, 28, 28});
        }
        bubble.setBackground(bubbleBg);

        LinearLayout.LayoutParams bP = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        bP.setMargins(isOut ? 120 : 0, 0, isOut ? 0 : 120, 0);
        bubble.setLayoutParams(bP);
        wrapper.addView(bubble);

        // Zaman + durum
        TextView tvMeta = new TextView(this);
        tvMeta.setText(msg.getTimeString() +
            (isOut ? "  " + msg.getStatusIcon() : ""));
        tvMeta.setTextColor(0xFF6E7681);
        tvMeta.setTextSize(10);
        LinearLayout.LayoutParams metaP = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        metaP.setMargins(isOut ? 0 : 8, 4, isOut ? 8 : 0, 0);
        tvMeta.setLayoutParams(metaP);
        wrapper.addView(tvMeta);

        return wrapper;
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        Message msg = Message.createText(myNumber,
            contact.omniNumber, text);

        etMessage.setText("");

        Executors.newSingleThreadExecutor().execute(() -> {
            OmniDatabase.get(this).messageDao().insert(msg);

            // Mesh üzerinden gönder
            Intent intent = new Intent(this,
                com.omninet.services.OmniBackgroundService.class);
            intent.setAction(
                com.omninet.services.OmniBackgroundService
                    .ACTION_SEND_MESSAGE);
            intent.putExtra("target", contact.omniNumber);
            intent.putExtra("payload", text.getBytes());
            intent.putExtra("type", (byte) 0x01);
            startService(intent);

            // Gönderildi işaretle
            handler.postDelayed(() -> {
                Executors.newSingleThreadExecutor().execute(() -> {
                    OmniDatabase.get(this).messageDao()
                        .markSent(msg.msgId);
                    loadMessages();
                });
            }, 500);
        });

        loadMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(msgReceiver);
        } catch (Exception ignored) {}
    }
}
