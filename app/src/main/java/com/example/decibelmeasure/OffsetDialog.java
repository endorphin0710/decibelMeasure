package com.example.decibelmeasure;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class OffsetDialog extends DialogFragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    private View btnCancel;
    private View btnApply;
    private TextView textStatus;
    private EditText textOffset;

    public OffsetDialog() {}

    public static OffsetDialog newInstance(double offset) {
        OffsetDialog fragment = new OffsetDialog();
        Bundle arguments = new Bundle();
        arguments.putDouble("OFFSET", offset);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_offset_dialog, container, false);
        btnCancel = rootView.findViewById(R.id.btnCancel);
        btnApply = rootView.findViewById(R.id.btnApply);
        textStatus = rootView.findViewById(R.id.textStatus);
        textOffset = rootView.findViewById(R.id.offsetValue);

        if (getArguments().containsKey("OFFSET")) {
            double offsetValue = getArguments().getDouble("OFFSET");
            textOffset.setText(Double.toString(offsetValue));
        }

        btnCancel.setOnClickListener(this);
        btnApply.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnCancel:
                try{
                    mListener.onChoice(0, 0.0);
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
                break;
            case R.id.btnApply:
                try{
                    double offset_value = Double.parseDouble(textOffset.getText().toString());
                    offset_value = roundToOneDecimalPlace(offset_value);
                    mListener.onChoice(1, offset_value);
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void updateDecibel(double db){
        textStatus.setText(Double.toString(db) + " dB");
    }

    private double roundToOneDecimalPlace(double val){
        return Math.round(val * 10) / 10.0;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener){
            mListener = (OnFragmentInteractionListener) context;
        }else{
            throw new ClassCastException(context.toString() + "must implement OnFragmentInteractionListener");
        }
    }

    interface OnFragmentInteractionListener {
        void onChoice(int choice, double offsetValue);
    }

}
