package com.omninet.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.omninet.R;
import com.omninet.core.OmniID;
import com.omninet.data.models.Message;
import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private List<Message> messages = new ArrayList<>();
    private Context context;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public MessagesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.senderId.equals(OmniID.get())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = viewType == VIEW_TYPE_SENT ? R.layout.item_message_sent : R.layout.item_message_received;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp, status;
        LinearLayout messageContainer;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestamp);
            status = itemView.findViewById(R.id.status);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }

        void bind(Message message) {
            messageText.setText(message.messageText);
            timestamp.setText(message.getTimeFormatted());
            if (status != null && getItemViewType() == VIEW_TYPE_SENT) {
                status.setText(message.status);
            }
        }
    }
}
