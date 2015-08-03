package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.entity.UserDetailEntity.UserDetail.AccountType;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.net.MLHttpConstant;
import com.foryou.truck.parser.CommonConfigJsonParser;
import com.foryou.truck.parser.GetUserContractJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.tools.ImageTools;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class BeginSendProduct extends BaseActivity {
	private ImageView mCover;
	private Button mSender;
	private RelativeLayout mContentLayout, mBaoxianLayout, mDiscountLayout;
	private TextView mBaoxianLine, mDiscountLine;
	private RelativeLayout mSenderLayout, mReceiverLayout, mPayOnlineLayout,
			mPayOfflineLayout;
	private TextView mPayOnlineline, mPayOfflineline;
	private TextView mAgreeText, mFetchDetail, mSenderHintText,
			mReceiverHintText, mCouponText;
	private ImageView mAgreeCheckImg;

	private String order_id, agent_id, cover, name, chengdan_m, chengdan_t,
			baojia, xinyi, expire_time, phone_number, pingjia, pay_type;
	int remain_time;
	private TextView mName, mChengdanM, mChengdanT, mBaojia, mRemainTime,
			mXinyi, mChakan, mPinjia;
	private Long remainTime;
	private String start_place,end_place,hd_start_place,hd_end_place;

	private Activity mContext;
	private String TAG = "BeginSendProduct";

	private String mCache;
	private CommonConfigJsonParser mConfigparser;

	private LinearLayout mContactlayout;

	private class SenderInfo {
		String sender_name = "";
		String sender_mobile = "";
		String sender_mobile2 = "";
		String sender_address = "";

	}

	private class ReceiverInfo {
		String receiver_name = "";
		String receiver_mobile = "";
		String receiver_mobile2 = "";
		String receiver_address = "";
	}

	private class BaoxianInfo {
		String insurance_name = "";
		String insurance_num = "";
		String insurance_package = "";
		String insurance_type = "";
		String insurance_value = "";
	}

	SenderInfo mSenderInfo = new SenderInfo();
	ReceiverInfo mReceiverInfo = new ReceiverInfo();
	BaoxianInfo mBaoxianInfo = new BaoxianInfo();

	private ImageLoadingListener mLoaderlistenr = new ImageLoadingListener() {

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			// TODO Auto-generated method stub
			mCover.setImageBitmap(ImageTools.toRoundBitmap(arg2));
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			// TODO Auto-generated method stub

		}

	};

	private void UpdateRemainTimeText(TextView v, Long seconds) {
		if (seconds <= 0) {
			v.setText("报价已失效");
			return;
		}
		Long number = seconds;
		String content = "";
		if (seconds / (60 * 60) > 0) {
			content += seconds / 3600 + "小时";
		} else {
			content += 0 + "小时";
		}
		number = number % 3600;
		if (number / 60 > 0) {
			content += number / 60 + "分";
		} else {
			content += 0 + "分";
		}
		content += number % 60 + "秒";
		int numberColor = mContext.getResources().getColor(
				R.color.tishi_bg_color);
		SpannableString msp = new SpannableString(content);
		msp.setSpan(new ForegroundColorSpan(numberColor), 0,
				content.indexOf("小时"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), 0, content.indexOf("小时"),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		msp.setSpan(new ForegroundColorSpan(numberColor),
				content.indexOf("小时") + 2, content.indexOf("分"),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), content.indexOf("小时") + 2,
				content.indexOf("分"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		msp.setSpan(new ForegroundColorSpan(numberColor),
				content.indexOf("分") + 1, content.indexOf("秒"),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), content.indexOf("分") + 1,
				content.indexOf("秒"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		v.setText(msp);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.begin_send_product);
		mContext = this;

		Intent intent = getIntent();
		order_id = intent.getStringExtra("order_id");
		agent_id = intent.getStringExtra("agent_id");
		cover = intent.getStringExtra("mCover");
		name = intent.getStringExtra("name");
		chengdan_m = intent.getStringExtra("chengdan_m");
		chengdan_t = intent.getStringExtra("chengdan_t");
		baojia = intent.getStringExtra("baojia");
		expire_time = intent.getStringExtra("expire_time");
		phone_number = intent.getStringExtra("phone_number");
		xinyi = intent.getStringExtra("xinyi");
		pingjia = intent.getStringExtra("pingjia");
		pay_type = intent.getStringExtra("pay_type");
		start_place = intent.getStringExtra("start_place");
		end_place = intent.getStringExtra("end_place");
		hd_start_place = intent.getStringExtra("hd_start_place");
		hd_end_place = intent.getStringExtra("hd_end_place");
		
		Log.i(TAG, "start_place:"+start_place+",end_place:"+end_place);
		// remain_time = intent.getStringExtra("remain_time");

		mCover = (ImageView) findViewById(R.id.mCover);
		imageLoader.loadImage(cover, mLoaderlistenr);

		mName = (TextView) findViewById(R.id.name);
		mName.setText(name);

		mChengdanM = (TextView) findViewById(R.id.chengdan_m);
		mChengdanM.setText(chengdan_m);

		mChengdanT = (TextView) findViewById(R.id.chengdan_t);
		mChengdanT.setText(chengdan_t);

		mBaojia = (TextView) findViewById(R.id.baojia);
		mBaojia.setText(baojia);

		mRemainTime = (TextView) findViewById(R.id.remain_time);

		mSender = (Button) findViewById(R.id.to_order);
		mSender.setOnClickListener(this);

		mXinyi = (TextView) findViewById(R.id.xinyi);
		mXinyi.setText(xinyi);

		mPinjia = (TextView) findViewById(R.id.pingjia);
		mPinjia.setText(pingjia);
		mChakan = (TextView) findViewById(R.id.chakan);
		mChakan.setOnClickListener(this);
		if (pingjia.charAt(0) == '0') {
			mChakan.setVisibility(android.view.View.GONE);
		}

		setTitle("选择报价下单");
		ShowBackView();

		mContentLayout = (RelativeLayout) findViewById(R.id.content_layout);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, ScreenInfo.dip2px(this,
						150));
		mContentLayout.setLayoutParams(lp);

		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp2.rightMargin = ScreenInfo.dip2px(this, 12);
		Log.i(TAG, "" + ScreenInfo.getScreenInfo(this).widthPixels + ","
				+ ScreenInfo.getScreenInfo(this).heightPixels);
		if (ScreenInfo.getScreenInfo(this).widthPixels <= 540) {
			lp2.bottomMargin = 0;
		} else {
			lp2.bottomMargin = ScreenInfo.dip2px(this, 8);
		}

		mContactlayout = (LinearLayout) findViewById(R.id.contact_layout);
		mContactlayout.setOnClickListener(contactClickListener);

		mBaoxianLayout = (RelativeLayout) findViewById(R.id.baoxian_layout);
		mBaoxianLayout.setOnClickListener(this);
		mBaoxianLine = (TextView) findViewById(R.id.baoxian_line);
		mDiscountLayout = (RelativeLayout) findViewById(R.id.discount_layout);
		mDiscountLayout.setOnClickListener(this);
		mCouponText = (TextView) findViewById(R.id.daijinquan);
		mDiscountLine = (TextView) findViewById(R.id.discount_line);

		mSenderLayout = (RelativeLayout) findViewById(R.id.sender_layout);
		mSenderLayout.setOnClickListener(this);
		mReceiverLayout = (RelativeLayout) findViewById(R.id.receiver_layout);
		mReceiverLayout.setOnClickListener(this);

		mPayOnlineLayout = (RelativeLayout) findViewById(R.id.payonline_layout);
		mPayOnlineLayout.setOnClickListener(this);
		mPayOnlineline = (TextView) findViewById(R.id.payonline_line);
		mPayOfflineLayout = (RelativeLayout) findViewById(R.id.payoffline_layout);
		mPayOfflineLayout.setOnClickListener(this);
		mPayOfflineline = (TextView) findViewById(R.id.payoffline_line);

		mAgreeText = (TextView) findViewById(R.id.agree_text);
		mAgreeText.setOnClickListener(this);
		mAgreeCheckImg = (ImageView) findViewById(R.id.checkimg3);
		mAgreeCheckImg.setOnClickListener(this);

		mFetchDetail = (TextView) findViewById(R.id.fetch_detail);
		mFetchDetail.setOnClickListener(this);

		mSenderHintText = (TextView) findViewById(R.id.send_info);
		mReceiverHintText = (TextView) findViewById(R.id.receive_info);

		String cacheUrl = MLHttpConstant.URL_START + "/common/config?version="
				+ Tools.getVersionCode(mContext);
		mCache = MLHttpConnect2.readCacheDirectly(cacheUrl, mContext);
		// Log.i(TAG, "mCache:" + mCache);
		if (mCache != null && !TextUtils.isEmpty(mCache)) {
			mConfigparser = new CommonConfigJsonParser();
			mConfigparser.parser(mCache);
			// initProvinceDatas();
			InitView();
		} else {
		}
		getUserContract();
	}

	private View.OnClickListener contactClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Constant.GotoDialPage(mContext, phone_number);
		}
	};

	private void InitView() {
		boolean insurance_allow_type = false;
		for (int i = 0; i < mConfigparser.entity.data.system_switch.size(); i++) {
			String key = mConfigparser.entity.data.system_switch.get(i).key;
			String value = mConfigparser.entity.data.system_switch.get(i).value;
			Log.i(TAG, "key:" + key + ",value:" + value);

			if (key.equals("insurance_switch")) {
				if (value.equals("1")) {
					mBaoxianLayout.setVisibility(android.view.View.VISIBLE);
					mBaoxianLine.setVisibility(android.view.View.VISIBLE);
				} else {
					mBaoxianLayout.setVisibility(android.view.View.GONE);
					mBaoxianLine.setVisibility(android.view.View.GONE);
				}
			}
			if (key.equals("coupon_switch")) {
				if (value.equals("1")) {
					mDiscountLayout.setVisibility(android.view.View.VISIBLE);
					mDiscountLine.setVisibility(android.view.View.VISIBLE);
				} else {
					mDiscountLayout.setVisibility(android.view.View.GONE);
					mDiscountLine.setVisibility(android.view.View.GONE);
				}
			}

			if (key.equals("insurance_allow_type")) {
				int len = value.length();
				AccountType accountType = SharePerfenceUtil
						.getUserAccountType(mContext);
				Log.i(TAG, "accountType:" + accountType.key);
				if (len > 1) {
					String[] type = value.split(",");
					for (int j = 0; j < type.length; j++) {
						if (type[j].equals(accountType.key)) {
							insurance_allow_type = true;
							break;
						}
					}
				} else {
					if (value.equals(accountType.key)) {
						insurance_allow_type = true;
					}
				}
			}

			if (key.equals("payment_switch")) {
				if (value.equals("1")
						&& Constant.needPayOnLine(mContext, pay_type)) {
					mPayOnlineLayout.setVisibility(android.view.View.VISIBLE);
					mPayOfflineLayout.setVisibility(android.view.View.VISIBLE);
					mPayOnlineline.setVisibility(android.view.View.VISIBLE);
					mPayOfflineline.setVisibility(android.view.View.VISIBLE);
				} else {
					mPayOnlineLayout.setVisibility(android.view.View.GONE);
					mPayOfflineLayout.setVisibility(android.view.View.GONE);
					mPayOnlineline.setVisibility(android.view.View.GONE);
					mPayOfflineline.setVisibility(android.view.View.GONE);
				}
			}
		}

		if (insurance_allow_type == false) {
			mBaoxianLayout.setVisibility(android.view.View.GONE);
			mBaoxianLine.setVisibility(android.view.View.GONE);
		}
	}

	private Handler mReflesh = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			remainTime--;
			UpdateRemainTimeText(mRemainTime, remainTime);
			if (remainTime > 0) {
				mSender.setBackgroundResource(R.drawable.login_btn);
				mReflesh.sendEmptyMessageDelayed(0, 1000);
			} else {
				mSender.setBackgroundResource(R.drawable.anniuhui);
			}
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		remainTime = (Long) (Long.valueOf(Long.valueOf(expire_time)) - System
				.currentTimeMillis() / 1000);
		if (remainTime > 0) {
			// mTimer.schedule(task, 0,1000);
			mSender.setBackgroundResource(R.drawable.login_btn);
			mReflesh.sendEmptyMessageDelayed(0, 1000);
		} else {
			mSender.setBackgroundResource(R.drawable.anniuhui);
		}
		UpdateRemainTimeText(mRemainTime, remainTime);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// mTimer.cancel();
		mReflesh.removeMessages(0);
	}

	SimpleJasonParser parser;
	private boolean isTaskRunning = false;

	public class QuoteConfirmTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			parmas = new HashMap<String, String>();
			parser = new SimpleJasonParser();
			parmas.put("order_id", order_id);
			parmas.put("agent_id", agent_id);
			parmas.put("sender_name", mSenderInfo.sender_name);
			parmas.put("sender_mobile", mSenderInfo.sender_mobile);
			parmas.put("sender_address", mSenderInfo.sender_address);
			parmas.put("receiver_name", mReceiverInfo.receiver_name);
			parmas.put("receiver_mobile", mReceiverInfo.receiver_mobile);
			parmas.put("receiver_address", mReceiverInfo.receiver_address);
			parmas.put("sender_mobile2", mSenderInfo.sender_mobile2);
			parmas.put("receiver_mobile2", mReceiverInfo.receiver_mobile2);
			if (mPayOnlineLayout.getVisibility() == android.view.View.VISIBLE) {
				if (mPayOfflineLayout.getTag().equals("1")) {
					parmas.put("payee", "2");
				} else {
					parmas.put("payee", "1");
				}
			} else {
				parmas.put("payee", "");
			}

			if (mBaoxianInfo.insurance_name.equals("")) {
				parmas.put("insurance_flag", "0");
			} else {
				parmas.put("insurance_flag", "1");
			}
			parmas.put("insurance_name", mBaoxianInfo.insurance_name);
			parmas.put("insurance_num", mBaoxianInfo.insurance_num);
			parmas.put("insurance_type", mBaoxianInfo.insurance_type);
			parmas.put("insurance_package", mBaoxianInfo.insurance_package);

			if (mCouponText.getText().toString().equals("")) {
				parmas.put("coupon_flag", "0");
			} else {
				parmas.put("coupon_flag", "1");
			}
			parmas.put("coupon", mCouponText.getText().toString());

			isTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();
			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (parser.entity.status.equals("Y")) {
					ToastUtils.toast(mContext, "选择报价成功");
					Intent intent;
					if (mPayOnlineLayout.getTag().equals("1")) {
						intent = new Intent(mContext, PayOnLineActivity.class);
						intent.putExtra("order_id", order_id);
						startActivity(intent);
					} else {
						intent = new Intent(mContext, TabActivity.class);
						TabActivity.mCurrentTabIndex = 3;
						startActivity(intent);
					}
				} else {
					ToastUtils.toast(mContext, parser.entity.msg);
				}
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			isTaskRunning = false;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.QuoteConfirm2(mContext, parmas, parser);
			return msg.what;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "resultCode:" + resultCode);
		if (resultCode == Constant.GET_SENDER_CODE) {
			// sender info return
			mSenderInfo.sender_name = data.getStringExtra("sender_name");
			mSenderInfo.sender_mobile = data.getStringExtra("sender_mobile");
			mSenderInfo.sender_mobile2 = data.getStringExtra("sender_mobile2");
			mSenderInfo.sender_address = data.getStringExtra("sender_address");
			if (mSenderInfo.sender_name.equals("")
					&& mSenderInfo.sender_mobile.equals("")) {
				mSenderHintText.setText("");
			} else {
				mSenderHintText.setText(mSenderInfo.sender_name + "    "
						+ mSenderInfo.sender_mobile);
			}
		} else if (resultCode == Constant.GET_RECEIVER_CODE) {
			mReceiverInfo.receiver_name = data.getStringExtra("receiver_name");
			mReceiverInfo.receiver_mobile = data
					.getStringExtra("receiver_mobile");
			mReceiverInfo.receiver_mobile2 = data
					.getStringExtra("receiver_mobile2");
			mReceiverInfo.receiver_address = data
					.getStringExtra("receiver_address");
			if (mReceiverInfo.receiver_name.equals("")
					&& mReceiverInfo.receiver_mobile.equals("")) {
				mReceiverHintText.setText("");
			} else {
				mReceiverHintText.setText(mReceiverInfo.receiver_name + "    "
						+ mReceiverInfo.receiver_mobile);
			}
		} else if (resultCode == Constant.GET_BAOXIAN_CODE) {
			mBaoxianInfo.insurance_name = data.getStringExtra("insurance_name");
			mBaoxianInfo.insurance_num = data.getStringExtra("insurance_num");
			mBaoxianInfo.insurance_package = data
					.getStringExtra("insurance_package");
			mBaoxianInfo.insurance_type = data.getStringExtra("insurance_type");
			mBaoxianInfo.insurance_value = data
					.getStringExtra("insurance_value");
			if (!mBaoxianInfo.insurance_name.equals("")) {
				((TextView) mBaoxianLayout.findViewById(R.id.baoxian_text))
						.setText("保险人：" + mBaoxianInfo.insurance_name);
			}
		} else if (resultCode == Constant.GET_COUPON_CODE) {
			String coupon = data.getStringExtra("coupon");
			mCouponText.setText(coupon);
		} else if (resultCode == Constant.AGREEMENT_CODE) {
			if (mAgreeCheckImg.getTag().equals("0")) {
				mAgreeCheckImg.setImageResource(R.drawable.check_btn_clicked);
				mAgreeCheckImg.setTag("1");
			}
		} else if (resultCode == Constant.NO_BAOXIAN_CODE) {
			((TextView) mBaoxianLayout.findViewById(R.id.baoxian_text))
					.setText("");
			mBaoxianInfo.insurance_name = "";
			mBaoxianInfo.insurance_num = "";
			mBaoxianInfo.insurance_package = "";
			mBaoxianInfo.insurance_type = "";
			mBaoxianInfo.insurance_value = "";
			
		}else if(resultCode == Constant.NO_DISCOUNT_CODE){
			mCouponText.setText("");
		}
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (id) {
		case R.id.to_order:
			if (remainTime <= 0) {
				ToastUtils.toast(mContext, "很抱歉,此报价已失效,无法下单");
				return;
			}

			if (mSenderInfo.sender_name.equals("")) {
				ToastUtils.toast(this, "请填写发货人相关信息");
				return;
			}

			if (mReceiverInfo.receiver_name.equals("")) {
				ToastUtils.toast(this, "请填写收货人相关信息");
				return;
			}

			if (mAgreeCheckImg.getTag().equals("0")) {
				ToastUtils.toast(this, "您还未同意福佑卡车托运协议");
				return;
			}

			if (mPayOnlineLayout.getVisibility() == android.view.View.VISIBLE) {
				if (mPayOfflineLayout.getTag().equals("0")
						&& mPayOnlineLayout.getTag().equals("0")) {
					ToastUtils.toast(this, "请选择支付方式");
					return;
				}
			}

			if (isTaskRunning)
				return;

			new QuoteConfirmTask().execute();
			break;
		case R.id.back_view:
			finish();
			break;
		case R.id.sender_layout:
			intent = new Intent(mContext, GetSenderInfoActivity.class);
			intent.putExtra("sender_name", mSenderInfo.sender_name);
			intent.putExtra("sender_mobile", mSenderInfo.sender_mobile);
			intent.putExtra("sender_mobile2", mSenderInfo.sender_mobile2);
			intent.putExtra("sender_address", mSenderInfo.sender_address);
			intent.putExtra("start_place", start_place);
			intent.putExtra("hd_start_place", hd_start_place);
			this.startActivityForResult(intent, 100);
			break;
		case R.id.receiver_layout:
			intent = new Intent(mContext, GetReceiverInfoActivity.class);
			intent.putExtra("receiver_name", mReceiverInfo.receiver_name);
			intent.putExtra("receiver_mobile", mReceiverInfo.receiver_mobile);
			intent.putExtra("receiver_mobile2", mReceiverInfo.receiver_mobile2);
			intent.putExtra("receiver_address", mReceiverInfo.receiver_address);
			intent.putExtra("end_place", end_place);
			intent.putExtra("hd_end_place", hd_end_place);
			this.startActivityForResult(intent, 101);
			break;
		case R.id.payonline_layout:
			if (mPayOnlineLayout.getTag().equals("0")) {

				setViewChecked(mPayOnlineLayout.findViewById(R.id.checkimg1),
						true);
				mPayOnlineLayout.setTag("1");

				setViewChecked(mPayOfflineLayout.findViewById(R.id.checkimg2),
						false);
				mPayOfflineLayout.setTag("0");
			}
			break;
		case R.id.payoffline_layout:
			if (mPayOfflineLayout.getTag().equals("0")) {

				setViewChecked(mPayOfflineLayout.findViewById(R.id.checkimg2),
						true);
				mPayOfflineLayout.setTag("1");

				setViewChecked(mPayOnlineLayout.findViewById(R.id.checkimg1),
						false);
				mPayOnlineLayout.setTag("0");
			}
			break;

		case R.id.agree_text:
		case R.id.checkimg3:
			if (mAgreeCheckImg.getTag().equals("0")) {
				mAgreeCheckImg.setImageResource(R.drawable.check_btn_clicked);
				mAgreeCheckImg.setTag("1");
			} else {
				mAgreeCheckImg.setImageResource(R.drawable.chec_btn);
				mAgreeCheckImg.setTag("0");
			}
			break;
		case R.id.fetch_detail:
			intent = new Intent(this, XieyiActivity.class);
			this.startActivityForResult(intent, 104);
			break;
		case R.id.baoxian_layout:
			intent = new Intent(this, GetBaoxianActivity.class);
			GetBaoxianActivity.insurance_type = mConfigparser.entity.data.insurance_type;
			intent.putExtra("insurance_name", mBaoxianInfo.insurance_name);
			intent.putExtra("insurance_num", mBaoxianInfo.insurance_num);
			intent.putExtra("insurance_package", mBaoxianInfo.insurance_package);
			intent.putExtra("insurance_type", mBaoxianInfo.insurance_type);
			intent.putExtra("insurance_value", mBaoxianInfo.insurance_value);
			startActivityForResult(intent, 102);
			break;
		case R.id.discount_layout:
			intent = new Intent(this, getDiscountActivity.class);
			intent.putExtra("coupon", mCouponText.getText().toString());
			startActivityForResult(intent, 103);
			break;
		case R.id.chakan:
			intent = new Intent(mContext, CommentActivity.class);
			intent.putExtra("agent_id", agent_id);
			intent.putExtra("name", name.substring(2, name.length()));
			startActivity(intent);
			break;
		}
	}

	private void setViewChecked(View view, boolean flag) {
		if (flag) {
			view.setBackgroundResource(R.drawable.check_btn_clicked);
		} else {
			view.setBackgroundResource(R.drawable.chec_btn);
		}
	}

	private GetUserContractJsonParser mContractParser;
	private boolean isGetContactRunning = false;

	private void getUserContract() {
		if (isGetContactRunning) {
			Log.i("aa", "getUserContract task is not finish ....");
			return;
		} else {
			new GetContractTask().execute();
		}
	}

	public class GetContractTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mContractParser = new GetUserContractJsonParser();
			parmas = new HashMap<String, String>();
			isGetContactRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();

			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mContractParser.entity.status.equals("Y")) {
					SharePerfenceUtil.SaveUserContactData(mContext,
							mContractParser.entity);
				} else {
					ToastUtils.toast(mContext, "网络连接失败，请稍后重试");
				}
				break;
			case MLHttpConnect2.FAILED:
				ToastUtils.toast(mContext, "网络连接失败，请稍后重试");
				break;
			}
			isGetContactRunning = false;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.getContract(mContext, parmas, mContractParser);
			return msg.what;
		}

	}
}
