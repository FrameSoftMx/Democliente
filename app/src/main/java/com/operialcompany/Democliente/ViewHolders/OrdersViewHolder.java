package com.operialcompany.Democliente.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.operialcompany.Democliente.R;


public class OrdersViewHolder extends RecyclerView.ViewHolder{


   public TextView menuItemName, retaurantName, orderDate, orderTime, orderPrice, orderNumber, trackTv;
   public RelativeLayout orderItemMain;
   public ImageView dealImg;


    public OrdersViewHolder(View itemView) {

        super(itemView);
        menuItemName = (TextView)itemView.findViewById(R.id.deal_name);
        retaurantName = (TextView)itemView.findViewById(R.id.hotal_name_tv);

        orderDate = (TextView)itemView.findViewById(R.id.date_deal_tv);
        orderTime = (TextView)itemView.findViewById(R.id.time_deal_tv);
        orderPrice = (TextView) itemView.findViewById(R.id.price_deal_tv) ;
        orderNumber = (TextView)itemView.findViewById(R.id.order_number);
        trackTv = itemView.findViewById(R.id.track_tv);
        dealImg = itemView.findViewById(R.id.deal_img);

        orderItemMain = (RelativeLayout) itemView.findViewById(R.id.order_item_main_div);

    }
}
