package com.dasta.eemapp.fragment.Shop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dasta.eemapp.activity.Shop.*;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.adapter.Shop.Adapter_CustomList_All_Product;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.WebServies;
import com.dasta.eemapp.helper.setwget;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 28/06/2017.
 */

public class Product extends Fragment {

    private FloatingActionButton fabAddProduct;
    private ListView lstShopProduct;
    ArrayList<setwget> CustomListViewValuesArr = new ArrayList<setwget>();
    Adapter_CustomList_All_Product adapter;
    Resources res;
    RequestQueue requestQueue;
    private ProgressDialog pDialog;
    public static String dept, cat;
    WebServies webServies = new WebServies();
    public static int flag = 0;
    BottomSheetLayout bottomSheetLayout;
    Bitmap sharedBitmap;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    setwget tempValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_shop_product, null, false);
        FacebookSdk.sdkInitialize(getActivity());
        callbackManager = CallbackManager.Factory.create();
        sharedBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.logo_home);
        bottomSheetLayout =(BottomSheetLayout) v.findViewById(R.id.client_home_bottomsheet);
        shareDialog = new ShareDialog(getActivity());

        Offer.flag = 0;
        LiveInfo.flag = 0;
        Category.flag = 0;
        Appointment.flag = 0;
        Home.flag = 0;
        Department.flag = 0;
        AddDepartment.flag = 0;

        flag = 1;


        dept = getArguments().getString("shop_dept_specific");
        cat = getArguments().getString("shop_cat_specific");

        com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(dept);

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage(getString(R.string.loading));
        pDialog.show();

        lstShopProduct = (ListView) v.findViewById(R.id.lstShopProduct);

        requestQueue = Volley.newRequestQueue(getActivity());

        res = getResources();

        if (isNetworkAvailable()) {
            hidePDialog();
            //Creating a string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ALL_PRODUCT,
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

                                    String id = shop.getString("id");
                                    String title = shop.getString("product_name");
                                    String dept = shop.getString("product_dept");
                                    String sell = shop.getString("product_sell");
                                    String buy = shop.getString("product_buy");
                                    String deliver = shop.getString("product_deliver");
                                    String reservation = shop.getString("product_reservation");
                                    String avnotification = shop.getString("product_av_notification");
                                    String avquantity = shop.getString("product_av_quantity");
                                    String date_enter = shop.getString("product_date_enter");
                                    String imgurl = shop.getString("product_image");
                                    String desc = shop.getString("product_desc");
                                    String imagename = shop.getString("image_name");

                                    //
                                    setwget sw = new setwget();

                                    sw.setId(id);
                                    sw.setTitle(title);
                                    sw.setDept(dept);
                                    sw.setSell(sell);
                                    sw.setBuy(buy);

                                    if (deliver.equals("0")) {
                                        sw.setDeliver(getString(R.string.deliverNo));
                                    } else {
                                        sw.setDeliver(getString(R.string.deliverYes));
                                    }

                                    if (reservation.equals("0")) {
                                        sw.setReservation(getString(R.string.reservationNo));
                                    } else {
                                        sw.setReservation(getString(R.string.reservationYes));
                                    }

                                    sw.setNotification(avnotification);
                                    sw.setQuantity(avquantity);
                                    sw.setDate(date_enter);
                                    sw.setImgurl(imgurl);
                                    sw.setDesc(desc);
                                    sw.setImgName(imagename);

                                    CustomListViewValuesArr.add(sw);
                                }

                                adapter = new Adapter_CustomList_All_Product(getActivity(), CustomListViewValuesArr, res, Product.this);
                                adapter.notifyDataSetChanged();
                                adapter.notifyDataSetInvalidated();
                                lstShopProduct.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            hidePDialog();
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put("id", com.dasta.eemapp.activity.Shop.Home.shop_id);
                    params.put("dept", dept);

                    //returning parameter
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //Adding the string request to the queue
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(getActivity(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
        }


        fabAddProduct = (FloatingActionButton) v.findViewById(R.id.fabShopProduct);
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddProduct.class);
                intent.putExtra("product_dept_specific", dept);
                intent.putExtra("product_cat_specific", cat);
                startActivity(intent);
            }
        });

        return v;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void onItemClick(int position) {

          tempValues = CustomListViewValuesArr.get(position);

        //Toast.makeText(getActivity(), tempValues.getId(), Toast.LENGTH_LONG).show();

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                getActivity(), AlertDialog.THEME_HOLO_LIGHT);

        builderSingle.setTitle(getString(R.string.productData));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

        arrayAdapter.add(getString(R.string.edit));
        //arrayAdapter.add(getString(R.string.editproductimage));
        arrayAdapter.add(getString(R.string.sale));
        arrayAdapter.add(getString(R.string.delete));
        arrayAdapter.add("Share On FaceBook");

        builderSingle.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            flag = 0;
                            Intent intent = new Intent(getActivity(), EditProduct.class);
                            intent.putExtra("ShopCat", cat);
                            intent.putExtra("EditProductId", tempValues.getId());
                            intent.putExtra("EditProductDept", tempValues.getDept());
                            intent.putExtra("EditProductName", tempValues.getTitle());
                            intent.putExtra("EditProductSell", tempValues.getSell());
                            intent.putExtra("EditProductBuy", tempValues.getBuy());
                            intent.putExtra("EditProductDeliver", tempValues.getDeliver());
                            intent.putExtra("EditProductDesc", tempValues.getDesc());
                            intent.putExtra("EditProductNotification", tempValues.getNotification());
                            intent.putExtra("EditProductQuantity", tempValues.getQuantity());
                            intent.putExtra("EditProductReservation", tempValues.getReservation());
                            startActivity(intent);
                        } /*else if (which == 1) {
                            flag = 0;
                            Intent intent = new Intent(getActivity(), EditProductImage.class);
                            intent.putExtra("edit_product_image_id", tempValues.getId());
                            intent.putExtra("edit_product_image_dept", tempValues.getDept());
                            intent.putExtra("edit_product_image_cat", cat);
                            intent.putExtra("edit_product_image_name", tempValues.getImgName());
                            startActivity(intent);
                        } */ else if (which == 1) {
                            flag = 0;
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            AddOfferProduct department = new AddOfferProduct();
                            Bundle bundle = new Bundle();
                            bundle.putString("offer_sell_price_product", tempValues.getSell());
                            bundle.putString("offer_id_product", tempValues.getId());
                            bundle.putString("offer_dept_product", tempValues.getDept());
                            bundle.putString("offer_cat_product", cat);
                            department.setArguments(bundle);
                            ft.replace(R.id.fShopHomeLayout, department);
                            ft.commit();
                        } else if (which == 2) {
                            //Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                    getActivity());
                            builderSingle.setTitle(getString(R.string.delete));
                            builderSingle.setIcon(R.drawable.logo_home);
                            builderSingle.setMessage(getString(R.string.deleteMessage));
                            builderSingle.setNegativeButton(getString(R.string.questionNo), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderSingle.setPositiveButton(getString(R.string.questionYes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    webServies.delShopProduct(getActivity(), tempValues.getId(), cat, dept);
                                }
                            });
                            builderSingle.show();
                        } else {
                            handleShareLink();
                            /*if (ShareDialog.canShow(ShareLinkContent.class)) {

                                *//*ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(tempValues.getImgurl()))
                                        .setContentTitle(tempValues.getTitle())
                                        .setContentDescription(tempValues.getSell())
                                        .setShareHashtag(new ShareHashtag.Builder()
                                                .setHashtag("#EEM App")
                                                .build())
                                        .build();
                                shareDialog.show(content);
                                ShareApi.share(content, null);*//*

                            }*/
                        }
                    }
                });
        builderSingle.show();
    }
    private void handleShareLink() {



        //Share Intent for Text and Image
        if (Utility.checkPermissionREAD_EXTERNAL_STORAGE(getActivity())&&Utility.checkPermissionWRITE_EXTERNAL_STORAGE(getActivity())){


            final Intent imgShareIntent = new Intent(Intent.ACTION_SEND);
            imgShareIntent.setType("image/*");
            imgShareIntent.putExtra(Intent.EXTRA_STREAM,Utility.getImageUri(getActivity(), sharedBitmap));

            String url="http://dasta.net/data/eem/Client/redirect_single_product.php/"
                    .concat(tempValues.getPrice()).concat("/").concat(tempValues.getDesc()).concat("/").concat(tempValues.getTitle()).concat("/").concat(tempValues.getImgurl().substring(45)).
                            concat("/").concat(tempValues.getId())
                    .concat("/").concat(tempValues.getQuantity()).concat("/").concat(tempValues.getReservation()).concat("/").
                            concat(tempValues.getDeliver());
            URI uri = null;
            try {
                uri = new URI(url.replace(" ", "%20"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            imgShareIntent.putExtra(Intent.EXTRA_TEXT,uri.toString());


            final URI finalUri = uri;
            IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(getActivity(), imgShareIntent, "مشاركة...",
                    new IntentPickerSheetView.OnIntentPickedListener() {
                        @Override
                        public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo) {
                            bottomSheetLayout.dismissSheet();
                           if (activityInfo.componentName.getPackageName().equals("com.facebook.orca")){
                               final Intent imgShareIntent2 = new Intent(Intent.ACTION_SEND);
                               imgShareIntent2.setType("text/plain");
                               imgShareIntent2.putExtra(Intent.EXTRA_TEXT, finalUri.toString());
                               startActivity(activityInfo.getConcreteIntent(imgShareIntent2));

                           }else if (activityInfo.componentName.getPackageName().equals("com.facebook.katana")){
                                //startActivity(activityInfo.getConcreteIntent(imgShareIntent));

                                /*SharePhoto photo = new SharePhoto.Builder()
                                        .setBitmap(sharedBitmap)
                                        .build();
                                SharePhotoContent photoContent = new SharePhotoContent.Builder()
                                        .addPhoto(photo)
                                        .build();



                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.Slack&hl=en"))
                                            .setShareHashtag(new ShareHashtag.Builder()
                                                    .setHashtag("#EEM_App")
                                                    .build()).setQuote("Connect on a global scale.")
                                            .setContentDescription("Welcome To EEM App").setImageUrl(Utility.getImageUri(Home.this, sharedBitmap))
                                            .build();

                                    shareDialog.show(linkContent);
                                }*/


                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(finalUri.toString()))
                                        .build();
                                shareDialog.show(content);


                            }else if (activityInfo.componentName.getPackageName().equals("com.twitter.android")){

                                //Toast.makeText(Home.this,"Twitter Selected",Toast.LENGTH_SHORT).show();
                                startActivity(activityInfo.getConcreteIntent(imgShareIntent));

                            }else{

                                // Toast.makeText(Home.this,"FaceBook,Twitter Not Selected",Toast.LENGTH_SHORT).show();
                                startActivity(activityInfo.getConcreteIntent(imgShareIntent));
                            }
                        }
                    });

            // Filter out built in sharing options such as bluetooth and beam.
            intentPickerSheet.setFilter(new IntentPickerSheetView.Filter() {

                @Override
                public boolean include(IntentPickerSheetView.ActivityInfo info) {
                    return !info.componentName.getPackageName().startsWith("com.android");
                }

            });
            // Sort activities in reverse order for no good reason
            intentPickerSheet.setSortMethod(new Comparator<IntentPickerSheetView.ActivityInfo>() {
                @Override
                public int compare(IntentPickerSheetView.ActivityInfo lhs, IntentPickerSheetView.ActivityInfo rhs) {
                    return rhs.label.compareTo(lhs.label);
                }
            });
            bottomSheetLayout.showWithSheetView(intentPickerSheet);

        }/*else {
            Utility.showDialog("السماح للتطبيق بحفظ الصور ف الذاكرة الخارجية ؟",Home.this,"android.permission.WRITE_EXTERNAL_STORAGE");
        }*/


    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
