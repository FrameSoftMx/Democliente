package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.operialcompany.Democliente.Adapters.AddToCartExpandable;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.CalculationModel;
import com.operialcompany.Democliente.Models.CartChildModel;
import com.operialcompany.Democliente.Models.CartParentModel;
import com.operialcompany.Democliente.Models.RestaurantChildModel;
import com.operialcompany.Democliente.Models.RestaurantsModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.ContextWrapper;
import com.operialcompany.Democliente.Utils.CustomExpandableListView;
import com.operialcompany.Democliente.Utils.FontHelper;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.operialcompany.Democliente.ActivitiesAndFragments.CartFragment.UPDATE_NODE;
import static com.operialcompany.Democliente.ActivitiesAndFragments.PagerMainActivity.tabLayout;


/**
 * Created by qboxus on 10/18/2019.
 */

public class AddToCartActivity extends AppCompatActivity {

    ImageView closMenuItemsDetail;
    Button increamentBtn, decrementBtn;
    TextView incDecTv, grandTotalPriceTv, totalPriceTv, descTv, nameTv, cartTitleTv;
    int presentCount = 1;
    SharedPreferences sPref;

    AddToCartExpandable listAdapter;
    CustomExpandableListView cartExpandableListView;
    ArrayList<CartParentModel> listDataHeader;
    ArrayList<CartChildModel> listChildData;
    private ArrayList<ArrayList<CartChildModel>> listChild;
    String restaurantMenuItemId;
    DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    private  String userId,udid, key, name,desc, price,symbol, resId, resName, resTax, resFee;
    Double menuExtraItemObj,itemPrice;
    int required = 0;
    Double totalExtraItemPrice,grandTotal;

    int randomNum,count;

    ArrayList<HashMap<String,String>> extraItem;

    ArrayList<Integer> arrayList = new ArrayList<>();

    public  boolean FLAG_ONCE_LOOP_ADD,FLAG_CART_ADD,FIRST_TIME_LOADER,WAS_IN_BG;
    String previousCheck;
    RelativeLayout addToCart;
    EditText instText;
    public TextView tabBadge, cartBtnText;
    String prevRestId;
    int showcartCount = 0;
    String minOrderPrice, descFinal;
    private boolean UPDATE_CONFIRM;


    RestaurantsModel rest_model;
    RestaurantChildModel rest_child_model;



    SharedPreferences sharedPreferences;

    @Override
    protected void attachBaseContext(Context newBase) {

        if (new DarkModePrefManager(newBase).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        String[] language_array = newBase.getResources().getStringArray(R.array.language_code);
        List<String> language_code = Arrays.asList(language_array);
        sharedPreferences = newBase.getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);

        PreferenceClass.sharedPreferences = sharedPreferences;

        String language = sharedPreferences.getString(PreferenceClass.selected_language, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && language_code.contains(language)) {
            Locale newLocale = new Locale(language);
            Context context = ContextWrapper.wrap(newBase, newLocale);
            super.attachBaseContext(context);
        } else {
            super.attachBaseContext(newBase);
        }

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        firebaseDatabase = FirebaseDatabase.getInstance();

        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);


        View view = LayoutInflater.from(AddToCartActivity.this).inflate(R.layout.custom_tab, null);
        tabBadge =  view.findViewById(R.id.tab_badge);


        sPref = getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        udid = sPref.getString(PreferenceClass.UDID,"");

        if(UPDATE_NODE) {
            key = getIntent().getStringExtra("key");
        }


        Intent intent=getIntent();
        if(intent.hasExtra("rest_model")){
         rest_model=(RestaurantsModel) intent.getSerializableExtra("rest_model");
         rest_child_model=(RestaurantChildModel) intent.getSerializableExtra("rest_child_model");
        }

        restaurantMenuItemId = rest_child_model.restaurant_menu_item_id;


        name = rest_child_model.child_title;
        desc = rest_child_model.child_sub_title;
        price = rest_child_model.price;
        symbol = rest_child_model.currency_symbol;


        resId = rest_model.restaurant_id;
        resName =rest_model.restaurant_name;
        resTax = rest_model.restaurant_tax;

        if( resTax ==null|| resTax.equals("null"))
            resTax = "0";




        minOrderPrice = rest_model.min_order_price;


        resFee = "0";
        instText = findViewById(R.id.inst_text);
        Random r = new Random();
        randomNum = r.nextInt(1000 - 65) + 65;


        extraItem = new ArrayList<>();


        RelativeLayout main_add_to_cart = findViewById(R.id.main_add_to_cart);
        FontHelper.applyFont(getApplicationContext(),main_add_to_cart, AllConstants.verdana);

