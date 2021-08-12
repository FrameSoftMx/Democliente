package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.operialcompany.Democliente.Adapters.CountryListAdapter;
import com.operialcompany.Democliente.Adapters.RestSpecialityAdapter;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.SpecialityModel;

import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.FontHelper;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.gmail.samehadar.iosdialog.CamomileSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by qboxus on 10/18/2019.
 */

public class RestaurantSpecialityFrag extends RootFragment {

    ImageView closeCountry;
    TextView titleCityTv;

    ArrayList<SpecialityModel> specialityArray;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    RestSpecialityAdapter recyclerViewadapter;
    RecyclerView cardRecyclerView;

    CamomileSpinner pbHeaderProgress;
    SharedPreferences sharedPreferences;
    SearchView searchView;

    RelativeLayout transparentLayer,progressDialog;

    View view;
    Context context;
    String lat,lon;

    public RestaurantSpecialityFrag (){

    }

    FragmentCallback fragmentCallback;
    public RestaurantSpecialityFrag (FragmentCallback fragmentCallback){
        this.fragmentCallback = fragmentCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_screen, container, false);
        context=getContext();

        FontHelper.applyFont(getContext(),getActivity().getWindow().getDecorView().getRootView(), AllConstants.verdana);

        searchView = view.findViewById(R.id.simpleSearchView);

        String txt="<font color = #dddddd>" + getString(R.string.search_restaurant_speciality) + "</font>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            searchView.setQueryHint(Html.fromHtml(txt, Html.FROM_HTML_MODE_LEGACY));
        } else {
            searchView.setQueryHint(Html.fromHtml(txt));
        }


        TextView searchText = (TextView)
                view.findViewById(R.id.search_src_text);
        searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        searchText.setPadding(0,0,0,0);
        LinearLayout searchEditFrame = (LinearLayout) searchView.findViewById(R.id.search_edit_frame); // Get the Linear Layout

        ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = 5;
        search(searchView);
        cardRecyclerView = view.findViewById(R.id.countries_list);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());
        cardRecyclerView.setLayoutManager(recyclerViewlayoutManager);
        sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        lat = sharedPreferences.getString(PreferenceClass.LATITUDE,"");
        lon = sharedPreferences.getString(PreferenceClass.LONGITUDE,"");

        if(lat.isEmpty() || lon.isEmpty()){
            lat = "31.4904023";
            lon = "74.2906989";
        }

        pbHeaderProgress = view.findViewById(R.id.pbHeaderProgress);
        pbHeaderProgress.start();

        init(view);


        return view;

    }

    public void init(View v){


        progressDialog = v.findViewById(R.id.progressDialog);
        transparentLayer = v.findViewById(R.id.transparent_layer);

        specialityArray = new ArrayList<>();
        titleCityTv = v.findViewById(R.id.title_city_tv);


        titleCityTv.setText(R.string.select_speciality);


        closeCountry = v.findViewById(R.id.close_country);
        closeCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();


            }
        });

        getRestSpecialityList();
    }


    public void getRestSpecialityList(){

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);


        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("lat", lat);
            jsonObject.put("long", lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(AllConstants.tag,lat+" "+lon);


        ApiRequest.callApi(context, Config.SHOW_REST_SPECIALITY_LIST, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    Log.d("JSONPost", jsonResponse.toString());

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");

                        for(int i = 0; i<jsonarray.length(); i++){

                            JSONObject jsonObject = jsonarray.getJSONObject(i);
                            JSONObject resJsonObject1 = jsonObject.getJSONObject("Restaurant");

                            SpecialityModel specialityModel = new SpecialityModel();

                            specialityModel.setName(resJsonObject1.optString("speciality"));
                            specialityModel.setId(resJsonObject1.optString("id"));


                            specialityArray.add(specialityModel);

                        }

                        if(specialityArray!=null) {
                            recyclerViewadapter = new RestSpecialityAdapter(specialityArray, getActivity());
                            cardRecyclerView.setAdapter(recyclerViewadapter);
                            recyclerViewadapter.notifyDataSetChanged();
                        }

                        recyclerViewadapter.setOnItemClickListner(new CountryListAdapter.OnItemClickListner() {
                            @Override
                            public void OnItemClicked(View view, int position) {



                                if(fragmentCallback !=null){
                                    Bundle bundle=new Bundle();
                                    bundle.putString("speciality",specialityArray.get(position).getName());
                                    fragmentCallback.onResponce(bundle);
                                }
                                getActivity().onBackPressed();

                            }
                        });
                    }


                }

                catch (JSONException e){
                    e.getMessage();

                }


            }
        });


    }

    private void search(androidx.appcompat.widget.SearchView searchView) {

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (recyclerViewadapter != null) recyclerViewadapter.getFilter().filter(newText);
                return true;
            }
        });
    }

}
