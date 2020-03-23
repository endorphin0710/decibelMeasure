package com.example.decibelmeasure;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;

import com.example.decibelmeasure.util.Constants;

import java.io.IOException;

public class Presenter implements Contract.Presenter{

    private Contract.View view;
    private Contract.Model model;

    private Runnable runnable;
    private Handler handler;
    private MediaRecorder mediaRecorder;

    private boolean started;
    private double offset;
    private double sum;
    private double min;
    private double max;
    private int cnt_sum;
    private int total;

    public Presenter(Contract.View view){
        this.view = view;
        this.model = new Model();
        this.handler = new Handler();
        this.started = false;
        this.total = 0;
    }

    @Override
    public void measureStart() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile("/dev/null");
        try{
            mediaRecorder.prepare();
        }catch(IOException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        mediaRecorder.start();
        started = true;
        view.setSwitchText();

        min = offset + Constants.MAX_DECIBEL;
        max = offset + Constants.MIN_DECIBEL;
        sum = 0;
        cnt_sum = 0;
        runnable = new Runnable() {
            @Override
            public void run() {
                double db = getDecibel();
                db = roundToOneDecimalPlace(db + offset);
                if(offset + Constants.MIN_DECIBEL < db && db < Constants.MAX_DECIBEL){
                    total += 1;
                    if(db < min){
                        min = db;
                        if(min < 0) min = 0;
                    }
                    if(db > max){
                        max = db;
                        if(max > 200) max = 200;
                    }
                    double avr = getAverage(db);

                    view.updateDialogDecibel(db);
                    view.setDecibelValues(db, min, max, avr);
                    view.updateLineChart(db, avr, total);
                }

                handler.postDelayed(runnable, 150);
            }
        };
        handler.post(runnable);

    }

    @Override
    public void measureStop() {
        handler.removeCallbacks(runnable);
        mediaRecorder.stop();
        started = false;
        view.setSwitchText();
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public double getOffset() {
        return offset;
    }

    @Override
    public void reset() {
        started = false;
        releaseMediaRecorder();
        view.clearChart();
        view.initView(offset);
        view.initLineChart();
        total = 0;
    }

    private void releaseMediaRecorder(){
        if(started) measureStop();
        if(mediaRecorder != null) mediaRecorder.release();
    }

    public double getDecibel() {
        double amplitude = mediaRecorder.getMaxAmplitude();
        double db = 20 * Math.log10(amplitude);
        if(db < 0) db = 0;
        return db;
    }

    private double getAverage(double db){
        sum += db;
        cnt_sum += 1;
        double avr = sum / (double)cnt_sum;
        return roundToOneDecimalPlace(avr);
    }

    public double roundToOneDecimalPlace(double val){
        return Math.round(val * 10) / 10.0;
    }

    @Override
    public void saveOffset(Context context, double val) {
        offset = val;
        model.saveOffset(context, val);
    }

    @Override
    public void retrieveOffset(Context context) {
        offset = model.retrieveOffset(context);
    }

}
