package com.dasta.eemapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.panoramagl.PLICamera;
import com.panoramagl.PLImage;
import com.panoramagl.PLSphericalPanorama;
import com.panoramagl.structs.PLRange;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowPanoramaActivity extends AppCompatActivity {

    ImageView cancel;
    Bundle bundle;
    private PLManager plManager;
    ProgressDialog progressDialog;
    private ImageLoaderTask backgroundImageLoaderTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_panorama);

        //Init Views
        initViews();

        //Set Panorama Gl
        plManager = new PLManager(this);
        plManager.setContentView((ViewGroup)findViewById(R.id.dialog_panorama_image_pano_view));
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
        plManager.getCamera().setFovRange(PLRange.PLRangeMake(0.0f, 1.0f));

        //Example with Sphere type (you need one image)
//        plManager.setType(PLViewType.PLViewTypeSpherical);
        //plManager.setValidForScrolling(true);
        //plManager.setAccelerometerEnabled(false);
//        plManager.startInertia();
//        plManager.setScrollingEnabled(true);

//        plManager.setScrollingEnabled(true);
//        plManager.startAnimation();

//        plManager.setAccelerometerLeftRightEnabled(false);
//        plManager.setAccelerometerEnabled(false);
//        plManager.setAccelerometerUpDownEnabled(false);
//
//        plManager.setAccelerometerLeftRightEnabled(true);
//        plManager.setAccelerometerSensitivity((float) 20.1);

        // pass in the name of the image to load from assets.
        RetrieveFeedTask retrieveFeedTask=new RetrieveFeedTask();
        retrieveFeedTask.execute("http://dasta.net/data/eem/Upload/Shop/Image/"+bundle.getString("img"));


        //On Cancel Button Click
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Init Views
    private void initViews() {
        cancel=findViewById(R.id.dialog_panorama_image_ic);
        bundle=getIntent().getExtras();
    }

    class RetrieveFeedTask extends AsyncTask<String, Integer, Bitmap> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog
                    .show(ShowPanoramaActivity.this,
                            null,
                            "جارى تحميل الصورة 360 ");
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
                loadPanoImage("eem_panorama360.jpg");
            }
        }
    }

    private synchronized void loadPanoImage(String panoImageName) {
        ImageLoaderTask task = backgroundImageLoaderTask;
        if (task != null && !task.isCancelled()) {
            // Cancel any task from a previous loading.
            task.cancel(true);
        }

//        // pass in the name of the image to load from assets.
//        VrPanoramaView.Options viewOptions = new VrPanoramaView.Options();
//        viewOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER|VrPanoramaView.Options.TYPE_MONO;

        // create the task passing the widget view and call execute to start.
        task = new ImageLoaderTask(plManager,panoImageName);
        task.execute(getAssets());
        backgroundImageLoaderTask = task;
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
}
