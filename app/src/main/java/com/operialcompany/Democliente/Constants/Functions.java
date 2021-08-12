package com.operialcompany.Democliente.Constants;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.gms.maps.model.LatLng;
import com.operialcompany.Democliente.R;


import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.DecimalFormat;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by qboxus on 10/18/2019.
 */

public class Functions {

    public static void Hide_keyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            UIUtil.hideKeyboard(activity);
        }
    }


    public static Dialog dialog;
    public static void showLoader(Context context, boolean outside_touch, boolean cancleable) {
        Functions.cancelLoader();

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dialog_loading_view);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.round_shape_dialog_white));


        CamomileSpinner loader = dialog.findViewById(R.id.loader);
        loader.start();


        if (!outside_touch)
            dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            dialog.setCancelable(false);

        dialog.show();
    }


    public static void cancelLoader() {
        if (dialog != null) {
            dialog.cancel();
        }
    }


    public static Double roundoffDecimal(Double value) {
        return Double.valueOf(new DecimalFormat("##.##").format(value));

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public static int convertDpToPx(Context context, int dp) {
        return (int) ((int) dp * context.getResources().getDisplayMetrics().density);
    }

    // this is the delete message diloge which will show after long press in chat message
    public static void Show_Options(Context context, CharSequence[] options, final Callback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                callback.onResponce("" + options[item]);

            }

        });

        builder.show();

    }


    public static void ShowToast(Context context, String msg) {
        if (AllConstants.IsToastShow) {
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
        }
    }


    public static String Check_Image_Status(Context context, long Image_DownloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();

        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(Image_DownloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if (cursor.moveToFirst()) {
            return DownloadStatus(cursor, Image_DownloadId);
        }
        return "";
    }


    private static String DownloadStatus(Cursor cursor, long DownloadId) {

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                return "STATUS_FAILED";

            case DownloadManager.STATUS_PAUSED:
                return "";

            case DownloadManager.STATUS_PENDING:
                return "";

            case DownloadManager.STATUS_RUNNING:
                return "";

            case DownloadManager.STATUS_SUCCESSFUL:
                return "STATUS_SUCCESSFUL";
            default:
                return "none";
        }

    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int Get_screen_width(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int Get_screen_height(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static double getBearingBetweenTwoPoints1(LatLng latLng1, LatLng latLng2) {

        double lat1 = degreesToRadians(latLng1.latitude);
        double long1 = degreesToRadians(latLng1.longitude);
        double lat2 = degreesToRadians(latLng2.latitude);
        double long2 = degreesToRadians(latLng2.longitude);


        double dLon = (long2 - long1);


        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double radiansBearing = Math.atan2(y, x);


        return radiansToDegrees(radiansBearing);
    }

    public static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }

    public static double radiansToDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }

    public static double computeRotation(float fraction, double start, double end) {
        double normalizeEnd = end - start;
        double normalizedEndAbs = (normalizeEnd + 360) % 360;

        double direction = (normalizedEndAbs > 180) ? -1 : 1;
        double rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        double result = fraction * rotation + start;
        return (result + 360) % 360;
    }


    public static long calculateDistance(double lat1, double lng1, double lat2, double lng2) {


        double dLat = Math.toRadians(lat1 - lat2);
        double dLon = Math.toRadians(lng1 - lng2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMeters = Math.round(6371000 * c);
        return distanceInMeters;
    }


    public static boolean checkLocationStatus(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {
            return false;
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;

    }




}
