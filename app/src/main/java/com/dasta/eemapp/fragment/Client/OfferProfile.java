package com.dasta.eemapp.fragment.Client;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Offer_Profile;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Product_Profile;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.ViewPagerAdapter;
import com.dasta.eemapp.helper.setwget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mohamed on 20/09/2017.
 */

public class OfferProfile extends Fragment {

    public static String id;
    GridView gridViewProductProfile;
    Adapter_CustomList_All_Offer_Profile adapter;
    Resources res;
    ArrayList<setwget> mDataSet = new ArrayList<>();
    RequestQueue requestQueue;
    private ProgressDialog pDialog;
    public static int flag = 0;
    ViewPager vpClientAdv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_client_shop_offer, null);

        flag = 1;
        ShopProfile.flag = 0;

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("جاري التحميل ..");
        pDialog.show();

        requestQueue = Volley.newRequestQueue(getActivity());

        res = getResources();

        id = getArguments().getString("shop_offer_profile_id");

        gridViewProductProfile = (GridView) v.findViewById(R.id.gridViewOfferProfile);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SHOP_OFFER,
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
                                String name = shop.getString("name");
                                String desc = shop.getString("desc");
                                String per = shop.getString("per");
                                String price = shop.getString("price");
                                String img = shop.getString("img");
                                String quantity = shop.getString("quantity");

                                // Toast.makeText(getActivity(), name, Toast.LENGTH_LONG).show();
                                setwget sw = new setwget();
                                sw.setTitle(name);
                                sw.setImgurl(img);
                                sw.setDesc(desc);
                                sw.setPercentage(per);
                                sw.setPrice(price);
                                sw.setId(id);
                                sw.setQuantity(quantity);

                                mDataSet.add(sw);
                            }

                            adapter = new Adapter_CustomList_All_Offer_Profile(getActivity(), mDataSet, res, OfferProfile.this);
                            gridViewProductProfile.setAdapter(adapter);

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
                        hidePDialog();
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("id", id);
                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        requestQueue.add(stringRequest);


        ArrayList<Integer> listAdv = new ArrayList<>();
        listAdv.add(R.drawable.adv1);
        listAdv.add(R.drawable.adv2);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), listAdv);
        vpClientAdv =  v.findViewById(R.id.offer_vpClientAdv);
        vpClientAdv.setAdapter(viewPagerAdapter);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new OfferProfile.MyTimerTask(), 2000, 4000);

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
        SingleOffer.flag = 1;
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        SingleOffer singleProduct = new SingleOffer();
        Bundle bundle = new Bundle();
        bundle.putString("single_offer_price", tempValues.getPrice());
        bundle.putString("single_offer_desc", tempValues.getDesc());
        bundle.putString("single_offer_title", tempValues.getTitle());
        bundle.putString("single_offer_img", tempValues.getImgurl());
        bundle.putString("single_offer_id", tempValues.getId());
        bundle.putString("single_offer_per", tempValues.getPercentage());
        bundle.putString("single_offer_quantity", tempValues.getQuantity());
        singleProduct.setArguments(bundle);
        ft.replace(R.id.fClientShop, singleProduct);
        ft.commit();
    }
}
