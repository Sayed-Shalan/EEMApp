package com.dasta.eemapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dasta.eemapp.helper.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BottomSheetRecyclerFragment extends BottomSheetDialogFragment
{

    //Init Views
    Bundle bundle;
    RecyclerView recyclerView;
    TextView noImgsTxt;
    ArrayList<PanoramaModel> panoramaList;
    PanoramaImageAdapter panoramaImageAdapter;
    ProgressBar progressBar;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.bottom_sheet_360_fragment, container,
                false);

        //Init Views
        initViews();

        //get Images
        getImages();

        //on click
        onRecyclerItemTouch();

        // get the views and attach the listener
        return view;
    }

    private void onRecyclerItemTouch() {
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListner(getActivity(), recyclerView, new RecyclerViewTouchListner.recyclerViewTouchListner() {
            @Override
            public void onclick(View child, int position) {

                Snackbar.make(child,"برجاء الإنتظار حتي يتم تحميل الصورة",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                PanoramaActivity.RetrieveFeedTask retrieveFeedTask=new PanoramaActivity.RetrieveFeedTask();
                retrieveFeedTask.execute("http://dasta.net/data/eem/Upload/Shop/Image/".
                        concat(((PanoramaImageAdapter)recyclerView.getAdapter()).getItem(position).getUrl()).concat(".png"));
                dismiss();
            }

            @Override
            public void onLongClick(View child, int position) {

            }
        }));
    }

    private void getImages() {

        noImgsTxt.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        StringRequest getImages=new StringRequest(Request.Method.POST,
                "http://dasta.net/data/eem/Shop/get_shop_photos.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("get Images",response);

                try {
                    JSONObject object=new JSONObject(response);
                    if (object.getString("response").equals("exist")){


                        noImgsTxt.setVisibility(View.GONE);
                        JSONArray array=object.getJSONArray("data");
                        for (int i=0;i<array.length();i++){
                            PanoramaModel model0=new PanoramaModel();
                            JSONObject item=array.getJSONObject(i);
//                                ImageModel imageModel  =new ImageModel();
//                                imageModel.setId(item.getInt("id"));
//                                imageModel.setShop_id(item.getInt("shop_id"));
                            model0.setUrl(item.getString("name"));
                            model0.setDepartmentName(item.getString("department"));
                            panoramaList.add(model0);
                        }

//                        if (array.length()>0){
//                            RetrieveFeedTask retrieveFeedTask=new RetrieveFeedTask();
//                            retrieveFeedTask.execute("http://dasta.net/data/eem/Upload/Shop/Image/".
//                                    concat(panoramaList.get(0).getUrl()).concat(".png"));
//                        }

                        noImgsTxt.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);

                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1,
                                GridLayoutManager.HORIZONTAL,true));
                        panoramaImageAdapter=new PanoramaImageAdapter(getActivity(),panoramaList);
                        recyclerView.setAdapter(panoramaImageAdapter);
                        panoramaImageAdapter.notifyDataSetChanged();

                    }else {
                        noImgsTxt.setText("لا يوجد صور حاليا");
                        noImgsTxt.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                noImgsTxt.setText("حدث خطأ , تأكد من الإتصال بالإنترنت..");
                noImgsTxt.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();
                params.put("id",bundle.getString("id"));
                return params;
            }
        };
        getImages.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 150000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 150000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if(error instanceof TimeoutError){
                }

            }
        });

        AppController.getInstance().addToRequestQueue(getImages);
    }

    //Init Views
    private void initViews() {
        recyclerView=view.findViewById(R.id.bottomsheet_recycler);
        noImgsTxt=view.findViewById(R.id.no_photosTxt);
        progressBar=view.findViewById(R.id.progress_bar);
        noImgsTxt.setVisibility(View.INVISIBLE);
        panoramaList=new ArrayList<>();
        bundle=getArguments();
    }
}
