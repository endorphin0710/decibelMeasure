package com.example.decibelmeasure;

import android.content.Context;

public interface Contract {

    interface Model {
        void saveOffset(Context context, double val);
        double retrieveOffset(Context context);
    }

    interface View {
        void initView(double offset);
        void setSwitchText();
        void setDecibelValues(double cur, double min, double max, double avr);
        void initLineChart();
        void updateLineChart(double db, double avr, int cnt);
        void clearChart();
        void updateDialogDecibel(double db);
    }

    interface Presenter {
        void measureStart();
        void measureStop();
        void reset();
        boolean isStarted();
        double getOffset();
        void saveOffset(Context context, double val);
        void retrieveOffset(Context context);
    }

}
