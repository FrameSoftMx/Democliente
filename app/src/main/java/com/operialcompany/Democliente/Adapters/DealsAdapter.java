package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.operialcompany.Democliente.Models.DealsModel;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.ViewHolders.DealsViewHolder;

import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class DealsAdapter extends RecyclerView.Adapter<DealsViewHolder>  {

    ArrayList<DealsModel> getDataAdapter;
    Context context;
    OnItemClickListner onItemClickListner;

    public DealsAdapter(ArrayList<DealsModel> getDataAdapter, Context context){
        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public DealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_items_deals, parent, false);

        DealsViewHolder viewHolder = new DealsViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DealsViewHolder holder, final int position) {
        DealsModel getDataAdapter1 =  getDataAdapter.get(position);

        holder.featuredImg.setTag(getDataAdapter1);
        DealsModel checkWetherToShow=(DealsModel)holder.featuredImg.getTag();

        Uri uri = Uri.parse(Config.imgBaseURL+getDataAdapter1.deal_image);
        holder.dealImg.setImageURI(uri);

        String expiry_date = getDataAdapter1.deal_expiry_date;

        try{String finalExpiryDate = expiry_date.substring(0,10);
            holder.dealExpiryDate.setText(finalExpiryDate);
        }
        catch (StringIndexOutOfBoundsException e){

            e.getCause();
        }
                holder.perKmDealTv.setText(getDataAdapter1.deal_symbol+" "+ getDataAdapter1.deal_delivery_fee+"/km");
                holder.dealName.setText(getDataAdapter1.deal_name);
                holder.restaurantName.setText(getDataAdapter1.restaurant_name);
                holder.dealPrice.setText(getDataAdapter1.deal_symbol+" "+ getDataAdapter1.deal_price);

                holder.rowItemDealMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListner.OnItemClicked(view,position);
                    }
                });

                if (checkWetherToShow.promoted.equalsIgnoreCase("1")){
                    holder.featuredImg.setVisibility(View.VISIBLE);
                }
                else {
                    holder.featuredImg.setVisibility(View.GONE);
                }

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