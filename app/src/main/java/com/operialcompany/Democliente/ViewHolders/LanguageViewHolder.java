package com.operialcompany.Democliente.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.operialcompany.Democliente.Constants.AdapterClickListener;
import com.operialcompany.Democliente.Models.Languages_Model;
import com.operialcompany.Democliente.R;

public class LanguageViewHolder  extends RecyclerView.ViewHolder {

    public TextView languageTitle, languageNameTxt;
    public ImageView countryIcon;


    public LanguageViewHolder(View itemView) {

        super(itemView);
        languageTitle = (TextView) itemView.findViewById(R.id.language_title);
        languageNameTxt = (TextView) itemView.findViewById(R.id.language_name_txt);

        countryIcon = (ImageView) itemView.findViewById(R.id.country_icon);

    }

    public void OnBind(int pos, Languages_Model item, AdapterClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, pos, item);
            }
        });
    }
}