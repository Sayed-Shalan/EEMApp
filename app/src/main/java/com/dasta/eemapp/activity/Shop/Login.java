package com.dasta.eemapp.activity.Shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Category;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Shop.SQLiteHandler;
import com.dasta.eemapp.helper.Shop.SessionManager;
import com.dasta.eemapp.helper.WebServies;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Login extends Activity {

    private EditText etShopUserName, etShopPassword;
    private Button btnShopSignin;
    private TextView txtShopSignup;
    String username, password;

    private SessionManager session;
    private SQLiteHandler db;

    RequestQueue requestQueue;
    final String[] token = {""};


    /*** Reference ***/
    /*** https://www.simplifiedcoding.net/android-login-example-using-php-mysql-and-volley/ ***/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token[0] =instanceIdResult.getToken();
            }
        });
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
            finish();
        }

        etShopUserName = (EditText) findViewById(R.id.etShopUserName);

        etShopPassword = (EditText) findViewById(R.id.etShopPassword);

        btnShopSignin = (Button) findViewById(R.id.btnShopSignin);
        btnShopSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    username = etShopUserName.getText().toString().trim();
                    password = etShopPassword.getText().toString().trim();
                    loginShop(Login.this, username, password);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        txtShopSignup = (TextView) findViewById(R.id.txtShopSignup);
        txtShopSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent i = new Intent(Login.this, Register.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // Login Shop
    public void loginShop(final Activity activity, final String username, final String password) {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.v("Shop Login Response :",response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            /***  ***/
                            JSONArray data = jObj.getJSONArray("result");
                            /*** Data Return from server ***/
                            for (int i = 0; i < data.length(); i++) {

                                // user successfully logged in
                                // Create login session
                                session.setLogin(true);

                                // Now store the user in SQLite

                                JSONObject shop = data.getJSONObject(i);

                                String shop_id = shop.getString("id");
                                sendUserFCMTokenToServer(shop_id);
                                String shop_name = shop.getString("shop_name");
                                String shop_image = shop.getString("shop_image");
                                String shop_phone = shop.getString("shop_phone");
                                String shop_address = shop.getString("shop_address");
                                String owner_name = shop.getString("owner_name");
                                String owner_mail = shop.getString("owner_mail");
                                String owner_username = shop.getString("owner_username");
                                String owner_city = shop.getString("city");
                                String owner_mall = shop.getString("mall");
                                String lat = shop.getString("lat");
                                String lng = shop.getString("lng");
                                String open = shop.getString("open");

                                //Starting profile activity
                                if (shop_name != null && !shop_name.isEmpty() && !shop_name.equals("null")) {
                                    // Inserting row in users table
                                    db.addUser(shop_id, shop_name, shop_address, shop_phone,
                                            owner_name, owner_mail, owner_city, owner_mall, owner_username,
                                            shop_image, lat, lng, open);
                                    Intent intent = new Intent(activity, Home.class);
                                    activity.startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(activity, ContinueData.class);
                                    intent.putExtra("owner_username", owner_username);
                                    intent.putExtra("shop_id", shop_id);
                                    activity.startActivity(intent);
                                    finish();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "اسم المستخدم أو كلمة المرور غير صحيحة", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(activity, "كلمة المرور أو اسم الحساب خطأ", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put(AppConfig.KEY_USERNAME, username);
                params.put(AppConfig.KEY_PASSWORD, password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Login.this, Category.class);
        startActivity(intent);
        finish();
    }

    //add user FCM Token
    private void sendUserFCMTokenToServer(final String user_id){
        StringRequest sendToken=new StringRequest(Request.Method.POST, "http://dasta.net/data/eem/Chat/insert_user_token.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("INSERT TOKEN: ",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<>();
                map.put("user_id",user_id);

                map.put("user_token",token[0]);
                return map;
            }
        };

        requestQueue=Volley.newRequestQueue(Login.this);
        requestQueue.add(sendToken);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
