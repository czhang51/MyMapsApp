package com.example.zhangy2322.mymapsapp;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean isGPSenabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15 * 1;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;

    private LocationManager locationManager;
    private LocationListener locationListenerNetwork;
    private LocationListener locationListenerGPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapsApp", "Failed Permission check 2");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }


        // Add a marker at birthplace and move the camera
        LatLng birthplace = new LatLng(39.9042, 116.4074);
        mMap.addMarker(new MarkerOptions().position(birthplace).title("Born here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(birthplace));


        mMap.setMyLocationEnabled(true);

        // Add a marker at current location


    }

    public void switchView(View v) {
        if ()
    }


    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //get GPS status
            isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSenabled) {
                Log.d("MyMaps", "getLocation: GPS is enabled");
            }

            //get network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                Log.d("MyMaps", "getLocation: NETWORK is enabled");
            }

            if (!isGPSenabled && !isNetworkEnabled) {
                Log.d("MyMaps", "getLocation: No provider is enabled");
            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {
                    Log.d("MyMaps", "getLocation: Network enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);



                    Log.d("MyMaps", "getLocation: NetworkLoc update request successful");
                    Toast.makeText(this, "Using Network", Toast.LENGTH_SHORT);
                }

                if (isGPSenabled) {
                    Log.d("MyMaps", "getLocation: GPS enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGPS);

                    Log.d("MyMaps", "getLocation: NetworkLoc update request successful");
                    Toast.makeText(this, "Using Network", Toast.LENGTH_SHORT);
                }
            }


        } catch (Exception e) {
            Log.d("MyMaps", "getLocation: Caught exception");
            e.printStackTrace();
        }
    }
}
