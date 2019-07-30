package com.dasta.eemapp.activity.Shop;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.adapter.Shop.Adapter_CustomList_All_Shop_Order;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.setwget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 02/10/2017.
 */

public class Order extends FragmentActivity {

    ListView lstShopOrder;
    ArrayList<setwget> CustomListViewValuesArr = new ArrayList<setwget>();
    Adapter_CustomList_All_Shop_Order adapter;
    Resources res;
    RequestQueue requestQueue;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_order);

        lstShopOrder = (ListView) findViewById(R.id.lstShopOrder);

        pDialog = new ProgressDialog(Order.this);
        // Showing progress dialog before making http request
        pDialog.setMessage(getString(R.string.loading));
        pDialog.show();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        res = getResources();

        if (isNetworkAvailable()) {
            //Creating a string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ORDER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //If we are getting success from server
                            try {
                                hidePDialog();
                                JSONObject jObj = new JSONObject(response);
                                /***  ***/
                                JSONArray data = jObj.getJSONArray("result");
                                /*** Data Return from server ***/
                                for (int i = 0; i < data.length(); i++) {

                                    JSONObject shop = data.getJSONObject(i);

                                    setwget sw = new setwget();
                                    sw.setOrderPName(shop.getString("pname"));
                                    sw.setOrderSName(shop.getString("uname"));
                                    sw.setOrderP(shop.getString("pimg"));
                                    sw.setOrderPCode(shop.getString("code"));
                                    sw.setPhone(shop.getString("uphone"));
                                    sw.setOrderState(shop.getString("pstate"));
                                    sw.setId(shop.getString("oid"));
                                    sw.setQuantity(shop.getString("pquantity"));
                                    int p = Integer.parseInt(shop.getString("pprice"));
                                    int q = Integer.parseInt(shop.getString("pquantity"));
                                    int t = p * q;
                                    sw.setPrice(t + "");


                                    CustomListViewValuesArr.add(sw);
                                }

                                adapter = new Adapter_CustomList_All_Shop_Order(Order.this, CustomListViewValuesArr, res);
                                lstShopOrder.setAdapter(adapter);

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
                            hidePDialog();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("shop_id", Home.shop_id);

                    //returning parameter
                    return params;
                }
            };

            //*** add request data from link into volley connection
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //*** add request data from link into volley connection
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(getApplicationContext(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
        }

    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void onItemClick(int position) {

        setwget tempValues = CustomListViewValuesArr.get(position);

        String state = tempValues.getOrderState();

        final String oid = tempValues.getId();

        if (state.equals("Waiting")) {

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Order.this,
                    android.R.layout.select_dialog_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

            arrayAdapter.add(getString(R.string.shopOrderComplete));
            arrayAdapter.add(getString(R.string.shopOrderCancel));

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(Order.this);


            builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {

                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                // Complete Order
                                orderStateComplete(Order.this, oid);
                            } else {
                                // Cancel Order
                                orderStateCancel(Order.this, oid);
                            }
                        }
                    });

            builderSingle.show();
        } else {
            if (state.equals("Complete")) {
                Toast.makeText(getApplicationContext(), R.string.shopOrderComplete, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.shopOrderCancel, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onPhoneClick(int position) {
        setwget tempValues = CustomListViewValuesArr.get(position);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Order.this);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Order.this,
                android.R.layout.select_dialog_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

        arrayAdapter.add(tempValues.getPhone());

        builderSingle.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                arrayAdapter.getItem(which).toString()));
                        if (ActivityCompat.checkSelfPermission(Order.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(i);
                    }
                });
        builderSingle.show();
    }

    // Order State
    public void orderStateComplete(final Activity activity, final String oid) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ORDER_COMPLETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(Order.this, Order.class);
                        startActivity(intent);
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
                params.put("orderid", oid);
                return params;
            }
        };
        //*** add request data from link into volley connection
        request.setRetryPolicy(new

                DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //*** add request data from link into volley connection
        requestQueue.add(request);

    }

    // Order State
    public void orderStateCancel(final Activity activity, final String oid) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ORDER_CANCEL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(Order.this, Order.class);
                        startActivity(intent);
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
                params.put("orderid", oid);
                return params;
            }
        };
        //*** add request data from link into volley connection
        request.setRetryPolicy(new

                DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //*** add request data from link into volley connection
        requestQueue.add(request);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Order.this, Home.class);
        startActivity(intent);
        finish();
    }
}
