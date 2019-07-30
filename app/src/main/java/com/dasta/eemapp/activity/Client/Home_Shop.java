package com.dasta.eemapp.activity.Client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Utility;
import com.dasta.eemapp.fragment.Client.AllOfferReservation;
import com.dasta.eemapp.fragment.Client.AllSingleOffer;
import com.dasta.eemapp.fragment.Client.ChatRoom;
import com.dasta.eemapp.fragment.Client.ClientChat;
import com.dasta.eemapp.fragment.Client.ClientOrder;
import com.dasta.eemapp.fragment.Client.DepartmentProfile;
import com.dasta.eemapp.fragment.Client.EditProfileData;
import com.dasta.eemapp.fragment.Client.Home_Category;
import com.dasta.eemapp.fragment.Client.MapProfile;
import com.dasta.eemapp.fragment.Client.Offer;
import com.dasta.eemapp.fragment.Client.OfferProfile;
import com.dasta.eemapp.fragment.Client.OfferReservation;
import com.dasta.eemapp.fragment.Client.ProductProfile;
import com.dasta.eemapp.fragment.Client.ProductReservation;
import com.dasta.eemapp.fragment.Client.Shop;
import com.dasta.eemapp.fragment.Client.ShopProfile;
import com.dasta.eemapp.fragment.Client.SingleOffer;
import com.dasta.eemapp.fragment.Client.SingleProduct;
import com.dasta.eemapp.fragment.Client.VideoProfile;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.Client.SessionManager;
import com.dasta.eemapp.helper.WebServies;
import com.dasta.eemapp.helper.setwget;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
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

public class Home_Shop extends FragmentActivity {

    public static String title, city, mall,cat;
    //public static TextView txtClientShopTitle;
    //public static ImageView imgClientShopHome;
    //public static ImageView imgClientShopUser;
    public static int keySearch = 0, keyUser = 0;
    //public static Spinner spCategories;
    List<String> list = new ArrayList<>();
    RequestQueue requestQueue;
    public static int flag = 0, flag1 = 0;

    ImageView homeImg;
    TextView txtOffer;
    public static LinearLayout toolBar;

