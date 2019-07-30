package com.dasta.eemapp.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Shop.Home;
import com.dasta.eemapp.fragment.Shop.AddOffer;
import com.dasta.eemapp.fragment.Shop.AddOfferProduct;
import com.dasta.eemapp.fragment.Shop.Appointment;
import com.dasta.eemapp.fragment.Shop.Category;
import com.dasta.eemapp.fragment.Shop.Department;
import com.dasta.eemapp.fragment.Shop.LivePerview;
import com.dasta.eemapp.fragment.Shop.Offer;
import com.dasta.eemapp.fragment.Shop.Product;
import com.dasta.eemapp.helper.Shop.SQLiteHandler;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 14/07/2017.
 */

public class WebServies {

    RequestQueue requestQueue;

    // Shop
    // Add Shop Department
    public void addShopDepartment(final FragmentActivity activity, final String id, final String departmentname, final String cat) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ADD_DEPARTMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        Department.flag = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_cat_specific", cat);
                        android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        Department department = new Department();
                        department.setArguments(bundle);
                        ft.replace(R.id.fShopHomeLayout, department);
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
                params.put("departmentname", departmentname);
                params.put("cat", cat);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Add Shop Category
    public void addShopCategory(final FragmentActivity activity, final String id, final String name) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ADD_Category,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        Category.flag = 1;
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
                params.put("category", name);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Add New Appointment
    public void addShopAppointment(final FragmentActivity activity, final String date, final String note, final String shopid) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_SHOP_APPOINTMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        Appointment.flag = 1;
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
                params.put("id", shopid);
                params.put("date", date);
                params.put("note", note);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Add Shop Offer
    public void addShopOffer(final FragmentActivity activity, final String saleprice, final String saleper, final String id) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_Add_Offer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        AddOffer.flag = 0;
                        Offer.flag = 1;
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
                params.put("sale_price", saleprice);
                params.put("sale_per", saleper);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Add Shop Offer
    public void addShopOfferProduct(final FragmentActivity activity, final String saleprice, final String saleper, final String id) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_Add_Offer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        AddOfferProduct.flag = 0;
                        Product.flag = 1;
                        android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_dept_specific", AddOfferProduct.dept);
                        bundle.putString("shop_cat_specific", AddOfferProduct.cat);
                        Product product = new Product();
                        product.setArguments(bundle);
                        ft.replace(R.id.fShopHomeLayout, product);
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
                params.put("sale_price", saleprice);
                params.put("sale_per", saleper);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Update Shop Location
    public void updateShopLocation(final Activity activity, final String lat, final String lng, final String id) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_SHOP_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SQLiteHandler db = new SQLiteHandler(activity);
                        db.updateLocation(id, lat, lng);
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(activity, Home.class);
                        activity.startActivity(intent);
                        activity.finish();
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
                params.put("lat", lat);
                params.put("lng", lng);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Update Shop Password
    public void shopChangePassword(final Activity activity, final String id, final String newpass) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_CHANGE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(activity, Home.class);
                        activity.startActivity(intent);
                        activity.finish();
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
                params.put("shop_id", id);
                params.put("newPassword", newpass);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Update Shop Password
    public void shopLogout(final Activity activity, final String id) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DEL_SHOP_LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
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

    // Add Shop Open
    public void addShopOpen(final Activity activity, final String id, final String open) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ADD_OPEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        SQLiteHandler db = new SQLiteHandler(activity);
                        db.updateOpen(id, open);
                        Intent intent = new Intent(activity, Home.class);
                        activity.startActivity(intent);
                        activity.finish();
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
                params.put("open", open);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Edit Register Shop Owner
    public void editShopOwner(final Activity activity, final String name, final String mail, final String username,
                              final String shopid) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_EDIT_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(activity, Home.class);
                        activity.startActivity(intent);
                        activity.finish();
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
                params.put("ownername", name);
                params.put("ownermail", mail);
                params.put("username", username);
                params.put("shopid", shopid);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Del Shop Product
    public void delShopProduct(final FragmentActivity activity, final String id, final String cat, final String dept) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DEL_SHOP_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();

                        android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_dept_specific", dept);
                        bundle.putString("shop_cat_specific", cat);
                        Product product = new Product();
                        product.setArguments(bundle);
                        ft.replace(R.id.fShopHomeLayout, product);
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

    // Del Shop Department
    public void delShopDepartment(final FragmentActivity activity, final String id, final String name, final String cat) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DEL_SHOP_DEPARTMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();

                        android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_cat_specific", cat);
                        Department department = new Department();
                        department.setArguments(bundle);
                        ft.replace(R.id.fShopHomeLayout, department);
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
                params.put("shop_id", id);
                params.put("department_name", name);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Edit Shop Department
    public void editShopDepartment(final FragmentActivity activity, final String shop_id, final String dept_id, final String dept_be
            , final String dept_af, final String cat) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_EDIT_SHOP_DEPARTMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();

                        android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putString("shop_cat_specific", cat);
                        Department department = new Department();
                        department.setArguments(bundle);
                        ft.replace(R.id.fShopHomeLayout, department);
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
                params.put("shop_id", shop_id);
                params.put("shop_department", dept_be);
                params.put("shop_departmentt", dept_af);
                params.put("dept_id", dept_id);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    // Del Shop Category
    public void delShopCategory(final FragmentActivity activity, final String shop_id, final String cat) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_DEL_SHOP_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                        Category.flag = 1;
                        LivePerview.flag = 0;
                        Offer.flag = 0;
                        Appointment.flag = 0;
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
                params.put("shop_id", shop_id);
                params.put("cat", cat);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

    //Client Shop View
    // Add Shop Open
    public void addShopView(final Activity activity, final String id) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_ADD_VIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
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
                params.put("shop_id", id);
                return params;
            }
        };
        //*** add request data from link into volley connection
        requestQueue.add(request);
    }

}
