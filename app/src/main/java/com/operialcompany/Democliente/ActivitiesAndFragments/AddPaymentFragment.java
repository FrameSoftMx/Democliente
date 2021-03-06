package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.operialcompany.Democliente.Adapters.CreditCardDetailAdapter;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.CardDetailModel;
import com.operialcompany.Democliente.R;
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
public class AddPaymentFragment extends RootFragment {

    RelativeLayout addPaymentMethodDiv, cashOnDeliveryDiv;
    ImageView backIcon;
    public static boolean FLAG_FRAGMENT,FLAG_PAYMENT_METHOD,FLAG_ADD_PAYMENT;

    RecyclerView.LayoutManager recyclerViewlayoutManager;
    CreditCardDetailAdapter recyclerViewadapter;
    RecyclerView cardRecyclerView;
    SharedPreferences sharedPreferences;

    ArrayList<CardDetailModel> cardDetailModelArrayList;
    CamomileSpinner pbHeaderProgress;

    RelativeLayout transparentLayer,progressDialog;

    View view;
    Context context;



    public AddPaymentFragment (){
    }

    FragmentCallback fragment_callback;
    public AddPaymentFragment (FragmentCallback fragment_callback){
        this.fragment_callback=fragment_callback;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view = inflater.inflate(R.layout.add_payment_fragment, container, false);
         context=getContext();

         sharedPreferences = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);

        initUI(view);
        getPaymentList();


        return view;
    }


    public void initUI(View v){
        progressDialog = v.findViewById(R.id.progressDialog_payment);
        transparentLayer = v.findViewById(R.id.transparent_layer_payment);
        cashOnDeliveryDiv = v.findViewById(R.id.cash_on_delivery_div);
        addPaymentMethodDiv = v.findViewById(R.id.add_payment_method_div);
        pbHeaderProgress = v.findViewById(R.id.paymentListProgress);
        pbHeaderProgress.start();
        backIcon =v.findViewById(R.id.back_icon);
        cardRecyclerView = v.findViewById(R.id.paymenth_recycler);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());
        cardRecyclerView.setLayoutManager(recyclerViewlayoutManager);



        addPaymentMethodDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddPaymentDetailFragment addPaymentDetailFragment = new AddPaymentDetailFragment(new FragmentCallback() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        getPaymentList();
                    }
                });
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.add(R.id.add_payment_main_container, addPaymentDetailFragment,"parent").commit();

                FLAG_FRAGMENT = true;
            }
        });

        if(fragment_callback!=null){
            cashOnDeliveryDiv.setVisibility(View.VISIBLE);
        }
        else {
            cashOnDeliveryDiv.setVisibility(View.GONE);
        }

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   getActivity().onBackPressed();

            }
        });

        cashOnDeliveryDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fragment_callback!=null){
                    Bundle bundle=new Bundle();
                    bundle.putString("card_name","");
                    bundle.putString("card_number","Cash on delivery");
                    bundle.putString("card_id","0");
                    fragment_callback.onResponce(bundle);
                    getActivity().onBackPressed();
                }
            }
        });


    }

    public void getPaymentList()  {

        cardDetailModelArrayList = new ArrayList<>();
        String user_id = sharedPreferences.getString(PreferenceClass.pre_user_id,"");

        JSONObject params = new JSONObject();

        try {
            params.put("user_id",user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        ApiRequest.callApi(context, Config.GET_PAYMENT_METHID, params, new Callback() {
            @Override
            public void onResponce(String resp) {
                try {
                    JSONObject  jsonResponse = new JSONObject(resp);

                    int code_id  = Integer.parseInt(jsonResponse.optString("code"));


                    if(code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject json1 = jsonarray.getJSONObject(i);

                            CardDetailModel cardDetailModel = new CardDetailModel();
                            cardDetailModel.setCard_name(json1.optString("brand"));
                            cardDetailModel.setCard_number(json1.optString("last4"));

                            JSONObject payment_id_JsonObject = json1.getJSONObject("PaymentMethod");
                            cardDetailModel.setPayment_id(payment_id_JsonObject.optString("id"));

                            cardDetailModelArrayList.add(cardDetailModel);

                        }

                        if(cardDetailModelArrayList!=null) {
                            recyclerViewadapter = new CreditCardDetailAdapter(cardDetailModelArrayList, getActivity());
                            cardRecyclerView.setAdapter(recyclerViewadapter);
                            recyclerViewadapter.notifyDataSetChanged();

                            recyclerViewadapter.setOnItemClickListner(new CreditCardDetailAdapter.OnItemClickListner() {
                                @Override
                                public void OnItemClicked(View view, int position) {

                                    CardDetailModel cardDetailModel=cardDetailModelArrayList.get(position);
                                    Bundle bundle=new Bundle();
                                    bundle.putString("card_name",cardDetailModel.getCard_name());
                                    bundle.putString("card_number","**** **** **** " + cardDetailModel.getCard_number());
                                    bundle.putString("card_id",cardDetailModel.getPayment_id());

                                    if(fragment_callback!=null){
                                        fragment_callback.onResponce(bundle);
                                        getActivity().onBackPressed();
                                    }

                                }
                            });

                        }

                    }

                    else {
                        pbHeaderProgress.setVisibility(View.GONE);
                    }
                }catch (JSONException e){
                    e.getCause();

                }


                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);
            }
        });


    }

}
