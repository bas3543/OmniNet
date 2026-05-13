package com.omninet.ui.chat;

import android.content.*;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Contact;
import com.omninet.data.models.Message;
import com.omninet.network.NumberManager;
import com.omninet.ui.contacts.AddContactFragment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ChatsFragment extends Fragment {

    private LinearLayout chatList;
    private String myNumber;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<Contact> allContacts = new ArrayList<>();
    private BroadcastReceiver msgReceiver;

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
        topBar.setPadding(24, 52, 24, 20);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText("Mesajlar");
        tvTitle.setTextColor(0xFFE6EDF3);
        tvTitle.setTextSize(20);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        topBar.addView(tvTitle);

        Button btnAdd = new Button(getContext());
        btnAdd.setText("+ Kişi");
        btnAdd.setTextColor(0xFF2EA043);
        btnAdd.setTextSize(12);
        android.graphics.drawable.GradientDrawable addBg =
            new android.graphics.drawable.GradientDrawable();
        addBg.setColor(0xFF0F3D1F);
        addBg.setCornerRadius(18f);
        addBg.setStroke(1, 0xFF238636);
        btnAdd.setBackground(addBg);
        btnAdd.setPadding(24, 12, 24, 12);
        btnAdd.setOnClickListener(v ->
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(com.omninet.R.id.fragment_container, new AddContactFragment())
                .addToBackStack(null)
                .commit());
        topBar.addView(btnAdd);
        root.addView(topBar);

        // Benim numaram
        TextView tvMyNumber = new TextView(getContext());
        tvMyNumber.setText("📱 Numaran: " + NumberManager.format(myNumber));
        tvMyNumber.setTextColor(0xFF2EA043);
        tvMyNumber.setTextSize(11);
        tvMyNumber.setPadding(28, 10, 28, 10);
        tvMyNumber.setBackgroundColor(0xFF0F3D1F);
        root.addView(tvMyNumber);

        // Arama kutusu
        EditText etSearch = new EditText(getContext());
        etSearch.setHint("🔍  Kişi ara...");
        etSearch.setHintTextColor(0xFF484F58);
        etSearch.setTextColor(0xFFC9D1D9);
        etSearch.setTextSize(13);
        etSearch.setPadding(28, 18, 28, 18);
        etSearch.setSingleLine(true);
        android.graphics.drawable.GradientDrawable searchBg =
            new android.graphics.drawable.GradientDrawable();
        searchBg.setColor(0xFF161B22);
        etSearch.setBackground(searchBg);
        etSearch.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(etSearch);

        // Liste
        ScrollView scroll = new ScrollView(getContext());
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        chatList = new LinearLayout(getContext());
        chatList.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(chatList);
        root.addView(scroll);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {
                filterList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadContacts();
        return root;
    }

    private void loadContacts() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Contact> contacts = OmniDatabase.get(requireContext())
                .contactDao().getAllSync();
            handler.post(() -> {
                allContacts = contacts;
                renderList(contacts);
            });
        });
    }

    private void filterList(String query) {
        if (query.isEmpty()) { renderList(allContacts); return; }
        List<Contact> filtered = new ArrayList<>();
        String q = query.toLowerCase();
        for (Contact c : allContacts) {
            if (c.displayName.toLowerCase().contains(q) || c.omniNumber.contains(q))
                filtered.add(c);
        }
        renderList(filtered);
    }

    private void renderList(List<Contact> contacts) {
        if (chatList == null) return;
        chatList.removeAllViews();
        if (contacts.isEmpty()) { showEmptyState(); return; }
        for (Contact c : contacts) chatList.addView(buildContactRow(c));
    }

    private View buildContactRow(Contact contact) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(24, 18, 24, 18);
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(v -> openChat(contact));

        // Avatar
        FrameLayout avFrame = new FrameLayout(getContext());
        LinearLayout.LayoutParams frameP = new LinearLayout.LayoutParams(88, 88);
        frameP.setMargins(0, 0, 20, 0);
        avFrame.setLayoutParams(frameP);

        TextView av = new TextView(getContext());
        av.setText(contact.initials != null ? contact.initials : "??");
        av.setTextColor(0xFFFFFFFF);
        av.setTextSize(16);
        av.setTypeface(null, android.graphics.Typeface.BOLD);
        av.setGravity(Gravity.CENTER);
        av.setLayoutParams(new FrameLayout.LayoutParams(88, 88));
        android.graphics.drawable.GradientDrawable avBg =
            new android.graphics.drawable.GradientDrawable();
        avBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        try {
            avBg.setColor(android.graphics.Color.parseColor(
                contact.avatarColor != null ? contact.avatarColor : "#238636"));
        } catch (Exception e) { avBg.setColor(0xFF238636); }
        av.setBackground(avBg);
        avFrame.addView(av);

        if (contact.isOnline) {
            View dot = new View(getContext());
            android.graphics.drawable.GradientDrawable dotBg =
                new android.graphics.drawable.GradientDrawable();
            dotBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            dotBg.setColor(0xFF2EA043);
            dot.setBackground(dotBg);
            FrameLayout.LayoutParams dotP = new FrameLayout.LayoutParams(18, 18);
            dotP.gravity = Gravity.BOTTOM | Gravity.END;
            dot.setLayoutParams(dotP);
            avFrame.addView(dot);
        }
        row.addView(avFrame);

        // İsim + mesaj
        LinearLayout info = new LinearLayout(getContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(getContext());
        tvName.setText(contact.displayName + (contact.isFounder ? " ⭐" : ""));
        tvName.setTextColor(0xFFE6EDF3);
        tvName.setTextSize(14);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(tvName);

        TextView tvNumber = new TextView(getContext());
        tvNumber.setText(NumberManager.format(contact.omniNumber));
        tvNumber.setTextColor(0xFF2EA043);
        tvNumber.setTextSize(11);
        tvNumber.setPadding(0, 3, 0, 0);
        info.addView(tvNumber);

        TextView tvLast = new TextView(getContext());
        tvLast.setTextColor(0xFF6E7681);
        tvLast.setTextSize(12);
        tvLast.setPadding(0, 3, 0, 0);
        tvLast.setSingleLine(true);
        tvLast.setEllipsize(android.text.TextUtils.TruncateAt.END);
        info.addView(tvLast);
        row.addView(info);

        // Sağ: zaman + badge
        LinearLayout right = new LinearLayout(getContext());
        right.setOrientation(LinearLayout.VERTICAL);
        right.setGravity(Gravity.END);
        right.setPadding(12, 0, 0, 0);

        TextView tvTime = new TextView(getContext());
        tvTime.setTextColor(0xFF6E7681);
        tvTime.setTextSize(11);
        right.addView(tvTime);

        TextView badge = new TextView(getContext());
        badge.setTextColor(0xFFFFFFFF);
        badge.setTextSize(10);
        badge.setGravity(Gravity.CENTER);
        badge.setPadding(10, 3, 10, 3);
        android.graphics.drawable.GradientDrawable badgeBg =
            new android.graphics.drawable.GradientDrawable();
        badgeBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        badgeBg.setColor(0xFF238636);
        badge.setBackground(badgeBg);
        badge.setVisibility(View.GONE);
        LinearLayout.LayoutParams badgeP = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeP.topMargin = 6;
        badge.setLayoutParams(badgeP);
        right.addView(badge);
        row.addView(right);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Message> msgs = OmniDatabase.get(requireContext())
                .messageDao().getThreadSync(contact.omniNumber);
            int unread = OmniDatabase.get(requireContext())
                .messageDao().getUnreadCount(contact.omniNumber, myNumber);
            handler.post(() -> {
                if (!isAdded()) return;
                if (!msgs.isEmpty()) {
                    Message last = msgs.get(msgs.size() - 1);
                    tvLast.setText((last.isOutgoing(myNumber) ? "Sen: " : "") +
                        (last.clearText != null ? last.clearText : "📎 Medya"));
                    tvTime.setText(last.getTimeString());
                }
                if (unread > 0) {
                    badge.setText(String.valueOf(unread));
                    badge.setVisibility(View.VISIBLE);
                }
            });
        });

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
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(40, 120, 40, 40);

        TextView icon = new TextView(getContext());
        icon.setText("💬");
        icon.setTextSize(48);
        icon.setGravity(Gravity.CENTER);
        empty.addView(icon);

        TextView tvMsg = new TextView(getContext());
        tvMsg.setText("Henüz kişi yok");
        tvMsg.setTextColor(0xFFE6EDF3);
        tvMsg.setTextSize(17);
        tvMsg.setTypeface(null, android.graphics.Typeface.BOLD);
        tvMsg.setGravity(Gravity.CENTER);
        tvMsg.setPadding(0, 16, 0, 8);
        empty.addView(tvMsg);

        TextView tvSub = new TextView(getContext());
        tvSub.setText("\"+ Kişi\" butonuna tıklayarak\nOmniNet numarasıyla kişi ekle");
        tvSub.setTextColor(0xFF6E7681);
        tvSub.setTextSize(13);
        tvSub.setGravity(Gravity.CENTER);
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
        msgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) { loadContacts(); }
        };
        IntentFilter f = new IntentFilter("com.omninet.MESSAGE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(msgReceiver, f, Context.RECEIVER_NOT_EXPORTED);
        } else {
            requireContext().registerReceiver(msgReceiver, f);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (msgReceiver != null) requireContext().unregisterReceiver(msgReceiver);
        } catch (Exception ignored) {}
    }
}
