package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Models.CartChildModel;
import com.operialcompany.Democliente.Models.CartParentModel;
import com.operialcompany.Democliente.R;


import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class AddToCartExpandable extends BaseExpandableListAdapter {

    Context context;
    ArrayList<CartParentModel> listTerbaru;
    ArrayList<ArrayList<CartChildModel>> listChildTerbaru;
    public boolean flagCheckbox;

    public AddToCartExpandable (Context context, ArrayList<CartParentModel>ListTerbaru, ArrayList<ArrayList<CartChildModel>> ListChildTerbaru){
        this.context=context;
        this.listTerbaru =ListTerbaru;
        this.listChildTerbaru =ListChildTerbaru;
    }
    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }


    @Override
    public CartChildModel getChild(int groupPosition, int childPosition) {
        return listChildTerbaru.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }



    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final CartChildModel childTerbaru = getChild(groupPosition, childPosition);

        AddToCartExpandable.ViewHolder holder= null;
        notifyDataSetChanged();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_item_add_to_cart_child, null);

            holder=new AddToCartExpandable.ViewHolder();
            holder.radioBtnItemName =(TextView)convertView.findViewById(R.id.radio_btn_item_name);
            holder.itemPriceTv =(TextView)convertView.findViewById(R.id.item_price_tv);

            if(new DarkModePrefManager(context).isNightMode()) {
                holder.radioBtnItemName.setTextColor(context.getResources().getColor(R.color.bg_top_color));
                holder.itemPriceTv.setTextColor(context.getResources().getColor(R.color.bg_top_color));
            }
            else {
                holder.radioBtnItemName.setTextColor(context.getResources().getColor(R.color.bg_light_top_color));
                holder.itemPriceTv.setTextColor(context.getResources().getColor(R.color.bg_light_top_color));
            }

            holder.radioBtn = convertView.findViewById(R.id.radio_btn);
            holder.checkBtn = convertView.findViewById(R.id.check_btn);


            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }

        holder.radioBtnItemName.setText(childTerbaru.getChild_item_name());
        holder.itemPriceTv.setText("+ "+childTerbaru.getSymbol()+childTerbaru.getChild_item_price());
        if (childTerbaru.isCheckedddd()) {
                holder.radioBtn.setChecked(true);

            }

        else {
                holder.radioBtn.setChecked(false);
            }

        if(flagCheckbox){
            childTerbaru.setCheckRequired(false);
            holder.radioBtn.setVisibility(View.INVISIBLE);
            holder.checkBtn.setVisibility(View.VISIBLE);
        }
        else {
            childTerbaru.setCheckRequired(true);
            holder.radioBtn.setVisibility(View.VISIBLE);
            holder.checkBtn.setVisibility(View.GONE);
        }


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChildTerbaru.get(groupPosition).size();
    }

    public ArrayList<CartChildModel> getChilderns(int groupPos){

        return listChildTerbaru.get(groupPos);
    }

    @Override
    public CartParentModel getGroup(int groupPosition) {
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

        CartParentModel terbaruModel = (CartParentModel) getGroup(groupPosition);
        AddToCartExpandable.ViewHolder holder= null;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_item_add_to_cart_parent, null);

            holder=new AddToCartExpandable.ViewHolder();
            holder.parentTv =(TextView)convertView.findViewById(R.id.parent_tv);

            convertView.setTag(holder);

        }

        else{
            holder=(AddToCartExpandable.ViewHolder)convertView.getTag();
        }

        String checkRequired = terbaruModel.getRequired();
        if (checkRequired.equalsIgnoreCase("1")) {

            holder.parentTv.setText(terbaruModel.getParentName() + " (Required)");
            flagCheckbox = false;
        }
        else {
            holder.parentTv.setText(terbaruModel.getParentName());
            flagCheckbox = true;
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



    static class ViewHolder{
        TextView parentTv, radioBtnItemName, itemPriceTv;
        RadioButton radioBtn;
        CheckBox checkBtn;
    }


}

