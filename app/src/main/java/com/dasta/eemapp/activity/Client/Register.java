package com.dasta.eemapp.activity.Client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;

import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.Client.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends Activity {

    EditText etRUsername, etRPassword, etRUserPhone, etRUserEmail, etRUserConfirm;
    Button btnRUserSignup;
    String username, password, phone, email, confirm;
    RequestQueue requestQueue;

    private SessionManager session;
    private SQLiteHandler db;

    RequestQueue getDataQueue;
    final String[] token = {""};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_register);


        getDataQueue = Volley.newRequestQueue(getApplicationContext());
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

        etRUsername = (EditText) findViewById(R.id.etRUserName);

        etRPassword = (EditText) findViewById(R.id.etRUserPassword);

        etRUserConfirm = (EditText) findViewById(R.id.etRUserConfirmPassword);

        etRUserEmail = (EditText) findViewById(R.id.etRUserEmail);

        etRUserPhone = (EditText) findViewById(R.id.etRUserPhone);

        btnRUserSignup = (Button) findViewById(R.id.btnRUserSignup);

        btnRUserSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = etRUsername.getText().toString().trim();
                password = etRPassword.getText().toString().trim();
                confirm = etRUserConfirm.getText().toString().trim();
                phone = etRUserPhone.getText().toString().trim();
                email = etRUserEmail.getText().toString().trim();

                if (password.equals(confirm)) {
                    if (!(username.isEmpty()) && !(password.isEmpty()) && !(confirm.isEmpty())
                            && !(phone.isEmpty()) && !(email.isEmpty())) {
                        addUser(Register.this, username, password, email, phone);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.emptydata, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.confirmpass, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // Register Shop Owner
    public void addUser(final Activity activity, String username, final String password, final String email,
                        final String phone) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        username=username.replace(".","_");
        final String finalUsername = username;
        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        //Intent intent = new Intent(activity, Login.class);
                        //activity.startActivity(intent);

                        getDataFromCloud(finalUsername,password);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", finalUsername);
                params.put("password", password);
                params.put("email", email);
                params.put("phone", phone);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    private void getDataFromCloud(final String username, final String password) {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
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

                                String user_id = shop.getString("id");
                                sendUserFCMTokenToServer(user_id);
                                String user_name = shop.getString("name");
                                String user_mail = shop.getString("mail");
                                String user_phone = shop.getString("phone");

                                // Inserting row in users table
                                db.addUser(user_id, user_name, user_mail, user_phone);

                                Toast.makeText(Register.this,"تم تسجيل الدخول بنجاح..", Toast.LENGTH_LONG).show();
                                //Starting profile activity
                                Intent intent = new Intent(Register.this, Home.class);
                                startActivity(intent);
                                finish();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(activity, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        //Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("username", username);
                params.put("password", password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        getDataQueue = Volley.newRequestQueue(Register.this);
        getDataQueue.add(stringRequest);
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

        requestQueue=Volley.newRequestQueue(Register.this);
        requestQueue.add(sendToken);
    }
}
