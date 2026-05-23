package com.omninet.ui.chat;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
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
    private MessageAdapter adapter;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private Handler handler = new Handler(Looper.getMainLooper());
    private BroadcastReceiver msgReceiver;

    public static void start(Activity activity, Contact contact) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("number", contact.omniNumber);
        intent.putExtra("name",   contact.displayName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myNumber = NumberManager.getOrCreate(this);

        String number = getIntent().getStringExtra("number");
        String name   = getIntent().getStringExtra("name");

        // Views
        etMessage = findViewById(R.id.et_message);
        rvMessages = findViewById(R.id.rv_messages);

        // RecyclerView
        adapter = new MessageAdapter(myNumber);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        rvMessages.setLayoutManager(llm);
        rvMessages.setAdapter(adapter);

        // Gönder butonu
        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());

        // Geri butonu
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Kişiyi yükle
        Executors.newSingleThreadExecutor().execute(() -> {
            contact = OmniDatabase.get(this).contactDao()
                .getByNumber(number);
            if (contact == null) {
                contact = new Contact(number,
                    name != null ? name : number);
            }
            handler.post(() -> {
                bindContact();
                loadMessages();
            });
        });

        // Gelen mesaj dinle
        msgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                String from = intent.getStringExtra("from");
                if (contact != null &&
                    contact.omniNumber.equals(from)) {
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
    }

    private void bindContact() {
        if (contact == null) return;

        TextView tvName   = findViewById(R.id.tv_name);
        TextView tvNumber = findViewById(R.id.tv_number);
        TextView tvAvatar = findViewById(R.id.tv_avatar);
        TextView tvMesh   = findViewById(R.id.tv_mesh);

        tvName.setText(contact.displayName +
            (contact.isFounder ? " ⭐" : ""));
        tvNumber.setText(NumberManager.format(contact.omniNumber));
        tvAvatar.setText(contact.initials != null ?
            contact.initials : "??");

        if (contact.avatarColor != null) {
            try {
                android.graphics.drawable.GradientDrawable bg =
                    new android.graphics.drawable.GradientDrawable();
                bg.setShape(
                    android.graphics.drawable.GradientDrawable.OVAL);
                bg.setColor(android.graphics.Color.parseColor(
                    contact.avatarColor));
                tvAvatar.setBackground(bg);
            } catch (Exception ignored) {}
        }

        tvMesh.setText("⬡ Mesh · AES-256 · " +
            (contact.hopDistance > 0 ?
                contact.hopDistance + " atlama" : "Direkt bağlı"));

        // Arama butonu
        findViewById(R.id.btn_call).setOnClickListener(v ->
            Toast.makeText(this,
                "📞 " + contact.displayName + " aranıyor...",
                Toast.LENGTH_SHORT).show());

        // Video butonu
        findViewById(R.id.btn_video).setOnClickListener(v ->
            Toast.makeText(this,
                "📹 Görüntülü arama başlatılıyor...",
                Toast.LENGTH_SHORT).show());

        // Okundu işaretle
        Executors.newSingleThreadExecutor().execute(() ->
            OmniDatabase.get(this).messageDao()
                .markAllRead(contact.omniNumber));
    }

    private void loadMessages() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Message> msgs = OmniDatabase.get(this)
                .messageDao()
                .getThreadSync(contact.omniNumber);
            handler.post(() -> {
                adapter.setMessages(msgs);
                if (!msgs.isEmpty()) {
                    rvMessages.scrollToPosition(
                        msgs.size() - 1);
                }
            });
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        Message msg = Message.createText(
            myNumber, contact.omniNumber, text);
        etMessage.setText("");

        // Önce UI'ya ekle
        adapter.addMessage(msg);
        rvMessages.scrollToPosition(
            adapter.getItemCount() - 1);

        // DB'ye kaydet ve mesh'e gönder
        Executors.newSingleThreadExecutor().execute(() -> {
            OmniDatabase.get(this).messageDao().insert(msg);

            // Mesh servisine gönder
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(msgReceiver); }
        catch (Exception ignored) {}
    }
}
