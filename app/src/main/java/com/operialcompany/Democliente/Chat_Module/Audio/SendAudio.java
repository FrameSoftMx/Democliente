package com.operialcompany.Democliente.Chat_Module.Audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.operialcompany.Democliente.Chat_Module.Chat_A;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.PreferenceClass;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SendAudio {


    DatabaseReference rootref;
    String senderid = "";
    String sendName = "";
    String sendPic = "";
    String Receiverid = "";
    String OrderId = "";
    String Receiver_name = "";
    String Receiver_pic = "null";
    String token;
    SharedPreferences preferences;
    Context context;

    private static String mFileName = null;
    private MediaRecorder mRecorder = null;

    private DatabaseReference Adduser_to_inbox;
    private String chatchild="";
    EditText message_field;


    public SendAudio(Context context, EditText message_field,
                     DatabaseReference rootref, DatabaseReference adduser_to_inbox, String senderid,
                     String receiverid, String OrderId, String receiver_name, String receiver_pic
            , String token) {

        this.context = context;
        preferences=context.getSharedPreferences(PreferenceClass.user,context.MODE_PRIVATE);
        this.message_field = message_field;
        this.rootref = rootref;
        this.Adduser_to_inbox = adduser_to_inbox;
        this.senderid = senderid;
        this.Receiverid = receiverid;
        this.OrderId=OrderId;
        this.Receiver_name = receiver_name;
        this.Receiver_pic = receiver_pic;
        this.token = token;
        mFileName = context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.mp3";

        if (OrderId.equalsIgnoreCase("0"))
        {
            chatchild=Receiverid + "-" + senderid;
        }
        else
        {
            chatchild=Receiverid + "-" + senderid+"-"+OrderId;
        }

    }


    public void startRecording() {

        try {


            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

            mRecorder = new MediaRecorder();

            if (mRecorder != null)
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            if (mRecorder != null)
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            if (mRecorder != null)
                mRecorder.setOutputFile(mFileName);

            if (mRecorder != null)
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                if (mRecorder != null)
                    mRecorder.prepare();
            } catch (IOException e) {
                Log.e("resp", "prepare() failed");
            }
            if (mRecorder != null)
                mRecorder.start();

        } catch (Exception e) {

        }
    }


    public void stopRecording() {
        try {


            stop_timer_without_recoder();
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
               // Runbeep("stop");
                UploadAudio();
            }

        } catch (Exception e) {

        }
    }


    public void UploadAudio() {

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = AllConstants.df.format(c);

        final StorageReference reference = FirebaseStorage.getInstance().getReference();
        DatabaseReference dref = rootref.child("chat").child(chatchild).push();
        final String key = dref.getKey();
        Chat_A.uploading_Audio_id = key;
        final String current_user_ref = "chat" + "/" + chatchild;
        final String chat_user_ref = "chat" + "/" + chatchild ;

        HashMap my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", Receiverid);
        my_dummi_pic_map.put("sender_id", senderid);
        my_dummi_pic_map.put("chat_id", key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type", "audio");
        my_dummi_pic_map.put("pic_url", "none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", sendName);
        my_dummi_pic_map.put("timestamp", formattedDate);

        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        rootref.updateChildren(dummy_push);

        Uri uri = Uri.fromFile(new File(mFileName));

        reference.child("Audio").child(key + ".mp3").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                reference.child("Audio").child(key + ".mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Chat_A.uploading_Audio_id = "none";

                        HashMap message_user_map = new HashMap<>();
                        message_user_map.put("receiver_id", Receiverid);
                        message_user_map.put("sender_id", senderid);
                        message_user_map.put("chat_id", key);
                        message_user_map.put("text", "");
                        message_user_map.put("type", "audio");
                        message_user_map.put("pic_url", uri.toString());
                        message_user_map.put("status", "0");
                        message_user_map.put("time", "");
                        message_user_map.put("sender_name",sendName);
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
                                sendermap.put("name", sendName);
                                sendermap.put("msg", "Send a Audio");
                                sendermap.put("pic", sendPic);
                                sendermap.put("status", "0");
                                sendermap.put("type", "store");
                                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                                sendermap.put("date", formattedDate);

                                HashMap receivermap = new HashMap<>();
                                receivermap.put("id", Receiverid);
                                receivermap.put("name", Receiver_name);
                                receivermap.put("msg", "Send a Audio");
                                receivermap.put("pic", Receiver_pic);
                                receivermap.put("status", "1");
                                receivermap.put("type", "store");
                                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                                receivermap.put("date", formattedDate);


                                Log.d(AllConstants.tag, sendermap.toString());
                                Log.d(AllConstants.tag, receivermap.toString());

                                HashMap both_user_map = new HashMap<>();
                                both_user_map.put(inbox_sender_ref, receivermap);
                                both_user_map.put(inbox_receiver_ref, sendermap);

                                Adduser_to_inbox.updateChildren(both_user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                    }
                                });
                            }
                        });

                    }
                });
            }
        });

    }



    public void stop_timer() {

        try {


            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

            message_field.setText(null);

        } catch (Exception e) {

        }
    }


    public void stop_timer_without_recoder() {

        try {

            message_field.setText(null);

        } catch (Exception e) {

        }
    }


}
