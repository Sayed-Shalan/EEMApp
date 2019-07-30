package com.dasta.eemapp.activity.Shop;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Shop.ListChat;
import com.dasta.eemapp.fragment.Shop.ShopChat;

/**
 * Created by Mohamed on 23/03/2018.
 */

public class Chat extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_chat);

        Bundle fcmBundle=getIntent().getExtras();
        if (fcmBundle!=null){
            if (fcmBundle.containsKey("open_chat")){
                if (fcmBundle.getBoolean("open_chat",false)){
                    FragmentManager fm = getSupportFragmentManager();
                    ListChat.flag=0;
                    FragmentTransaction ft = fm.beginTransaction();
                    ShopChat chat = new ShopChat();
                    Bundle bundle = new Bundle();
                    bundle.putString("shop_chat_receiver_id", fcmBundle.getString("shop_chatt_receiver_id"));
                    bundle.putString("shop_chat_receiver_name", fcmBundle.getString("shop_chatt_receiver_name"));
                    chat.setArguments(bundle);
                    ft.replace(R.id.fShopChatLayout, chat);
                    ft.commit();
                }else {
                    FragmentManager fm = getSupportFragmentManager();

                    FragmentTransaction ft = fm.beginTransaction();
                    ListChat chat = new ListChat();
                    ft.replace(R.id.fShopChatLayout, chat);
                    ft.commit();
                }
            }else {

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ListChat chat = new ListChat();
                ft.replace(R.id.fShopChatLayout, chat);
                ft.commit();
            }

        }else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ListChat chat = new ListChat();
            ft.replace(R.id.fShopChatLayout, chat);
            ft.commit();
        }



    }

    @Override
    public void onBackPressed() {
        if (ShopChat.flag == 1) {
            ShopChat.flag = 0;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ListChat chat = new ListChat();
            ft.replace(R.id.fShopChatLayout, chat);
            ft.commit();
        } else if (ListChat.flag == 1) {
            ListChat.flag = 0;
            Intent intent = new Intent(Chat.this, Home.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
