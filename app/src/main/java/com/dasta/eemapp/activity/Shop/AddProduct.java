package com.dasta.eemapp.activity.Shop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 01/07/2017.
 */

public class AddProduct extends FragmentActivity {

    private EditText etProductName, etProductBuyPrice, etProductSellPrice, etProductAvialableQuantity,
            etProductAvialableNotification, etProductDesc;
    private Switch swProductReservation, swProductDeliver;
    private Button btnProductImage, btnAddProduct;
    private ImageView imgProductImage;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    public static String name, buy, sell, avquantity, avnotification, desc, department, reservation1, deliver1, image, category, pid;
    private int reservation, deliver;
    RequestQueue requestQueue;
    public static int flag = 0;
    public static int key = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        flag = 1;
        Appointment.flag = 0;
        Category.flag = 0;
        LiveInfo.flag = 0;
        Offer.flag = 0;
        com.dasta.eemapp.fragment.Shop.Home.flag = 0;
        Product.flag = 0;

        department = getIntent().getStringExtra("product_dept_specific");
        category = getIntent().getStringExtra("product_cat_specific");


        requestQueue = Volley.newRequestQueue(getApplicationContext());

        etProductName = (EditText) findViewById(R.id.etProductName);

        etProductBuyPrice = (EditText) findViewById(R.id.etProductBuyPrice);

        etProductSellPrice = (EditText) findViewById(R.id.etProductSellPrice);

        etProductAvialableQuantity = (EditText) findViewById(R.id.etProductAvialableQuantity);

        etProductAvialableNotification = (EditText) findViewById(R.id.etProductAvialableNotification);

        etProductDesc = (EditText) findViewById(R.id.etProductDesc);

        swProductReservation = (Switch) findViewById(R.id.swProductReservation);
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

        swProductDeliver = (Switch) findViewById(R.id.swProductDeliver);
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

        btnProductImage = (Button) findViewById(R.id.btnProductImage);
        btnProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        imgProductImage = (ImageView) findViewById(R.id.imgProductImage);

        getLastId(AddProduct.this);

        btnAddProduct = (Button) findViewById(R.id.btnAddShopProduct);
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
                image = getStringImage(bitmap);

                if (isNetworkAvailable()) {
                    if (!(name.isEmpty()) && !(buy.isEmpty()) && !(sell.isEmpty()) && !(avnotification.isEmpty())
                            && !(avquantity.isEmpty()) && !(desc.isEmpty())) {
                        if (image == null) {
                            Toast.makeText(getApplicationContext(), R.string.emptyimage, Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                addproduct(department, category, name, buy, sell, desc, avnotification, avquantity,
                                        deliver1, reservation1, image, pid);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
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
        finish();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imgProductImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public String getStringImage(Bitmap bmp) {
        if (bmp == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }
    }

    public void addproduct(final String department, final String cat, final String name, final String buy,
                           final String sell, final String desc, final String avnotification, final String avquantity,
                           final String deliver, final String reservation, final String img, final String pid) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "", getString(R.string.waiting), false, false);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_SHOP_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        key = 1;
                        Intent intent = new Intent(AddProduct.this, Home.class);
                        startActivity(intent);
                        finish();
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

                params.put("product_cat", cat);
                params.put("product_dept", department);
                params.put("product_name", name);
                params.put("product_buy", buy);
                params.put("product_sell", sell);
                params.put("product_desc", desc);
                params.put("product_av_notification", avnotification);
                params.put("product_av_quantity", avquantity);
                params.put("product_deliver", deliver);
                params.put("product_reservation", reservation);
                params.put("shop_id", Home.shop_id);
                params.put("product_image", img);

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    public void getLastId(final Activity activity) {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GET_PRODUCT_ID,
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


                                // Now store the user in SQLite

                                JSONObject shop = data.getJSONObject(i);

                                pid = shop.getString("id");

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //Adding the string request to the queue
        requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
