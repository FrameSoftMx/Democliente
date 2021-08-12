package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.operialcompany.Democliente.Adapters.AdapterPager;
import com.operialcompany.Democliente.Adapters.CartFragExpandable;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.AddressListModel;
import com.operialcompany.Democliente.Models.CartFragChildModel;
import com.operialcompany.Democliente.Models.CartFragParentModel;
import com.operialcompany.Democliente.Models.RestaurantChildModel;
import com.operialcompany.Democliente.Models.RestaurantsModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.CustomExpandableListView;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by qboxus on 10/18/2019.
 */

public class CartFragment extends RootFragment implements View.OnClickListener{

    RelativeLayout deliveryDiv, pickupDiv, cartPaymentMethodDiv, cartAddressDiv, cartDatetimeDiv, tipDiv, promoCodeDiv, cartCheckOutDiv;
    TextView declineTv, acceptTv, taxTv, creditCardNumberTv, deliveryAddressTv, deliveryDatetimeTv, riderTipPriceTv, totalDeliveryFeeTv,
            promoTv, totalPromoTv, totalSumTv,rider_tip, discountTv, restNameTv, freeDeliveryTv;
    CustomExpandableListView selectedItemList;
    SharedPreferences sPref;
    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;
   private String udid,tax_dues,riderTip, taxPreference, feePrefernce, totalSum,res_id,rest_name,user_id,mQuantity,
            coupanCode;
    String grandTotal_ = "0";

    private String card_number;
    private String street,apartment,city,state, addressId;

    TextView restuarentInstructionTv;
    
    CartFragExpandable cartFragExpandable;
    ArrayList<CartFragParentModel> listDataHeader;
    ArrayList<CartFragChildModel> listChildData;
    private ArrayList<ArrayList<CartFragChildModel>> listChild;
    TextView subTotalPriceTv, totalTexTv;
    String grandTotal,symbol;
    CamomileSpinner cartProgress;
    Button clearBtn;
    RelativeLayout noCartDiv;
    Collection<Object> values;
    Map<String, Object> td;

    boolean getLoINSession,PICK_UP;
    Double previousRiderTip = 0.0;
    private boolean isViewShown = false;
    LinearLayout mainCartDiv;
    JSONArray jsonArrayMenuExtraItem;
    FrameLayout cartMainContainer;
    public static boolean ORDER_PLACED,UPDATE_NODE;

    RelativeLayout transparent_layer,progressDialog;

