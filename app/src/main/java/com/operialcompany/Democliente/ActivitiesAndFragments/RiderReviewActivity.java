package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by qboxus on 10/18/2019.
 */

public class RiderReviewActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sPref;
    TextView restName;
    String riderUserId, orderId, riderName, userId;
    RatingBar reviewRatingBar;
    EditText edMessage;
    RelativeLayout submitBtn;
    float rating;
    ImageView closMenuItemsDetail;
    SimpleDraweeView restImg;
    CamomileSpinner progress;
    RelativeLayout transparentLayer,progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_review_alert);
        initUI();
    }

    public void initUI(){
        sPref = getSharedPreferences(PreferenceClass.user,MODE_PRIVATE);
        submitBtn = findViewById(R.id.submitBtn);
        edMessage = findViewById(R.id.ed_message);
        reviewRatingBar = findViewById(R.id.reviewRatingBar);
        closMenuItemsDetail = findViewById(R.id.clos_menu_items_detail);
        restImg = findViewById(R.id.rest_img);

        progressDialog = findViewById(R.id.progressDialog);
        transparentLayer = findViewById(R.id.transparent_layer);

        progress = findViewById(R.id.addToCartProgress);
        progress.start();

        userId = sPref.getString(PreferenceClass.pre_user_id,"");

        closMenuItemsDetail.setOnClickListener(this::onClick);


            riderName = sPref.getString(PreferenceClass.RIDER_NAME_NOTIFY,"");
            riderUserId = sPref.getString(PreferenceClass.RIDER_USER_ID_NOTIFY,"");
            orderId = sPref.getString(PreferenceClass.ORDER_ID_NOTIFY,"");

            restName = findViewById(R.id.rest_name);
            restName.setText(riderName);


        reviewRatingBar.setOnClickListener(this::onClick);

        submitBtn.setOnClickListener(this::onClick);


    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.reviewRatingBar:
                rating = reviewRatingBar.getRating();
                break;

            case R.id.clos_menu_items_detail:
                RiderReviewActivity.this.finish();
                SharedPreferences.Editor editor = sPref.edit();
                editor.putBoolean("isOpen",true).commit();
                break;

            case R.id.submitBtn:
                if(reviewRatingBar.getRating()>0){
                    postReview();
                }
                else {
                    Toast.makeText(RiderReviewActivity.this,"Please select at-least ONE star.",Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void postReview(){

        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();


        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("rider_user_id",""+ riderUserId);
            jsonObject.put("comment", edMessage.getText().toString());
            jsonObject.put("order_id",""+ orderId);
            jsonObject.put("star",""+reviewRatingBar.getRating());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.callApi(this, Config.GiveRatingsToRider, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {


                try {
                    JSONObject jsonObject1 = new JSONObject(resp);
                    int code = Integer.parseInt(jsonObject1.optString("code"));
                    if(code==200){

                        Toast.makeText(RiderReviewActivity.this, R.string.thanks_for_review,Toast.LENGTH_SHORT).show();
                        RiderReviewActivity.this.finish();
                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putBoolean("isOpen",true).commit();
                        transparentLayer.setVisibility(View.GONE);
                        progressDialog.setVisibility(View.GONE);
                    }
                    else {

                        // Else Part
                        transparentLayer.setVisibility(View.GONE);
                        progressDialog.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    transparentLayer.setVisibility(View.GONE);
                    progressDialog.setVisibility(View.GONE);
                }


            }
        });

    }

}
