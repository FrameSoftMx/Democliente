package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.operialcompany.Democliente.Models.CardDetailModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.ViewHolders.CreditCardViewHolder;


import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class CreditCardDetailAdapter extends RecyclerView.Adapter<CreditCardViewHolder>  {

    ArrayList<CardDetailModel> getDataAdapter;
    Context context;
    OnItemClickListner onItemClickListner;

    public CreditCardDetailAdapter(ArrayList<CardDetailModel> getDataAdapter, Context context){
        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public CreditCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_payment_details, parent, false);

        CreditCardViewHolder viewHolder = new CreditCardViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CreditCardViewHolder holder, final int position) {

        CardDetailModel getDataAdapter1 =  getDataAdapter.get(position);

        String card_name = getDataAdapter1.getCard_name();
        holder.cardNumber.setText("**** **** **** "+getDataAdapter1.getCard_number());
        holder.cardImage.setImageResource(getDataAdapter1.getCard_image());

        if(card_name.equalsIgnoreCase("visa")){
            holder.cardImage.setImageResource(R.drawable.visa);
        }
        else if (card_name.equalsIgnoreCase("master")){
            holder.cardImage.setImageResource(R.drawable.master_card);
        }

        holder.paymentMainDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onItemClickListner.OnItemClicked(view,position);
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

    public void setOnItemClickListner(OnItemClickListner onCardClickListner) {
        this.onItemClickListner = onCardClickListner;
    }
}
