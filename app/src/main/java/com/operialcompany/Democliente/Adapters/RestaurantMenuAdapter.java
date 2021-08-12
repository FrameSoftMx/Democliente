package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Models.RestaurantParentModel;
import com.operialcompany.Democliente.ActivitiesAndFragments.RestaurantMenuItemsFragment;
import com.operialcompany.Democliente.Models.RestaurantChildModel;
import com.operialcompany.Democliente.R;

import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class RestaurantMenuAdapter extends BaseExpandableListAdapter implements Filterable {
    Context context;
    ArrayList<RestaurantParentModel> listTerbaru;
    ArrayList<ArrayList<RestaurantChildModel>> listChildTerbaru;
    private   ArrayList<RestaurantParentModel> mFilteredList;
    public boolean flagOutOfOrder;


    public RestaurantMenuAdapter (Context context, ArrayList<RestaurantParentModel> ListTerbaru, ArrayList<ArrayList<RestaurantChildModel>> ListChildTerbaru){
        this.context=context;
        this.listTerbaru =ListTerbaru;
        this.listChildTerbaru =ListChildTerbaru;
        this.mFilteredList = ListTerbaru;
    }
    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }


    @Override
    public RestaurantChildModel getChild(int groupPosition, int childPosition) {
        return listChildTerbaru.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        RestaurantChildModel childTerbaru = getChild(groupPosition, childPosition);
        RestaurantMenuAdapter.ViewHolder holder= null;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_item_restaurant_child, null);

            holder=new RestaurantMenuAdapter.ViewHolder();
            holder.image=(SimpleDraweeView) convertView.findViewById(R.id.image);
            holder.titleNameChild =(TextView)convertView.findViewById(R.id.title_name_child);
            holder.subTitleNameChild = convertView.findViewById(R.id.sub_title_name_child);
            holder.priceTv = convertView.findViewById(R.id.price_tv);
            holder.orderStatusTv = convertView.findViewById(R.id.order_status_tv);



            convertView.setTag(holder);

        }
        else{
            holder=(RestaurantMenuAdapter.ViewHolder)convertView.getTag();
        }
        String get_order_status = childTerbaru.order_detail;
        String get_symbol = childTerbaru.currency_symbol;
        if (get_order_status.equalsIgnoreCase("1"))
        {
            holder.priceTv.setText(context.getResources().getString(R.string.out_of_order));
            holder.priceTv.setTextSize(11);
            flagOutOfOrder = true;
        }
        else {
            holder.priceTv.setText(get_symbol+childTerbaru.price);
            holder.priceTv.setTextSize(14);
            flagOutOfOrder = false;
        }

        if(childTerbaru.image!=null && !childTerbaru.image.equals("")){
            holder.image.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(Config.imgBaseURL+childTerbaru.image);
            holder.image.setImageURI(uri);
        }else {
            holder.image.setVisibility(View.INVISIBLE);
        }

        String title = childTerbaru.child_title.replaceAll("&amp;", "&");
        holder.titleNameChild.setText(title);
        String subtitle = childTerbaru.child_sub_title.replaceAll("&amp;", "&");
        holder.subTitleNameChild.setText(subtitle);


        return convertView;
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        return listChildTerbaru.get(groupPosition).size();
    }@Override
    public RestaurantParentModel getGroup(int groupPosition) {
        return mFilteredList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mFilteredList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        RestaurantParentModel terbaruModel =  getGroup(groupPosition);
        RestaurantMenuAdapter.ViewHolder holder= null;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_item_restaurant_parent, null);

            holder=new RestaurantMenuAdapter.ViewHolder();
            holder.image=(SimpleDraweeView) convertView.findViewById(R.id.image);
            holder.titleName =(TextView)convertView.findViewById(R.id.title_name);
            holder.subTitleName =(TextView)convertView.findViewById(R.id.sub_title_name);
            holder.maindivParent = (RelativeLayout)convertView.findViewById(R.id.mainDiv_Parent);
            convertView.setTag(holder);

        }

        else{
            holder=(RestaurantMenuAdapter.ViewHolder)convertView.getTag();
        }

        holder.titleName.setText(terbaruModel.getTitle());
        if(RestaurantMenuItemsFragment.FLAG_SUGGESTION){
            holder.subTitleName.setVisibility(View.GONE);
            holder.maindivParent.setBackgroundColor(ContextCompat.getColor(context,R.color.colorWhite));
        }
        else {
            holder.subTitleName.setText(terbaruModel.getSub_title());
        }


        if(terbaruModel.image!=null && !terbaruModel.image.equals("")){
            holder.image.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(Config.imgBaseURL+terbaruModel.image);
            holder.image.setImageURI(uri);
        }else {
            holder.image.setVisibility(View.INVISIBLE);
        }



        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = listTerbaru;
                } else {
                    ArrayList<RestaurantParentModel> filteredList = new ArrayList<>();
                    for (RestaurantParentModel row : listTerbaru) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
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
                mFilteredList = (ArrayList<RestaurantParentModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };


    }


    static class ViewHolder{
        TextView titleName, subTitleName, titleNameChild, subTitleNameChild, priceTv, orderStatusTv;
        RelativeLayout maindivParent;
        SimpleDraweeView image;
    }


}