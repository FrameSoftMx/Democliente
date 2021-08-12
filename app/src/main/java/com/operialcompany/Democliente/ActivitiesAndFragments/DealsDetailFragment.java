package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.DealsModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by qboxus on 10/18/2019.
 */

public class DealsDetailFragment extends RootFragment {

    ImageView backIcon, closeIcon;
    SimpleDraweeView dealsBgImg;

    Button increamentBtn, decrementBtn;
    TextView incDecTv;
    int presentCount = 1;
    SharedPreferences dealsDetailPref;
    String dealsName, dealsDesc, dealsPrice, dealsHotelName, dealsImage, dealsSymbol;
    TextView dealsMenuItemTitleTv, dealNameTv, dealAmountTv, dealHotelNameTv, dealDescTv;
    RelativeLayout dealsOrderNowDiv;


    DealsModel dealsModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_deals_main, container, false);


        Bundle bundle=getArguments();
        if(bundle!=null){
            dealsModel=(DealsModel) bundle.getSerializable("data");
        }

        dealsDetailPref = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);

        FrameLayout deals_main_container = view.findViewById(R.id.deals_main_container);
        deals_main_container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        init(view);
        SharedPreferences.Editor editor = dealsDetailPref.edit();
        editor.putInt(PreferenceClass.DEALS_QUANTITY,1).commit();
        return view;

    }


    public void init(View v){
        dealsDesc = dealsModel.deal_desc;
        dealsName =dealsModel.deal_name;
        dealsPrice =dealsModel.deal_price;
        dealsHotelName =dealsModel.restaurant_name;
        dealsImage =dealsModel.deal_image;
        dealsSymbol =dealsModel.deal_symbol;

        dealsMenuItemTitleTv = v.findViewById(R.id.deals_menu_item_title_tv);
        dealNameTv = v.findViewById(R.id.deal_name_tv);
        dealAmountTv = v.findViewById(R.id.deal_amount_tv);
        dealHotelNameTv = v.findViewById(R.id.deal_hotel_name_tv);
        dealDescTv = v.findViewById(R.id.deal_desc_tv);
        dealsBgImg = v.findViewById(R.id.deals_bg_img);

        dealDescTv.setText(dealsDesc);
        dealAmountTv.setText(dealsSymbol +""+ dealsPrice);
        dealHotelNameTv.setText(dealsHotelName);
        dealNameTv.setText(dealsName);
        dealsMenuItemTitleTv.setText(dealsName);


        Uri uri = Uri.parse(Config.imgBaseURL+ dealsImage);
        dealsBgImg.setImageURI(uri);



        backIcon = v.findViewById(R.id.back_icon);
        closeIcon = v.findViewById(R.id.clos_menu_deals_detail);
        increamentBtn = v.findViewById(R.id.plus_btn);
        decrementBtn = v.findViewById(R.id.minus_btn);
        incDecTv = v.findViewById(R.id.inc_dec_tv);
        dealsOrderNowDiv = v.findViewById(R.id.deals_order_now_div);


        increamentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    String presentValStr= incDecTv.getText().toString();
                    presentCount =Integer.parseInt(presentValStr);
                    presentCount++;
                    incDecTv.setText(String.valueOf(presentCount));


                    SharedPreferences.Editor editor = dealsDetailPref.edit();
                    editor.putInt(PreferenceClass.DEALS_QUANTITY, presentCount).commit();

                }

                catch(Exception e)
                {
                    e.printStackTrace();
                 }
            }
        });

        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {

                    String presentValStr= incDecTv.getText().toString();
                    presentCount =Integer.parseInt(presentValStr);
                    if(!presentValStr.equalsIgnoreCase(String.valueOf(Integer.parseInt("1")))) {

                        presentCount--;
                    }
                    incDecTv.setText(String.valueOf(presentCount));

                    SharedPreferences.Editor editor = dealsDetailPref.edit();
                    editor.putInt(PreferenceClass.DEALS_QUANTITY, presentCount).commit();


                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });



        if(DealsFragment.FLAG_DEAL_FRAGMENT){
            backIcon.setVisibility(View.VISIBLE);
            closeIcon.setVisibility(View.GONE);
            DealsFragment.FLAG_DEAL_FRAGMENT = false;
        }
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              getActivity().onBackPressed();

            }
        });

        dealsOrderNowDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DealOrderFragment dealOrderFragment = new DealOrderFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",dealsModel);
                dealOrderFragment.setArguments(bundle);
                transaction.addToBackStack(null);
                transaction.add(R.id.deals_main_container, dealOrderFragment,"parent").commit();
            }
        });

    }


}
