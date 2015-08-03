package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.RegisterJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;

public class GetPassowrdActivity extends BaseActivity {

	private Button mNextStep;
	private TextView mTitle;
	private EditText mAccountEdit, mYanzhengmaEdit;
	private Button mGetYanzhengma;
	private SimpleJasonParser mYanzhengmaParser;
	private RegisterJsonParser mVerifyYanzhengmaParser;
	private String TAG = "GetPassowrdActivity";
	private Context mContext;
	private int COUNT_SECONDS = 60;
	private TextView mVoiceVerify;
	private long mGetVoiceVerifyTime = 0;
	private boolean voiceYanzheng = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_password);
		mContext = this;

		// DistanceUtil

		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("找回密码");

		ShowBackView();

		mNextStep = (Button) findViewById(R.id.next_step);
		mNextStep.setOnClickListener(this);

		mAccountEdit = (EditText) findViewById(R.id.account_edit);
		mGetYanzhengma = (Button) findViewById(R.id.get_yanzhengma);
		mGetYanzhengma.setOnClickListener(this);

		mYanzhengmaEdit = (EditText) findViewById(R.id.yanzhengma);

		mVoiceVerify = (TextView) findViewById(R.id.voice_verify);
		String content = "语音验证码";
		SpannableString msp = new SpannableString(content);
		msp.setSpan(new UnderlineSpan(), 0, content.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		mVoiceVerify.setText(msp);
		mVoiceVerify.setOnClickListener(this);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				mGetYanzhengma.setText(COUNT_SECONDS + "秒后重试");
				COUNT_SECONDS--;
				if (COUNT_SECONDS < 0) {
					mGetYanzhengma.setText("获取验证码");
					COUNT_SECONDS = 60;
					mGetYanzhengma.setEnabled(true);
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
				Log.i(TAG, "result:" + Tools.UnicodeDecode((String) msg.obj));
				if (mYanzhengmaParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					// Intent intent = new Intent(mContext,
					// BeginQueryPrice.class);
					// startActivity(intent);
					Log.i(TAG, "result:" + result);
					ToastUtils.toast(mContext, "验证码获取成功");
					if (!voiceYanzheng) {
						mGetYanzhengma.setEnabled(false);
						mGetYanzhengma.setText(COUNT_SECONDS + "秒后重试");
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

	Handler mVerifyYanzhengmaHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mVerifyYanzhengmaParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i(TAG, "result:" + result);
					SharePerfenceUtil.setKey(mContext,
							mVerifyYanzhengmaParser.entity.data.key);
					SharePerfenceUtil.setUid(mContext,
							mVerifyYanzhengmaParser.entity.data.uid);
					ToastUtils.toast(mContext, "验证通过");
					Intent intent = new Intent(mContext,
							SetNewPasswordActivity.class);
					startActivity(intent);
				} else {
					ToastUtils.toast(mContext,
							mVerifyYanzhengmaParser.entity.msg);
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
		parmas.put("tel", mAccountEdit.getText().toString());
		parmas.put("type", "2");
		if (voiceYanzheng) {
			parmas.put("extend", "voice");
		} else {
			parmas.put("extend", "text");
		}
		MLHttpConnect.GetYanzhengma(this, parmas, mYanzhengmaParser,
				mGetYanzhengmaHandler);
	}

	private void VerifyYanzhengma() {
		showProgressDialog();
		mVerifyYanzhengmaParser = new RegisterJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("tel", mAccountEdit.getText().toString());
		parmas.put("verify_code", mYanzhengmaEdit.getText().toString());
		MLHttpConnect.VerifyYanzhengma(this, parmas, mVerifyYanzhengmaParser,
				mVerifyYanzhengmaHandler);
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		String number = mAccountEdit.getText().toString();
		switch (id) {
		case R.id.next_step:
			if (number.equals("")) {
				ToastUtils.toast(this, "手机号不能为空!");
				return;
			}
			if (!Tools.isNumeric(number) || !number.startsWith("1")) {
				ToastUtils.toast(this, "号码格式不正确!");
				return;
			}
			if (number.length() < 11) {
				ToastUtils.toast(this, "号码长度不正确!");
				return;
			}
			if (mYanzhengmaEdit.getText().toString().equals("")) {
				ToastUtils.toast(this, "验证码不能为空");
				return;
			}

			VerifyYanzhengma();
			break;
		case R.id.get_yanzhengma:
			if (number.equals("")) {
				ToastUtils.toast(this, "手机号不能为空!");
				return;
			}
			if (!Tools.isNumeric(number) || !number.startsWith("1")) {
				ToastUtils.toast(this, "号码格式不正确!");
				return;
			}
			if (number.length() < 11) {
				ToastUtils.toast(this, "号码长度不正确!");
				return;
			}
			voiceYanzheng = false;
			getYanzhengma();
			break;
		case R.id.back_view:
			finish();
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
				mVoiceVerify.setTextColor(Color.GRAY);
				mHandler.sendEmptyMessageDelayed(1, 1000 * 60);
				voiceYanzheng = true;
				mGetVoiceVerifyTime = System.currentTimeMillis();
				getYanzhengma();
			} else {
				ToastUtils.toast(this, "60秒内只能获取一次验证码,请稍后再试");
			}
			break;
		}
	}

}
