package com.dasta.eemapp.activity.Shop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Home_Shop;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Shop.SQLiteHandler;
import com.dasta.eemapp.helper.Shop.SessionManager;
import com.dasta.eemapp.helper.WebServies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ContinueData extends Activity {

    private EditText etShopName, etShopAddress, etShopPhone;
    private ImageView imgShopImage;
    private Button btnContinueData, btnShopImage;
    String name, address, phone;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    RequestQueue requestQueue;
    String username, id;
    List<String> listCity = new ArrayList<>();
    List<String> listMall = new ArrayList<>();
    Spinner spShopMall, spShopCity;
    public static int flag1 = 0, flag2 = 0;
    public static String place, mall;
    SQLiteHandler db;
    SessionManager session;

    //*** Reference :
    //*** https://www.simplifiedcoding.net/android-volley-tutorial-to-upload-image-to-server/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_data);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        db = new SQLiteHandler(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        username = getIntent().getStringExtra("owner_username");

        id = getIntent().getStringExtra("shop_id");

        spShopCity = (Spinner) findViewById(R.id.spShopCity);
        shopCity();

        spShopMall = (Spinner) findViewById(R.id.spShopMall);
        shopMall();

        etShopName = (EditText) findViewById(R.id.etShopName);

        etShopAddress = (EditText) findViewById(R.id.etShopAddress);

        etShopPhone = (EditText) findViewById(R.id.etShopPhone);

        imgShopImage = (ImageView) findViewById(R.id.imgShopImage);

        //select image from gallery to upload
        btnShopImage = (Button) findViewById(R.id.btnShopImage);
        btnShopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        // add data in database
        btnContinueData = (Button) findViewById(R.id.btnShopContinue);
        btnContinueData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = etShopName.getText().toString().trim();
                address = etShopAddress.getText().toString().trim();
                phone = etShopPhone.getText().toString().trim();

                if (isNetworkAvailable()) {
                    if (!(name.isEmpty()) && !(address.isEmpty()) && !(phone.isEmpty())) {
                        //** Go for home
                        if (place.equals("")) {
                           // Toast.makeText(getApplicationContext(), place, Toast.LENGTH_LONG).show();
                          //  Toast.makeText(getApplicationContext(), R.string.chooseCity, Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(getApplicationContext(), place, Toast.LENGTH_LONG).show();
                            continueData(username, name, address, phone, place, mall);
                            new WebServies().shopLogout(ContinueData.this, id);
                        }
                    } else {
                        //** message for empty data
                        Toast.makeText(getApplicationContext(), R.string.emptydata, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imgShopImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void continueData(final String username, final String sname,
                              final String address, final String phone, final String place, final String mall) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "", getString(R.string.waiting), false, false);

        // make volley request
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_CONTINUE_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        logoutUser();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("username", username);
                params.put("shopimage", image);
                params.put("shopname", sname);
                params.put("shopaddress", address);
                params.put("shopphone", phone);
                params.put("shopcity", place);
                params.put("shopmall", mall);

                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    public void shopCity() {

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SHOP_ALL_CITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        try {
                            listCity.add(getString(R.string.selectCity));
                            JSONObject jObj = new JSONObject(response);
                            /***  ***/
                            JSONArray data = jObj.getJSONArray("result");
                            /*** Data Return from server ***/
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject shop = data.getJSONObject(i);

                                String id = shop.getString("id");
                                String cat_ar = shop.getString("name");

                                listCity.add(cat_ar);
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinnerview_item, listCity) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);
                                    return v;
                                }

                                @Override
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getDropDownView(position, convertView, parent);
                                    return v;
                                }
                            };
                            spShopCity.setAdapter(arrayAdapter);
                            spShopCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (flag1 == 0) {
                                        flag1 = 1;
                                        place = "";
                                    } else {
                                        flag1 = 0;
                                        place = spShopCity.getItemAtPosition(position).toString();
                                 //       Toast.makeText(getApplicationContext(), place, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                           // Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                       // Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //Adding the string request to the queue
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void shopMall() {

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SHOP_ALL_MALL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        try {
                            listMall.add(getString(R.string.selectMall));
                            JSONObject jObj = new JSONObject(response);
                            /***  ***/
                            JSONArray data = jObj.getJSONArray("result");
                            /*** Data Return from server ***/
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject shop = data.getJSONObject(i);

                                String id = shop.getString("id");
                                String cat_ar = shop.getString("name");
                                listMall.add(cat_ar);
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinnerview_item, listMall) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);
                                    return v;
                                }

                                @Override
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getDropDownView(position, convertView, parent);
                                    return v;
                                }
                            };
                            spShopMall.setAdapter(arrayAdapter);
                            spShopMall.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (flag2 == 0) {
                                        flag2 = 1;
                                        mall = "";
                                    } else {
                                        flag2 = 0;
                                        mall = spShopMall.getItemAtPosition(position).toString();

                                      //  Toast.makeText(getApplicationContext(), mall, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                          //  Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                     //   Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //Adding the string request to the queue
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(ContinueData.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logoutUser();
        Intent intent = new Intent(ContinueData.this, Login.class);
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
