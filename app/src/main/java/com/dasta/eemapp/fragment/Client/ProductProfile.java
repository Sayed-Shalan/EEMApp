package com.dasta.eemapp.fragment.Client;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Product_Profile;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.setwget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductProfile extends Fragment {

    public static String id, departmentName;
    GridView gridViewProductProfile;
    Adapter_CustomList_All_Product_Profile adapter;
    Resources res;
    ArrayList<setwget> mDataSet = new ArrayList<>();
    RequestQueue requestQueue;
    private ProgressDialog pDialog;
    public static int flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_client_shop_product, null);

        flag = 1;
        DepartmentProfile.flag = 0;

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("جاري التحميل...");
        pDialog.show();

        requestQueue = Volley.newRequestQueue(getActivity());

        res = getResources();

        id = getArguments().getString("shop_product_profile_id");

        departmentName = getArguments().getString("shop_product_department_name");

        gridViewProductProfile = (GridView) v.findViewById(R.id.gridViewProductProfile);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SHOP_PRODUCT,
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
                                String price = shop.getString("price");
                                String reservation = shop.getString("reservation");
                                String deliver = shop.getString("deliver");
                                String img = shop.getString("img");
                                String quantity = shop.getString("quantity");

                                // Toast.makeText(getActivity(), name, Toast.LENGTH_LONG).show();
                                setwget sw = new setwget();
                                sw.setTitle(name);
                                sw.setImgurl(img);
                                sw.setDesc(desc);
                                sw.setPrice(price);
                                sw.setId(id);
                                sw.setQuantity(quantity);
                                sw.setDeliver(deliver);
                                sw.setReservation(reservation);

                                mDataSet.add(sw);
                            }

                            adapter = new Adapter_CustomList_All_Product_Profile(getActivity(), mDataSet, res, ProductProfile.this);
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
                params.put("department", departmentName);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        requestQueue.add(stringRequest);

        return v;
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
        SingleProduct singleProduct = new SingleProduct();
        Bundle bundle = new Bundle();
        bundle.putString("single_product_price", tempValues.getPrice());
        bundle.putString("single_product_desc", tempValues.getDesc());
        bundle.putString("single_product_title", tempValues.getTitle());
        bundle.putString("single_product_img", tempValues.getImgurl());
        bundle.putString("single_product_id", tempValues.getId());
        bundle.putString("single_product_quantity", tempValues.getQuantity());
        bundle.putString("single_product_reservation", tempValues.getReservation());
        bundle.putString("single_product_deliver", tempValues.getDeliver());
        singleProduct.setArguments(bundle);
        ft.replace(R.id.fClientShop, singleProduct);
        ft.commit();

    }
}
