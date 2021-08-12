package com.operialcompany.Democliente.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import android.widget.RatingBar;

import com.operialcompany.Democliente.R;


public class ReviewViewHolder extends RecyclerView.ViewHolder {


    public TextView reviewer_name_tv, reviewer_desc_tv;
    public TextView reviewer_time_tv;


    public  RatingBar ruleRatingBar;
        public ReviewViewHolder(View itemView) {

        super(itemView);
        reviewer_name_tv = (TextView) itemView.findViewById(R.id.reviewer_name_tv);
        reviewer_time_tv = (TextView) itemView.findViewById(R.id.reviewer_time_tv);
        reviewer_desc_tv = (TextView) itemView.findViewById(R.id.reviewer_desc_tv);
        ruleRatingBar = itemView.findViewById(R.id.ruleRatingBar);

        }
        }