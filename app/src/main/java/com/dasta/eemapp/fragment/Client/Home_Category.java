package com.dasta.eemapp.fragment.Client;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Client.Home;
import com.dasta.eemapp.activity.Client.Home_Shop;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Shop;
import com.dasta.eemapp.adapter.Client.CustomArrarAdapter;
import com.dasta.eemapp.adapter.Client.SnapAdapter;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Client.App;
import com.dasta.eemapp.helper.Client.Snap;
import com.dasta.eemapp.helper.ViewPagerAdapter;
import com.dasta.eemapp.helper.ViewPagerAdapterTop;
import com.dasta.eemapp.helper.setwget;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Home_Category extends Fragment {

    MaterialBetterSpinner spPlaces;
    public static int key = 0;
    public static int flag = 0, flag1 = 0, flag2 = 0;
    //private RecyclerView mRecyclerView;
    RequestQueue requestQueue;
    List<String> listCity = new ArrayList<>();
    // List<String> listMall = new ArrayList<>();
    String city;
    CoordinatorLayout coordinatorCategory;
    ViewPager /**vpClientOffer,*/ vpClientAdv, vpClientTop;

    ArrayList<setwget> customData = new ArrayList<>();
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_client_home_category, null);

        key = 1;

        flag2=1;

        coordinatorCategory = v.findViewById(R.id.coordinatorCategory);

        requestQueue = Volley.newRequestQueue(getActivity());

        spPlaces =v.findViewById(R.id.spClientPlaces);
        shopCity();

        /*spMall = (Spinner) v.findViewById(R.id.spMall);
        shopMall();*/

        /*
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewClient);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        setupAdapter();
        */

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Log.e("FCM TOKEN ",instanceIdResult.getToken());
            }
        });

        ArrayList<Integer> listAdv = new ArrayList<>();
        listAdv.add(R.drawable.adv1);
        listAdv.add(R.drawable.adv2);

        ArrayList<Integer> listOffer = new ArrayList<>();
        listOffer.add(R.drawable.offer1);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_TOP_VIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        try {
                            JSONObject jObj = new JSONObject(response);
                            /* **  ***/
                            JSONArray data = jObj.getJSONArray("result");
                            /* ** Data Return from server ***/
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject shop = data.getJSONObject(i);

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

                                customData.add(sw);
                            }
                            if (getActivity() != null) {
                                ViewPagerAdapterTop viewPagerAdapterTop = new ViewPagerAdapterTop(getActivity(), customData,
                                        Home_Category.this);
                                vpClientTop.setAdapter(viewPagerAdapterTop);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), listAdv);

        //ViewPagerAdapter viewPagerAdapter1 = new ViewPagerAdapter(getActivity(), listOffer);
        //vpClientOffer = (ViewPager) v.findViewById(R.id.vpClientOffer);

        vpClientAdv =  v.findViewById(R.id.vpClientAdv);

        vpClientTop =  v.findViewById(R.id.vpClientTop);

        vpClientAdv.setAdapter(viewPagerAdapter);

        //vpClientOffer.setAdapter(viewPagerAdapter1);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        //Timer timer1 = new Timer();
        //timer1.scheduleAtFixedRate(new MyTimerTask1(), 2000, 5000);

        Timer timerTop = new Timer();
        timerTop.scheduleAtFixedRate(new MyTimerTaskTop(), 2000, 3000);

        bundle=getArguments();
        if (bundle!=null){
            Toast.makeText(getActivity(),"Bundle Not Null",Toast.LENGTH_SHORT).show();
            if (bundle.getBoolean("from_share",false)){
                Toast.makeText(getActivity(),"FROM SHARE",Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(),"ID ",Toast.LENGTH_SHORT).show();

                for (int i=0;i<customData.size();i++){
                    if (Integer.parseInt(customData.get(i).getId())==bundle.getInt("id")){
                        Toast.makeText(getActivity(),"Find Shop",Toast.LENGTH_SHORT).show();

                        final setwget tempValues = customData.get(i);
                        Home_Shop.keySearch = 2;
                        Intent intent = new Intent(getActivity(), Home_Shop.class);
                        intent.putExtra("view_shop_id", tempValues.getId());
                        intent.putExtra("view_shop_address", tempValues.getAddress());
                        intent.putExtra("view_shop_phone", tempValues.getPhone());
                        intent.putExtra("view_shop_name", tempValues.getTitle());
                        intent.putExtra("view_shop_img", tempValues.getImgurl());

                        //Toast.makeText(getActivity(),tempValues.getLat()+" + "+tempValues.getLng(),Toast.LENGTH_SHORT).show();

                        intent.putExtra("shop_profile_map_lat", tempValues.getLat());
                        intent.putExtra("shop_profile_map_lng", tempValues.getLng());

                        startActivity(intent);
                        break;
                    }
                }
            }
        }

        return v;
    }
    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (vpClientAdv.getCurrentItem()==vpClientAdv.getChildCount()-1){
                        vpClientAdv.setCurrentItem(0);
                    }else {
                        vpClientAdv.setCurrentItem(vpClientAdv.getCurrentItem()+1);
                    }
                   /* if (vpClientAdv.getCurrentItem() == 0) {
                    } else if (vpClientAdv.getCurrentItem() == 1) {
                        vpClientAdv.setCurrentItem(2);
                    } else if (vpClientAdv.getCurrentItem() == 2) {
                        vpClientAdv.setCurrentItem(3);
                    } else if (vpClientAdv.getCurrentItem() == 3) {
                        vpClientAdv.setCurrentItem(4);
                    } else if (vpClientAdv.getCurrentItem() == 4) {
                        vpClientAdv.setCurrentItem(5);
                    } else if (vpClientAdv.getCurrentItem() == 5) {
                        vpClientAdv.setCurrentItem(6);
                    } else if (vpClientAdv.getCurrentItem() == 6) {
                        vpClientAdv.setCurrentItem(7);
                    } else if (vpClientAdv.getCurrentItem() == 7) {
                        vpClientAdv.setCurrentItem(8);
                    } else if (vpClientAdv.getCurrentItem() == 8) {
                        vpClientAdv.setCurrentItem(9);
                    } else if (vpClientAdv.getCurrentItem() == 9) {
                        vpClientAdv.setCurrentItem(0);
                    }*/
                }
            });
        }
    }

    /*public class MyTimerTask1 extends TimerTask {

        @Override
        public void run() {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (vpClientOffer.getCurrentItem() == 0) {
                        vpClientOffer.setCurrentItem(1);
                    } else if (vpClientOffer.getCurrentItem() == 1) {
                        vpClientOffer.setCurrentItem(2);
                    } else if (vpClientOffer.getCurrentItem() == 2) {
                        vpClientOffer.setCurrentItem(3);
                    } else if (vpClientOffer.getCurrentItem() == 3) {
                        vpClientOffer.setCurrentItem(4);
                    } else if (vpClientOffer.getCurrentItem() == 4) {
                        vpClientOffer.setCurrentItem(5);
                    } else if (vpClientOffer.getCurrentItem() == 5) {
                        vpClientOffer.setCurrentItem(6);
                    } else if (vpClientOffer.getCurrentItem() == 6) {
                        vpClientOffer.setCurrentItem(7);
                    } else if (vpClientOffer.getCurrentItem() == 8) {
                        vpClientOffer.setCurrentItem(9);
                    } else if (vpClientOffer.getCurrentItem() == 9) {
                        vpClientOffer.setCurrentItem(0);
                    }
                }
            });
        }
    }*/

    public class MyTimerTaskTop extends TimerTask {

        @Override
        public void run() {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (vpClientTop.getCurrentItem() == 0) {
                        vpClientTop.setCurrentItem(1);
                    } else if (vpClientTop.getCurrentItem() == 1) {
                        vpClientTop.setCurrentItem(2);
                    } else if (vpClientTop.getCurrentItem() == 2) {
                        vpClientTop.setCurrentItem(3);
                    } else if (vpClientTop.getCurrentItem() == 3) {
                        vpClientTop.setCurrentItem(4);
                    } else if (vpClientTop.getCurrentItem() == 4) {
                        vpClientTop.setCurrentItem(5);
                    } else if (vpClientTop.getCurrentItem() == 5) {
                        vpClientTop.setCurrentItem(6);
                    } else if (vpClientTop.getCurrentItem() == 6) {
                        vpClientTop.setCurrentItem(7);
                    } else if (vpClientTop.getCurrentItem() == 7) {
                        vpClientTop.setCurrentItem(8);
                    } else if (vpClientTop.getCurrentItem() == 8) {
                        vpClientTop.setCurrentItem(9);
                    } else if (vpClientTop.getCurrentItem() == 9) {
                        vpClientTop.setCurrentItem(0);
                    }
                }
            });
        }
    }

    /*
        private void setupAdapter() {
            List<App> apps2 = getClothes();
            List<App> apps3 = getShoes();
            List<App> apps4 = getElectronics();
            List<App> apps5 = getAccessories();
            List<App> apps6 = getHome();
            List<App> apps7 = getCars();
            List<App> apps8 = getDifferent();
            List<App> apps9 = getServices();

            SnapAdapter snapAdapter = new SnapAdapter();

            snapAdapter.addSnap(new Snap(Gravity.START, "Clothes", apps2));
            snapAdapter.addSnap(new Snap(Gravity.START, "Shoes", apps3));
            snapAdapter.addSnap(new Snap(Gravity.START, "Electronics", apps4));
            snapAdapter.addSnap(new Snap(Gravity.START, "Accessories", apps5));
            snapAdapter.addSnap(new Snap(Gravity.START, "House", apps6));
            snapAdapter.addSnap(new Snap(Gravity.START, "Cars", apps7));
            snapAdapter.addSnap(new Snap(Gravity.START, "Different", apps8));
            snapAdapter.addSnap(new Snap(Gravity.START, "Services", apps9));


            mRecyclerView.setAdapter(snapAdapter);
        }

        private List<App> getClothes() {
            List<App> apps = new ArrayList<>();
            apps.add(new App("Women Clothes", R.drawable.ic_clothes_48dp));
            apps.add(new App("Men clothes", R.drawable.ic_clothes_48dp));
            apps.add(new App("Children Clothes", R.drawable.ic_clothes_48dp));
            apps.add(new App("Wedding Dress", R.drawable.ic_clothes_48dp));
            apps.add(new App("Scarve", R.drawable.ic_clothes_48dp));
            apps.add(new App("Laundries Clothes", R.drawable.ic_clothes_48dp));
            return apps;
        }

        private List<App> getShoes() {
            List<App> apps = new ArrayList<>();
            apps.add(new App("Women Shoes", R.drawable.ic_shoes_48dp));
            apps.add(new App("Men Shoes", R.drawable.ic_shoes_48dp));
            apps.add(new App("Children Shoes", R.drawable.ic_shoes_48dp));
            return apps;
        }

        private List<App> getElectronics() {
            List<App> apps = new ArrayList<>();
            apps.add(new App("Mobile", R.drawable.ic_electronic_48dp));
            apps.add(new App("Electrical Devices", R.drawable.ic_electronic_48dp));
            apps.add(new App("Computers", R.drawable.ic_electronic_48dp));
            apps.add(new App("Watches", R.drawable.ic_electronic_48dp));
            return apps;
        }

        private List<App> getAccessories() {
            List<App> apps = new ArrayList<>();
            apps.add(new App("Women Accessories", R.drawable.ic_accessories_48dp));
            apps.add(new App("Makeup", R.drawable.ic_accessories_48dp));
            apps.add(new App("Hair Dresser", R.drawable.ic_accessories_48dp));
            apps.add(new App("Jewellery", R.drawable.ic_accessories_48dp));
            apps.add(new App("Glasses", R.drawable.ic_accessories_48dp));
            apps.add(new App("Perfumes", R.drawable.ic_accessories_48dp));
            apps.add(new App("Library", R.drawable.ic_accessories_48dp));
            return apps;
        }

        private List<App> getHome() {
            List<App> apps = new ArrayList<>();
            apps.add(new App("House ware", R.drawable.ic_home_48dp));
            apps.add(new App("Toilets Tools", R.drawable.ic_home_48dp));
            apps.add(new App("Antiques", R.drawable.ic_home_48dp));
            apps.add(new App("Cloth Furniture", R.drawable.ic_home_48dp));
            apps.add(new App("Home Furniture", R.drawable.ic_home_48dp));
            apps.add(new App("Kitchens", R.drawable.ic_home_48dp));
            apps.add(new App("Scenery", R.drawable.ic_home_48dp));
            return apps;
        }

        private List<App> getCars() {
            List<App> apps = new ArrayList<>();
            apps.add(new App("Cars Gallery", R.drawable.ic_car_48dp));
            apps.add(new App("Car Accessories", R.drawable.ic_car_48dp));
            apps.add(new App("Wheels and Batteries", R.drawable.ic_car_48dp));
            apps.add(new App("Spare Personal Car Parts", R.drawable.ic_car_48dp));
            apps.add(new App("Spare Half Car Parts", R.drawable.ic_car_48dp));
            apps.add(new App("Spare Vehicles Transporting Parts", R.drawable.ic_car_48dp));
            apps.add(new App("Spare Import and Export Car Parts", R.drawable.ic_car_48dp));
            return apps;
        }

        private List<App> getDifferent() {
            List<App> apps = new ArrayList<>();
            apps.add(new App("Toys", R.drawable.ic_all_48dp));
            apps.add(new App("Gym", R.drawable.ic_all_48dp));
            apps.add(new App("Hyper Market", R.drawable.ic_all_48dp));
            apps.add(new App("Detergent", R.drawable.ic_all_48dp));
            apps.add(new App("Tourism Companies", R.drawable.ic_all_48dp));
            apps.add(new App("Coffee Shops", R.drawable.ic_all_48dp));
            apps.add(new App("Resturants", R.drawable.ic_all_48dp));
            return apps;
        }

        private List<App> getServices() {
            List<App> apps = new ArrayList<>();
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            apps.add(new App("Services", R.drawable.ic_service_48dp));
            return apps;
        }

        */

    public void shopCity() {

        CustomArrarAdapter customArrarAdapter=new CustomArrarAdapter(getActivity(),
                R.layout.custom_array_adapter_two_text_views,listCity);

        spPlaces.setAdapter(customArrarAdapter);
        spPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position==0||position==1){
                    city = spPlaces.getText().toString();
                    Intent intent = new Intent(getActivity(), Home_Shop.class);
                    intent.putExtra("shop_city", city);
                    intent.putExtra("shop_city_position", position+1);

                    startActivity(intent);
                    getActivity().finish();
                    spPlaces.setSelection(position);
                }else {
                    Snackbar.make(spPlaces,"قريبا..",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
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

                                    spPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            if (position==0||position==1){
                                                city = spPlaces.getText().toString();
                                                Intent intent = new Intent(getActivity(), Home_Shop.class);
                                                intent.putExtra("shop_city", city);
                                                intent.putExtra("shop_city_position", position);

                                                startActivity(intent);
                                                getActivity().finish();
                                                spPlaces.setSelection(0);
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

    public void shopMall() {

        //Creating a string request
     /*   StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SHOP_ALL_MALL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        try {
                            listMall.add("Select Mall");
                            JSONObject jObj = new JSONObject(response);
                            /***  ***/
           /*                 JSONArray data = jObj.getJSONArray("result");
                            /*** Data Return from server ***/
          /*                  for (int i = 0; i < data.length(); i++) {

                                JSONObject shop = data.getJSONObject(i);

                                String id = shop.getString("id");
                                String cat_ar = shop.getString("name");
                                listMall.add(cat_ar);
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnerview_item, listMall) {
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
                            spMall.setAdapter(arrayAdapter);
                            spMall.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (flag2 == 0) {
                                        flag2 = 1;
                                    } else {
                                        flag2 = 0;
                                        mall = spMall.getItemAtPosition(position).toString();
                                        //Toast.makeText(getApplicationContext(), spData, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //Adding the string request to the queue
        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }*/
    }

    public void onItemClickTopView(int position) {
        final setwget tempValues = customData.get(position);
        Home_Shop.keySearch = 2;
        Intent intent = new Intent(getActivity(), Home_Shop.class);
        intent.putExtra("view_shop_id", tempValues.getId());
        intent.putExtra("view_shop_address", tempValues.getAddress());
        intent.putExtra("view_shop_phone", tempValues.getPhone());
        intent.putExtra("view_shop_name", tempValues.getTitle());
        intent.putExtra("view_shop_img", tempValues.getImgurl());

        //Toast.makeText(getActivity(),tempValues.getLat()+" + "+tempValues.getLng(),Toast.LENGTH_SHORT).show();

        intent.putExtra("shop_profile_map_lat", tempValues.getLat());
        intent.putExtra("shop_profile_map_lng", tempValues.getLng());

        startActivity(intent);
        getActivity().finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}