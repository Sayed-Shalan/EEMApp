package com.dasta.eemapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Shop.Department;
import com.dasta.eemapp.helper.AppConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.dasta.eemapp.helper.AppController.TAG;


public class Splash extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the Branch object
        setContentView(R.layout.activity_splash);


        requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String[] token = {""};
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token[0] =instanceIdResult.getToken();
            }
        });

        PreferenceManager.getDefaultSharedPreferences(Splash.this).edit().putString("token",token[0]).apply();

        //Log.d("test", FirebaseInstanceId.getInstance().getToken());

//        Intent intent = getIntent();
//        String action = intent.getAction();
//        Uri data = intent.getData();
//
//        if (data!=null)
//        Toast.makeText(Splash.this,data.getQueryParameter("id"),Toast.LENGTH_LONG).show();


        updateState(Splash.this);

        getDeviceToken();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent intent = new Intent(Splash.this, Category.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    public void updateState(final Activity activity) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_UPDATE_STATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateShipping(activity);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    public void updateShipping(final Activity activity) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_UPDATE_SHIPPING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    public void getDeviceToken() {
        // make volley request
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DeviceToken,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("devicetoken", FirebaseInstanceId.getInstance().getToken());

                //returning parameter
                return params;
            }
        };

        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

}
