package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.operialcompany.Democliente.Constants.AllConstants;
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

public class RestReveiwActivity extends AppCompatActivity {
    SharedPreferences sPref;
    TextView restName;
    String restaurantId, restaurantName,imageUrl, orderId, userId;
    RatingBar reviewRatingBar;
    EditText edMessage;
    RelativeLayout submitBtn;
    float rating;
    ImageView closMenuItemsDetail;
    SimpleDraweeView rest_img;
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
        rest_img = findViewById(R.id.rest_img);

        progressDialog = findViewById(R.id.progressDialog);
        transparentLayer = findViewById(R.id.transparent_layer);

        progress = findViewById(R.id.addToCartProgress);
        progress.start();

        userId = sPref.getString(PreferenceClass.pre_user_id,"");
        String type = sPref.getString(PreferenceClass.REVIEW_TYPE,"");

        Log.d(AllConstants.tag,type);

        closMenuItemsDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestReveiwActivity.this.finish();
                SharedPreferences.Editor editor = sPref.edit();
                editor.putBoolean("isOpen",true).commit();
            }
        });

        if(type.equalsIgnoreCase("order_review")){
            restaurantName = sPref.getString(PreferenceClass.RESTAURANT_NAME_NOTIFY,"");
            restaurantId = sPref.getString(PreferenceClass.RESTAURANT_ID_NOTIFY,"");
            imageUrl = sPref.getString(PreferenceClass.REVIEW_IMG_PIC,"");
            orderId =sPref.getString(PreferenceClass.ORDER_ID_NOTIFY,"");

            restName = findViewById(R.id.rest_name);
            restName.setText(restaurantName);


            Uri uri = Uri.parse(Config.imgBaseURL+imageUrl);
            rest_img.setImageURI(uri);


            Log.d(AllConstants.tag, restaurantName);
            Log.d(AllConstants.tag,Config.imgBaseURL+imageUrl);
        }

        reviewRatingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = reviewRatingBar.getRating();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(reviewRatingBar.getRating()>0){
                    postReview();

                }
                else {
                    Toast.makeText(RestReveiwActivity.this, R.string.please_select_your_rating_star,Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void postReview(){

        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id",""+ userId);
                jsonObject.put("restaurant_id",""+ restaurantId);
                jsonObject.put("comment",""+ edMessage.getText().toString());
                jsonObject.put("order_id",""+ orderId);
                jsonObject.put("star",""+reviewRatingBar.getRating());
            } catch (JSONException e) {
                e.printStackTrace();
            }



        ApiRequest.callApi(this, Config.AddRestaurantRating, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject1 = new JSONObject(resp);
                    int code = Integer.parseInt(jsonObject1.optString("code"));
                    if(code==200){

                        Toast.makeText(RestReveiwActivity.this,"Thanks for review",Toast.LENGTH_SHORT).show();
                        RestReveiwActivity.this.finish();
                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putBoolean("isOpen",true).commit();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }



}
