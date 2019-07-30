package com.dasta.eemapp.activity.Shop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dasta.eemapp.R;
import com.dasta.eemapp.helper.WebServies;

/**
 * Created by Mohamed on 27/08/2017.
 */

public class UpdateLocation extends Activity {

    private Button btnUpdateLocation;
    String lat, lng;
    WebServies webServies = new WebServies();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_update_location);

        btnUpdateLocation = (Button) findViewById(R.id.btnShopUpdateLocation);

        // Get user location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Keep track of user location.
        // Use callback/listener since requesting immediately may return null location.
        // IMPORTANT: TO GET GPS TO WORK, MAKE SURE THE LOCATION SERVICES ON YOUR PHONE ARE ON.
        // FOR ME, THIS WAS LOCATED IN SETTINGS > LOCATION.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, new Listener());
        // Have another for GPS provider just in case.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new Listener());
        // Try to request the location immediately
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location != null) {
            handleLatLng(location.getLatitude(), location.getLongitude());
            lat = location.getLatitude() + "";
            lng = location.getLongitude() + "";
        } else {
            lat = Home.lat;
            lng = Home.lng;
            Toast.makeText(getApplicationContext(), R.string.gpsError, Toast.LENGTH_LONG).show();
        }

        btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    webServies.updateShopLocation(UpdateLocation.this, lat, lng, Home.shop_id);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connectionMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UpdateLocation.this, Home.class);
        startActivity(intent);
        finish();
    }

    //**** to get current location of user

    /**
     * Handle lat lng.
     */
    private void handleLatLng(double latitude, double longitude) {
        Log.v("TAG", "(" + latitude + "," + longitude + ")");
    }

    /**
     * Listener for changing gps coords.
     */
    private class Listener implements LocationListener {
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            handleLatLng(latitude, longitude);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
