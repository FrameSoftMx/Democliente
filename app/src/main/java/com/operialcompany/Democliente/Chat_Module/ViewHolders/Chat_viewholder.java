package com.operialcompany.Democliente.Chat_Module.ViewHolders;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.operialcompany.Democliente.Chat_Module.ChatAdapter;
import com.operialcompany.Democliente.Chat_Module.ChatModel;
import com.operialcompany.Democliente.R;


public class Chat_viewholder extends RecyclerView.ViewHolder {

    public TextView message,datetxt,message_seen,msg_date;
    public View view;
    public TextView user_name;

    public Chat_viewholder(View itemView) {
        super(itemView);
        view = itemView;

        this.message = view.findViewById(R.id.msgtxt);
        this.datetxt=view.findViewById(R.id.datetxt);
        message_seen=view.findViewById(R.id.message_seen);
        msg_date=view.findViewById(R.id.msg_date);

    }

    public void bind(final ChatModel item,
                     final ChatAdapter.OnLongClickListener long_listener) {
        message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                long_listener.onLongclick(item,v);
                return false;
            }
        });
    }
}

