package com.omninet.ui.chat;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Contact;
import com.omninet.data.models.Message;
import com.omninet.network.NumberManager;
import com.omninet.services.OmniBackgroundService;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private Contact contact;
    private String myNumber;
    private LinearLayout msgList;
    private EditText etMessage;
    private ScrollView scrollView;
    private Button btnSend;
    private android.graphics.drawable.GradientDrawable sendBg;
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
        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        myNumber = NumberManager.getOrCreate(this);
        String number = getIntent().getStringExtra("number");
        String name   = getIntent().getStringExtra("name");

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

        // Üst bar
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(0xFF161B22);
        topBar.setPadding(8, 44, 16, 12);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        Button btnBack = new Button(this);
        btnBack.setText("←");
        btnBack.setTextColor(0xFF58A6FF);
        btnBack.setTextSize(22);
        btnBack.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnBack.setOnClickListener(v -> finish());
        topBar.addView(btnBack);

        TextView av = new TextView(this);
        av.setText(contact.initials != null ? contact.initials : "??");
        av.setTextColor(0xFFFFFFFF);
        av.setTextSize(14);
        av.setTypeface(null, android.graphics.Typeface.BOLD);
        av.setGravity(Gravity.CENTER);
        android.graphics.drawable.GradientDrawable avBg =
            new android.graphics.drawable.GradientDrawable();
        avBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        try {
            avBg.setColor(android.graphics.Color.parseColor(
                contact.avatarColor != null ? contact.avatarColor : "#238636"));
        } catch (Exception e) { avBg.setColor(0xFF238636); }
        av.setBackground(avBg);
        LinearLayout.LayoutParams avP = new LinearLayout.LayoutParams(72, 72);
        avP.setMargins(4, 0, 14, 0);
        av.setLayoutParams(avP);
        topBar.addView(av);

        LinearLayout nameBlock = new LinearLayout(this);
        nameBlock.setOrientation(LinearLayout.VERTICAL);
        nameBlock.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(this);
        tvName.setText(contact.displayName + (contact.isFounder ? " ⭐" : ""));
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

        Button btnCall = new Button(this);
        btnCall.setText("📞");
        btnCall.setTextSize(20);
        btnCall.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnCall.setOnClickListener(v ->
            Toast.makeText(this, "Aranıyor: " + contact.displayName,
                Toast.LENGTH_SHORT).show());
        topBar.addView(btnCall);
        root.addView(topBar);

        // Mesh şerit
        TextView tvMesh = new TextView(this);
        tvMesh.setText("⬡ AES-256 şifreli · " +
            (contact.hopDistance > 0 ? contact.hopDistance + " atlama" : "Direkt bağlantı"));
        tvMesh.setTextColor(0xFF2EA043);
        tvMesh.setTextSize(10);
        tvMesh.setPadding(28, 9, 28, 9);
        tvMesh.setBackgroundColor(0xFF0F3D1F);
        root.addView(tvMesh);

        // Mesaj listesi
        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        scrollView.setBackgroundColor(0xFF0D1117);

        msgList = new LinearLayout(this);
        msgList.setOrientation(LinearLayout.VERTICAL);
        msgList.setPadding(14, 14, 14, 14);
        scrollView.addView(msgList);
        root.addView(scrollView);

        // Giriş çubuğu
        LinearLayout inputBar = new LinearLayout(this);
        inputBar.setOrientation(LinearLayout.HORIZONTAL);
        inputBar.setBackgroundColor(0xFF161B22);
        inputBar.setPadding(12, 10, 12, 28);
        inputBar.setGravity(Gravity.BOTTOM);

        etMessage = new EditText(this);
        etMessage.setHint("Şifreli mesaj yaz...");
        etMessage.setHintTextColor(0xFF484F58);
        etMessage.setTextColor(0xFFE6EDF3);
        etMessage.setTextSize(14);
        etMessage.setPadding(20, 14, 20, 14);
        etMessage.setMaxLines(5);
        etMessage.setInputType(
            android.text.InputType.TYPE_CLASS_TEXT |
            android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE |
            android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
            android.text.InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        etMessage.setImeOptions(EditorInfo.IME_ACTION_SEND |
            EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        etMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

        android.graphics.drawable.GradientDrawable inputBg =
            new android.graphics.drawable.GradientDrawable();
        inputBg.setColor(0xFF21262D);
        inputBg.setCornerRadius(28f);
        inputBg.setStroke(1, 0xFF30363D);
        etMessage.setBackground(inputBg);

        LinearLayout.LayoutParams etLP = new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        etLP.setMargins(0, 0, 10, 0);
        etMessage.setLayoutParams(etLP);

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        inputBar.addView(etMessage);

        btnSend = new Button(this);
        btnSend.setText("↑");
        btnSend.setTextColor(0xFFFFFFFF);
        btnSend.setTextSize(20);
        btnSend.setTypeface(null, android.graphics.Typeface.BOLD);
        sendBg = new android.graphics.drawable.GradientDrawable();
        sendBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        sendBg.setColor(0xFF484F58);
        btnSend.setBackground(sendBg);
        btnSend.setLayoutParams(new LinearLayout.LayoutParams(88, 88));
        btnSend.setEnabled(false);
        btnSend.setOnClickListener(v -> sendMessage());
        inputBar.addView(btnSend);

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                boolean has = s.toString().trim().length() > 0;
                btnSend.setEnabled(has);
                sendBg.setColor(has ? 0xFF238636 : 0xFF484F58);
            }
        });

        root.addView(inputBar);
        setContentView(root);

        etMessage.requestFocus();
        handler.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(etMessage, InputMethodManager.SHOW_IMPLICIT);
        }, 250);

        loadMessages();
        registerMsgReceiver();

        Executors.newSingleThreadExecutor().execute(() ->
            OmniDatabase.get(this).messageDao().markAllRead(contact.omniNumber));
    }

    private void registerMsgReceiver() {
        msgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                String from = intent.getStringExtra("from");
                if (contact != null && contact.omniNumber.equals(from)) {
                    loadMessages();
                }
            }
        };
        IntentFilter f = new IntentFilter("com.omninet.MESSAGE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(msgReceiver, f, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(msgReceiver, f);
        }
    }

    private void loadMessages() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Message> msgs = OmniDatabase.get(this)
                .messageDao().getThreadSync(contact.omniNumber);
            handler.post(() -> {
                msgList.removeAllViews();
                if (msgs.isEmpty()) {
                    addEmptyState();
                } else {
                    for (Message m : msgs) msgList.addView(buildMsgBubble(m));
                }
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            });
        });
    }

    private void addEmptyState() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(40, 100, 40, 40);

        TextView icon = new TextView(this);
        icon.setText("🔒");
        icon.setTextSize(40);
        icon.setGravity(Gravity.CENTER);
        box.addView(icon);

        TextView msg = new TextView(this);
        msg.setText("Mesajlar uçtan uca şifreli\nMesh ağı üzerinden iletiliyor");
        msg.setTextColor(0xFF6E7681);
        msg.setTextSize(13);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, 16, 0, 0);
        box.addView(msg);

        msgList.addView(box);
    }

    private View buildMsgBubble(Message msg) {
        boolean isOut = msg.isOutgoing(myNumber);

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setGravity(isOut ? Gravity.END : Gravity.START);
        LinearLayout.LayoutParams wP = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wP.setMargins(0, 3, 0, 3);
        wrapper.setLayoutParams(wP);

        TextView bubble = new TextView(this);
        bubble.setText(msg.clearText);
        bubble.setTextColor(isOut ? 0xFFAFF5C3 : 0xFFE6EDF3);
        bubble.setTextSize(14);
        bubble.setPadding(22, 13, 22, 13);
        bubble.setLineSpacing(4, 1);
        bubble.setTextIsSelectable(true);

        android.graphics.drawable.GradientDrawable bBg =
            new android.graphics.drawable.GradientDrawable();
        bBg.setColor(isOut ? 0xFF1A4731 : 0xFF21262D);
        bBg.setCornerRadii(isOut
            ? new float[]{24, 24, 4, 4, 24, 24, 24, 24}
            : new float[]{4, 4, 24, 24, 24, 24, 24, 24});
        bubble.setBackground(bBg);

        LinearLayout.LayoutParams bP = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bP.setMarginStart(isOut ? 72 : 0);
        bP.setMarginEnd(isOut ? 0 : 72);
        bubble.setLayoutParams(bP);
        wrapper.addView(bubble);

        TextView tvMeta = new TextView(this);
        tvMeta.setText(msg.getTimeString() + (isOut ? "  " + msg.getStatusIcon() : ""));
        tvMeta.setTextColor(0xFF6E7681);
        tvMeta.setTextSize(10);
        LinearLayout.LayoutParams metaP = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        metaP.setMarginStart(isOut ? 0 : 6);
        metaP.setMarginEnd(isOut ? 6 : 0);
        metaP.topMargin = 3;
        tvMeta.setLayoutParams(metaP);
        wrapper.addView(tvMeta);

        return wrapper;
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        Message msg = Message.createText(myNumber, contact.omniNumber, text);
        etMessage.setText("");

        Executors.newSingleThreadExecutor().execute(() -> {
            OmniDatabase.get(this).messageDao().insert(msg);
            handler.post(this::loadMessages);

            Intent intent = new Intent(this, OmniBackgroundService.class);
            intent.setAction(OmniBackgroundService.ACTION_SEND_MESSAGE);
            intent.putExtra("target", contact.omniNumber);
            intent.putExtra("payload", text.getBytes());
            intent.putExtra("msg_id", msg.msgId);
            startService(intent);

            handler.postDelayed(() ->
                Executors.newSingleThreadExecutor().execute(() -> {
                    OmniDatabase.get(this).messageDao().markSent(msg.msgId);
                    handler.post(this::loadMessages);
                }), 800);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(msgReceiver); } catch (Exception ignored) {}
    }
}
