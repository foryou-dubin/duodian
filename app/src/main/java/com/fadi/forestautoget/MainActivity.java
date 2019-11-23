package com.fadi.forestautoget;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fadi.forestautoget.service.AccessibilityServiceMonitor;
import com.fadi.forestautoget.util.AccessibilitUtil;
import com.fadi.forestautoget.util.Config;
import com.fadi.forestautoget.util.ShareUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TimePicker.OnTimeChangedListener {

    private ShareUtil mShareUtil;

    private TimePicker timepick;
    private NumberPicker secPicker,millPicker;

    private Button btnSettings;

    private final int REQUEST_WRITE = 100;


    public static boolean SDCARD_PERSIMITION = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initVaule();
        initListener();
        startService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_WRITE&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
            mShareUtil.setShare(Config.KEY_SDCARD, true);
            SDCARD_PERSIMITION = true;
        }
    }


    private void requestSdcardPermisstion() {
        //判断是否6.0以上的手机   不是就不用
        if (Build.VERSION.SDK_INT >= 23) {
            //判断是否有这个权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //2、申请权限: 参数二：权限的数组；参数三：请求码
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE);
            } else {
                mShareUtil.setShare(Config.KEY_SDCARD, true);
                SDCARD_PERSIMITION = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void initView() {
        timepick = findViewById(R.id.timepick);
        secPicker = findViewById(R.id.sec_picker);
        secPicker.setMaxValue(59);
        secPicker.setMinValue(0);
        secPicker.setValue(0);
        secPicker.setOnValueChangedListener(valueChangeListener);

        millPicker = findViewById(R.id.millsec_picker);
        millPicker.setMaxValue(99);
        millPicker.setMinValue(0);
        millPicker.setValue(0);
        millPicker.setOnValueChangedListener(valueChangeListener);

        btnSettings = (Button) findViewById(R.id.btn_settings);
    }

    NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            if(mShareUtil == null){
                return;
            }
            if(numberPicker == secPicker){
                mShareUtil.setShare(Config.KEY_SECONDS, i1);
            }else if(numberPicker == millPicker){
                mShareUtil.setShare(Config.KEY_MILLSECNDS, i1);
            }
            startAccessService();
        }
    };

    private void initVaule() {
        mShareUtil = new ShareUtil(this);

        timepick.setIs24HourView(true);
        timepick.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        requestSdcardPermisstion();
    }

    private void initListener() {
        btnSettings.setOnClickListener(this);
        timepick.setOnTimeChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_settings:
                AccessibilitUtil.showSettingsUI(this);
                break;
        }
    }

    private void updateUI() {
        if (AccessibilitUtil.isAccessibilitySettingsOn(this, AccessibilityServiceMonitor.class.getCanonicalName())) {
            btnSettings.setEnabled(false);
        } else {
            btnSettings.setEnabled(true);
        }

        int hour = mShareUtil.getInt(Config.KEY_HOUR, 9);
        int minute = mShareUtil.getInt(Config.KEY_MINUTE, 0);
        int sec = mShareUtil.getInt(Config.KEY_SECONDS, 0);
        int millSec = mShareUtil.getInt(Config.KEY_MILLSECNDS, 0);

        timepick.setHour(hour);
        timepick.setMinute(minute);

        secPicker.setValue(sec);
        millPicker.setValue(millSec);
    }

    private void startService() {
        Intent mIntent = new Intent(this, AccessibilityServiceMonitor.class);
        startService(mIntent);
    }


    private void startAccessService(){
        Intent intent = new Intent(this, AccessibilityServiceMonitor.class);
        intent.setAction(AccessibilityServiceMonitor.ACTION_ALAM_TIMER);
        startService(intent);
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        if (mShareUtil != null) {
            mShareUtil.setShare(Config.KEY_HOUR, hourOfDay);
            mShareUtil.setShare(Config.KEY_MINUTE, minute);
            startAccessService();
        }
    }
}
