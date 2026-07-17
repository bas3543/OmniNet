package com.omninet.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.omninet.R;
import com.omninet.data.models.Chat;
import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {
    private List<Chat> chats = new ArrayList<>();
    private Context context;

    public ChatsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatName, lastMessage, timestamp, unreadBadge;
        ImageView profilePic;
        LinearLayout chatItem;

        ChatViewHolder(View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chatName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timestamp = itemView.findViewById(R.id.timestamp);
            unreadBadge = itemView.findViewById(R.id.unreadBadge);
            profilePic = itemView.findViewById(R.id.profilePic);
            chatItem = itemView.findViewById(R.id.chatItem);
        }

        void bind(Chat chat) {
            chatName.setText(chat.chatName);
            lastMessage.setText(chat.getLastMessagePreview());
            timestamp.setText(chat.getFormattedTime());

            if (chat.unreadCount > 0) {
                unreadBadge.setVisibility(View.VISIBLE);
                unreadBadge.setText(String.valueOf(chat.unreadCount));
            } else {
                unreadBadge.setVisibility(View.GONE);
            }

            chatItem.setOnClickListener(v -> openChat(chat));
        }

        private void openChat(Chat chat) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatId", chat.id);
            intent.putExtra("chatName", chat.chatName);
            context.startActivity(intent);
        }
    }
}
