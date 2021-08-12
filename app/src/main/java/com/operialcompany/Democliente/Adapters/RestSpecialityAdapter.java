package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.operialcompany.Democliente.Models.SpecialityModel;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.ViewHolders.RestSpecialityViewHolder;


import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class RestSpecialityAdapter extends RecyclerView.Adapter<RestSpecialityViewHolder> implements Filterable {

    ArrayList<SpecialityModel> getDataAdapter;
    private ArrayList<SpecialityModel> mFilteredList;
    Context context;

    CountryListAdapter.OnItemClickListner onItemClickListner;

    public RestSpecialityAdapter(ArrayList<SpecialityModel> getDataAdapter, Context context){
        super();
        this.getDataAdapter = getDataAdapter;
        this.mFilteredList = getDataAdapter;
        this.context = context;

    }

    @Override
    public RestSpecialityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_rest_speciality, parent, false);

        RestSpecialityViewHolder viewHolder = new RestSpecialityViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestSpecialityViewHolder holder, final int position) {


        holder.nameTv.setText(mFilteredList.get(position).getName());

        holder.mainSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemClickListner.OnItemClicked(v, position);
            }
        });


    }


    @Override
    public int getItemCount() {
        return mFilteredList.size() ;
    }
    @SuppressWarnings("unchecked")
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = getDataAdapter;
                } else {
                    ArrayList<SpecialityModel> filteredList = new ArrayList<>();
                    for (SpecialityModel row : getDataAdapter) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
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
                mFilteredList = (ArrayList<SpecialityModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }



    public interface OnItemClickListner {
        void OnItemClicked(View view, int position);
    }

    public void setOnItemClickListner(CountryListAdapter.OnItemClickListner onCardClickListner) {
        this.onItemClickListner = onCardClickListner;
    }
}