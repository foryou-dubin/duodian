package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.ui.fragment.BaseFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.OrderPayJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.parser.UserDetailJsonParser;
import com.foryou.truck.tools.ImageTools;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class ForthTabFragment extends BaseFragment {
	private TextView mTitle;
	private TextView mCheckUpdate, mCurrentVersion;
	private TabActivity mContext;
	private Button mExitApp;
	private TextView mPhonenumber, mName;
	private RelativeLayout mSuggestLayout, mInfolayout;

	private View mRootView;

	private RelativeLayout mShareToWx,mContractUsLayout,mAboutUsLayout;
	private IWXAPI api;

	private ImageView mCover;
	private RelativeLayout mCheckUpdateLayout;
	private String TAG = "ForthTabFragment";
	private TextView mGender;
	private LinearLayout mGenderLayout;
	private TextView mAccountType;
	private LinearLayout mAccountTypeLayout;
	private TextView mVip;

	void GotoDialPage() {
		TextView phonenumber = (TextView)mContractUsLayout.findViewById(R.id.phone_text);
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ phonenumber.getText().toString()));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	private UmengUpdateListener mUmUpdateListener = new UmengUpdateListener() {
		@Override
		public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
			// TODO Auto-generated method stub
			mContext.cancelProgressDialog();
			switch (updateStatus) {
			case UpdateStatus.Yes: // has update
				UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
				break;
			case UpdateStatus.No: // has no update
				Toast.makeText(mContext, "您的版本为最新版本,无需更新", Toast.LENGTH_SHORT)
						.show();
				break;
			case UpdateStatus.NoneWifi: // none wifi
				Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新",
						Toast.LENGTH_SHORT).show();
				break;
			case UpdateStatus.Timeout: // time out
				Toast.makeText(mContext, "连接超时", Toast.LENGTH_SHORT).show();
				break;
			}
			UmengUpdateAgent.setUpdateAutoPopup(true);
			UmengUpdateAgent.setUpdateListener(null);
		}

	};

	private void shareText() {
		WXTextObject textObj = new WXTextObject();
		textObj.text = "test app adffd";
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description = "test app dafdfasfasdf";
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}

	private void shareWebPage() {
		Log.i("ForthTabFragment", "shareWebPage");
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = Constant.SHARE_URL;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "福佑相伴 省钱放心";
		msg.description = "福佑卡车分享";
		Bitmap thumb = BitmapFactory.decodeResource(getResources(),
				R.drawable.share_wx);
		msg.thumbData = Constant.bmpToByteArray(thumb, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}

	SimpleJasonParser unBindParser;
	Handler mUnBindHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mContext.cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (unBindParser.entity != null) {
					if (unBindParser.entity.status.equals("Y")) {
						String result = (String) msg.obj;
						Log.i(TAG, "result:" + result);
						SharePerfenceUtil.ClearAll(mContext);
						mRootView = null;
						mContext.finish();
						mContext.mCurrentTabIndex = 1;
						Intent intent = new Intent(mContext,
								EntryLoginActivity.class);
						startActivity(intent);
					} else {
						ToastUtils.toast(mContext, unBindParser.entity.msg);
					}
				} else {
					Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				ToastUtils.toast(mContext, "当前网络连接失败,退出失败");
			}
			super.handleMessage(msg);
		}

	};

	private void unBindGt() {
		mContext.showProgressDialog();
		unBindParser = new SimpleJasonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("gt_id", "");
		MLHttpConnect.BindGt(mContext, parmas, unBindParser, mUnBindHandler);
	}

	@Override
	protected void widgetClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.check_update:
			UmengUpdateAgent.forceUpdate(mContext);
			break;
		case R.id.contract_us_layout:
			/*
			 * Intent intent = new Intent(); // 系统默认的action，用来打开默认的电话界面
			 * intent.setAction(Intent.ACTION_CALL); // 需要拨打的号码
			 * intent.setData(Uri.parse("tel:" + mAboutPhone.getText()));
			 * startActivity(intent);
			 */
			GotoDialPage();
			break;
		case R.id.exit_app:
			unBindGt();
			break;
		case R.id.suggest_layout:
			intent = new Intent(mContext, GiveSuggestActivity.class);
			startActivity(intent);
			break;
		case R.id.about_us_layout:
			intent = new Intent(mContext, AboutUsActivity.class);
			startActivity(intent);
			break;
		case R.id.share_to_wx:
			shareWebPage();
			break;
		case R.id.check_update_layout:
			// UmengUpdateAgent.setDialogListener(arg0);
			mContext.showProgressDialog();
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(mUmUpdateListener);
			UmengUpdateAgent.update(mContext);
			// UmengUpdateAgent.forceUpdate(mContext);
			break;
		case R.id.info_layout:
			intent = new Intent(mContext, ModifyMyInfoActivity.class);
			startActivity(intent);
			break;
		}
	}

	private UserDetailJsonParser mUserDetailParser;
	private boolean isTaskRunning = false;
	public class getUserDetailTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mContext.showProgressDialog();
			mUserDetailParser = new UserDetailJsonParser();
			parmas = new HashMap<String, String>();
			isTaskRunning = true;
		}
		
		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.GetUserDetail2(mContext, parmas, mUserDetailParser);
			return msg.what;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mContext.cancelProgressDialog();

			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mUserDetailParser.entity.status.equals("Y")) {
					//String result = (String) msg.obj;
					//Log.i("aa", "result:" + Tools.UnicodeDecode(result));
					if (mUserDetailParser.entity.data.name.equals("")) {
						mName.setText("无");
					} else {
						mName.setText(mUserDetailParser.entity.data.name);
					}

					if (mUserDetailParser.entity.data.gender.equals("0")) {
						mGenderLayout.setVisibility(android.view.View.GONE);
					} else if (mUserDetailParser.entity.data.gender.equals("1")) {
						mGenderLayout.setVisibility(android.view.View.VISIBLE);
						mGender.setText("男");
					} else {
						mGenderLayout.setVisibility(android.view.View.VISIBLE);
						mGender.setText("女");
					}

					if (mUserDetailParser.entity.data.account_type.equals("")) {
						mAccountTypeLayout
								.setVisibility(android.view.View.GONE);
					} else {
						mAccountTypeLayout
								.setVisibility(android.view.View.VISIBLE);
						mAccountType
								.setText(mUserDetailParser.entity.data.account_type.text);
					}

					if (mUserDetailParser.entity.data.vip.equals("1")) {
						mVip.setText("VIP");
					} else {
						mVip.setText("");
					}

					SharePerfenceUtil.setName(mContext,
							mUserDetailParser.entity.data.name);
					SharePerfenceUtil.setMobile(mContext,
							mUserDetailParser.entity.data.mobile);
					SharePerfenceUtil.setUserType(mContext,
							mUserDetailParser.entity.data.type);
					SharePerfenceUtil.setUserGender(mContext,
							mUserDetailParser.entity.data.gender);
					SharePerfenceUtil.setUserAccountType(mContext,
							mUserDetailParser.entity.data.account_type);
				} else {
					ToastUtils.toast(mContext, mUserDetailParser.entity.msg);
				}
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			isTaskRunning = false;
		}

	}


	private void getUserDetail() {
		// mContext.showProgressDialog();
		if(isTaskRunning){
			ToastUtils.toast(mContext, "getUserDetail is running");
		}else{
			new getUserDetailTask().execute();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (SharePerfenceUtil.IsLogin(mContext)) {
			getUserDetail();
		}
	}

	@Override
	protected View inflaterView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle) {
		// TODO Auto-generated method stub
		mContext = (TabActivity) getActivity();

		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.my_info, null);
			mTitle = (TextView) mRootView.findViewById(R.id.title);
			mTitle.setText("会员中心");

			mCheckUpdate = (TextView) mRootView.findViewById(R.id.check_update);
			// mCheckUpdate.setOnClickListener(this);

			mCheckUpdateLayout = (RelativeLayout) mRootView
					.findViewById(R.id.check_update_layout);
			mCheckUpdateLayout.setOnClickListener(this);

			mCurrentVersion = (TextView) mRootView
					.findViewById(R.id.current_version);

			mAccountType = (TextView) mRootView
					.findViewById(R.id.account_type_text);
			mAccountTypeLayout = (LinearLayout) mRootView
					.findViewById(R.id.acccount_type_layout);
			String versionName = "";
			try {
				versionName = mContext.getPackageManager().getPackageInfo(
						mContext.getPackageName(),
						PackageManager.GET_CONFIGURATIONS).versionName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCurrentVersion.setText(versionName);

			mContractUsLayout = (RelativeLayout)mRootView.findViewById(R.id.contract_us_layout);
			mContractUsLayout.setOnClickListener(this);

			mAboutUsLayout = (RelativeLayout) mRootView.findViewById(R.id.about_us_layout);
			mAboutUsLayout.setOnClickListener(this);

			mInfolayout = (RelativeLayout) mRootView
					.findViewById(R.id.info_layout);
			mInfolayout.setOnClickListener(this);

			mExitApp = (Button) mRootView.findViewById(R.id.exit_app);
			mExitApp.setOnClickListener(this);

			mPhonenumber = (TextView) mRootView.findViewById(R.id.phone_number);
			mPhonenumber.setText(SharePerfenceUtil.getMobile(mContext));

			mName = (TextView) mRootView.findViewById(R.id.name);
			mSuggestLayout = (RelativeLayout) mRootView
					.findViewById(R.id.suggest_layout);
			mSuggestLayout.setOnClickListener(this);

			mGenderLayout = (LinearLayout) mRootView
					.findViewById(R.id.gender_layout);
			mGender = (TextView) mRootView.findViewById(R.id.gender);

			api = WXAPIFactory.createWXAPI(mContext, Constant.APP_ID, true);
			api.registerApp(Constant.APP_ID);
			mShareToWx = (RelativeLayout) mRootView
					.findViewById(R.id.share_to_wx);
			mShareToWx.setOnClickListener(this);

			mCover = (ImageView) mRootView.findViewById(R.id.cover_img);
			Drawable drawable = mContext.getResources().getDrawable(
					R.drawable.aver);
			Bitmap bitmap = ImageTools.toRoundBitmap(ImageTools
					.drawableToBitmap(drawable));
			mCover.setImageBitmap(bitmap);

			mVip = (TextView) mRootView.findViewById(R.id.vip);
		} else {
			ViewGroup viewgroup = (ViewGroup) mRootView.getParent();
			if (viewgroup != null) {
				viewgroup.removeView(mRootView);
			}
		}
		return mRootView;
	}

}
