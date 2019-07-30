package com.dasta.eemapp.activity.Client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dasta.eemapp.R;

/**
 * Created by Mohamed on 25/03/2018.
 */

public class Video extends Activity {

    String fimg;
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_client_video);

        fimg = getIntent().getStringExtra("Video_fimg");

        final ProgressDialog pd = ProgressDialog.show(Video.this, "", "Loading...", true);
        pd.setCancelable(true);

        webView = (WebView) findViewById(R.id.webViewVideo);
        webView.loadUrl(fimg);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.clearHistory();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (pd.isShowing() && pd != null) {
                    pd.dismiss();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
