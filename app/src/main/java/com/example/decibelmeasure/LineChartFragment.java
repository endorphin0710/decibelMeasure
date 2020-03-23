package com.example.decibelmeasure;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.decibelmeasure.util.Constants;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LineChartFragment extends Fragment {

    private LineChart lineChart;
    private LineDataSet dataSetCur;
    private LineDataSet dataSetAvr;
    private LineData lineData;

    private List decibelCur;
    private List decibelAvr;
    private double offset;

    public LineChartFragment(){}

    public LineChartFragment(double offset) {
        this.offset = offset;
    }

    public static LineChartFragment newInstance(double offset) {
        LineChartFragment fragment = new LineChartFragment(offset);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

        lineChart = rootView.findViewById(R.id.chart);
        decibelCur = new ArrayList();
        decibelAvr = new ArrayList();

        dataSetCur = new LineDataSet(decibelCur, Constants.PRESENT);
        dataSetAvr = new LineDataSet(decibelAvr, Constants.AVERAGE);
        lineData = new LineData();
        lineData.addDataSet(dataSetCur);
        lineData.addDataSet(dataSetAvr);

        lineChartConfigure();
        lineChart.setData(lineData);
        lineChart.invalidate();

        return rootView;
    }

    private void lineChartConfigure(){
        dataSetCur.setLineWidth(2);
        dataSetCur.setDrawValues(false);
        dataSetCur.setDrawCircles(false);
        dataSetCur.setColor(Color.parseColor("#FF4848"));

        dataSetAvr.setLineWidth(2);
        dataSetAvr.setDrawValues(false);
        dataSetAvr.setDrawCircles(false);
        dataSetAvr.setColor(Color.parseColor("#5586EB"));

        lineChart.getAxisLeft().setAxisMaximum(Constants.MAX_DECIBEL + (float)offset);
        lineChart.getAxisLeft().setAxisMinimum(Constants.MIN_DECIBEL + (float)offset);
        lineChart.getAxisRight().setDrawLabels(false);

        lineChart.getXAxis().setEnabled(false);
        lineChart.getXAxis().setAxisMaximum(Constants.MAX_NUM_DATA);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    public void updateLineChart(double db, double avr, int cnt){
        if(decibelCur.size() < Constants.MAX_NUM_DATA){
            decibelCur.add(new Entry(cnt++, (float)db));
            decibelAvr.add(new Entry(cnt, (float)avr));
        }else{
            decibelCur.remove(0);
            decibelCur.add(new Entry(cnt++, (float)db));
            decibelAvr.remove(0);
            decibelAvr.add(new Entry(cnt, (float)avr));
        }
        if(cnt > Constants.MAX_NUM_DATA) {
            lineChart.getXAxis().setAxisMaximum(cnt);
        }
        dataSetCur.notifyDataSetChanged();
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public void clearChart(){
        decibelCur.clear();
        decibelAvr.clear();
        lineChart.invalidate();
        lineChart.clear();
    }

}
