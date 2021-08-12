package com.operialcompany.Democliente.ActivitiesAndFragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.R;
import com.operialcompany.Democliente.Utils.RelateToFragment_OnBack.RootFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectDateTime_F extends RootFragment implements View.OnClickListener {

    View view;
    Context context;

    TextView dateTxt, timeTxt;

    public SelectDateTime_F() {

    }

    FragmentCallback fragmentCallback;
    public SelectDateTime_F(FragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_select_date_time, container, false);
        context=getContext();

        view.findViewById(R.id.Goback).setOnClickListener(this::onClick);

        dateTxt =view.findViewById(R.id.date_txt);
        timeTxt =view.findViewById(R.id.time_txt);


        view.findViewById(R.id.btn_edit_date).setOnClickListener(this::onClick);
        view.findViewById(R.id.btn_edit_time).setOnClickListener(this::onClick);
        view.findViewById(R.id.continue_btn).setOnClickListener(this::onClick);




        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+45);

        SimpleDateFormat timeformat= new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        timeTxt.setText(timeformat.format(calendar.getTime()));

        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateTxt.setText(format.format(calendar.getTime()));

        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Goback:
                getActivity().onBackPressed();
                break;

            case R.id.btn_edit_date:
                opendatePicker();
                break;

            case R.id.btn_edit_time:
                opentimepicker();
                break;

            case R.id.continue_btn:
                if(fragmentCallback !=null) {
                    Bundle bundle=new Bundle();
                    bundle.putString("datetime", dateTxt.getText()+" "+ timeTxt.getText());
                    fragmentCallback.onResponce(bundle);
                    getActivity().onBackPressed();
                }
                break;
        }
    }


    public  void opendatePicker(){
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog mdiDialog =new DatePickerDialog(context,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(year, monthOfYear, dayOfMonth);
                Date chosenDate = cal.getTime();
                SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dateTxt.setText(format.format(chosenDate));

            }
        }, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
        mdiDialog.show();
    }



    public void opentimepicker(){
        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar calendar=Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat timeformat= new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                timeTxt.setText(timeformat.format(calendar.getTime()));

            }
        }, 12, 00, false).show();
    }
}
