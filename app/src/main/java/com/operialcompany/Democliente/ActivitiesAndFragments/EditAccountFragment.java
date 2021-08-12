package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.gmail.samehadar.iosdialog.CamomileSpinner;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qboxus on 10/18/2019.
 */

public class EditAccountFragment extends RootFragment {

    ImageView backIcon;
    EditText firstName, lastName, phoneNumber, edEmail;
    Button btnEditDone;
    String firstNameStr, lastNameStr, userId,email,phone;
    SharedPreferences sharedPreferences;

    CamomileSpinner editAccountProgress;
    RelativeLayout transparentLayer,progressDialog;

    View view;
    Context context;


    public EditAccountFragment(){

    }

    FragmentCallback fragmentCallback;
    public EditAccountFragment(FragmentCallback fragmentCallback){
        this.fragmentCallback = fragmentCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.edit_account_fragment,container,false);
         context=getContext();

         sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
         init(view);
         return view;
    }

    public void init(View v){
        editAccountProgress = v.findViewById(R.id.editAccountProgress);
        editAccountProgress.start();
        progressDialog = v.findViewById(R.id.progressDialog);
        transparentLayer = v.findViewById(R.id.transparent_layer);
        firstName = v.findViewById(R.id.first_name);
        lastName = v.findViewById(R.id.last_name);
        phoneNumber = v.findViewById(R.id.ed_phone_number);
        edEmail = v.findViewById(R.id.ed_edit_email);

        btnEditDone = v.findViewById(R.id.btn_edit_done);
        btnEditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserProfile();
            }
        });

        firstNameStr = sharedPreferences.getString(PreferenceClass.pre_first,"");
        lastNameStr = sharedPreferences.getString(PreferenceClass.pre_last,"");
        userId = sharedPreferences.getString(PreferenceClass.pre_user_id,"");
        email = sharedPreferences.getString(PreferenceClass.pre_email,"");
        phone = sharedPreferences.getString(PreferenceClass.pre_contact,"");


        firstName.setText(firstNameStr);
        lastName.setText(lastNameStr);
        edEmail.setText(email);
        phoneNumber.setText(phone);


        backIcon = v.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getActivity().onBackPressed();

            }
        });

    }


    public void editUserProfile(){
        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();

        try {

            params.put("user_id", userId);
            if(firstName.getText().toString().isEmpty()){
                params.put("first_name", firstNameStr);
            }
            else {
                params.put("first_name", firstName.getText().toString());
            }
            if(lastName.getText().toString().isEmpty()) {
                params.put("last_name", lastNameStr);
            }
            else {
                params.put("last_name", lastName.getText().toString());
            }
            params.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.callApi(context, Config.EDIT_PROFILE, params, new Callback() {
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
                        JSONObject resultObj = json.getJSONObject("msg");
                        JSONObject json1 = new JSONObject(resultObj.toString());
                        JSONObject resultObj1 = json1.getJSONObject("UserInfo");

                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.profile_update_successfully),Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PreferenceClass.pre_first,resultObj1.optString("first_name"));
                        editor.putString(PreferenceClass.pre_last,resultObj1.optString("last_name"));
                        editor.commit();

                        if(fragmentCallback !=null)
                        fragmentCallback.onResponce(new Bundle());


                        getActivity().onBackPressed();


                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });


    }


}
