package com.operialcompany.Democliente.ActivitiesAndFragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.operialcompany.Democliente.GoogleMapWork.MapsActivity;
import com.gmail.samehadar.iosdialog.CamomileSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by qboxus on 10/18/2019.
 */

public class AddressDetailFragment extends RootFragment  {

    Button cancleAddAddressBtn, saveAddressBtn;
    ImageView backIcon;
    CamomileSpinner pbHeaderProgress;

    RelativeLayout transparentLayer,progressDialog;
    SharedPreferences sharedPreferences;
    EditText stAddress, addCity, addInstructions;
    String addState;
    String latitude,longitude;


    RelativeLayout addLocDiv;
    TextView addLocTv;

    View view;
    Context context;


    public  AddressDetailFragment(){

    }

    FragmentCallback fragment_callback;
    public  AddressDetailFragment(FragmentCallback fragment_callback){
        this.fragment_callback=fragment_callback;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.add_address_detail, container, false);
         context=getContext();

         sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);

        initUI(view);

       addCity.setFocusable(false);

        return view;
    }


    public void initUI(View v){
        progressDialog = v.findViewById(R.id.progressDialog);
        transparentLayer = v.findViewById(R.id.transparent_layer);
        stAddress = v.findViewById(R.id.st_address);
        addCity = v.findViewById(R.id.add_city);
        addInstructions = v.findViewById(R.id.add_instructions);
        addLocDiv = v.findViewById(R.id.add_loc_div);

        addLocTv = v.findViewById(R.id.add_loc_tv);

        if(SearchFragment.FLAG_COUNTRY_NAME) {

            stAddress.setText(sharedPreferences.getString(PreferenceClass.STREET,""));


            addState = sharedPreferences.getString(PreferenceClass.STATE,"");
             addInstructions.setText(sharedPreferences.getString(PreferenceClass.INSTRUCTIONS,""));
            SearchFragment.FLAG_COUNTRY_NAME=false;
        }


        cancleAddAddressBtn = v.findViewById(R.id.cancle_add_address_btn);
        backIcon = v.findViewById(R.id.back_icon);
        saveAddressBtn = v.findViewById(R.id.save_address_btn);
        pbHeaderProgress = v.findViewById(R.id.pbHeaderProgress);
        pbHeaderProgress.start();

        saveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveAddressRequest();
            }
        });

        if(AddressListFragment.FLAG_ADDRESS_LIST){
            cancleAddAddressBtn.setVisibility(View.GONE);
            backIcon.setVisibility(View.VISIBLE);
            AddressListFragment.FLAG_ADDRESS_LIST = false;
            UserAccountFragment.FLAG_DELIVER_ADDRESS = true;

            backIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Functions.Hide_keyboard(getActivity());
                    getActivity().onBackPressed();
                }
            });

            addLocDiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                 startActivity(new Intent(getContext(),MapsActivity.class));
                }
            });

        }

        cancleAddAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }

    public void saveAddressRequest(){
        latitude = sharedPreferences.getString(PreferenceClass.LATITUDE, "");
        longitude = sharedPreferences.getString(PreferenceClass.LONGITUDE, "");

        String user_id = sharedPreferences.getString(PreferenceClass.pre_user_id,"");


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);
            jsonObject.put("default","1");
            jsonObject.put("street", stAddress.getText().toString());
            jsonObject.put("apartment","0");
            jsonObject.put("city", addCity.getText().toString());
            jsonObject.put("state","state");
            jsonObject.put("country","0");
            jsonObject.put("zip","0");
            jsonObject.put("instruction", addInstructions.getText().toString());
            jsonObject.put("lat",""+latitude);
            jsonObject.put("long",""+longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        ApiRequest.callApi(context, Config.ADD_DELIVERY_ADDRESS, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        if(fragment_callback!=null)
                            fragment_callback.onResponce(new Bundle());

                        getActivity().onBackPressed();

                    }

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });




    }



    @Override
    public void onResume() {
        super.onResume();
        if(MapsActivity.SAVE_LOCATION_ADDRESS) {
            MapsActivity.SAVE_LOCATION_ADDRESS = false;
            latitude = sharedPreferences.getString(PreferenceClass.LATITUDE, "");
            longitude = sharedPreferences.getString(PreferenceClass.LONGITUDE, "");

            Address locationAddress;

            locationAddress = getAddress(Double.parseDouble(latitude), Double.parseDouble(longitude));
            if (locationAddress != null) {


                String city="";
                if(locationAddress.getLocality()!=null && !locationAddress.getLocality().equals("null"))
                    city = ""+locationAddress.getLocality();

                String country="";
                if(locationAddress.getCountryName()!=null && !locationAddress.getCountryName().equals("null"))
                    country = ""+locationAddress.getCountryName();


                String address = city + " " + country;

                addLocTv.setText(latitude+","+longitude);



                stAddress.setText(locationAddress.getAddressLine(0));
                addCity.setText(city);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PreferenceClass.LATITUDE,latitude);
                editor.putString(PreferenceClass.LONGITUDE,longitude);
                editor.putString(PreferenceClass.CURRENT_LOCATION_ADDRESS, address).commit();

            }
        }

     }



    public Address getAddress(double latitude, double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


   }