        mDatabase = firebaseDatabase.getReference().child(AllConstants.CALCULATION).child(udid);
        mDatabase.keepSynced(true);
        FLAG_CART_ADD = false;

        Query query = mDatabase;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if(UPDATE_NODE){
                        mDatabase.child(key).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                name, price,"","1","0", minOrderPrice,
                                extraItem, instText.getText().toString(), resId, resName,symbol,desc, resFee, resTax));
                    }
                    else {
                        userId = mDatabase.push().getKey();
                        mDatabase.child(userId).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                name, price, "", "1", "0", minOrderPrice,
                                extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                    }
                }
                else {

                    if(UPDATE_NODE){
                        mDatabase.child(key).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                name, price,"","1","0", minOrderPrice,
                                extraItem, instText.getText().toString(), resId, resName,symbol,desc, resFee, resTax));
                    }
                    else {
                        userId = mDatabase.push().getKey();
                        mDatabase.child(userId).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                name, price, "", "1", "0", minOrderPrice, extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listDataHeader = new ArrayList<CartParentModel>();
        listChild = new ArrayList<>();


        cartExpandableListView = (CustomExpandableListView ) findViewById(R.id.item_detail_list);
        cartExpandableListView .setExpanded(true);
        cartExpandableListView.setGroupIndicator(null);

        listAdapter = new AddToCartExpandable(getApplicationContext(), listDataHeader, listChild);
        cartExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {

                final CartChildModel item = (CartChildModel) listAdapter.getChild(groupPosition, childPosition);

                boolean iteIsRequired = item.isCheckRequired();

                if (!iteIsRequired) {

                    CheckBox checkBox = view.findViewById(R.id.check_btn);

                    if (checkBox != null) {
                        if (!checkBox.isChecked()) {
                            checkBox.setChecked(true);
                            FLAG_ONCE_LOOP_ADD = true;
                            addNewNode(item.getExtra_item_id(), item.getChild_item_name(), item.getChild_item_price());
                            loadCalculationDetail();
                        } else if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                            FLAG_ONCE_LOOP_ADD = false;
                            deleteNewNode(item.getExtra_item_id());
                        }
                    }
                } else {


                    if (!item.isCheckedddd()) {
                        String string = arrayList.toString();
                        ArrayList<CartChildModel> childsList=listAdapter.getChilderns(groupPosition);
                        for (CartChildModel model:childsList){
                            if(model.isCheckedddd()){
                                previousCheck=model.getExtra_item_id();
                                break;
                            }
                        }
                        if (arrayList.contains(groupPosition)) {
                            deleteNewNode(previousCheck);
                            addNewNode(item.getExtra_item_id(), item.getChild_item_name(), item.getChild_item_price());
                            previousCheck = item.getExtra_item_id();


                        } else {

                            addNewNode(item.getExtra_item_id(), item.getChild_item_name(), item.getChild_item_price());
                            previousCheck = item.getExtra_item_id();
                            loadCalculationDetail();
                            required = required + 1;
                                 }

                        if (!arrayList.contains(groupPosition)) {
                            arrayList.add(groupPosition);
                        }

                        upDateNotify(listAdapter.getChilderns(groupPosition));
                        item.setCheckeddd(true);
                        listAdapter.notifyDataSetChanged();
                    }


                }
                return false;
            }
        });
        cartExpandableListView.setAdapter(listAdapter);


        init();
        increamentDecFunc();

    }


    public void init(){
        cartBtnText = findViewById(R.id.cart_btn_text);
        if(UPDATE_NODE){
            cartBtnText.setText("Update Cart");
        }


        addToCart = findViewById(R.id.add_to_cart);
        increamentBtn = findViewById(R.id.plus_btn);
        decrementBtn = findViewById(R.id.minus_btn);
        incDecTv = findViewById(R.id.inc_dec_tv);
        closMenuItemsDetail = findViewById(R.id.clos_menu_items_detail);
        grandTotalPriceTv = findViewById(R.id.grand_total_price_tv);
        totalPriceTv = findViewById(R.id.total_price_tv);
        descTv = findViewById(R.id.desc_tv);
        nameTv = findViewById(R.id.name_tv);

        cartTitleTv = findViewById(R.id.cart_title_tv);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCalculationDetail();
            }
        },500);

        closMenuItemsDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();


            }
        });


        descFinal = desc.replaceAll("&amp;", "&");


        nameTv.setText(name.replaceAll("&amp;", "&"));

        descTv.setText(descFinal);
        totalPriceTv.setText(symbol+ price);
        cartTitleTv.setText(name.replaceAll("&amp;", "&"));
        getOrderDetailItems();

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(count==required){
                    Query lastQuery = mDatabase.orderByKey().limitToFirst(1);
                    mDatabase.keepSynced(true);
                    lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                            Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                            Collection<Object> values = td.values();

                            JSONArray jsonArray = new JSONArray(values);

                            int size = jsonArray.length();

                            for (int a = 0; a < 1; a++) {

                                JSONObject allJsonObject = null;
                                try {
                                    allJsonObject = jsonArray.getJSONObject(0);
                                    prevRestId = String.valueOf(allJsonObject.optString("restID"));

                                    if (!prevRestId.equalsIgnoreCase(resId)) {
                                        showDialogIfChangeRest();

                                    } else {
                                        if (UPDATE_NODE) {
                                            mDatabase.child(key).setValue(new CalculationModel(key, restaurantMenuItemId,
                                                    name, price, String.valueOf(grandTotal), incDecTv.getText().toString(), "0",
                                                    minOrderPrice, extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                                            AddPaymentFragment.FLAG_ADD_PAYMENT = false;
                                            AddressListFragment.FLAG_ADDRESS_LIST = false;
                                            UPDATE_NODE = false;
                                            UPDATE_CONFIRM = true;
                                        } else {
                                            mDatabase.child(userId).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                                    name, price, String.valueOf(grandTotal), incDecTv.getText().toString(), "0",
                                                    minOrderPrice, extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                                            AddPaymentFragment.FLAG_ADD_PAYMENT = false;
                                            AddressListFragment.FLAG_ADDRESS_LIST = false;
                                            SharedPreferences.Editor editor = sPref.edit();
                                            count = sPref.getInt("count", 0);
                                            showcartCount = count + 1;
                                            editor.putInt("count", showcartCount);
                                            editor.putInt(PreferenceClass.CART_COUNT, 1).commit();
                                            FLAG_CART_ADD = true;
                                            Intent data = new Intent();
                                            setResult(RESULT_OK, data);
                                        }
                                        finish();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
                }
                else {
                    Toast.makeText(AddToCartActivity.this, R.string.select_all_required_items,Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(WAS_IN_BG) {
            mDatabase = firebaseDatabase.getReference().child(AllConstants.CALCULATION).child(udid);
            mDatabase.keepSynced(true);
            FLAG_CART_ADD = false;
            WAS_IN_BG = false;
            Query query = mDatabase;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        // mDatabase.setValue(null);

                        if(UPDATE_NODE){
                            mDatabase.child(key).setValue(new CalculationModel(key, restaurantMenuItemId,
                                    name, price, "", "1", "0", minOrderPrice,
                                    extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                        }else {

                            mDatabase.child(userId).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                    name, price, "", "1", "0", minOrderPrice,
                                    extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                        }
                    }
                    else {
                        if(UPDATE_NODE){
                            mDatabase.child(key).setValue(new CalculationModel(key, restaurantMenuItemId,
                                    name, price, "", "1", "0", minOrderPrice,
                                    extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                        }else {

                            mDatabase.child(userId).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                    name, price, "", "1", "0",
                                    minOrderPrice, extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }


    @Override
    public void onBackPressed() {
        if(UPDATE_NODE){
            Toast.makeText(AddToCartActivity.this, "Please Select the items", Toast.LENGTH_SHORT).show();
        }
        else {
            super.onBackPressed();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();



        if(UPDATE_CONFIRM || FLAG_CART_ADD){
            UPDATE_CONFIRM = false;
            FLAG_CART_ADD= false;
        }
        else {
            deleteCurrentNode();
        }

        UPDATE_NODE = false;
        RestaurantMenuItemsFragment.FLAG_SUGGESTION = false;
        finish();

    }


    public void increamentDecFunc(){

        increamentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    String presentValStr= incDecTv.getText().toString();
                    presentCount =Integer.parseInt(presentValStr);
                    presentCount++;
                    incDecTv.setText(String.valueOf(presentCount));
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putInt(PreferenceClass.DEALS_QUANTITY, presentCount).commit();

                    if(UPDATE_NODE){
                        mDatabase.child(key).setValue(new CalculationModel(key, restaurantMenuItemId,
                                name, price, "", incDecTv.getText().toString(), "0", minOrderPrice,
                                extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                    }else {
                        mDatabase.child(userId).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                name, price, "", incDecTv.getText().toString(), "0", minOrderPrice, extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                    }
                    loadCalculationDetail();
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
                    if(presentValStr.equalsIgnoreCase(String.valueOf(Integer.parseInt("1")))) {
                        //  Toast.makeText(getContext(),"Can not Less than 1",Toast.LENGTH_LONG).show();
                    }
                    else {
                        presentCount--;
                    }
                    incDecTv.setText(String.valueOf(presentCount));
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putInt(PreferenceClass.DEALS_QUANTITY, presentCount).commit();
                    if(UPDATE_NODE){
                        mDatabase.child(key).setValue(new CalculationModel(key, restaurantMenuItemId,
                                name, price, "", incDecTv.getText().toString(), "0", minOrderPrice,
                                extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                    }else {
                        mDatabase.child(userId).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                name, price, "", incDecTv.getText().toString(), "0", minOrderPrice, extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                    }
                    loadCalculationDetail();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }

    public void getOrderDetailItems(){

        JSONObject params = new JSONObject();
        try {
            if(restaurantMenuItemId !=null) {
                params.put("restaurant_menu_item_id", restaurantMenuItemId);
            }

            params.put("restaurant_id", resId);
        } catch (JSONException e) {
            e.printStackTrace();

        }

        Functions.showLoader(this,false,false);
        ApiRequest.callApi(this, Config.SHOW_MENU_EXTRA_ITEM, params, new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.cancelLoader();

               try {
                    JSONObject  jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));
                    count  = Integer.parseInt(jsonResponse.optString("count"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonArray = json.getJSONArray("msg");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject allJsonObject = jsonArray.getJSONObject(i);
                            JSONObject restaurantMenuExtraSection = allJsonObject.getJSONObject("RestaurantMenuExtraSection");
                            JSONArray restaurantMenuExtraItem = restaurantMenuExtraSection.getJSONArray("RestaurantMenuExtraItem");
                            JSONObject restaurant = allJsonObject.getJSONObject("Restaurant");
                            JSONObject currency = restaurant.getJSONObject("Currency");
                            String symbol = currency.optString("symbol");

                            CartParentModel menuItemModel = new CartParentModel();

                            menuItemModel.setParentName(restaurantMenuExtraSection.optString("name"));
                            menuItemModel.setRequired(restaurantMenuExtraSection.optString("required"));
                            menuItemModel.setSymbol(symbol);

                            listDataHeader.add(menuItemModel);
                            listChildData = new ArrayList<>();

                            for (int j = 0; j < restaurantMenuExtraItem.length(); j++) {

                                JSONObject alljsonJsonObject2 = restaurantMenuExtraItem.getJSONObject(j);

                                CartChildModel menuItemExtraModel = new CartChildModel();

                                menuItemExtraModel.setChild_item_name(alljsonJsonObject2.optString("name"));
                                menuItemExtraModel.setChild_item_price(alljsonJsonObject2.optString("price"));
                                menuItemExtraModel.setExtra_item_id(alljsonJsonObject2.optString("id"));
                                menuItemExtraModel.setPos(j);
                                menuItemExtraModel.setSymbol(symbol);

                                listChildData.add(menuItemExtraModel);

                            }
                            listChild.add(listChildData);

                        }

                        listAdapter.notifyDataSetChanged();

                        for (int l = 0; l < listAdapter.getGroupCount(); l++)
                            cartExpandableListView.expandGroup(l);

                    }

                }catch (Exception e){
                    e.getMessage();
                }
            }
        });

    }

    public void upDateNotify(ArrayList<CartChildModel> child){
        for(int i=0; i<child.size(); i++) {
            child.get(i).setCheckeddd(false);
        }
    }

    public void addNewNode(String id, String name,String price){
        HashMap<String, String> names = new HashMap<>();
        names.put("menu_extra_item_id", id);
        names.put("menu_extra_item_name",name);
        names.put("menu_extra_item_price",price);
        names.put("menu_extra_item_quantity", incDecTv.getText().toString());
        extraItem.add(names);
        if(UPDATE_NODE){
            mDatabase.child(key).setValue(new CalculationModel(key, restaurantMenuItemId,
                    this.name, this.price, "", "1", "0", minOrderPrice,
                    extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
        }else {
            mDatabase.child(userId).setValue(new CalculationModel(userId, restaurantMenuItemId,
                    this.name, this.price, "", incDecTv.getText().toString(), "0",
                    minOrderPrice, extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
        }
    }

    public void deleteNewNode(final String id){

        DatabaseReference query = mDatabase;//mDatabase;//FirebaseDatabase.getInstance().getReference();
        Query lastQuery = query.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                    Collection<Object> values = td.values();

                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(values);

                        for (int a = 0; a < jsonArray.length(); a++) {

                            JSONObject allJsonObject = jsonArray.getJSONObject(a);
                            JSONArray extraItemArray = allJsonObject.getJSONArray("extraItem");

                            for (int b = 0; b < extraItemArray.length(); b++) {

                                JSONObject jsonObject = extraItemArray.getJSONObject(b);
                                String menuExtraItemObj = jsonObject.optString("menu_extra_item_id");

                                if (menuExtraItemObj.equalsIgnoreCase(id)) {

                                    try {
                                        extraItem.remove(b);
                                        if (UPDATE_NODE) {
                                            mDatabase.child(key).setValue(new CalculationModel(key, restaurantMenuItemId,
                                                    name, price, "", "1", "0", minOrderPrice,
                                                    extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                                        } else {
                                            mDatabase.child(userId).setValue(new CalculationModel(userId, restaurantMenuItemId,
                                                    name, price, "", incDecTv.getText().toString(), "0",
                                                    minOrderPrice, extraItem, instText.getText().toString(), resId, resName, symbol, desc, resFee, resTax));
                                        }
                                    } catch (IndexOutOfBoundsException e) {
                                        e.getMessage();
                                    }

                                }

                            }
                            loadCalculationDetail();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void loadCalculationDetail(){
        if(!FIRST_TIME_LOADER) {
            Functions.showLoader(this,false,false);
            FIRST_TIME_LOADER = true;
        }
        DatabaseReference query = mDatabase;
        Query lastQuery = query.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Functions.cancelLoader();

                if(dataSnapshot.exists()){
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                if(td!=null) {
                    Collection<Object> value = td.values();

                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(value);
                        grandTotal = 0.0;
                        totalExtraItemPrice = 0.0;
                        menuExtraItemObj = 0.0;
                        itemPrice = 0.0;
                        for (int a = 0; a < jsonArray.length(); a++) {

                            JSONObject allJsonObject = jsonArray.getJSONObject(a);
                            itemPrice = Double.parseDouble(allJsonObject.optString("mPrice"));

                            grandTotal = totalExtraItemPrice + itemPrice * Double.valueOf(incDecTv.getText().toString());
                           grandTotalPriceTv.setText(symbol+grandTotal);
                            if (allJsonObject.getJSONArray("extraItem") != null) {

                                JSONArray extraItemArray = allJsonObject.getJSONArray("extraItem");

                                for (int b = 0; b < extraItemArray.length(); b++) {

                                    JSONObject jsonObject = extraItemArray.getJSONObject(b);
                                    menuExtraItemObj = Double.parseDouble(jsonObject.optString("menu_extra_item_price"));

                                    totalExtraItemPrice = totalExtraItemPrice + menuExtraItemObj;

                                }

                                grandTotal = Functions.roundoffDecimal((totalExtraItemPrice + itemPrice) * Double.valueOf(incDecTv.getText().toString()));
                                grandTotalPriceTv.setText(symbol+grandTotal);

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }catch (Exception e){

                    }
                }
            }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void deleteCurrentNode(){

        DatabaseReference query = mDatabase;
        Query lastQuery = query.orderByKey().limitToLast(1);

        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){

                    dataSnapshot1.getRef().setValue(null);
                }
            }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteNodeExpectLast(){
       final Query query = mDatabase.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{

                    if (dataSnapshot.exists()) {
                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                        Collection<Object> values = td.values();

                        JSONArray jsonArray = new JSONArray(values);

                        Log.d(AllConstants.tag,jsonArray.toString());

                        Log.d(AllConstants.tag,""+jsonArray.length());

                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=jsonArray.optJSONObject(i);
                            if(userId!=null && !userId.equals(jsonObject.optString("key")))
                            mDatabase.child(jsonObject.optString("key")).setValue(null);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addToCart.performClick();
                            }
                        },500);


                    }else {
                        addToCart.performClick();
                    }
                }catch (Exception e){
                }


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void showDialogIfChangeRest(){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(AddToCartActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(AddToCartActivity.this);
        }
        builder.setTitle(R.string.changing_restuarents)
                .setMessage(R.string.whould_you_like_to_change_order)
                .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        deleteNodeExpectLast();

                        SharedPreferences.Editor editor = sPref.edit();
                        editor.putInt("count",0).commit();

                        TabLayout.Tab tab = tabLayout.getTabAt(2); // fourth tab
                        View tabView = tab.getCustomView();
                        TextView badgeText = (TextView) tabView.findViewById(R.id.tab_badge);
                        badgeText.setVisibility(View.GONE);
                        badgeText.setText("0");

                    }


                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

}

