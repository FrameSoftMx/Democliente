package com.operialcompany.Democliente.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.operialcompany.Democliente.R;

public class CreditCardViewHolder extends RecyclerView.ViewHolder{

    public TextView cardNumber;
    public ImageView cardImage;
    public RelativeLayout paymentMainDiv;

    public CreditCardViewHolder(View itemView) {

        super(itemView);


        cardImage = itemView.findViewById(R.id.card_image);
        cardNumber = itemView.findViewById(R.id.credit_card_number_tv);
        paymentMainDiv = itemView.findViewById(R.id.payment_main_div);

    }
}