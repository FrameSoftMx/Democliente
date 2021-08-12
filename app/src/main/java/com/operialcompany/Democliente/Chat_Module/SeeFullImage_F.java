package com.operialcompany.Democliente.Chat_Module;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.CircleProgressBarDrawable;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.Constants.PreferenceClass;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;

import java.io.File;


public class SeeFullImage_F extends RootFragment implements View.OnClickListener {


    private View view;
    private Context context;
    private SimpleDraweeView single_image;
    private String image_url, chat_id;
    private ProgressDialog progressDialog;
    // this is the third party library that will download the image
    Uri uri;
    SharedPreferences preferences;
    private File direct;
    private File fullpath;
    private Boolean isfromchatscreen;
    private DownloadRequest prDownloader;

    public SeeFullImage_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.see_full_img_f, container, false);
        context = getContext();
        preferences=context.getSharedPreferences(PreferenceClass.user,Context.MODE_PRIVATE);
        isfromchatscreen = getArguments().getBoolean("isfromchat");

        Log.d("isfromchatscreen", "" + isfromchatscreen);
        if (isfromchatscreen) {
            image_url = getArguments().getString("image_url");

        } else {
            image_url = getArguments().getString("image_url");
            chat_id = getArguments().getString("chat_id");
        }

        ImageView close_gallery = view.findViewById(R.id.close_gallery);
        close_gallery.setOnClickListener(this);


        progressDialog = new ProgressDialog(context, R.style.MyDialogStyle);
        progressDialog.setMessage("Please Wait");

        PRDownloader.initialize(getActivity().getApplicationContext());


        //get the full path of image in database
        fullpath = new File(AllConstants.folder_parcel + chat_id + ".jpg");

        //if the image file is exits then we will hide the save btn
        ImageView savebtn = view.findViewById(R.id.savebtn);
        if (fullpath.exists()) {
            savebtn.setVisibility(View.GONE);
        }

        //get the directory inwhich we want to save the image
        direct = new File(AllConstants.folder_parcel);

        //this code will download the image
        prDownloader = PRDownloader.download(image_url, direct.getPath(), chat_id + ".jpg")
                .build();

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Savepicture();
            }
        });

        single_image = view.findViewById(R.id.single_image);

        // if the image is already save then we will show the image from directory otherwise
        // we will show the image by using picasso
        if (fullpath.exists()) {
            Uri uri = Uri.parse(fullpath.getAbsolutePath());
            single_image.setImageURI(uri);
        }

        else {

            GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources())
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                    .setProgressBarImage(new CircleProgressBarDrawable())
                    .build();

            single_image.setHierarchy(hierarchy);
            uri = Uri.parse(image_url);

            single_image.setImageURI(uri);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.close_gallery:
                if (isfromchatscreen)
                    getActivity().onBackPressed();
                else
                    getFragmentManager().popBackStackImmediate();
                break;
        }
    }

    // this funtion will save the picture but we have to give tht permision to right the storage
    private void Savepicture() {
        if (Checkstoragepermision()) {

            final File direct = new File(AllConstants.folder_parcel_DCIM);
            progressDialog.show();
            prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.parse(direct.getPath() + chat_id + ".jpg"));
                    context.sendBroadcast(intent);
                    progressDialog.dismiss();

                }


                @Override
                public void onError(Error error) {
                    progressDialog.dismiss();
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.something_went_wrong));
                }

            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.click_again));
        }
    }

    private boolean Checkstoragepermision() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {

            return true;
        }
    }
}


