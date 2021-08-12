package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.AddressListModel;
import com.operialcompany.Democliente.Models.DealsModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.FontHelper;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by qboxus on 10/18/2019.
 */

public class DealOrderFragment extends RootFragment implements View.OnClickListener {

    SharedPreferences dealOrderPref;

    ImageView backIcon;
    String dealName, dealsTax, dealPrice, dealCurrency, deliveryFee, deliveryAddressStreet, deliveryAddressState, deliveryAddressCity,
            apartment, cardNumber, cardBrand, dealDesc, userId, paymentId, addressId, restName,riderTip, resId,formattedDate, dealId;
    TextView restNameTv, dealDescTv, dealPriceTv, subTotalPriceTv, taxTv, totalDeliveryFeeTv, deliveryAddressTv,
            creditCardNumberTv, totalTexTv, dealNameTv, totalSumTv,rider_tip, riderTipPriceTv, declineTv,accept_tv;
    RelativeLayout dealPaymentMethodDiv, dealAddressDiv, cartCheckOutDiv, tipDiv, acceptDiv, declineDiv;
    int dealQuantity;
    double getTax, getFinalPrice,grandTotal;
    boolean getLoINSession, pickUp;

    Double previousRiderTip = 0.0;
    public static boolean DEAL_ADDRESS,DEAL_PAYMENT_METHOD;

    CamomileSpinner pbHeaderProgress;

    RelativeLayout transparentLayer,progressDialog;
    View view;
    Context context;