    public static boolean FLAG_CLEAR_ORDER;
    String minimumOrderPrice;
    private static  String key;
    View view;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_cart, container, false);
         context=getContext();

         if (!isViewShown) {
            initUI(view);
        }
        return view;

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            isViewShown = true;
            cartMainContainer.invalidate();
            initUI(getView());
        } else {
            isViewShown = false;
        }
    }



    public void initUI(View view){
        sPref = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        udid = sPref.getString(PreferenceClass.UDID,"");
        getLoINSession = sPref.getBoolean(PreferenceClass.IS_LOGIN,false);
        user_id = sPref.getString(PreferenceClass.pre_user_id,"");



        freeDeliveryTv = view.findViewById(R.id.free_delivery_tv);
        progressDialog = view.findViewById(R.id.progressDialog);
        transparent_layer = view.findViewById(R.id.transparent_layer);

        deliveryAddressTv = view.findViewById(R.id.delivery_address_tv);
        cartProgress = view.findViewById(R.id.cartProgress);
        cartProgress.start();
        cartMainContainer = view.findViewById(R.id.cart_main_container);
        cartMainContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


        cartMainContainer.invalidate();
        creditCardNumberTv = view.findViewById(R.id.credit_card_number_tv);


        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference().child(AllConstants.CALCULATION).child(udid);
        riderTip = "0";

        noCartDiv = view.findViewById(R.id.no_cart_div);
        mainCartDiv = view.findViewById(R.id.mainCartDiv);
        promoTv = view.findViewById(R.id.promo_tv);
        totalPromoTv = view.findViewById(R.id.total_promo_tv);


        cartCheckOutDiv = view.findViewById(R.id.cart_check_out_div);

        clearBtn = view.findViewById(R.id.clear_btn);
        restNameTv = view.findViewById(R.id.rest_name_tv);

        deliveryDatetimeTv =view.findViewById(R.id.delivery_datetime_tv);

        discountTv = view.findViewById(R.id.discount_tv);
        promoCodeDiv = view.findViewById(R.id.promo_code_div);
        rider_tip = view.findViewById(R.id.rider_tip);
        totalSumTv = view.findViewById(R.id.total_sum_tv);

        totalDeliveryFeeTv = view.findViewById(R.id.total_delivery_fee_tv);
        riderTipPriceTv = view.findViewById(R.id.rider_tip_price_tv);

        tipDiv = view.findViewById(R.id.tip_div);
        totalTexTv = view.findViewById(R.id.total_tex_tv);
        taxTv = view.findViewById(R.id.tax_tv);


        subTotalPriceTv = view.findViewById(R.id.sub_total_price_tv);
        pickupDiv = view.findViewById(R.id.pickup_div);
        deliveryDiv = view.findViewById(R.id.delivery_div);
        declineTv = view.findViewById(R.id.decline_tv);
        acceptTv = view.findViewById(R.id.accept_tv);
        selectedItemList = view.findViewById(R.id.selected_item_list);




        cartPaymentMethodDiv = view.findViewById(R.id.cart_payment_method_div);
        cartAddressDiv = view.findViewById(R.id.cart_address_div);
        cartDatetimeDiv =view.findViewById(R.id.cart_datetime_div);


        cartCheckOutDiv.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        tipDiv.setOnClickListener(this);
        promoCodeDiv.setOnClickListener(this);
        cartAddressDiv.setOnClickListener(this);
        cartDatetimeDiv.setOnClickListener(this);
        cartPaymentMethodDiv.setOnClickListener(this);





        selectedItemList.setExpanded(true);
        selectedItemList.setGroupIndicator(null);



        pickupDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickupDiv.setBackground(ContextCompat.getDrawable(context,R.drawable.round_shape_btn_login));
                deliveryDiv.setBackground(ContextCompat.getDrawable(context,R.drawable.round_shape_btn_grey));
                declineTv.setTextColor(ContextCompat.getColor(context,R.color.colorWhite));
                acceptTv.setTextColor(ContextCompat.getColor(context,R.color.color_light_black));
                riderTipPriceTv.setText(symbol+"0");
                totalDeliveryFeeTv.setText(symbol+"0");
                rider_tip.setText(symbol+"0");
                deliveryAddressTv.setText(getResources().getString(R.string.pick_up));
                cartDatetimeDiv.setVisibility(View.GONE);
                PICK_UP = true;

                calculatePrice();

                cartAddressDiv.setOnTouchListener(new View.OnTouchListener() {
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

        deliveryDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickupDiv.setBackground(ContextCompat.getDrawable(context,R.drawable.round_shape_btn_grey));
                deliveryDiv.setBackground(ContextCompat.getDrawable(context,R.drawable.round_shape_btn_login));
                declineTv.setTextColor(ContextCompat.getColor(context,R.color.color_light_black));
                acceptTv.setTextColor(ContextCompat.getColor(context,R.color.colorWhite));
                riderTipPriceTv.setText(symbol+riderTip);
                totalDeliveryFeeTv.setText(symbol+ feePrefernce);
                rider_tip.setText(symbol+riderTip);
                if(street==null&&apartment==null&&city==null&&state==null){
                    deliveryAddressTv.setText(getResources().getString(R.string.select_delivery_address));
                }
                else {
                    deliveryAddressTv.setText(street + " " + apartment + " " + city + " " + state);
                }
                PICK_UP = false;

                previousRiderTip = 0.0;

                cartDatetimeDiv.setVisibility(View.VISIBLE);

                calculatePrice();

                cartAddressDiv.setOnTouchListener(new View.OnTouchListener() {
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


        restuarentInstructionTv =view.findViewById(R.id.restuarent_instruction_tv);
        restuarentInstructionTv.setSelected(true);
        view.findViewById(R.id.restuarent_instruction_div).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRestuarentInstruction();
            }
        });


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.tip_div:
                addRiderTip();
                break;


            case R.id.promo_code_div:
                varifyCoupan();
                break;


            case R.id.cart_address_div:
                if(!getLoINSession){

                    TabLayout.Tab tab =PagerMainActivity.tabLayout.getTabAt(3);
                    tab.select();

                }
                else {
                   if(addressListModel!=null){
                       openAddressDetail();
                   }
                   else {
                       openAddressList();
                   }
                }
                break;


            case R.id.cart_datetime_div:
                SelectDateTime_F select_dateTime_f = new SelectDateTime_F(new FragmentCallback() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if(bundle!=null){
                            deliveryDatetimeTv.setText(bundle.getString("datetime"));
                        }
                    }
                });
                FragmentTransaction dtransaction = getChildFragmentManager().beginTransaction();
                dtransaction.addToBackStack(null);
                dtransaction.add(R.id.cart_main_container, select_dateTime_f, "parent").commit();
                break;

            case R.id.cart_payment_method_div:
                if(!getLoINSession){

                    TabLayout.Tab tab =PagerMainActivity.tabLayout.getTabAt(3);
                    tab.select();

                }
                else {
                    Fragment restaurantMenuItemsFragment = new AddPaymentFragment(new FragmentCallback() {
                        @Override
                        public void onResponce(Bundle bundle) {
                            if(bundle!=null){

                                card_number=bundle.getString("card_number");
                                creditCardNumberTv.setText(card_number);
                                creditCardNumberTv.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
                            }
                        }
                    });
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.add(R.id.cart_main_container, restaurantMenuItemsFragment, "parent").commit();
                }
                break;


            case R.id.cart_check_out_div:
                if(!getLoINSession){

                    TabLayout.Tab tab =PagerMainActivity.tabLayout.getTabAt(3);
                    tab.select();

                }
                else {
                    if (deliveryAddressTv.getText().toString().equalsIgnoreCase(getString(R.string.select_delivery_address))) {
                        Toast.makeText(getContext(), R.string.delivery_address_can_not_empty, Toast.LENGTH_LONG).show();
                    } else {
                        placeOrder();
                    }
                }
                break;


            case R.id.clear_btn:
                showDialogCartDelete();
                break;


        }

    }


    AddressListModel addressListModel;
    public void openAddressList(){
        Fragment restaurantMenuItemsFragment = new AddressListFragment(new FragmentCallback() {
            @Override
            public void onResponce(Bundle bundle) {
                if(bundle!=null){
                    addressListModel=(AddressListModel)bundle.getSerializable("data");

                    street =addressListModel.getStreet();
                    city =addressListModel.getCity();
                    state =addressListModel.getState();
                    apartment =addressListModel.getApartment();
                    addressId =addressListModel.getAddress_id();

                    openAddressDetail();

                    creditCardNumberTv.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
                    deliveryAddressTv.setText(street + " " + city + " " + state);
                    deliveryAddressTv.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
                    feePrefernce =addressListModel.getDelivery_fee();
                    totalDeliveryFeeTv.setText(symbol+ feePrefernce);

                    calculatePrice();
                }
            }
        });
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Bundle bundle=new Bundle();
        bundle.putString("grand_total",grandTotal);
        bundle.putString("rest_id",res_id);
        restaurantMenuItemsFragment.setArguments(bundle);
        transaction.addToBackStack(null);
        transaction.add(R.id.cart_main_container, restaurantMenuItemsFragment, "parent").commit();

    }


    public void openAddressDetail(){
        DeliveryAddressDetail_F schedule_ride_f = new DeliveryAddressDetail_F(addressListModel,new FragmentCallback() {
            @Override
            public void onResponce(Bundle bundle) {

                if(bundle!=null){
                    addressListModel.setInstruction(bundle.getString("instruction"));
                }
                else {
                    openAddressList();
                }

            }
        });
        schedule_ride_f.show(getFragmentManager(), "");
    }

    public void showDialogCartDelete(){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        } else {
            builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        }
        builder.setTitle(R.string.delete_cart)
                .setMessage(R.string.are_you_sure_to_delete_cart)
                .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mDatabase.setValue(null);
                        noCartDiv.setVisibility(View.VISIBLE);
                        mainCartDiv.setVisibility(View.GONE);
                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putInt(PreferenceClass.CART_COUNT,0);
                        editor.putInt("count",0)
                                .commit();
                        Intent intent = new Intent();
                        intent.setAction("AddToCart");
                        getContext().sendBroadcast(intent);

                        AdapterPager a = (AdapterPager) PagerMainActivity.viewPager.getAdapter();
                        a.destroyItem(PagerMainActivity.viewPager, 2,a.instantiateItem(PagerMainActivity.viewPager, 2));
                        CartFragment f = (CartFragment) a.instantiateItem(PagerMainActivity.viewPager, 2);


                        FLAG_CLEAR_ORDER = true;


                        dialog.dismiss();

                    }


                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                    }
                })
                .show();

    }


    public void getCartData(){
        mDatabase.keepSynced(true);
        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);
        listDataHeader = new ArrayList<>();
        listChild = new ArrayList<>();
        DatabaseReference query = mDatabase;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               if(dataSnapshot.exists()) {

                   Log.d(AllConstants.tag, dataSnapshot.toString());

                   td = (HashMap<String, Object>) dataSnapshot.getValue();
                   if (td != null) {

                       values = td.values();
                       JSONArray jsonArray = null;
                       try {
                           jsonArray = new JSONArray(values);
                           grandTotal_ = "0";
                           for (int a = 0; a < jsonArray.length(); a++) {

                               JSONObject allJsonObject = jsonArray.getJSONObject(a);

                               CartFragParentModel cartFragParentModel = new CartFragParentModel();

                               cartFragParentModel.setItem_name(allJsonObject.optString("mName"));
                               cartFragParentModel.setItem_price(allJsonObject.optString("mPrice"));
                               mQuantity = allJsonObject.optString("mQuantity");
                               cartFragParentModel.setItem_quantity(allJsonObject.optString("mQuantity"));
                               cartFragParentModel.setItem_symbol(allJsonObject.optString("mCurrency"));
                               cartFragParentModel.setItem_key(allJsonObject.optString("key"));

                               String total = allJsonObject.optString("grandTotal");
                               minimumOrderPrice = allJsonObject.optString("minimumOrderPrice");
                               symbol = allJsonObject.optString("mCurrency");

                               res_id = allJsonObject.optString("restID");
                               rest_name = allJsonObject.optString("rest_name");
                               restNameTv.setText(rest_name);


                               if (total.isEmpty() || total.equalsIgnoreCase("null")) {

                                   total = "0";
                               }

                               getDescText(minimumOrderPrice, total);

                               grandTotal = "" + (Double.parseDouble(total) + Double.parseDouble(grandTotal_));

                               grandTotal_ = grandTotal;

                               taxPreference = allJsonObject.optString("mTax");


                               listDataHeader.add(cartFragParentModel);
                               listChildData = new ArrayList<>();

                               if (!allJsonObject.has("extraItem")) {
                                   TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
                                   listChild.add(listChildData);
                               } else {
                                   JSONArray extraItemArray = allJsonObject.getJSONArray("extraItem");

                                   for (int b = 0; b < extraItemArray.length(); b++) {

                                       JSONObject jsonObject = extraItemArray.getJSONObject(b);

                                       CartFragChildModel cartFragChildModel = new CartFragChildModel();

                                       cartFragChildModel.setQuantity(allJsonObject.optString("mQuantity"));
                                       cartFragChildModel.setSymbol(allJsonObject.optString("mCurrency"));
                                       cartFragChildModel.setName(jsonObject.optString("menu_extra_item_name"));
                                       cartFragChildModel.setPrice(jsonObject.optString("menu_extra_item_price"));

                                       listChildData.add(cartFragChildModel);
                                   }
                                   listChild.add(listChildData);

                               }
                           }
                           if ((listDataHeader != null  && getView()!=null) && listDataHeader.size() > 0) {

                               (getView().findViewById(R.id.no_cart_div)).setVisibility(View.GONE);
                               (getView().findViewById(R.id.mainCartDiv)).setVisibility(View.VISIBLE);

                               subTotalPriceTv.setText(symbol + grandTotal);

                               if (!taxPreference.isEmpty()) {
                                   taxTv.setText("(" + taxPreference + "%)");
                               } else {
                                   taxPreference = String.valueOf(0);
                                   taxTv.setText("(0%)");
                               }


                               if (feePrefernce != null) {
                                   if (feePrefernce.isEmpty()) {
                                       feePrefernce = String.valueOf(0);
                                   }
                               }else {
                                   feePrefernce = "0";
                               }



                               if (grandTotal.isEmpty()) {
                                   grandTotal = "0.0";
                               }

                               if (deliveryAddressTv.getText().toString().equalsIgnoreCase(getString(R.string.select_delivery_address))) {
                                   feePrefernce = "" + 0.0;
                               }
                               totalDeliveryFeeTv.setText(symbol + feePrefernce);


                               if(discountTv.getText().toString().equalsIgnoreCase(getString(R.string.add_promo_code))){
                                   discount="0";
                                   discountAmount =0.0;
                               }


                               riderTipPriceTv.setText(symbol + "0.0");

                               totalPromoTv.setText(symbol + "0.0");
                               totalSumTv.setText(totalSum);

                               calculatePrice();

                               cartFragExpandable = new CartFragExpandable(getContext(), listDataHeader, listChild);
                               selectedItemList.setAdapter(cartFragExpandable);

                               TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, true);
                               int itemCount = cartFragExpandable.getGroupCount();


                               for (int i = 0; i < cartFragExpandable.getGroupCount(); i++)
                                   try {

                                       selectedItemList.expandGroup(i);
                                   } catch (IndexOutOfBoundsException e) {
                                       e.getCause();
                                   }

                               selectedItemList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                                   @Override
                                   public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                                       CartFragParentModel item = (CartFragParentModel) listDataHeader.get(groupPosition);
                                       key = item.getItem_key();
                                       customDialogbox();
                                       return true;
                                   }
                               });

                           } else if(getView()!=null) {
                               (getView().findViewById(R.id.no_cart_div)).setVisibility(View.VISIBLE);
                               (getView().findViewById(R.id.mainCartDiv)).setVisibility(View.GONE);
                           }

                           transparent_layer.setVisibility(View.GONE);
                           progressDialog.setVisibility(View.GONE);


                       } catch (JSONException e) {
                           noDataFound();
                          }
                   } else {
                       noDataFound();
                      }
               }
               else {
                     noDataFound();
                   }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                noDataFound();
            }
        });

    }


    public void noDataFound(){

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
        transparent_layer.setVisibility(View.GONE);
        progressDialog.setVisibility(View.GONE);

        if(getView()!=null) {
            (getView().findViewById(R.id.no_cart_div)).setVisibility(View.VISIBLE);
            (getView().findViewById(R.id.mainCartDiv)).setVisibility(View.GONE);
        }
    }

    private void getDescText(String minimumOrderPrice, String grandTotal){

        try {
            Double var3 = Double.parseDouble(minimumOrderPrice) - Double.parseDouble(grandTotal);

            if (var3 >= Double.parseDouble(minimumOrderPrice)) {

                freeDeliveryTv.setText(R.string.you_have_reached_your_free_delivery_order);



            } else {
                if (String.valueOf(var3).contains("-")) {
                    freeDeliveryTv.setText(R.string.you_have_reached_your_free_delivery_order);

                } else {
                    freeDeliveryTv.setText(getString(R.string.you_have_to_need_more) + symbol + var3 + getString(R.string.for_free_delivery_order));
                }
            }
        }catch (Exception e){

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
        title.setText(getContext().getResources().getString(R.string.add_rider_tip));
        ed_text.setHint(R.string.enter_tip_here);

        Button cancelDiv = (Button) dialog.findViewById(R.id.cancel_btn);
        Button done_btn =  (Button) dialog.findViewById(R.id.done_btn);

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(ed_text.getText().toString())){

                riderTip = ed_text.getText().toString();
                PICK_UP = false;
                riderTipPriceTv.setText(symbol+riderTip);
                rider_tip.setText(symbol+riderTip);

                calculatePrice();

                dialog.dismiss();
                }else {
                    Toast.makeText(getContext(), R.string.please_enter_the_ammount, Toast.LENGTH_SHORT).show();
                }
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


    public void addRestuarentInstruction(){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.item_dialog_restaurent_instruction);

        final EditText add_instructions_edit = dialog.findViewById(R.id.add_instructions_edit);

        Button done_btn = (Button) dialog.findViewById(R.id.done_btn);
        Button cancel_btn=dialog.findViewById(R.id.cancel_btn);


        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(done_btn.getText().toString())){

                    restuarentInstructionTv.setText(add_instructions_edit.getText().toString());
                    restuarentInstructionTv.setSelected(true);
                    dialog.cancel();
                }
                else {
                    Toast.makeText(getContext(), "Please add instructions", Toast.LENGTH_SHORT).show();
                }


            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();

    }


    public void varifyCoupan(){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog_cart);


       final EditText ed_text = dialog.findViewById(R.id.ed_text);

        Button cancelDiv = (Button) dialog.findViewById(R.id.cancel_btn);
        Button done_btn = (Button) dialog.findViewById(R.id.done_btn);

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(ed_text.getText().toString())) {

                    coupanCode = ed_text.getText().toString();
                    getCoupanRequest(coupanCode);

                    TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout, false);
                    transparent_layer.setVisibility(View.VISIBLE);
                    progressDialog.setVisibility(View.VISIBLE);
                    dialog.dismiss();

                }else {
                    Toast.makeText(getContext(), R.string.please_enter_the_promocode, Toast.LENGTH_SHORT).show();
                }
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

    String discount="0";
    Double discountAmount =0.0;
    String couponId ="0";
    public void getCoupanRequest(String coupan_code){


        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("coupon_code",coupan_code);
            jsonObject.put("restaurant_id",res_id);
            jsonObject.put("user_id",user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        ApiRequest.callApi(context, Config.VERIFY_COUPAN, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject1 = new JSONObject(resp);

                    int code = Integer.parseInt(jsonObject1.optString("code"));
                    if (code == 200) {

                            JSONArray jsonArray = jsonObject1.getJSONArray("msg");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                JSONObject jsonObject3 = jsonObject2.getJSONObject("RestaurantCoupon");
                                discount = jsonObject3.optString("discount");
                                couponId =jsonObject3.optString("id");
                                promoTv.setText("("+discount+"%)");

                                calculatePrice();
                            }

                        } else {
                            Toast.makeText(getContext(), resp.toString(), Toast.LENGTH_SHORT).show();
                        }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });



    }


    public void calculatePrice(){

        totalSum =grandTotal_;


        if(!PICK_UP){
            totalSum = ""+(Double.parseDouble(totalSum) + Double.parseDouble(feePrefernce));

            totalSum = ""+(Double.parseDouble(totalSum) + Double.parseDouble(riderTip));

        }


        float tax= (float) (Double.parseDouble(grandTotal) * Double.parseDouble(taxPreference) / 100);

        tax_dues = getRoundoffDouble(""+tax);
        totalSum = ""+(Double.valueOf(Double.parseDouble(totalSum) + Double.parseDouble(tax_dues)));
        totalTexTv.setText(symbol + tax_dues);



         float dis= (float) ((Double.parseDouble(discount)*Double.parseDouble(grandTotal_))/100);
         discountAmount = Double.valueOf(getRoundoffDouble(""+dis));

       if(discountAmount >0) {
           discountTv.setText(symbol + " " + discountAmount + " (" + discount + "%)");
           totalPromoTv.setText(symbol + " " + discountAmount);
       }


        totalSum = ""+(Double.parseDouble(totalSum)- discountAmount);


        totalSum = getRoundoffDouble(totalSum);
        totalSumTv.setText(symbol+ getRoundoffDouble(totalSum));


    }


    public String getRoundoffDouble(String value){

        if(!value.contains(".")){
            return value;
        }
        else
        {
            int position = value.indexOf(".");
            return value.substring(0,position+2);
        }
    }



    HashMap<String,Object> values_final;
    ArrayList<HashMap<String,Object>> extraItemArray;
    public void placeOrder(){

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparent_layer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        extraItemArray = new ArrayList<>();

        JSONArray menu_item=null;
        JSONArray valueArray = new JSONArray(values);
        for (int i=0;i<valueArray.length();i++){

            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = valueArray.getJSONObject(i);
                values_final= new HashMap<>();

                if(jsonObject1.optString("extraItem")!=null&& !jsonObject1.optString("extraItem").isEmpty()) {
                    jsonArrayMenuExtraItem = new JSONArray(jsonObject1.optString("extraItem"));
                    values_final.put("menu_extra_item",jsonArrayMenuExtraItem);
                    String size = String.valueOf(jsonArrayMenuExtraItem.length());
                }
                else {
                    values_final.put("menu_extra_item",new JSONArray("["+"]"));
                }


                    values_final.put("menu_item_price", jsonObject1.optString("mPrice"));
                    values_final.put("menu_item_quantity", jsonObject1.optString("mQuantity"));
                    values_final.put("menu_item_name", jsonObject1.optString("mName"));


                     extraItemArray.add(values_final);

            } catch (JSONException e) {
                e.printStackTrace();
                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

            }

        }

        menu_item =new JSONArray(extraItemArray);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id",user_id);
            jsonObject.put("price", totalSum);
            jsonObject.put("sub_total",grandTotal);
            jsonObject.put("tax",tax_dues);
            jsonObject.put("quantity",mQuantity);
            if(deliveryAddressTv.getText().toString().equalsIgnoreCase(getResources().getString(R.string.pick_up)))
            {
                jsonObject.put("address_id", "");
            }
            else {

                jsonObject.put("address_id", addressId);
                jsonObject.put("rider_instruction",addressListModel.getInstruction());

            }

            jsonObject.put("restaurant_id",res_id);
            jsonObject.put("restaurant_instruction", restuarentInstructionTv.getText().toString());

            jsonObject.put("coupon_id", couponId);
            jsonObject.put("discount", discountAmount);

            jsonObject.put("order_time",formattedDate);
            jsonObject.put("delivery_fee", feePrefernce);
            jsonObject.put("version",SplashScreen.versionCode);

            if(deliveryAddressTv.getText().toString().equalsIgnoreCase(getResources().getString(R.string.pick_up))) {
                jsonObject.put("delivery","0");
            }
            else {
                jsonObject.put("delivery","1");
            }


            if(deliveryAddressTv.getText().toString().equalsIgnoreCase(getResources().getString(R.string.pick_up)))
            {
                jsonObject.put("delivery_date_time","");
            }
            else if(deliveryDatetimeTv.getText().toString().equalsIgnoreCase(getResources().getString(R.string.select_delivery_time))){
                jsonObject.put("delivery_date_time","");
            }
            else {
                jsonObject.put("delivery_date_time", deliveryDatetimeTv.getText());
            }


            if(rider_tip.getText().toString().equalsIgnoreCase(getResources().getString(R.string.add_rider_tip))){
                jsonObject.put("rider_tip","0");
            }
            else {
                String riderTip_ = riderTip;
                jsonObject.put("rider_tip",riderTip_ );
            }

            jsonObject.put("device","android");

            jsonObject.put("cod","1");
            jsonObject.put("payment_id","0");

            jsonObject.put("menu_item",menu_item);


            List <String> language_names_for_api=  Arrays.asList(getResources().getStringArray(R.array.language_names_for_api));
            List <String> language_code=  Arrays.asList(getResources().getStringArray(R.array.language_code));

            String language= Locale.getDefault().getLanguage();
            if(sPref.getString(PreferenceClass.selected_language,null)!=null)
                language = sPref.getString(PreferenceClass.selected_language, language_code.get(0));

            if(language_code.contains(language)){
                    jsonObject.put("lang",language_names_for_api.get(language_code.indexOf(language)));
            }else {
                jsonObject.put("lang","english");
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
        Log.d(AllConstants.tag,jsonObject.toString());


        /*
         Gson userGson=new GsonBuilder().create();
         String jsonstring=userGson.toJson(jsonObject.toString());
        */

       Log.d(AllConstants.tag,jsonObject.toString());


        ApiRequest.callApi(context, Config.addOrderSession, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {
                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparent_layer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);

                try {
                    JSONObject  responce = new JSONObject(resp);
                    int code_id  = Integer.parseInt(responce.optString("code"));
                    if(code_id == 200) {
                        JSONObject msg=responce.optJSONObject("msg");
                        if(msg!=null){
                            JSONObject OrderSession=msg.optJSONObject("OrderSession");
                            String id=OrderSession.optString("id");

                            Log.d(AllConstants.tag,Config.payment_link);
                            openCheckoutFragment(id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    public void openCheckoutFragment(String id){
        Checkout_F checkout_f = new Checkout_F(new FragmentCallback() {
            @Override
            public void onResponce(Bundle bundle) {

                mDatabase.setValue(null);
                Intent intent = new Intent();
                intent.setAction("AddToCart");
                getContext().sendBroadcast(intent);

                SharedPreferences.Editor editor = sPref.edit();
                editor.putInt(PreferenceClass.CART_COUNT,0);
                editor.putInt("count",0)
                        .commit();
                ORDER_PLACED = true;
                FLAG_CLEAR_ORDER = true;

                getCartData();

                startActivity(new Intent(getContext(),MainActivity.class));
                getActivity().finish();

            }
        });
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle=new Bundle();
        bundle.putString("url",Config.payment_link+id);
        checkout_f.setArguments(bundle);

        transaction.addToBackStack(null);
        transaction.replace(R.id.cart_main_container, checkout_f).commit();
    }

    @SuppressLint("SetTextI18n")
    public void customDialogbox(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialoge_box);


        RelativeLayout cancelDiv = (RelativeLayout) dialog.findViewById(R.id.forth);
        RelativeLayout currentOrderDiv = (RelativeLayout) dialog.findViewById(R.id.second);
        RelativeLayout pastOrderDiv = (RelativeLayout) dialog.findViewById(R.id.third);
        TextView first_tv = (TextView)dialog.findViewById(R.id.first_tv);
        TextView second_tv = (TextView)dialog.findViewById(R.id.second_tv);
        TextView third_tv = (TextView)dialog.findViewById(R.id.third_tv);
        first_tv.setText(R.string.edit);
        first_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorFB));
        second_tv.setText(R.string.delete);
        second_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        third_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorFB));

        currentOrderDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNode();

                UPDATE_NODE = true;

                dialog.dismiss();

            }
        });

        pastOrderDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteSelectedNode(key);
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

    @Override
    public void onResume() {
        super.onResume();
        if(AddressListFragment.CART_NOT_LOAD ){
            AddressListFragment.CART_NOT_LOAD = false;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getCartData();
            }
        },1000);

        
    }

    public void deleteSelectedNode(final String key){

       final DatabaseReference deleteNode = mDatabase.child(key);

       deleteNode.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               String name = dataSnapshot.child("key").getValue(String.class);


                   if(name!=null && name.equalsIgnoreCase(key)){
                       deleteNode.setValue(null);

                       int getCartCount = sPref.getInt("count",0);
                       getCartCount=getCartCount-1;
                       SharedPreferences.Editor editor = sPref.edit();
                       editor.putInt("count",getCartCount).commit();
                       getActivity().sendBroadcast(new Intent("AddToCart"));


                       if(getCartCount<=0) {
                           mDatabase.setValue(null);
                           noCartDiv.setVisibility(View.VISIBLE);
                           mainCartDiv.setVisibility(View.GONE);

                           AdapterPager a = (AdapterPager) PagerMainActivity.viewPager.getAdapter();
                           a.destroyItem(PagerMainActivity.viewPager, 2, a.instantiateItem(PagerMainActivity.viewPager, 2));
                           CartFragment f = (CartFragment) a.instantiateItem(PagerMainActivity.viewPager, 2);

                           FLAG_CLEAR_ORDER = true;
                       }else {
                           getCartData();
                       }

                   }

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

    }

    public void editNode(){

        final DatabaseReference deleteNode = mDatabase.child(key);

        deleteNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("key").getValue(String.class);

                if(name.equalsIgnoreCase(key)){



                    Intent intent = new Intent(getContext(),AddToCartActivity.class);


                    RestaurantsModel rest_model=new RestaurantsModel();
                    RestaurantChildModel rest_child_model=new RestaurantChildModel();

                    rest_model.restaurant_id=dataSnapshot.child("restID").getValue(String.class);
                    rest_model.restaurant_name=dataSnapshot.child("rest_name").getValue(String.class);
                    rest_model.restaurant_tax=dataSnapshot.child("mTax").getValue(String.class);
                    rest_model.min_order_price=dataSnapshot.child("minimumOrderPrice").getValue(String.class);


                    rest_child_model.restaurant_menu_item_id=dataSnapshot.child("mID").getValue(String.class);
                    rest_child_model.child_title=dataSnapshot.child("mName").getValue(String.class);
                    rest_child_model.child_sub_title=dataSnapshot.child("mDesc").getValue(String.class);
                    rest_child_model.price=dataSnapshot.child("mPrice").getValue(String.class);
                    rest_child_model.currency_symbol=dataSnapshot.child("mCurrency").getValue(String.class);


                    intent.putExtra("rest_model",rest_model);
                    intent.putExtra("rest_child_model",rest_child_model);
                    intent.putExtra("key",key);


                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
