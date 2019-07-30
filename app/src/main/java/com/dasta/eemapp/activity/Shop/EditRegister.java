package com.dasta.eemapp.activity.Shop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Shop.Category;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Shop.SQLiteHandler;
import com.dasta.eemapp.helper.Shop.SessionManager;
import com.dasta.eemapp.helper.WebServies;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Mohamed on 28/06/2017.
 */

public class EditRegister extends Activity {

    private EditText etShopOwnerName, etShopOwnerEmail, etShopOwnerUsername;
    private Button btnShopRegister;
    String name, mail, username;
    WebServies webServies = new WebServies();
    SQLiteHandler sqLiteHandler;
    RequestQueue requestQueue;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_edit_register);

        sqLiteHandler = new SQLiteHandler(getApplicationContext());

        sessionManager = new SessionManager(getApplicationContext());

        etShopOwnerName = (EditText) findViewById(R.id.etShopEditOwnerName);
        etShopOwnerName.setText(Home.owner_name);

        etShopOwnerEmail = (EditText) findViewById(R.id.etShopEditOwnerEmail);
        etShopOwnerEmail.setText(Home.owner_mail);

        etShopOwnerUsername = (EditText) findViewById(R.id.etShopEditOwnerUsername);
        etShopOwnerUsername.setText(Home.owner_username);

        btnShopRegister = (Button) findViewById(R.id.btnShopEditRegister);
        configureButton();

    }

    private void configureButton() {
        btnShopRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etShopOwnerName.getText().toString().trim();
                mail = etShopOwnerEmail.getText().toString().trim();
                username = etShopOwnerUsername.getText().toString().trim();

                if (isNetworkAvailable()) {
                    if (!(name.isEmpty()) && !(mail.isEmpty()) && !(username.isEmpty())) {
                        if (username.equals(Home.owner_username)) {
                            //*** register now ...
                            webServies.editShopOwner(EditRegister.this, name, mail, username, Home.shop_id);
                            sqLiteHandler.updateUser(Home.shop_id, name, mail, username);
                        } else {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                    EditRegister.this, AlertDialog.THEME_HOLO_LIGHT);

                            builderSingle.setMessage("لقد قمت بتغير اسم المستخدم هذا سيجعلك تقوم بالخروج" +
                                    " ثم الدخول مره اخرى");
                            builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builderSingle.setPositiveButton(getString(R.string.questionYes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    editShopOwnerusername(name, mail, username, Home.shop_id);
                                }
                            });

                            builderSingle.show();

                        }
                    } else {
                        //*** message for empty data
                        Toast.makeText(getApplicationContext(), R.string.emptydata, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditRegister.this, Home.class);
        startActivity(intent);
        finish();
    }

    private void logoutUser() {
        sessionManager.setLogin(false);

        sqLiteHandler.deleteUsers();
        deleteCache(getApplicationContext());
        webServies.shopLogout(EditRegister.this, Home.shop_id);
        // Launching the login activity
        Intent intent = new Intent(EditRegister.this, Login.class);
        startActivity(intent);
        finish();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            Intent intent = new Intent(context, Category.class);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void editShopOwnerusername(final String name, final String mail, final String username,
                                      final String shopid) {
        // make volley request
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_EDIT_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        logoutUser();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ownername", name);
                params.put("ownermail", mail);
                params.put("username", username);
                params.put("shopid", shopid);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }
}
