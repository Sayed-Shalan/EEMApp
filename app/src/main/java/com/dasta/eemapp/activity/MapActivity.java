package com.dasta.eemapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.dasta.eemapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    //declare needed views
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad = "ar_EG";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_map);

        //get views

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //go to onMapReady when map is ready -- action

        //initialize location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //check if location is active in settings
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }

        //start connection to google api client
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        } else {
            Location userCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (userCurrentLocation != null) { //put marker on the current location
                MarkerOptions currentUserLocation = new MarkerOptions();
                LatLng currentUserLatLang = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                currentUserLocation.position(currentUserLatLang);
                currentUserLocation.title("\u200e" + "عربي");
                mMap.addMarker(currentUserLocation);

                MarkerOptions currentUserLocation2 = new MarkerOptions();
                LatLng currentUserLatLang2 = new LatLng(getIntent().getExtras().getDouble("shop_profile_map_lat"),
                        getIntent().getExtras().getDouble("shop_profile_map_lng"));
                currentUserLocation2.position(currentUserLatLang2);

                currentUserLocation2.title("\u200e" + "عربي");

                mMap.addMarker(currentUserLocation2);


                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, 16));

                GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                        .from(currentUserLatLang)
                        .to(new LatLng(getIntent().getExtras().getDouble("shop_profile_map_lat"),
                                getIntent().getExtras().getDouble("shop_profile_map_lng")))
                        .transportMode(TransportMode.DRIVING)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if (direction.isOK()) {
                                    Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(MapActivity.this, directionPositionList, 5, Color.RED);
                                    mMap.addPolyline(polylineOptions);
                                } else {
                                    Toast.makeText(MapActivity.this, "اتجاه غير معروف", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onDirectionFailure(Throwable t) {

                            }
                        });

            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; //get the object so u can deal with it outside this method

        //set click listener for map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                /*Intent intent=new Intent();
                intent.putExtra(map_return_latitude,latLng.latitude);
                intent.putExtra(Util.LONGITUDE_CODE,latLng.longitude);
                setResult(RESULT_OK,intent);
                finish();*/
            }
        });
    }

    //when activity starts
    @Override
    protected void onStart() {
        googleApiClient.connect(); //connect when activity started
        super.onStart();
    }

    //when activity stops
    @Override
    protected void onStop() {
        googleApiClient.disconnect(); //disconnect when activity stopped
        super.onStop();
    }

    //when user accept or cancel the permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //user accept the permission
            onConnected(null);
        } else { //user cancel "not allow" to use permission
            Toast.makeText(MapActivity.this, "No Permissions Granted", Toast.LENGTH_SHORT).show();
        }
    }

    //dialog appear to the user if gps is disabled
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
