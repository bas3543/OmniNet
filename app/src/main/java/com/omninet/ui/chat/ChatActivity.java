package com.omninet.ui.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.omninet.R;
import com.omninet.core.OmniID;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Chat;
import com.omninet.data.models.Message;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private TextView chatTitle;
    private MessagesAdapter adapter;
    private int chatId;
    private OmniDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatTitle = findViewById(R.id.chatTitle);
        database = OmniDatabase.getInstance(this);

        chatId = getIntent().getIntExtra("chatId", -1);
        String name = getIntent().getStringExtra("chatName");
        chatTitle.setText(name);

        setupRecyclerView();
        loadMessages();
        setupSendButton();
    }

    private void setupRecyclerView() {
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(this);
        messagesRecyclerView.setAdapter(adapter);
    }

    private void loadMessages() {
        new Thread(() -> {
            List<Message> messages = database.messageDao().getMessagesByChat(chatId);
            runOnUiThread(() -> {
                adapter.setMessages(messages);
                if (messages.size() > 0) {
                    messagesRecyclerView.scrollToPosition(messages.size() - 1);
                }
            });
        }).start();
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        Message message = new Message();
        message.chatId = chatId;
        message.senderId = OmniID.get();
        message.senderName = "You";
        message.messageText = text;
        message.mediaType = "text";
        message.timestamp = System.currentTimeMillis();
        message.status = "sent";

        new Thread(() -> {
            database.messageDao().insert(message);
            Chat chat = database.chatDao().getChatById(chatId);
            if (chat != null) {
                chat.lastMessage = text;
                chat.lastMessageTime = System.currentTimeMillis();
                database.chatDao().update(chat);
            }
            runOnUiThread(() -> {
                messageInput.setText("");
                loadMessages();
            });
        }).start();
    }
}
