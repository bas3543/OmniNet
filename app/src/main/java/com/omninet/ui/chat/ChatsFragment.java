package com.omninet.ui.chat;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Contact;
import com.omninet.data.models.Message;
import com.omninet.network.NumberManager;
import com.omninet.ui.contacts.AddContactFragment;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatsFragment extends Fragment {

    private LinearLayout chatList;
    private String myNumber;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {

        myNumber = NumberManager.getOrCreate(requireContext());

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0D1117);

        // Üst bar
        LinearLayout topBar = new LinearLayout(getContext());
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(0xFF161B22);
        topBar.setPadding(28, 24, 28, 24);
        topBar.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText("Mesajlar");
        tvTitle.setTextColor(0xFFE6EDF3);
        tvTitle.setTextSize(18);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        topBar.addView(tvTitle);

        // Kişi ekle butonu
        Button btnAdd = new Button(getContext());
        btnAdd.setText("+ Kişi");
        btnAdd.setTextColor(0xFF2EA043);
        btnAdd.setTextSize(12);
        android.graphics.drawable.GradientDrawable addBg =
            new android.graphics.drawable.GradientDrawable();
        addBg.setColor(0xFF0F3D1F);
        addBg.setCornerRadius(16f);
        addBg.setStroke(1, 0xFF238636);
        btnAdd.setBackground(addBg);
        btnAdd.setPadding(24, 12, 24, 12);
        btnAdd.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(com.omninet.R.id.fragment_container,
                    new AddContactFragment())
                .addToBackStack(null)
                .commit();
        });
        topBar.addView(btnAdd);
        root.addView(topBar);

        // Benim numaram
        TextView tvMyNumber = new TextView(getContext());
        tvMyNumber.setText("Numaran: " + NumberManager.format(myNumber));
        tvMyNumber.setTextColor(0xFF2EA043);
        tvMyNumber.setTextSize(11);
        tvMyNumber.setPadding(28, 12, 28, 12);
        tvMyNumber.setBackgroundColor(0xFF0F3D1F);
        root.addView(tvMyNumber);

        // Arama kutusu
        EditText etSearch = new EditText(getContext());
        etSearch.setHint("Kişi veya mesaj ara...");
        etSearch.setHintTextColor(0xFF484F58);
        etSearch.setTextColor(0xFFC9D1D9);
        etSearch.setTextSize(13);
        etSearch.setPadding(28, 20, 28, 20);
        android.graphics.drawable.GradientDrawable searchBg =
            new android.graphics.drawable.GradientDrawable();
        searchBg.setColor(0xFF161B22);
        searchBg.setCornerRadius(0f);
        searchBg.setStroke(0, 0xFF21262D);
        etSearch.setBackground(searchBg);
        LinearLayout.LayoutParams searchParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        etSearch.setLayoutParams(searchParams);
        root.addView(etSearch);

        // Kişi listesi
        ScrollView scroll = new ScrollView(getContext());
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        chatList = new LinearLayout(getContext());
        chatList.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(chatList);
        root.addView(scroll);

        // Kişileri yükle
        loadContacts();

        // Arama filtresi
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterContacts(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        return root;
    }

    private void loadContacts() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Contact> contacts = OmniDatabase.get(requireContext())
                .contactDao().getAllSync();

            handler.post(() -> {
                chatList.removeAllViews();
                if (contacts.isEmpty()) {
                    showEmptyState();
                } else {
                    for (Contact c : contacts) {
                        chatList.addView(buildContactRow(c));
                    }
                }
            });
        });
    }

    private void filterContacts(String query) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Contact> all = OmniDatabase.get(requireContext())
                .contactDao().getAllSync();

            handler.post(() -> {
                chatList.removeAllViews();
                for (Contact c : all) {
                    if (query.isEmpty() ||
                        c.displayName.toLowerCase().contains(query.toLowerCase()) ||
                        c.omniNumber.contains(query)) {
                        chatList.addView(buildContactRow(c));
                    }
                }
            });
        });
    }

    private View buildContactRow(Contact contact) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(28, 20, 28, 20);
        row.setClickable(true);
        row.setFocusable(true);

        android.graphics.drawable.GradientDrawable rowBg =
            new android.graphics.drawable.GradientDrawable();
        rowBg.setColor(0x00000000);
        row.setBackground(rowBg);

        row.setOnClickListener(v -> openChat(contact));

        // Avatar
        LinearLayout avWrap = new LinearLayout(getContext());
        avWrap.setLayoutParams(new LinearLayout.LayoutParams(96, 96));

        TextView av = new TextView(getContext());
        av.setText(contact.initials != null ? contact.initials : "??");
        av.setTextColor(0xFFFFFFFF);
        av.setTextSize(16);
        av.setTypeface(null, android.graphics.Typeface.BOLD);
        av.setGravity(android.view.Gravity.CENTER);
        av.setLayoutParams(new LinearLayout.LayoutParams(96, 96));

        android.graphics.drawable.GradientDrawable avBg =
            new android.graphics.drawable.GradientDrawable();
        avBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        try {
            avBg.setColor(android.graphics.Color.parseColor(
                contact.avatarColor != null ? contact.avatarColor : "#238636"));
        } catch (Exception e) {
            avBg.setColor(0xFF238636);
        }
        av.setBackground(avBg);
        avWrap.addView(av);

        // Online nokta
        if (contact.isOnline) {
            View dot = new View(getContext());
            android.graphics.drawable.GradientDrawable dotBg =
                new android.graphics.drawable.GradientDrawable();
            dotBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            dotBg.setColor(0xFF2EA043);
            dot.setBackground(dotBg);
            LinearLayout.LayoutParams dotP =
                new LinearLayout.LayoutParams(20, 20);
            dotP.setMargins(-20, 60, 0, 0);
            dot.setLayoutParams(dotP);
            avWrap.addView(dot);
        }

        row.addView(avWrap);

        // Bilgi
        LinearLayout info = new LinearLayout(getContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(24, 0, 0, 0);
        info.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(getContext());
        String nameText = contact.displayName;
        if (contact.isFounder) nameText += " ⭐";
        tvName.setText(nameText);
        tvName.setTextColor(0xFFE6EDF3);
        tvName.setTextSize(14);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(tvName);

        TextView tvNumber = new TextView(getContext());
        tvNumber.setText(NumberManager.format(contact.omniNumber));
        tvNumber.setTextColor(0xFF2EA043);
        tvNumber.setTextSize(11);
        tvNumber.setPadding(0, 4, 0, 0);
        info.addView(tvNumber);

        // Son mesaj
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Message> msgs = OmniDatabase.get(requireContext())
                .messageDao().getThreadSync(contact.omniNumber);
            handler.post(() -> {
                if (!msgs.isEmpty()) {
                    Message last = msgs.get(msgs.size() - 1);
                    TextView tvLast = new TextView(getContext());
                    boolean isOut = last.isOutgoing(myNumber);
                    tvLast.setText((isOut ? "Sen: " : "") +
                        (last.clearText != null ? last.clearText : "📎 Medya"));
                    tvLast.setTextColor(0xFF6E7681);
                    tvLast.setTextSize(12);
                    tvLast.setPadding(0, 3, 0, 0);
                    tvLast.setSingleLine(true);
                    tvLast.setEllipsize(
                        android.text.TextUtils.TruncateAt.END);
                    info.addView(tvLast);
                }
            });
        });

        row.addView(info);

        // Sağ taraf — zaman + okunmamış
        LinearLayout right = new LinearLayout(getContext());
        right.setOrientation(LinearLayout.VERTICAL);
        right.setGravity(android.view.Gravity.END);
        right.setPadding(16, 0, 0, 0);

        TextView tvTime = new TextView(getContext());
        tvTime.setTextColor(0xFF6E7681);
        tvTime.setTextSize(11);
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Message> msgs = OmniDatabase.get(requireContext())
                .messageDao().getThreadSync(contact.omniNumber);
            int unread = OmniDatabase.get(requireContext())
                .messageDao().getUnreadCount(contact.omniNumber, myNumber);
            handler.post(() -> {
                if (!msgs.isEmpty()) {
                    tvTime.setText(msgs.get(msgs.size() - 1).getTimeString());
                }
                if (unread > 0) {
                    TextView badge = new TextView(getContext());
                    badge.setText(String.valueOf(unread));
                    badge.setTextColor(0xFFFFFFFF);
                    badge.setTextSize(10);
                    badge.setGravity(android.view.Gravity.CENTER);
                    badge.setPadding(12, 4, 12, 4);
                    android.graphics.drawable.GradientDrawable badgeBg =
                        new android.graphics.drawable.GradientDrawable();
                    badgeBg.setShape(
                        android.graphics.drawable.GradientDrawable.OVAL);
                    badgeBg.setColor(0xFF238636);
                    badge.setBackground(badgeBg);
                    LinearLayout.LayoutParams bp =
                        new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    bp.setMargins(0, 8, 0, 0);
                    badge.setLayoutParams(bp);
                    right.addView(badge);
                }
            });
        });
        right.addView(tvTime);
        row.addView(right);

        // Alt çizgi
        LinearLayout wrapper = new LinearLayout(getContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(row);
        View line = new View(getContext());
        line.setBackgroundColor(0xFF21262D);
        line.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 1));
        wrapper.addView(line);

        return wrapper;
    }

    private void showEmptyState() {
        LinearLayout empty = new LinearLayout(getContext());
        empty.setOrientation(LinearLayout.VERTICAL);
        empty.setGravity(android.view.Gravity.CENTER);
        empty.setPadding(40, 120, 40, 40);

        TextView tvIcon = new TextView(getContext());
        tvIcon.setText("💬");
        tvIcon.setTextSize(48);
        tvIcon.setGravity(android.view.Gravity.CENTER);
        empty.addView(tvIcon);

        TextView tvMsg = new TextView(getContext());
        tvMsg.setText("Henüz kişi yok");
        tvMsg.setTextColor(0xFFE6EDF3);
        tvMsg.setTextSize(16);
        tvMsg.setTypeface(null, android.graphics.Typeface.BOLD);
        tvMsg.setGravity(android.view.Gravity.CENTER);
        tvMsg.setPadding(0, 16, 0, 8);
        empty.addView(tvMsg);

        TextView tvSub = new TextView(getContext());
        tvSub.setText("+ Kişi butonuna tıklayarak\nOmniNet numarasıyla kişi ekle");
        tvSub.setTextColor(0xFF6E7681);
        tvSub.setTextSize(13);
        tvSub.setGravity(android.view.Gravity.CENTER);
        empty.addView(tvSub);

        chatList.addView(empty);
    }

    private void openChat(Contact contact) {
        ChatActivity.start(requireActivity(), contact);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadContacts();
    }
}
