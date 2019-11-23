package com.fadi.forestautoget.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.fadi.forestautoget.util.AccessibilitUtil;
import com.fadi.forestautoget.util.Config;
import com.fadi.forestautoget.util.DateTimeUtil;
import com.fadi.forestautoget.util.ShareUtil;
import com.fadi.forestautoget.util.UtilsLog;

import java.util.Calendar;


public class AccessibilityServiceMonitor extends AccessibilityService {

    private static final String TAG = AccessibilityServiceMonitor.class.getSimpleName();

    public static final String ACTION_UPDATE_SWITCH = "action_update_switch";
    public static final String ACTION_ALAM_TIMER= "action_alarm_timer";


    public static AccessibilityServiceMonitor mAccessInstance = null;

    private boolean isNewday;

    /**
     * Keep App 辅助功能
     */
    private boolean isKeepEnable = true;

    /**
     * 支付宝 App 辅助功能
     */
    private boolean isAlipayForest = true;

    /**
     * 联通手机营业厅 辅助功能
     */
    private boolean isLiangTongEnable = true;

    /**
     * 微信运动的自动点赞器
     */
    private boolean isWeChatMotionEnable = true;


    public static int CURRENT_PAGE = 1;

    private H mHandle = new H();
    private static final int MSG_DELAY_ENTER_FOREST = 0;
    private static final int MSG_DELAY_ENTER_LIANGTONG = 1;
    private static final int DEFAULT_DELAY_TIME = 1 * 1000;


    private static final int MSG_START_ACTION = 2;

    private boolean startFlag = false;

    private MyBroadCast myBroadCast;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        myBroadCast = new MyBroadCast();
        myBroadCast.init(this);

        mAccessInstance = this;
    }


    public static AccessibilityServiceMonitor getInstance(){
        return mAccessInstance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return super.onStartCommand(intent, flags, startId);
        }

        String action = intent.getAction();
        Log.d(TAG, "onStartCommand Aciton: " + action);

        if (ACTION_UPDATE_SWITCH.equals(action)) {
            updateSwitchStatus();
        } else if (ACTION_ALAM_TIMER.equals(action)) {
            startTrigleAction();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    public void startTrigleAction(){
        startFlag = false;
        ShareUtil mShareUtil = new ShareUtil(this);
        int hour = mShareUtil.getInt(Config.KEY_HOUR, 9);
        int minute = mShareUtil.getInt(Config.KEY_MINUTE,0);
        int seconds = mShareUtil.getInt(Config.KEY_SECONDS,0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,seconds*10);
        UtilsLog.d(Config.TAG,"day:"+calendar.get(Calendar.DAY_OF_MONTH));

        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        UtilsLog.d(Config.TAG,"delay1:"+ AccessibilitUtil.timeStamp2Date(calendar.getTimeInMillis(),null));
        UtilsLog.d(Config.TAG,"delay2:"+delay);
        mHandle.removeMessages(MSG_START_ACTION);
        if(delay > 0){
            mHandle.sendEmptyMessageDelayed(MSG_START_ACTION,delay);
        }
    }



    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        //serviceInfo.packageNames = new String[]{"com.gotokeep.keep", "com.eg.android.AlipayGphone", "com.sinovatech.unicom.ui", "com.tencent.mm"};// 监控的app
        serviceInfo.packageNames = new String[]{"com.wm.dmall"};// 监控的app
        serviceInfo.notificationTimeout = 100;
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        setServiceInfo(serviceInfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String packageName = event.getPackageName().toString();
        String className = event.getClassName().toString();
        UtilsLog.d(Config.TAG,"packageName = " + packageName + ", className = " + className +",eventType:"+eventType);
        if(startFlag){
//            if(packageName.equals("com.wm.dmall") && className.equals("com.tencent.tbs.core.webkit.WebView")){
//                mHandle.sendEmptyMessageDelayed(MSG_DELAY_ENTER_FOREST,1000);
//            }
            UtilsLog.d(Config.TAG,"SystemTime:" + AccessibilitUtil.timeStamp2Date(System.currentTimeMillis(),null));
            DuodianMonitor.enterMaotaiPage(getRootInActiveWindow(),packageName,className);
        }
//        switch (eventType) {
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//          //      DuodianMonitor.enterMaotaiPage(getRootInActiveWindow(),packageName,className);
//                break;
//
//        }
    }


    public void onBackPage(){
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {

    }

    private class H extends Handler {

        public H() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DELAY_ENTER_FOREST:
                    UtilsLog.d(Config.TAG,"message setFlag true");
                    break;
                case MSG_DELAY_ENTER_LIANGTONG:
                    startLiangTongUI();
                    break;
                case MSG_START_ACTION:
                    UtilsLog.d(Config.TAG,"handler message:" + AccessibilitUtil.timeStamp2Date(System.currentTimeMillis(),null));
                    DuodianMonitor.enterMaotaiPage(getRootInActiveWindow(),"com.wm.dmall","");
                    startFlag = true;
                    break;
            }
        }
    }



    class MyBroadCast extends BroadcastReceiver {

        public void init(Context mContext) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(this, intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }

            String action = intent.getAction();
            UtilsLog.d(Config.TAG,"action = " + action);

            if (Intent.ACTION_USER_PRESENT.equals(action)) {
                isNewday = isNewDay();
                if (isNewday) {
                    startUI();
                }
            }
        }
    }



    /**
     * 更新开关状态
     */
    private void updateSwitchStatus() {
        ShareUtil mShareUtil = new ShareUtil(this);
        isKeepEnable = mShareUtil.getBoolean(Config.APP_KEEP, true);
        isAlipayForest = mShareUtil.getBoolean(Config.APP_ALIPAY_FOREST, true);
        isLiangTongEnable = mShareUtil.getBoolean(Config.APP_LIANG_TONG, true);
        isWeChatMotionEnable = mShareUtil.getBoolean(Config.APP_WECHART_MOTHION, true);
    }

    /**
     * 判断是否新的一天
     */
    private boolean isNewDay() {
        boolean result = false;

        ShareUtil mShareUtil = new ShareUtil(this);
        int saveDay = mShareUtil.getInt(Config.KEY_NEW_DAY, -1);
        int curDay = DateTimeUtil.getDayOfYear();

        if (saveDay != curDay) {
            result = true;
            mShareUtil.setShare(Config.KEY_NEW_DAY, curDay);
        }

        UtilsLog.d(Config.TAG, "isNewDay = " + result);
        return result;
    }


    /**
     * 启动UI界面
     */
    private void startUI() {
        startDuodianUI();
    }

    private void startDuodianUI(){
        DuodianMonitor.startDuodianPage(this);
    }

    private void startAlipayUI() {
        AlipayForestMonitor.startAlipay(this);
        mHandle.sendEmptyMessageDelayed(MSG_DELAY_ENTER_LIANGTONG, DEFAULT_DELAY_TIME * 10);
    }

    private void startLiangTongUI() {
        LiangTongMonitor.startLiangTongUI(this);
    }
}
