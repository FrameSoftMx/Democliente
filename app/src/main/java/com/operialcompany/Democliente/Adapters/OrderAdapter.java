package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.operialcompany.Democliente.Models.OrderModelClass;
import com.operialcompany.Democliente.ActivitiesAndFragments.OrdersFragment;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.ViewHolders.OrdersViewHolder;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by qboxus on 10/18/2019.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrdersViewHolder>  {

    ArrayList<OrderModelClass> getDataAdapter;
    Context context;
    OrderAdapter.OnItemClickListner onItemClickListner;

    public OrderAdapter(ArrayList<OrderModelClass> getDataAdapter, Context context){
        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_items_orders, parent, false);

        OrdersViewHolder viewHolder = new OrdersViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrdersViewHolder holder, final int position) {
        OrderModelClass getDataAdapter1 =  getDataAdapter.get(position);

        holder.trackTv.setTag(getDataAdapter1);
        holder.dealImg.setTag(getDataAdapter1);
        OrderModelClass checkWetherToShow=(OrderModelClass)holder.trackTv.getTag();
        OrderModelClass checkWetherToShowDeal=(OrderModelClass)holder.dealImg.getTag();

        String date_time = getDataAdapter1.getOrder_created();

        if(OrdersFragment.statusinactive){
            holder.trackTv.setTextColor(context.getResources().getColor(R.color.trackColor));
        }
        else {
                if (checkWetherToShow.getDelivery().equalsIgnoreCase("0")) {
                    holder.trackTv.setTextColor(context.getResources().getColor(R.color.trackColor));
                } else {
                    holder.trackTv.setTextColor(context.getResources().getColor(R.color.colorRed));
                }

        }

        StringTokenizer tk = new StringTokenizer(date_time);
        String date = tk.nextToken();
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
        Date dt;
        try {
            dt = sdf.parse(time);
            System.out.println("Time Display: " + sdfs.format(dt));
            String finalTime = sdfs.format(dt);
            holder.orderTime.setText(finalTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.menuItemName.setText(getDataAdapter1.getOrder_name().replaceAll("&amp;", "&"));
        holder.retaurantName.setText(getDataAdapter1.getRestaurant_name().replaceAll("&amp;", "&"));
        holder.orderDate.setText(date);
        holder.orderNumber.setText("Order #"+getDataAdapter1.getOrder_id());
        holder.orderPrice.setText(getDataAdapter1.getCurrency_symbol()+ getDataAdapter1.getOrder_price());

        if(!checkWetherToShowDeal.getDeal_id().equalsIgnoreCase("0")){
            holder.dealImg.setVisibility(View.VISIBLE);
        }
        else {
            holder.dealImg.setVisibility(View.GONE);
        }

        holder.orderItemMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    onItemClickListner.OnItemClicked(v,position);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return getDataAdapter.size() ;
    }

    public interface OnItemClickListner {
        void OnItemClicked(View view, int position);
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

}
