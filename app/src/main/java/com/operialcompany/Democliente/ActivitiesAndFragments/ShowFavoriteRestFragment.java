package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.operialcompany.Democliente.Constants.AdapterClickListener;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.DataParser;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Adapters.RestaurantsAdapter;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.RestaurantsModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class ShowFavoriteRestFragment extends RootFragment {

    SwipeRefreshLayout refreshLayout;

    ArrayList<RestaurantsModel> dataList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RestaurantsAdapter adapter;


    SharedPreferences sharedPreferences;

    ImageView back_icon;
    EditText searchView;
    public static boolean FROM_FAVORITE;

   String userId;
   RelativeLayout noJobDiv;

   View view;
   Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.show_favorite_fragment, container, false);
        context=getContext();

        sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(PreferenceClass.pre_user_id,"");
        recyclerView = view.findViewById(R.id.restaurant_recycler_view);

        dataList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RestaurantsAdapter(dataList, getContext(), new AdapterClickListener() {
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
                    transaction.add(R.id.restaurent_main_layout, restaurantMenuItemsFragment, "parent").commit();

                }
            }
        });

        recyclerView.setAdapter(adapter);


        init(view);
        getRestaurantList(userId);
        return view;

    }

    public void init(View v){
        noJobDiv = v.findViewById(R.id.no_job_div);

        searchView = v.findViewById(R.id.search_edit);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        back_icon = v.findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                getActivity().onBackPressed();


            }
        });
        refreshLayout = v.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getRestaurantList(userId);
                refreshLayout.setRefreshing(false);
            }
        });

    }

    public void getRestaurantList(String user_id){

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Functions.showLoader(context,false,false);

        ApiRequest.callApi(context, Config.SHOW_FAV_RESTAURANT, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.cancelLoader();

                try {
                    JSONObject jsonResponse = new JSONObject(resp);
                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");

                        ArrayList<RestaurantsModel> temp_list=new ArrayList<>();

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject json1 = jsonarray.getJSONObject(i);

                            temp_list.add(DataParser.Pasrse_favourite_Restaurent(json1));

                        }


                        dataList.clear();

                        dataList.addAll(temp_list);

                        adapter.notifyDataSetChanged();
                        if(dataList.isEmpty()){
                            noJobDiv.setVisibility(View.VISIBLE);
                        }

                    }

                    else{
                        noJobDiv.setVisibility(View.VISIBLE);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    noJobDiv.setVisibility(View.VISIBLE);
                }


            }
        });


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
                        dataList.remove(pos);
                        adapter.notifyDataSetChanged();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }



}
