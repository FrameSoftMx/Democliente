package com.operialcompany.Democliente.ActivitiesAndFragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.GpsUtils;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Enable_location_A extends AppCompatActivity{



    Button enableLocationBtn;
    SharedPreferences sharedPreferences;


    public Enable_location_A() {
        // Required empty public constructor
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_location);




        sharedPreferences= getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);;

        enableLocationBtn =findViewById(R.id.enable_location_btn);
        enableLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();

            }
        });


        gpsstatus();
    }





    private void getLocationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        AllConstants.permission_location);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case   AllConstants.permission_location:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gpsstatus();
                } else {
                    Toast.makeText(this, "Please Grant permission", Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }


    public void gpsstatus(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!GpsStatus) {

            new GpsUtils(Enable_location_A.this).turnGPSOn(new GpsUtils.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {

                }
            });


        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        }
        else {
            getCurrentlocation();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2){
            gpsstatus();
        }

    }








    private FusedLocationProviderClient mFusedLocationClient;
    private void getCurrentlocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        createLocationRequest();
        startLocationUpdates();
    }

    public void goNext(Location location){

        if (location != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PreferenceClass.u_currentLat, "" + location.getLatitude());
            editor.putString(PreferenceClass.u_currentlng, "" + location.getLongitude());
            editor.commit();

           setResult(1);
           finish();


        } else {
            // else we will use the basic location

            if (sharedPreferences.getString(PreferenceClass.u_currentLat, "").equals("") || sharedPreferences.getString(PreferenceClass.u_currentlng, "").equals("")) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PreferenceClass.u_currentLat, "33.738045");
                editor.putString(PreferenceClass.u_currentlng, "73.084488");
                editor.commit();

            }

            finish();

        }
    }



    private LocationRequest mLocationRequest;
    private int UPDATE_INTERVAL = 3000;
    private int FATEST_INTERVAL = 3000;
    private int DISPLACEMENT = 0;



    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    LocationCallback locationCallback;
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        goNext(location);
                        stopLocationUpdates();

                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,locationCallback
                , Looper.myLooper());

    }


    protected void stopLocationUpdates() {
        if(mFusedLocationClient!=null && locationCallback!=null)
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }


}

