package com.omninet.ui.chat;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.omninet.R;
import com.omninet.data.models.Contact;
import com.omninet.network.NumberManager;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends
    RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    public interface OnContactClick {
        void onClick(Contact contact);
    }

    private List<Contact> contacts = new ArrayList<>();
    private OnContactClick listener;

    public ContactAdapter(OnContactClick listener) {
        this.listener = listener;
    }

    public void setContacts(List<Contact> list) {
        this.contacts = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                  int position) {
        Contact contact = contacts.get(position);

        // Avatar
        holder.tvAvatar.setText(
            contact.initials != null ? contact.initials : "??");
        try {
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor(
                contact.avatarColor != null ?
                contact.avatarColor : "#238636"));
            holder.tvAvatar.setBackground(bg);
        } catch (Exception ignored) {}

        // Online dot
        holder.onlineDot.setVisibility(
            contact.isOnline ? View.VISIBLE : View.GONE);

        // İsim
        String name = contact.displayName;
        if (contact.isFounder) name += " ⭐";
        holder.tvName.setText(name);

        // Numara
        holder.tvNumber.setText(
            NumberManager.format(contact.omniNumber));

        // Hop
        if (contact.hopDistance > 0) {
            holder.tvHop.setText("⬡" + contact.hopDistance);
            holder.tvHop.setVisibility(View.VISIBLE);
        } else {
            holder.tvHop.setVisibility(View.GONE);
        }

        // Tıklama
        holder.itemView.setOnClickListener(v ->
            listener.onClick(contact));
    }

    @Override
    public int getItemCount() { return contacts.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvNumber,
                 tvPreview, tvTime, tvUnread, tvHop;
        View onlineDot;

        ViewHolder(View v) {
            super(v);
            tvAvatar  = v.findViewById(R.id.tv_avatar);
            tvName    = v.findViewById(R.id.tv_name);
            tvNumber  = v.findViewById(R.id.tv_number);
            tvPreview = v.findViewById(R.id.tv_preview);
            tvTime    = v.findViewById(R.id.tv_time);
            tvUnread  = v.findViewById(R.id.tv_unread);
            tvHop     = v.findViewById(R.id.tv_hop);
            onlineDot = v.findViewById(R.id.online_dot);
        }
    }
}
