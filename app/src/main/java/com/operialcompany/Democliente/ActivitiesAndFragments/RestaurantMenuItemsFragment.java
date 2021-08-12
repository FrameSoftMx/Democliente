package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.operialcompany.Democliente.Adapters.RestaurantMenuAdapter;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.RestaurantChildModel;
import com.operialcompany.Democliente.Models.RestaurantParentModel;
import com.operialcompany.Democliente.Models.RestaurantsModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.CustomExpandableListView;
import com.operialcompany.Democliente.Utils.FontHelper;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gmail.samehadar.iosdialog.CamomileSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by qboxus on 10/18/2019.
 */

public class RestaurantMenuItemsFragment extends RootFragment {

    ImageView backIcon, aboutIcon, closeSuggestion;
    RelativeLayout aboutRestaurantDiv, reviewRestaurantDiv, suggestionDiv, suggestionTxt, restOpenDiv;
    CustomExpandableListView expandableListView, restaurantMenuItemListSuggestion;
    TextView rastaurantMenuItemTitleTv, restaurantNameTv, salogonTv, milesDescTv;

    SimpleDraweeView restaurantImg, coverImage;

    SharedPreferences sharedPreferences;
    RestaurantMenuAdapter restaurantMenuAdapter;
    ArrayList<RestaurantParentModel> listDataHeader;
    ArrayList<RestaurantChildModel> listChildData;
    private ArrayList<ArrayList<RestaurantChildModel>> listChild;
    CamomileSpinner resMenuItemProgress;
    RatingBar rating;
    SearchView searchView;
    RelativeLayout upper_header;
    LinearLayout about_div;
    String udid;

    public static boolean FLAG_SUGGESTION;


    RelativeLayout transparentLayer,progressDialog;
    public static final int  PERMISSION_DATA_CART_ADED = 5;
    String restarant_open;


    View view;
    Context context;

