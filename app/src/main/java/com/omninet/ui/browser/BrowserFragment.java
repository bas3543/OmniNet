package com.omninet.ui.browser;

import android.os.Bundle;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class BrowserFragment extends Fragment {

    private WebView webView;
    private EditText etUrl;
    private TextView tvMode;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle saved) {

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0D1117);

        // URL Bar
        LinearLayout urlBar = new LinearLayout(getContext());
        urlBar.setOrientation(LinearLayout.HORIZONTAL);
        urlBar.setBackgroundColor(0xFF161B22);
        urlBar.setPadding(20, 16, 20, 16);
        urlBar.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Geri butonu
        Button btnBack = new Button(getContext());
        btnBack.setText("←");
        btnBack.setTextColor(0xFF58A6FF);
        btnBack.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnBack.setPadding(0, 0, 16, 0);
        btnBack.setOnClickListener(v -> {
            if (webView.canGoBack()) webView.goBack();
        });

        // URL input
        etUrl = new EditText(getContext());
        etUrl.setLayoutParams(new LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        etUrl.setHint("Adres veya arama...");
        etUrl.setHintTextColor(0xFF484F58);
        etUrl.setTextColor(0xFFC9D1D9);
        etUrl.setTextSize(12);
        etUrl.setPadding(24, 16, 24, 16);
        etUrl.setSingleLine(true);
        etUrl.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_GO);

        android.graphics.drawable.GradientDrawable inputBg =
            new android.graphics.drawable.GradientDrawable();
        inputBg.setColor(0xFF21262D);
        inputBg.setCornerRadius(20f);
        inputBg.setStroke(1, 0xFF30363D);
        etUrl.setBackground(inputBg);

        etUrl.setOnEditorActionListener((v, actionId, event) -> {
            loadUrl(etUrl.getText().toString().trim());
            return true;
        });

        // Git butonu
        Button btnGo = new Button(getContext());
        btnGo.setText("→");
        btnGo.setTextColor(0xFFFFFFFF);
        btnGo.setPadding(16, 0, 0, 0);
        android.graphics.drawable.GradientDrawable goBg =
            new android.graphics.drawable.GradientDrawable();
        goBg.setColor(0xFF238636);
        goBg.setCornerRadius(16f);
        btnGo.setBackground(goBg);
        btnGo.setOnClickListener(v ->
            loadUrl(etUrl.getText().toString().trim()));

        urlBar.addView(btnBack);
        urlBar.addView(etUrl);
        LinearLayout.LayoutParams goParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        goParams.setMargins(12, 0, 0, 0);
        btnGo.setLayoutParams(goParams);
        urlBar.addView(btnGo);
        root.addView(urlBar);

        // Mod göstergesi
        tvMode = new TextView(getContext());
        tvMode.setText("⬡ Mesh proxy · Şifreli · Reklam engellendi");
        tvMode.setTextColor(0xFF2EA043);
        tvMode.setTextSize(10);
        tvMode.setPadding(28, 8, 28, 8);
        tvMode.setBackgroundColor(0xFF0F3D1F);
        root.addView(tvMode);

        // Progress bar
        progressBar = new ProgressBar(getContext(),
            null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 6));
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        root.addView(progressBar);

        // WebView
        webView = new WebView(getContext());
        webView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                etUrl.setText(url);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                etUrl.setText(url);
                tvMode.setText("⬡ Mesh proxy · Şifreli · ✓ Yüklendi");
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        root.addView(webView);

        // Başlangıç sayfası
        webView.loadData(buildHomePage(), "text/html", "UTF-8");

        return root;
    }

    private void loadUrl(String input) {
        if (input.isEmpty()) return;
        String url;
        if (input.startsWith("http://") || input.startsWith("https://")) {
            url = input;
        } else if (input.contains(".") && !input.contains(" ")) {
            url = "https://" + input;
        } else {
            url = "https://duckduckgo.com/?q=" + android.net.Uri.encode(input);
        }
        etUrl.setText(url);
        webView.loadUrl(url);
        tvMode.setText("⬡ Yükleniyor...");

        // Klavyeyi kapat
        android.view.inputmethod.InputMethodManager imm =
            (android.view.inputmethod.InputMethodManager)
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etUrl.getWindowToken(), 0);
    }

    private String buildHomePage() {
        return "<!DOCTYPE html><html><head>" +
            "<meta name='viewport' content='width=device-width,initial-scale=1'/>" +
            "<style>body{background:#0D1117;color:#E6EDF3;font-family:sans-serif;" +
            "padding:24px;text-align:center}" +
            "h2{color:#2EA043;margin-top:40px}" +
            ".search{background:#21262D;border:1px solid #30363D;border-radius:20px;" +
            "padding:12px 20px;color:#C9D1D9;width:90%;font-size:14px;margin:20px auto;display:block}" +
            ".site{background:#161B22;border-radius:12px;padding:14px;margin:10px 0;" +
            "text-align:left;border:1px solid #21262D}" +
            ".site-title{color:#E6EDF3;font-size:14px;font-weight:bold}" +
            ".site-url{color:#2EA043;font-size:11px;margin:4px 0}" +
            ".site-desc{color:#8B949E;font-size:12px}" +
            ".badge{background:#0F3D1F;color:#2EA043;padding:2px 8px;border-radius:10px;" +
            "font-size:10px;float:right}</style></head><body>" +
            "<h2>⬡ OmniNet Tarayıcı</h2>" +
            "<p style='color:#6E7681;font-size:13px'>Mesh proxy ile güvenli internet</p>" +
            "<div style='margin:24px 0;color:#6E7681;font-size:11px;text-transform:uppercase;" +
            "letter-spacing:1px'>Son Ziyaretler</div>" +
            "<div class='site'><span class='badge'>Önbellekte</span>" +
            "<div class='site-title'>Wikipedia</div>" +
            "<div class='site-url'>https://wikipedia.org</div>" +
            "<div class='site-desc'>Özgür ansiklopedi</div></div>" +
            "<div class='site'><span class='badge'>Mesh</span>" +
            "<div class='site-title'>DuckDuckGo</div>" +
            "<div class='site-url'>https://duckduckgo.com</div>" +
            "<div class='site-desc'>Gizlilik odaklı arama</div></div>" +
            "<div class='site'><span class='badge'>Önbellekte</span>" +
            "<div class='site-title'>GitHub</div>" +
            "<div class='site-url'>https://github.com</div>" +
            "<div class='site-desc'>Kod deposu</div></div>" +
            "</body></html>";
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroyView();
    }
}
