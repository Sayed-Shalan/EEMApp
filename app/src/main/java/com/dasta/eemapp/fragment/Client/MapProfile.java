package com.dasta.eemapp.fragment.Client;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dasta.eemapp.R;

public class MapProfile extends Fragment {

    private WebView webView;

    public static int flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_map, null);
        flag = 1;
        ShopProfile.flag = 0;
        setRetainInstance(true);
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "جارى التحميل ...", true);
        pd.setCancelable(true);

        String lat = getArguments().getString("shop_profile_map_lat");
        String lng = getArguments().getString("shop_profile_map_lng");
        String url = "http://maps.google.com/maps?daddr=" + lat + "," + lng;
        webView =  view.findViewById(R.id.webView2);
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
