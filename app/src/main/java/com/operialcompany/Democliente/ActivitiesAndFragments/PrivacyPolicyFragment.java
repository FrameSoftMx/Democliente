package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.operialcompany.Democliente.Constants.AllConstants;
import com.operialcompany.Democliente.Constants.Config;
import com.operialcompany.Democliente.Constants.Functions;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.FontHelper;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;


/**
 * Created by qboxus on 10/18/2019.
 */

public class PrivacyPolicyFragment extends RootFragment {


    WebView mWebview;
    ImageView closeIcon;
    TextView riderJobs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.rider_weekly_earning_fragment, container, false);
        FrameLayout frameLayout = v.findViewById(R.id.weekly_earning_main_container);
        FontHelper.applyFont(getContext(),frameLayout, AllConstants.verdana);

        init(v);

        return v;
    }

    public void init(View v){

        riderJobs = v.findViewById(R.id.rider_jobs);
        riderJobs.setText(R.string.privacy_policy);

         callWebView(v);

        closeIcon = v.findViewById(R.id.close_btn);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }


    public void callWebView(View v){


        mWebview = v.findViewById(R.id.web_view);
        mWebview.getSettings().setJavaScriptEnabled(true);

        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setBuiltInZoomControls(true);


        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), rerr.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                Functions.showLoader(getContext(),false,false);

            }


            @Override
            public void onPageFinished(WebView view, String url) {
              Functions.cancelLoader();

            }



    });
        mWebview.loadUrl(Config.Privacy_policy);}

}
