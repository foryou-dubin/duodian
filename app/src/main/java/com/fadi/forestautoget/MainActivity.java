package com.fadi.forestautoget;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fadi.forestautoget.service.AccessibilityServiceMonitor;
import com.fadi.forestautoget.util.AccessibilitUtil;
import com.fadi.forestautoget.util.Config;
import com.fadi.forestautoget.util.ShareUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TimePicker.OnTimeChangedListener {

    private ShareUtil mShareUtil;

    private TimePicker timepick;
    private NumberPicker numberPicker;

    private Switch sw_keep;
    private Switch sw_liangtong;
    private Switch sw_alipay_forest;
    private Switch sw_wechart_motion;
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
        timepick = (TimePicker) findViewById(R.id.timepick);
        numberPicker = (NumberPicker) findViewById(R.id.num_picker);
        numberPicker.setMaxValue(99);
        numberPicker.setMinValue(0);
        numberPicker.setValue(0);
        numberPicker.setOnValueChangedListener(valueChangeListener);
        sw_keep = (Switch) findViewById(R.id.sw_keep);
        sw_liangtong = (Switch) findViewById(R.id.sw_liangtong);
        btnSettings = (Button) findViewById(R.id.btn_settings);
        sw_alipay_forest = (Switch) findViewById(R.id.sw_alipay_forest);
        sw_wechart_motion = (Switch) findViewById(R.id.sw_wechart_motion);
    }

    NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            if(mShareUtil!= null){
                mShareUtil.setShare(Config.KEY_SECONDS, i1);
                startAccessService();
            }
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
        sw_keep.setOnCheckedChangeListener(this);
        sw_liangtong.setOnCheckedChangeListener(this);
        sw_alipay_forest.setOnCheckedChangeListener(this);
        sw_wechart_motion.setOnCheckedChangeListener(this);

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

        sw_keep.setChecked(mShareUtil.getBoolean(Config.APP_KEEP, true));
        sw_alipay_forest.setChecked(mShareUtil.getBoolean(Config.APP_ALIPAY_FOREST, true));
        sw_liangtong.setChecked(mShareUtil.getBoolean(Config.APP_LIANG_TONG, true));
        sw_wechart_motion.setChecked(mShareUtil.getBoolean(Config.APP_WECHART_MOTHION, true));

        int hour = mShareUtil.getInt(Config.KEY_HOUR, -1);
        int minute = mShareUtil.getInt(Config.KEY_MINUTE, -1);

        if (hour == -1 && minute == -1) {
            // do nothing
        } else {
            timepick.setHour(hour);
            timepick.setMinute(minute);
        }
    }

    private void startService() {
        Intent mIntent = new Intent(this, AccessibilityServiceMonitor.class);
        startService(mIntent);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.sw_keep:
                mShareUtil.setShare(Config.APP_KEEP, b);
                Log.d(Config.TAG, "Keep is " + b);
                break;
            case R.id.sw_alipay_forest:
                mShareUtil.setShare(Config.APP_ALIPAY_FOREST, b);
                Log.d(Config.TAG, "AlipayForest is " + b);
                break;
            case R.id.sw_liangtong:
                mShareUtil.setShare(Config.APP_LIANG_TONG, b);
                Log.d(Config.TAG, "LiangTong is " + b);
                break;
            case R.id.sw_wechart_motion:
                mShareUtil.setShare(Config.APP_WECHART_MOTHION, b);
                Log.d(Config.TAG, "Wechat mothion is " + b);
                break;
        }

        Intent intent = new Intent(this, AccessibilityServiceMonitor.class);
        intent.setAction(AccessibilityServiceMonitor.ACTION_UPDATE_SWITCH);
        MainActivity.this.startService(intent);
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

           // MyApplication.startAlarmTask(MainActivity.this);

            startAccessService();
        }
    }
}