    public static AutoCompleteTextView txtTitle;
    NavigationView navigationView;
    View navigationHeader;
    ImageView headerImg;
    private SQLiteHandler db;
    BottomSheetLayout bottomSheetLayout;
    Bitmap sharedBitmap;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private SessionManager session;
    ArrayList<setwget> CustomListViewValuesArr = new ArrayList<>();
    private DrawerLayout drawer_layout;
    public int cityPosition=0;
    public  int shopCategoryPosition=0;
    public static TextView barTitle;
    TextView navNameTxt;
    public static Activity context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_client_home_shop);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        context=Home_Shop.this;

        //title = getIntent().getStringExtra("shop_cat_title");
        city = getIntent().getStringExtra("shop_city");
        cityPosition=getIntent().getIntExtra("shop_city_position",0);
        shopCategoryPosition=getIntent().getIntExtra("client_shop_cat_position",0);
        cat=getIntent().getStringExtra("client_shop_cat");

        barTitle=findViewById(R.id.barTitle);



        toolBar=(LinearLayout)findViewById(R.id.toolBar);



        //mall = getIntent().getStringExtra("shop_mall");

        /*spCategories = (Spinner) findViewById(R.id.spClientCategories);
        spCategories.setVisibility(View.VISIBLE);
        shopcategory();

        txtClientShopTitle = (TextView) findViewById(R.id.txtClientShopTitle);

        imgClientShopUser = (ImageView) findViewById(R.id.imgClientShopUser);
        imgClientShopUser.setVisibility(View.VISIBLE);
        imgClientShopUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Shop.flag == 1) {
                    UserProfile.flag = 1;
                    Intent intent = new Intent(Home_Shop.this, UserProfile.class);
                    startActivity(intent);
                }
            }
        });*/

        /*imgClientShopHome = (ImageView) findViewById(R.id.imgClientShopHome);
        imgClientShopHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shop.flag = 0;
                ShopProfile.flag = 0;
                DepartmentProfile.flag = 0;
                SingleProduct.flag = 0;
                MapProfile.flag = 0;
                OfferProfile.flag = 0;
                ProductProfile.flag = 0;
                SingleOffer.flag = 0;
                ProductReservation.flag = 0;
                flag1 = 0;
                Intent intent = new Intent(Home_Shop.this, Home.class);
                startActivity(intent);
                finish();
            }
        });*/


        // Session manager
        session = new SessionManager(getApplicationContext());

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        navigationView=(NavigationView) findViewById(R.id.navigationView);
        navigationHeader=navigationView.getHeaderView(0);
        headerImg=(ImageView)navigationHeader.findViewById(R.id.user_navigation_view_header_imageView);
        sharedBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.logo_home);
        bottomSheetLayout =(BottomSheetLayout) findViewById(R.id.client_home_bottomsheet);
        navNameTxt=navigationHeader.findViewById(R.id.user_navigation_header_userName);
        HashMap<String, String> user = db.getUserDetails();
        navNameTxt.setText(user.get("username"));


        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.logo_home); // problem here,incoming different sizes won't work
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
                bundle.putString("from","shop");
                offer.setArguments(bundle);
                ft.replace(R.id.fClientShop, offer);
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
        barTitle.setVisibility(View.GONE);
        txtTitle.setVisibility(View.VISIBLE);
        //Creating a string request
        StringRequest reqShop = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SEARCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        try {
                            JSONObject jObj = new JSONObject(response);

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Home_Shop.this,
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
                Intent intent = new Intent(Home_Shop.this, Home_Shop.class);
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


        Bundle fcmBundle=getIntent().getExtras();
        if (fcmBundle!=null){
            if (fcmBundle.containsKey("open_chat")){
                if (fcmBundle.getBoolean("open_chat",false)){

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("shop_chat_receiver_id", fcmBundle.getString("shop_chatt_receiver_id"));
                    bundle.putString("shop_chat_receiver_name", fcmBundle.getString("shop_chatt_receiver_name"));
                    ChatRoom offerProfile = new ChatRoom();
                    offerProfile.setArguments(bundle);
                    ft.replace(R.id.fClientShop, offerProfile);
                    ft.commit();
                    return;
                }
            }
        }

        boolean isdata=false;
        ProgressDialog pDialog;
        //Handle If Coming from share
        Intent activityIntent=getIntent();
        if (activityIntent!=null && activityIntent.getData()!=null && (activityIntent.getData().getScheme().equals("http"))){
            Uri data = activityIntent.getData();
            final List<String> pathSegments = data.getPathSegments();
            if(pathSegments.size()>0) {
                pDialog = new ProgressDialog(Home_Shop.this);
                // Showing progress dialog before making http request
                pDialog.setMessage("جاري التحميل ..");
                pDialog.show();
                Home_Shop.keySearch=10;


                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SingleProduct singleProduct = new SingleProduct();
                Bundle bundle = new Bundle();
                bundle.putString("single_product_price", pathSegments.get(4));
                bundle.putString("single_product_desc", pathSegments.get(5));
                bundle.putString("single_product_title", pathSegments.get(6));
               // Toast.makeText(Home_Shop.this,pathSegments.get(7),Toast.LENGTH_SHORT).show();
                bundle.putString("single_product_img", "http://dasta.net/data/eem/Upload/Shop/Product/".concat(pathSegments.get(7)));
                bundle.putString("single_product_id", pathSegments.get(8));
                bundle.putString("single_product_quantity", pathSegments.get(9));
                bundle.putString("single_product_reservation", pathSegments.get(10));
                bundle.putString("single_product_deliver", pathSegments.get(11));
                singleProduct.setArguments(bundle);
                ft.replace(R.id.fClientShop, singleProduct);
                ft.commit();


                pDialog.dismiss();
                isdata=true;
                return;
            }
        }




        if(keySearch == 10){
            Intent intent = new Intent(Home_Shop.this, Home.class);
            startActivity(intent);
            finish();
        }else if (keySearch == 1) {
            String sid = getIntent().getStringExtra("search_shop_id");
            String sname = getIntent().getStringExtra("search_shop_name");
            String sphone = getIntent().getStringExtra("search_shop_phone");
            String saddress = getIntent().getStringExtra("search_shop_address");
            String simg = getIntent().getStringExtra("search_shop_img");
            String slat = getIntent().getStringExtra("search_shop_lat");
            String slng = getIntent().getStringExtra("search_shop_lng");
            String active = getIntent().getStringExtra("search_shop_active");

            /*if (active.equals("0")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ShopClosed shopClosed = new ShopClosed();
                ft.replace(R.id.fClientShop, shopClosed);
                ft.commit();
            } else {*/
                WebServies webServies = new WebServies();
                webServies.addShopView(Home_Shop.this, sid);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ShopProfile shopProfile = new ShopProfile();
                Bundle bundle = new Bundle();
                bundle.putString("shop_client_title", sname);
                bundle.putString("shop_client_address", saddress);
                bundle.putString("shop_client_phone", sphone);
                bundle.putString("shop_client_img_url", simg);
                bundle.putString("shop_client_id", sid);
                bundle.putString("shop_client_lat", slat);
                bundle.putString("shop_client_lng", slng);
                bundle.putString("search_shop_active",active);
                shopProfile.setArguments(bundle);
                ft.replace(R.id.fClientShop, shopProfile);
                ft.commit();
           // }
        } else if (keySearch == 2) {

            String vid = getIntent().getStringExtra("view_shop_id");
            String vname = getIntent().getStringExtra("view_shop_name");
            String vphone = getIntent().getStringExtra("view_shop_phone");
            String vaddress = getIntent().getStringExtra("view_shop_address");
            String vimg = getIntent().getStringExtra("view_shop_img");
            String vlat = getIntent().getStringExtra("shop_profile_map_lat");
            String vlng = getIntent().getStringExtra("shop_profile_map_lng");

            WebServies webServies = new WebServies();
            webServies.addShopView(Home_Shop.this, vid);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ShopProfile shopProfile = new ShopProfile();
            Bundle bundle = new Bundle();
            bundle.putString("shop_client_title", vname);
            bundle.putString("shop_client_address", vaddress);
            bundle.putString("shop_client_phone", vphone);
            bundle.putString("shop_client_img_url", vimg);
            bundle.putString("shop_client_id", vid);
            bundle.putString("shop_client_lat", vlat);
            bundle.putString("shop_client_lng", vlng);
            shopProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, shopProfile);
            ft.commit();
        }
        else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Shop shop = new Shop();
            Bundle bundle = new Bundle();
            bundle.putString("client_shop_cat", cat);
            bundle.putString("client_shop_city", city);
            bundle.putInt("client_shop_city_position",cityPosition);
            bundle.putInt("client_shop_cat_position",shopCategoryPosition);

            shop.setArguments(bundle);
            ft.replace(R.id.fClientShop, shop);
            ft.commit();
        }

        // Check if no view has focus:
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtTitle.getWindowToken(), 0);

    }



    @Override
    public void onBackPressed() {
        if(keySearch == 10){
            Intent intent = new Intent(Home_Shop.this, Home.class);
            startActivity(intent);
            finish();
        }else if (ClientChat.flag==1){

            ClientChat.flag=0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (city==null||city.length()==0){
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("shop_client_title", ShopProfile.title);
                bundle.putString("shop_client_address", ShopProfile.address);
                bundle.putString("shop_client_phone", ShopProfile.phone);
                bundle.putString("shop_client_img_url", ShopProfile.logoUrl);
                bundle.putString("shop_client_id", ShopProfile.id);
                bundle.putString("shop_client_lat", ShopProfile.lat);
                bundle.putString("shop_client_lng", ShopProfile.lng);
                bundle.putString("shop_client_cat", ShopProfile.cat);
                bundle.putString("shop_client_city", ShopProfile.city);
                ShopProfile shopProfile = new ShopProfile();
                shopProfile.setArguments(bundle);
                ft2.replace(R.id.fClientShop, shopProfile);
                ft2.commit();
            }else {
                Shop shop = new Shop();
                Bundle bundle = new Bundle();
                bundle.putString("client_shop_cat", cat);
                bundle.putString("client_shop_city", city);
                bundle.putInt("client_shop_city_position",cityPosition);
                bundle.putInt("client_shop_cat_position",shopCategoryPosition);

                shop.setArguments(bundle);
                ft.replace(R.id.fClientShop, shop);
                ft.commit();

            }

        }else if (ChatRoom.flag==1){
            ChatRoom.flag=0;
            FragmentTransaction ft3 =getSupportFragmentManager().beginTransaction();
            ClientChat clientChat = new ClientChat();
            Bundle bundly=new Bundle();
            bundly.putBoolean("home",false);
            clientChat.setArguments(bundly);
            ShopProfile.flag=0;
            ft3.replace(R.id.fClientShop, clientChat);
            ft3.commit();
        }else if (ShopProfile.flag == 1) {
            ShopProfile.flag = 0;
            UserProfile.flag = 0;
            if (keySearch == 1) {
                keySearch = 0;
                Home.txtTitle.setText("");
                Intent intent = new Intent(Home_Shop.this, Home.class);
                startActivity(intent);
                finish();
            } else if (keySearch == 2) {
                keySearch = 0;
                Intent intent = new Intent(Home_Shop.this, Home.class);
                startActivity(intent);
                finish();
            } else {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("client_shop_cat", ShopProfile.cat);
                bundle.putString("client_shop_city", ShopProfile.city);
                //txtClientShopTitle.setText(ShopProfile.cat);
                //spCategories.setVisibility(View.VISIBLE);
                Shop shop = new Shop();
                shop.setArguments(bundle);
                ft.replace(R.id.fClientShop, shop);
                ft.commit();
            }
        }else if (AllSingleOffer.flag == 1) {
            AllSingleOffer.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Offer client_home_category = new Offer();
            Bundle bundle=new Bundle();
            bundle.putString("from","home_shop");
            client_home_category.setArguments(bundle);
            ft.replace(R.id.fClientShop, client_home_category);
            ft.commit();
        }else if (AllOfferReservation.flag == 1) {
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
            bundle.putString("from","home_shop");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            AllSingleOffer productReservation = new AllSingleOffer();
            productReservation.setArguments(bundle);
            ft.replace(R.id.fClientShop, productReservation);
            ft.commit();
        }/*else if (ShopClosed.flag == 1) {
            ShopClosed.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("client_shop_cat", ShopClosed.cat);
            txtClientShopTitle.setText(ShopProfile.cat);
            Shop shop = new Shop();
            shop.setArguments(bundle);
            ft.replace(R.id.fClientShop, shop);
            ft.commit();
        }*/ else if (DepartmentProfile.flag == 1) {
            DepartmentProfile.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_client_title", ShopProfile.title);
            bundle.putString("shop_client_address", ShopProfile.address);
            bundle.putString("shop_client_phone", ShopProfile.phone);
            bundle.putString("shop_client_img_url", ShopProfile.logoUrl);
            bundle.putString("shop_client_id", ShopProfile.id);
            bundle.putString("shop_client_lat", ShopProfile.lat);
            bundle.putString("shop_client_lng", ShopProfile.lng);
            bundle.putString("shop_client_cat", ShopProfile.cat);
            bundle.putString("shop_client_city", ShopProfile.city);
            ShopProfile shopProfile = new ShopProfile();
            shopProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, shopProfile);
            ft.commit();
        } else if (MapProfile.flag == 1) {
            MapProfile.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_client_title", ShopProfile.title);
            bundle.putString("shop_client_address", ShopProfile.address);
            bundle.putString("shop_client_phone", ShopProfile.phone);
            bundle.putString("shop_client_img_url", ShopProfile.logoUrl);
            bundle.putString("shop_client_id", ShopProfile.id);
            bundle.putString("shop_client_lat", ShopProfile.lat);
            bundle.putString("shop_client_lng", ShopProfile.lng);
            bundle.putString("shop_client_cat", ShopProfile.cat);
            bundle.putString("shop_client_city", ShopProfile.city);
            ShopProfile shopProfile = new ShopProfile();
            shopProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, shopProfile);
            ft.commit();
        } else if (OfferProfile.flag == 1) {
            OfferProfile.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_client_title", ShopProfile.title);
            bundle.putString("shop_client_address", ShopProfile.address);
            bundle.putString("shop_client_phone", ShopProfile.phone);
            bundle.putString("shop_client_img_url", ShopProfile.logoUrl);
            bundle.putString("shop_client_id", ShopProfile.id);
            bundle.putString("shop_client_lat", ShopProfile.lat);
            bundle.putString("shop_client_lng", ShopProfile.lng);
            bundle.putString("shop_client_cat", ShopProfile.cat);
            bundle.putString("shop_client_city", ShopProfile.city);
            bundle.putString("from","home_shop");

            ShopProfile shopProfile = new ShopProfile();
            shopProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, shopProfile);
            ft.commit();
        } /*else if (VideoProfile.flag == 1) {
            VideoProfile.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_client_title", ShopProfile.title);
            bundle.putString("shop_client_address", ShopProfile.address);
            bundle.putString("shop_client_phone", ShopProfile.phone);
            bundle.putString("shop_client_img_url", ShopProfile.logoUrl);
            bundle.putString("shop_client_id", ShopProfile.id);
            bundle.putString("shop_client_lat", ShopProfile.lat);
            bundle.putString("shop_client_lng", ShopProfile.lng);
            bundle.putString("shop_client_cat", ShopProfile.cat);
            bundle.putString("shop_client_city", ShopProfile.city);
            ShopProfile shopProfile = new ShopProfile();
            shopProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, shopProfile);
            ft.commit();
        }*/ else if (ChatRoom.flag == 1) {
            ChatRoom.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_client_title", ShopProfile.title);
            bundle.putString("shop_client_address", ShopProfile.address);
            bundle.putString("shop_client_phone", ShopProfile.phone);
            bundle.putString("shop_client_img_url", ShopProfile.logoUrl);
            bundle.putString("shop_client_id", ShopProfile.id);
            bundle.putString("shop_client_lat", ShopProfile.lat);
            bundle.putString("shop_client_lng", ShopProfile.lng);
            bundle.putString("shop_client_cat", ShopProfile.cat);
            bundle.putString("shop_client_city", ShopProfile.city);
            ShopProfile shopProfile = new ShopProfile();
            shopProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, shopProfile);
            ft.commit();
        } else if (SingleOffer.flag == 1) {
            SingleOffer.flag = 0;
            Bundle bundle = new Bundle();
            bundle.putString("shop_offer_profile_id", OfferProfile.id);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            OfferProfile productProfile = new OfferProfile();
            productProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, productProfile);
            ft.commit();
        } else if (OfferReservation.flag == 1) {
            OfferReservation.flag = 0;
            Bundle bundle = new Bundle();
            bundle.putString("single_offer_price", SingleOffer.price);
            bundle.putString("single_offer_desc", SingleOffer.desc);
            bundle.putString("single_offer_id", SingleOffer.id);
            bundle.putString("single_offer_img", SingleOffer.img);
            bundle.putString("single_offer_quantity", SingleOffer.quantity);
            bundle.putString("single_offer_title", SingleOffer.title);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SingleOffer productReservation = new SingleOffer();
            productReservation.setArguments(bundle);
            ft.replace(R.id.fClientShop, productReservation);
            ft.commit();
        } else if (ProductProfile.flag == 1) {
            ProductProfile.flag = 0;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("shop_department_profile_id", DepartmentProfile.id);
            DepartmentProfile departmentProfile = new DepartmentProfile();
            departmentProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, departmentProfile);
            ft.commit();
        } else if (SingleProduct.flag == 1) {
//            Toast.makeText(Home_Shop.this,"FROM SINGLE PRODUCT",Toast.LENGTH_SHORT).show();

            SingleProduct.flag = 0;
            Bundle bundle = new Bundle();
            bundle.putString("shop_product_profile_id", ProductProfile.id);
            bundle.putString("shop_product_department_name", ProductProfile.departmentName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ProductProfile productProfile = new ProductProfile();
            productProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, productProfile);
            ft.commit();
        } else if (ProductReservation.flag == 1) {
//            Toast.makeText(Home_Shop.this,"FROM PRODUCT RESERVATION",Toast.LENGTH_SHORT).show();
            ProductReservation.flag = 0;
            Bundle bundle = new Bundle();
            bundle.putString("single_product_price", SingleProduct.price);
            bundle.putString("single_product_desc", SingleProduct.desc);
            bundle.putString("single_product_id", SingleProduct.id);
            bundle.putString("single_product_reservation",SingleProduct.reservation);
            bundle.putString("single_product_img", SingleProduct.img);
            bundle.putString("single_product_quantity", SingleProduct.quantity);
            bundle.putString("single_product_title", SingleProduct.title);
            bundle.putString("single_product_deliver",SingleProduct.deliver);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SingleProduct productReservation = new SingleProduct();
            productReservation.setArguments(bundle);
            ft.replace(R.id.fClientShop, productReservation);
            ft.commit();
        } else if (Shop.flag == 1) {
            Shop.flag = 0;
            ShopProfile.flag = 0;
            DepartmentProfile.flag = 0;
            SingleProduct.flag = 0;
            MapProfile.flag = 0;
            OfferProfile.flag = 0;
            ProductProfile.flag = 0;
            SingleOffer.flag = 0;
            ProductReservation.flag = 0;
            flag1 = 0;
            Intent intent = new Intent(Home_Shop.this, Home.class);
            startActivity(intent);
            finish();
        }
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
                        ft0.add(R.id.fClientShop, client_home_category);
                        ft0.commit();
                        drawer_layout.closeDrawer(Gravity.RIGHT);
                        return true;
                    case R.id.user_menu_action_login:
                        Intent intent = new Intent(Home_Shop.this, Login.class);
                        startActivity(intent);
                        drawer_layout.closeDrawer(Gravity.RIGHT);

                        return true;
                    case R.id.user_menu_action_edit_data:
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        EditProfileData editProfileData = new EditProfileData();
                        ft.replace(R.id.fClientShop, editProfileData);
                        ft.commit();
                        drawer_layout.closeDrawer(Gravity.RIGHT);

                        return true;
                    case R.id.user_menu_action_your_orders:
                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                        ClientOrder clientOrder = new ClientOrder();
                        ft2.replace(R.id.fClientShop, clientOrder);
                        ft2.commit();
                        drawer_layout.closeDrawer(Gravity.RIGHT);

                        return true;
                    case R.id.user_menu_action_chat:
                        FragmentTransaction ft3 =getSupportFragmentManager().beginTransaction();
                        ClientChat clientChat = new ClientChat();
                        Bundle bundly=new Bundle();
                        bundly.putBoolean("home",false);
                        clientChat.setArguments(bundly);
                        ShopProfile.flag=0;
                        ft3.replace(R.id.fClientShop, clientChat);
                        ft3.commit();
                        drawer_layout.closeDrawer(Gravity.RIGHT);

                        return true;
                    case R.id.user_menu_action_logout:
                        session.setLogin(false);
                        AccessToken.setCurrentAccessToken(null);
                        db.deleteUsers();
                        // Launching the login activity
                        Intent intent2 = new Intent(Home_Shop.this, Login.class);
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
        //Share
        //Share Intent for Text Only
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.dasta.eemapp");
        shareIntent.setType("text/plain");

        //Share Intent for Text and Image
        if (Utility.checkPermissionREAD_EXTERNAL_STORAGE(Home_Shop.this)&&Utility.checkPermissionWRITE_EXTERNAL_STORAGE(Home_Shop.this)){


            final Intent imgShareIntent = new Intent(Intent.ACTION_SEND);
            imgShareIntent.setType("image/*");
            imgShareIntent.putExtra(Intent.EXTRA_STREAM,Utility.getImageUri(Home_Shop.this, sharedBitmap));
            imgShareIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.dasta.eemapp");


            IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(Home_Shop.this, imgShareIntent, "مشاركة...",
                    new IntentPickerSheetView.OnIntentPickedListener() {
                        @Override
                        public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo) {
                            bottomSheetLayout.dismissSheet();
                            if (activityInfo.componentName.getPackageName().equals("com.facebook.katana")){
                                /*Toast.makeText(Home.this,"FaceBook Selected",Toast.LENGTH_SHORT).show();
                                //startActivity(activityInfo.getConcreteIntent(imgShareIntent));



                                SharePhoto photo = new SharePhoto.Builder()
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
                                final Intent imgShareIntent2 = new Intent(Intent.ACTION_SEND);
                                imgShareIntent2.setType("text/plain");
                                imgShareIntent2.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.dasta.eemapp");
                                startActivity(activityInfo.getConcreteIntent(imgShareIntent2));


                            }else if (activityInfo.componentName.getPackageName().equals("com.twitter.android")){

                               // Toast.makeText(Home_Shop.this,"Twitter Selected",Toast.LENGTH_SHORT).show();
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

}
