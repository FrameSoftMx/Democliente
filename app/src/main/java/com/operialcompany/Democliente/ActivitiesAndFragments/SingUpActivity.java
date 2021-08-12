package com.operialcompany.Democliente.ActivitiesAndFragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.reinaldoarrosi.maskededittext.MaskedEditText;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import at.markushi.ui.CircleButton;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * Created by qboxus on 10/18/2019.
 */

public class SingUpActivity extends RootFragment implements AdapterView.OnItemSelectedListener {

    CircleButton confirmBtn;


    ImageView backSignUp, backIconVerification, backIconConfirm;

    LinearLayout verificationDiv, mainSignUp, confirmDiv, verificationMainScreen, confirmationMainScreen;
    Button btn_signup, btn_done;


    EditText editText1, editText2, editText3, editText4, eFirst, eLast, eEmail, ePassword;
    MaskedEditText phone;
     TextView countryCode, btnResend;

    RelativeLayout fbDiv;

    FrameLayout mainSignUpDiv;

    private Spinner spinner;

    SharedPreferences preferences;

    CamomileSpinner progressBar;
    RelativeLayout transparent_layer, progressDialog;
    GoogleSignInClient mGoogleSignInClient;
    TextView fb_btn;

    LoginButton login_button_fb;
    CallbackManager callbackManager;

    RelativeLayout google_sign_in_div;


