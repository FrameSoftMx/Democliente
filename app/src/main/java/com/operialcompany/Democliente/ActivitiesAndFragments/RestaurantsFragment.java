package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.operialcompany.Democliente.Adapters.RestaurantsAdapter;
import com.operialcompany.Democliente.Adapters.SlidingImageAdapter;
import com.operialcompany.Democliente.Constants.AdapterClickListener;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.DataParser;
import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.GoogleMapWork.MapsActivity;
import com.operialcompany.Democliente.Models.ImageSliderModel;
import com.operialcompany.Democliente.Models.RestaurantsModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.FontHelper;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.rd.PageIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by qboxus on 10/18/2019.
 */

public class RestaurantsFragment extends RootFragment  {

    private  ViewPager mPager;
    private  int currentPage = 0;
    private ArrayList<ImageSliderModel> ImagesArray;
    private ImageView resFilter;


    private TextView titleCityTv;


    private ArrayList<RestaurantsModel> datalist;
    private RecyclerView recyclerView;
    private RecyclerViewHeader recyclerHeader;
    SwipeRefreshLayout refreshLayout;

    RecyclerView.LayoutManager layoutManager;
    RestaurantsAdapter adapter;

    CamomileSpinner progressBar;
    String currentLoc;
    SharedPreferences sharedPreferences;
    EditText searchEdit;

    public Timer swipeTimer;

    Handler handler = new Handler();
    Runnable timeCounter;
    String lat, lon, userId;

    RelativeLayout transparentLayer, progressDialog;
    PageIndicatorView pageIndicatorView;


