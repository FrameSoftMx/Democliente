package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.FontHelper;
import com.gmail.samehadar.iosdialog.CamomileSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverPasswordActivity extends AppCompatActivity implements View.OnClickListener {


    ViewFlipper viewFlipper;
    EditText recoverEmail, codeEdit, edNewPass, edConfirmPass;
    SharedPreferences sharedPreferences;
    CamomileSpinner progressBar;
    RelativeLayout transparentLayer, progressDialog;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        sharedPreferences = getSharedPreferences(PreferenceClass.user,Context.MODE_PRIVATE);

        FrameLayout frameLayout = findViewById(R.id.main_recover_pass);
        FontHelper.applyFont(RecoverPasswordActivity.this,frameLayout, AllConstants.verdana);
        initUI();

    }

    public void initUI(){

        progressBar = findViewById(R.id.signUpProgress);
        progressBar.start();
        progressDialog = findViewById(R.id.progressDialog);
        transparentLayer =findViewById(R.id.transparent_layer);



        viewFlipper=findViewById(R.id.viewflliper);

        recoverEmail = findViewById(R.id.recover_email);
        findViewById(R.id.btn_recover).setOnClickListener(this::onClick);


        codeEdit =findViewById(R.id.code_edit);
        findViewById(R.id.sendcode_btn).setOnClickListener(this::onClick);


        edNewPass =findViewById(R.id.ed_new_pass);
        edConfirmPass =findViewById(R.id.ed_confirm_pass);
        findViewById(R.id.btn_change_pass).setOnClickListener(this::onClick);


        findViewById(R.id.Goback1).setOnClickListener(this::onClick);
        findViewById(R.id.Goback2).setOnClickListener(this::onClick);
        findViewById(R.id.Goback3).setOnClickListener(this::onClick);

    }

    public void callapiSendemail(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", recoverEmail.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        progressDialog.setVisibility(View.VISIBLE);
        transparentLayer.setVisibility(View.VISIBLE);

        ApiRequest.callApi(this, Config.forgotPassword, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                progressDialog.setVisibility(View.GONE);
                transparentLayer.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));
                    if (code_id==200){

                        Toast.makeText(RecoverPasswordActivity.this, getString(R.string.password_sent_to_given_email),Toast.LENGTH_LONG).show();
                        viewFlipper.showNext();

                    }
                    else {
                        Toast.makeText(RecoverPasswordActivity.this, getString(R.string.your_email_is_not_correct),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        });


    }


    public void callApiSendCode(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", recoverEmail.getText().toString());
            jsonObject.put("code", codeEdit.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        progressDialog.setVisibility(View.VISIBLE);
        transparentLayer.setVisibility(View.VISIBLE);


        ApiRequest.callApi(this, Config.verifyforgotPasswordCode, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                progressDialog.setVisibility(View.GONE);
                transparentLayer.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));
                    if (code_id==200){

                        JSONArray msg=jsonResponse.optJSONArray("msg");
                        JSONObject user_info=msg.getJSONObject(0).optJSONObject("UserInfo");
                        if(user_info!=null){
                             user_id=user_info.optString("user_id");
                             Toast.makeText(RecoverPasswordActivity.this, getString(R.string.reset_the_password), Toast.LENGTH_SHORT).show();
                             viewFlipper.showNext();
                        }
                    }
                    else {
                        Toast.makeText(RecoverPasswordActivity.this,  getString(R.string.your_code_is_not_correct),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        });


    }



    String user_id;
    public void callApiChangePassword(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id",user_id);
            jsonObject.put("password", edNewPass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog.setVisibility(View.VISIBLE);
        transparentLayer.setVisibility(View.VISIBLE);

        ApiRequest.callApi(this, Config.changePasswordForgot, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                progressDialog.setVisibility(View.GONE);
                transparentLayer.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));
                    if (code_id==200){
                        Toast.makeText(RecoverPasswordActivity.this, getString(R.string.password_rest_successfully), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(RecoverPasswordActivity.this, getString(R.string.password_rest_fail),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        });


    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.Goback1:
                finish();
                break;

            case R.id.btn_recover:
                if(TextUtils.isEmpty(recoverEmail.getText().toString())){

                    Toast.makeText(this, R.string.please_enter_the_email, Toast.LENGTH_SHORT).show();
                }else
                callapiSendemail();
                break;


            case R.id.Goback2:
                viewFlipper.showPrevious();
                break;

            case R.id.sendcode_btn:
                if(TextUtils.isEmpty(codeEdit.getText().toString())){

                    Toast.makeText(this, R.string.please_enter_the_code, Toast.LENGTH_SHORT).show();
                }else
                    callApiSendCode();
                break;


            case R.id.Goback3:
                viewFlipper.showPrevious();
                break;

            case R.id.btn_change_pass:
                String pass= edNewPass.getText().toString();
                String confirm_pass= edConfirmPass.getText().toString();

                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(this, R.string.please_enter_the_password, Toast.LENGTH_SHORT).show();
                }if(TextUtils.isEmpty(confirm_pass)){
                    Toast.makeText(this, R.string.please_enter_the_confirm_password, Toast.LENGTH_SHORT).show();
                }if(!pass.equals(confirm_pass)){
                    Toast.makeText(this, R.string.new_password_and_confirm_password_not_match, Toast.LENGTH_SHORT).show();
                 }else
                    callApiChangePassword();
                break;





        }
    }


    @Override
    public void onBackPressed() {
        if(viewFlipper.getDisplayedChild()==0){
            finish();
        }else {
            viewFlipper.showPrevious();
        }
    }
}
