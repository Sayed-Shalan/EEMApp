package com.dasta.eemapp.activity.Shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.WebServies;

/**
 * Created by Mohamed on 27/08/2017.
 */

public class ShopOpen extends Activity {


    private Switch swShopOpen;
    private Button btnShopOpen;
    String open;
    int open1, open2;
    WebServies webServies = new WebServies();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_open);

        swShopOpen = (Switch) findViewById(R.id.swShopOpen);
        open2 = Integer.parseInt(Home.open);
        if (open2 == 1) {
            swShopOpen.setChecked(true);
        } else {
            swShopOpen.setChecked(false);
        }
        swShopOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    open1 = 1;
                } else {
                    open1 = 0;
                }
            }
        });
        btnShopOpen = (Button) findViewById(R.id.btnShopOpen);
        btnShopOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    open = open1 + "";
                    webServies.addShopOpen(ShopOpen.this, Home.shop_id, open);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShopOpen.this, Home.class);
        startActivity(intent);
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
