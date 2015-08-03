package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.RegisterJsonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;

public class EntryLoginActivity extends BaseActivity {

	TextView mTitle, mRegister, mGetPassword;
	Button mLogin;
	ProgressBar mProgressBar;
	private RegisterJsonParser parser;
	private Context mContext;
	private String TAG = "EntryLoginActivity";
	private EditText mAccountEdit, mPassowordEdit;
	private ImageView mNumberClose,mPasswordClose;
	private RelativeLayout mBackView;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (parser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i(TAG, "result:" + result);
					SharePerfenceUtil.setKey(mContext, parser.entity.data.key);
					SharePerfenceUtil.setUid(mContext, parser.entity.data.uid);
					finish();
					Intent intent = new Intent(mContext, TabActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				} else {
					ToastUtils.toast(mContext, parser.entity.msg);
				}
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}

	};
	
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		super.onBackPressed();
		Intent intent = new Intent(this,TabActivity.class);
		startActivity(intent);
	}

	private void LoginAccount() {
		showProgressDialog();
		parser = new RegisterJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("tel", mAccountEdit.getText().toString());
		parmas.put("password", mPassowordEdit.getText().toString());
		MLHttpConnect.LoginAccount(this, parmas, parser, mHandler);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foryou_login);
		mContext = this;

		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("用户登录");
		
		//ShowBackView();
		mBackView = (RelativeLayout)findViewById(R.id.back_view);
		mBackView.setVisibility(android.view.View.VISIBLE);
		mBackView.setOnClickListener(this);

		mLogin = (Button) findViewById(R.id.login_btn);
		mLogin.setOnClickListener(this);

		mRegister = (TextView) findViewById(R.id.register);
		mRegister.setOnClickListener(this);

		mGetPassword = (TextView) findViewById(R.id.fetch_password);
		mGetPassword.setOnClickListener(this);

		mAccountEdit = (EditText) findViewById(R.id.account_edit);
		mPassowordEdit = (EditText) findViewById(R.id.mPassword_edit);
		
		mNumberClose = (ImageView)findViewById(R.id.close_img);
		mNumberClose.setOnClickListener(this);
		mPasswordClose = (ImageView)findViewById(R.id.close_img2);
		mPasswordClose.setOnClickListener(this);

		// mProgressBar = (ProgressBar)findViewById(R.id.progtessBer_btn_id1);
		// mProgressBar.setVisibility(android.view.View.VISIBLE);
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (id) {
		case R.id.login_btn:
			String number = mAccountEdit.getText().toString();
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
			if (mPassowordEdit.getText().toString().equals("")) {
				ToastUtils.toast(this, "密码不能为空");
				return;
			}
			if (mPassowordEdit.getText().toString().length() < 6) {
				ToastUtils.toast(this, "密码长度不能低于6位");
				return;
			}
			LoginAccount();
			break;
		case R.id.register:
			intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.fetch_password:
			intent = new Intent(this, GetPassowrdActivity.class);
			startActivity(intent);
			break;
		case R.id.close_img:
			mAccountEdit.getEditableText().clear();
			break;
		case R.id.close_img2:
			mPassowordEdit.getEditableText().clear();
			break;
		case R.id.back_view:
			onBackPressed();
			break;
		}
	}
}
