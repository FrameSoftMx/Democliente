package com.operialcompany.Democliente.GoogleMapWork;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.ImageSliderModel;
import com.operialcompany.Democliente.Adapters.SlidingImageAdapterTrackingStatus;
import com.operialcompany.Democliente.Constants.Config;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.operialcompany.Democliente.R;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback, android.location.LocationListener,
        RoutingListener{


    ImageView close;
    SharedPreferences sPref;
    String userId, orderId;
    String map_change = "1";
    private SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;

    String riderFName, riderLName, riderPhoneNumber;
    String userLat, userLong, restLat, restLong, riderLat, riderLong;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;



    DatabaseReference mDatabase;
    DatabaseReference mDatebaseTracking;
    FirebaseDatabase firebaseDatabase;


    LatLng destination;
    private List<Polyline> polylines;



    Bitmap riderBitmap, userBitmap, hotelBitmap;
    Marker riderMarker, userMarker, hotelMarker;



    public static String rider_id;
    private ArrayList<ImageSliderModel> ImagesArray;
    private static ViewPager mPager;
    private PageIndicatorView pageIndicatorView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);


        mPager = (ViewPager)findViewById(R.id.image_slider_pager);
        sPref = getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);
        userId = sPref.getString(PreferenceClass.pre_user_id, "");
        orderId = sPref.getString(PreferenceClass.ORDER_ID, "");
        pageIndicatorView = findViewById(R.id.pageIndicatorView);

        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference().child("tracking_status");
        mDatebaseTracking = firebaseDatabase.getReference().child(AllConstants.TRACKING);

        hotelBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) getResources()
                .getDrawable(R.drawable.hotel_pin)).getBitmap(), Functions.convertDpToPx(this,50), Functions.convertDpToPx(this,50), false);

        userBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) getResources()
                .getDrawable(R.drawable.user_pin)).getBitmap(), Functions.convertDpToPx(this,50), Functions.convertDpToPx(this,50), false);

        riderBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) getResources()
                .getDrawable(R.drawable.rider_pin)).getBitmap(), Functions.convertDpToPx(this,50), Functions.convertDpToPx(this,50), false);


        setupMapIfNeeded();
        mapFragment.setRetainInstance(true);

        polylines = new ArrayList<>();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

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



        showRiderLocationAgainstOrder();
        getStatus();


    }

   @SuppressWarnings("deprecation")
    public void showRiderLocationAgainstOrder() {


        ImagesArray = new ArrayList<ImageSliderModel>();
        OrderTrack_Model orderTrack_model=new OrderTrack_Model();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("user_id", userId);
            jsonObject1.put("order_id", orderId);
            jsonObject1.put("map_change", map_change);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(this, Config.SHOW_RIDER_LOCATION_AGAINST_LATLONG, jsonObject1, new Callback() {
            @Override
            public void onResponce(String resp) {

                map_change = "0";
                try {
                    JSONObject jsonObjectmain = new JSONObject(resp);
                    int code = Integer.parseInt(jsonObjectmain.getString("code"));
                    if (code == 200) {

                        JSONArray msgArray = jsonObjectmain.getJSONArray("msg");


                        for (int i = 0; i < msgArray.length(); i++) {

                            JSONObject jsonObject = msgArray.getJSONObject(i);
                            JSONObject RiderOrder = jsonObject.getJSONObject("RiderOrder");
                            JSONObject RiderLocation = RiderOrder.getJSONObject("RiderLocation");
                            JSONArray jsonArray = RiderLocation.getJSONArray("status");

                            orderTrack_model.rider_lat = RiderLocation.optString("lat","");
                            orderTrack_model.rider_lng = RiderLocation.optString("long","");
                            orderTrack_model.rider_user_id = RiderOrder.optString("rider_user_id");


                            orderTrack_model.order_status=new ArrayList<>();
                            for (int j = 0; j < jsonArray.length(); j++) {

                                JSONObject statusJsonObject = jsonArray.getJSONObject(j);


                                map_change = statusJsonObject.optString("map_change");

                                ImageSliderModel imageSliderModel = new ImageSliderModel();
                                imageSliderModel.setSliderImageUrl(statusJsonObject.optString("order_status"));

                                orderTrack_model.order_status.add(statusJsonObject.optString("order_status"));

                                ImagesArray.add(imageSliderModel);

                            }

                            pageIndicatorView.setCount(ImagesArray.size());
                            mPager.setAdapter(new SlidingImageAdapterTrackingStatus(TrackingActivity.this, ImagesArray));

                            JSONObject Rider = jsonObject.getJSONObject("Rider");
                            riderFName = Rider.optString("first_name");
                            riderLName = Rider.optString("last_name");
                            riderPhoneNumber = Rider.optString("phone");

                            JSONObject UserLocation = jsonObject.getJSONObject("UserLocation");
                            userLat = UserLocation.optString("lat","");
                            userLong = UserLocation.optString("long","");

                            JSONObject RestaurantLocation = jsonObject.getJSONObject("RestaurantLocation");

                            restLat = RestaurantLocation.optString("lat","");
                            restLong = RestaurantLocation.optString("long","");

                            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    marker.showInfoWindow();
                                    mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
                                            phoneCall();
                                        }
                                    });

                                    return false;
                                }
                            });


                            if ((riderLat.equalsIgnoreCase("") && riderLong.equalsIgnoreCase("")) && (userLat.equalsIgnoreCase("") && userLong.equalsIgnoreCase(""))) {

                                if(hotelMarker ==null)
                               hotelMarker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(
                                                new LatLng(Double.parseDouble(restLat), Double.parseDouble(restLong)))
                                        .icon(BitmapDescriptorFactory.fromBitmap(hotelBitmap)));

                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(  new LatLng(Double.parseDouble(restLat), Double.parseDouble(restLong)), 17));

                            }

                            else if (userLat.equalsIgnoreCase("") && userLong.equalsIgnoreCase("")) {

                                if(hotelMarker ==null)
                                    hotelMarker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(
                                                    new LatLng(Double.parseDouble(restLat), Double.parseDouble(restLong)))
                                            .icon(BitmapDescriptorFactory.fromBitmap(hotelBitmap)));

                                if(riderMarker ==null)
                                    riderMarker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(
                                                    new LatLng(Double.parseDouble(riderLat), Double.parseDouble(riderLong)))
                                            .draggable(true).visible(true).title(riderFName +" "+ riderLName).snippet(riderPhoneNumber).icon(BitmapDescriptorFactory.fromBitmap(riderBitmap)));

                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(Double.parseDouble(riderLat), Double.parseDouble(riderLong)), 17));

                               /*
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(new LatLng(Double.parseDouble(rest_lat), Double.parseDouble(rest_long)));
                                builder.include(new LatLng(Double.parseDouble(rider_lat), Double.parseDouble(rider_long)));
                                LatLngBounds bounds = builder.build();
                                CameraUpdate camerabounry = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                                mGoogleMap.animateCamera(camerabounry);
                               */

                            }

                            else if (restLat.equalsIgnoreCase("") && restLong.equalsIgnoreCase("")) {

                                if(hotelMarker !=null)
                                    hotelMarker.remove();

                                if(riderMarker ==null)
                                    riderMarker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(
                                                    new LatLng(Double.parseDouble(riderLat), Double.parseDouble(riderLong)))
                                            .draggable(true).visible(true).title(riderFName +" "+ riderLName).snippet(riderPhoneNumber).icon(BitmapDescriptorFactory.fromBitmap(riderBitmap)));


                                if(userMarker ==null)
                                    userMarker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(userLat),
                                                    Double.parseDouble(userLong)))
                                            .icon(BitmapDescriptorFactory.fromBitmap(userBitmap)));

                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(Double.parseDouble(riderLat), Double.parseDouble(riderLong)), 17));

                                /*
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(new LatLng(Double.parseDouble(rider_lat), Double.parseDouble(rider_long)));
                                builder.include(new LatLng(Double.parseDouble(user_lat), Double.parseDouble(user_long)));
                                LatLngBounds bounds = builder.build();
                                CameraUpdate camerabounry = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                                mGoogleMap.animateCamera(camerabounry);*/

                            }

                        }


                    } else {
                        Toast.makeText(TrackingActivity.this, resp, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private void setupMapIfNeeded() {

        if(mGoogleMap==null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    }




    public void getStatus(){
        mDatabase.keepSynced(true);
        DatabaseReference query2 = mDatabase.child(orderId).child("order_status");

        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                showRiderLocationAgainstOrder();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    public void mapChangeAnimation(final boolean user){
        mDatebaseTracking.keepSynced(true);
        DatabaseReference query = mDatebaseTracking.child(rider_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    riderLat = "" + dataSnapshot.child("rider_lat").getValue();
                    riderLong = "" + dataSnapshot.child("rider_long").getValue();
                    String rider_previous_lat = "" + dataSnapshot.child("rider_previous_lat").getValue();
                    String rider_previous_long = "" + dataSnapshot.child("rider_previous_long").getValue();
                    LatLng latlongLatest = new LatLng(Double.parseDouble(riderLat), Double.parseDouble(rider_previous_long));


                    if (!user) {

                        if (!riderLat.equalsIgnoreCase(rider_previous_lat) && !riderLong.equalsIgnoreCase(rider_previous_long)) {


                            Routing routing = new Routing.Builder()
                                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                                    .withListener(TrackingActivity.this)
                                    .alternativeRoutes(false)
                                    .waypoints(latlongLatest, destination)
                                    .build();
                            routing.execute();

                        }


                    } else {

                        if (!riderLat.equalsIgnoreCase(rider_previous_lat) && !riderLong.equalsIgnoreCase(rider_previous_long)) {


                            Routing routing = new Routing.Builder()
                                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                                    .withListener(TrackingActivity.this)
                                    .alternativeRoutes(false)
                                    .waypoints(latlongLatest, destination)
                                    .build();
                            routing.execute();


                        }
                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




     @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
     }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }


    public void phoneCall() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(TrackingActivity.this);
        builder1.setMessage("Cal to rider from you phone.");
        builder1.setTitle("Make a cal!");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Call",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        onCall();

                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Cancle",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }


    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    123);
        }

        else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+ riderPhoneNumber)));
        }
    }



    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();

        for (int i = 0; i <route.size(); i++) {


            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(this,R.color.colorRed));
            polyOptions.width(7);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
            polylines.add(polyline);

        }


    }

    @Override
    public void onRoutingCancelled() {
        Log.i("Rout TAG", "Routing was cancelled.");
    }


}
