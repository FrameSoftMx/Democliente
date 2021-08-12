package com.operialcompany.Democliente.Constants;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.operialcompany.Democliente.GoogleMapWork.LatLngInterpolator;
import com.operialcompany.Democliente.Models.RestaurantsModel;
import com.operialcompany.Democliente.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MapWorker {

    GoogleMap googleMap;
    Context context;

    public Bitmap hotelLocationMarker, userMarker, riderMarker;


    public MapWorker(Context context, GoogleMap googleMap) {

        this.context=context;
        this.googleMap=googleMap;

        hotelLocationMarker = Bitmap.createScaledBitmap(((BitmapDrawable) ResourcesCompat
                .getDrawable(context.getResources(), R.drawable.hotel_pin,null)).getBitmap(), Functions.convertDpToPx(context,50), Functions.convertDpToPx(context,50), false);

        userMarker = Bitmap.createScaledBitmap(((BitmapDrawable) ResourcesCompat
                .getDrawable(context.getResources(),R.drawable.user_pin,null)).getBitmap(), Functions.convertDpToPx(context,50), Functions.convertDpToPx(context,50), false);

        riderMarker = Bitmap.createScaledBitmap(((BitmapDrawable) ResourcesCompat
                .getDrawable(context.getResources(),R.drawable.rider_pin,null)).getBitmap(), Functions.convertDpToPx(context,50), Functions.convertDpToPx(context,50), false);


    }


    public Marker add_marker(String tag, LatLng latLng, Bitmap marker_image){

        MarkerOptions markerOptions=new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(marker_image));
        Marker m=googleMap.addMarker(markerOptions);
        m.setTag(tag);
        return m;
    }

    public Marker add_marker(LatLng latLng, Bitmap marker_image){

        if(latLng!=null && marker_image!=null){
        MarkerOptions markerOptions=new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(marker_image));
        Marker m=googleMap.addMarker(markerOptions);

        return m;

        }
        else
            return null;
    }


    long DURATION_MS = 5000;
    public void animateMarkerTo(final Marker marker, final double latitude, final double longitude) {

        final Handler handler = new Handler();
        final LatLngInterpolator latLngInterpol = new LatLngInterpolator.LinearFixed();
        final Interpolator interpolator = new LinearInterpolator();

        final LatLng startPosition = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        final long start = SystemClock.uptimeMillis();

        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t=elapsed/DURATION_MS;
                float v = interpolator.getInterpolation(t);

                LatLng latLng=latLngInterpol.interpolate(v,startPosition,new LatLng(latitude,longitude));

                marker.setPosition(latLng);

                if (t < 1) {
                    handler.postDelayed(this,16);
                }
            }
        });



    }



    Handler handler;
    Runnable runnable;
    public void animateMarker_with_Map(final Marker marker, final double latitude, final double longitude) {


        final LatLngInterpolator latLngInterpol = new LatLngInterpolator.LinearFixed();
        final Interpolator interpolator = new LinearInterpolator();

        final LatLng startPosition = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        final long start = SystemClock.uptimeMillis();

        if(handler!=null && runnable!=null)
            handler.removeCallbacks(runnable);

        handler = new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t=elapsed/DURATION_MS;
                float v = interpolator.getInterpolation(t);

                final LatLng latLng=latLngInterpol.interpolate(v,startPosition,new LatLng(latitude,longitude));

                marker.setPosition(latLng);

                if (t < 1) {
                    if(handler!=null)
                    handler.postDelayed(this,14);
                }
            }
        };
        handler.post(runnable);



    }


    public void rotateMarker(final Marker marker, final LatLng destination, final double rotation) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(2000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    try {
                        float v = animation.getAnimatedFraction();
                        double bearing = Functions.computeRotation(v, startRotation, rotation);

                        marker.setRotation((float) bearing);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }


    public void animateCameraTo(final GoogleMap googleMap, final double lat, final double lng, float zoomlevel) {

            googleMap.getUiSettings().setScrollGesturesEnabled(false);

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),zoomlevel), new GoogleMap.CancelableCallback()
            {

                @Override
                public void onFinish()
                {
                    googleMap.getUiSettings().setScrollGesturesEnabled(true);
                    googleMap.getUiSettings().setRotateGesturesEnabled(false);
                }

                @Override
                public void onCancel()
                {
                    googleMap.getUiSettings().setScrollGesturesEnabled(true);
                    googleMap.getUiSettings().setRotateGesturesEnabled(false);
                }
            });

    }


    public void animateCameraTo(final GoogleMap googleMap, CameraUpdate cameraUpdate) {
            googleMap.getUiSettings().setScrollGesturesEnabled(false);

            googleMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback()
            {

                @Override
                public void onFinish() {
                    googleMap.getUiSettings().setScrollGesturesEnabled(true);
                    googleMap.getUiSettings().setRotateGesturesEnabled(false);
                }

                @Override
                public void onCancel()
                {
                    googleMap.getUiSettings().setScrollGesturesEnabled(true);
                    googleMap.getUiSettings().setRotateGesturesEnabled(false);

                }
            });
        }

    HashMap<String, Marker> marker_hash;
    public HashMap Add_Saved_places_Marker(ArrayList<RestaurantsModel> list){

        if(marker_hash==null)
            marker_hash=new HashMap<>();

            for(RestaurantsModel item:list){

                if(marker_hash.containsKey(item.restaurant_id)) {
                    Marker marker = marker_hash.get(item.restaurant_id);
                    marker.remove();
                }

                Marker heart_marker = add_marker(item.restaurant_id,new LatLng(Double.parseDouble(item.lat), Double.parseDouble(item.lng)), getLiked_marker(context, item.restaurant_name));
                marker_hash.put(item.restaurant_id, heart_marker);

            }

            return marker_hash;
    }

    public static Bitmap getLiked_marker(Context context , String home) {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_restaurent_map_layout, null);
        TextView location_liked =  customMarkerView.findViewById(R.id.place_name);
        location_liked.setText(home);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public static RestaurantsModel Get_nearby_saved_place(LatLng latLng){

//        boolean is_list_exits= Paper.book().contains(Variables.saved_places_list);
//        if(is_list_exits){
//            ArrayList<SavedPlacesModel> place_list=Paper.book().read(Variables.saved_places_list,new ArrayList<SavedPlacesModel>());
//            for(SavedPlacesModel item:place_list){
//
//                if(Functions.calculateDistance(latLng.latitude,latLng.longitude,item.lat,item.lng)<30){
//                    return item;
//                }
//
//            }
//
//        }

        return null;
    }

}
