package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.operialcompany.Democliente.Constants.Config;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by qboxus on 10/18/2019.
 */

public class AddPaymentDetailFragment extends RootFragment {

    private SharedPreferences sharedPreferences;
    String month,year;
    ImageView backIcon;
    Button cancleCreditCardBtn, savePaymentMethodBtn;
    private Calendar myCalendar;
    private EditText cardNumberEdittext, cardValidity, nameOnCard,cvv;
    CamomileSpinner pbHeaderProgress;
    RelativeLayout transparent_layer,progressDialog;

    View view;
    Context context;


    public  AddPaymentDetailFragment(){

    }

    FragmentCallback fragment_callback;
    public  AddPaymentDetailFragment(FragmentCallback fragment_callback){
        this.fragment_callback=fragment_callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          view = inflater.inflate(R.layout.add_credit_card_detail, container, false);
         context=getContext();

         init(view);

        sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);


        return view;
    }

    public void init(View v){
        myCalendar = Calendar.getInstance();
        backIcon = v.findViewById(R.id.back_icon);
        pbHeaderProgress = v.findViewById(R.id.pbHeaderProgress);
        pbHeaderProgress.start();
        progressDialog = v.findViewById(R.id.progressDialog);
        transparent_layer = v.findViewById(R.id.transparent_layer);

        cancleCreditCardBtn = v.findViewById(R.id.cancle_credit_card_btn);
        cardNumberEdittext = v.findViewById(R.id.card_number_editText);
        cardValidity = v.findViewById(R.id.card_validity);
        nameOnCard = v.findViewById(R.id.name_on_card);
        cvv = v.findViewById(R.id.cvv);

        savePaymentMethodBtn = v.findViewById(R.id.save_payment_method_btn);

        savePaymentMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCard(view);
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                pbHeaderProgress.setVisibility(View.VISIBLE);
            }
        });

        if (AddPaymentFragment.FLAG_FRAGMENT){
            backIcon.setVisibility(View.VISIBLE);
            cancleCreditCardBtn.setVisibility(View.GONE);
            AddPaymentFragment.FLAG_FRAGMENT = false;
        }

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               getActivity().onBackPressed();
            }
        });

        cardNumberEdittext.addTextChangedListener(new PaymentMethodActivity.FourDigitCardFormatWatcher());

        datePickerDialog();

    }

    private void datePickerDialog(){


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
               myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        cardValidity.setInputType(0);
        cardValidity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        cardValidity.setText(sdf.format(myCalendar.getTime()));
    }


    public void submitCard(View view) {
        try{

        if(cardNumberEdittext.getText().toString().isEmpty()){

            cardNumberEdittext.setError(getString(R.string.not_be_empty));
        }
        else if(nameOnCard.getText().toString().isEmpty()){
            nameOnCard.setError(getString(R.string.not_be_empty));
        }
        else if(cvv.getText().toString().isEmpty()){
            cvv.setError(getString(R.string.not_be_empty));
        }
        else if(cardValidity.getText().toString().isEmpty()){
            Toast.makeText(getContext(), R.string.select_the_card_validity, Toast.LENGTH_SHORT).show();
        }
        else {


            String monthField = cardValidity.getText().toString();
            if (!monthField.isEmpty()) {
                month = monthField.substring(0, 2);
            }
            String yearField = cardValidity.getText().toString();
            if (!yearField.isEmpty()) {
                year = yearField.substring(3, 5);
            }

            addPaymentMethodVollyCal();
        }

    }catch (Exception e){
            Toast.makeText(context, getString(R.string.wrong_data_enter_please_try_again), Toast.LENGTH_SHORT).show();
        }

    }


    public void addPaymentMethodVollyCal(){

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        String user_id = sharedPreferences.getString(PreferenceClass.pre_user_id,"");
        String card_number = cardNumberEdittext.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", user_id);
            jsonObject.put("name", nameOnCard.getText().toString());
            jsonObject.put("card",card_number.replace(" ",""));
            jsonObject.put("cvc",cvv.getText().toString());
            jsonObject.put("exp_month",month);
            jsonObject.put("exp_year",year);
            jsonObject.put("default","1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(context, Config.ADD_PAYMENT_METHOD, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {


                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);


                try {
                    JSONObject jsonResponse = new JSONObject(resp);
                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        if(fragment_callback!=null){
                            fragment_callback.onResponce(new Bundle());
                        }
                        getActivity().onBackPressed();

                    }else {

                        Toast.makeText(getContext(), ""+jsonResponse.optString("msg"), Toast.LENGTH_SHORT).show();

                    }

                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });



    }


}
