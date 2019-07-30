package com.dasta.eemapp.activity.Shop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.dasta.eemapp.helper.Shop.SQLiteHandler;
import com.dasta.eemapp.helper.Shop.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 28/06/2017.
 */

public class Register extends Activity {

    private EditText etShopOwnerName, etShopOwnerEmail, etShopOwnerUsername, etShopOwnerPassword, etShopConfirmPassword;
    private Button btnShopRegister;
    String name, mail, username, password, confirm;
    public static String lat, lng;
    RequestQueue requestQueue;

    private SessionManager session;
    private SQLiteHandler db;

    RequestQueue getDataQueue;
    final String[] token = {""};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shop_register);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token[0] =instanceIdResult.getToken();
            }
        });

        // Get user location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Keep track of user location.
        // Use callback/listener since requesting immediately may return null location.
        // IMPORTANT: TO GET GPS TO WORK, MAKE SURE THE LOCATION SERVICES ON YOUR PHONE ARE ON.
        // FOR ME, THIS WAS LOCATED IN SETTINGS > LOCATION.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, new Listener());
        // Have another for GPS provider just in case.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new Listener());
        // Try to request the location immediately
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location != null) {
            handleLatLng(location.getLatitude(), location.getLongitude());
            lat = location.getLatitude() + "";
            lng = location.getLongitude() + "";
        } else {
            lat = "";
            lng = "";
            Toast.makeText(getApplicationContext(), R.string.gpsMessage, Toast.LENGTH_LONG).show();
        }

        getDataQueue = Volley.newRequestQueue(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        etShopOwnerName = (EditText) findViewById(R.id.etShopOwnerName);

        etShopOwnerEmail = (EditText) findViewById(R.id.etShopOwnerEmail);

        etShopOwnerUsername = (EditText) findViewById(R.id.etShopOwnerUsername);

        etShopOwnerPassword = (EditText) findViewById(R.id.etShopOwnerPassword);

        etShopConfirmPassword = (EditText) findViewById(R.id.etShopConfirmPassword);

        btnShopRegister = (Button) findViewById(R.id.btnShopRegister);
        configureButton();

    }


    private void configureButton() {
        btnShopRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etShopOwnerName.getText().toString().trim();
                mail = etShopOwnerEmail.getText().toString().trim();
                username = etShopOwnerUsername.getText().toString().trim();
                password = etShopOwnerPassword.getText().toString().trim();
                confirm = etShopConfirmPassword.getText().toString().trim();

                if (isNetworkAvailable()) {
                    if (!(name.isEmpty()) && !(mail.isEmpty()) && !(username.isEmpty()) && !(password.isEmpty()) && !(confirm.isEmpty())) {

                        if (password.equals(confirm)) {
                            //*** register now ...
                            addShopOwner(Register.this, name, mail, username, password, lat, lng);
                            //Toast.makeText(getApplicationContext(), lat + "\n" + lng, Toast.LENGTH_LONG).show();
                        } else {
                            //*** message for wrong password confirmation
                            Toast.makeText(getApplicationContext(), R.string.confirmpass, Toast.LENGTH_LONG).show();
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

    // Register Shop Owner
    public void addShopOwner(final Activity activity, final String name, final String mail, final String username,
                             final String password, final String lat, final String lng) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Intent intent = new Intent(activity, Login.class);
                        //activity.startActivity(intent);

                        getData(username,password);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ownername", name);
                params.put("ownermail", mail);
                params.put("username", username);
                params.put("password", password);
                params.put("lat", lat);
                params.put("lng", lng);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    private void getData(final String username, final String password) {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_LOGIN,
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

                                Toast.makeText(Register.this, "تم انشاء الحساب بنجاح", Toast.LENGTH_LONG).show();

                                //Starting profile activity
                                    // Inserting row in users table
                                    db.addUser(shop_id, shop_name, shop_address, shop_phone,
                                            owner_name, owner_mail, owner_city, owner_mall, owner_username,
                                            shop_image, lat, lng, open);

                                Intent intent = new Intent(Register.this, ContinueData.class);
                                intent.putExtra("owner_username", owner_username);
                                intent.putExtra("shop_id", shop_id);
                                startActivity(intent);
                                finish();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
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
                params.put(AppConfig.KEY_USERNAME, username);
                params.put(AppConfig.KEY_PASSWORD, password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        getDataQueue = Volley.newRequestQueue(Register.this);
        getDataQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }

    //**** to get current location of user

    /**
     * Handle lat lng.
     */
    private void handleLatLng(double latitude, double longitude) {
        Log.v("TAG", "(" + latitude + "," + longitude + ")");
    }

    /**
     * Listener for changing gps coords.
     */
    private class Listener implements LocationListener {
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            handleLatLng(latitude, longitude);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
