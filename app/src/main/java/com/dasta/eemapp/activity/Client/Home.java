package com.dasta.eemapp.activity.Client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Category;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.adapter.Client.CustomArrarAdapter;
import com.dasta.eemapp.fragment.Client.AllOfferReservation;
import com.dasta.eemapp.fragment.Client.AllSingleOffer;
import com.dasta.eemapp.fragment.Client.ChatRoom;
import com.dasta.eemapp.fragment.Client.ClientChat;
import com.dasta.eemapp.fragment.Client.ClientOrder;
import com.dasta.eemapp.fragment.Client.EditProfileData;
import com.dasta.eemapp.fragment.Client.Home_Category;
import com.dasta.eemapp.fragment.Client.Offer;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.Client.SessionManager;
import com.dasta.eemapp.helper.setwget;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;

import com.facebook.share.widget.ShareDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends FragmentActivity {

    TextView txtOffer;
    ImageView homeImg;

    public static AutoCompleteTextView txtTitle;
    RequestQueue requestQueue;
    ArrayList<setwget> CustomListViewValuesArr = new ArrayList<>();
    private DrawerLayout drawer_layout;
    private SessionManager session;
    NavigationView navigationView;
    View navigationHeader;
    ImageView headerImg;
    private SQLiteHandler db;
    BottomSheetLayout bottomSheetLayout;
    Bitmap sharedBitmap;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private ProgressDialog pDialog;
    public static Activity context;
    TextView navNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);
        context=Home.this;


        // Session manager
        session = new SessionManager(getApplicationContext());

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        HashMap<String, String> user = db.getUserDetails();


        navigationView=(NavigationView) findViewById(R.id.navigationView);
        navigationHeader=navigationView.getHeaderView(0);
        headerImg=(ImageView)navigationHeader.findViewById(R.id.user_navigation_view_header_imageView);
        navNameTxt=navigationHeader.findViewById(R.id.user_navigation_header_userName);
        sharedBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.logo_home);
        bottomSheetLayout =(BottomSheetLayout) findViewById(R.id.client_home_bottomsheet);
        navNameTxt.setText(user.get("username"));
        Bitmap bm =  decodeSampledBitmapFromResource(getResources(), R.drawable.logo_home,
                FrameLayout.LayoutParams.MATCH_PARENT, 350); // problem here,incoming different sizes won't work
        Bitmap resized = Bitmap.createScaledBitmap(bm,(int)(bm.getWidth()*0.3), (int)(bm.getHeight()*0.3), true);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), resized);
        drawable.setCircular(true);
        headerImg.setImageDrawable(drawable);
        //headerImg.setImageDrawable(getResources().getDrawable(R.drawable.logo_home));

        homeImg=(ImageView) findViewById(R.id.imgClientHomeHome);
        drawer_layout=(DrawerLayout) findViewById(R.id.drawerLayout);



        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(Gravity.RIGHT);
            }
        });

        txtOffer = (TextView) findViewById(R.id.txtClientHomeOffer);
        txtOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Offer offer = new Offer();
                Bundle bundle=new Bundle();
                bundle.putString("from","home");
                offer.setArguments(bundle);
                ft.replace(R.id.fClientHomeLayout, offer);
                ft.commit();

            }
        });

        //on navigation item click
        if (session.isLoggedIn()){
            navigationView.getMenu().getItem(1).setVisible(false);
        }else {
            navigationView.getMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(4).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            navigationView.getMenu().getItem(6).setVisible(false);


        }
        setNavigationItemClick();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        txtTitle = (AutoCompleteTextView) findViewById(R.id.txtClientHomeTitle);

        //Creating a string request
        StringRequest reqShop = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SEARCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        try {
                            JSONObject jObj = new JSONObject(response);

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Home.this,
                                    R.layout.l_spinner_text);

                            /***  ***/
                            JSONArray data = jObj.getJSONArray("result");
                            /*** Data Return from server ***/
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject shop = data.getJSONObject(i);

                                String sname = shop.getString("sname");
                                String sphone = shop.getString("sphone");
                                String saddress = shop.getString("saddress");
                                String simg = shop.getString("simg");
                                String slat = shop.getString("slat");
                                String slng = shop.getString("slng");
                                String sid = shop.getString("sid");
                                String sactive = shop.getString("sactive");

                                setwget sw = new setwget();
                                sw.setId(sid);
                                sw.setAddress(saddress);
                                sw.setPhone(sphone);
                                sw.setTitle(sname);
                                sw.setImgurl(simg);
                                sw.setLat(slat);
                                sw.setLng(slng);
                                sw.setDesc(sactive);

                                CustomListViewValuesArr.add(sw);

                                arrayAdapter.add(sname);

                            }
                            txtTitle.setAdapter(arrayAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت..", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(getApplicationContext(), "لا يوجد اتصال بالانترنت..", Toast.LENGTH_LONG).show();
                    }
                });

        //Adding the string request to the queue
        requestQueue.add(reqShop);

        txtTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final setwget tempValues = CustomListViewValuesArr.get(position);
                Home_Shop.keySearch = 1;
                Intent intent = new Intent(Home.this, Home_Shop.class);
                intent.putExtra("search_shop_id", tempValues.getId());
                intent.putExtra("search_shop_address", tempValues.getAddress());
                intent.putExtra("search_shop_phone", tempValues.getPhone());
                intent.putExtra("search_shop_name", tempValues.getTitle());
                intent.putExtra("search_shop_img", tempValues.getImgurl());
                intent.putExtra("search_shop_lat", tempValues.getLat());
                intent.putExtra("search_shop_lng", tempValues.getLng());
                intent.putExtra("search_shop_active", tempValues.getDesc());
                startActivity(intent);
            }
        });

        Intent activityIntent=getIntent();
        if (activityIntent!=null && activityIntent.getData()!=null && (activityIntent.getData().getScheme().equals("http"))){
            Uri data = activityIntent.getData();
            //Toast.makeText(Home.this,data.toString(),Toast.LENGTH_SHORT).show();
            final List<String> pathSegments = data.getPathSegments();
            if(pathSegments.size()>0) {
               // Toast.makeText(Home.this, pathSegments.get(4), Toast.LENGTH_SHORT).show(); // This will give you prefix as path
                pDialog = new ProgressDialog(Home.this);
                // Showing progress dialog before making http request
                pDialog.setMessage("جاري التحميل ..");
                pDialog.show();
                RequestQueue requestQueue;
                requestQueue = Volley.newRequestQueue(Home.this);

                //Creating a string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SINGLE_SHOP,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //If we are getting success from server
                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    /* **  ***/
                                    JSONArray data = jObj.getJSONArray("result");
                                    /* ** Data Return from server ***/


                                        JSONObject shop = data.getJSONObject(0);

                                        String id = shop.getString("id");
                                        String title = shop.getString("title");
                                        String img = shop.getString("img");
                                        String phone = shop.getString("phone");
                                        String address = shop.getString("address");
                                        String lat = shop.getString("lat");
                                        String lng = shop.getString("lng");

                                        // Toast.makeText(getActivity(), name, Toast.LENGTH_LONG).show();
                                        setwget sw = new setwget();
                                        sw.setId(id);
                                        sw.setTitle(title);
                                        sw.setImgurl(img);
                                        sw.setPhone(phone);
                                        sw.setAddress(address);
                                        sw.setLat(lat);
                                        sw.setLng(lng);

                                    Home_Shop.keySearch = 2;
                                    Intent intent = new Intent(Home.this, Home_Shop.class);
                                    intent.putExtra("view_shop_id", sw.getId());
                                    intent.putExtra("view_shop_address", sw.getAddress());
                                    intent.putExtra("view_shop_phone", sw.getPhone());
                                    intent.putExtra("view_shop_name", sw.getTitle());
                                    intent.putExtra("view_shop_img", sw.getImgurl());

                                    //Toast.makeText(getActivity(),tempValues.getLat()+" + "+tempValues.getLng(),Toast.LENGTH_SHORT).show();

                                    intent.putExtra("shop_profile_map_lat", sw.getLat());
                                    intent.putExtra("shop_profile_map_lng", sw.getLng());

                                    startActivity(intent);
                                    pDialog.dismiss();




                                } catch (JSONException e) {
                                    e.printStackTrace();
                                   // Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //You can handle error here if you want
                                //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        //Adding parameters to request
                        params.put("id",pathSegments.get(4) );

                        //returning parameter
                        return params;
                    }
                };

                requestQueue.add(stringRequest);



               /* imm.hideSoftInputFromWindow(txtTitle.getWindowToken(), 0);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Home_Category client_home_category = new Home_Category();
                Bundle bundle=new Bundle();
                bundle.putBoolean("from_share",true);
                bundle.putInt("id", Integer.parseInt(pathSegments.get(4)));
                client_home_category.setArguments(bundle);
                ft.add(R.id.fClientHomeLayout, client_home_category);
                ft.commit();
                // Check if no view has focus:
                InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);*/
