package com.example.decibelmeasure;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.decibelmeasure.util.Constants;

public class Model implements Contract.Model{

    @Override
    public void saveOffset(Context context, double val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("OFFSET", (float)val);
        editor.commit();
    }

    @Override
    public double retrieveOffset(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("OFFSET", 0.0f);
    }

}
