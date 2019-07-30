package com.dasta.eemapp.fragment.Shop;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Shop.Home;
import com.dasta.eemapp.helper.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 30/08/2017.
 */

public class ReportProductSales extends Fragment {

    TableLayout table_layout;

    RequestQueue requestQueue;

    public static int flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shop_report_products, null, false);

        flag = 1;
        table_layout = (TableLayout) v.findViewById(R.id.tableLayout2);

        // make volley request
        requestQueue = Volley.newRequestQueue(getActivity());
        if (isNetworkAvailable()) {
            StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_REPORT_PRODUCT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jObj = new JSONObject(response);
                                /***  ***/
                                JSONArray data = jObj.getJSONArray("result");
                                /*** Data Return from server ***/
                                for (int i = 0; i < data.length(); i++) {

                                    TableRow row = new TableRow(getActivity());
                                    row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                            TableRow.LayoutParams.WRAP_CONTENT));

                                    JSONObject shop = data.getJSONObject(i);

                                    TextView tv = new TextView(getActivity());
                                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    tv.setGravity(Gravity.CENTER);
                                    tv.setTextSize(18);
                                    tv.setBackground(getResources().getDrawable(R.drawable.back));
                                    tv.setPadding(0, 5, 0, 5);
                                    tv.setText(shop.getString("product_name"));
                                    row.addView(tv);

                                    TextView tv1 = new TextView(getActivity());
                                    tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    tv1.setGravity(Gravity.CENTER);
                                    tv1.setTextSize(18);
                                    tv1.setBackground(getResources().getDrawable(R.drawable.back));
                                    tv1.setPadding(0, 5, 0, 5);
                                    tv1.setText(shop.getString("product_dept"));
                                    row.addView(tv1);

                                    TextView tv2 = new TextView(getActivity());
                                    tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    tv2.setGravity(Gravity.CENTER);
                                    tv2.setTextSize(18);
                                    tv2.setBackground(getResources().getDrawable(R.drawable.back));
                                    tv2.setPadding(0, 5, 0, 5);
                                    tv2.setText(shop.getString("product_sell"));
                                    row.addView(tv2);

                                    TextView tv3 = new TextView(getActivity());
                                    tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    tv3.setGravity(Gravity.CENTER);
                                    tv3.setTextSize(18);
                                    tv3.setBackground(getResources().getDrawable(R.drawable.back));
                                    tv3.setPadding(0, 5, 0, 5);
                                    tv3.setText(shop.getString("product_buy"));
                                    row.addView(tv3);

                                    TextView tv4 = new TextView(getActivity());
                                    tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    tv4.setGravity(Gravity.CENTER);
                                    tv4.setTextSize(18);
                                    tv4.setBackground(getResources().getDrawable(R.drawable.back));
                                    tv4.setPadding(0, 5, 0, 5);
                                    tv4.setText(shop.getString("product_quantity"));
                                    row.addView(tv4);

                                    table_layout.addView(row);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", Home.shop_id);
                    return params;
                }
            };
            //*** add request data from link into volley connection
            requestQueue.add(request);
        } else

        {
            Toast.makeText(getActivity(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
        }
        return v;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
