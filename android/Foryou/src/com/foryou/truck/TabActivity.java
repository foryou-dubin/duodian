package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.activity.KJFragmentActivity;
import org.kymjs.aframe.ui.fragment.BaseFragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.MyCustomProgressDlg;
import com.umeng.update.UmengUpdateAgent;

public class TabActivity extends KJFragmentActivity {
	FirstTabFragment content1 = new FirstTabFragment(); // 第一个界面
	BaseFragment content2 = new SecondTabFragment(); // 第二个界面
	BaseFragment content3 = new ThirdTabFragment(); // 第三个界面
	BaseFragment content4 = new ForthTabFragment(); // 第四个界面
	private RelativeLayout mRbtn1,mRbtn2, mRbtn3, mRbtn4;
	private String TAG = "TabActivity";
	private MyCustomProgressDlg progressDialog;
	private BaseFragment mCurrentFragment = null;
	public static int mCurrentTabIndex = 1;
	private boolean btGt = false;
	// public static int tabIndex = 1;

	// bottom img and text
	ImageView mImg1, mImg2, mImg3, mImg4;
	TextView mText1, mText2, mText3, mText4;

	private Context mContext;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i(TAG, "receiver broadcast for gt cid");
			BindGeTui();
		}
	};

	public TabActivity() {
		setHiddenActionBar(true);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			btGt = false;
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (parser.entity != null) {
					if (parser.entity.status.equals("Y")) {
						String result = (String) msg.obj;
						Log.i(TAG, "result:" + result);
					} else {
						ToastUtils.toast(mContext, parser.entity.msg);
					}
				} else {
					Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
							.show();
				}
			}
			super.handleMessage(msg);
		}

	};

	private SimpleJasonParser parser;

	private void BindGeTui() {
		Log.i(TAG, "BindGeTui id:" + btGt);
		if (!btGt) {
			String uid = SharePerfenceUtil.getUid(mContext);
			String cid = SharePerfenceUtil.GetGtCid(mContext);
			Log.i(TAG, "uid:" + uid + ",cid:" + cid);
			if (uid.equals("") || cid.equals("")) {
				Log.i("aa", "gt cid or uid is null");
				return;
			}
			btGt = true;
			parser = new SimpleJasonParser();
			Map<String, String> parmas = new HashMap<String, String>();
			parmas.put("gt_id", SharePerfenceUtil.GetGtCid(mContext));
			MLHttpConnect.BindGt(mContext, parmas, parser, mHandler);
		}
	}

	@Override
	protected void initWidget() {
		super.initWidget();
		mRbtn1 = (RelativeLayout) findViewById(R.id.bottombar_content1);
		mRbtn1.setOnClickListener(this);
		mRbtn2 = (RelativeLayout) findViewById(R.id.bottombar_content2);
		mRbtn2.setOnClickListener(this);
		mRbtn3 = (RelativeLayout) findViewById(R.id.bottombar_content3);
		mRbtn3.setOnClickListener(this);
		mRbtn4 = (RelativeLayout) findViewById(R.id.bottombar_content4);
		mRbtn4.setOnClickListener(this);

		mImg1 = (ImageView) findViewById(R.id.img1);
		mImg2 = (ImageView) findViewById(R.id.img2);
		mImg3 = (ImageView) findViewById(R.id.img3);
		mImg4 = (ImageView) findViewById(R.id.img4);

		mText1 = (TextView) findViewById(R.id.text1);
		mText2 = (TextView) findViewById(R.id.text2);
		mText3 = (TextView) findViewById(R.id.text3);
		mText4 = (TextView) findViewById(R.id.text4);

		mContext = this;
		// int index = getIntent().getIntExtra("tabIndex", 1);
		// changFragment(index);
		Log.i(TAG, "TabActivity init widget ......");
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Constant.GT_CID_CHANGED_BROADCASE);
		this.registerReceiver(mReceiver, mFilter);

		UmengUpdateAgent.silentUpdate(this);
		UmengUpdateAgent.update(this);
	}
	
	private void runOnUithread(){
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				content1.initValue(firstValue);
			}
			
		});
	}
	
	
	private FirstTabInitValue firstValue;
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Log.i(TAG, "onNewIntent .....");
		firstValue = (FirstTabInitValue) intent
				.getSerializableExtra("firstTabValue");
		if (firstValue != null) {
			Thread t = new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					int count = 10;
					while (!content1.isAdded() && count != 0) {
						Log.i(TAG, "sleep 200ms");
						count--;
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					runOnUithread();
				}

			};
			t.start();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume .....");
		if (mCurrentTabIndex < 1 || mCurrentTabIndex > 4) {
			changFragment(1);
		} else {
			changFragment(mCurrentTabIndex);
		}
		BindGeTui();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// 供firstFragment 调用
	public void superOnBackPress() {
		// super.onBackPressed();
		ViewInject.create().getExitDialog(this);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.i("aa", "onBackPressed .....");
		if (mCurrentFragment == content1) {
			FirstTabFragment firstFragment = (FirstTabFragment) mCurrentFragment;
			firstFragment.onBackPressed();
		} else {
			superOnBackPress();
		}
	}

	@Override
	public void widgetClick(View v) {
		super.widgetClick(v);
		Log.i(TAG, "TabActivity click...");
		if (!SharePerfenceUtil.IsLogin(this)) {
			Intent intent = new Intent(this, EntryLoginActivity.class);
			startActivity(intent);
			return;
		}

		switch (v.getId()) {
		case R.id.bottombar_content1:
			// startActivity(new Intent(this, SlidExample.class));
			// ViewInject.toast("侧滑试试");
			changeFragment(content1);
			break;
		case R.id.bottombar_content2:
			// actionBar.setTitle("网络请求");
			changeFragment(content2);
			break;
		case R.id.bottombar_content3:
			// actionBar.setTitle("listview网络图片加载");
			changeFragment(content3);
			break;
		case R.id.bottombar_content4:
			// actionBar.setTitle("图片模糊效果");
			Log.i(TAG, "TabActivity click 4...");
			changeFragment(content4);
			break;
		}
	}

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.foryou_tab);
	}

	private void UpdateBottomView(BaseFragment targetFragment) {
		if (targetFragment == content1) {
			mImg1.setBackgroundResource(R.drawable.sendimg);
			mText1.setTextColor(this.getResources().getColor(
					R.color.tishi_bg_color));

			mImg2.setBackgroundResource(R.drawable.u_query_img);
			mText2.setTextColor(Color.GRAY);

			mImg3.setBackgroundResource(R.drawable.u_order_list);
			mText3.setTextColor(Color.GRAY);

			mImg4.setBackgroundResource(R.drawable.u_userinfo);
			mText4.setTextColor(Color.GRAY);

			mCurrentTabIndex = 1;
		} else if (targetFragment == content2) {
			mImg1.setBackgroundResource(R.drawable.u_sendimg);
			mText1.setTextColor(Color.GRAY);

			mImg2.setBackgroundResource(R.drawable.query_img);
			mText2.setTextColor(this.getResources().getColor(
					R.color.tishi_bg_color));

			mImg3.setBackgroundResource(R.drawable.u_order_list);
			mText3.setTextColor(Color.GRAY);

			mImg4.setBackgroundResource(R.drawable.u_userinfo);
			mText4.setTextColor(Color.GRAY);

			mCurrentTabIndex = 2;
		} else if (targetFragment == content3) {
			mImg1.setBackgroundResource(R.drawable.u_sendimg);
			mText1.setTextColor(Color.GRAY);

			mImg2.setBackgroundResource(R.drawable.u_query_img);
			mText2.setTextColor(Color.GRAY);

			mImg3.setBackgroundResource(R.drawable.order_list);
			mText3.setTextColor(this.getResources().getColor(
					R.color.tishi_bg_color));

			mImg4.setBackgroundResource(R.drawable.u_userinfo);
			mText4.setTextColor(Color.GRAY);

			mCurrentTabIndex = 3;
		} else if (targetFragment == content4) {
			mImg1.setBackgroundResource(R.drawable.u_sendimg);
			mText1.setTextColor(Color.GRAY);

			mImg2.setBackgroundResource(R.drawable.u_query_img);
			mText2.setTextColor(Color.GRAY);

			mImg3.setBackgroundResource(R.drawable.u_order_list);
			mText3.setTextColor(Color.GRAY);

			mImg4.setBackgroundResource(R.drawable.userinfo);
			mText4.setTextColor(this.getResources().getColor(
					R.color.tishi_bg_color));

			mCurrentTabIndex = 4;
		}
	}

	@Override
	public void changeFragment(BaseFragment targetFragment) {
		// TODO Auto-generated method stub
		// changeFragment(R.id.content, targetFragment);
		UpdateBottomView(targetFragment);
		if (targetFragment == mCurrentFragment) {
			return;
		}
		changeFragment(R.id.content, targetFragment);
		mCurrentFragment = targetFragment;
	}

	public void changFragment(int index) {
		switch (index) {
		case 1:
			if (mCurrentFragment != content1) {
				changeFragment(content1);
			}
			break;
		case 2:
			if (mCurrentFragment != content2) {
				changeFragment(content2);
			}
			break;
		case 3:
			if (mCurrentFragment != content3) {
				changeFragment(content3);
			}
			break;
		case 4:
			if (mCurrentFragment != content4) {
				changeFragment(content4);
			}
			break;
		default:
			changeFragment(content1);
			break;
		}
	}

	/**
	 * 普通的提示框，只有确定按钮，点击关闭对话框，停留当前页
	 * 
	 * @param title
	 *            对话框标题
	 * @param des
	 *            对话框内容
	 */
	public void alertDialog(String title, String des, final boolean needfinish) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(title);
		adb.setMessage(des);
		adb.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (needfinish) {
					finish();
				}
			}
		});
		adb.show();
	}

	/**
	 * 取消加载框
	 */
	public void cancelProgressDialog() {

		if (!this.isFinishing()) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.cancel();
				progressDialog = null;
			}
		}
	}

	public void setTextView(TextView tx, String str) {
		if (str != null && tx != null) {
			tx.setText(str);
		}
	}

	public void showProgressDialog() {
		showProgressDialog("正在加载数据，请稍候...");
	}

	public void showProgressDialog(String message) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		progressDialog = MyCustomProgressDlg.createDialog(this);
		if (progressDialog != null) {
			progressDialog.setMessage(message);
			progressDialog.show();
		}
	}
}
