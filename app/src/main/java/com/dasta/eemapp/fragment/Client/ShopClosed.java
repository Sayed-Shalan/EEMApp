package com.dasta.eemapp.fragment.Client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Home_Shop;

/**
 * Created by Mohamed on 03/10/2017.
 */

public class ShopClosed extends Fragment {

    public static int flag = 0;
    public static String cat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_shop_closed, null);
        flag = 1;
        if (Home_Shop.keySearch == 0) {
            cat = getArguments().getString("shop_client_catt");
        }
        return v;
    }
}