    DealsModel dealsModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.deals_report_layout, container, false);
        context=getContext();

        Bundle bundle=getArguments();
        if(bundle!=null){
            dealsModel=(DealsModel) bundle.getSerializable("data");
        }

        dealOrderPref = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        getLoINSession = dealOrderPref.getBoolean(PreferenceClass.IS_LOGIN,false);
        grandTotal = 0.0;
        riderTip = "0";
        FrameLayout frameLayout = view.findViewById(R.id.deal_order_main_container);
        FontHelper.applyFont(getContext(), frameLayout, AllConstants.verdana);

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        initUI(view);
        return view;

    }

    public void initUI(View v){
        declineDiv = v.findViewById(R.id.pickup_div);
        acceptDiv = v.findViewById(R.id.delivery_div);
        declineTv = v.findViewById(R.id.decline_tv);
        accept_tv = v.findViewById(R.id.accept_tv);
        riderTipPriceTv = v.findViewById(R.id.rider_tip_price_tv);
        rider_tip = v.findViewById(R.id.rider_tip);

        dealId =dealsModel.deal_id;
        dealName = dealsModel.deal_name;
        dealDesc =dealsModel.deal_desc;

        dealPrice =dealsModel.deal_price;
        dealCurrency =dealsModel.deal_symbol;
        if(dealsModel.isDeliveryFree.equalsIgnoreCase("1")) {
            dealsTax = "0";
        }
        else {
            dealsTax =dealsModel.deal_tax;
        }


        resId =dealsModel.deal_restaurant_id;
        restName =dealsModel.restaurant_name;


        deliveryAddressStreet = dealOrderPref.getString(PreferenceClass.STREET,"");
        deliveryAddressState = dealOrderPref.getString(PreferenceClass.STATE,"");
        deliveryAddressCity = dealOrderPref.getString(PreferenceClass.CITY,"");
        apartment = dealOrderPref.getString(PreferenceClass.APARTMENT,"");
        userId = dealOrderPref.getString(PreferenceClass.pre_user_id,"");
        paymentId = dealOrderPref.getString(PreferenceClass.PAYMENT_ID,"");
        addressId = dealOrderPref.getString(PreferenceClass.ADDRESS_ID,"");


        cardNumber = dealOrderPref.getString(PreferenceClass.CREDIT_CARD_ARRAY,"");
        cardBrand = dealOrderPref.getString(PreferenceClass.CREDIT_CARD_BRAND,"");



        dealQuantity = dealOrderPref.getInt(PreferenceClass.DEALS_QUANTITY,1);

        getFinalPrice = Double.parseDouble(dealPrice)*Double.parseDouble(String.valueOf(dealQuantity));


        progressDialog = v.findViewById(R.id.progressDialog);
        transparentLayer = v.findViewById(R.id.transparent_layer);
        pbHeaderProgress = v.findViewById(R.id.dealOrderProgress);
        pbHeaderProgress.start();
        creditCardNumberTv = v.findViewById(R.id.credit_card_number_tv);
        dealPaymentMethodDiv = v.findViewById(R.id.deal_payment_method_div);
        dealAddressDiv = v.findViewById(R.id.deal_address_div);
        deliveryAddressTv = v.findViewById(R.id.delivery_address_tv);
        restNameTv = v.findViewById(R.id.rest_name_tv);
        restNameTv.setText(restName);
        dealDescTv = v.findViewById(R.id.deal_desc_tv);
        dealDescTv.setText(dealDesc);
        subTotalPriceTv = v.findViewById(R.id.sub_total_price_tv);
        subTotalPriceTv.setText(dealCurrency +""+getFinalPrice);

        creditCardNumberTv.setText("Select Payment Method");



        dealPriceTv = v.findViewById(R.id.deal_price_tv);
        dealPriceTv.setText(dealCurrency + dealPrice);

        taxTv = v.findViewById(R.id.tax_tv);
        taxTv.setText("("+ dealsTax +"%)");
        totalTexTv = v.findViewById(R.id.total_tex_tv);
        getTax = getFinalPrice*Double.parseDouble(dealsTax)/100;
        totalTexTv.setText(dealCurrency +getTax);

        dealNameTv = v.findViewById(R.id.deal_name_tv);
        dealNameTv.setText(dealName + " (x"+ dealQuantity +")");

        totalDeliveryFeeTv = v.findViewById(R.id.total_delivery_fee_tv);


        deliveryAddressTv.setText("Select Delivery Address");
        totalDeliveryFeeTv.setText("0");
        deliveryFee = "0";


        totalSumTv = v.findViewById(R.id.total_sum_tv);
        grandTotal = Double.parseDouble(deliveryFee)+getFinalPrice+getTax;
        totalSumTv.setText(dealCurrency +new DecimalFormat("##.##").format(grandTotal));

        tipDiv = v.findViewById(R.id.tip_div);
        tipDiv.setOnClickListener(this);

        backIcon = v.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(this);

        dealAddressDiv.setOnClickListener(this);
        dealPaymentMethodDiv.setOnClickListener(this);
        cartCheckOutDiv = v.findViewById(R.id.cart_check_out_div);
        cartCheckOutDiv.setOnClickListener(this);


        pickUpOrDelivery();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_icon:
                getActivity().onBackPressed();
                break;

            case R.id.tip_div:
                addRiderTip();
                break;

            case R.id.deal_address_div:
                if(!getLoINSession){

                    TabLayout.Tab tab =PagerMainActivity.tabLayout.getTabAt(3);
                    tab.select();

                }
                else {
                    Fragment restaurantMenuItemsFragment = new AddressListFragment(new FragmentCallback() {
                        @Override
                        public void onResponce(Bundle bundle) {
                            if(bundle!=null){
                                AddressListModel addressListModel=(AddressListModel)bundle.getSerializable("data");

                                deliveryAddressStreet =addressListModel.getStreet();
                                deliveryAddressCity =addressListModel.getCity();
                                deliveryAddressState =addressListModel.getState();
                                apartment =addressListModel.getApartment();
                                addressId =addressListModel.getAddress_id();
                                creditCardNumberTv.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));

                                deliveryAddressTv.setText(deliveryAddressStreet + " " + deliveryAddressCity + " " + deliveryAddressState);
                                deliveryAddressTv.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));

                            }
                        }
                    });

                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    Bundle bundle=new Bundle();
                    bundle.putString("grand_total",String.valueOf(grandTotal));
                    bundle.putString("rest_id", resId);
                    transaction.addToBackStack(null);
                    transaction.add(R.id.deal_order_main_container, restaurantMenuItemsFragment, "parent").commit();

                }
                break;


            case R.id.deal_payment_method_div:
                if(!getLoINSession){

                    TabLayout.Tab tab =PagerMainActivity.tabLayout.getTabAt(3);
                    tab.select();

                }
                else {
                    Fragment restaurantMenuItemsFragment = new AddPaymentFragment(new FragmentCallback() {
                        @Override
                        public void onResponce(Bundle bundle) {
                            if(bundle!=null){

                                paymentId =bundle.getString("card_id");
                                cardNumber =bundle.getString("card_number");
                                creditCardNumberTv.setText(cardNumber);
                                creditCardNumberTv.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
                            }
                        }
                    });
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.add(R.id.deal_order_main_container, restaurantMenuItemsFragment, "parent").commit();
                }
                break;


            case R.id.cart_check_out_div:
                if(deliveryAddressTv.getText().toString().equalsIgnoreCase("Select Delivery Address")
                        || creditCardNumberTv.getText().toString().equalsIgnoreCase("Select Payment Method")
                )
                {
                    Toast.makeText(getContext(), context.getResources().getString(R.string.delivery_address_or_payment_method_is_missing),Toast.LENGTH_LONG).show();
                }else {
                    dealOrder();
                }
                break;



        }
    }




    public void addRiderTip(){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog_cart);

        final EditText ed_text = dialog.findViewById(R.id.ed_text);
        ed_text.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Add Rider Tip");
        ed_text.setHint("Enter Tip Here");

        Button cancelDiv = (Button) dialog.findViewById(R.id.cancel_btn);
        Button done_btn =  (Button) dialog.findViewById(R.id.done_btn);

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                riderTip = ed_text.getText().toString();
                pickUp = false;
                getTotalSumTip(riderTip, pickUp);
                riderTipPriceTv.setText(dealCurrency +riderTip);
                rider_tip.setText(dealCurrency +riderTip);
                dialog.dismiss();
            }
        });


        cancelDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void getTotalSumTip(String riderTip,boolean rider_tip_pick_up){
        if(rider_tip_pick_up){
            grandTotal = Double.parseDouble(String.valueOf(grandTotal-Double.parseDouble(riderTip)));
        }
        else {

            grandTotal = Double.parseDouble(String.valueOf(grandTotal + Double.parseDouble(riderTip)));
            grandTotal = grandTotal-previousRiderTip;
            previousRiderTip = Double.parseDouble(riderTip);

        }
        totalSumTv.setText(dealCurrency +new DecimalFormat("##.##").format(grandTotal));

    }

    public void pickUpOrDelivery(){
        declineDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declineDiv.setBackground(ContextCompat.getDrawable(context,R.drawable.round_shape_btn_login));
                acceptDiv.setBackground(ContextCompat.getDrawable(context,R.drawable.round_shape_btn_grey));
                declineTv.setTextColor(ContextCompat.getColor(context,R.color.colorWhite));
                accept_tv.setTextColor(ContextCompat.getColor(context,R.color.color_light_black));
                riderTipPriceTv.setText(dealCurrency +"0");
                totalDeliveryFeeTv.setText(dealCurrency +"0");
                rider_tip.setText(dealCurrency +"0");
                deliveryAddressTv.setText("Pick Up");
                pickUp = true;
                getTotalSumDeliveryFee(deliveryFee, pickUp);
                getTotalSumTip(riderTip, pickUp);
                dealAddressDiv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });

                tipDiv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });

            }
        });

        acceptDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declineDiv.setBackground(ContextCompat.getDrawable(context,R.drawable.round_shape_btn_grey));
                acceptDiv.setBackground(ContextCompat.getDrawable(context,R.drawable.round_shape_btn_login));
                declineTv.setTextColor(ContextCompat.getColor(context,R.color.color_light_black));
                accept_tv.setTextColor(ContextCompat.getColor(context,R.color.colorWhite));

                riderTipPriceTv.setText(dealCurrency +riderTip);
                totalDeliveryFeeTv.setText(dealCurrency + deliveryFee);
                rider_tip.setText(dealCurrency +riderTip);
                if(deliveryAddressStreet.isEmpty()&&apartment.isEmpty()&& deliveryAddressCity.isEmpty()&& deliveryAddressState.isEmpty()){
                    deliveryAddressTv.setText("Select Delivery Address");
                }
                else {
                    deliveryAddressTv.setText(deliveryAddressStreet + " " + apartment + " " + deliveryAddressCity + " " + deliveryAddressState);
                }
                pickUp = false;

                previousRiderTip = 0.0;
                getTotalSumDeliveryFee(deliveryFee, pickUp);
                getTotalSumTip(riderTip, pickUp);

                dealAddressDiv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });

                tipDiv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
        });
    }


    public void getTotalSumDeliveryFee(String deliveryFee,boolean picu_up){

        if(picu_up){
            grandTotal = Double.parseDouble(String.valueOf(grandTotal-Double.parseDouble(deliveryFee)));
        }
        else {
            grandTotal = Double.parseDouble(String.valueOf(grandTotal + Double.parseDouble(deliveryFee)));
        }
        totalSumTv.setText(dealCurrency +new DecimalFormat("##.##").format(grandTotal));

    }


    public void dealOrder() {

        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", dealName);
            jsonObject.put("description", dealDesc);
            jsonObject.put("price", grandTotal);
            if (AddPaymentFragment.FLAG_PAYMENT_METHOD) {
                jsonObject.put("cod", "1");
                jsonObject.put("payment_id", "0");
                AddPaymentFragment.FLAG_PAYMENT_METHOD = false;
            } else {
                jsonObject.put("cod", "0");
                jsonObject.put("payment_id", paymentId);
            }

            jsonObject.put("order_time", formattedDate);
            jsonObject.put("user_id", userId);
            jsonObject.put("quantity",String.valueOf(dealQuantity));
            jsonObject.put("tax",getTax);
            jsonObject.put("sub_total",getFinalPrice);
            jsonObject.put("delivery_fee", deliveryFee);
            jsonObject.put("restaurant_id", resId);
            jsonObject.put("device","android");
            jsonObject.put("deal_id", dealId);
            jsonObject.put("version",SplashScreen.versionCode);
            if(rider_tip.getText().toString().equalsIgnoreCase("Add Rider Tip")){
                jsonObject.put("rider_tip","0");
            }
            else {
                jsonObject.put("rider_tip", riderTip);
            }
            if(deliveryAddressTv.getText().toString().equalsIgnoreCase("Pick Up"))
            {
                jsonObject.put("delivery","0");
                jsonObject.put("address_id", "0");
            }
            else {
                jsonObject.put("delivery","1");
                jsonObject.put("address_id", addressId);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        ApiRequest.callApi(context, Config.ORDER_DEAL, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        SharedPreferences.Editor editor = dealOrderPref.edit();
                        editor.putString(PreferenceClass.ADDRESS_DELIVERY_FEE,"0").commit();
                        CartFragment.ORDER_PLACED = true;
                        getActivity().startActivity(new Intent(getContext(),MainActivity.class));
                        getActivity().finish();

                    }else {
                        Toast.makeText(context, ""+jsonResponse.optString("msg"), Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.getCause();
                }
            }
        });


    }


}