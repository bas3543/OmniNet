package com.omninet.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.omninet.R;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Chat;
import java.util.List;

public class ChatsFragment extends Fragment {
    private RecyclerView chatsRecyclerView;
    private ChatsAdapter adapter;
    private FloatingActionButton fabNewChat;
    private OmniDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatsRecyclerView = view.findViewById(R.id.chatsRecyclerView);
        fabNewChat = view.findViewById(R.id.fabNewChat);
        database = OmniDatabase.getInstance(requireContext());

        setupRecyclerView();
        loadChats();

        fabNewChat.setOnClickListener(v -> startNewChat());
    }

    private void setupRecyclerView() {
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ChatsAdapter(requireContext());
        chatsRecyclerView.setAdapter(adapter);
    }

    private void loadChats() {
        new Thread(() -> {
            List<Chat> chats = database.chatDao().getAllChats();
            requireActivity().runOnUiThread(() -> adapter.setChats(chats));
        }).start();
    }

    private void startNewChat() {
    }
}
