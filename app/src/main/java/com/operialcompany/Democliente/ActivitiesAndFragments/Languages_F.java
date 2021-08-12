package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.operialcompany.Democliente.Adapters.LanguagesAdapter;
import com.operialcompany.Democliente.Constants.AdapterClickListener;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.Languages_Model;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Languages_F extends RootFragment implements View.OnClickListener{


    View view;
    Context context;
    ArrayList<Languages_Model> data_list;
    RecyclerView recyclerView;
    LanguagesAdapter adapter;

    SharedPreferences sharedPreferences;

    List <String> countryCodeList;
    List <String> languageNames;
    List <String> languageNamesForApi;
    List <String> languageCode;

    public Languages_F() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_languages, container, false);
        context=getContext();

        countryCodeList =Arrays.asList(getResources().getStringArray(R.array.language_country_code));
        languageNamesForApi =Arrays.asList(getResources().getStringArray(R.array.language_names_for_api));
        languageNames =  Arrays.asList(getResources().getStringArray(R.array.language_names_for_show));
        languageCode =  Arrays.asList(getResources().getStringArray(R.array.language_code));

        sharedPreferences=context.getSharedPreferences(PreferenceClass.user,Context.MODE_PRIVATE);

        data_list=new ArrayList<>();

        recyclerView = view.findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter=new LanguagesAdapter(context, data_list, new AdapterClickListener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {
                sharedPreferences.edit().putString(PreferenceClass.selected_language, languageCode.get(pos)).commit();

                startActivity(new Intent(getActivity(),SplashScreen.class));
                getActivity().finish();
            }
        });

        recyclerView.setAdapter(adapter);

        addData();

        view.findViewById(R.id.back_icon).setOnClickListener(this::onClick);

        return view;
    }

    public void addData(){

        for(int i = 0; i< languageNames.size(); i++){

            Languages_Model item=new Languages_Model();
            item.name= languageNamesForApi.get(i).toUpperCase();
            item.language= languageNames.get(i);
            item.code= countryCodeList.get(i);
            data_list.add(item);

        }

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.back_icon:
                getActivity().onBackPressed();
                break;

        }

    }



}