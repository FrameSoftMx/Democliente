package com.operialcompany.Democliente.Constants;

import android.app.Application;

//import com.crashlytics.android.Crashlytics;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;


/**
 * Created by qboxus on 10/18/2019.
 */

public class Foodies extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {

            FirebaseApp.initializeApp(this);
            Fresco.initialize(this);

        }catch (Exception e){

        }
    }




}
