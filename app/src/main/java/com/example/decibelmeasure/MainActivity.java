package com.example.decibelmeasure;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.decibelmeasure.util.Constants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements Contract.View, OffsetDialog.OnFragmentInteractionListener, View.OnClickListener{

    private Presenter presenter;

    private OffsetDialog offsetDialog;
    private LineChartFragment lineChartFragment;

    private Button btnSwitch;
    private Button btnOffset;
    private Button btnReset;
    private TextView status;
    private TextView minVal;
    private TextView maxVal;
    private TextView avrVal;

    private boolean offsetOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        presenter = new Presenter(this);
        presenter.retrieveOffset(getApplicationContext());

        initView(presenter.getOffset());
        initLineChart();
        attachButtonListener();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(presenter.isStarted()){
            presenter.measureStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(presenter.isStarted()){
//            presenter.measureStart();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.AUDIO_REQUEST_CODE : {
                /** on permission granted **/
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.measureStart();
                }
                /** on permission denied **/
                else {
                    Toast.makeText(getApplicationContext(), Constants.PERMISSION_WARNING, Toast.LENGTH_LONG).show();
                }
                return;
            }
            default :
                return;
        }
    }

    @Override
    public void initView(double offset){
        btnSwitch = findViewById(R.id.btnSwitch);
        btnOffset = findViewById(R.id.btnOffset);
        btnReset = findViewById(R.id.btnReset);

        status = findViewById(R.id.status);
        status.setText(Constants.MIN_DECIBEL + Constants.DECIBEL_UNIT);
        minVal = findViewById(R.id.minValue);
        minVal.setText(Constants.MIN_DECIBEL + Constants.DECIBEL_UNIT);
        maxVal = findViewById(R.id.maxValue);
        maxVal.setText(Constants.MIN_DECIBEL + Constants.DECIBEL_UNIT);
        avrVal = findViewById(R.id.avrValue);
        avrVal.setText(Constants.MIN_DECIBEL + Constants.DECIBEL_UNIT);
        findViewById(R.id.offsetDialog).bringToFront();
    }

    private void attachButtonListener(){
        btnSwitch.setOnClickListener(this);
        btnOffset.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSwitch:
                if(!presenter.isStarted()){
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.AUDIO_REQUEST_CODE);
                    } else {
                        presenter.measureStart();
                    }
                }else{
                    presenter.measureStop();
                }
                break;
            case R.id.btnOffset:
                toggleOffsetDialog();
                break;
            case R.id.btnReset:
                presenter.reset();
                break;
            default:
                break;
        }
    }

    @Override
    public void setDecibelValues(double cur, double min, double max, double avr) {
        status.setText(cur + Constants.DECIBEL_UNIT);
        minVal.setText(min + Constants.DECIBEL_UNIT);
        maxVal.setText(max + Constants.DECIBEL_UNIT);
        avrVal.setText(avr + Constants.DECIBEL_UNIT);
    }

    @Override
    public void setSwitchText() {
        if(presenter.isStarted()){
            btnSwitch.setText(Constants.STOP);
            Toast.makeText(getApplicationContext(), Constants.START_TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
        }else{
            btnSwitch.setText(Constants.START);
            Toast.makeText(getApplicationContext(), Constants.STOP_TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initLineChart(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        lineChartFragment = LineChartFragment.newInstance(presenter.getOffset());
        ft.replace(R.id.linechartFrame, lineChartFragment).addToBackStack(null).commit();
    }

    @Override
    public void updateLineChart(double db, double avr, int cnt){
        lineChartFragment.updateLineChart(db, avr, cnt);
    }

    private void toggleOffsetDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(offsetOpened){
            offsetDialog = (OffsetDialog)fragmentManager.findFragmentById(R.id.offsetDialog);
            ft.remove(offsetDialog).commit();
        }else{
            offsetDialog = OffsetDialog.newInstance(presenter.getOffset());
            ft.replace(R.id.offsetDialog, offsetDialog).addToBackStack(null).commit();
        }
        offsetOpened = !offsetOpened;
    }

    @Override
    public void updateDialogDecibel(double db) {
        if(offsetOpened){
            offsetDialog.updateDecibel(db);
        }
    }

    @Override
    public void onChoice(int choice, double offsetValue) {
        if(choice == 0){
            toggleOffsetDialog();
        }else{
            applyOffset(offsetValue);
            toggleOffsetDialog();
        }
    }

    private void applyOffset(double offsetValue){
        double offset = presenter.roundToOneDecimalPlace(offsetValue);
        presenter.saveOffset(getApplicationContext(), offset);
    }

    @Override
    public void clearChart(){
        lineChartFragment.clearChart();
    }

}