package com.operialcompany.Democliente.ActivitiesAndFragments;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.operialcompany.Democliente.Adapters.RestaurantsAdapter;
import com.operialcompany.Democliente.Constants.AdapterClickListener;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.DataParser;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.MapWorker;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.RestaurantsModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RestaurentsOnMap_F extends RootFragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    MapView mMapView;
    public GoogleMap googleMap;
    MapWorker mapWorker;

    LatLng current_latlng;


    ArrayList<RestaurantsModel> data_list;
    private RecyclerView recyclerView;
    RestaurantsAdapter adapter;

    int currentPage=0;

    View view;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_restaurents_on_map, container, false);
        context = getContext();


        mMapView = (MapView) view.findViewById(R.id.map_main_layout);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        view.findViewById(R.id.back_icon).setOnClickListener(this::onClick);
        view.findViewById(R.id.list_btn).setOnClickListener(this::onClick);

        data_list=new ArrayList<>();
        recyclerView = view.findViewById(R.id.recylerview);
        recyclerView.setLayoutManager( new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        adapter = new RestaurantsAdapter(data_list,context, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                RestaurantsModel model = (RestaurantsModel) object;

                if(view.getId()==R.id.favorite_icon){

                    if(PreferenceClass.sharedPreferences.getBoolean(PreferenceClass.IS_LOGIN,false)){

                        addFavoriteRestaurant(pos,model.restaurant_id);
                    }
                }
                else {
                    Fragment restaurantMenuItemsFragment = new RestaurantMenuItemsFragment();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", model);
                    restaurantMenuItemsFragment.setArguments(bundle);
                    transaction.addToBackStack(null);
                    transaction.add(R.id.RestaurentsOnMap_F, restaurantMenuItemsFragment, "parent").commit();

                }
            }
        });

        new PagerSnapHelper().attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);


        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int page_no = recyclerView.computeHorizontalScrollOffset() / recyclerView.getWidth();
                if (page_no != currentPage || page_no == 0) {
                    currentPage=page_no;
                    if(hashMap!=null) {
                        RestaurantsModel item=data_list.get(currentPage);
                        Marker marker =hashMap.get(item.restaurant_id);
                        mapWorker.animateCameraTo(googleMap,marker.getPosition().latitude,marker.getPosition().longitude, AllConstants.max_zoom);
                    }
                }
            }
        });

        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setTrafficEnabled(false);
        this.googleMap.setIndoorEnabled(true);
        this.googleMap.setBuildingsEnabled(true);
        this.googleMap.getUiSettings().setRotateGesturesEnabled(false);

        mMapView.onResume();
        mapWorker=new MapWorker(context,this.googleMap);
        checkLocationStatus();

        if (new DarkModePrefManager(context).isNightMode()) {
            googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            context, R.raw.map_night_style));
        }



        this.googleMap.setOnMarkerClickListener(RestaurentsOnMap_F.this);

    }


    private void checkLocationStatus() {

        if(Functions.checkLocationStatus(context)){
            getCurrentlocation();

        }
        else {
            startActivityForResult(new Intent(getContext(),Enable_location_A.class),AllConstants.Request_code_Location);
        }
    }

    private FusedLocationProviderClient mFusedLocationClient;
    protected void getCurrentlocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        else {

            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(),location.getLongitude()), 15.0f);

                            mapWorker.animateCameraTo(googleMap,cameraUpdate);

                            current_latlng=new LatLng(location.getLatitude(),location.getLongitude());

                            getRestaurantList();

                        }
                    }
                });
            }

        }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back_icon:
               getActivity().onBackPressed();
                break;

            case R.id.list_btn:
                getActivity().onBackPressed();
                break;
        }
    }



    public void getRestaurantList() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", current_latlng.latitude);
            jsonObject.put("long", current_latlng.longitude);
            jsonObject.put("current_time", formattedDate);
            jsonObject.put("user_id", PreferenceClass.sharedPreferences.getString(PreferenceClass.pre_user_id,""));


        }

        catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.callApi(context, Config.SHOW_RESTAURANTS, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        ArrayList<RestaurantsModel> tem_list = new ArrayList<>();

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");

                        JSONArray promoted = json.optJSONArray("promoted");
                        if (promoted != null) {
                            for (int i = 0; i < promoted.length(); i++) {

                                JSONObject json1 = promoted.getJSONObject(i);

                                tem_list.add(DataParser.Pasrse_Restaurent(json1));
                            }
                        }

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject json1 = jsonarray.getJSONObject(i);

                            tem_list.add(DataParser.Pasrse_Restaurent(json1));

                        }

                        data_list.addAll(tem_list);
                        adapter.notifyDataSetChanged();
                        addMarker();

                    } else {
                        JSONObject json = new JSONObject(jsonResponse.toString());
                        Toast.makeText(getApplicationContext(), json.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }



            }
        });



    }


    HashMap<String, Marker> hashMap;
    public void addMarker(){
        hashMap = mapWorker.Add_Saved_places_Marker(data_list);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {


        String id=marker.getTag().toString();
        changeRestuarentPosition(id);

        return false;
    }


    public void changeRestuarentPosition(String id){

        for(int i=0;i<data_list.size();i++){
            if(data_list.get(i).restaurant_id.equals(id)){
                recyclerView.scrollToPosition(i);
                break;
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==AllConstants.Request_code_Location){
            checkLocationStatus();
        }
    }


    public void addFavoriteRestaurant(int pos,String res_id){

        final String user_id = PreferenceClass.sharedPreferences.getString(PreferenceClass.pre_user_id,"");
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_id",user_id);
            jsonObject.put("restaurant_id",res_id);
            jsonObject.put("favourite","1");

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(context,false,false);
        ApiRequest.callApi(context, Config.ADD_FAV_RESTAURANT, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                Functions.cancelLoader();

                try {
                    JSONObject  converResponseToJson = new JSONObject(resp);

                    int code_id  = Integer.parseInt(converResponseToJson.optString("code"));
                    if(code_id == 200) {
                        RestaurantsModel item=data_list.get(pos);
                        if(item.restaurant_isFav.equals("0")){
                            item.restaurant_isFav="1";
                        }
                        else {
                            item.restaurant_isFav="0";
                        }
                        data_list.remove(pos);
                        data_list.add(pos,item);
                        adapter.notifyDataSetChanged();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }


}