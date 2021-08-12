package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.android.volley.toolbox.ImageLoader;
import com.operialcompany.Democliente.Constants.AdapterClickListener;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.Models.RestaurantsModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.ViewHolders.RestuarentsViewHolder;
import com.operialcompany.Democliente.Constants.Config;

import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class RestaurantsAdapter extends RecyclerView.Adapter<RestuarentsViewHolder> implements Filterable {

    ArrayList<RestaurantsModel> data_list;
    private ArrayList<RestaurantsModel> mFilteredList;
    Context context;
    ImageLoader imageLoader1;
    SharedPreferences sharedPreferences;
    AdapterClickListener adapter_click_listener;

    public RestaurantsAdapter(ArrayList<RestaurantsModel> data_list, Context context, AdapterClickListener adapter_click_listener){
        super();
        this.data_list = data_list;
        mFilteredList = data_list;
        this.context = context;
        this.adapter_click_listener=adapter_click_listener;
    }

    @Override
    public RestuarentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_items_restaurants, parent, false);

        RestuarentsViewHolder viewHolder = new RestuarentsViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RestuarentsViewHolder holder, final int position) {

        final RestaurantsModel item =  mFilteredList.get(position);

        holder.bind(position,item,adapter_click_listener);

        sharedPreferences = context.getSharedPreferences(PreferenceClass.user,Context.MODE_PRIVATE);
        imageLoader1 = ServerImageParseAdapter.getInstance(context).getImageLoader();

        holder.favoriteIcon.setTag(item);
        RestaurantsModel checkWetherToShow=(RestaurantsModel)holder.favoriteIcon.getTag();


        Uri uri = Uri.parse(Config.imgBaseURL+item.restaurant_image);
        holder.restaurantImg.setImageURI(uri);

        if(item.restaurant_name!=null)
        holder.titleRestaurants.setText(item.restaurant_name.trim());



        String symbol = item.restaurant_currency;
        holder.salogonRestaurants.setText(item.restaurant_salgon.trim());
        holder.bakedTimeTv.setText(item.preparation_time+ " min");
        holder.itemDeliveryTimeTv.setText(item.deliveryTime+" min");

        holder.ratingBar.setRating(Float.parseFloat(item.restaurant_avgRating));

        if(item.min_order_price.equalsIgnoreCase("0.00")){
            holder.itemTimeTv.setText(symbol + " " + item.delivery_fee_per_km + " /km");
        }
        else {
            holder.itemTimeTv.setText(symbol + " " + item.delivery_fee_per_km+ " /km- Free over" + " " + symbol + " " + item.min_order_price);

        }

        if (checkWetherToShow.restaurant_isFav.equalsIgnoreCase("1")){
            holder.favoriteIcon.setImageResource(R.drawable.ic_heart_filled);
        }
        else {
            holder.favoriteIcon.setImageResource(R.drawable.ic_heart_not_filled);
        }

        String getPromotedString = item.promoted;

        if (getPromotedString.equalsIgnoreCase("1")) {
            holder.featured.setVisibility(View.VISIBLE);
        }
        else {
            holder.featured.setVisibility(View.GONE);
        }

        holder.distanseRestaurants.setText(item.restaurant_distance);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 1;
        else
            return 2;
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size() ;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = data_list;
                } else {
                    ArrayList<RestaurantsModel> filteredList = new ArrayList<>();
                    for (RestaurantsModel row : data_list) {
                        if (row.restaurant_name.toLowerCase().contains(charString.toLowerCase()) || row.restaurant_name.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<RestaurantsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };


    }


}
