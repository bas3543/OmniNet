package com.omninet.ui.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.omninet.R;
import com.omninet.data.models.Contact;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<Contact> contacts = new ArrayList<>();
    private Context context;

    public ContactsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, status, phoneNumber;
        ImageView profilePic, statusIndicator;
        LinearLayout contactItem;

        ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            status = itemView.findViewById(R.id.status);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            profilePic = itemView.findViewById(R.id.profilePic);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            contactItem = itemView.findViewById(R.id.contactItem);
        }

        void bind(Contact contact) {
            contactName.setText(contact.displayName);
            phoneNumber.setText(contact.phoneNumber);
            status.setText(contact.status != null ? contact.status : "Hey there!");

            if (contact.isOnline) {
                statusIndicator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            } else {
                statusIndicator.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            }
        }
    }
}
