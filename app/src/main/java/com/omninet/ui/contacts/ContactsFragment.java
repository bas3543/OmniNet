package com.omninet.ui.contacts;

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
import com.omninet.data.models.Contact;
import java.util.List;

public class ContactsFragment extends Fragment {
    private RecyclerView contactsRecyclerView;
    private ContactsAdapter adapter;
    private FloatingActionButton fabAddContact;
    private OmniDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView);
        fabAddContact = view.findViewById(R.id.fabAddContact);
        database = OmniDatabase.getInstance(requireContext());

        setupRecyclerView();
        loadContacts();

        fabAddContact.setOnClickListener(v -> addNewContact());
    }

    private void setupRecyclerView() {
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ContactsAdapter(requireContext());
        contactsRecyclerView.setAdapter(adapter);
    }

    private void loadContacts() {
        new Thread(() -> {
            List<Contact> contacts = database.contactDao().getAllContacts();
            requireActivity().runOnUiThread(() -> adapter.setContacts(contacts));
        }).start();
    }

    private void addNewContact() {
    }
}
