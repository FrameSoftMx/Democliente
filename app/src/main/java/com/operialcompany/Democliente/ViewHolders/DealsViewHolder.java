package com.operialcompany.Democliente.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.operialcompany.Democliente.R;

public class DealsViewHolder extends RecyclerView.ViewHolder{

    public TextView restaurantName, dealName, dealPrice, dealExpiryDate, perKmDealTv;
    public SimpleDraweeView dealImg;
    public LinearLayout rowItemDealMain;
    public ImageView featuredImg;

    public DealsViewHolder(View itemView) {

        super(itemView);



        dealName = (TextView)itemView.findViewById(R.id.deal_name) ;
        restaurantName = (TextView)itemView.findViewById(R.id.hotal_name_tv) ;
        dealPrice = (TextView)itemView.findViewById(R.id.price_deal_tv) ;
        dealExpiryDate = (TextView)itemView.findViewById(R.id.date_deal_tv) ;
        perKmDealTv = (TextView)itemView.findViewById(R.id.per_km_deal_tv);
        dealImg =  itemView.findViewById(R.id.deals_image) ;
        rowItemDealMain = itemView.findViewById(R.id.row_item_deal_main);
        featuredImg = itemView.findViewById(R.id.featured_img);



    }
}