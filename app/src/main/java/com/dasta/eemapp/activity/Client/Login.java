package com.dasta.eemapp.activity.Client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.dasta.eemapp.fragment.Client.DepartmentProfile;
import com.dasta.eemapp.fragment.Client.EditProfileData;
import com.dasta.eemapp.fragment.Client.Home_Category;
import com.dasta.eemapp.fragment.Client.MapProfile;
import com.dasta.eemapp.fragment.Client.OfferProfile;
import com.dasta.eemapp.fragment.Client.ProductProfile;
import com.dasta.eemapp.fragment.Client.Shop;
import com.dasta.eemapp.fragment.Client.ShopProfile;
import com.dasta.eemapp.fragment.Client.SingleOffer;
import com.dasta.eemapp.fragment.Client.SingleProduct;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.Client.SessionManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class Login extends Activity {

    private EditText etUserName, etPassword;
    private Button btnSignin;
    private TextView txtSignup;
    String username, password;
    public static String title, logoUrl, phone, address, id, lat, lng, cat, city;

    private SessionManager session;
    private SQLiteHandler db;

    RequestQueue requestQueue;
    LoginButton facebookLoginButton;
    CallbackManager callbackManager;
    public static boolean facebokkLoginRespone=false;
    RequestQueue requestQueueReg;
    RequestQueue getDataQueue;
    final String[] token = {""};



    /*** Reference ***/
    /*** https://www.simplifiedcoding.net/android-login-example-using-php-mysql-and-volley/ ***/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_login);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token[0] =instanceIdResult.getToken();
            }
        });
        DepartmentProfile.flag = 0;
        EditProfileData.flag = 0;
        MapProfile.flag = 0;
        OfferProfile.flag = 0;
        ProductProfile.flag = 0;
        Shop.flag = 0;
        ShopProfile.flag = 0;
        SingleOffer.flag = 0;
        SingleProduct.flag = 0;

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        getDataQueue = Volley.newRequestQueue(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Login.this, UserProfile.class);
            startActivity(intent);
            finish();
        }

        etUserName = (EditText) findViewById(R.id.etUserName);

        etPassword = (EditText) findViewById(R.id.etUserPassword);

        btnSignin = (Button) findViewById(R.id.btnUserSignin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUserName.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                facebokkLoginRespone=false;
                username=username.replace(".","_");
                loginuser(Login.this, username, password);
            }
        });

        txtSignup = (TextView) findViewById(R.id.txtUserSignup);
        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        facebookLoginButton=(LoginButton) findViewById(R.id.facebooklogin);
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions(Arrays.asList("email"));
        // Callback registration
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                try {
                                    Log.i("Response",response.toString());

                                    String email = response.getJSONObject().getString("email");
                                    String firstName = response.getJSONObject().getString("first_name");
                                    String lastName = response.getJSONObject().getString("last_name");
                                    //String gender = response.getJSONObject().getString("gender");
                                    Snackbar.make(facebookLoginButton,firstName.concat(" ").concat(lastName),Toast.LENGTH_SHORT)
                                            .show();
                                    Profile profile = Profile.getCurrentProfile();
                                    String id = profile.getId();

                                    facebokkLoginRespone=true;

                                    email=email.replace(".","_");
                                        loginuser(Login.this ,email,id);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,last_name,gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(Login.this,"تم الغاء التسجيل",Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(Login.this,"حدث خطأ ما حاول مرة آخري",Toast.LENGTH_SHORT ).show();
            }
        });
    }

    // Login Shop
    public void loginuser(final Activity activity, final String username, final String password) {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        Log.v("Login : ",response);
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

                                //Starting profile activity
                                if (user_name != null && !user_name.isEmpty() && !user_name.equals("null")) {

                                    Bundle bundle=getIntent().getExtras();
                                    if (bundle!=null){
                                        if (bundle.getString("calling_activity","NO").equals("shop_profile")){

                                            title = bundle.getString("shop_client_title");

                                            logoUrl = bundle.getString("shop_client_img_url");

                                            phone = bundle.getString("shop_client_phone");

                                            address = bundle.getString("shop_client_address");

                                            id = bundle.getString("shop_client_id");

                                            lat = bundle.getString("shop_client_lat");

                                            lng = bundle.getString("shop_client_lng");

                                            Intent intent = new Intent(activity, Home_Shop.class);
                                            intent.putExtra("view_shop_id", id);
                                            intent.putExtra("view_shop_address", address);
                                            intent.putExtra("view_shop_phone", phone);
                                            intent.putExtra("view_shop_name", title);
                                            intent.putExtra("view_shop_img", logoUrl);
                                            intent.putExtra("view_shop_lat", lat);
                                            intent.putExtra("view_shop_lng", lng);
                                            intent.putExtra("shop_client_cat",bundle.getString("shop_client_cat"));
                                            intent.putExtra("shop_client_city",bundle.getString("shop_client_city"));

                                            //activity.startActivity(intent);
                                            //Will Edited
                                            finish();
                                        }else {
                                            Intent intent = new Intent(activity, Home.class);
                                            activity.startActivity(intent);
                                            finish();
                                        }
                                    }else {
                                        Intent intent = new Intent(activity, Home.class);
                                        activity.startActivity(intent);
                                        finish();
                                    }


                                } else {
                                    if (facebokkLoginRespone){
                                        registerUsingFaceBook(username,password);
                                    }else {
                                        Toast.makeText(getApplicationContext(), "اسم المستخدم أو كلمة السر خاطئة", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(activity, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            if (facebokkLoginRespone){
                                registerUsingFaceBook(username,password);
                            }else {
                                Toast.makeText(getApplicationContext(), "اسم المستخدم أو كلمة السر خاطئة", Toast.LENGTH_LONG).show();
                            }

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        if (facebokkLoginRespone){
                            registerUsingFaceBook(username,password);
                        }else {
                            Toast.makeText(activity," حدث خطأ ما حاول لاحق404ا", Toast.LENGTH_LONG).show();
                        }
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
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    private void registerUsingFaceBook(final String username, final String password) {

        // make volley request
        requestQueueReg = Volley.newRequestQueue(Login.this);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("Register REsponse :",response);
                        //Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        //Intent intent = new Intent(activity, Login.class);
                        //activity.startActivity(intent);

                        getDataFromCloud(username, password);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
                //
                Toast.makeText(Login.this,"Error Registeration : Pass ="+password, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("email", username);
                params.put("phone", "01234567896");
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueueReg.add(request);
    }

    private synchronized void getDataFromCloud(final String username, final String password) {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.v("Response : ",response);
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

                                Toast.makeText(Login.this,"تم تسجيل الدخول بنجاح..", Toast.LENGTH_LONG).show();
                                //Starting profile activity
                                Intent intent = new Intent(Login.this, Home.class);
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
        getDataQueue = Volley.newRequestQueue(Login.this);
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

        requestQueue=Volley.newRequestQueue(Login.this);
        requestQueue.add(sendToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
