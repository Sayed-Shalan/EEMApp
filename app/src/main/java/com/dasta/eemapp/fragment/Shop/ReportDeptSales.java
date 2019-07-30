package com.dasta.eemapp.fragment.Shop;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dasta.eemapp.R;
import com.dasta.eemapp.activity.Shop.Home;
import com.dasta.eemapp.helper.AppConfig;
import com.dasta.eemapp.helper.MySingleton;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mohamed on 30/08/2017.
 */

public class ReportDeptSales extends Fragment {

    public static int flag = 0;
    private ProgressDialog pd;

    ArrayList<BarDataSet> yAxis;
    ArrayList<BarEntry> yValues;
    ArrayList<String> xAxis1;
    BarEntry values;
    BarChart chart;
    BarData data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report_dept_sales, null, false);
        flag = 1;

        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.loading));


        // Log.d("array",Arrays.toString(fullData));
        chart = (BarChart) v.findViewById(R.id.chart);
        load_data_from_server();


        return v;
    }

    public void load_data_from_server() {
        pd.show();
        String url = AppConfig.URL_SHOP_REPORT_DEPT_SALES;
        xAxis1 = new ArrayList<>();
        yAxis = null;
        yValues = new ArrayList<>();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("string", response);

                        try {


                            JSONObject jObj = new JSONObject(response);
                            /***  ***/
                            JSONArray data = jObj.getJSONArray("result");

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject jsonobject = data.getJSONObject(i);


                                String score = jsonobject.getString("total").trim();
                                String name = jsonobject.getString("product_dept").trim();

                                xAxis1.add(name);

                                values = new BarEntry(Float.valueOf(score), i);
                                yValues.add(values);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();


                        }


                        BarDataSet barDataSet1 = new BarDataSet(yValues, getString(R.string.departmentSaleTitle));
                        barDataSet1.setColor(Color.rgb(205, 32, 40));

                        yAxis = new ArrayList<>();
                        yAxis.add(barDataSet1);
                        String names[] = xAxis1.toArray(new String[xAxis1.size()]);
                        data = new BarData(names, yAxis);
                        chart.setData(data);
                        chart.setDescription("");
                        chart.animateXY(1500, 1500);
                        chart.invalidate();
                        pd.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {

                            Toast.makeText(getActivity(), R.string.emptydata, Toast.LENGTH_LONG).show();
                            pd.hide();
                        }
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", Home.shop_id);
                return params;
            }
        };

        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

}
