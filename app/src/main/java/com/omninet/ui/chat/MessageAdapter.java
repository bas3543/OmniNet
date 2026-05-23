package com.omninet.ui.chat;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.omninet.R;
import com.omninet.data.models.Message;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_OUT = 1;
    private static final int TYPE_IN  = 0;

    private List<Message> messages = new ArrayList<>();
    private final String myNumber;

    public MessageAdapter(String myNumber) {
        this.myNumber = myNumber;
    }

    public void setMessages(List<Message> msgs) {
        this.messages = msgs;
        notifyDataSetChanged();
    }

    public void addMessage(Message msg) {
        messages.add(msg);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isOutgoing(myNumber) ?
            TYPE_OUT : TYPE_IN;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_OUT) {
            View view = inflater.inflate(R.layout.item_message_out,
                parent, false);
            return new OutViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_in,
                parent, false);
            return new InViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                  int position) {
        Message msg = messages.get(position);
        if (holder instanceof OutViewHolder) {
            OutViewHolder vh = (OutViewHolder) holder;
            vh.tvMessage.setText(msg.clearText);
            vh.tvTime.setText(msg.getTimeString());
            vh.tvStatus.setText(msg.getStatusIcon());
        } else if (holder instanceof InViewHolder) {
            InViewHolder vh = (InViewHolder) holder;
            vh.tvMessage.setText(msg.clearText);
            vh.tvTime.setText(msg.getTimeString());
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class OutViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvStatus;
        OutViewHolder(View v) {
            super(v);
            tvMessage = v.findViewById(R.id.tv_message);
            tvTime    = v.findViewById(R.id.tv_time);
            tvStatus  = v.findViewById(R.id.tv_status);
        }
    }

    static class InViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        InViewHolder(View v) {
            super(v);
            tvMessage = v.findViewById(R.id.tv_message);
            tvTime    = v.findViewById(R.id.tv_time);
        }
    }
}
