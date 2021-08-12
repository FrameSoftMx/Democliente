package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Models.RestaurantsModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.operialcompany.Democliente.Adapters.ShowReviewListAdapter;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.RatingListModel;
import com.operialcompany.Democliente.Utils.FontHelper;
import com.operialcompany.Democliente.Utils.TabLayoutUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class ReviewListFragment extends RootFragment {

    ImageView backIcon;
    SharedPreferences sPref;
    ArrayList<RatingListModel> listDataReview;

    RecyclerView.LayoutManager recyclerViewlayoutManager;
    ShowReviewListAdapter recyclerViewadapter;
    RecyclerView reviewRecyclerView;
    SwipeRefreshLayout refreshLayout;

    CamomileSpinner progressBar;
    RelativeLayout transparentLayer,progressDialog;

    TextView totalReviewTv;


    View view;
    Context context;


    RestaurantsModel itemModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.review_list_fragment, container, false);
        context=getContext();

        Bundle bundle=getArguments();
        if(bundle!=null){
            itemModel =(RestaurantsModel) bundle.get("data");
        }

        FrameLayout frameLayout = view.findViewById(R.id.review_list_main);

        FontHelper.applyFont(getContext(),frameLayout, AllConstants.verdana);

        sPref = getContext().getSharedPreferences(PreferenceClass.user, Context.MODE_PRIVATE);
        totalReviewTv = view.findViewById(R.id.total_review_tv);
        initUI(view);
        showRatingList();

        return view;
    }

    public void initUI(View v){

        reviewRecyclerView = v.findViewById(R.id.review_list_recycler_view);
        progressBar = v.findViewById(R.id.reviewProgress);
         progressBar.start();
        progressDialog = v.findViewById(R.id.progressDialog);
        transparentLayer = v.findViewById(R.id.transparent_layer);

        reviewRecyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());
        reviewRecyclerView.setLayoutManager(recyclerViewlayoutManager);

        refreshLayout = v.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showRatingList();

                refreshLayout.setRefreshing(false);
            }
        });



        backIcon = v.findViewById(R.id.back_icon_review_list);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                 getActivity().onBackPressed();

            }
        });


    }


    public void showRatingList(){


          listDataReview = new ArrayList<>();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("restaurant_id", itemModel.restaurant_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,false);
        transparentLayer.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);

        ApiRequest.callApi(context, Config.SHOE_TOTAL_RATINGS, jsonObject, new Callback() {
            @Override
            public void onResponce(String resp) {

                try {
                    JSONObject jsonResponse = new JSONObject(resp);

                    int code_id = Integer.parseInt(jsonResponse.optString("code"));

                    if (code_id == 200) {

                        JSONObject json = new JSONObject(jsonResponse.toString());
                        JSONArray jsonarray = json.getJSONArray("msg");

                        for (int i = 0; i < jsonarray.length(); i++) {

                            JSONObject jsonObject1 = jsonarray.getJSONObject(i);
                            JSONArray commentArray = jsonObject1.getJSONArray("comments");

                            for (int j = 0; j < commentArray.length(); j++) {

                                JSONObject jsonObject2 = commentArray.getJSONObject(j);
                                JSONObject restaurantRating;

                                restaurantRating = jsonObject2.getJSONObject("RestaurantRating");

                                JSONObject userInfo = jsonObject2.getJSONObject("UserInfo");

                                RatingListModel ratingListModel = new RatingListModel();

                                ratingListModel.setComment(restaurantRating.optString("comment"));
                                ratingListModel.setCreated(restaurantRating.optString("created"));
                                ratingListModel.setRating(restaurantRating.optString("star"));
                                ratingListModel.setF_name(userInfo.optString("first_name"));
                                ratingListModel.setL_name(userInfo.optString("last_name"));

                                listDataReview.add(ratingListModel);
                            }

                        }

                        if (listDataReview != null && listDataReview.size()>0) {
                            view.findViewById(R.id.no_job_div).setVisibility(View.GONE);

                            recyclerViewadapter = new ShowReviewListAdapter(listDataReview, getContext());
                            reviewRecyclerView.setAdapter(recyclerViewadapter);
                            recyclerViewadapter.notifyDataSetChanged();

                            totalReviewTv.setText(String.valueOf(listDataReview.size()) + " REVIEWS");
                        }else {
                            view.findViewById(R.id.no_job_div).setVisibility(View.VISIBLE);
                        }

                    }else {

                        view.findViewById(R.id.no_job_div).setVisibility(View.VISIBLE);

                    }

                }catch (JSONException e){
                    e.getCause();
                    view.findViewById(R.id.no_job_div).setVisibility(View.VISIBLE);

                }

                TabLayoutUtils.enableTabs(PagerMainActivity.tabLayout,true);
                transparentLayer.setVisibility(View.GONE);
                progressDialog.setVisibility(View.GONE);
            }
        });

    }

}
