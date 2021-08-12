package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.operialcompany.Democliente.Models.RatingListModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.ViewHolders.ReviewViewHolder;

import java.util.ArrayList;


/**
 * Created by qboxus on 10/18/2019.
 */

public class ShowReviewListAdapter extends RecyclerView.Adapter<ReviewViewHolder> {

    ArrayList<RatingListModel> getDataAdapter;
    Context context;
    OrderAdapter.OnItemClickListner onItemClickListner;

    public ShowReviewListAdapter(ArrayList<RatingListModel> getDataAdapter, Context context) {
        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_items_reviews, parent, false);

        ReviewViewHolder viewHolder = new ReviewViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, final int position) {
        RatingListModel getDataAdapter1 = getDataAdapter.get(position);

        String f_name = getDataAdapter1.getF_name();
        String l_name = getDataAdapter1.getL_name();

        String date = getDataAdapter1.getCreated();

        holder.reviewer_time_tv.setText(date);

        holder.reviewer_name_tv.setText(f_name+" "+l_name);
        holder.reviewer_desc_tv.setText(getDataAdapter1.getComment());
        holder.ruleRatingBar.setRating(Float.parseFloat(getDataAdapter1.getRating()));

    }

    @Override
    public int getItemCount() {
        return getDataAdapter.size();
    }




    public interface OnItemClickListner {
        void OnItemClicked(View view, int position);
    }

    public void setOnItemClickListner(OrderAdapter.OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }



}
