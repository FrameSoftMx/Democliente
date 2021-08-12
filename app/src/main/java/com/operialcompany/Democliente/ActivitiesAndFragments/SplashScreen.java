package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.operialcompany.Democliente.BuildConfig;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.GoogleMapWork.MapsActivity;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.ContextWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;



public class SplashScreen extends AppCompatActivity {



    public static String versionCode;


    private final int SPLASH_TIME_OUT = 3000;

    TextView welcomeLocationTxt;
    private RelativeLayout mainWelcomeScreenLayout, mainSplashLayout, welcomeSearchDiv;


    String getCurrentLocationAddress;



    SharedPreferences sharedPreferences;
    double latitude, longitude;
    private Button welcomeShowRestaurantsBtn;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSION_DATA_ACCESS_CODE = 2;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;



    @Override
    protected void attachBaseContext(Context newBase) {

        String[] language_array = newBase.getResources().getStringArray(R.array.language_code);
        List<String> language_code = Arrays.asList(language_array);
        sharedPreferences = newBase.getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);
        String language = sharedPreferences.getString(PreferenceClass.selected_language, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && language_code.contains(language)) {
            Locale newLocale = new Locale(language);
            Context context = ContextWrapper.wrap(newBase, newLocale);
            super.attachBaseContext(context);
        }
        else {
            super.attachBaseContext(newBase);
        }

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        try {
            sharedPreferences = getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);
            getCurrentLocationAddress = sharedPreferences.getString(PreferenceClass.CURRENT_LOCATION_ADDRESS, "");
            if (!getCurrentLocationAddress.isEmpty()) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                setLanguageLocal();
            }

            setContentView(R.layout.splash);

            versionCode = BuildConfig.VERSION_NAME;

            createLocationRequest();
            startLocationUpdates();


            welcomeLocationTxt = findViewById(R.id.welcome_location_txt);
            mainWelcomeScreenLayout = findViewById(R.id.main_welcome_screen_layout);
            mainSplashLayout = findViewById(R.id.main_splash_layout);
            welcomeSearchDiv = findViewById(R.id.welcome_search_div);
            welcomeShowRestaurantsBtn = findViewById(R.id.welcome_show_restaurants_btn);
            welcomeShowRestaurantsBtn.setOnClickListener(view -> {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                MapsActivity.SAVE_LOCATION = false;
                finish();
            });


            welcomeSearchDiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(SplashScreen.this, MapsActivity.class);
                    startActivityForResult(i, PERMISSION_DATA_ACCESS_CODE);

                }
            });


            if (!getCurrentLocationAddress.isEmpty()) {

                mainWelcomeScreenLayout.setVisibility(View.GONE);
                mainSplashLayout.setVisibility(View.VISIBLE);

                /*final String android_id = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                SharedPreferences.Editor editor2 = sharedPreferences.edit();
                editor2.putString(PreferenceClass.UDID, android_id).commit();
                String getUserType = sharedPreferences.getString(PreferenceClass.USER_TYPE, "");
                boolean getLoINSession = sharedPreferences.getBoolean(PreferenceClass.IS_LOGIN, false);

                if (!getLoINSession) {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {

                    if (getUserType.equalsIgnoreCase("user")) {
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                }*/

              new Handler().postDelayed(() -> {
                    final String android_id = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    SharedPreferences.Editor editor2 = sharedPreferences.edit();
                    editor2.putString(PreferenceClass.UDID, android_id).commit();
                    String getUserType = sharedPreferences.getString(PreferenceClass.USER_TYPE, "");
                    boolean getLoINSession = sharedPreferences.getBoolean(PreferenceClass.IS_LOGIN, false);

                    if (!getLoINSession) {
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {

                        if (getUserType.equalsIgnoreCase("user")) {
                            Intent i = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }

                    }
                }, SPLASH_TIME_OUT);

            } else {

                checkLocationPermission();

            }

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    "error" + e.getMessage().toString(), Toast.LENGTH_SHORT);
        }

    }


    public void setLanguageLocal(){
        String [] language_array=getResources().getStringArray(R.array.language_code);
        List <String> language_code= Arrays.asList(language_array);

        String language=sharedPreferences.getString(PreferenceClass.selected_language,"");


        if(language_code.contains(language)) {
            Locale myLocale = new Locale(language);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = new Configuration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            onConfigurationChanged(conf);

        }



    }



    private void checkLocationPermission() {

        if(Functions.checkLocationStatus(this)){

            createLocationRequest();
            startLocationUpdates();
        }
        else {
            startActivityForResult(new Intent(this,Enable_location_A.class),AllConstants.Request_code_Location);
        }

    }



    private boolean checkPlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }

            return false;
        }

        return true;

    }



    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERMISSION_DATA_ACCESS_CODE) {

            if(resultCode == RESULT_OK) {
                latitude = Double.parseDouble(data.getStringExtra("lat"));
                longitude = Double.parseDouble(data.getStringExtra("lng"));

                Address locationAddress;

                locationAddress=getAddress(latitude,longitude);
                if(locationAddress!=null)
                {

                    String city="";
                    if(locationAddress.getLocality()!=null && !locationAddress.getLocality().equals("null"))
                        city = ""+locationAddress.getLocality();

                    String country="";
                    if(locationAddress.getCountryName()!=null && !locationAddress.getCountryName().equals("null"))
                        country = ""+locationAddress.getCountryName();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PreferenceClass.CURRENT_LOCATION_LAT_LONG,latitude+","+longitude);
                    editor.putString(PreferenceClass.CURRENT_LOCATION_ADDRESS,city+" " +country);
                    editor.putString(PreferenceClass.LATITUDE,String.valueOf(latitude));
                    editor.putString(PreferenceClass.LONGITUDE,String.valueOf(longitude));
                    editor.commit();

                    welcomeLocationTxt.setText(getCurrentLocationAddress);
                    welcomeLocationTxt.setText(city+" " +country);

                }


            }


        }

        else if(requestCode==3){
            checkLocationPermission();
        }

        else if(requestCode== AllConstants.Request_code_Location){
            Log.d(AllConstants.tag,"Request_code_Location");
            checkLocationPermission();
        }


    }

    public Address getAddress(double latitude,double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermission();
                }
            }
        }

    }



    private LocationRequest mLocationRequest;
    private int UPDATE_INTERVAL = 3000;
    private int FATEST_INTERVAL = 3000;
    private int DISPLACEMENT = 0;
    private FusedLocationProviderClient mFusedLocationClient;

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

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        Address locationAddress;

                        locationAddress = getAddress(latitude, longitude);
                        if (locationAddress != null) {

                            String city = "";
                            if (locationAddress.getLocality() != null && !locationAddress.getLocality().equals("null"))
                                city = "" + locationAddress.getLocality();

                            String country = "";
                            if (locationAddress.getCountryName() != null && !locationAddress.getCountryName().equals("null"))
                                country = "" + locationAddress.getCountryName();


                            if (!getCurrentLocationAddress.isEmpty()) {
                                welcomeLocationTxt.setText(getCurrentLocationAddress);
                            } else {

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(PreferenceClass.CURRENT_LOCATION_LAT_LONG, latitude + "," + longitude);
                                editor.putString(PreferenceClass.CURRENT_LOCATION_ADDRESS, city + " " + country);
                                editor.putString(PreferenceClass.LATITUDE, String.valueOf(latitude));
                                editor.putString(PreferenceClass.LONGITUDE, String.valueOf(longitude));
                                editor.commit();

                                welcomeLocationTxt.setText(getCurrentLocationAddress);
                                welcomeLocationTxt.setText(city + " " + country);
                            }

                        }

                    } else {

                        welcomeLocationTxt
                                .setText("Kalma Chowk, Lahore");

                    }


                    stopLocationUpdates();


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
