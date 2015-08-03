package com.foryou.truck;

import org.kymjs.aframe.ui.activity.BaseSplash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.foryou.truck.util.SharePerfenceUtil;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseSplash {
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		PushManager.getInstance().initialize(this.getApplicationContext());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void setRootBackground(ImageView img) {
		// TODO Auto-generated method stub
		img.setBackgroundResource(R.drawable.bg);
	}

	@Override
	protected void redirectTo() {
		// TODO Auto-generated method stub
		if (SharePerfenceUtil.getFirstLogin(this)) {
			skipActivity(this, WelcomeViewPage.class);
		} else {
			Intent intent;
			String classname = SharePerfenceUtil.getLoadClass(this);
			String id = SharePerfenceUtil.getFYID(this);
			Log.i("aa","classname:"+classname+",id:"+id);
			if (!classname.equals("") && !id.equals("")) {
				if(classname.equals("AgentListActivity")){
					intent = new Intent(this,AgentListActivity.class);
					intent.putExtra("order_id", id);
					startActivity(intent);
					
				}else if(classname.equals("OrderDetailActivity")){
					intent = new Intent(this,OrderDetailActivity.class);
					intent.putExtra("order_id", id);
					startActivity(intent);
				}
				SharePerfenceUtil.clearGTMESSAGE(this);
				this.finish();
			} else {
				skipActivity(this, TabActivity.class);
			}
			// skipActivity(this, WelcomeViewPage.class);
		}
	}
}
