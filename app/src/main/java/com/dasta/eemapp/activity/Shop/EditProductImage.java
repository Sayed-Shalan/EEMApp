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
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 09/10/2017.
 */

public class EditProductImage extends Activity {

    ImageView imgEditShopImage;
    Button btnEditChooseImage, btnShopImageEdit;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;

    public static String image, id, dept, cat, imgName;
    public static int key = 0;
    public static int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_image);

        flag = 1;
        Appointment.flag = 0;
        Category.flag = 0;
        LiveInfo.flag = 0;
        Offer.flag = 0;
        com.dasta.eemapp.fragment.Shop.Home.flag = 0;
        Product.flag = 0;

        id = getIntent().getStringExtra("edit_product_image_id");
        cat = getIntent().getStringExtra("edit_product_image_cat");
        dept = getIntent().getStringExtra("edit_product_image_dept");
        imgName = getIntent().getStringExtra("edit_product_image_name");

        imgEditShopImage = (ImageView) findViewById(R.id.imgEditProductImage);

        btnEditChooseImage = (Button) findViewById(R.id.btnEditChooseProductImage);
        btnEditChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        btnShopImageEdit = (Button) findViewById(R.id.btnProductImageEdit);
        btnShopImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {
                    image = getStringImage(bitmap);
                    editshopimage(id, image, imgName);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imgEditShopImage.setImageBitmap(bitmap);
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
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }
    }

    public void editshopimage(final String shopid, final String img, final String imgName) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "", getString(R.string.waiting), false, false);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_EDIT_SHOP_PRODUCT_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        key = 1;
                        Intent intent = new Intent(EditProductImage.this, Home.class);
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

                params.put("productid", shopid);
                params.put("productimage", img);
                params.put("imagename", imgName);

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
