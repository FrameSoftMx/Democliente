package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.operialcompany.Democliente.Adapters.ExpandableListAdapter;
import com.operialcompany.Democliente.Chat_Module.Chat_A;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.GoogleMapWork.OrderTracking_A;
import com.operialcompany.Democliente.Models.MenuItemExtraModel;
import com.operialcompany.Democliente.Models.MenuItemModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.CustomExpandableListView;
import com.operialcompany.Democliente.Utils.FontHelper;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.gmail.samehadar.iosdialog.CamomileSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class OrderDetailFragment extends RootFragment implements View.OnClickListener{

    ImageView backIcon;
    TextView orderTitleTv, instTv, hotelNameTv, hotelPhoneNumberTv, hotelAddTv, totalAmountTv, paymentMethodTv, totalTipTv,
            orderUserNameTv, orderUserAddressTv, orderUserNumberTv, totalDeliveryFeeTv, taxTv, totalTexTv, totalDiscountTv, subTotalAmountTv;
    SharedPreferences orderSharedPreferences;
    ExpandableListAdapter listAdapter;
    CustomExpandableListView customExpandableListView;
    ArrayList<MenuItemModel> listDataHeader;
    ArrayList<MenuItemExtraModel> listChildData;
    private ArrayList<ArrayList<MenuItemExtraModel>> listChild;
    String orderId, userId;

    TextView riderInsTv;

    private RelativeLayout trackOrderDiv;
    private  int pickUp;
    String order_status="";
    ScrollView scrolView;
    CamomileSpinner orderProgress;
    RelativeLayout transparentLayer,progressDialog;
    String delivery;


    View view;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_detail_fragment, container, false);
        context=getContext();

        orderSharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        FrameLayout frameLayout = view.findViewById(R.id.order_detail_container);
        FontHelper.applyFont(getContext(),frameLayout, AllConstants.verdana);

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        pickUp = 0;
        orderId = orderSharedPreferences.getString(PreferenceClass.ORDER_ID,"");
        userId = orderSharedPreferences.getString(PreferenceClass.pre_user_id,"");
        init(view);
        getOrderDetailItems();


        customExpandableListView = (CustomExpandableListView ) view.findViewById(R.id.custon_list_order_items);
        customExpandableListView .setExpanded(true);
        customExpandableListView.setGroupIndicator(null);

        customExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true;
            }
        });

        return view;
    }

    @SuppressLint("ResourceAsColor")
    public void init(View v){

        subTotalAmountTv = v.findViewById(R.id.sub_total_amount_tv);
        orderProgress = v.findViewById(R.id.orderProgress);
        orderProgress.start();
        progressDialog = v.findViewById(R.id.progressDialog);
        transparentLayer = v.findViewById(R.id.transparent_layer);
        String order_title = orderSharedPreferences.getString(PreferenceClass.ORDER_HEADER,"");
        String order_inst = orderSharedPreferences.getString(PreferenceClass.ORDER_INS,"");
        delivery = orderSharedPreferences.getString("delivery_","");
        orderTitleTv = v.findViewById(R.id.order_title_tv);

        trackOrderDiv = v.findViewById(R.id.track_order_div);

        orderTitleTv.setText(order_title.replaceAll("&amp;", "&"));


        scrolView = v.findViewById(R.id.scrolView);
        hotelNameTv = v.findViewById(R.id.order_hotel_name);
        hotelAddTv = v.findViewById(R.id.order_hotel_address);
        hotelPhoneNumberTv = v.findViewById(R.id.order_hotel_number);
        paymentMethodTv = v.findViewById(R.id.payment_method_tv);
        totalAmountTv = v.findViewById(R.id.total_amount_tv);
        instTv = v.findViewById(R.id.inst_tv);
        instTv.setText(order_inst);
        orderUserNameTv = v.findViewById(R.id.order_user_name_tv);
        orderUserAddressTv = v.findViewById(R.id.order_user_address_tv);
        orderUserNumberTv = v.findViewById(R.id.order_user_number_tv);
        totalDeliveryFeeTv = v.findViewById(R.id.total_delivery_fee_tv);
        totalTipTv = v.findViewById(R.id.total_tip_tv);
        taxTv = v.findViewById(R.id.tax_tv);
        totalTexTv = v.findViewById(R.id.total_tex_tv);
        totalDiscountTv =v.findViewById(R.id.total_discount_tv);

        trackOrderDiv.setOnClickListener(this);

        riderInsTv =view.findViewById(R.id.rider_ins_tv);

        backIcon = v.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(this);



        v.findViewById(R.id.cancel_order_div).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back_icon:
                getActivity().onBackPressed();
                break;

            case R.id.track_order_div:
                if(pickUp ==1 || (order_status!=null && order_status.equals("2"))){
                }
                else {
                    Open_tracking();
                }
                break;

            case R.id.cancel_order_div:
                METHOD_openChat();
                break;

        }
    }


    public void Open_tracking(){
        Intent intent=new Intent(getActivity(), OrderTracking_A.class);
        intent.putExtra("order_id", orderId);
        startActivity(intent);
    }


    private void METHOD_openChat() {

        Intent intent=new Intent(getActivity(),Chat_A.class);
        intent.putExtra("Receiverid", "0");
        intent.putExtra("Receiver_name", "Admin");
        intent.putExtra("Receiver_pic", "");
        intent.putExtra("Order_id", orderId);
        intent.putExtra("senderid", PreferenceClass.sharedPreferences.getString(PreferenceClass.pre_first,"")+
                PreferenceClass.sharedPreferences.getString(PreferenceClass.pre_last,""));
        startActivity(intent);
    }

    public void getOrderDetailItems(){

        listDataHeader = new ArrayList<MenuItemModel>();
        listChild = new ArrayList<>();

         JSONObject orderJsonObject = new JSONObject();
        try {
            orderJsonObject.put("order_id", orderId);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        ApiRequest.callApi(context, Config.SHOW_ORDER_DETAIL, orderJsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonArray = json.getJSONArray("msg");

                        for (int i=0;i<jsonArray.length();i++){

                            JSONObject allJsonObject = jsonArray.getJSONObject(i);
                            JSONObject orderJsonObject = allJsonObject.optJSONObject("Order");
                            JSONObject userInfoObj = allJsonObject.optJSONObject("UserInfo");
                            JSONObject userAddressObj = allJsonObject.optJSONObject("Address");
                            JSONObject restaurantJsonObject = allJsonObject.optJSONObject("Restaurant");
                            JSONObject taxObj = restaurantJsonObject.optJSONObject("Tax");
                            JSONObject restaurantCurrencuObj = restaurantJsonObject.getJSONObject("Currency");

                            String currency_symbol= restaurantCurrencuObj.optString("symbol");

                            String first_name = userInfoObj.optString("first_name");
                            String last_name = userInfoObj.optString("last_name");
                            orderUserNameTv.setText(first_name+" "+last_name);
                            orderUserNumberTv.setText(userInfoObj.optString("phone"));
                            String street_user = userAddressObj.optString("street");
                            String city_user = userAddressObj.optString("city");



                            if(delivery.equalsIgnoreCase("0")){
                                orderUserAddressTv.setText("Pick Up");
                            }
                            else {
                                orderUserAddressTv.setText(street_user + ", " + city_user);
                            }

                            order_status=orderJsonObject.optString("status","");
                            if(orderUserAddressTv.getText().toString().equalsIgnoreCase("Pick Up")){
                                trackOrderDiv.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.trackColor));
                                pickUp = 1;
                            }

                            else if(order_status.equals("2")){
                                trackOrderDiv.setVisibility(View.GONE);
                                view.findViewById(R.id.cancel_order_div).setVisibility(View.GONE);

                            }


                            instTv.setText(orderJsonObject.optString("restaurant_instruction"));
                            riderInsTv.setText(orderJsonObject.optString("rider_instruction"));


                            totalAmountTv.setText(currency_symbol+orderJsonObject.optString("price"));

                            String getPaymentMethodTV = orderJsonObject.optString("cod");
                            if(getPaymentMethodTV.equalsIgnoreCase("0")) {
                                paymentMethodTv.setText("Credit Card");
                            }
                            else {
                                paymentMethodTv.setText("Cash On Delivery");
                            }

                            hotelNameTv.setText(restaurantJsonObject.optString("name"));
                            hotelPhoneNumberTv.setText(restaurantJsonObject.optString("phone"));
                            JSONObject restaurantAddress = restaurantJsonObject.getJSONObject("RestaurantLocation");
                            String street = restaurantAddress.optString("street");
                            String city = restaurantAddress.optString("city");

                            hotelAddTv.setText(street+", "+city);

                            String tax = orderJsonObject.optString("tax");

                            totalDiscountTv.setText(currency_symbol+""+orderJsonObject.optString("discount"));

                            String delivery_fee = orderJsonObject.optString("delivery_fee");
                            totalDeliveryFeeTv.setText(currency_symbol+delivery_fee);
                            String tax_free = restaurantJsonObject.optString("tax_free");
                            String rider_tip = orderJsonObject.optString("rider_tip");
                            if(rider_tip.equalsIgnoreCase("")){
                                rider_tip = "0.0";
                            }
                            String taxPercent = taxObj.optString("tax");
                            if(tax_free.equalsIgnoreCase("1")) {
                                taxTv.setText("(" + "0" + "%)");
                                totalTexTv.setText(currency_symbol+" 0.0");
                            }
                            else {
                                taxTv.setText("(" + taxPercent + "%)");
                                totalTexTv.setText(currency_symbol+" "+tax);
                            }

                            String subTotal = orderJsonObject.optString("sub_total");
                            subTotalAmountTv.setText(currency_symbol+" "+subTotal);
                            totalTipTv.setText(currency_symbol+" "+rider_tip);


                            JSONArray menuItemArray = allJsonObject.getJSONArray("OrderMenuItem");

                            for (int j=0;j<menuItemArray.length();j++) {

                                JSONObject alljsonJsonObject2 = menuItemArray.getJSONObject(j);
                                MenuItemModel menuItemModel = new MenuItemModel();
                                menuItemModel.setItem_name(alljsonJsonObject2.optString("name"));
                                menuItemModel.setItem_price(currency_symbol + alljsonJsonObject2.optString("price"));
                                menuItemModel.setId(alljsonJsonObject2.optString("id"));
                                menuItemModel.setOrder_id(alljsonJsonObject2.optString("order_id"));
                                menuItemModel.setOrder_quantity(alljsonJsonObject2.optString("quantity"));

                                listDataHeader.add(menuItemModel);

                                listChildData = new ArrayList<>();

                                JSONArray extramenuItemArray = alljsonJsonObject2.getJSONArray("OrderMenuExtraItem");
                                if(extramenuItemArray!=null&& extramenuItemArray.length()>0){
                                    for (int k = 0; k < extramenuItemArray.length(); k++) {
                                        if (extramenuItemArray.length() != 0) {
                                            JSONObject allJsonObject3 = extramenuItemArray.getJSONObject(k);
                                            MenuItemExtraModel menuItemExtraModel = new MenuItemExtraModel();

                                            menuItemExtraModel.setExtra_item_name(allJsonObject3.optString("name"));
                                            menuItemExtraModel.setPrice(allJsonObject3.optString("price"));
                                            menuItemExtraModel.setQuantity(allJsonObject3.optString("quantity"));
                                            menuItemExtraModel.setCurrency(currency_symbol);

                                            listChildData.add(menuItemExtraModel);

                                        }

                                    }

                                }
                                listChild.add(listChildData);
                            }


                        }

                        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listChild);
                        customExpandableListView.setAdapter(listAdapter);

                        for(int l=0; l < listAdapter.getGroupCount(); l++)
                            if(listChild.size()!=0) {
                                customExpandableListView.expandGroup(l);
                            }


                    }

                }

                catch (Exception e){
                    e.getMessage();

                }


                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);
            }
        });


    }







}
