package com.operialcompany.Democliente.Constants;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by qboxus on 10/18/2019.
 */

public class AllConstants {


    public static int width=0;
    public static int height=0;

    public static String verdana = "verdana.ttf";
    public static String arial = "arial.ttf";

    public static final String CALCULATION = "CalculationAndroid";
    public static final String TRACKING = "tracking";

    public final static String tag="foodies_customer";

    public static String folder_parcel = Environment.getExternalStorageDirectory() +"/Foodies/";
    public static String folder_parcel_DCIM = Environment.getExternalStorageDirectory() +"/DCIM/Foodies/";
    public static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ",Locale.getDefault());
    public static SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mmZZ",Locale.getDefault());

    public static final int permission_location=790;
    public static final int permission_camera_code=786;
    public static final int permission_write_data=788;
    public static final int permission_Read_data=789;
    public static final int permission_Recording_audio=790;
    public static final int Request_code_Location=800;
    public static final boolean IsToastShow=true;

    public static final boolean ISShowAd=true;

    public static final int max_zoom=15;

    public static String Opened_Chat_id="";
}
