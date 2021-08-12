package com.operialcompany.Democliente.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.operialcompany.Democliente.Constants.AdapterClickListener;
import com.operialcompany.Democliente.Models.RestaurantsModel;
import com.operialcompany.Democliente.R;


public class RestuarentsViewHolder extends RecyclerView.ViewHolder {

    public TextView titleRestaurants, distanseRestaurants, salogonRestaurants, itemPriceTv, itemTimeTv, bakedTimeTv,
            itemDeliveryTimeTv;
    public SimpleDraweeView restaurantImg;
    public RelativeLayout restaurantRowMain;
    public RatingBar ratingBar;
    public ImageView favoriteIcon, featured;

    public RestuarentsViewHolder(View itemView) {

        super(itemView);
        titleRestaurants = (TextView) itemView.findViewById(R.id.title_restaurants);
        salogonRestaurants = (TextView) itemView.findViewById(R.id.salogon_restaurants);
        distanseRestaurants = (TextView) itemView.findViewById(R.id.distanse_restaurants);
        itemPriceTv = itemView.findViewById(R.id.item_price_tv);

        restaurantImg = itemView.findViewById(R.id.profile_image_restaurant);
        restaurantRowMain = itemView.findViewById(R.id.restaurant_row_main);
        ratingBar = itemView.findViewById(R.id.ruleRatingBar);
        favoriteIcon = itemView.findViewById(R.id.favorite_icon);
        featured = itemView.findViewById(R.id.featured);
        itemTimeTv = itemView.findViewById(R.id.item_time_tv);
        bakedTimeTv = itemView.findViewById(R.id.baked_time_tv);
        itemDeliveryTimeTv = itemView.findViewById(R.id.item_delivery_time_tv);

    }

    public void bind(final int postion, final RestaurantsModel item, final AdapterClickListener listener) {



        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, postion, item);
            }
        });

        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, postion, item);
            }
        });
    }


}

