package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.CommonConfigJsonParser;
import com.foryou.truck.parser.RegisterJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;

public class RegisterActivity extends BaseActivity {
	TextView mTitle, mReLogin;
	EditText mAccountEditText, mPasswordEditText, mYanzhengmaEditText;
	Button mGetYanzhengmaBtn;
	SimpleJasonParser mYanzhengmaParser;
	RegisterJsonParser mRegisterParser;
	Context mContext;
	int COUNT_SECONDS = 60;
	Button mRegisterBtn;
	ImageView mNumberClose, mPassClose;
	TextView mVoiceVerify;
	boolean voiceYanzheng = false;
	long mGetVoiceVerifyTime = (long) 0;
	private RelativeLayout mAccountTypeLayout;
	private TextView mAccoutTypeText;
	private String[] mAccountTypeItem;
	private String mChooseKey = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foryou_register);
		mContext = this;

		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("新用户注册");

		ShowBackView();

		mReLogin = (TextView) findViewById(R.id.re_login);
		mReLogin.setOnClickListener(this);

		mAccountEditText = (EditText) findViewById(R.id.account_edit);
		mPasswordEditText = (EditText) findViewById(R.id.mPassword_edit);
		mYanzhengmaEditText = (EditText) findViewById(R.id.yanzhengma_edit);

		mGetYanzhengmaBtn = (Button) findViewById(R.id.get_yanzhengma);
		mGetYanzhengmaBtn.setOnClickListener(this);

		mRegisterBtn = (Button) findViewById(R.id.register_btn);
		mRegisterBtn.setOnClickListener(this);

		mNumberClose = (ImageView) findViewById(R.id.close_img);
		mNumberClose.setOnClickListener(this);
		mPassClose = (ImageView) findViewById(R.id.close_img2);
		mPassClose.setOnClickListener(this);

		mVoiceVerify = (TextView) findViewById(R.id.voice_verify);
		String content = "语音验证码";
		SpannableString msp = new SpannableString(content);
		msp.setSpan(new UnderlineSpan(), 0, content.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		mVoiceVerify.setText(msp);
		mVoiceVerify.setOnClickListener(this);

		mAccountTypeLayout = (RelativeLayout) findViewById(R.id.acccount_type_layout);
		mAccountTypeLayout.setOnClickListener(this);
		mAccoutTypeText = (TextView) findViewById(R.id.account_type_text);

		getConfigData();
	}

	private CommonConfigJsonParser mConfigParser;
	Handler mConfigHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (MLHttpConnect2.SUCCESS == msg.what) {
				String result = (String) msg.obj;
				Log.i("aa", "result:" + Tools.UnicodeDecode(result));
				if (mConfigParser.entity.status.equals("Y")) {
					// SharePerfenceUtil.SaveConfigData(mContext,
					// mConfigParser.entity);
					mAccountTypeItem = new String[mConfigParser.entity.data.account_type
							.size()];
					for (int i = 0; i < mAccountTypeItem.length; i++) {
						mAccountTypeItem[i] = mConfigParser.entity.data.account_type
								.get(i).value;
					}
				}

			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}

	};

	private void getConfigData() {
		mConfigParser = new CommonConfigJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		MLHttpConnect.getConfigData(mContext, parmas, mConfigParser,
				mConfigHandler);
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mGetYanzhengmaBtn.setText(COUNT_SECONDS + "秒后重试");
				COUNT_SECONDS--;
				if (COUNT_SECONDS < 0) {
					mGetYanzhengmaBtn.setText("获取验证码");
					COUNT_SECONDS = 60;
					mGetYanzhengmaBtn.setEnabled(true);
				} else {
					mHandler.sendEmptyMessageDelayed(0, 1000);
				}
				break;
			case 1:
				mVoiceVerify.setTextColor(Color.RED);
				break;
			}
		}
	};

	Handler mGetYanzhengmaHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mYanzhengmaParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i("aa", "result:" + result);
					ToastUtils.toast(mContext, "验证码获取成功");
					if (!voiceYanzheng) {
						mGetYanzhengmaBtn.setEnabled(false);
						mGetYanzhengmaBtn.setText(COUNT_SECONDS + "秒后重试");
						COUNT_SECONDS--;
						mHandler.sendEmptyMessageDelayed(0, 1000);
					}

				} else {
					ToastUtils.toast(mContext, mYanzhengmaParser.entity.msg);
				}
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}

	};

	private void getYanzhengma() {
		showProgressDialog();
		mYanzhengmaParser = new SimpleJasonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("tel", mAccountEditText.getText().toString());
		parmas.put("type", "1");
		if (voiceYanzheng) {
			parmas.put("extend", "voice");
		} else {
			parmas.put("extend", "text");
		}
		MLHttpConnect.GetYanzhengma(this, parmas, mYanzhengmaParser,
				mGetYanzhengmaHandler);
	}

	Handler mRegisterHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mRegisterParser.entity.status.equals("Y")) {
					SharePerfenceUtil.setKey(mContext,
							mRegisterParser.entity.data.key);
					SharePerfenceUtil.setUid(mContext,
							mRegisterParser.entity.data.uid);
					ToastUtils.toast(mContext, "登录成功!");
					finish();
					Intent intent = new Intent(mContext, TabActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				} else {
					ToastUtils.toast(mContext, mRegisterParser.entity.msg);
				}
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}

	};

	private void Register() {
		showProgressDialog();
		mRegisterParser = new RegisterJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("tel", mAccountEditText.getText().toString());
		parmas.put("verify_code", mYanzhengmaEditText.getText().toString());
		parmas.put("password", mPasswordEditText.getText().toString());
		parmas.put("account_type", mChooseKey);
		MLHttpConnect.registerAccount(this, parmas, mRegisterParser,
				mRegisterHandler);
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		String number = mAccountEditText.getText().toString();
		switch (id) {
		case R.id.re_login:
			finish();
			break;
		case R.id.get_yanzhengma:
			if (number.equals("")) {
				ToastUtils.toast(this, "手机号不能为空");
				return;
			}
			if (!Tools.isNumeric(number) || !number.startsWith("1")) {
				ToastUtils.toast(this, "号码格式不正确");
				return;
			}
			if (number.length() < 11) {
				ToastUtils.toast(this, "号码长度不正确");
				return;
			}
			voiceYanzheng = false;
			getYanzhengma();
			break;
		case R.id.register_btn:
			if (number.equals("")) {
				ToastUtils.toast(this, "手机号不能为空");
				return;
			}
			if (!Tools.isNumeric(number) || !number.startsWith("1")) {
				ToastUtils.toast(this, "号码格式不正确");
				return;
			}
			if (number.length() < 11) {
				ToastUtils.toast(this, "号码长度不正确");
				return;
			}
			if (mPasswordEditText.getText().toString().equals("")) {
				ToastUtils.toast(this, "密码不能为空");
				return;
			}
			if (mPasswordEditText.getText().toString().length() < 6) {
				ToastUtils.toast(this, "密码不能低于六位");
				return;
			}
			if (mYanzhengmaEditText.getText().toString().equals("")) {
				ToastUtils.toast(this, "验证码不能为空");
				return;
			}
			if (mChooseKey.equals("")) {
				ToastUtils.toast(this, "请选择客户类型");
				return;
			}
			Register();
			break;
		case R.id.close_img:
			mAccountEditText.getEditableText().clear();
			break;
		case R.id.close_img2:
			mPasswordEditText.getEditableText().clear();
			break;
		case R.id.voice_verify:
			if (number.equals("")) {
				ToastUtils.toast(this, "手机号不能为空");
				return;
			}
			if (!Tools.isNumeric(number) || !number.startsWith("1")) {
				ToastUtils.toast(this, "号码格式不正确");
				return;
			}
			if (number.length() < 11) {
				ToastUtils.toast(this, "号码长度不正确");
				return;
			}

			if (System.currentTimeMillis() - mGetVoiceVerifyTime > 60 * 1000) {
				voiceYanzheng = true;
				mVoiceVerify.setTextColor(Color.GRAY);
				mHandler.sendEmptyMessageDelayed(1, 1000 * 60);
				mGetVoiceVerifyTime = System.currentTimeMillis();
				getYanzhengma();
			} else {
				ToastUtils.toast(this, "60秒内只能获取一次验证码,请稍后再试");
			}
			break;
		case R.id.acccount_type_layout:
			if(mConfigParser == null){
				ToastUtils.toast(this, "无法获取账户类型配置,请稍后再试");
				return;
			}
			new AlertDialog.Builder(mContext).setItems(mAccountTypeItem,
					new AlertListener()).show();
			break;
		}
	}

	class AlertListener implements OnClickListener {
		@Override
		public void onClick(DialogInterface view, int position) {
			mChooseKey = mConfigParser.entity.data.account_type.get(position).key;
			mAccoutTypeText.setText(mAccountTypeItem[position]);
			mAccoutTypeText.setTextColor(Color.BLACK);
		}
	}
}
