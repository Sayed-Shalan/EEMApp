package com.dasta.eemapp.fragment.Shop;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.WebServies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed on 16/08/2017.
 */

public class AddCategory extends Fragment {

    private Spinner spShopCategory;
    private Button btnShopAddCategory;
    String categoryname;
    RequestQueue requestQueue;
    List<String> list = new ArrayList<>();
    int flag = 0;
    public static int flag1 = 0;
    WebServies webServies = new WebServies();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_add_category, null, false);

        Appointment.flag = 0;
        Category.flag = 0;
        LiveInfo.flag = 0;
        Home.flag = 0;
        Offer.flag = 0;

        flag1 = 1;

        requestQueue = Volley.newRequestQueue(getActivity());

        spShopCategory = (Spinner) v.findViewById(R.id.spShopCategoryName);
        shopcategory();

        btnShopAddCategory = (Button) v.findViewById(R.id.btnShopAddCategory);
        btnShopAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    webServies.addShopCategory(getActivity(), com.dasta.eemapp.activity.Shop.Home.shop_id, categoryname);
                } else {
                    Toast.makeText(getActivity(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    public void shopcategory() {

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        try {
                            list.add("اختر النشاط");
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
                            spShopCategory.setAdapter(arrayAdapter);
                            spShopCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (flag == 0) {
                                        flag = 1;
                                    } else {
                                        flag = 0;
                                        categoryname = spShopCategory.getItemAtPosition(position).toString();
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

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