    RestaurantsModel item_model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.restaurant_menu_items_fragment, container, false);
        context=getContext();

        Bundle bundle=getArguments();
        if(bundle!=null){
            item_model=(RestaurantsModel) bundle.get("data");
        }

        FrameLayout frameLayout = view.findViewById(R.id.resaurant_menu_items_main_layout);
        FontHelper.applyFont(getContext(),frameLayout.getRootView(), AllConstants.verdana);

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        udid = sharedPreferences.getString(PreferenceClass.UDID,"");
        initUI(view);

        expandableListView = (CustomExpandableListView) view.findViewById(R.id.restaurant_menu_item_list);
        expandableListView.setExpanded(true);
        expandableListView.setGroupIndicator(null);

        restaurantMenuItemListSuggestion = (CustomExpandableListView)view.findViewById(R.id.restaurant_menu_item_list_suggestion);
        restaurantMenuItemListSuggestion.setExpanded(true);
        restaurantMenuItemListSuggestion.setGroupIndicator(null);


       restaurantMenuDetailForListview();



        return view;
    }


    public void initUI(View v){

     progressDialog = v.findViewById(R.id.progressDialog);
     transparentLayer = v.findViewById(R.id.transparent_layer);


        suggestionTxt = v.findViewById(R.id.suggestion_txt);
        closeSuggestion = v.findViewById(R.id.close_suggestion);
        suggestionDiv = v.findViewById(R.id.suggestion_div);
        suggestionDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 FLAG_SUGGESTION = true;

                restaurantMenuDetailForListview();

                closeSuggestion.setVisibility(View.VISIBLE);
                suggestionTxt.setVisibility(View.VISIBLE);
                suggestionDiv.setVisibility(View.GONE);

                upper_header.setVisibility(View.GONE);
                reviewRestaurantDiv.setVisibility(View.GONE);
                aboutRestaurantDiv.setVisibility(View.GONE);
                about_div.setVisibility(View.GONE);
            }
        });

        closeSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FLAG_SUGGESTION = false;
                upper_header.setVisibility(View.VISIBLE);
                reviewRestaurantDiv.setVisibility(View.VISIBLE);
                aboutRestaurantDiv.setVisibility(View.VISIBLE);
                about_div.setVisibility(View.VISIBLE);
                closeSuggestion.setVisibility(View.GONE);
                suggestionTxt.setVisibility(View.GONE);
                suggestionDiv.setVisibility(View.VISIBLE);

                restaurantMenuDetailForListview();



            }
        });


     milesDescTv = v.findViewById(R.id.miles_desc_tv);
     searchView = v.findViewById(R.id.floating_search_view);
     String txt="<font color = #dddddd>" + getString(R.string.search_restuarents_menu) + "</font>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
              searchView.setQueryHint(Html.fromHtml(txt, Html.FROM_HTML_MODE_LEGACY));
        } else {
            searchView.setQueryHint(Html.fromHtml(txt));
        }
     TextView searchText = (TextView)
             v.findViewById(R.id.search_src_text);
     searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
     searchText.setPadding(0,0,0,0);
     LinearLayout searchEditFrame = (LinearLayout) searchView.findViewById(R.id.search_edit_frame); // Get the Linear Layout
     ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = 5;
     search(searchView);

     upper_header = v.findViewById(R.id.upper_header);
     about_div = v.findViewById(R.id.about_div);

     aboutIcon = v.findViewById(R.id. about_icon);

     resMenuItemProgress = v.findViewById(R.id.res_menu_item_progress);
     resMenuItemProgress.start();
     rating = v.findViewById(R.id.rating);
     rastaurantMenuItemTitleTv = v.findViewById(R.id.rastaurant_menu_item_title_tv);
     restaurantNameTv = v.findViewById(R.id.restaurant_name_tv);
     salogonTv = v.findViewById(R.id.salogon_tv);
     restaurantImg = v.findViewById(R.id.restaurant_image);
     coverImage = v.findViewById(R.id.cover_image);


     rating.setRating(Float.parseFloat(item_model.restaurant_avgRating));

     rastaurantMenuItemTitleTv.setText(item_model.restaurant_name);
     restaurantNameTv.setText(item_model.restaurant_name);
     salogonTv.setText(item_model.restaurant_salgon);

     if(item_model.deliveryFee_Range.equalsIgnoreCase("0")){
         milesDescTv.setText(item_model.restaurant_currency + " " + item_model.delivery_fee_per_km + " /km");
     }
        else {
         milesDescTv.setText(item_model.restaurant_currency + " " + item_model.delivery_fee_per_km + " /km- free delivery over " + item_model.restaurant_currency + " " + item_model.min_order_price + " within " + item_model.deliveryFee_Range + " km");
     }


        Uri uri = Uri.parse(Config.imgBaseURL+item_model.restaurant_image);
        restaurantImg.setImageURI(uri);


     aboutIcon.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             new MaterialDialog.Builder(getContext())
                     .title(item_model.restaurant_name)
                     .content(item_model.restaurant_about)
                     .positiveText("OK")
                     .show();
         }
     });

        backIcon = v.findViewById(R.id.back_icon_menu_option);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               getActivity().onBackPressed();

            }
        });

        aboutRestaurantDiv = (RelativeLayout)v.findViewById(R.id.about_restaurant_div);
        aboutRestaurantDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RestaurantDealsFragment restaurantDealsFragment = new RestaurantDealsFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",item_model);
                restaurantDealsFragment.setArguments(bundle);
                transaction.addToBackStack(null);
                transaction.replace(R.id.resaurant_menu_items_main_layout, restaurantDealsFragment,"ParentFragment_MenuItems").commit();

            }
        });

     reviewRestaurantDiv = (RelativeLayout)v.findViewById(R.id.review_restaurant_div);
     reviewRestaurantDiv.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Fragment reviewListFragment = new ReviewListFragment();
             FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
             Bundle bundle=new Bundle();
             bundle.putSerializable("data",item_model);
             reviewListFragment.setArguments(bundle);
             transaction.addToBackStack(null);
             transaction.replace(R.id.resaurant_menu_items_main_layout, reviewListFragment,"ParentFragment_MenuItems").commit();
         }
     });


     upper_header.setVisibility(View.VISIBLE);
     reviewRestaurantDiv.setVisibility(View.VISIBLE);
     aboutRestaurantDiv.setVisibility(View.VISIBLE);
     about_div.setVisibility(View.VISIBLE);

     restOpenDiv = v.findViewById(R.id.rest_open_div);




 }

    @Override
    public void onResume() {
        super.onResume();
        upper_header.setVisibility(View.VISIBLE);
        reviewRestaurantDiv.setVisibility(View.VISIBLE);
        aboutRestaurantDiv.setVisibility(View.VISIBLE);
        about_div.setVisibility(View.VISIBLE);
    }


    private void search(final androidx.appcompat.widget.SearchView searchView) {


        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {



                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equalsIgnoreCase("")){
                    FLAG_SUGGESTION = false;
                    upper_header.setVisibility(View.VISIBLE);
                    reviewRestaurantDiv.setVisibility(View.VISIBLE);
                    aboutRestaurantDiv.setVisibility(View.VISIBLE);
                    about_div.setVisibility(View.VISIBLE);

                    restaurantMenuDetailForListview();
                }
                else {
                    if (restaurantMenuAdapter != null)
                        restaurantMenuAdapter.getFilter().filter(newText);
                    upper_header.setVisibility(View.GONE);
                    reviewRestaurantDiv.setVisibility(View.GONE);
                    aboutRestaurantDiv.setVisibility(View.GONE);
                    about_div.setVisibility(View.GONE);
                    closeSuggestion.setVisibility(View.GONE);
                    suggestionTxt.setVisibility(View.GONE);


                }
                return true;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {

                Log.i("SearchView:", "onClose");
                searchView.onActionViewCollapsed();
                upper_header.setVisibility(View.VISIBLE);
                reviewRestaurantDiv.setVisibility(View.VISIBLE);
                aboutRestaurantDiv.setVisibility(View.VISIBLE);
                about_div.setVisibility(View.VISIBLE);
                for(int m=0; m < restaurantMenuAdapter.getGroupCount(); m++)
                    expandableListView.expandGroup(m);

                return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();

            }
        });

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @Override
            public void onViewDetachedFromWindow(View arg0) {

                upper_header.setVisibility(View.VISIBLE);
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
            }
        });
    }


    public void restaurantMenuDetailForListview(){

        if(FLAG_SUGGESTION){
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return false; // This way the expander cannot be collapsed
                }
            });
        }
        else {
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return true; // This way the expander cannot be collapsed
                }
            });
        }


        expandableListView.setVisibility(View.VISIBLE);
        listDataHeader = new ArrayList<>();
        listChild = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("id",item_model.restaurant_id);
            jsonObject.put("current_time",formattedDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        ApiRequest.callApi(context, Config.SHOW_RESTAURANT_MENU, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                try {
                    JSONObject jsonResponse = new JSONObject(resp);


                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonArray = json.getJSONArray("msg");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject allJsonObject1 = jsonArray.getJSONObject(i);
                            JSONObject currency = allJsonObject1.getJSONObject("Currency");
                            String currency_symbol = currency.optString("symbol");
                            JSONObject coverImage = allJsonObject1.getJSONObject("Restaurant");
                            String coverImgURL = coverImage.optString("cover_image");
                            restarant_open = coverImage.optString("open");


                            Uri uri = Uri.parse(Config.imgBaseURL+coverImgURL);
                            RestaurantMenuItemsFragment.this.coverImage.setImageURI(uri);


                            final JSONArray resMenuArray = allJsonObject1.getJSONArray("RestaurantMenu");

                            for (int j =0; j<resMenuArray.length();j++){

                                JSONObject resMenuAllObj = resMenuArray.getJSONObject(j);

                                RestaurantParentModel restaurantParentModel = new RestaurantParentModel();

                                restaurantParentModel.setTitle(resMenuAllObj.optString("name"));
                                restaurantParentModel.setSub_title(resMenuAllObj.optString("description"));
                                restaurantParentModel.setImage(resMenuAllObj.optString("image"));

                                listDataHeader.add(restaurantParentModel);

                                listChildData = new ArrayList<>();

                                JSONArray menuItemArray = resMenuAllObj.getJSONArray("RestaurantMenuItem");
                                Log.d("Count",String.valueOf(menuItemArray.length()));
                                for(int k=0;k<menuItemArray.length();k++){
                                    JSONObject menuItemArrayObj = menuItemArray.getJSONObject(k);

                                    RestaurantChildModel restaurantChildModel = new RestaurantChildModel();


                                    restaurantChildModel.restaurant_menu_item_id=menuItemArrayObj.optString("id");
                                    restaurantChildModel.child_title=menuItemArrayObj.optString("name");
                                    restaurantChildModel.child_sub_title=menuItemArrayObj.optString("description");
                                    restaurantChildModel.order_detail=menuItemArrayObj.optString("out_of_order");
                                    restaurantChildModel.price=menuItemArrayObj.optString("price");
                                    restaurantChildModel.image=menuItemArrayObj.optString("image");
                                    restaurantChildModel.currency_symbol=currency_symbol;

                                    listChildData.add(restaurantChildModel);


                                }
                                listChild.add(listChildData);

                            }


                            restaurantMenuItemListSuggestion.setVisibility(View.GONE);
                            restaurantMenuAdapter = new RestaurantMenuAdapter(getContext(), listDataHeader, listChild);
                            expandableListView.setAdapter(restaurantMenuAdapter);


                            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                                @Override
                                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                                    FLAG_SUGGESTION = false;

                                    return false;
                                }
                            });



                            if(restarant_open.equalsIgnoreCase("1")) {

                                expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                    @Override
                                    public boolean onChildClick(ExpandableListView expandableListView, View view,
                                                                int groupPosition, int childPosition, long l) {
                                        RestaurantChildModel item = (RestaurantChildModel) restaurantMenuAdapter.getChild(groupPosition, childPosition);


                                        if (item.order_detail.equalsIgnoreCase("1")) {

                                        } else {
                                            Intent intent = new Intent(getActivity(), AddToCartActivity.class);

                                            intent.putExtra("rest_model",item_model);
                                            intent.putExtra("rest_child_model",item);

                                            getActivity().startActivityForResult(intent, PERMISSION_DATA_CART_ADED);

                                        }

                                        return false;
                                    }
                                });
                            }
                            else {
                                restOpenDiv.setVisibility(View.VISIBLE);
                                aboutRestaurantDiv.setClickable(false);
                                expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                    @Override
                                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                        return true;
                                    }
                                });

                            }
                        }

                        if(!FLAG_SUGGESTION){
                            for (int m = 0; m < restaurantMenuAdapter.getGroupCount(); m++)
                                expandableListView.expandGroup(m);

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
