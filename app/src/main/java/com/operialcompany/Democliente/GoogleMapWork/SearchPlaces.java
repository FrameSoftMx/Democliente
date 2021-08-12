package com.operialcompany.Democliente.GoogleMapWork;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class SearchPlaces extends AppCompatActivity implements PlaceAutocompleteAdapter.PlaceAutoCompleteInterface, View.OnClickListener, SavedPlaceListener {

    Context mContext;

    private RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    PlaceAutocompleteAdapter mAdapter;

    EditText mSearchEdittext;

    String latNorth, lngNorth, latSouth, lngSouth;

    Button close_places;
    ImageView mClear;

    SharedPreferences placePref;
    String city_name;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_google_places);

        mContext = SearchPlaces.this;

        placePref = getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);
        city_name = placePref.getString(PreferenceClass.CURRENT_LOCATION_ADDRESS, "");

        getLatlngBounds();

        close_places = findViewById(R.id.cancel_places);
        close_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    public void getLatlngBounds(){

        String finalCityName = city_name.replaceAll(" ","%20");
        RequestQueue requestQueue = Volley.newRequestQueue(SearchPlaces.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Config.GET_CITY_BOUNDRIES+finalCityName,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));

                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i =0;i<jsonArray.length();i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        JSONObject geometry = jsonObject1.getJSONObject("geometry");
                        JSONObject bounds = geometry.getJSONObject("bounds");
                        JSONObject northeast = bounds.getJSONObject("northeast");
                        JSONObject southwest = bounds.getJSONObject("southwest");
                        try {
                            latNorth = northeast.optString("lat").trim();
                            lngNorth = northeast.optString("lng").trim();

                            latSouth = southwest.optString("lat").trim();
                            lngSouth = southwest.optString("lng").trim();


                        } catch (Exception e){
                            e.getCause();
                        }

                    }

                    initViews();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);

    }



    private void initViews(){
        mRecyclerView = (RecyclerView)findViewById(R.id.list_search);
        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);

        mSearchEdittext = (EditText)findViewById(R.id.search_et);
        mClear = (ImageView)findViewById(R.id.clear);
        mClear.setOnClickListener(this);

        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.row_item_view_placesearch);

        mRecyclerView.setAdapter(mAdapter);

        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    mClear.setVisibility(View.VISIBLE);
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    mClear.setVisibility(View.GONE);
                }
                if (!s.toString().equals("") ) {
                    mAdapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == mClear){
            mSearchEdittext.setText("");
            if(mAdapter!=null){
                mAdapter.clearList();
            }

        }
    }



    @Override
    public void onPlaceClick(ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> mResultList, int position) {
        if(mResultList!=null){
            try {
                final String placeId = String.valueOf(mResultList.get(position).placeId);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */
                StringBuilder stringBuilder=new StringBuilder();
                stringBuilder.append("https://maps.googleapis.com/maps/api/place/details/json?placeid=");
                stringBuilder.append(URLEncoder.encode(placeId, "utf8"));
                stringBuilder.append("&key=");
                stringBuilder.append(getResources().getString(R.string.key_for_places));



                RequestQueue rq = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, stringBuilder.toString(), null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonResults) {
                                String respo=jsonResults.toString();
                                Log.d("responce",respo);

                                JSONObject jsonObj = null;
                                try {
                                    jsonObj = new JSONObject(jsonResults.toString());

                                    Log.d("resp",jsonResults.toString());
                                    JSONObject result = jsonObj.getJSONObject("result");
                                    JSONObject geometry = result.getJSONObject("geometry");
                                    JSONObject location = geometry.getJSONObject("location");


                                    Intent data = new Intent();
                                    Log.e("Latiturekdjkjdf",String.valueOf(location.opt("lat")));
                                    data.putExtra("lat",String.valueOf(location.opt("lat")));
                                    data.putExtra("lng", String.valueOf(location.opt("lng")));
                                    setResult(RESULT_OK, data);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                Log.d("respoeee",error.toString());
                            }
                        });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rq.getCache().clear();
                rq.add(jsonObjectRequest);

            }
            catch (Exception e){

            }

        }
    }

    @Override
    public void onSavedPlaceClick(ArrayList<SavedAddress> mResultList, int position) {
        if(mResultList!=null){
            try {
                Intent data = new Intent();
                data.putExtra("lat",String.valueOf(mResultList.get(position).getLatitude()));
                data.putExtra("lng", String.valueOf(mResultList.get(position).getLongitude()));
                setResult(SearchPlaces.RESULT_OK, data);
                finish();

            }
            catch (Exception e){

            }

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
