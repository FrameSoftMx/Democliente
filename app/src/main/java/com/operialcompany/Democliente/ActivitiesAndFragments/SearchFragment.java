package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.operialcompany.Democliente.Adapters.CountryListAdapter;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.CountryListModel;
import com.operialcompany.Democliente.R;
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


public class SearchFragment extends RootFragment implements View.OnClickListener {
    SearchView searchView;

    ArrayList<CountryListModel> arrayListCountry;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    CountryListAdapter recyclerViewadapter;
    RecyclerView cardRecyclerView;

    CamomileSpinner pbHeaderProgress;
    SharedPreferences sharedPreferences;
    public static boolean FLAG_COUNTRY_NAME;
    RelativeLayout transparentLayer,progressDialog;


    View view;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.search_screen, container, false);
        context=getContext();

        progressDialog = view.findViewById(R.id.progressDialog);
        transparentLayer = view.findViewById(R.id.transparent_layer);
        searchView = view.findViewById(R.id.simpleSearchView);

        String txt="<font color = #dddddd>\" + \"Search Country\" + \"</font>";


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


        pbHeaderProgress = view.findViewById(R.id.pbHeaderProgress);
        pbHeaderProgress.start();

        view.findViewById(R.id.close_country).setOnClickListener(this::onClick);

        getCountryList();


        return view;
    }



    public void getCountryList(){
        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        arrayListCountry = new ArrayList<>();

        ApiRequest.callApi(context, Config.SHOW_COUNTRIES_LIST, null, new Callback() {
            @Override
            public void onResponce(String resp) {


                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("countries");

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject json1 = jsonarray.getJSONObject(i);

                            JSONObject jsonObjTax = json1.getJSONObject("Tax");

                            CountryListModel cardDetailModel = new CountryListModel();
                            cardDetailModel.setCountry_name(jsonObjTax.optString("country"));
                            if(jsonObjTax.optString("country_code").isEmpty())
                            {
                                cardDetailModel.setCountry_code("");
                            }
                            else {
                                cardDetailModel.setCountry_code(jsonObjTax.optString("country_code"));
                            }

                            arrayListCountry.add(cardDetailModel);

                        }
                        if(arrayListCountry!=null) {
                            recyclerViewadapter = new CountryListAdapter(arrayListCountry, getActivity());
                            cardRecyclerView.setAdapter(recyclerViewadapter);
                            recyclerViewadapter.notifyDataSetChanged();

                            recyclerViewadapter.setOnItemClickListner(new CountryListAdapter.OnItemClickListner() {
                                @Override
                                public void OnItemClicked(View view, int position) {

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(PreferenceClass.COUNTRY_NAME,arrayListCountry.get(position).getCountry_name());
                                    editor.commit();
                                    FLAG_COUNTRY_NAME = true;
                                    AddressListFragment.FLAG_ADDRESS_LIST = true;
                                    Fragment restaurantMenuItemsFragment = new AddressDetailFragment();
                                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                    transaction.add(R.id.search_main_container, restaurantMenuItemsFragment,"parent").commit();

                                }
                            });
                        }



                    }

                }catch (JSONException e){

                    e.getCause();

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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_country:
                getActivity().onBackPressed();
                break;
        }
    }


}




