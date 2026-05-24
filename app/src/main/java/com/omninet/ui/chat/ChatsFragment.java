package com.omninet.ui.chat;

import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.omninet.R;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Contact;
import com.omninet.data.models.Message;
import com.omninet.network.NumberManager;
import com.omninet.ui.contacts.AddContactFragment;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatsFragment extends Fragment {

    private ContactAdapter adapter;
    private String myNumber;
    private Handler handler = new Handler(Looper.getMainLooper());
    private View emptyState;
    private RecyclerView rvContacts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {
        View root = inflater.inflate(
            R.layout.fragment_chats, container, false);

        myNumber = NumberManager.getOrCreate(requireContext());

        // Numaram
        TextView tvMyNumber = root.findViewById(R.id.tv_my_number);
        tvMyNumber.setText("Numaran: " +
            NumberManager.format(myNumber));

        // RecyclerView
        rvContacts = root.findViewById(R.id.rv_contacts);
        rvContacts.setLayoutManager(
            new LinearLayoutManager(getContext()));
        adapter = new ContactAdapter(contact ->
            ChatActivity.start(requireActivity(), contact));
        rvContacts.setAdapter(adapter);

        emptyState = root.findViewById(R.id.empty_state);

        // Kişi ekle
        root.findViewById(R.id.btn_add_contact)
            .setOnClickListener(v ->
                requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,
                        new AddContactFragment())
                    .addToBackStack(null)
                    .commit());

        // Arama
        EditText etSearch = root.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(
                CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(
                CharSequence s, int st, int b, int c) {
                filterContacts(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadContacts();
        return root;
    }

    private void loadContacts() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Contact> contacts = OmniDatabase
                .get(requireContext())
                .contactDao()
                .getAllSync();

            // Son mesajları ekle
            for (Contact c : contacts) {
                List<Message> msgs = OmniDatabase
                    .get(requireContext())
                    .messageDao()
                    .getThreadSync(c.omniNumber);
                if (!msgs.isEmpty()) {
                    Message last = msgs.get(msgs.size() - 1);
                    c.status = last.clearText;
                }
            }

            handler.post(() -> {
                if (contacts.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    rvContacts.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    rvContacts.setVisibility(View.VISIBLE);
                    adapter.setContacts(contacts);
                }
            });
        });
    }

    private void filterContacts(String query) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Contact> all = OmniDatabase
                .get(requireContext())
                .contactDao()
                .getAllSync();

            List<Contact> filtered = new java.util.ArrayList<>();
            for (Contact c : all) {
                if (query.isEmpty() ||
                    c.displayName.toLowerCase()
                        .contains(query.toLowerCase()) ||
                    c.omniNumber.contains(query)) {
                    filtered.add(c);
                }
            }

            handler.post(() -> adapter.setContacts(filtered));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadContacts();
    }
}
