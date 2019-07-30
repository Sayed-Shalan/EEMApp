package com.dasta.eemapp.fragment.Client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.dasta.eemapp.activity.Shop.Order;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Client_Order;
import com.dasta.eemapp.adapter.Client.Adapter_CustomList_All_Department_Profile;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.Client.SQLiteHandler;
import com.dasta.eemapp.helper.setwget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientOrder extends Fragment {


    SQLiteHandler db;
    String userid;
    ListView lstClientOrder;
    ArrayList<setwget> CustomListViewValuesArr = new ArrayList<setwget>();
    Adapter_CustomList_All_Client_Order adapter;
    Resources res;
    RequestQueue requestQueue;
    private ProgressDialog pDialog;
    public static int flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client_order, null);

        flag = 1;

        lstClientOrder = (ListView) v.findViewById(R.id.lstClientOrder);

        db = new SQLiteHandler(getActivity());

        final HashMap<String, String> user = db.getUserDetails();
        userid = user.get("userid");

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        requestQueue = Volley.newRequestQueue(getActivity());

        res = getResources();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_ORDER,
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

                                setwget sw = new setwget();
                                sw.setOrderPName(shop.getString("pname"));
                                sw.setOrderSName(shop.getString("sname"));
                                sw.setOrderP(shop.getString("pimg"));
                                sw.setOrderPCode(shop.getString("code"));
                                sw.setOrderS(shop.getString("simg"));
                                sw.setOrderSAddress(shop.getString("saddress"));
                                sw.setPhone(shop.getString("sphone"));
                                sw.setOrderState(shop.getString("pstate"));
                                sw.setId(shop.getString("oid"));


                                CustomListViewValuesArr.add(sw);
                            }

                            adapter = new Adapter_CustomList_All_Client_Order(getActivity(), CustomListViewValuesArr, res, ClientOrder.this);
                            lstClientOrder.setAdapter(adapter);

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
                params.put("user_id", userid);

                //returning parameter
                return params;
            }
        };

        //*** add request data from link into volley connection
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //*** add request data from link into volley connection
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
        final setwget tempValues = CustomListViewValuesArr.get(position);

        final String oid = tempValues.getId();
        final String uid = userid;


        String state = tempValues.getOrderState();

        if (!(state.equals("Cancel"))) {

            final AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.select_dialog_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

            arrayAdapter.add("Follow Order");
            arrayAdapter.add("Cancel Order");

            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {

                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                // Follow Order
                                dialog.dismiss();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                FollowOrder followOrder = new FollowOrder();
                                Bundle bundle = new Bundle();
                                bundle.putString("follow_order_id", tempValues.getId());
                                bundle.putString("follow_order_state", tempValues.getOrderState());
                                bundle.putString("follow_order_code", tempValues.getOrderPCode());
                                followOrder.setArguments(bundle);
                                ft.replace(R.id.fClientProfile, followOrder);
                                ft.commit();
                            } else {
                                // Cancel Order
                                dialog.dismiss();
                                orderState(getActivity(), uid, oid);
                            }
                        }
                    });

            builderSingle.show();
        } else {
            Toast.makeText(getActivity(), "you Cancelled This Product", Toast.LENGTH_LONG).show();
        }

    }

    public void onPhoneClick(int position) {
        setwget tempValues = CustomListViewValuesArr.get(position);
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);

        arrayAdapter.add(tempValues.getPhone());

        builderSingle.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                arrayAdapter.getItem(which).toString()));
                        startActivity(i);
                    }
                });
        builderSingle.show();
    }

    // Order State
    public void orderState(final Activity activity, final String userid, final String oid) {
        // make volley request
        requestQueue = Volley.newRequestQueue(activity);

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_CLIENT_ORDER_STATE,
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
                params.put("userid", userid);
                params.put("orderid", oid);
                return params;
            }
        };
        //*** add request data from link into volley connection
        request.setRetryPolicy(new

                DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //*** add request data from link into volley connection
        requestQueue.add(request);

    }

}
