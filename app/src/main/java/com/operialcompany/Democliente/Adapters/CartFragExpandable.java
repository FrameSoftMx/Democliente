package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.operialcompany.Democliente.Models.CartFragChildModel;
import com.operialcompany.Democliente.Models.CartFragParentModel;
import com.operialcompany.Democliente.R;

import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class CartFragExpandable  extends BaseExpandableListAdapter {

    Context context;
    ArrayList<CartFragParentModel> listTerbaru;
    ArrayList<ArrayList<CartFragChildModel>> listchildterbaru;

    public CartFragExpandable (Context context, ArrayList<CartFragParentModel>ListTerbaru, ArrayList<ArrayList<CartFragChildModel>> listchildterbaru){
        this.context=context;
        this.listTerbaru =ListTerbaru;
        this.listchildterbaru = listchildterbaru;

    }
    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }


    @Override
    public CartFragChildModel getChild(int groupPosition, int childPosition) {
        return listchildterbaru.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        final CartFragChildModel childTerbaru = getChild(groupPosition, childPosition);

        CartFragExpandable.ViewHolder holder= null;
        notifyDataSetChanged();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_item_cart_child, null);

            holder=new CartFragExpandable.ViewHolder();
            holder.itemDetailTv = (TextView)convertView.findViewById(R.id.item_detail_tv);

            convertView.setTag(holder);
        }
        else{
            holder=(CartFragExpandable.ViewHolder)convertView.getTag();
        }

        String quantity = childTerbaru.getQuantity();
        String name = childTerbaru.getName();
        String price = childTerbaru.getPrice();
        String symbol = childTerbaru.getSymbol();
        holder.itemDetailTv.setText(quantity+"x "+name+" + "+symbol+price);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(listchildterbaru.get(groupPosition).size() != 0){
            return listchildterbaru.get(groupPosition).size();
        }
        return 0;

    }

    @Override
    public CartFragParentModel getGroup(int groupPosition) {
        return listTerbaru.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return listTerbaru.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        CartFragParentModel terbaruModel = (CartFragParentModel) getGroup(groupPosition);
        CartFragExpandable.ViewHolder holder= null;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_item_cart_parent, null);

            holder=new CartFragExpandable.ViewHolder();
            holder.nameTv =(TextView)convertView.findViewById(R.id.name_tv);
            holder.priceTv = (TextView)convertView.findViewById(R.id.price_tv);

            convertView.setTag(holder);

        }

        else{
            holder=(CartFragExpandable.ViewHolder)convertView.getTag();
        }
        String quantity = terbaruModel.getItem_quantity();
        String name = terbaruModel.getItem_name();
        String price = terbaruModel.getItem_price();
        String symbol = terbaruModel.getItem_symbol();

        holder.nameTv.setText(name+" x"+quantity);
        holder.priceTv.setText(symbol+price);

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

    static class ViewHolder{
        TextView nameTv, priceTv, itemDetailTv;
    }


}