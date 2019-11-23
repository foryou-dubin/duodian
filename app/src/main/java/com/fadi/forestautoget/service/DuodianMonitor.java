package com.fadi.forestautoget.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.fadi.forestautoget.util.Config;
import com.fadi.forestautoget.util.UtilsLog;

import java.util.List;

public class DuodianMonitor {


    public static boolean shouldReturn(AccessibilityNodeInfo child){
        if(child != null && child.getText()!=null){
            String text = child.getText().toString();
            if(text.contains("每天上午9") || text.contains("去首页看看")){
                return true;
            }
        }
        return false;
    }

    public static void findEveryViewNode(AccessibilityNodeInfo node,String pagename) {
            UtilsLog.d(Config.TAG, "entry findEveryViewNode:"+ node.getChildCount());
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child =  node.getChild(i);
                // 有时 child 为空
                if (child == null) {
                    continue;
                }
                String className = child.getClassName().toString();
                UtilsLog.d(Config.TAG, "findEveryViewNode className:"+ className+",getText:"+child.getText()+",pagename:"+pagename);

                if(pagename.equals(""+child.getText())){
                    UtilsLog.d(Config.TAG, "performAction click ......");
                    child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }else{
                    if(shouldReturn(child)){
                        UtilsLog.d(Config.TAG, "performGlobalBack Action");
                        AccessibilityServiceMonitor.getInstance().performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        return;
                    }
                    findEveryViewNode(child,pagename);
                }
            }
    }

    public static void policy(AccessibilityNodeInfo nodeInfo, String packageName, String className,String pagename) {

            if (nodeInfo != null) {
                UtilsLog.d(Config.TAG,"policy childCount:"+nodeInfo.getChildCount());
                UtilsLog.d(Config.TAG,"policy1:"+nodeInfo.getClassName().toString()+","+nodeInfo.getText());
                for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                    AccessibilityNodeInfo child =  nodeInfo.getChild(i);
                    UtilsLog.d(Config.TAG,"policy child count:"+child.getChildCount());
                    UtilsLog.d(Config.TAG,"policy child:"+child.getClassName().toString()+","+child.getText());
                    //if ("com.tencent.tbs.core.webkit.WebView".equals(child.getClassName())) {
                    findEveryViewNode(child,pagename);
                //        break;
                //    }
                }
            } else {
                UtilsLog.d(Config.TAG, "alipayPolicy = null");
            }

    }


    public static void enterPage(AccessibilityNodeInfo nodeInfo,String name){
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(name);

            if (list == null) {
                UtilsLog.d(Config.TAG, name+ " finding no");
                return;
            } else {
                UtilsLog.d(Config.TAG, name + "finding yes");
            }

            UtilsLog.d(Config.TAG, "listSize:"+list.size());
            for (AccessibilityNodeInfo item : list) {
                /**
                 *  蚂蚁森林本身不可点击，但是他的父控件可以点击
                 */
                UtilsLog.d(Config.TAG, "item:"+item);
                if(item.isClickable()){
                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }else{
                    AccessibilityNodeInfo parent = item.getParent();
                    UtilsLog.d(Config.TAG, "parent:"+parent);
                    if (null != parent && parent.isClickable()) {
                        UtilsLog.d(Config.TAG, "parent clickable");
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        UtilsLog.d(Config.TAG, "item = " + item.toString() + ", parent click = " + parent.toString());
                        break;
                    }
                }
            }
        }
    }



    public static void enterMaotaiPage(AccessibilityNodeInfo nodeInfo,String packageName,String className) {
        UtilsLog.d(Config.TAG, "enterDuoDianUI ");

        if(packageName.equals("com.wm.dmall") && className.equals("com.tencent.tbs.core.webkit.WebView")){
            policy(nodeInfo,packageName,className,"预约购买");
        }else{
            enterPage(nodeInfo,"茅台预售");
        }
    }

    public static void startDuodianPage(Context mContext) {
        Intent intent = new Intent();
        intent.setPackage(" com.wm.dmall");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(" com.wm.dmall", " com.wm.dmall.MainActivity");

        mContext.startActivity(intent);
    }

}
