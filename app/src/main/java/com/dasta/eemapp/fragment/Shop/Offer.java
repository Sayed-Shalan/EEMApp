package com.dasta.eemapp.fragment.Shop;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.dasta.eemapp.activity.Shop.Home;
import com.dasta.eemapp.adapter.Shop.Adapter_CustomList_All_Offer;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.setwget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 28/06/2017.
 */

public class Offer extends Fragment {

    private ListView lstOffer;
    ArrayList<setwget> CustomListViewValuesArr = new ArrayList<setwget>();
    Adapter_CustomList_All_Offer adapter;
    Resources res;
    RequestQueue requestQueue;
    private ProgressDialog pDialog;
    public static int flag = 0;
    FloatingActionButton fabShopOfferDelAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shop_offer, null, false);
        flag = 1;
        setRetainInstance(true);
        lstOffer = (ListView) v.findViewById(R.id.lstShopOffer);

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage(getString(R.string.loading));
        pDialog.show();
        requestQueue = Volley.newRequestQueue(getActivity());

        res = getResources();

        if (isNetworkAvailable()) {
            //Creating a string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ALL_Offer,
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
                                    String sell = shop.getString("product_sell");
                                    String buy = shop.getString("product_buy");
                                    String sale = shop.getString("product_sale");
                                    String imgurl = shop.getString("product_image");
                                    String per = shop.getString("product_per");


                                    //
                                    setwget sw = new setwget();

                                    sw.setId(id);
                                    sw.setTitle(title);
                                    sw.setDept(sale);
                                    sw.setSell(sell);
                                    sw.setBuy(buy);
                                    sw.setImgurl(imgurl);
                                    if (per.equals("0")) {
                                        sw.setDesc("");
                                    } else {
                                        sw.setDesc(getString(R.string.salePercentage) + per + "%");
                                    }

                                    CustomListViewValuesArr.add(sw);
                                }

                                adapter = new Adapter_CustomList_All_Offer(getActivity(), CustomListViewValuesArr, res, Offer.this);
                                lstOffer.setAdapter(adapter);

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
                    params.put("id", Home.shop_id);

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

        fabShopOfferDelAll = (FloatingActionButton) v.findViewById(R.id.fabShopOfferDelAll);
        fabShopOfferDelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                        getActivity());
                builderSingle.setTitle(getString(R.string.delete));
                builderSingle.setIcon(R.drawable.logo_home);
                builderSingle.setMessage(getString(R.string.deleteOfferAll));
                builderSingle.setNegativeButton(getString(R.string.questionNo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderSingle.setPositiveButton(getString(R.string.questionYes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delShopProductOfferAll(getActivity(), Home.shop_id);
                    }
                });

                builderSingle.show();
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
        final setwget tempValues = CustomListViewValuesArr.get(position);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                getActivity());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

        arrayAdapter.add(getString(R.string.edit));
        arrayAdapter.add(getString(R.string.delete));

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
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            AddOffer department = new AddOffer();
                            Bundle bundle = new Bundle();
                            bundle.putString("offer_sell_price", tempValues.getSell());
                            bundle.putString("offer_sale_price", tempValues.getBuy());
                            bundle.putString("offer_id", tempValues.getId());
                            department.setArguments(bundle);
                            ft.replace(R.id.fShopHomeLayout, department);
                            ft.commit();
                        } else if (which == 1) {
                            //Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_LONG).show();
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                    getActivity());
                            builderSingle.setTitle(getString(R.string.delete));
                            builderSingle.setIcon(R.drawable.logo_home);
                            builderSingle.setMessage(getString(R.string.deleteOffer));
                            builderSingle.setNegativeButton(getString(R.string.questionNo), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderSingle.setPositiveButton(getString(R.string.questionYes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    delShopProductOffer(getActivity(), tempValues.getId());
                                }
                            });

                            builderSingle.show();
                        }
                    }
                });
        builderSingle.show();


    }

    // Del Shop Product
    public void delShopProductOffer(final FragmentActivity activity, final String id) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DEL_SHOP_PRODUCT_OFFER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
                        ft.replace(R.id.fShopHomeLayout, home);
                        ft.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Del Shop Product
    public void delShopProductOfferAll(final FragmentActivity activity, final String id) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DEL_SHOP_PRODUCT_OFFER_ALL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        com.dasta.eemapp.fragment.Shop.Home home = new com.dasta.eemapp.fragment.Shop.Home();
                        ft.replace(R.id.fShopHomeLayout, home);
                        ft.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("shopId", id);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
