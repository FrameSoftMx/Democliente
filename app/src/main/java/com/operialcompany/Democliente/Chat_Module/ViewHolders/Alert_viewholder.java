package com.operialcompany.Democliente.Chat_Module.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.operialcompany.Democliente.R;


public class Alert_viewholder extends RecyclerView.ViewHolder {


    public TextView message,datetxt;
    public View view;

    public Alert_viewholder(View itemView) {
        super(itemView);
        view = itemView;
        this.message = view.findViewById(R.id.message);
        this.datetxt = view.findViewById(R.id.datetxt);
    }

}