package com.dasta.eemapp.fragment.Shop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.dasta.eemapp.activity.Shop.*;
import com.dasta.eemapp.adapter.Shop.Adapter_CustomList_All_Department;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.WebServies;
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

public class Department extends Fragment {


    private ListView lstShopDepartment;
    String id;
    ArrayList<setwget> CustomListViewValuesArr = new ArrayList<setwget>();
    Adapter_CustomList_All_Department adapter;
    Resources res;
    RequestQueue requestQueue;
    private ProgressDialog pDialog;
    public static String cat;
    private FloatingActionButton fabDepartment;
    WebServies webServies = new WebServies();
    public static int flag = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_shop_department, null, false);

        Appointment.flag = 0;
        Category.flag = 0;
        LiveInfo.flag = 0;
        Home.flag = 0;
        Offer.flag = 0;

        flag = 1;

        com.dasta.eemapp.activity.Shop.Home.txtShopHomeTitle.setText(R.string.department);
        cat = getArguments().getString("shop_cat_specific");

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage(getString(R.string.loading));
        pDialog.show();

        id = com.dasta.eemapp.activity.Shop.Home.shop_id;

        lstShopDepartment = (ListView) v.findViewById(R.id.lstShopDepartment);

        requestQueue = Volley.newRequestQueue(getActivity());

        res = getResources();

        if (isNetworkAvailable()) {
            //Creating a string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ALL_DEPARTMENT,
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
                                    String name = shop.getString("department_name");

                                    // Toast.makeText(getActivity(), name, Toast.LENGTH_LONG).show();
                                    setwget sw = new setwget();
                                    sw.setDepartment_name(name);
                                    sw.setId(id);

                                    CustomListViewValuesArr.add(sw);
                                }

                                adapter = new Adapter_CustomList_All_Department(getActivity(), CustomListViewValuesArr, res, Department.this);
                                lstShopDepartment.setAdapter(adapter);

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
                    params.put("cat", cat);

                    //returning parameter
                    return params;
                }
            };

            //Adding the string request to the queue
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(getActivity(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
        }

        fabDepartment = (FloatingActionButton) v.findViewById(R.id.fabShopDepartment);
        fabDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Department.flag = 0;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                AddDepartment department = new AddDepartment();
                Bundle bundle = new Bundle();
                bundle.putString("shop_catt_specific", cat);
                department.setArguments(bundle);
                ft.replace(R.id.fShopHomeLayout, department);
                ft.commit();
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
        setwget tempValues = CustomListViewValuesArr.get(position);
        flag = 0;
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Product department = new Product();
        Bundle bundle = new Bundle();
        bundle.putString("shop_dept_specific", tempValues.getDepartment_name());
        bundle.putString("shop_cat_specific", cat);
        department.setArguments(bundle);
        ft.replace(R.id.fShopHomeLayout, department);
        ft.commit();
    }

    public void OnItemDel(int position) {
        setwget tempValues = CustomListViewValuesArr.get(position);
        final String name = tempValues.getDepartment_name();

        //Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_LONG).show();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                getActivity());
        builderSingle.setTitle(getString(R.string.delete));
        builderSingle.setIcon(R.drawable.logo_home);
        builderSingle.setMessage(getString(R.string.departmentMessage));
        builderSingle.setNegativeButton(getString(R.string.questionNo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setPositiveButton(getString(R.string.questionYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                webServies.delShopDepartment(getActivity(), com.dasta.eemapp.activity.Shop.Home.shop_id, name, cat);
            }
        });

        builderSingle.show();
    }

    public void OnItemEdit(int position) {
        setwget tempValues = CustomListViewValuesArr.get(position);
        flag = 0;
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        EditDepartment department = new EditDepartment();
        Bundle bundle = new Bundle();
        bundle.putString("shop_department_id_edit", tempValues.getId());
        bundle.putString("shop_department_be_edit", tempValues.getDepartment_name());
        bundle.putString("shop_department_cat_edit", cat);
        department.setArguments(bundle);
        ft.replace(R.id.fShopHomeLayout, department);
        ft.commit();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
