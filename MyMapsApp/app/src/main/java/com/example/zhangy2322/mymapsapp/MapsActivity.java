package com.example.zhangy2322.mymapsapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/* Tasks:
1. Create MapsActivity class.
2. Create key for access to maps APIs
3. Change the location of the initial marker to your place of birth, move the camera towards it, and display a message "Born here" if tap the marker. (see Android ActivityCompat, GoogleMap API's).
4. Drop a marker dot at your current location.
5. Add a "Map View" button and switch the map display between overhead satellite view and normal street map view. The button should toggle between the two modes.
6. Create a LocationManager based tracker. App should track user based on GPS and Wifi location services.
7. Create a point-of-interest (POI) search feature. App should drop markers at the location of the POI's.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean isGPSenabled = false;
    private boolean isNetworkEnabled = false;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
    private Location myLocation;
    private static final int MY_LOC_ZOOM_FACTOR = 20;
    private LatLng userLocation;
    private int isTracking = 0;

    private LocationManager locationManager;


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
        //    googleMap.setMyLocationEnabled(true);
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



        // Add a marker at current location

    }

    public void switchView(View v) {
        if (mMap.getMapType() == 1) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }



    public void dropMarker(String provider) {

        Log.d("MyMaps", "dropMarker: entered method");
        Toast.makeText(MapsActivity.this, "dropMarker: entered method", Toast.LENGTH_SHORT);

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyMaps", "Permission check failed");
                Toast.makeText(MapsActivity.this, "Permission check failed", Toast.LENGTH_SHORT);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListenerNetwork);
            myLocation = locationManager.getLastKnownLocation(provider);

            if (myLocation == null) {
                //Display a message via Log.d and Toast
                Log.d("MyMaps", "dropMarker: myLocation is null");
                Toast.makeText(MapsActivity.this, "dropMarker: myLocation is null", Toast.LENGTH_SHORT);
            } else {

                // Get user location
                userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                //Display a message with the lat/long
                Log.d("MyMaps", "LatLng: (" + myLocation.getLatitude() + ", " + myLocation.getLongitude() + ")");
                Toast.makeText(MapsActivity.this, "LatLng: (" + myLocation.getLatitude() + ", " + myLocation.getLongitude() + ")", Toast.LENGTH_SHORT);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);

                // Drop the marker (circles)
                if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                    Log.d("MyMaps", "dropMarker: network marker");
                    Toast.makeText(MapsActivity.this, "dropMarker: network marker", Toast.LENGTH_SHORT);
                    mMap.addCircle(new CircleOptions().center(userLocation).radius(1).strokeColor(Color.RED).strokeWidth(2).fillColor(Color.RED));

                }
                else {
                    Log.d("MyMaps", "dropMarker: GPS marker");
                    Toast.makeText(MapsActivity.this, "dropMarker: GPS marker", Toast.LENGTH_SHORT);
                    mMap.addCircle(new CircleOptions().center(userLocation).radius(1).strokeColor(Color.BLUE).strokeWidth(2).fillColor(Color.BLUE));

                }
                mMap.animateCamera(update);
            }
        }

        else {
            Log.d("MyMaps", "dropMarker: locationManager is null");
            Toast.makeText(MapsActivity.this, "dropMarker: locationManager is null", Toast.LENGTH_SHORT);
        }


    }



    public void getLocation() {
        try {
            // check permissions
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            //    mMap.setMyLocationEnabled(true);
            }

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                Log.d("MyMapsApp", "Failed Permission check 2");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            }


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

                if (isNetworkEnabled) {
                    Log.d("MyMaps", "getLocation: Network enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);

                    dropMarker("NETWORK_PROVIDER");


                    Log.d("MyMaps", "getLocation: NetworkLoc update request successful");
                    Toast.makeText(this, "Using Network", Toast.LENGTH_SHORT);
                }

                if (isGPSenabled) {
                    Log.d("MyMaps", "getLocation: GPS enabled - requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerGPS);

                    dropMarker("GPS_PROVIDER");


                    Log.d("MyMaps", "getLocation: GPS update request successful");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
            }


        } catch (Exception e) {
            Log.d("MyMaps", "getLocation: Caught exception");
            e.printStackTrace();
        }
    }


    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // output in Log.d and Toast that GPS is enabled and working
            Log.d("MyMaps", "GPS enabled");
            Toast.makeText(MapsActivity.this, "GPS enabled", Toast.LENGTH_SHORT).show();


            // drop a marker on the map - create a method called drop marker
            dropMarker(LocationManager.GPS_PROVIDER);


            // remove the NETWORK location updates. Hint: see LocationManager for update removal method
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(locationListenerNetwork);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // output in Log.d and Toast that GPS is enabled and working
            Log.d("MyMaps", "GPS enabled and working");
            Toast.makeText(MapsActivity.this, "GPS enabled and working", Toast.LENGTH_SHORT);


            // setup a switch statement to check the status input parameter
            // case LocationProvider.AVAILABLE (2)--> output message to Log.d and Toast
            // case LocationProvider.OUT_OF_SERVICE (0) --> output message like "GPS is unavailable", request updates from NETWORK_PROVIDER using locationManager.requestLocationUpdates
            // case LocationProvider.TEMPORARILY_UNAVAILABLE (1) --> output message like "GPS is unavailable", request updates from NETWORK_PROVIDER using locationManager.requestLocationUpdates

            switch(status) {

                case 2:
                    Log.d("MyMaps", "GPS provider available");
                    Toast.makeText(MapsActivity.this, "GPS provider available", Toast.LENGTH_SHORT);
                    break;

                case 1:
                    Log.d("MyMaps", "GPS provider temporarily unavailable");
                    Toast.makeText(MapsActivity.this, "GPS provider temporarily unavailable", Toast.LENGTH_SHORT);
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MyMaps", "Permission check failed");
                        Toast.makeText(MapsActivity.this, "Permission check failed", Toast.LENGTH_SHORT);
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);

                    break;

                case 0:
                    Log.d("MyMaps", "GPS provider unavailable");
                    Toast.makeText(MapsActivity.this, "GPS provider unavailable", Toast.LENGTH_SHORT);
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MyMaps", "Permission check failed");
                        Toast.makeText(MapsActivity.this, "Permission check failed", Toast.LENGTH_SHORT);
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            locationListenerNetwork);

                    break;
            }


        }

        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };

    android.location.LocationListener locationListenerNetwork = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // output in Log.d and Toast that network is enabled and working
            Log.d("MyMaps", "locationListenerNetwork: Network enabled");
            Toast.makeText(MapsActivity.this, "locationListenerNetwork: Network enabled", Toast.LENGTH_SHORT).show();


            // drop a marker on the map - create a method called drop marker
            dropMarker(LocationManager.NETWORK_PROVIDER);

            // relaunch the network provider request, request location updates from the network provider (NETWORK_PROVIDER)
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyMaps", "Permission check failed");
                Toast.makeText(MapsActivity.this, "Permission check failed", Toast.LENGTH_SHORT);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    locationListenerNetwork);


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // output message in Log.d and Toast
            Log.d("MyMaps", "Network enabled");
            Toast.makeText(MapsActivity.this, "Network enabled", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void track(View v) {
        isTracking++;


        if (isTracking % 2 == 1) {
            Log.d("MyMaps", "Tracking on");
            Toast.makeText(MapsActivity.this, "Tracking on", Toast.LENGTH_SHORT).show();
            getLocation();
        }

        else {
            Log.d("MyMaps", "Tracking off");
            Toast.makeText(MapsActivity.this, "Tracking off", Toast.LENGTH_SHORT).show();

            if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyMaps", "Permission check failed");
                Toast.makeText(MapsActivity.this, "Permission check failed", Toast.LENGTH_SHORT);
                return;
            }
            locationManager.removeUpdates(locationListenerNetwork);
            locationManager.removeUpdates(locationListenerGPS);
            Log.d("MyMaps", "track: remove updates");
            Toast.makeText(MapsActivity.this, "track: remove updates", Toast.LENGTH_SHORT);


        }






    }

}
