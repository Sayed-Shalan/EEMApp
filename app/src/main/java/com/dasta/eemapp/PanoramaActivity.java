package com.dasta.eemapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.flipboard.bottomsheet.BottomSheetLayout;

import com.panoramagl.PLICamera;
import com.panoramagl.PLImage;
import com.panoramagl.PLSphericalPanorama;
import com.panoramagl.structs.PLRange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PanoramaActivity extends AppCompatActivity {


    TextView noImgsTxt;
    Bundle bundle;
    RequestQueue requestQueue;
    private static PLManager plManager;
    static BottomSheetLayout parent;
    static ProgressDialog progressDialog=null;
    public static Activity activity;
    ImageView showBottomImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama);

        activity=PanoramaActivity.this;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        noImgsTxt=findViewById(R.id.no_photosTxt);
        bundle=getIntent().getExtras();

        showBottomImg=findViewById(R.id.moreImg);
        showBottomImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetRecyclerFragment bottomSheetRecyclerFragment=new BottomSheetRecyclerFragment();
                bottomSheetRecyclerFragment.setArguments(bundle);
                bottomSheetRecyclerFragment.show(getSupportFragmentManager(),"Show");
            }
        });

        //Set Panorama Gl
        plManager =new PLManager(PanoramaActivity.this);
        plManager.setContentView((ViewGroup)findViewById(R.id.pano_view));
        plManager.onCreate();

//        plManager.setInertiaEnabled(true);
        plManager.setZoomEnabled(true);
//        plManager.setMinDistanceToEnableScrolling(10);
        plManager.startSensorialRotation();

        //You can use accelerometer
        plManager.setAccelerometerEnabled(false);
        plManager.setAccelerometerLeftRightEnabled(true);
        plManager.setAccelerometerUpDownEnabled(false);
        plManager.setScrollingEnabled(true);
        plManager.setInertiaEnabled(true);
        plManager.setMinDistanceToEnableScrolling(2);


        //setFovRange determines Zoom range. Range values from -1.0f to 1.0f
        parent=findViewById(R.id.parent);
        plManager.getCamera().setFovRange(PLRange.PLRangeMake(0.0f, 1.0f));



        // Initialize GyroscopeObserver.
        //gyroscopeObserver = new GyroscopeObserver();
        // Set the maximum radian the device should rotate to show image's bounds.
        // It should be set between 0 and π/2.
        // The default value is π/9.
        //gyroscopeObserver.setMaxRotateRadian(Math.PI/9);

        //panoramaImageView = (PanoramaImageView) findViewById(R.id.panorama_image_view);
        //panoramaImageView.setEnablePanoramaMode(true);
        //panoramaImageView.setEnableScrollbar(true);

        // Set GyroscopeObserver for PanoramaImageView.
        //panoramaImageView.setGyroscopeObserver(gyroscopeObserver);


        //loadPanoImage("eem_panorama360.jpg");

        setPanoramaImages(bundle.getString("id"));

        //On RecyclerView Item click
        //onRecyclerViewItemClick();

    }


