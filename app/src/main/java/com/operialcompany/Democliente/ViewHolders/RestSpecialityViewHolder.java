package com.operialcompany.Democliente.ViewHolders;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.operialcompany.Democliente.R;

public class RestSpecialityViewHolder extends RecyclerView.ViewHolder{

    public TextView nameTv;
    public RelativeLayout mainSpeciality;

    public RestSpecialityViewHolder(View itemView) {

        super(itemView);

        nameTv = itemView.findViewById(R.id.name_tv);
        mainSpeciality = itemView.findViewById(R.id.main_speciality);


    }
}
