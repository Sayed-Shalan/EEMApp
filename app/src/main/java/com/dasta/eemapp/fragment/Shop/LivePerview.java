package com.dasta.eemapp.fragment.Shop;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Shop.*;
import com.dasta.eemapp.activity.Shop.Home;

/**
 * Created by Mohamed on 27/08/2017.
 */

public class LivePerview extends Fragment {

    private WebView webView;

    public static int flag = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_live, null, false);
        flag = 1;
        setRetainInstance(true);
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Loading...", true);
        pd.setCancelable(true);

        String url = "http://dasta.net/eem.store/index.php";
        webView = (WebView) view.findViewById(R.id.webView1);
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {


                if (pd.isShowing() && pd != null) {
                    pd.dismiss();
                }
            }
        });

        return view;
    }
}
