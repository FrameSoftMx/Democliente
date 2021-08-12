package com.operialcompany.Democliente.Chat_Module;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.operialcompany.Democliente.Chat_Module.ViewHolders.Alert_viewholder;
import com.operialcompany.Democliente.Chat_Module.ViewHolders.ChatAudio_viewholder;
import com.operialcompany.Democliente.Chat_Module.ViewHolders.ChatImage_viewholder;
import com.operialcompany.Democliente.Chat_Module.ViewHolders.Chat_viewholder;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatModel> mDataSet;
    String myID="";
    private static final int mychat = 1;
    private static final int friendchat = 2;
    private static final int mychatimage = 3;
    private static final int otherchatimage = 4;
    private static final int alert_message = 7;
    private static final int my_audio_message = 8;
    private static final int other_audio_message = 9;

    Context context;
    Integer today_day = 0;

    private OnItemClickListener listener;
    private ChatAdapter.OnLongClickListener long_listener;

    public interface OnItemClickListener {
        void onItemClick(int postion, ChatModel item, View view);
    }

    public interface OnLongClickListener {
        void onLongclick(ChatModel item, View view);
    }


    ChatAdapter(List<ChatModel> dataSet, String id, Context context, ChatAdapter.OnItemClickListener listener, ChatAdapter.OnLongClickListener long_listener) {
        mDataSet = dataSet;
        this.myID = id;
        this.context = context;
        this.listener = listener;
        this.long_listener = long_listener;
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);
    }


    // this is the all types of view that is used in the chat
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View v = null;
        switch (viewtype) {
            // we have 4 type of layout in chat activity text chat of my and other and also
            // image layout of my and other
            case mychat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_my, viewGroup, false);
                Chat_viewholder mychatHolder = new Chat_viewholder(v);
                return mychatHolder;
            case friendchat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_other, viewGroup, false);
                Chat_viewholder friendchatHolder = new Chat_viewholder(v);
                return friendchatHolder;
            case mychatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_my, viewGroup, false);
                ChatImage_viewholder mychatimageHolder = new ChatImage_viewholder(v);
                return mychatimageHolder;
            case otherchatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_other, viewGroup, false);
                ChatImage_viewholder otherchatimageHolder = new ChatImage_viewholder(v);
                return otherchatimageHolder;

            case my_audio_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_my, viewGroup, false);
                ChatAudio_viewholder chatAudioviewholder = new ChatAudio_viewholder(v);
                return chatAudioviewholder;

            case other_audio_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_other, viewGroup, false);
                ChatAudio_viewholder other = new ChatAudio_viewholder(v);
                return other;

            case alert_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_alert, viewGroup, false);
                Alert_viewholder alertviewholder = new Alert_viewholder(v);
                return alertviewholder;

            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatModel chat = mDataSet.get(position);

          if (chat.getType().equals("text")) {
            Chat_viewholder chatviewholder = (Chat_viewholder) holder;

            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1"))
                    chatviewholder.message_seen.setText(context.getString(R.string.seen_at)+" " + changeDateToTime(chat.getTime()));
                else
                    chatviewholder.message_seen.setText(""+context.getString(R.string.sent));
            } else {

                chatviewholder.message_seen.setText("");
            }

            chatviewholder.message.setText(chat.getText());
            chatviewholder.msg_date.setText(showMessageTime(chat.getTimestamp()));

            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(0, 2).equals(chat.getTimestamp().substring(0, 2))) {
                    chatviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatviewholder.datetxt.setVisibility(View.VISIBLE);
                    chatviewholder.datetxt.setText(changeDate(chat.getTimestamp()));
                }

            } else {
                chatviewholder.datetxt.setVisibility(View.VISIBLE);
                chatviewholder.datetxt.setText(changeDate(chat.getTimestamp()));
            }

            chatviewholder.bind(chat, long_listener);

        }

         else if (chat.getType().equals("image")) {
            final ChatImage_viewholder chatimageholder = (ChatImage_viewholder) holder;
            // check if the message is from sender or receiver
            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1"))
                    chatimageholder.message_seen.setText(context.getString(R.string.seen_at)+" " + changeDateToTime(chat.getTime()));
                else
                    chatimageholder.message_seen.setText(""+context.getString(R.string.sent));

            } else {
                chatimageholder.message_seen.setText("");
            }
            chatimageholder.msg_date.setText(showMessageTime(chat.getTimestamp()));

            if (chat.getPic_url().equals("none")) {
                if (Chat_A.uploading_image_id.equals(chat.getChat_id())) {
                    chatimageholder.p_bar.setVisibility(View.VISIBLE);
                    chatimageholder.message_seen.setText("");
                } else {
                    chatimageholder.p_bar.setVisibility(View.GONE);
                    chatimageholder.not_send_message_icon.setVisibility(View.VISIBLE);
                    chatimageholder.message_seen.setText(context.getString(R.string.not_delivered)+" ");
                }
            } else {
                chatimageholder.not_send_message_icon.setVisibility(View.GONE);
                chatimageholder.p_bar.setVisibility(View.GONE);
            }

            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(0, 2).equals(chat.getTimestamp().substring(0, 2))) {
                    chatimageholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatimageholder.datetxt.setVisibility(View.VISIBLE);
                    chatimageholder.datetxt.setText(changeDate(chat.getTimestamp()));
                }



                Uri uri = Uri.parse(chat.getPic_url());
                chatimageholder.chatimage.setImageURI(uri);



            } else {
                chatimageholder.datetxt.setVisibility(View.VISIBLE);
                chatimageholder.datetxt.setText(changeDate(chat.getTimestamp()));


                Uri uri = Uri.parse(chat.getPic_url());
                chatimageholder.chatimage.setImageURI(uri);


            }

            chatimageholder.bind(position,mDataSet.get(position), listener, long_listener);
        }

          else if (chat.getType().equals("audio")) {
            final ChatAudio_viewholder chatAudioviewholder = (ChatAudio_viewholder) holder;
            // check if the message is from sender or receiver
            if (chat.getSender_id().equals(myID)) {
                if (chat.getStatus().equals("1"))
                    chatAudioviewholder.message_seen.setText(context.getString(R.string.seen_at)+" "+ changeDateToTime(chat.getTime()));
                else
                    chatAudioviewholder.message_seen.setText(""+context.getString(R.string.sent));

            } else {
                chatAudioviewholder.message_seen.setText("");
            }


            chatAudioviewholder.msg_date.setText(showMessageTime(chat.getTimestamp()));

            if (chat.getSender_id().equals(myID) && chat.getPic_url().equals("none")) {
                chatAudioviewholder.p_bar.setVisibility(View.VISIBLE);
            } else {
                chatAudioviewholder.p_bar.setVisibility(View.GONE);
            }

            String downloadid = PreferenceClass.sharedPreferences.getString(chat.getChat_id(), "");
            if (!downloadid.equals("")) {
                String status = Functions.Check_Image_Status(context, Long.parseLong(downloadid));
                if (status.equals("STATUS_FAILED") || status.equals("STATUS_SUCCESSFUL")) {
                    chatAudioviewholder.p_bar.setVisibility(View.GONE);
                    PreferenceClass.sharedPreferences.edit().remove(chat.getChat_id()).commit();
                } else {
                    chatAudioviewholder.p_bar.setVisibility(View.VISIBLE);
                }
            }

            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(0, 2).equals(chat.getTimestamp().substring(0, 2))) {
                    chatAudioviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatAudioviewholder.datetxt.setVisibility(View.VISIBLE);
                    chatAudioviewholder.datetxt.setText(changeDate(chat.getTimestamp()));
                }
            }
            else {
                chatAudioviewholder.datetxt.setVisibility(View.VISIBLE);
                chatAudioviewholder.datetxt.setText(changeDate(chat.getTimestamp()));

            }

            File fullpath = new File(AllConstants.folder_parcel + chat.chat_id + ".mp3");
            if (fullpath.exists()) {
                chatAudioviewholder.total_time.setText(getfileduration(Uri.parse(fullpath.getAbsolutePath())));
            } else {
                chatAudioviewholder.total_time.setText(null);
            }


              if (Chat_A.playing_id.equals(chat.chat_id) && Chat_A.mediaplayer != null) {
                  chatAudioviewholder.play_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_pause_icon,null));
                  chatAudioviewholder.seekBar.setProgress(Chat_A.media_player_progress);
              } else {
                  chatAudioviewholder.play_btn.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_play_icon,null));
                  chatAudioviewholder.seekBar.setProgress(0);
              }


            chatAudioviewholder.bind(position,mDataSet.get(position), listener, long_listener);

        }

          else if (chat.getType().equals("delete")) {
            Alert_viewholder alertviewholder = (Alert_viewholder) holder;
            alertviewholder.message.setTextColor(context.getResources().getColor(R.color.colorBlack));
            alertviewholder.message.setBackground(context.getResources().getDrawable(R.drawable.d_bottom_gray_line));

            alertviewholder.message.setText(""+context.getString(R.string.this_message_was_deleted));

            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(11, 13).equals(chat.getTimestamp().substring(11, 13))) {
                    alertviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    alertviewholder.datetxt.setVisibility(View.VISIBLE);
                    alertviewholder.datetxt.setText(changeDate(chat.getTimestamp()));
                }

            } else {
                alertviewholder.datetxt.setVisibility(View.VISIBLE);
                alertviewholder.datetxt.setText(changeDate(chat.getTimestamp()));
            }

        }
    }

    @Override
    public int getItemViewType(int position) {

         if (mDataSet.get(position).getType().equals("text")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return mychat;
            }
            return friendchat;
        } else if (mDataSet.get(position).getType().equals("image")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return mychatimage;
            }

            return otherchatimage;
        } else if (mDataSet.get(position).getType().equals("audio")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return my_audio_message;
            }
            return other_audio_message;
        } else {
            return alert_message;
        }
    }



    public String changeDate(String date) {
        long currenttime = System.currentTimeMillis();

        long databasedate = 0;
        Date d = null;
        try {
            d = AllConstants.df.parse(date);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = currenttime - databasedate;
        if (difference < 86400000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if (today_day == chatday)
                return "Today";
            else if ((today_day - chatday) == 1)
                return "Yesterday";
        } else if (difference < 172800000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if ((today_day - chatday) == 1)
                return "Yesterday";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());

        if (d != null)
            return sdf.format(d);
        else
            return "";
    }


    public String showMessageTime(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.getDefault());

        Date d = null;
        try {
            d = AllConstants.df.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d != null)
            return sdf.format(d);
        else
            return "null";
    }

    public String changeDateToTime(String date) {

        Date d = null;
        try {
            d = AllConstants.df2.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.getDefault());

        if (d != null)
            return sdf.format(d);
        else
            return "";
    }


      public String getfileduration(Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            long second = (file_duration / 1000) % 60;
            long minute = (file_duration / (1000 * 60)) % 60;

            return String.format("%02d:%02d", minute, second);
        } catch (Exception e) {
            return null;
        }

    }
}
