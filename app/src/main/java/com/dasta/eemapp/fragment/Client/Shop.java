package com.dasta.eemapp.fragment.Client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.NonScrollListView;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Home_Shop;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Shop;
import com.dasta.eemapp.adapter.Client.CustomArrarAdapter;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.ViewPagerAdapter;
import com.dasta.eemapp.helper.ViewPagerAdapterTop;
import com.dasta.eemapp.helper.ViewPagerAdapterTopProductCat;
import com.dasta.eemapp.helper.WebServies;
import com.dasta.eemapp.helper.setwget;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Shop extends Fragment {

    MaterialBetterSpinner spPlaces;
    NonScrollListView lstClientShop;
    ArrayList<setwget> CustomListViewValuesArr = new ArrayList<setwget>();
    Adapter_CustomList_All_Shop adapter;
    Resources res;
    RequestQueue requestQueue,requestQueue2;
    private ProgressDialog pDialog;
    String title, city, url, urlCat;
    public static int flag = 0;
    public static int key = 0;
    ViewPager vpClientTopCategory;
    List<String> listCity = new ArrayList<>();
    public static Spinner spCategories;
    List<String> list = new ArrayList<>();
    public boolean firstTime;

    public int city_position=0;
    public int category_position=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_shop_home, null);


        Home_Shop.txtTitle.setVisibility(View.VISIBLE);
        Home_Shop.barTitle.setVisibility(View.GONE);
        Home_Shop.barTitle.setText("");

        flag = 1;
        title = getArguments().getString("client_shop_cat");
        city = getArguments().getString("client_shop_city");
        city_position=getArguments().getInt("client_shop_city_position",0);
        category_position=getArguments().getInt("client_shop_cat_position",0);

        firstTime=true;

        spPlaces = (MaterialBetterSpinner) v.findViewById(R.id.spClientPlaces);
        shopCity();

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("جارى التحميل ...");
        pDialog.show();

        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue2 = Volley.newRequestQueue(getActivity());


        //Home_Shop.imgClientShopUser.setVisibility(View.VISIBLE);

        res = getResources();

        if (title != null && !title.isEmpty()) {
            url = AppConfig.URL_CLIENT_ALL_SHOP;
            urlCat = AppConfig.URL_CLIENT_TOP_PRODUCT_CAT_SALE;
        } else {
            url = AppConfig.URL_CLIENT_ALL_SHOP_CITY;
            urlCat = AppConfig.URL_CLIENT_TOP_PRODUCT_CAT;
        }

        lstClientShop =  v.findViewById(R.id.lstClientShop);


        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
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
                                String address = shop.getString("address");
                                String phone = shop.getString("phone");
                                String title = shop.getString("title");
                                String imgurl = shop.getString("img");
                                String lat = shop.getString("lat");
                                String lng = shop.getString("lng");
                                String active = shop.getString("active");

                                // Toast.makeText(getActivity(), name, Toast.LENGTH_LONG).show();
                                setwget sw = new setwget();
                                sw.setId(id);
                                sw.setAddress(address);
                                sw.setPhone(phone);
                                sw.setTitle(title);
                                sw.setImgurl(imgurl);
                                sw.setLat(lat);
                                sw.setLng(lng);
                                sw.setDesc(active);

                                CustomListViewValuesArr.add(sw);
                            }
                            if (getActivity() != null) {
                                adapter = new Adapter_CustomList_All_Shop(getActivity(), CustomListViewValuesArr, res, Shop.this);
                                lstClientShop.setAdapter(adapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "لا يوجد اتصال بالانترنت..." + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        hidePDialog();
                        Toast.makeText(getActivity(),"لا يوجد اتصال بالانترنت...", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (title != null && !title.isEmpty()) {
                    params.put("cat", title);
                    params.put("city", city);
                } else {
                    //Adding parameters to request
                    params.put("city", city);
                }

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        requestQueue.add(stringRequest);

        final ArrayList<setwget> listAdv = new ArrayList<>();

        vpClientTopCategory = (ViewPager) v.findViewById(R.id.vpClientTopCategory);

        //Creating a string request
        //Toast.makeText(getActivity(),title,Toast.LENGTH_SHORT).show();

        StringRequest stringRequestTop = new StringRequest(Request.Method.POST, urlCat,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.v("Ads Response : ",response);

                        //If we are getting success from server
                        try {
                            JSONObject jObj = new JSONObject(response);
                            /***  ***/
                            JSONArray data = jObj.getJSONArray("result");
                            /*** Data Return from server ***/
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject shop = data.getJSONObject(i);

                                String sale_percentage = shop.getString("sale_percentage");
                                String img = shop.getString("img");


                                // Toast.makeText(getActivity(), name, Toast.LENGTH_LONG).show();
                                setwget sw = new setwget();
                                //sw.setTitle(sale_percentage);
                                sw.setImgurl(img);

                                listAdv.add(sw);
                            }
                            if (getActivity() != null) {
                                ViewPagerAdapterTopProductCat viewPagerAdapter = new ViewPagerAdapterTopProductCat(getActivity(), listAdv);

                                vpClientTopCategory.setAdapter(viewPagerAdapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"لا يوجد اتصال بالانترنت..."+ e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                if (title != null && !title.isEmpty()) {
                    params.put("cat", title);
                } else {
                    //Adding parameters to request

                }

                //returning parameter
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequestTop);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        spCategories = (Spinner) v.findViewById(R.id.spClientCategories);
        spCategories.setVisibility(View.VISIBLE);
        if (isAdded()){
            shopcategory();
        }


        return v;
    }

    public void shopCity() {

        CustomArrarAdapter customArrarAdapter=new CustomArrarAdapter(getActivity(),
                R.layout.custom_array_adapter_two_text_views,listCity);

        spPlaces.setAdapter(customArrarAdapter);
        spPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position==0||position==1){

                    Intent intent = new Intent(getActivity(), Home_Shop.class);
                    intent.putExtra("client_shop_cat",title);
                    intent.putExtra("shop_city", city);
                    intent.putExtra("shop_city_position", city_position);
                    intent.putExtra("client_shop_cat_position", category_position);
                    Home_Shop.keySearch=0;
                    getActivity().finish();
                    startActivity(intent);
                    //getActivity().finish();
                    spPlaces.setSelection(position);
                }else {
                    Snackbar.make(spPlaces, "قريبا..", Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }

            }
        });
        if (isNetworkAvailable()){
            try {
                //Creating a string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SHOP_ALL_CITY,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //If we are getting success from server
                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    /***  ***/
                                    JSONArray data = jObj.getJSONArray("result");
                                    /*** Data Return from server ***/
                                    listCity.clear();
                                    for (int i = 0; i < data.length(); i++) {

                                        JSONObject shop = data.getJSONObject(i);

                                        String id = shop.getString("id");

                                        listCity.add(shop.getString("name"));

                                    }



                                    CustomArrarAdapter customArrarAdapter=new CustomArrarAdapter(getActivity(),
                                            R.layout.custom_array_adapter_two_text_views,listCity);

                                    spPlaces.setAdapter(customArrarAdapter);
                                    spPlaces.setText(city);


                                    spPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            if (position==0||position==1){
                                                city = spPlaces.getText().toString();
                                                Intent intent = new Intent(getActivity(), Home_Shop.class);
                                                intent.putExtra("client_shop_cat",title);
                                                intent.putExtra("shop_city", city);
                                                intent.putExtra("shop_city_position", position);
                                                intent.putExtra("client_shop_cat_position", category_position);
                                                startActivity(intent);
                                                getActivity().finish();
                                               // spPlaces.setSelection(position);
                                            }else {
                                                Snackbar.make(spPlaces,"قريبا..",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                                            }

                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(),"لا يوجد اتصال بالانترنت...", Toast.LENGTH_LONG).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //You can handle error here if you want
                                // Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });

                //Adding the string request to the queue
                requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(stringRequest);

            }catch (NullPointerException e){
                e.printStackTrace();
                Toast.makeText(getContext(),"لا يوجد اتصال بالانترنت...",Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(getContext(),"لا يوجد اتصال بالانترنت...",Toast.LENGTH_SHORT).show();
        }



    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (vpClientTopCategory.getCurrentItem() == 0) {
                        vpClientTopCategory.setCurrentItem(1);
                    } else if (vpClientTopCategory.getCurrentItem() == 1) {
                        vpClientTopCategory.setCurrentItem(2);
                    } else if (vpClientTopCategory.getCurrentItem() == 2) {
                        vpClientTopCategory.setCurrentItem(3);
                    } else if (vpClientTopCategory.getCurrentItem() == 3) {
                        vpClientTopCategory.setCurrentItem(4);
                    } else if (vpClientTopCategory.getCurrentItem() == 4) {
                        vpClientTopCategory.setCurrentItem(5);
                    } else if (vpClientTopCategory.getCurrentItem() == 5) {
                        vpClientTopCategory.setCurrentItem(6);
                    } else if (vpClientTopCategory.getCurrentItem() == 6) {
                        vpClientTopCategory.setCurrentItem(7);
                    } else if (vpClientTopCategory.getCurrentItem() == 7) {
                        vpClientTopCategory.setCurrentItem(8);
                    } else if (vpClientTopCategory.getCurrentItem() == 8) {
                        vpClientTopCategory.setCurrentItem(9);
                    } else if (vpClientTopCategory.getCurrentItem() == 9) {
                        vpClientTopCategory.setCurrentItem(0);
                    }
                }
            });
        }
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void onItemClick(int position) {

        final setwget tempValues = CustomListViewValuesArr.get(position);

        String active = tempValues.getDesc();
        //Toast.makeText(getActivity(), active, Toast.LENGTH_LONG).show();
        /*if (active.equals("0")) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ShopClosed shopClosed = new ShopClosed();
            Bundle bundle = new Bundle();
            bundle.putString("shop_client_catt", title);
            shopClosed.setArguments(bundle);
            ft.replace(R.id.fClientShop, shopClosed);
            ft.commit();
        } else {*/
            WebServies webServies = new WebServies();
            webServies.addShopView(getActivity(), tempValues.getId());

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ShopProfile shopProfile = new ShopProfile();
            Bundle bundle = new Bundle();
            bundle.putString("shop_client_title", tempValues.getTitle());
            bundle.putString("shop_client_address", tempValues.getAddress());
            bundle.putString("shop_client_phone", tempValues.getPhone());
            bundle.putString("shop_client_img_url", tempValues.getImgurl());
            bundle.putString("shop_client_id", tempValues.getId());
            bundle.putString("shop_client_lat", tempValues.getLat());
            bundle.putString("shop_client_lng", tempValues.getLng());
            bundle.putString("shop_client_cat", title);
            bundle.putString("shop_client_city", city);

            shopProfile.setArguments(bundle);
            ft.replace(R.id.fClientShop, shopProfile);
            ft.commit();
       // }

    }

    public void shopcategory() {

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.v("Response : ",response);
                        //If we are getting success from server
                        try {
                            list.add(getResources().getString(R.string.category));
                            JSONObject jObj = new JSONObject(response);
                            /***  ***/
                            JSONArray data = jObj.getJSONArray("result");
                            /*** Data Return from server ***/
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject shop = data.getJSONObject(i);

                                String id = shop.getString("cat_id");
                                String cat_ar = shop.getString("cat_ar");
                                String cat_en = shop.getString("cat_en");
                                list.add(cat_ar);
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnerview_item, list) {
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
                            spCategories.setAdapter(arrayAdapter);
                            spCategories.setSelection(category_position);
                            spCategories.setPrompt(list.get(category_position));


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "حدث خطأ ما حاول لاحقا..." , Toast.LENGTH_LONG).show();
                        }catch (IllegalStateException e){

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(getActivity(), "حدث خطأ ما حاول لاحقا...", Toast.LENGTH_LONG).show();
                    }
                });

        //Adding the string request to the queue
        requestQueue2.add(stringRequest);

        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (firstTime)
                {
                    firstTime=false;
                    return;
                }

                String cat = spCategories.getItemAtPosition(position).toString();
                // txtClientShopTitle.setText(cat);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Shop shop = new Shop();
                Bundle bundle = new Bundle();
                bundle.putString("client_shop_cat", cat);
                bundle.putString("client_shop_city", city);
                bundle.putInt("client_shop_city_position", city_position);
                bundle.putInt("client_shop_cat_position", category_position);
                shop.setArguments(bundle);

                Intent intent = new Intent(getActivity(), Home_Shop.class);

                if (cat.equals("النشاط")){
                    cat="";
                }
                intent.putExtra("client_shop_cat",cat);
                //intent.putExtra("shop_city", city);
                //intent.putExtra("client_shop_cat",title);
                intent.putExtra("shop_city", city);
                intent.putExtra("shop_city_position", city_position);
                intent.putExtra("client_shop_cat_position", position);
                //getActivity().finish();
                startActivity(intent);

                //ft.replace(R.id.fClientShop, shop);
                //firstTime=10;
                //ft.commit();
                spCategories.setSelection(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
