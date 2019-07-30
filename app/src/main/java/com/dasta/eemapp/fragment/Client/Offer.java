package com.dasta.eemapp.fragment.Client;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Offer;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.ViewPagerAdapter;
import com.dasta.eemapp.helper.setwget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Offer extends Fragment {

    public static int flag = 0;
    GridView gridViewProductProfile;
    Adapter_CustomList_All_Offer adapter;
    Resources res;
    ArrayList<setwget> mDataSet = new ArrayList<>();
    RequestQueue requestQueue;
    private ProgressDialog pDialog;
    ViewPager vpClientAdv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_client_offer, null);

        flag = 1;
        Home_Category.key = 0;

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("جاري التحميل..");
        pDialog.show();

        requestQueue = Volley.newRequestQueue(getActivity());
        res = getResources();
        gridViewProductProfile = (GridView) v.findViewById(R.id.gridViewOffer);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_ALL_OFFER,
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
                                String shop_id=shop.getString("shop_id");
                                String name = shop.getString("name");
                                String desc = shop.getString("desc");
                                String per = shop.getString("per");
                                String price = shop.getString("price");
                                String img = shop.getString("img");
                                String quantity = shop.getString("quantity");
                                String deliver = shop.getString("deliver");
                                String reservation = shop.getString("reservation");

                                // Toast.makeText(getActivity(), name, Toast.LENGTH_LONG).show();
                                setwget sw = new setwget();
                                sw.setTitle(name);
                                sw.setShop_id(shop_id);
                                sw.setImgurl(img);
                                sw.setDesc(desc);
                                sw.setPercentage(per);
                                sw.setPrice(price);
                                sw.setId(id);
                                sw.setQuantity(quantity);
                                sw.setDeliver(deliver);
                                sw.setReservation(reservation);

                                mDataSet.add(sw);
                            }

                            adapter = new Adapter_CustomList_All_Offer(getActivity(), mDataSet, res, Offer.this);
                            gridViewProductProfile.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        hidePDialog();
                        //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        //Adding the string request to the queue
        requestQueue.add(stringRequest);

        ArrayList<Integer> listAdv = new ArrayList<>();
        listAdv.add(R.drawable.adv1);
        listAdv.add(R.drawable.adv2);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), listAdv);
        vpClientAdv =  v.findViewById(R.id.offer_vpClientAdv);
        vpClientAdv.setAdapter(viewPagerAdapter);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Offer.MyTimerTask(), 2000, 4000);

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

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void onItemClick(int position) {
        setwget tempValues = mDataSet.get(position);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        AllSingleOffer singleProduct = new AllSingleOffer();
        Bundle bundle = new Bundle();
        Log.e("SHOP ID",tempValues.getShop_id());
        bundle.putString("shop_id",tempValues.getShop_id());
        bundle.putString("all_single_offer_price", tempValues.getPrice());
        bundle.putString("all_single_offer_desc", tempValues.getDesc());
        bundle.putString("all_single_offer_title", tempValues.getTitle());
        bundle.putString("all_single_offer_img", tempValues.getImgurl());
        bundle.putString("all_single_offer_id", tempValues.getId());
        bundle.putString("all_single_offer_per", tempValues.getPercentage());
        bundle.putString("all_single_offer_quantity", tempValues.getQuantity());
        bundle.putString("all_single_offer_deliver", tempValues.getDeliver());
        bundle.putString("all_single_offer_reservation", tempValues.getReservation());


        Bundle bundle1 =getArguments();
        if (bundle1!=null){

            bundle.putString("from",getArguments().getString("from","Default"));
            singleProduct.setArguments(bundle);

            if (getArguments().getString("from","Default").equals("home")){
                Log.e("TRANSACTION ","First");

                ft.replace(R.id.fClientHomeLayout, singleProduct);
            }else {
                Log.e("TRANSACTION ","Second");

                ft.replace(R.id.fClientShop, singleProduct);
            }
        }else {
            Log.e("TRANSACTION ","Third");
            bundle.putString("from",getArguments().getString("from","Default"));
            singleProduct.setArguments(bundle);
            ft.replace(R.id.fClientHomeLayout, singleProduct);
        }

        ft.commit();

    }
}
