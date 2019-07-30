package com.dasta.eemapp;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.panoramagl.PLICamera;
import com.panoramagl.PLImage;
import com.panoramagl.PLSphericalPanorama;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;


public class ImageLoaderTask extends AsyncTask<AssetManager, Void, Bitmap> {

    private static final String TAG = "ImageLoaderTask";
    private final String assetName;
    private static WeakReference<Bitmap> lastBitmap = new WeakReference<>(null);
    private static String lastName;
    private PLManager plManager=null;

    public ImageLoaderTask(PLManager plManager, String assetName) {
        this.plManager = plManager;
        this.assetName = assetName;
    }

    @Override
    protected Bitmap doInBackground(AssetManager... params) {
        AssetManager assetManager = params[0];

        if (assetName.equals(lastName) && lastBitmap.get() != null) {
            return lastBitmap.get();
        }

        try(InputStream istr = assetManager.open(assetName)) {
            Bitmap b = BitmapFactory.decodeStream(istr);
            lastBitmap = new WeakReference<>(b);
            lastName = assetName;
            return b;
        } catch (IOException e) {
            Log.e(TAG, "Could not decode default bitmap: " + e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (plManager != null && bitmap != null) {
            PLSphericalPanorama panorama = new PLSphericalPanorama();
//                Bitmap b = BitmapFactory.decodeResource(getResources(), resourceIds[index]);

//        panorama.setImage(new PLImage(PLUtils.getBitmap(this, resourceIds[index]), false));
            panorama.setImage(new PLImage(Bitmap.createScaledBitmap(bitmap, 1024, 1024,
                    false), false));

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
            Log.e("PLmanager is nul","Image Loader Task");
        }
    }
}
