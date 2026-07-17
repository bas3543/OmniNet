package com.omninet.ui.feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.omninet.R;
import com.omninet.data.models.Status;
import java.util.ArrayList;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {
    private List<Status> statuses = new ArrayList<>();
    private Context context;

    public StatusAdapter(Context context) {
        this.context = context;
    }

    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatusViewHolder holder, int position) {
        Status status = statuses.get(position);
        holder.bind(status);
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
        notifyDataSetChanged();
    }

    class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView userName, timestamp;
        ImageView profilePic, statusMedia, viewedIndicator;
        LinearLayout statusItem;

        StatusViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            timestamp = itemView.findViewById(R.id.timestamp);
            profilePic = itemView.findViewById(R.id.profilePic);
            statusMedia = itemView.findViewById(R.id.statusMedia);
            viewedIndicator = itemView.findViewById(R.id.viewedIndicator);
            statusItem = itemView.findViewById(R.id.statusItem);
        }

        void bind(Status status) {
            userName.setText(status.userName);
            long ago = System.currentTimeMillis() - status.createdAt;
            long minutes = ago / (60 * 1000);
            timestamp.setText(minutes + "m ago");

            if (status.isViewed) {
                viewedIndicator.setAlpha(0.5f);
            } else {
                viewedIndicator.setAlpha(1.0f);
            }
        }
    }
}