//    //On Recycler Item Touch
//    private void onRecyclerViewItemClick() {
//        panoramaRecyclerView.addOnItemTouchListener(new  RecyclerViewTouchListner(PanoramaActivity.this, panoramaRecyclerView, new RecyclerViewTouchListner.recyclerViewTouchListner() {
//            @Override
//            public void onclick(View child, int position) {
//
//                //loadPanoImage(((PanoramaImageAdapter)panoramaRecyclerView.getAdapter()).getItem(position).getUrl());
//
//                Snackbar.make(child,"برجاء الإنتظار حتي يتم تحميل الصورة",Snackbar.LENGTH_LONG).setAction("Action",null).show();
//                RetrieveFeedTask retrieveFeedTask=new RetrieveFeedTask();
//                retrieveFeedTask.execute("http://dasta.net/data/eem/Upload/Shop/Image/".
//                        concat(((PanoramaImageAdapter)panoramaRecyclerView.getAdapter()).getItem(position).getUrl()).concat(".png"));
//               // panoramaImageView.setImageResource(((PanoramaImageAdapter)panoramaRecyclerView.getAdapter()).getItem(position).getDrawable_id());
//                //panoramaImageView.setGyroscopeObserver(gyroscopeObserver);
//            }
//
//            @Override
//            public void onLongClick(View child, int position) {
//
//            }
//        }));
//    }

    //Set Panorama Images
    private void setPanoramaImages(final String id) {
        progressDialog=ProgressDialog.show(PanoramaActivity.this,"","جاري تحميل الصورة..",true,true);

        //Get Images
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
//                            for (int i=0;i<array.length();i++){
//                                PanoramaModel model0=new PanoramaModel();
//                                JSONObject item=array.getJSONObject(i);
////                                ImageModel imageModel  =new ImageModel();
////                                imageModel.setId(item.getInt("id"));
////                                imageModel.setShop_id(item.getInt("shop_id"));
//                                model0.setUrl(item.getString("name"));
//                                model0.setDepartmentName(item.getString("department"));
//                                panoramaList.add(model0);
//                            }

                            progressDialog.dismiss();
                            if (array.length()>0){
                                RetrieveFeedTask retrieveFeedTask=new RetrieveFeedTask();
                                retrieveFeedTask.execute("http://dasta.net/data/eem/Upload/Shop/Image/".
                                        concat(array.getJSONObject(0).getString("name")).concat(".png"));
                            }else {
                                noImgsTxt.setVisibility(View.VISIBLE);
                            }


//                            panoramaRecyclerView.setLayoutManager(new LinearLayoutManager(PanoramaActivity.this, LinearLayoutManager.HORIZONTAL, true));
//                            panoramaImageAdapter=new PanoramaImageAdapter(PanoramaActivity.this,panoramaList);
//                            panoramaRecyclerView.setAdapter(panoramaImageAdapter);
//                            panoramaImageAdapter.notifyDataSetChanged();

                        }else {
                            noImgsTxt.setVisibility(View.VISIBLE);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> params=new HashMap<>();
                    params.put("id",id);
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
            requestQueue.add(getImages);
    }

    @Override
    protected void onResume() {
        super.onResume();
        plManager.onResume();
    }

    @Override
    protected void onPause() {
        plManager.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        plManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return plManager.onTouchEvent(event);
    }

    //    private synchronized void loadPanoImage(String panoImageName) {
//        ImageLoaderTask task = backgroundImageLoaderTask;
//        if (task != null && !task.isCancelled()) {
//            // Cancel any task from a previous loading.
//            task.cancel(true);
//        }
//
//        // pass in the name of the image to load from assets.
//        VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
//        viewOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER|VrPanoramaView.Options.TYPE_MONO;
//
//
//        // create the task passing the widget view and call execute to start.
//        task = new ImageLoaderTask(panoWidgetView, viewOptions, panoImageName);
//        task.execute(getAssets());
//        backgroundImageLoaderTask = task;
//    }

    static class RetrieveFeedTask extends AsyncTask<String, Void, Bitmap> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(activity,"","جاري تحميل الصورة..",true,true);

        }

        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap feed) {
            progressDialog.dismiss();
            if (feed!=null){
                PLSphericalPanorama panorama = new PLSphericalPanorama();
//                Bitmap b = BitmapFactory.decodeResource(getResources(), resourceIds[index]);

//        panorama.setImage(new PLImage(PLUtils.getBitmap(this, resourceIds[index]), false));
                panorama.setImage(new PLImage(Bitmap.createScaledBitmap(feed, 1024, 1024, false), false));

                float pitch = 5f;
                float yaw = 0f;
                float zoomFactor = 0.8f;

                PLICamera camera = plManager.getPanorama().getCamera();
                pitch = camera.getPitch();
                yaw = camera.getYaw();
                zoomFactor = camera.getZoomFactor();

                panorama.getCamera().setRotationSensitivity(97.0F);
                panorama.getCamera().lookAtAndZoomFactor(pitch, yaw, 0.65f, true);
                plManager.setPanorama(panorama);
            }else {
                //loadPanoImage("eem_panorama360.jpg");
            }
        }
    }


}
