package com.operialcompany.Democliente.Chat_Module.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.operialcompany.Democliente.Chat_Module.ChatAdapter;
import com.operialcompany.Democliente.Chat_Module.ChatModel;
import com.operialcompany.Democliente.R;


public class ChatImage_viewholder extends RecyclerView.ViewHolder {

    public SimpleDraweeView chatimage;
    public TextView datetxt,message_seen , msg_date;
    public ProgressBar p_bar;
    public ImageView not_send_message_icon;
    public View view;
    public TextView user_name;


    public ChatImage_viewholder(View itemView) {
        super(itemView);
        view = itemView;

        this.chatimage = view.findViewById(R.id.chatimage);
        this.datetxt = view.findViewById(R.id.datetxt);
        message_seen = view.findViewById(R.id.message_seen);
        msg_date = view.findViewById(R.id.msg_date);
        not_send_message_icon = view.findViewById(R.id.not_send_messsage);
        p_bar = view.findViewById(R.id.p_bar);

    }

    public void bind(final int pos,final ChatModel item,
                     final ChatAdapter.OnItemClickListener listener,
                     final ChatAdapter.OnLongClickListener long_listener) {

        chatimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(pos,item,v);
            }
        });

        chatimage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                long_listener.onLongclick(item,v);
                return false;
            }
        });
    }
}
