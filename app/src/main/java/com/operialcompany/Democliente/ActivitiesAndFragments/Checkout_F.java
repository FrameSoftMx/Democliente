package com.operialcompany.Democliente.ActivitiesAndFragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Checkout_F extends RootFragment {

    View view;
    Context context;

    ProgressBar progressBar;
    WebView webView;
    String url="www.google.com";
    public Checkout_F() {

    }

    FragmentCallback fragment_callback;
    public Checkout_F(FragmentCallback fragment_callback) {
        this.fragment_callback=fragment_callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_checkout, container, false);
        context=getContext();

        Bundle bundle=getArguments();
        if(bundle!=null){
            url=bundle.getString("url");
        }


        view.findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();
            }
        });


        webView=view.findViewById(R.id.webview);
        progressBar =view.findViewById(R.id.progress_bar);
        webView.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView view, int progress) {
                if(progress>=80){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(url);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if(url.contains("paymentSuccess")){
                    startTimer();
                }
                return false;
            }
        });


        return view;
    }


    Handler max_handler;
    Runnable max_runable;
    public void startTimer(){
        max_handler=new Handler();
        max_runable=new Runnable() {
            @Override
            public void run() {

                getActivity().onBackPressed();

            }
        };
        max_handler.postDelayed(max_runable,10000);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(max_handler!=null && max_runable!=null){

            if(fragment_callback!=null)
                fragment_callback.onResponce(new Bundle());

            max_handler.removeCallbacks(max_runable);
        }

    }


}
