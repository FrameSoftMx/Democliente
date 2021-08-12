package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.operialcompany.Democliente.Models.MenuItemExtraModel;
import com.operialcompany.Democliente.Models.MenuItemModel;
import com.operialcompany.Democliente.R;


import java.util.ArrayList;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	Context context;
	ArrayList<MenuItemModel> listTerbaru;
	ArrayList<ArrayList<MenuItemExtraModel>> listChildTerbaru;
	int count;

	public ExpandableListAdapter (Context context, ArrayList<MenuItemModel>ListTerbaru, ArrayList<ArrayList<MenuItemExtraModel>> ListChildTerbaru){
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
	public MenuItemExtraModel getChild(int groupPosition, int childPosition) {
		return listChildTerbaru.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,View convertView, ViewGroup parent) {

		MenuItemExtraModel childTerbaru = getChild(groupPosition, childPosition);
		ViewHolder holder= null;

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.row_item_menu_extra, null);

			holder=new ViewHolder();
			holder.menu_item_extra_tv=(TextView)convertView.findViewById(R.id.menu_item_extra_tv);
			convertView.setTag(holder);

		}
		else{
			holder=(ViewHolder)convertView.getTag();
		}

		String quantity = childTerbaru.getQuantity();

		holder.menu_item_extra_tv.setText(quantity+"x "+childTerbaru.getExtra_item_name()+" +"+childTerbaru.getCurrency()+childTerbaru.getPrice());


		return convertView;
	}
	@Override
	public int getChildrenCount(int groupPosition) {
		return listChildTerbaru.get(groupPosition).size();
	}

	@Override
	public MenuItemModel getGroup(int groupPosition) {
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

		MenuItemModel terbaruModel = (MenuItemModel) getGroup(groupPosition);
		ViewHolder holder= null;
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.row_item_order_menu, null);

			holder=new ViewHolder();
			holder.menu_item_name_tv=(TextView)convertView.findViewById(R.id.menu_item_name_tv);
			holder.menu_item_amount_tv=(TextView)convertView.findViewById(R.id.menu_item_amount_tv);
			convertView.setTag(holder);

		}

		else{
			holder=(ViewHolder)convertView.getTag();
		}

		holder.menu_item_amount_tv.setText(terbaruModel.getItem_price());
		holder.menu_item_name_tv.setText(terbaruModel.getItem_name().replaceAll("&amp;", "&")+" x "+terbaruModel.getOrder_quantity());

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
		TextView menu_item_name_tv,menu_item_amount_tv,menu_item_extra_tv;
	}
}
