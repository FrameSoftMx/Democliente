package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by qboxus on 10/18/2019.
 */

public class PaymentMethodActivity extends AppCompatActivity {

    private Button canclePaymentMethodBtn, cancleCreditCardBtn;
    private RelativeLayout addPaymentMethodDiv;

    private LinearLayout selectPaymentMethodLayout, addCardDetailLayout;

    private EditText cardNumberEdittext, cardValidity;

    private Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        myCalendar = Calendar.getInstance();
        initUI();
    }

    private void initUI(){

        canclePaymentMethodBtn = findViewById(R.id.cancle_payment_method_btn);
        canclePaymentMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cancleCreditCardBtn = findViewById(R.id.cancle_credit_card_btn);
        addCardDetailLayout = findViewById(R.id.add_card_detail_layout);
        selectPaymentMethodLayout = findViewById(R.id.select_payment_method_layout);
        addPaymentMethodDiv = findViewById(R.id.add_payment_method_div);
        cardNumberEdittext = findViewById(R.id.card_number_editText);
        cardValidity = findViewById(R.id.card_validity);
        datePickerDialog();

        addPaymentMethodDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectPaymentMethodLayout.setVisibility(View.GONE);
                addCardDetailLayout.setVisibility(View.VISIBLE);

            }
        });

        cancleCreditCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPaymentMethodLayout.setVisibility(View.VISIBLE);
                addCardDetailLayout.setVisibility(View.GONE);
            }
        });

        cardNumberEdittext.addTextChangedListener(new FourDigitCardFormatWatcher());



    }



    public static class FourDigitCardFormatWatcher implements TextWatcher {


        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);

                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
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
                new DatePickerDialog(PaymentMethodActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        cardValidity.setText(sdf.format(myCalendar.getTime()));
    }



}
