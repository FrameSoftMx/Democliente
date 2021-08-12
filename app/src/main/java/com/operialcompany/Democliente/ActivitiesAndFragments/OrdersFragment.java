package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.operialcompany.Democliente.Adapters.OrderAdapter;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.OrderModelClass;
import com.operialcompany.Democliente.R;
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

public class OrdersFragment extends RootFragment {

    ImageView filterSearch;
    public static boolean statusinactive;

    SharedPreferences sPre;

    RecyclerView orderHistoryRecyclerview;

    RecyclerView.LayoutManager recyclerViewlayoutManager;
    OrderAdapter recyclerViewadapter;

    LinearLayout recyclerViewRestaurant;
    CamomileSpinner orderProgressBar;
    SwipeRefreshLayout refreshLayout;

    boolean is_current_orders=true;


    ArrayList<OrderModelClass> orderArrayList;
   RelativeLayout noJobDiv;
    TextView titleTv;
    RelativeLayout transparentLayer,progressDialog;


    View view;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.orders_fragment, container, false);
        context=getContext();

        FrameLayout frameLayout = view.findViewById(R.id.order_fragment_container);
        FontHelper.applyFont(getContext(),frameLayout, AllConstants.verdana);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        initUI(view);
        sPre = getContext().getSharedPreferences(PreferenceClass.user,getContext().MODE_PRIVATE);
        orderHistoryRecyclerview = view.findViewById(R.id.order_history_recyclerview);
        orderProgressBar = view.findViewById(R.id.orderProgress);
        orderProgressBar.start();

        orderHistoryRecyclerview.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());
        orderHistoryRecyclerview.setLayoutManager(recyclerViewlayoutManager);


        getAllOrderParser();


        return view;

    }




    private void initUI(View v){

        progressDialog = v.findViewById(R.id.progressDialog);
        transparentLayer = v.findViewById(R.id.transparent_layer);

        titleTv = v.findViewById(R.id.title_tv);
        titleTv.setText(getResources().getString(R.string.history));
        noJobDiv = v.findViewById(R.id.no_job_div);
        refreshLayout = v.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getAllOrderParser();

                refreshLayout.setRefreshing(false);

            }
        });

       recyclerViewRestaurant = v.findViewById(R.id.recycler_view_restaurant );
                filterSearch = v.findViewById(R.id.filter_search);
        filterSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialogbox();
            }
        });


    }



    private void getAllOrderParser(){


        orderArrayList = new ArrayList<>();
        String user_id = sPre.getString(PreferenceClass.pre_user_id,"0");

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("user_id",user_id);
            if(is_current_orders)
                jsonObject.put("status","1");
            else
                jsonObject.put("status","2");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        ApiRequest.callApi(context, Config.SHOW_ORDERS, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                try {
                    JSONObject  jsonResponse = new JSONObject(resp);

                      int code_id  = Integer.parseInt(jsonResponse.optString("code"));

                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");


                        if(jsonarray.length()==0){
                            recyclerViewRestaurant.setVisibility(View.GONE);

                        }
                        else {
                            recyclerViewRestaurant.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject json1 = jsonarray.getJSONObject(i);

                            JSONObject jsonObjOrder = json1.getJSONObject("Order");
                            JSONObject jsonObjCurrency = jsonObjOrder.getJSONObject("Currency");
                            OrderModelClass orderModelClass = new OrderModelClass();
                            orderModelClass.setStatus(jsonObjCurrency.optString("status"));


                            orderModelClass.setCurrency_symbol(jsonObjCurrency.optString("symbol"));
                            orderModelClass.setOrder_price(jsonObjOrder.optString("price"));
                            orderModelClass.setInstructions(jsonObjOrder.optString("instructions"));
                            orderModelClass.setRestaurant_name(jsonObjOrder.optString("name"));
                            orderModelClass.setOrder_quantity(jsonObjOrder.optString("quantity"));
                            orderModelClass.setOrder_id(jsonObjOrder.optString("id"));
                            orderModelClass.setOrder_created(jsonObjOrder.optString("created"));
                            orderModelClass.setDelivery(jsonObjOrder.optString("delivery"));
                            orderModelClass.setDeal_id(jsonObjOrder.optString("deal_id"));
                            if(jsonObjOrder.getJSONArray("OrderMenuItem")!=null && jsonObjOrder.getJSONArray("OrderMenuItem").length()>0) {
                                JSONArray jsonarrayOrder = jsonObjOrder.getJSONArray("OrderMenuItem");
                                JSONObject jsonObjectMenu = jsonarrayOrder.getJSONObject(0);

                                orderModelClass.setOrder_menu_id(jsonObjectMenu.optString("id"));
                                orderModelClass.setOrder_name(jsonObjectMenu.optString("name"));

                                JSONArray  jsonarrayExtraOrder = jsonObjectMenu.getJSONArray("OrderMenuExtraItem");

                                if(jsonarrayExtraOrder!=null && jsonarrayExtraOrder.length()>0) {
                                    JSONObject jsonObjectExtraMenu = jsonarrayExtraOrder.getJSONObject(0);
                                    orderModelClass.setOrder_extra_item_name(jsonObjectExtraMenu.optString("name"));

                                }

                            }

//                            if(is_current_orders && (orderModelClass.getStatus().equalsIgnoreCase("1") ||
//                                    orderModelClass.getStatus().equalsIgnoreCase("3")))
//                            {
//
//
//                            }
//
//
//                            if(!is_current_orders && orderModelClass.getStatus().equalsIgnoreCase("2") )
//                            {
//
//                                orderArrayList.add(orderModelClass);
//                            }
//

                            orderArrayList.add(orderModelClass);
                        }

                        if (orderArrayList!=null) {

                            if(orderArrayList.size()>0){
                                noJobDiv.setVisibility(View.GONE);
                            }
                            else if(orderArrayList.size()==0) {
                                noJobDiv.setVisibility(View.VISIBLE);
                            }

                            recyclerViewadapter = new OrderAdapter(orderArrayList, getActivity());
                            orderHistoryRecyclerview.setAdapter(recyclerViewadapter);
                            recyclerViewadapter.notifyDataSetChanged();

                        }
                        recyclerViewadapter.setOnItemClickListner(new OrderAdapter.OnItemClickListner() {
                            @Override
                            public void OnItemClicked(View view, int position) {

                                SharedPreferences.Editor editor = sPre.edit();
                                editor.putString(PreferenceClass.ORDER_HEADER,orderArrayList.get(position).getOrder_name());
                                editor.putString(PreferenceClass.ORDER_ID,orderArrayList.get(position).getOrder_id());
                                editor.putString(PreferenceClass.ORDER_INS,orderArrayList.get(position).getInstructions());
                                editor.putString("delivery_",orderArrayList.get(position).getDelivery());
                                editor.putString(PreferenceClass.ORDER_QUANTITY,orderArrayList.get(position).getOrder_quantity());
                                editor.commit();

                                Fragment restaurantMenuItemsFragment = new OrderDetailFragment();
                                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                transaction.addToBackStack(null);
                                transaction.add(R.id.order_fragment_container, restaurantMenuItemsFragment,"ParentFragment").commit();

                            }
                        });


                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }


                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);
                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);

            }
        });



    }

    public void customDialogbox(){

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialoge_box);
        dialog.setTitle(R.string.order_filter);


        RelativeLayout cancelDiv = (RelativeLayout) dialog.findViewById(R.id.forth);
        RelativeLayout currentOrderDiv = (RelativeLayout) dialog.findViewById(R.id.second);
        RelativeLayout pastOrderDiv = (RelativeLayout) dialog.findViewById(R.id.third);
        TextView first_tv = (TextView)dialog.findViewById(R.id.first_tv);
        TextView second_tv = (TextView)dialog.findViewById(R.id.second_tv);
        first_tv.setText(R.string.current_orders);
        second_tv.setText(R.string.past_orders);

        currentOrderDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusinactive = false;
                is_current_orders = true;
                getAllOrderParser();
                dialog.dismiss();
            }
        });

        pastOrderDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusinactive = true;
                is_current_orders = false;
               getAllOrderParser();
                dialog.dismiss();
            }
        });

        // if button is clicked, close the custom dialog
        cancelDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
