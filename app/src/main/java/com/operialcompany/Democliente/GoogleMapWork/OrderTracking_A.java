package com.operialcompany.Democliente.GoogleMapWork;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.operialcompany.Democliente.Chat_Module.Chat_A;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.MapWorker;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class OrderTracking_A extends AppCompatActivity implements
        OnMapReadyCallback,
        OnClickListener  {

    private BottomSheetBehavior bts_behavior;


    private String orderId;

    private MapView mapView;
    private GoogleMap googleMap;
    private MapWorker mapWorker;

    DatabaseReference mDatabase;
    DatabaseReference mDatebaseTracking;
    FirebaseDatabase firebaseDatabase;

    TextView tvRiderName;

    public LatLng restLocation, riderLocation, userLocation;

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("type"))
                if (!(intent.getExtras().getString("type").equalsIgnoreCase("order_responce")))
                    showRiderLocationAgainstOrder();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_tracking);


        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference().child("tracking_status");
        mDatebaseTracking = firebaseDatabase.getReference().child(AllConstants.TRACKING);

        findViewById(R.id.iv_back).setOnClickListener(this);



        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        methodFindviewbyid();
        orderId = "" + getIntent().getStringExtra("order_id");

        findViewById(R.id.rl_call).setOnClickListener(this::onClick);
        findViewById(R.id.rl_chat).setOnClickListener(this::onClick);


    }


    String map_change = "1";
    OrderTrack_Model orderTrack_model=new OrderTrack_Model();
    public void showRiderLocationAgainstOrder() {

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("user_id", PreferenceClass.sharedPreferences.getString(PreferenceClass.pre_user_id,""));
            jsonObject1.put("order_id", orderId);
            jsonObject1.put("map_change", map_change);
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(this, Config.SHOW_RIDER_LOCATION_AGAINST_LATLONG, jsonObject1, new Callback() {
            @Override
            public void onResponce(String resp) {

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


                            JSONObject Rider = jsonObject.getJSONObject("Rider");
                            orderTrack_model.rider_first_name = Rider.optString("first_name");
                            orderTrack_model.rider_last_name = Rider.optString("last_name");
                            orderTrack_model.rider_phone = Rider.optString("phone");

                            JSONObject UserLocation = jsonObject.getJSONObject("UserLocation");
                            orderTrack_model.user_lat = UserLocation.optString("lat","");
                            orderTrack_model.user_lng = UserLocation.optString("long","");

                            JSONObject RestaurantLocation = jsonObject.getJSONObject("RestaurantLocation");
                            orderTrack_model.rest_lat = RestaurantLocation.optString("lat","");
                            orderTrack_model.rest_lng = RestaurantLocation.optString("long","");


                            if(!orderTrack_model.rider_lat.equalsIgnoreCase("") && !orderTrack_model.rider_lng.equalsIgnoreCase(""))
                                riderLocation =new LatLng(Double.parseDouble(orderTrack_model.rider_lat),Double.parseDouble(orderTrack_model.rider_lng));

                            if(!orderTrack_model.rest_lat.equalsIgnoreCase("") && !orderTrack_model.rest_lng.equalsIgnoreCase(""))
                                restLocation =new LatLng(Double.parseDouble(orderTrack_model.rest_lat),Double.parseDouble(orderTrack_model.rest_lng));

                            if(!orderTrack_model.user_lat.equalsIgnoreCase("") && !orderTrack_model.user_lng.equalsIgnoreCase(""))
                                userLocation =new LatLng(Double.parseDouble(orderTrack_model.user_lat),Double.parseDouble(orderTrack_model.user_lng));



                            orderTrack_model.order_status=new ArrayList<>();
                            for (int j = 0; j < jsonArray.length(); j++) {

                                JSONObject statusJsonObject = jsonArray.getJSONObject(j);
                                JSONObject Restaurant=statusJsonObject.getJSONObject("Restaurant");

                                map_change = statusJsonObject.optString("map_change");
                                orderTrack_model.order_status.add(statusJsonObject.optString("order_status"));

                                orderTrack_model.rest_id=Restaurant.optString("id");
                                orderTrack_model.rest_name=Restaurant.optString("name");
                                orderTrack_model.rest_phone=Restaurant.optString("phone");


                                changeRideStatus(statusJsonObject.optString("code"));


                            }


                        }

                        changeMap();



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(AllConstants.tag,e.toString());
                }


            }
        });


    }



    Marker rider_marker,user_marker,hotel_marker;
    public void changeMap(){
        if(riderLocation !=null){

            rider_marker = mapWorker.add_marker(riderLocation, mapWorker.riderMarker);

            mapWorker.animateCameraTo(googleMap, riderLocation.latitude, riderLocation.longitude,17);

            tvRiderName.setText(orderTrack_model.rider_first_name+" "+orderTrack_model.rider_last_name);


            if(userLocation !=null){
                user_marker = mapWorker.add_marker(userLocation, mapWorker.riderMarker);

            }

            findViewById(R.id.ll_chat_call).setVisibility(View.VISIBLE);

        }
        else if(!orderTrack_model.rest_lat.equalsIgnoreCase("") && !orderTrack_model.rest_lng.equalsIgnoreCase("")){

            hotel_marker = mapWorker.add_marker(restLocation, mapWorker.hotelLocationMarker);

            tvRiderName.setText(orderTrack_model.rest_name);


            mapWorker.animateCameraTo(googleMap, restLocation.latitude, restLocation.longitude,17);

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.googleMap.getUiSettings().setRotateGesturesEnabled(false);

        mapView.onResume();

        mapWorker = new MapWorker(OrderTracking_A.this, googleMap);

        if (new DarkModePrefManager(this).isNightMode()) {
            googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_night_style));

        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }



        getStatus();
    }


    private void methodFindviewbyid() {
        FrameLayout captain_rkl = (FrameLayout) findViewById(R.id.fl_id);

        findViewById(R.id.rl_chat).setOnClickListener(this);
        findViewById(R.id.rl_call).setOnClickListener(this);

        tvRiderName =findViewById(R.id.tv_rider_name);

        bts_behavior = BottomSheetBehavior.from(captain_rkl);
        bts_behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bts_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }else if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }

        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_chat:
                openChat();
                break;

            case R.id.rl_call:
                calltorider();
                break;

            case R.id.iv_back:
                OrderTracking_A.super.onBackPressed();
                break;


        }
    }


    private void openChat() {

        Intent intent=new Intent(this,Chat_A.class);
        intent.putExtra("Receiverid", orderTrack_model.rider_user_id);
        intent.putExtra("Receiver_name", orderTrack_model.rider_first_name+" "+orderTrack_model.rider_last_name);
        intent.putExtra("Receiver_pic", "");
        intent.putExtra("Order_id", orderId);
        intent.putExtra("senderid", PreferenceClass.sharedPreferences.getString(PreferenceClass.pre_user_id,""));

        startActivity(intent);

    }


    private void calltorider() {

        if (orderTrack_model.rider_phone.isEmpty()){
            Functions.ShowToast(OrderTracking_A.this, OrderTracking_A.this.getString(R.string.rider_has_no_number));
        }
        else {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+orderTrack_model.rider_phone));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);

        }

    }


    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("order_responce");
        registerReceiver(broadcastReceiver,intentFilter);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (broadcastReceiver!=null)
        unregisterReceiver(broadcastReceiver);

    }


    public void changeRideStatus(String rider_status) {

        switch (rider_status){
            case "0":
                ImageView order_processing_tick=findViewById(R.id.order_processing_tick);
                order_processing_tick.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_check_color,null));
                break;

            case "1":
                ImageView order_accepted_tick=findViewById(R.id.order_accepted_tick);
                order_accepted_tick.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_check_color,null));
                break;

            case "2":
                ImageView order_assigned_tick=findViewById(R.id.order_assigned_tick);
                order_assigned_tick.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_check_color,null));
                break;

            case "3":
                ImageView pickup_loc_tick=findViewById(R.id.pickup_loc_tick);
                pickup_loc_tick.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_check_color,null));
                break;

            case "4":
                ImageView order_picked_tick=findViewById(R.id.order_picked_tick);
                order_picked_tick.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_check_color,null));
                break;

            case "5":
                ImageView dropoff_location_tick=findViewById(R.id.dropoff_location_tick);
                dropoff_location_tick.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_check_color,null));
                break;

            case "6":
                ImageView order_deliverd_tick=findViewById(R.id.order_deliverd_tick);
                order_deliverd_tick.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_check_color,null));
                break;
        }


    }



}