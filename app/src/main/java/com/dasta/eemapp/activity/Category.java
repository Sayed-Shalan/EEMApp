package com.dasta.eemapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Home;
import com.dasta.eemapp.activity.Shop.Login;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;


public class Category extends AppCompatActivity {

    //Button btnShop, btnServices, btnClient;
    MaterialBetterSpinner spCategories;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE)
                .check();

       /* btnClient = (Button) findViewById(R.id.btnClient);
        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Category.this, Home.class);
                startActivity(i);
                finish();
            }
        });

        btnShop = (Button) findViewById(R.id.btnShop);
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Category.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        btnServices = (Button) findViewById(R.id.btnServices);
        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Category.this, com.dasta.eemapp.activity.Services.Login.class);
                startActivity(i);
                finish();
            }
        });
        */

        list.add(getString(R.string.client));
        list.add(getString(R.string.shop));
        spCategories = (MaterialBetterSpinner) findViewById(R.id.spCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list);

        spCategories.setAdapter(adapter);
        spCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                throw new RuntimeException("This is a crash");
                if (position == 0) {
                    Intent intent = new Intent(Category.this, Home.class);
                    startActivity(intent);
                    finish();
                } else
                if (position == 1) {
                    Intent intent = new Intent(Category.this, Login.class);
                    startActivity(intent);
                    finish();
                }
               /* String s = list.get(position).toString();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();*/
            }
        });
    }

}

