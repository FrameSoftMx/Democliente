package com.operialcompany.Democliente.Chat_Module;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.operialcompany.Democliente.Chat_Module.Audio.SendAudio;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.ContextWrapper;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Chat_A extends AppCompatActivity {

    DatabaseReference rootref;
    public String token = "null";
    EditText message;
    private DatabaseReference Adduser_to_inbox;
    public DatabaseReference mchatRef_reteriving;
    RecyclerView chatrecyclerview;
    TextView reciver_name;
    private List<ChatModel> mChats = new ArrayList<>();
    ChatAdapter mAdapter;
    ProgressBar p_bar;
    Query query_getchat;
    public String senderid, Receiverid, Receiver_name, Receiver_pic,Order_id;
    private String chatchild="";
    public String sender_name, rider_pic;
    private RelativeLayout write_layout;
    private  RecordView recordView;
    private ImageView sendbtn;
    private RecordButton mic_btn;
    private SendAudio sendAudio;
    File direct;
    SharedPreferences preferences;

    public static String uploading_image_id = "none";
    public static String uploading_Audio_id = "none";

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void attachBaseContext(Context newBase) {

        String[] language_array = newBase.getResources().getStringArray(R.array.language_code);
        List<String> language_code = Arrays.asList(language_array);
        preferences = newBase.getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);

        PreferenceClass.sharedPreferences=preferences;

        String language = preferences.getString(PreferenceClass.selected_language, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && language_code.contains(language)) {
            Locale newLocale = new Locale(language);
            Context context = ContextWrapper.wrap(newBase, newLocale);
            super.attachBaseContext(context);
        }
        else {
            super.attachBaseContext(newBase);
        }

    }


    public void set_language_local(){
        String [] language_array=getResources().getStringArray(R.array.language_code);
        List <String> language_code= Arrays.asList(language_array);

        String language=preferences.getString(PreferenceClass.selected_language,"");


        if(language_code.contains(language)) {
            Locale myLocale = new Locale(language);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = new Configuration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            onConfigurationChanged(conf);

        }



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            set_language_local();
        }

        setContentView(R.layout.activity_chat);


        preferences=getSharedPreferences(PreferenceClass.user,Context.MODE_PRIVATE);

        Intent bundle = getIntent();
        direct = new File(AllConstants.folder_parcel);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);


        rootref = FirebaseDatabase.getInstance().getReference();
        Adduser_to_inbox = FirebaseDatabase.getInstance().getReference();
        message = (EditText) findViewById(R.id.msgedittext);
        reciver_name = findViewById(R.id.reciver_name);
        write_layout=findViewById(R.id.typing_layout);

        rider_pic = "null";

        sender_name = preferences.getString(PreferenceClass.pre_first,"")+preferences.getString(PreferenceClass.pre_last,"");

        if (bundle != null) {

            senderid = bundle.getStringExtra("senderid");
            Receiverid = bundle.getStringExtra("Receiverid");
            Receiver_name = bundle.getStringExtra("Receiver_name");
            Receiver_pic = bundle.getStringExtra("Receiver_pic");
            Order_id = bundle.getStringExtra("Order_id");
            if (Order_id.equalsIgnoreCase("0"))
            {
                chatchild=Receiverid + "-" + senderid;
            }
            else
            {
                chatchild=Receiverid + "-" + senderid+"-"+Order_id;
            }
            Log.d(AllConstants.tag, "Receiver id : " + Receiverid);
            reciver_name.setText(Receiver_name);

        }


        token = "null";
        rootref.child("Users").child(Receiverid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    token = dataSnapshot.child("token").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        p_bar = findViewById(R.id.progress_bar);

        chatrecyclerview = (RecyclerView) findViewById(R.id.chatlist);
        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true);
        chatrecyclerview.setLayoutManager(layout);
        chatrecyclerview.setHasFixedSize(false);
        ((SimpleItemAnimator) chatrecyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new ChatAdapter(mChats, senderid, this, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, ChatModel item, View v) {

                if (item.getType().equals("image"))
                    OpenfullsizeImage(item);

                if (v.getId() == R.id.audio_bubble) {
                    RelativeLayout mainlayout = (RelativeLayout) v.getParent();
                    File fullpath = new File(AllConstants.folder_parcel + item.chat_id + ".mp3");
                    if (fullpath.exists()) {

                        if(playing_id.equals(item.chat_id)){
                            Stop_playing();
                        }
                        else {
                            if (check_ReadStoragepermission())
                            Play_audio(postion,item);
                        }

                    }
                    else {
                        download_audio((ProgressBar) mainlayout.findViewById(R.id.p_bar), item);
                    }

                }
            }

        }, new ChatAdapter.OnLongClickListener() {
            @Override
            public void onLongclick(ChatModel item, View view) {

                if (senderid.equals(item.getSender_id()))
                    delete_Message_dialog(item);

            }
        });

        chatrecyclerview.setAdapter(mAdapter);

        chatrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = layout.findFirstCompletelyVisibleItemPosition();

                if (userScrolled && (scrollOutitems == 0 && mChats.size() > 9)) {
                    userScrolled = false;

                    rootref.child("chat").child(chatchild).orderByChild("chat_id")
                            .endAt(mChats.get(0).getChat_id()).limitToLast(20)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<ChatModel> arrayList = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ChatModel item = snapshot.getValue(ChatModel.class);
                                        arrayList.add(item);
                                    }
                                    for (int i = arrayList.size() - 2; i >= 0; i--) {
                                        mChats.add(0, arrayList.get(i));
                                    }

                                    mAdapter.notifyDataSetChanged();

                                    if (arrayList.size() > 8) {
                                        chatrecyclerview.scrollToPosition(arrayList.size());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });
        sendbtn = findViewById(R.id.sendbtn);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(message.getText().toString())) {
                    SendMessage(message.getText().toString());
                    message.setText(null);
                }
            }
        });

        findViewById(R.id.uploadfilebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectfile();
            }
        });

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    sendbtn.setVisibility(View.VISIBLE);
                    mic_btn.setVisibility(View.GONE);
                } else {
                    sendbtn.setVisibility(View.GONE);
                    mic_btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sendAudio = new SendAudio(this, message, rootref, Adduser_to_inbox,
                senderid, Receiverid,Order_id, Receiver_name, Receiver_pic, token);

        mic_btn = findViewById(R.id.mic_btn);
        recordView = (RecordView)findViewById(R.id.record_view);
        mic_btn.setRecordView(recordView);
        recordView.setSoundEnabled(true);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                write_layout.setVisibility(View.GONE);
                recordView.setVisibility(View.VISIBLE);
                if (check_Recordpermission() && check_writeStoragepermission()) {
                    sendAudio.startRecording();
                }
            }

            @Override
            public void onCancel() {

                sendAudio.stop_timer();
                recordView.setVisibility(View.GONE);
                write_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish(long recordTime) {
                sendAudio.stopRecording();
                recordView.setVisibility(View.GONE);
                write_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLessThanSecond() {
                sendAudio.stop_timer_without_recoder();
                recordView.setVisibility(View.GONE);
                write_layout.setVisibility(View.VISIBLE);
            }
        });
        recordView.setSlideToCancelText(getString(R.string.slide_to_cancel));
        mic_btn.setListenForRecord(true);
        recordView.setLessThanSecondAllowed(false);

    }


    ValueEventListener valueEventListener;
    ChildEventListener eventListener;

    @Override
    public void onStart() {
        super.onStart();

        AllConstants.Opened_Chat_id = Receiverid;

        mChats.clear();
        mchatRef_reteriving = FirebaseDatabase.getInstance().getReference();

        query_getchat = mchatRef_reteriving.child("chat").child(chatchild);

        // this will get all the messages between two users
        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    ChatModel model = dataSnapshot.getValue(ChatModel.class);
                    mChats.add(model);
                    mAdapter.notifyDataSetChanged();
                    chatrecyclerview.scrollToPosition(mChats.size() - 1);
                } catch (Exception ex) {
                    Log.e("", ex.getMessage());
                }
                ChangeStatus();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    try {
                        ChatModel model = dataSnapshot.getValue(ChatModel.class);

                        for (int i = mChats.size() - 1; i >= 0; i--) {
                            if (mChats.get(i).getTimestamp().equals(dataSnapshot.child("timestamp").getValue())) {
                                mChats.remove(i);
                                mChats.add(i, model);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Log.e("", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", databaseError.getMessage());
            }
        };
        // this will check the two user are do chat before or not
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatchild)) {
                    p_bar.setVisibility(View.GONE);
                    query_getchat.removeEventListener(valueEventListener);
                } else {
                    p_bar.setVisibility(View.GONE);
                    query_getchat.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query_getchat.limitToLast(20).addChildEventListener(eventListener);
        mchatRef_reteriving.child("chat").addValueEventListener(valueEventListener);
    }


    // this method will change the status to ensure that
    // user is seen all the message or not (in both chat node and Chatinbox node)
    public void ChangeStatus() {
        final Date c = Calendar.getInstance().getTime();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final Query query1 = reference.child("chat").child(chatchild ).orderByChild("status").equalTo("0");
        final Query query2 = reference.child("chat").child(chatchild).orderByChild("status").equalTo("0");

        final DatabaseReference inbox_change_status_1 = reference.child("Inbox").child(chatchild);
        final DatabaseReference inbox_change_status_2 = reference.child("Inbox").child(chatchild);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)) {
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", AllConstants.df2.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)) {
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", AllConstants.df2.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        inbox_change_status_1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("id").getValue().equals(Receiverid)) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        inbox_change_status_1.updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        inbox_change_status_2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("id").getValue().equals(Receiverid)) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        inbox_change_status_2.updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // this will add the new message in chat node and update the ChatInbox by new message by present date
    public void SendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = AllConstants.df.format(c);

        final String current_user_ref = "chat" + "/" + chatchild;
        final String chat_user_ref = "chat" + "/" + chatchild ;

        DatabaseReference reference = rootref.child("chat").child(chatchild).push();
        final String pushid = reference.getKey();
        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", Receiverid);
        message_user_map.put("sender_id", senderid);
        message_user_map.put("chat_id", pushid);
        message_user_map.put("text", message);
        message_user_map.put("type", "text");
        message_user_map.put("pic_url", "");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", sender_name);
        message_user_map.put("timestamp", formattedDate);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);
        user_map.put(chat_user_ref + "/" + pushid, message_user_map);

        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                //if first message then set the visibility of whoops layout gone
                String inbox_sender_ref = "Inbox" + "/" + chatchild;
                String inbox_receiver_ref = "Inbox" + "/" + chatchild;

                HashMap sendermap = new HashMap<>();
                sendermap.put("id", senderid);
                sendermap.put("name", sender_name);
                sendermap.put("msg", message);
                sendermap.put("pic", rider_pic);
                sendermap.put("status", "0");
                sendermap.put("type", "store");
                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                sendermap.put("date", formattedDate);


                HashMap receivermap = new HashMap<>();
                receivermap.put("id", Receiverid);
                receivermap.put("name", Receiver_name);
                receivermap.put("msg", message);
                receivermap.put("pic", Receiver_pic);
                receivermap.put("status", "1");
                receivermap.put("type", "store");
                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                receivermap.put("date", formattedDate);

                HashMap both_user_map = new HashMap<>();
                both_user_map.put(inbox_sender_ref, receivermap);
                both_user_map.put(inbox_receiver_ref, sendermap);

                Adduser_to_inbox.updateChildren(both_user_map);
            }
        });
    }


    // this method will upload the image in chhat
    public void UploadImage(ByteArrayOutputStream byteArrayOutputStream) {
        byte[] data = byteArrayOutputStream.toByteArray();

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = AllConstants.df.format(c);

        final StorageReference reference = FirebaseStorage.getInstance().getReference();

        DatabaseReference dref = rootref.child("chat").child(chatchild).push();
        final String key = dref.getKey();
        uploading_image_id = key;

        final String current_user_ref = "chat" + "/" + chatchild;
        final String chat_user_ref = "chat" + "/" + chatchild ;

        HashMap my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", Receiverid);
        my_dummi_pic_map.put("sender_id", senderid);
        my_dummi_pic_map.put("chat_id", key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type", "image");
        my_dummi_pic_map.put("pic_url", "none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", sender_name);
        my_dummi_pic_map.put("timestamp", formattedDate);
        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        rootref.updateChildren(dummy_push);

        reference.child("images").child(key + ".jpg").putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                reference.child("images").child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        uploading_image_id = "none";

                        HashMap message_user_map = new HashMap<>();
                        message_user_map.put("receiver_id", Receiverid);
                        message_user_map.put("sender_id", senderid);
                        message_user_map.put("chat_id", key);
                        message_user_map.put("text", "");
                        message_user_map.put("type", "image");
                        message_user_map.put("pic_url", uri.toString());
                        message_user_map.put("status", "0");
                        message_user_map.put("time", "");
                        message_user_map.put("sender_name", sender_name);
                        message_user_map.put("timestamp", formattedDate);

                        HashMap user_map = new HashMap<>();

                        user_map.put(current_user_ref + "/" + key, message_user_map);
                        user_map.put(chat_user_ref + "/" + key, message_user_map);

                        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                String inbox_sender_ref = "Inbox" + "/" + chatchild;
                                String inbox_receiver_ref = "Inbox" + "/" + chatchild;

                                HashMap sendermap = new HashMap<>();
                                sendermap.put("id", senderid);
                                sendermap.put("name", sender_name);
                                sendermap.put("msg", "Send an image...");
                                sendermap.put("pic", rider_pic);
                                sendermap.put("status", "0");
                                sendermap.put("type", "store");
                                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                                sendermap.put("date", formattedDate);
                                HashMap receivermap = new HashMap<>();
                                receivermap.put("id", Receiverid);
                                receivermap.put("name", Receiver_name);
                                receivermap.put("msg", "Send an image...");
                                receivermap.put("pic", Receiver_pic);
                                receivermap.put("status", "1");
                                receivermap.put("type", "store");
                                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                                receivermap.put("date", formattedDate);

                                HashMap both_user_map = new HashMap<>();
                                both_user_map.put(inbox_sender_ref, receivermap);
                                both_user_map.put(inbox_receiver_ref, sendermap);

                                Adduser_to_inbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //  SendPushNotification(getActivity(),"Send an image");
                                    }
                                });
                            }
                        });

                    }
                });

            }
        });
    }


    // this is the delete message diloge which will show after long press in chat message
    private void delete_Message_dialog(final ChatModel chat_getSet) {
        final CharSequence[] options;
        if (chat_getSet.getType().equals("text")) {
            options = new CharSequence[]{Chat_A.this.getString(R.string.copy),Chat_A.this.getString(R.string.delete_this_message),Chat_A.this.getString(R.string.cancel) };
        } else {

            options = new CharSequence[]{Chat_A.this.getString(R.string.delete_this_message),Chat_A.this.getString(R.string.cancel)};
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogStyle);

        builder.setTitle(null);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(Chat_A.this.getString(R.string.delete_this_message))) {
                    update_message(chat_getSet);
                } else if (options[item].equals(Chat_A.this.getString(R.string.cancel))) {
                    dialog.dismiss();
                } else if (options[item].equals(Chat_A.this.getString(R.string.copy))) {

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", chat_getSet.getText());
                    clipboard.setPrimaryClip(clip);
                }
            }
        });
        builder.show();
    }


    // we will update the privious message means we will tells the other user that we have seen your message
    public void update_message(ChatModel item) {
        final String current_user_ref = "chat" + "/" + chatchild;
        final String chat_user_ref = "chat" + "/" + chatchild;


        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", item.getReceiver_id());
        message_user_map.put("sender_id", item.getSender_id());
        message_user_map.put("chat_id", item.getChat_id());
        message_user_map.put("text", "Delete this message");
        message_user_map.put("type", "delete");
        message_user_map.put("pic_url", "");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", sender_name);
        message_user_map.put("timestamp", item.getTimestamp());

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + item.getChat_id(), message_user_map);
        user_map.put(chat_user_ref + "/" + item.getChat_id(), message_user_map);

        rootref.updateChildren(user_map);

    }


    // this method will show the dialog of select the either take a picture form camera or pick the image from gallary
    private void selectfile() {

        final CharSequence[] options = {Chat_A.this.getString(R.string.take_photo),Chat_A.this.getString(R.string.choose_photo_from_gallery),Chat_A.this.getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogStyle);

        builder.setTitle("Select");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(Chat_A.this.getString(R.string.take_photo))) {
                    if (check_camrapermission())
                        openCameraIntent();
                } else if (options[item].equals(Chat_A.this.getString(R.string.choose_photo_from_gallery))) {
                    if (check_ReadStoragepermission()) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                } else if (options[item].equals(Chat_A.this.getString(R.string.cancel))) {

                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    // below tis the four types of permission
    //get the permission to record audio
    public boolean check_Recordpermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {

            sendAudio.stop_timer_without_recoder();
            recordView.setVisibility(View.GONE);
            write_layout.setVisibility(View.VISIBLE);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    AllConstants.permission_Recording_audio);
        }
        return false;
    }

    private boolean check_camrapermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, AllConstants.permission_camera_code);
        }
        return false;
    }

    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        AllConstants.permission_Read_data);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    private boolean check_writeStoragepermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        AllConstants.permission_write_data);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AllConstants.permission_camera_code) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Functions.ShowToast(this, getString(R.string.tap_again));

            } else {
                Functions.ShowToast(this, getString(R.string.camera_denied));
            }
        }

        if (requestCode == AllConstants.permission_Read_data) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Functions.ShowToast(this,getString(R.string.tap_again));
            }
        }

        if (requestCode == AllConstants.permission_write_data) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Functions.ShowToast(this, getString(R.string.tap_again));
            }
        }


        if (requestCode == AllConstants.permission_Recording_audio) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Functions.ShowToast(this, getString(R.string.tap_again));
            }
        }
    }

    // below three method is related with taking the picture from camera
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, 1);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor =getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = Chat_A.this.getString(R.string.not_found);
        }
        return result;
    }

    //on image select activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
                Matrix matrix = new Matrix();
                try {
                    ExifInterface exif = new ExifInterface(imageFilePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));

                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                UploadImage(baos);

            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

                String path = getPath(selectedImage);
                Matrix matrix = new Matrix();
                ExifInterface exif = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    try {
                        exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                UploadImage(baos);
            }
        }
    }



    // on destory delete the typing indicator
    @Override
    public void onDestroy() {
        uploading_image_id = "none";
        query_getchat.removeEventListener(eventListener);
        super.onDestroy();
    }

    // on stop delete the typing indicator and remove the value event listener
    @Override
    public void onStop() {
        super.onStop();
        query_getchat.removeEventListener(eventListener);
    }


    //this method will get the big size of image in private chat
    public void OpenfullsizeImage(ChatModel item) {
        SeeFullImage_F see_image_f = new SeeFullImage_F();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle args = new Bundle();
        args.putSerializable("image_url", item.getPic_url());
        args.putSerializable("chat_id", item.getChat_id());
        see_image_f.setArguments(args);
        transaction.addToBackStack(null);

        transaction.replace(R.id.Chat_F, see_image_f).commit();
    }



    public static MediaPlayer mediaplayer;
    public static String playing_id="none";
    public static int media_player_progress=0;
    public int audio_postion;
    public void Play_audio(int postion, ChatModel item){

        audio_postion=postion;
        media_player_progress=0;

        Stop_playing();

        File fullpath = new File(AllConstants.folder_parcel + item.chat_id + ".mp3");
        if (fullpath.exists()) {
            Uri uri= Uri.parse(fullpath.getAbsolutePath());

            mediaplayer = MediaPlayer.create(this, uri);

            if(mediaplayer!=null){
                mediaplayer.start();
                Countdown_timer(true);

                mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Stop_playing();
                    }
                });
                playing_id= item.chat_id;
                mAdapter.notifyDataSetChanged();
            }

        }
    }

    public void Stop_playing(){
        playing_id="none";
        Countdown_timer(false);
        mAdapter.notifyDataSetChanged();
        if(mediaplayer !=null){
            mediaplayer.reset();
            mediaplayer.release();
            mediaplayer =null;
        }
    }


    CountDownTimer countDownTimer;
    public void Countdown_timer(boolean starttimer){

        if(countDownTimer!=null)
            countDownTimer.cancel();


            if (starttimer) {
                countDownTimer = new CountDownTimer(mediaplayer.getDuration(), 300) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        media_player_progress = ((mediaplayer.getCurrentPosition() *100) / mediaplayer.getDuration());
                        if (media_player_progress > 95) {
                            Countdown_timer(false);
                            media_player_progress=0;
                        }
                        mAdapter.notifyItemChanged(audio_postion);
                    }

                    @Override
                    public void onFinish() {
                        media_player_progress=0;
                        Countdown_timer(false);
                        mAdapter.notifyItemChanged(audio_postion);
                    }
                };
                countDownTimer.start();


        }

    }

    public void download_audio(final ProgressBar p_bar, ChatModel item) {
        p_bar.setVisibility(View.VISIBLE);
        PRDownloader.download(item.getPic_url(), direct.getPath(), item.getChat_id() + ".mp3")
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        p_bar.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Error error) {

                    }

                });
    }

}
