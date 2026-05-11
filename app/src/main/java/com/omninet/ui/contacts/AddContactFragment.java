package com.omninet.ui.contacts;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Contact;
import com.omninet.network.NumberManager;
import java.util.concurrent.Executors;

public class AddContactFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0D1117);
        root.setPadding(40, 40, 40, 40);

        // Başlık
        TextView header = new TextView(getContext());
        header.setText("Kişi Ekle");
        header.setTextColor(0xFFE6EDF3);
        header.setTextSize(20);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(0, 0, 0, 32);
        root.addView(header);

        // Ad giriş
        TextView lblName = new TextView(getContext());
        lblName.setText("Ad Soyad");
        lblName.setTextColor(0xFF6E7681);
        lblName.setTextSize(12);
        lblName.setPadding(0, 0, 0, 8);
        root.addView(lblName);

        EditText etName = new EditText(getContext());
        etName.setHint("örn: Ahmet Yılmaz");
        etName.setHintTextColor(0xFF484F58);
        etName.setTextColor(0xFFC9D1D9);
        etName.setTextSize(14);
        etName.setPadding(28, 24, 28, 24);
        android.graphics.drawable.GradientDrawable nameBg =
            new android.graphics.drawable.GradientDrawable();
        nameBg.setColor(0xFF161B22);
        nameBg.setCornerRadius(20f);
        nameBg.setStroke(1, 0xFF30363D);
        etName.setBackground(nameBg);
        LinearLayout.LayoutParams nameParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.setMargins(0, 0, 0, 24);
        etName.setLayoutParams(nameParams);
        root.addView(etName);

        // Numara giriş
        TextView lblNumber = new TextView(getContext());
        lblNumber.setText("OmniNet Numarası");
        lblNumber.setTextColor(0xFF6E7681);
        lblNumber.setTextSize(12);
        lblNumber.setPadding(0, 0, 0, 8);
        root.addView(lblNumber);

        LinearLayout numberRow = new LinearLayout(getContext());
        numberRow.setOrientation(LinearLayout.HORIZONTAL);
        numberRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 0, 0, 8);
        numberRow.setLayoutParams(rowParams);

        // +777 prefix etiketi
        TextView tvPrefix = new TextView(getContext());
        tvPrefix.setText("+777 3543");
        tvPrefix.setTextColor(0xFF2EA043);
        tvPrefix.setTextSize(14);
        tvPrefix.setTypeface(null, android.graphics.Typeface.BOLD);
        tvPrefix.setPadding(20, 24, 16, 24);
        android.graphics.drawable.GradientDrawable prefixBg =
            new android.graphics.drawable.GradientDrawable();
        prefixBg.setColor(0xFF0F3D1F);
        prefixBg.setCornerRadius(20f);
        prefixBg.setStroke(1, 0xFF238636);
        tvPrefix.setBackground(prefixBg);
        LinearLayout.LayoutParams prefixParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        prefixParams.setMargins(0, 0, 12, 0);
        tvPrefix.setLayoutParams(prefixParams);
        numberRow.addView(tvPrefix);

        // Numara suffix girişi
        EditText etNumber = new EditText(getContext());
        etNumber.setHint("XXXX XXXX");
        etNumber.setHintTextColor(0xFF484F58);
        etNumber.setTextColor(0xFFC9D1D9);
        etNumber.setTextSize(14);
        etNumber.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etNumber.setPadding(24, 24, 24, 24);
        android.graphics.drawable.GradientDrawable numBg =
            new android.graphics.drawable.GradientDrawable();
        numBg.setColor(0xFF161B22);
        numBg.setCornerRadius(20f);
        numBg.setStroke(1, 0xFF30363D);
        etNumber.setBackground(numBg);
        etNumber.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        numberRow.addView(etNumber);
        root.addView(numberRow);

        // Numara formatı ipucu
        TextView tvHint = new TextView(getContext());
        tvHint.setText("Tam numara: +777 3543 XXXX XXXX");
        tvHint.setTextColor(0xFF484F58);
        tvHint.setTextSize(11);
        tvHint.setPadding(0, 4, 0, 32);
        root.addView(tvHint);

        // Durum mesajı
        TextView tvStatus = new TextView(getContext());
        tvStatus.setTextSize(13);
        tvStatus.setGravity(android.view.Gravity.CENTER);
        tvStatus.setPadding(0, 0, 0, 16);
        tvStatus.setVisibility(View.GONE);
        root.addView(tvStatus);

        // Ekle butonu
        Button btnAdd = new Button(getContext());
        btnAdd.setText("Kişiyi Ekle");
        btnAdd.setTextColor(0xFFFFFFFF);
        btnAdd.setTextSize(15);
        btnAdd.setTypeface(null, android.graphics.Typeface.BOLD);
        android.graphics.drawable.GradientDrawable addBg =
            new android.graphics.drawable.GradientDrawable();
        addBg.setColor(0xFF238636);
        addBg.setCornerRadius(24f);
        btnAdd.setBackground(addBg);
        btnAdd.setPadding(0, 28, 0, 28);
        btnAdd.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));

        btnAdd.setOnClickListener(v -> {
            String name   = etName.getText().toString().trim();
            String suffix = etNumber.getText().toString().trim()
                .replaceAll("\\s+", "");

            // Doğrulama
            if (name.isEmpty()) {
                tvStatus.setText("⚠ Ad girin");
                tvStatus.setTextColor(0xFFD29922);
                tvStatus.setVisibility(View.VISIBLE);
                return;
            }
            if (suffix.isEmpty() || suffix.length() < 8) {
                tvStatus.setText("⚠ Geçerli numara girin (8 hane)");
                tvStatus.setTextColor(0xFFD29922);
                tvStatus.setVisibility(View.VISIBLE);
                return;
            }

            String fullNumber = "+777" +
                com.omninet.core.OmniConstitution.NETWORK_PREFIX +
                suffix;

            // Kendi numaramla aynı mı?
            String myNumber = NumberManager.getOrCreate(requireContext());
            if (fullNumber.equals(myNumber)) {
                tvStatus.setText("⚠ Kendi numaranızı ekleyemezsiniz");
                tvStatus.setTextColor(0xFFF85149);
                tvStatus.setVisibility(View.VISIBLE);
                return;
            }

            // Veritabanına kaydet
            Contact contact = new Contact(fullNumber, name);
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText("⬡ Kişi ekleniyor...");
            tvStatus.setTextColor(0xFF6E7681);

            Executors.newSingleThreadExecutor().execute(() -> {
                OmniDatabase.get(requireContext())
                    .contactDao().insert(contact);

                requireActivity().runOnUiThread(() -> {
                    tvStatus.setText("✓ " + name + " eklendi! (" +
                        NumberManager.format(fullNumber) + ")");
                    tvStatus.setTextColor(0xFF2EA043);
                    etName.setText("");
                    etNumber.setText("");

                    // 2 saniye sonra geri dön
                    new android.os.Handler(
                        android.os.Looper.getMainLooper())
                        .postDelayed(() ->
                            requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack(), 2000);
                });
            });
        });

        root.addView(btnAdd);

        // İptal
        Button btnCancel = new Button(getContext());
        btnCancel.setText("İptal");
        btnCancel.setTextColor(0xFF6E7681);
        btnCancel.setTextSize(14);
        btnCancel.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        LinearLayout.LayoutParams cancelParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        cancelParams.setMargins(0, 12, 0, 0);
        btnCancel.setLayoutParams(cancelParams);
        btnCancel.setOnClickListener(v ->
            requireActivity().getSupportFragmentManager().popBackStack());
        root.addView(btnCancel);

        return root;
    }
}