    View view;
    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.resturent_fragment, container, false);
        context = getContext();


        FontHelper.applyFont(getContext(), getActivity().getWindow().getDecorView().getRootView(), AllConstants.verdana);

        FrameLayout frameLayout = view.findViewById(R.id.RestaurantsFragment);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return true;
            }
        });

        sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        currentLoc = sharedPreferences.getString(PreferenceClass.CURRENT_LOCATION_ADDRESS, "");
        lat = sharedPreferences.getString(PreferenceClass.LATITUDE, "");
        lon = sharedPreferences.getString(PreferenceClass.LONGITUDE, "");
        userId = sharedPreferences.getString(PreferenceClass.pre_user_id, "");
        titleCityTv = view.findViewById(R.id.title_city_tv);
        if (currentLoc.isEmpty()) {
            titleCityTv.setText("Kalma Chowk Lahore");
        } else {
            titleCityTv.setText(currentLoc);
        }

        if (lat.isEmpty() || lon.isEmpty()) {
            lat = "31.4904023";
            lon = "74.2906989";
        }


        resFilter = view.findViewById(R.id.res_filter);
        resFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment restaurantMenuItemsFragment = new RestaurantSpecialityFrag(new FragmentCallback() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (bundle != null) {
                            String speciality = bundle.getString("speciality");
                            getRestaurantListAgainstSpeciality(speciality);
                        }
                    }
                });
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.add(R.id.RestaurantsFragment, restaurantMenuItemsFragment, "ParentFragment").commit();
            }
        });

        initUI(view);


        return view;
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            if (ShowFavoriteRestFragment.FROM_FAVORITE) {
                getrestaurantlist();
                ShowFavoriteRestFragment.FROM_FAVORITE = false;
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void initUI(final View v) {
        progressDialog = v.findViewById(R.id.progressDialog);
        transparentLayer = v.findViewById(R.id.transparent_layer);



        searchEdit = v.findViewById(R.id.search_edit);

        search();

        mPager = (ViewPager) v.findViewById(R.id.image_slider_pager);
        pageIndicatorView = v.findViewById(R.id.pageIndicatorView);
        progressBar = v.findViewById(R.id.restaurantProgress);
        progressBar.start();


        recyclerWithHeader(v);

        datalist = new ArrayList<>();
        adapter = new RestaurantsAdapter(datalist, getContext(), new AdapterClickListener() {
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
                    transaction.add(R.id.RestaurantsFragment, restaurantMenuItemsFragment, "parent").commit();
                    if (swipeTimer != null) {
                        swipeTimer.cancel();
                        swipeTimer.purge();
                    }
                }
            }
        });

        recyclerView.setAdapter(adapter);



        getrestaurantlist();
        initPager(v);

        refreshLayout = v.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (handler != null && timeCounter != null) {
                    handler.removeCallbacks(timeCounter);
                }
                getrestaurantlist();
                initPager(v);
                refreshLayout.setRefreshing(false);

            }
        });

        titleCityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(), MapsActivity.class));
            }
        });


        view.findViewById(R.id.map_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRestaurentOnMap();
               // startActivity(new Intent(getContext(), RestaurentsOnMap_A.class));
            }
        });

    }


    public void openRestaurentOnMap(){
        Fragment restaurentsOnMap_a = new RestaurentsOnMap_F();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.RestaurantsFragment, restaurentsOnMap_a, "parent").commit();

    }

    public void getrestaurantlist() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", lat);
            jsonObject.put("long", lon);
            jsonObject.put("current_time", formattedDate);
            jsonObject.put("user_id", userId);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        datalist.clear();

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

                        datalist.addAll(tem_list);
                        adapter.notifyDataSetChanged();

                    } else {
                        JSONObject json = new JSONObject(jsonResponse.toString());
                        Toast.makeText(getApplicationContext(), json.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

                progressDialog.setVisibility(View.GONE);
                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
                transparentLayer.setVisibility(View.GONE);


            }
        });

    }


    public void getRestaurantListAgainstSpeciality(String speciality) {


        String lat = sharedPreferences.getString(PreferenceClass.LATITUDE, "");
        String lon = sharedPreferences.getString(PreferenceClass.LONGITUDE, "");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", lat);
            jsonObject.put("long", lon);
            jsonObject.put("user_id", userId);
            jsonObject.put("speciality", speciality);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        datalist.clear();

        ApiRequest.callApi(context, Config.SHOW_REST_AGAINST_SPECIALITY, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                try {
                    JSONObject jsonResponse = new JSONObject(resp);


                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject json1 = jsonarray.getJSONObject(i);

                            datalist.add(DataParser.Pasrse_Restaurent(json1));
                        }

                        adapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

            }
        });


    }


    private void initPager(final View v) {

        ImagesArray = new ArrayList<ImageSliderModel>();
        ApiRequest.callApi(context, Config.SHOW_SLIDER, null, new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject json1 = jsonarray.getJSONObject(i);

                            JSONObject jsonObjRestaurant = json1.getJSONObject("AppSlider");
                            ImageSliderModel imageSliderModel = new ImageSliderModel();
                            imageSliderModel.setSliderImageUrl(jsonObjRestaurant.optString("image"));
                            ImagesArray.add(imageSliderModel);
                        }


                        try {
                            pageIndicatorView.setCount(ImagesArray.size());
                            mPager.setAdapter(new SlidingImageAdapter(getContext(), ImagesArray));
                        } catch (NullPointerException e) {
                            e.getCause();
                        }

                        PageIndicatorView indicator = (PageIndicatorView) v.findViewById(R.id.pageIndicatorView);
                        indicator.setViewPager(mPager);
                        try {

                            timeCounter = new Runnable() {

                                @Override
                                public void run() {
                                    if ((currentPage + 1) > ImagesArray.size()) {
                                        currentPage = 0;
                                    } else {
                                        currentPage++;
                                    }
                                    mPager.setCurrentItem(currentPage);
                                    handler.postDelayed(timeCounter, 5 * 1000);

                                }
                            };
                            handler.post(timeCounter);

                        } catch (IllegalStateException e) {
                            e.getCause();
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void search() {


        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {

                    if (adapter != null)
                        adapter.getFilter().filter(s);


                    RelativeLayout.LayoutParams parms =
                            new RelativeLayout.LayoutParams(0, 0);
                    recyclerHeader.setLayoutParams(parms);

                } else {

                    RelativeLayout.LayoutParams parms =
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, AllConstants.height / 3);
                    recyclerHeader.setLayoutParams(parms);

                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    public void recyclerWithHeader(View view) {

        recyclerView = view.findViewById(R.id.restaurant_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        recyclerHeader = (RecyclerViewHeader) view.findViewById(R.id.header);
        recyclerHeader.attachTo(recyclerView);

        RelativeLayout.LayoutParams parms =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, AllConstants.height / 3);
        recyclerHeader.setLayoutParams(parms);

    }



    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(timeCounter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(timeCounter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timeCounter);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(timeCounter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (MapsActivity.SAVE_LOCATION) {

            lat = sharedPreferences.getString(PreferenceClass.LATITUDE, "");
            lon = sharedPreferences.getString(PreferenceClass.LONGITUDE, "");

            Address locationAddress;

            locationAddress = getAddress(Double.parseDouble(lat), Double.parseDouble(lon));
            if (locationAddress != null) {

                String city = "";
                if (locationAddress.getLocality() != null && !locationAddress.getLocality().equals("null"))
                    city = "" + locationAddress.getLocality();

                String country = "";
                if (locationAddress.getCountryName() != null && !locationAddress.getCountryName().equals("null"))
                    country = "" + locationAddress.getCountryName();


                String address = city + " " + country;

                titleCityTv.setText(address);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PreferenceClass.CURRENT_LOCATION_ADDRESS, address).commit();
                getrestaurantlist();
                MapsActivity.SAVE_LOCATION = false;


            }
        }


    }



    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public void addFavoriteRestaurant(int pos,String res_id){

        final String user_id = sharedPreferences.getString(PreferenceClass.pre_user_id,"");
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
                       RestaurantsModel item=datalist.get(pos);
                       if(item.restaurant_isFav.equals("0")){
                           item.restaurant_isFav="1";
                       }
                       else {
                           item.restaurant_isFav="0";
                       }
                       datalist.remove(pos);
                       datalist.add(pos,item);
                       adapter.notifyDataSetChanged();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }



}
