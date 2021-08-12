package com.operialcompany.Democliente.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Models.ImageSliderModel;
import com.operialcompany.Democliente.Constants.Config;


import com.facebook.drawee.view.SimpleDraweeView;
import com.operialcompany.Democliente.R;

import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class SlidingImageAdapter extends PagerAdapter {

    private ArrayList<ImageSliderModel> images;
    private LayoutInflater inflater;


    public SlidingImageAdapter(Context context,ArrayList<ImageSliderModel> images) {
        this.images = images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final SimpleDraweeView imageView = (SimpleDraweeView) imageLayout
                .findViewById(R.id.image_slider);
        ImageSliderModel imageSliderModel = images.get(position);

        Uri uri = Uri.parse(Config.imgBaseURL+imageSliderModel.getSliderImageUrl());
        imageView.setImageURI(uri);

        Log.d(AllConstants.tag,Config.imgBaseURL+imageSliderModel.getSliderImageUrl());

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