//                intent.putExtra("view_shop_address", pathSegments.get(5));
//                intent.putExtra("view_shop_phone", pathSegments.get(6));
//                intent.putExtra("view_shop_name", pathSegments.get(7));
//                intent.putExtra("view_shop_img", pathSegments.get(10));
//
//                //Toast.makeText(getActivity(),tempValues.getLat()+" + "+tempValues.getLng(),Toast.LENGTH_SHORT).show();
//
//                intent.putExtra("shop_profile_map_lat", pathSegments.get(8));
//                intent.putExtra("shop_profile_map_lng", pathSegments.get(9));
//
//                startActivity(intent);
            }else {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Home_Category client_home_category = new Home_Category();
                ft.add(R.id.fClientHomeLayout, client_home_category);
                ft.commit();
                // Check if no view has focus:
                InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtTitle.getWindowToken(), 0);
            }
        }else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Home_Category client_home_category = new Home_Category();
            ft.add(R.id.fClientHomeLayout, client_home_category);
            ft.commit();
            // Check if no view has focus:
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtTitle.getWindowToken(), 0);
        }

//        if (ChatRoom.active){
//            Toast.makeText(Home.this,"CHAT ROOM IS OPENED",Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(Home.this,"CHAT ROOM ISN'T OPENED",Toast.LENGTH_SHORT).show();
//        }
    }

    private void setNavigationItemClick() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                item.setCheckable(true);
                switch (item.getItemId()){
                    case R.id.user_menu_action_home:
                        FragmentTransaction ft0 = getSupportFragmentManager().beginTransaction();
                        Home_Category client_home_category = new Home_Category();
                        ft0.add(R.id.fClientHomeLayout, client_home_category);
                        ft0.commit();
                        drawer_layout.closeDrawer(Gravity.RIGHT);
                        return true;
                    case R.id.user_menu_action_login:
                        Intent intent = new Intent(Home.this, Login.class);
                        startActivity(intent);
                        drawer_layout.closeDrawer(Gravity.RIGHT);

                        return true;
                    case R.id.user_menu_action_edit_data:
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        EditProfileData editProfileData = new EditProfileData();
                        ft.replace(R.id.fClientHomeLayout, editProfileData);
                        ft.commit();
                        drawer_layout.closeDrawer(Gravity.RIGHT);

                        return true;
                    case R.id.user_menu_action_your_orders:
                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                        ClientOrder clientOrder = new ClientOrder();
                        ft2.replace(R.id.fClientHomeLayout, clientOrder);
                        ft2.commit();
                        drawer_layout.closeDrawer(Gravity.RIGHT);

                        return true;
                    case R.id.user_menu_action_chat:
                        FragmentTransaction ft3 =getSupportFragmentManager().beginTransaction();
                        ClientChat clientChat = new ClientChat();
                        Bundle bundly=new Bundle();
                        bundly.putBoolean("home",true);
                        clientChat.setArguments(bundly);
                        ft3.replace(R.id.fClientHomeLayout, clientChat);
                        ft3.commit();
                        drawer_layout.closeDrawer(Gravity.RIGHT);

                        return true;
                    case R.id.user_menu_action_logout:
                        session.setLogin(false);
                        AccessToken.setCurrentAccessToken(null);
                        db.deleteUsers();
                        // Launching the login activity
                        Intent intent2 = new Intent(Home.this, Login.class);
                        startActivity(intent2);
                        finish();
                        drawer_layout.closeDrawer(Gravity.RIGHT);

                        return true;

                    case R.id.user_menu_action_share:
                        shareApplicationToSocialMedia();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void shareApplicationToSocialMedia() {

        //Share Intent for Text Only
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"http://dasta.net/data/eem/Client/redirect_splash.php");
        shareIntent.setType("text/plain");

        //Share Intent for Text and Image
        if (Utility.checkPermissionREAD_EXTERNAL_STORAGE(Home.this)&&
                Utility.checkPermissionWRITE_EXTERNAL_STORAGE(Home.this)){


            final Intent imgShareIntent = new Intent(Intent.ACTION_SEND);
            imgShareIntent.setType("image/*");
            imgShareIntent.putExtra(Intent.EXTRA_STREAM,Utility.getImageUri(Home.this, sharedBitmap));
            imgShareIntent.putExtra(Intent.EXTRA_TEXT,"http://dasta.net/data/eem/Client/redirect_splash.php");


            IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(Home.this, imgShareIntent, "مشاركة...",
                    new IntentPickerSheetView.OnIntentPickedListener() {
                        @Override
                        public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo) {
                            bottomSheetLayout.dismissSheet();
                            if (activityInfo.componentName.getPackageName().equals("com.facebook.orca")) {
                                final Intent imgShareIntent2 = new Intent(Intent.ACTION_SEND);
                                imgShareIntent2.setType("text/plain");
                                imgShareIntent2.putExtra(Intent.EXTRA_TEXT,
                                        "http://dasta.net/data/eem/Client/redirect_splash.php");
                                startActivity(activityInfo.getConcreteIntent(imgShareIntent2));
                            }else if (activityInfo.componentName.getPackageName().equals("com.facebook.katana")){
                                    //startActivity(activityInfo.getConcreteIntent(imgShareIntent));


                                    ShareLinkContent content = new ShareLinkContent.Builder()
                                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.dasta.eemapp"))
                                            .build();
                                    shareDialog.show(content);

/*
                                SharePhoto photo = new SharePhoto.Builder()
                                        .setBitmap(sharedBitmap)
                                        .build();
                                SharePhotoContent photoContent = new SharePhotoContent.Builder()
                                        .addPhoto(photo)
                                        .build();



                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setContentUrl(Uri.parse("http://dasta.net/data/eem/Client/redirect_splash.php"))
                                            .setShareHashtag(new ShareHashtag.Builder()
                                                    .setHashtag("#EEM_App")
                                                    .build()).setQuote("Connect on a global scale.")
                                            .setContentDescription("Welcome To EEM App").setImageUrl(Utility.getImageUri(Home.this, sharedBitmap))
                                            .build();

                                    shareDialog.show(linkContent);
                                }*/

//                                final Intent imgShareIntent2 = new Intent(Intent.ACTION_SEND);
//                                imgShareIntent2.setType("text/plain");
//                                imgShareIntent2.putExtra(Intent.EXTRA_TEXT,"http://dasta.net/data/eem/Client/redirect_splash.php");
//                                startActivity(activityInfo.getConcreteIntent(imgShareIntent2));


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

    @Override
    public void onBackPressed() {

        if (drawer_layout.isDrawerOpen(Gravity.RIGHT)){
            drawer_layout.closeDrawer(Gravity.RIGHT);
            return;
        }

        if (Offer.flag == 1) {
            Offer.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Home_Category client_home_category = new Home_Category();
            ft.replace(R.id.fClientHomeLayout, client_home_category);
            ft.commit();
        } else if (AllSingleOffer.flag == 1) {
            AllSingleOffer.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Offer client_home_category = new Offer();
            Bundle bundle=new Bundle();
            bundle.putString("from",AllSingleOffer.bundle.getString("from","home"));
            client_home_category.setArguments(bundle);
            ft.replace(R.id.fClientHomeLayout, client_home_category);
            ft.commit();
        } else if (AllOfferReservation.flag == 1) {
            AllOfferReservation.flag = 0;
            Bundle bundle = new Bundle();
            bundle.putString("all_single_offer_price", AllSingleOffer.price);
            bundle.putString("all_single_offer_desc", AllSingleOffer.desc);
            bundle.putString("all_single_offer_id", AllSingleOffer.id);
            bundle.putString("all_single_offer_img", AllSingleOffer.img);
            bundle.putString("all_single_offer_quantity", AllSingleOffer.quantity);
            bundle.putString("all_single_offer_title", AllSingleOffer.title);
            bundle.putString("all_single_offer_per",AllSingleOffer.per);
            bundle.putString("all_single_offer_deliver",AllSingleOffer.deliver);
            bundle.putString("all_single_offer_reservation",AllSingleOffer.reservation);
            bundle.putString("from","home");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            AllSingleOffer productReservation = new AllSingleOffer();
            productReservation.setArguments(bundle);
            ft.replace(R.id.fClientHomeLayout, productReservation);
            ft.commit();
        }else if (ChatRoom.flag == 1){
            FragmentTransaction ft3 =getSupportFragmentManager().beginTransaction();
            ClientChat clientChat = new ClientChat();
            ChatRoom.flag=0;
            Bundle bundly=new Bundle();
            bundly.putBoolean("home",true);
            clientChat.setArguments(bundly);
            ft3.replace(R.id.fClientHomeLayout, clientChat);
            ft3.commit();
        } /*else if (Home_Category.flag2==1){
//                super.onBackPressed();
            Home_Category.flag2=0;

            //super.onBackPressed();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Home_Category client_home_category = new Home_Category();
            ft.add(R.id.fClientHomeLayout, client_home_category);
            ft.commit();
            // Check if no view has focus:
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtTitle.getWindowToken(), 0);
        }*/else {
            Intent intent = new Intent(Home.this, Category.class);
            startActivity(intent);
            finish();
        }
    }

    public static int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image

        final int height = options.outHeight;

        final int width = options.outWidth;

        int inSampleSize = 1;        if (height > reqHeight || width > reqWidth) {            final int halfHeight = height / 2;

            final int halfWidth = width / 2;            // Calculate the largest inSampleSize value that is a power of 2 and keeps both

            // height and width larger than the requested height and width.

            while ((halfHeight / inSampleSize) >= reqHeight

                    && (halfWidth / inSampleSize) >= reqWidth) {

                inSampleSize *= 2;

            }

        }        return inSampleSize;

    }    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,

                                                              int reqWidth, int reqHeight) {        // First decode with inJustDecodeBounds=true to check dimensions

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(res, resId, options);        // Calculate inSampleSize

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);        // Decode bitmap with inSampleSize set

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);

    }
}
