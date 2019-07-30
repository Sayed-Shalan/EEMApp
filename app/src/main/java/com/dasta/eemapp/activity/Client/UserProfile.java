package com.dasta.eemapp.activity.Client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Client.ClientChat;
import com.dasta.eemapp.fragment.Client.ClientChatRoom;
import com.dasta.eemapp.fragment.Client.ClientOrder;
import com.dasta.eemapp.fragment.Client.EditProfileData;
import com.dasta.eemapp.fragment.Client.FollowOrder;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.Client.SessionManager;

import java.util.HashMap;

public class UserProfile extends FragmentActivity {

    private SQLiteHandler db;
    private SessionManager session;
    public static String user_name, user_id, user_phone, user_mail;
    ImageView imgClientProfileLogout, imgClientHome;
    public static TextView txtClientProfileHeader;
    public static int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        imgClientProfileLogout = (ImageView) findViewById(R.id.imgClientLogout);

        txtClientProfileHeader = (TextView) findViewById(R.id.txtClientProfileHeader);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        user_id = user.get("userid");
        user_name = user.get("username");
        user_phone = user.get("userphone");
        user_mail = user.get("usermail");


        imgClientProfileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        imgClientHome = (ImageView) findViewById(R.id.imgClientHome);
        imgClientHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, Home.class);
                startActivity(intent);
            }
        });

        txtClientProfileHeader.setText("Hello, " + user_name);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        com.dasta.eemapp.fragment.Client.UserProfile userProfile = new com.dasta.eemapp.fragment.Client.UserProfile();
        ft.replace(R.id.fClientProfile, userProfile);
        ft.commit();

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(UserProfile.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (EditProfileData.flag == 1) {
            EditProfileData.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Client.UserProfile userProfile = new com.dasta.eemapp.fragment.Client.UserProfile();
            ft.replace(R.id.fClientProfile, userProfile);
            ft.commit();
        } else if (ClientOrder.flag == 1) {
            ClientOrder.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Client.UserProfile userProfile = new com.dasta.eemapp.fragment.Client.UserProfile();
            ft.replace(R.id.fClientProfile, userProfile);
            ft.commit();
        } else if (ClientChat.flag == 1) {
            ClientChat.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            com.dasta.eemapp.fragment.Client.UserProfile userProfile = new com.dasta.eemapp.fragment.Client.UserProfile();
            ft.replace(R.id.fClientProfile, userProfile);
            ft.commit();
        } else if (ClientChatRoom.flag == 1) {
            ClientChatRoom.flag = 0;
            ClientChat.flag = 1;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ClientChat clientChat = new ClientChat();
            ft.replace(R.id.fClientProfile, clientChat);
            ft.commit();
        } else if (FollowOrder.flag == 1) {
            FollowOrder.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ClientOrder userProfile = new ClientOrder();
            ft.replace(R.id.fClientProfile, userProfile);
            ft.commit();
        } else if (UserProfile.flag == 1) {
            UserProfile.flag = 0;
            Intent intent = new Intent(UserProfile.this, Home_Shop.class);
            intent.putExtra("shop_cat_title", Home_Shop.title);
            intent.putExtra("shop_mall", Home_Shop.mall);
            intent.putExtra("shop_city", Home_Shop.city);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
            Intent intent = new Intent(UserProfile.this, Home.class);
            startActivity(intent);
            finish();
        }
    }
}
