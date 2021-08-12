package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.FontHelper;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * Created by qboxus on 10/18/2019.
 */

public class LoginAcitvity extends RootFragment implements View.OnClickListener {

    SharedPreferences sPref;
    RelativeLayout fbDiv;
    FrameLayout loginMainDiv;
    CamomileSpinner logInProgress;
    RelativeLayout transparentLayer,progressDialog, googleSignInDiv;
    Button logInNow;
    TextView fbBtn;
    TextView loginText, tvEmail, tvPass, signUpTxt, tvForgetPassword, tvSignedUpNow, tvSignUp;
    EditText edEmail, edPassword;
    LoginButton loginButtonFb;
    ImageView backIcon;
    CallbackManager callbackManager;
    public GoogleSignInClient  mGoogleSignInClient;

    View view;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.login_activity, container, false);
         context=getContext();

        sPref = getContext().getSharedPreferences(PreferenceClass.user,Context.MODE_PRIVATE);


        edEmail = (EditText)view.findViewById(R.id.ed_email);
        edPassword =(EditText)view.findViewById(R.id.ed_password);
        logInNow = (Button)view.findViewById(R.id.btn_login);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


        tvSignUp = view.findViewById(R.id.tv_sign_up);
        tvSignedUpNow = view.findViewById(R.id.tv_signed_up_now);
        FontHelper.applyFont(getContext(), tvSignUp, AllConstants.verdana);

        //fbBtn = view.findViewById(R.id.fb_btn);
       // fbDiv = view.findViewById(R.id.fb_div);
        //fbDiv.setOnClickListener(this);
        //fbBtn.setOnClickListener(this);

        backIcon = view.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logOut();

                try  {
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                 getActivity().onBackPressed();

            }
        });


        loginMainDiv = view.findViewById(R.id.login_main_div);
        loginMainDiv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                return false;
            }
        });

        googleSignInDiv = view.findViewById(R.id.google_sign_in_div);
        googleSignInDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
                if (acct != null) {
                    String Email = acct.getEmail();
                    String password=acct.getId();

                    edEmail.setText(Email);


                }
                else {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 123);
                }
            }
        });



        tvEmail = (TextView)view.findViewById(R.id.tv_email);
        tvPass = (TextView)view.findViewById(R.id.tv_password);
        signUpTxt = (TextView)view.findViewById(R.id.tv_sign_up);

        logInProgress = view.findViewById(R.id.logInProgress);
        logInProgress.start();
        progressDialog = view.findViewById(R.id.progressDialog);
        transparentLayer = view.findViewById(R.id.transparent_layer);

        loginText = (TextView)view.findViewById(R.id.login_title);
        tvForgetPassword = view.findViewById(R.id.tv_forget_password);
        tvForgetPassword.setOnClickListener(this);
        FontHelper.applyFont(getContext(), tvForgetPassword, AllConstants.arial);


        logInNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean valid = Functions.isValidEmail(edEmail.getText().toString());

                if (TextUtils.isEmpty(edEmail.getText().toString())) {
                    Toast.makeText(getContext(), getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(edPassword.getText().toString())) {

                    Toast.makeText(getContext(),  getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
                }

                else if (edPassword.getText().toString().length()<6) {

                    Toast.makeText(getContext(),  getString(R.string.enter_password_atleast_characters), Toast.LENGTH_SHORT).show();
                }

                else if (!valid) {

                    Toast.makeText(getContext(),  getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
                }

                else {

                    String this_email = edEmail.getText().toString();
                    String this_password = edPassword.getText().toString();
                    AddressListFragment.CART_NOT_LOAD = true;
                    login(this_email,this_password);

                }
            }
        });


        tvSignedUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SingUpActivity singUpActivity = new SingUpActivity();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.add(R.id.login_main_div, singUpActivity,"parent").commit();

            }
        });


        return view;
    }



    public void fb_init(){

        callbackManager = CallbackManager.Factory.create();


        //loginButtonFb = (LoginButton) view.findViewById(R.id.login_button_fb);
        loginButtonFb.setReadPermissions(Arrays.asList("email"));


        loginButtonFb.setFragment(this);


        loginButtonFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        String useremail = user.optString("email");

                        edEmail.setText(useremail);

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "last_name,first_name,email");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });


    }

    private void login(String email,String pass){

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);


        String _lat = sPref.getString(PreferenceClass.LATITUDE,"");
        String _long = sPref.getString(PreferenceClass.LONGITUDE,"");
        String device_tocken = sPref.getString(PreferenceClass.device_token,"");


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", pass);
            jsonObject.put("device_token", device_tocken);
            jsonObject.put("role","user");

            if(_lat.isEmpty()){
                jsonObject.put("lat", "31.5042483");
            }else {
                jsonObject.put("lat", _lat);
            }
            if(_long.isEmpty()){
                jsonObject.put("long", "74.3307944");
            }else {
                jsonObject.put("long", _long);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.callApi(context,  Config.LOGIN_URL, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {
                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);


                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {
                        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                        transparentLayer.setVisibility(View.GONE);
                        progressDialog.setVisibility(View.GONE);
                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONObject resultObj = json.getJSONObject("msg");
                        JSONObject json1 = new JSONObject(resultObj.toString());
                        JSONObject resultObj1 = json1.getJSONObject("UserInfo");
                        JSONObject resultObj2 = json1.getJSONObject("User");

                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putString(PreferenceClass.pre_email, edEmail.getText().toString());
                        editor.putString(PreferenceClass.pre_pass, edPassword.getText().toString());
                        editor.putString(PreferenceClass.pre_first, resultObj1.optString("first_name"));
                        editor.putString(PreferenceClass.pre_last, resultObj1.optString("last_name"));
                        editor.putString(PreferenceClass.pre_contact, resultObj1.optString("phone"));
                        editor.putString(PreferenceClass.pre_user_id, resultObj1.optString("user_id"));

                        editor.putBoolean(PreferenceClass.IS_LOGIN, true);
                        editor.putString(PreferenceClass.USER_TYPE,resultObj2.optString("role"));
                        editor.commit();

                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();


                    }else{

                        try  {
                            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {

                        }

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        Toast.makeText(getContext(),json.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }




    @Override
    public void onClick(View view) {

        if(view == fbDiv){

            fb_init();

            LoginManager.getInstance().logOut();

            loginButtonFb.performClick();
        }

        if(view== fbBtn){

            fb_init();

            LoginManager.getInstance().logOut();

            loginButtonFb.performClick();
        }


        else if(view== tvForgetPassword){
            startActivity(new Intent(getActivity(),RecoverPasswordActivity.class));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("handleSignInResult", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            String Email = acct.getEmail();

            edEmail.setText(Email);


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(callbackManager!=null)
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);


        }
        else {
            loginButtonFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    final AccessToken accessToken = loginResult.getAccessToken();
                    GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                            String useremail = user.optString("email");

                            edEmail.setText(useremail);

                        }
                    });
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException exception) {
                }
            });
        }

    }




}
