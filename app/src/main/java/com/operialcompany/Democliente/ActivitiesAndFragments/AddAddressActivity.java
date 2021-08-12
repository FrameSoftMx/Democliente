package com.operialcompany.Democliente.ActivitiesAndFragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.R;


public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout addAddressDiv, addAddressLayout, selectAddressLayout;
    Button cancleAddressBtn, cancleAddAddressBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        initUI();

    }

    private void initUI(){
        addAddressDiv = findViewById(R.id.add_address_div);
        selectAddressLayout = findViewById(R.id.select_add_layout);
        addAddressLayout = findViewById(R.id.add_address_layout);
        cancleAddAddressBtn = findViewById(R.id.cancle_add_address_btn);
        addAddressDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectAddressLayout.setVisibility(View.GONE);
                addAddressLayout.setVisibility(View.VISIBLE);

            }
        });


        cancleAddAddressBtn.setOnClickListener(this::onClick);



        cancleAddressBtn = findViewById(R.id.cancle_address_btn);
        cancleAddressBtn.setOnClickListener(this::onClick);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.cancle_add_address_btn:
                selectAddressLayout.setVisibility(View.VISIBLE);
                addAddressLayout.setVisibility(View.GONE);
                break;

            case R.id.cancle_address_btn:
                finish();
                break;
        }
    }
}
