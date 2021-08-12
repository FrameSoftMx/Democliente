package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.ApiRequest;
import com.operialcompany.Democliente.Constants.Callback;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.DarkModePrefManager;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.ContextWrapper;
import com.operialcompany.Democliente.Utils.NotificationUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by qboxus on 10/18/2019.
 */

public class MainActivity extends AppCompatActivity {
    private PagerMainActivity mCurrentFrag;
    public static Context context;
    private long mBackPressed;
    public static SharedPreferences sPre;
    public static boolean FLAG_MAIN;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    SharedPreferences sharedPreferences;

    @Override
    protected void attachBaseContext(Context newBase) {

        String[] language_array = newBase.getResources().getStringArray(R.array.language_code);
        List<String> language_code = Arrays.asList(language_array);
        sharedPreferences = newBase.getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);

        PreferenceClass.sharedPreferences = sharedPreferences;

        String language = sharedPreferences.getString(PreferenceClass.selected_language, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && language_code.contains(language)) {
            Locale newLocale = new Locale(language);
            Context context = ContextWrapper.wrap(newBase, newLocale);
            super.attachBaseContext(context);
        }

        else {
            super.attachBaseContext(newBase);
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
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(getApplicationContext());

        sPre = getSharedPreferences(PreferenceClass.user, MODE_PRIVATE);


        FLAG_MAIN = true;
        context = MainActivity.this;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        AllConstants.width = size.x;
        AllConstants.height = size.y;


        mCurrentFrag = new PagerMainActivity();
        if (mCurrentFrag != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.main_container, mCurrentFrag).commit();

        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                }

                else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {

                    String message = intent.getStringExtra("message");

                    sendMyNotification(message);

                    sendBroadcast(new Intent("newOrder"));
                }

            }
        };

        Log.d(AllConstants.tag,"onCreateCall");

        if(AllConstants.ISShowAd)
        callAddHandler();


        printKeyHash();

        getPublicIP();

    }


    public void set_language_local() {
        String[] language_array = getResources().getStringArray(R.array.language_code);
        List<String> language_code = Arrays.asList(language_array);

        String language = sharedPreferences.getString(PreferenceClass.selected_language, "");


        if (language_code.contains(language)) {
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
    public void onBackPressed() {
        if (!mCurrentFrag.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Toast.makeText(getBaseContext(), R.string.tab_again_to_exit, Toast.LENGTH_SHORT).show();

                    mBackPressed = System.currentTimeMillis();

                }
            } else {
                super.onBackPressed();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RestaurantMenuItemsFragment.PERMISSION_DATA_CART_ADED) {

            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                intent.setAction("AddToCart");
                sendBroadcast(intent);
                SearchFragment.FLAG_COUNTRY_NAME = true;
            }
        } else if (requestCode == AllConstants.Request_code_Location) {
            checkLocationStatus();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();


        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());


        checkLocationStatus();

    }


    private void checkLocationStatus() {

        if (!Functions.checkLocationStatus(this)) {
            startActivityForResult(new Intent(this, Enable_location_A.class), AllConstants.Request_code_Location);

        }

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

    }


    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("keyhash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public void sendMyNotification(String message) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("channel-01",
                    getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel-01");
        builder.setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_icon))
                .setColor(ContextCompat.getColor(this, R.color.colorRed))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setContentText(String.format(message))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setChannelId("channel-01");

        notificationManager.notify(0, builder.build());

    }



    @Override
    protected void onStop() {
        super.onStop();
        Remove_CallBack();
    }

    Handler handler;
    Runnable runnable;
    public void callAddHandler(){
        if(handler!=null && runnable!=null)
            handler.removeCallbacks(runnable);

        runnable=new Runnable() {
            @Override
            public void run() {
                Load_Add();
            }
        };
        handler=new Handler();
        handler.postDelayed(runnable,3000);
    }

    public void Remove_CallBack(){
        if(handler!=null && runnable!=null)
            handler.removeCallbacks(runnable);

    }

    private InterstitialAd mInterstitialAd;

    public void Load_Add() {

        //code for intertial add
        if(mInterstitialAd==null) {
            mInterstitialAd = new InterstitialAd(context);
            //here we will get the add id keep in mind above id is app id and below Id is add Id
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.my_Interstitial_Add));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {

                }

                @Override
                public void onAdLoaded() {
                    mInterstitialAd.setAdListener(null);
                    mInterstitialAd.show();
                }
            });
        }
    }


    public void getPublicIP() {
        ApiRequest.Call_Api_GetRequest(this, "https://api.ipify.org/?format=json", resp -> {
            try {

                JSONObject responce = new JSONObject(resp);
                String ip = responce.optString("ip");

                addFirebaseTokon(ip);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void addFirebaseTokon(String ip) {


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult();
                        PreferenceClass.getSharedPreference(MainActivity.this).edit().putString(PreferenceClass.device_token, token).commit();

                        JSONObject params = new JSONObject();
                        try {
                            params.put("user_id", PreferenceClass.getSharedPreference(MainActivity.this).getString(PreferenceClass.pre_user_id, ""));
                            params.put("device", getString(R.string.device));
                            params.put("version", getString(R.string.version));
                            params.put("ip", "" + ip);
                            params.put("device_token", token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ApiRequest.callApi(MainActivity.this, Config.addDeviceData, params, null);

                    }
                });
    }


}

