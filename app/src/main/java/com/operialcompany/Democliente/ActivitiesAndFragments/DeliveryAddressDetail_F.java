package com.operialcompany.Democliente.ActivitiesAndFragments;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.operialcompany.Democliente.Constants.FragmentCallback;
import com.operialcompany.Democliente.Models.AddressListModel;
import com.operialcompany.Democliente.R;


public class DeliveryAddressDetail_F extends BottomSheetDialogFragment implements View.OnClickListener {

    View view;
    Context context;
    FragmentCallback fragmentCallback;
    AddressListModel addressListModel;

    EditText riderInstructionEdit;

    TextView addressTxt;

    public DeliveryAddressDetail_F(AddressListModel addressListModel, FragmentCallback fragmentCallback) {
        this.addressListModel=addressListModel;
        this.fragmentCallback = fragmentCallback;
    }

    public DeliveryAddressDetail_F() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_delivery_address_detail, container, false);
        context=getContext();

        addressTxt =view.findViewById(R.id.address_txt);
        addressTxt.setText(addressListModel.getStreet()+" "+addressListModel.getCity()+" "+addressListModel.getState());


        riderInstructionEdit =view.findViewById(R.id.rider_instruction_edit);
        riderInstructionEdit.setText(addressListModel.getInstruction());


        view.findViewById(R.id.edit_btn).setOnClickListener(this::onClick);
        view.findViewById(R.id.confirm_btn).setOnClickListener(this);

        return view;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.edit_btn:
                fragmentCallback.onResponce(null);
                dismiss();
                break;

            case R.id.confirm_btn:
                Bundle bundle=new Bundle();
                bundle.putString("instruction", riderInstructionEdit.getText().toString());
                fragmentCallback.onResponce(bundle);
                dismiss();
                break;




        }
    }



}