    View v;
    Context context;


    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_sing_up, container, false);
        context=getContext();

        progressBar = v.findViewById(R.id.signUpProgress);
        progressBar.start();
        progressDialog = v.findViewById(R.id.progressDialog);
        transparent_layer = v.findViewById(R.id.transparent_layer);

        preferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);

        

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_gmail_release))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);




        spinner = (Spinner) v.findViewById(R.id.spinner);


        backSignUp = (ImageView) v.findViewById(R.id.back_icon);
        countryCode = (TextView) v.findViewById(R.id.country_code);
        phone = (MaskedEditText) v.findViewById(R.id.country_phone);
        verificationDiv = v.findViewById(R.id.verification_screen);
        mainSignUp = v.findViewById(R.id.main_sign_up);
        btn_signup = v.findViewById(R.id.btn_signup);
        btnResend = v.findViewById(R.id.resend_btn);



        backIconVerification = v.findViewById(R.id.back_icon_verification);
        verificationMainScreen = v.findViewById(R.id.verification_main_div);

        confirmationMainScreen = v.findViewById(R.id.confirmation_main_screen);


        editText1 = (EditText) v.findViewById(R.id.edit_text1);
        editText2 = (EditText) v.findViewById(R.id.edit_text2);
        editText3 = (EditText) v.findViewById(R.id.edit_text3);
        editText4 = (EditText) v.findViewById(R.id.edit_text4);

        eFirst = (EditText) v.findViewById(R.id.ed_fname);
        eLast = (EditText) v.findViewById(R.id.ed_lname);
        eEmail = (EditText) v.findViewById(R.id.ed_email);
        ePassword = (EditText) v.findViewById(R.id.ed_password);

       // fbDiv = v.findViewById(R.id.fb_div);
        //fb_btn = (TextView) v.findViewById(R.id.btn_fb);


       /* fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FB_LOGIN();
                LoginManager.getInstance().logOut();
                login_button_fb.performClick();


            }
        });
        fbDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FB_LOGIN();
                LoginManager.getInstance().logOut();
                login_button_fb.performClick();

            }
        });*/

        google_sign_in_div = v.findViewById(R.id.google_sign_in_div);
        google_sign_in_div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
                if (acct != null) {

                    String Fname = acct.getGivenName();
                    String Lname = acct.getFamilyName();
                    String Email = acct.getEmail();
                    String ID = acct.getId();

                    eFirst.setText(Fname);
                    eLast.setText(Lname);
                    eEmail.setText(Email);



                }
                else {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 123);
                }
            }
        });




        mainSignUpDiv = v.findViewById(R.id.main_sign_up_div);



        editText1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView text = (TextView) getActivity().getCurrentFocus();

                if (editText1.getText().toString().length() == 1)     //size as per your requirement
                {
                    editText2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        editText2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                TextView text = (TextView) getActivity().getCurrentFocus();

                if (editText2.getText().toString().length() == 1)     //size as per your requirement
                {
                    editText3.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        editText3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView text = (TextView) getActivity().getCurrentFocus();

                if (editText3.getText().toString().length() == 1)     //size as per your requirement
                {
                    editText4.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });


        btn_done = v.findViewById(R.id.btn_done);
        confirmDiv = v.findViewById(R.id.confirm_screen);
        backIconConfirm = v.findViewById(R.id.back_icon_confirm);

        backIconConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                verificationMainScreen.setVisibility(View.VISIBLE);
                confirmDiv.setVisibility(View.GONE);

            }
        });


        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Verify();

            }
        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                if (countryCode.getText().toString().trim().equals("")) {

                    Toast.makeText(getContext(), R.string.entry_country, Toast.LENGTH_SHORT).show();

                } else if (phone.getText(true).toString().trim().equals("")) {

                    Toast.makeText(getContext(), R.string.enter_contact_number, Toast.LENGTH_SHORT).show();

                } else {

                    Verify();
                }

            }
        });



        backIconVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificationDiv.setVisibility(View.GONE);
                mainSignUp.setVisibility(View.VISIBLE);
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                catch (Exception e) {
                }


                boolean valid = Functions.isValidEmail(eEmail.getText().toString());


                if (eFirst.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), R.string.enter_first_name, Toast.LENGTH_SHORT).show();
                }

                else if (eLast.getText().toString().trim().equals("")) {

                    Toast.makeText(getContext(), R.string.enter_last_name, Toast.LENGTH_SHORT).show();

                }

                else if (eEmail.getText().toString().trim().equals("")) {

                    Toast.makeText(getContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();

                }

                else if (ePassword.getText().toString().trim().equals("")) {

                    Toast.makeText(getContext(), getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
                }

                else if (ePassword.getText().toString().length() < 6) {

                    Toast.makeText(getContext(), getString(R.string.enter_password_atleast_characters), Toast.LENGTH_SHORT).show();
                }

                else if (!valid) {

                    Toast.makeText(getContext(), getString(R.string.enter_valid_email),Toast.LENGTH_SHORT).show();
                }

                else {

                    verificationDiv.setVisibility(View.VISIBLE);
                    mainSignUp.setVisibility(View.GONE);

                    Get_Coutry_list();

                }


            }

        });

        backSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
                LoginManager.getInstance().logOut();
            }
        });



        confirmBtn = v.findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText1.getText().toString().trim().equals("") || editText2.getText().toString().trim().equals("") || editText3.getText().toString().trim().equals("") || editText4.getText().toString().trim().equals("")) {

                    Toast.makeText(getContext(), "Enter Code!", Toast.LENGTH_SHORT).show();

                } else {

                    verify2();
                }

            }
        });


        return v;
    }





    public void FB_LOGIN() {
        callbackManager = CallbackManager.Factory.create();

        //login_button_fb = (LoginButton) v.findViewById(R.id.login_button_fb);
        login_button_fb.setReadPermissions(Arrays.asList("email"));

        // If using in a fragment
        login_button_fb.setFragment(this);

        login_button_fb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        String useremail = user.optString("email");

                        String FName = user.optString("first_name");
                        String LName = user.optString("last_name");
                        String ID = user.optString("id");

                        if(FName!=null&& LName!=null) {

                            eFirst.setText(FName);
                            eLast.setText(LName);
                            eEmail.setText(useremail);



                        }
                        else {
                            Toast.makeText(getContext(),"Could not Login because Your ID does not Contain: First Or Last Name",Toast.LENGTH_LONG).show();
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "last_name,first_name,email");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getContext(), "Cancle", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }



    String [] countryname_list;
    String [] countrycode_list;
    private void Get_Coutry_list() {
        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, false);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);


        JSONObject jsonObject = new JSONObject();

        ApiRequest.callApi(context, Config.showCountries, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        JSONArray countries=jsonResponse.optJSONArray("Country");

                        countryname_list=new String[countries.length()];
                        countrycode_list=new String[countries.length()];

                        for(int i=0;i<countries.length();i++){
                            JSONObject jsonObject1=countries.optJSONObject(i);
                            JSONObject country=jsonObject1.optJSONObject("Country");

                            countryname_list[i]=country.optString("country");
                            countrycode_list[i]=country.optString("country_code");
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_spinner_item, countryname_list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(SingUpActivity.this);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);
            }
        });

    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        countryCode.setText(countrycode_list[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    private void Verify() {
        //Getting values from edit texts

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, false);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        String phne = phone.getText(true).toString().replaceAll("[^0-9]", "");


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("verify", "0");
            jsonObject.put("phone_no", countryCode.getText().toString() + phne);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSONPost",jsonObject.toString());

        ApiRequest.callApi(context, Config.Verify_URL, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                 try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        verificationMainScreen.setVisibility(View.GONE);
                        confirmDiv.setVisibility(View.VISIBLE);

                    }else {

                        Toast.makeText(context, ""+jsonResponse.optString("msg"), Toast.LENGTH_SHORT).show();
                    }


                 } catch (JSONException e) {
                    e.printStackTrace();
                }


                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

            }
        });


    }

    private void verify2() {

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        String phne = phone.getText(true).toString().replaceAll("[^0-9]", "");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("verify", "1");
            jsonObject.put("phone_no", countryCode.getText().toString() + phne);
           jsonObject.put("code", editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.callApi(context, Config.Verify_URL, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {
                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        signUp(eEmail.getText().toString(), ePassword.getText().toString(), eFirst.getText().toString(), eLast.getText().toString());

                    }else {

                        Toast.makeText(context, ""+jsonResponse.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void signUp(final String email, final String pass, String f_name, String l_name) {
        String device_tocken =preferences.getString(PreferenceClass.device_token,"");

        String phne = phone.getText(true).toString().replaceAll("[^0-9]", "");

        String url = Config.SignUp_URL;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", pass);
            jsonObject.put("device_token", device_tocken);
            jsonObject.put("first_name", f_name);
            jsonObject.put("last_name", l_name);
            jsonObject.put("phone", countryCode.getText().toString() + phne);
            jsonObject.put("role", "user");


        } catch (JSONException e) {
            e.printStackTrace();
        }


        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, false);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        ApiRequest.callApi(context, url, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {
                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {

                    JSONObject jsonResponse = new JSONObject(resp);
                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        login(email,pass);


                    }else {

                        Toast.makeText(context, ""+jsonResponse.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
                    transparent_layer.setVisibility(View.GONE);
                    progressDialog.setVisibility(View.GONE);
                }

            }
        });

    }


    private void login(final String email, final String pass){

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);


        String lat = preferences.getString(PreferenceClass.LATITUDE,"");
        String lng = preferences.getString(PreferenceClass.LONGITUDE,"");
        String device_tocken =preferences.getString(PreferenceClass.device_token,"");


        String url = Config.LOGIN_URL;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", pass);
            jsonObject.put("device_token", device_tocken);
            jsonObject.put("role", "user");

            if(lat.isEmpty()){
                jsonObject.put("lat", "31.5042483");
            }else {
                jsonObject.put("lat", lat);
            }
            if(lng.isEmpty()){
                jsonObject.put("long", "74.3307944");
            }else {
                jsonObject.put("long", lng);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(context, url, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {


                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONObject resultObj = json.getJSONObject("msg");
                        JSONObject json1 = new JSONObject(resultObj.toString());
                        JSONObject resultObj1 = json1.getJSONObject("UserInfo");
                        JSONObject resultObj2 = json1.getJSONObject("User");

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(PreferenceClass.pre_email, email);
                        editor.putString(PreferenceClass.pre_pass, pass);
                        editor.putString(PreferenceClass.pre_first, resultObj1.optString("first_name"));
                        editor.putString(PreferenceClass.pre_last, resultObj1.optString("last_name"));
                        editor.putString(PreferenceClass.pre_contact, resultObj1.optString("phone"));
                        editor.putString(PreferenceClass.pre_user_id, resultObj1.optString("user_id"));

                          editor.putBoolean(PreferenceClass.IS_LOGIN, true);
                          editor.commit();



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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(callbackManager!=null)
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("handleSignInResult", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();



            String Fname = acct.getGivenName();
            String Lname = acct.getFamilyName();
            String Email = acct.getEmail();

            eFirst.setText(Fname);
            eLast.setText(Lname);
            eEmail.setText(Email);

        }
    }


}
