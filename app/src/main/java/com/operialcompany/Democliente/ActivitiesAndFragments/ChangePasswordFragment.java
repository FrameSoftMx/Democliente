package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.gmail.samehadar.iosdialog.CamomileSpinner;


import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by qboxus on 10/18/2019.
 */

public class ChangePasswordFragment extends RootFragment {

    ImageView backIcon;
    EditText oldPassword, newPassword, confirmPassword;
    Button btnChangePass;
    SharedPreferences sharedPreferences;

    CamomileSpinner changePassProgress;
    RelativeLayout transparentLayer,progressDialog;

    View view;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.change_password_fragment, container, false);
         context=getContext();

         sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);

        init(view);

        return view;
    }

    public void init(View v){
        changePassProgress = v.findViewById(R.id.changePassProgress);
        changePassProgress.start();
        progressDialog = v.findViewById(R.id.progressDialog);
        transparentLayer = v.findViewById(R.id.transparent_layer);
        btnChangePass = v.findViewById(R.id.btn_change_pass);

        oldPassword = v.findViewById(R.id.ed_old_pass);
        newPassword = v.findViewById(R.id.ed_new_pass);
        confirmPassword = v.findViewById(R.id.ed_confirm_pass);

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(checkValidations()){
                   changePasswordVollyRequest();
               }
            }
        });
        backIcon = v.findViewById(R.id.back_icon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try  {
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                getActivity().onBackPressed();



            }
        });


    }

    public boolean checkValidations(){
        if (oldPassword.getText().toString().trim().equals("") || oldPassword.getText().toString().length()<6) {

            Toast.makeText(getContext(), "Check password length can not be shorter than 6!", Toast.LENGTH_SHORT).show();
            oldPassword.setError("Check password length can not be shorter than 6!");
            return false;

        }


        else if (newPassword.getText().toString().trim().equals("") || newPassword.getText().toString().length()<6) {

            Toast.makeText(getContext(), "Check password length can not be shorter than 6!", Toast.LENGTH_SHORT).show();
            newPassword.setError("Check password length can not be shorter than 6!");
            return false;
        }

        else if (confirmPassword.getText().toString().trim().equals("") || confirmPassword.getText().toString().length()<6) {

            Toast.makeText(getContext(), "Check password length can not be shorter than 6!", Toast.LENGTH_SHORT).show();
            confirmPassword.setError("Check password length can not be shorter than 6!");
            return false;
        }

        else if(!newPassword.getText().toString().equals(confirmPassword.getText().toString())){
            confirmPassword.setError("Password does not match");
            newPassword.setError("Password does not match");
            return false;
        }

        else if(newPassword.getText().toString().equals(oldPassword.getText().toString())) {
            Toast.makeText(context, "Old password and new password can't same", Toast.LENGTH_SHORT).show();

            return false;
        }
        else {
            return true;
        }


    }

  public void changePasswordVollyRequest(){
        String getUser_id = sharedPreferences.getString(PreferenceClass.pre_user_id,"");
       JSONObject jsonObject = new JSONObject();
      try {
          jsonObject.put("user_id",getUser_id);
          jsonObject.put("old_password", oldPassword.getText().toString());
          jsonObject.put("new_password", newPassword.getText().toString());
      } catch (JSONException e) {
          e.printStackTrace();
      }


      TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
      transparentLayer.setVisibility(View.VISIBLE);
      progressDialog.setVisibility(View.VISIBLE);

      ApiRequest.callApi(context, Config.CHANGE_PASSWORD, jsonObject, new Callback() {
          @Override
          public void onResponce(String resp) {

              TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
              transparentLayer.setVisibility(View.GONE);
              progressDialog.setVisibility(View.GONE);

              try {
                  JSONObject  jsonResponse = new JSONObject(resp);

                  int code_id  = Integer.parseInt(jsonResponse.optString("code"));
                  if (code_id==200){
                       Toast.makeText(getContext(),"Password Changed Successfully",Toast.LENGTH_LONG).show();

                      UserAccountFragment userAccountFragment = new UserAccountFragment();
                      FragmentTransaction transaction = getFragmentManager().beginTransaction();
                      transaction.replace(R.id.change_pass_main_container, userAccountFragment);
                      transaction.addToBackStack(null);
                      transaction.commit();

                  }
                  else {
                       Toast.makeText(getContext(),"Password Not Changed",Toast.LENGTH_LONG).show();
                  }

              } catch (JSONException e) {
                  e.printStackTrace();
              }
          }
      });



  }


}
