package com.dasta.eemapp.activity.Shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.fragment.Shop.Appointment;
import com.dasta.eemapp.fragment.Shop.Category;
import com.dasta.eemapp.fragment.Shop.LiveInfo;
import com.dasta.eemapp.fragment.Shop.Offer;
import com.dasta.eemapp.fragment.Shop.Product;
import com.dasta.eemapp.helper.AppConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 01/07/2017.
 */

public class EditProduct extends FragmentActivity {

    private EditText etProductName, etProductBuyPrice, etProductSellPrice, etProductAvialableQuantity,
            etProductAvialableNotification, etProductDesc;
    private Switch swProductReservation, swProductDeliver;
    private Button btnAddProduct;

    private String name, buy, sell, avquantity, avnotification, desc, reservation1, deliver1;
    public static String ename, ebuy, esell, eavquantity, eavnotification, edesc, ereservation, edeliver, eid, edept, cat;
    private int reservation, deliver;

    RequestQueue requestQueue;

    public static int flag = 0;
    public static int key = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        flag = 1;
        Appointment.flag = 0;
        Category.flag = 0;
        LiveInfo.flag = 0;
        Offer.flag = 0;
        com.dasta.eemapp.fragment.Shop.Home.flag = 0;
        Product.flag = 0;

        cat = getIntent().getStringExtra("ShopCat");
        edept = getIntent().getStringExtra("EditProductDept");

        eid = getIntent().getStringExtra("EditProductId");
        ename = getIntent().getStringExtra("EditProductName");
        ebuy = getIntent().getStringExtra("EditProductBuy");
        esell = getIntent().getStringExtra("EditProductSell");
        eavnotification = getIntent().getStringExtra("EditProductNotification");
        eavquantity = getIntent().getStringExtra("EditProductQuantity");
        edesc = getIntent().getStringExtra("EditProductDesc");
        ereservation = getIntent().getStringExtra("EditProductReservation");
        edeliver = getIntent().getStringExtra("EditProductDeliver");

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        etProductName = (EditText) findViewById(R.id.etProductEditName);
        etProductName.setText(ename);

        etProductBuyPrice = (EditText) findViewById(R.id.etProductEditBuyPrice);
        etProductBuyPrice.setText(ebuy);

        etProductSellPrice = (EditText) findViewById(R.id.etProductEditSellPrice);
        etProductSellPrice.setText(esell);

        etProductAvialableQuantity = (EditText) findViewById(R.id.etProductEditAvialableQuantity);
        etProductAvialableQuantity.setText(eavquantity);

        etProductAvialableNotification = (EditText) findViewById(R.id.etProductEditAvialableNotification);
        etProductAvialableNotification.setText(eavnotification);

        etProductDesc = (EditText) findViewById(R.id.etProductEditDesc);
        etProductDesc.setText(edesc);

        swProductReservation = (Switch) findViewById(R.id.swProductEditReservation);
        if (ereservation.equals(getString(R.string.reservationYes))) {
            swProductReservation.setChecked(true);
        } else {
            swProductReservation.setChecked(false);
        }
        swProductReservation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reservation = 1;
                } else {
                    reservation = 0;
                }
            }
        });

        swProductDeliver = (Switch) findViewById(R.id.swProductEditDeliver);
        if (edeliver.equals(getString(R.string.deliverYes))) {
            swProductDeliver.setChecked(true);
        } else {
            swProductDeliver.setChecked(false);
        }
        swProductDeliver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deliver = 1;
                } else {
                    deliver = 0;
                }
            }
        });


        btnAddProduct = (Button) findViewById(R.id.btnEditShopProduct);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etProductName.getText().toString().trim();
                buy = etProductBuyPrice.getText().toString().trim();
                sell = etProductSellPrice.getText().toString().trim();
                avnotification = etProductAvialableNotification.getText().toString().trim();
                avquantity = etProductAvialableQuantity.getText().toString().trim();
                desc = etProductDesc.getText().toString().trim();
                reservation1 = reservation + "";
                deliver1 = deliver + "";

                if (isNetworkAvailable()) {
                    if (!(name.isEmpty()) && !(buy.isEmpty()) && !(sell.isEmpty()) && !(avnotification.isEmpty())
                            && !(avquantity.isEmpty()) && !(desc.isEmpty())) {

                        editproduct(eid, name, buy, sell, desc, avnotification, avquantity,
                                deliver1, reservation1);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.emptydata, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    public void editproduct(final String id, final String name, final String buy,
                            final String sell, final String desc, final String avnotification, final String avquantity,
                            final String deliver, final String reservation) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "", getString(R.string.waiting), false, false);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_EDIT_SHOP_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        key = 1;
                        Intent intent = new Intent(EditProduct.this, Home.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "Error : " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("id", id);
                params.put("product_name", name);
                params.put("product_buy", buy);
                params.put("product_sell", sell);
                params.put("product_desc", desc);
                params.put("product_av_notification", avnotification);
                params.put("product_av_quantity", avquantity);
                params.put("product_deliver", deliver);
                params.put("product_reservation", reservation);

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
