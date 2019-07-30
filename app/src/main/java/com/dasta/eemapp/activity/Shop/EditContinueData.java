package com.dasta.eemapp.activity.Shop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Shop.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohamed on 01/07/2017.
 */

public class EditContinueData extends Activity {

    private EditText etShopName, etShopAddress, etShopPhone;
    private Button btnContinueData;
    String name, address, phone;
    RequestQueue requestQueue;
    List<String> listCity = new ArrayList<>();
    List<String> listMall = new ArrayList<>();
    Spinner spShopMall, spShopCity;
    public static int flag1 = 0, flag2 = 0;
    public static String place, mall;
    SQLiteHandler sqLiteHandler;

    //*** Reference :
    //*** https://www.simplifiedcoding.net/android-volley-tutorial-to-upload-image-to-server/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_continue_data);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        sqLiteHandler = new SQLiteHandler(getApplicationContext());

        spShopMall = (Spinner) findViewById(R.id.spEditShopMall);
        shopMall();

        spShopCity = (Spinner) findViewById(R.id.spEditShopCity);
        shopCity();

        etShopName = (EditText) findViewById(R.id.etShopEditName);
        etShopName.setText(Home.shop_name);

        etShopAddress = (EditText) findViewById(R.id.etShopEditAddress);
        etShopAddress.setText(Home.shop_address);

        etShopPhone = (EditText) findViewById(R.id.etShopEditPhone);
        etShopPhone.setText(Home.shop_phone);

        // edit in database
        btnContinueData = (Button) findViewById(R.id.btnShopEditContinue);
        btnContinueData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = etShopName.getText().toString().trim();
                address = etShopAddress.getText().toString().trim();
                phone = etShopPhone.getText().toString().trim();

                if (isNetworkAvailable()) {
                    if (name.equals("") || address.equals("") || phone.equals("")) {
                        //** message for empty data
                        Toast.makeText(getApplicationContext(), R.string.emptydata, Toast.LENGTH_LONG).show();
                    } else {
                        if (mall.equals(getString(R.string.selectMall))) {
                            mall = "";
                        } else {
                            //** Go for home
                            if (mall.equals("") && place.equals("")) {
                                editcontinueData(name, address, phone, mall, Home.city);
                            } else if (mall.equals("")) {
                                editcontinueData(name, address, phone, mall, place);
                            } else if (place.equals("")) {
                                editcontinueData(name, address, phone, mall, Home.city);
                            } else {
                                editcontinueData(name, address, phone, mall, place);
                            }
                        }

                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void editcontinueData(final String sname,
                                  final String address, final String phone, final String mall, final String city) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "", getString(R.string.waiting), false, false);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_EDIT_CONTINUE_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        sqLiteHandler.updateShop(Home.shop_id, sname, address, phone, mall, city);
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
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
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                params.put("shopname", sname);
                params.put("shopaddress", address);
                params.put("shopphone", phone);
                params.put("shopid", Home.shop_id);
                params.put("city", city);
                params.put("mall", mall);

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
                            spShopCity.setSelection(arrayAdapter.getPosition(Home.city));
                            spShopCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (flag1 == 0) {
                                        flag1 = 1;
                                        place = "";
                                    } else {
                                        flag1 = 0;
                                        place = spShopCity.getItemAtPosition(position).toString();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
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
                            flag2 = 0;
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
                            spShopMall.setSelection(arrayAdapter.getPosition(Home.mall));
                            spShopMall.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (flag2 == 0) {
                                        flag2 = 1;
                                        mall = "";
                                    } else {
                                        flag2 = 0;
                                        mall = spShopMall.getItemAtPosition(position).toString();

                                        //Toast.makeText(getApplicationContext(), spData, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //Adding the string request to the queue
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditContinueData.this, Home.class);
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
