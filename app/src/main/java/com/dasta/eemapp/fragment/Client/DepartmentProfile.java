package com.dasta.eemapp.fragment.Client;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Department_Profile;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.setwget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DepartmentProfile extends Fragment {

    public static String id;
    ListView lstShopDepartmentProfile;
    ArrayList<setwget> CustomListViewValuesArr = new ArrayList<setwget>();
    Adapter_CustomList_All_Department_Profile adapter;
    Resources res;
    RequestQueue requestQueue;
    private ProgressDialog pDialog;
    public static int flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_shop_department, null);

        flag = 1;
        ShopProfile.flag = 0;

        id = getArguments().getString("shop_department_profile_id");

        lstShopDepartmentProfile = (ListView) v.findViewById(R.id .lstShopDepartmentProfile);

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("جاري التحميل...");
        pDialog.show();

        requestQueue = Volley.newRequestQueue(getActivity());

        res = getResources();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_SHOP_DEPARTMENT,
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
                                String cat = shop.getString("cat");

                                setwget sw = new setwget();
                                sw.setDepartment_name(name);
                                sw.setCat(cat);
                                sw.setId(id);

                                CustomListViewValuesArr.add(sw);
                            }

                            adapter = new Adapter_CustomList_All_Department_Profile(getActivity(), CustomListViewValuesArr, res, DepartmentProfile.this);
                            lstShopDepartmentProfile.setAdapter(adapter);

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

        return v;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void onItemClick(int position) {
        setwget tempValues = CustomListViewValuesArr.get(position);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ProductProfile productProfile = new ProductProfile();
        Bundle bundle = new Bundle();
        bundle.putString("shop_product_department_name", tempValues.getDepartment_name());
        bundle.putString("shop_product_profile_id", id);
        productProfile.setArguments(bundle);
        ft.replace(R.id.fClientShop, productProfile);
        ft.commit();
    }
}
