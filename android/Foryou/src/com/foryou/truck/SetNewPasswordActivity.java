package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.ToastUtils;

public class SetNewPasswordActivity extends BaseActivity{
	Button mConfirm;
	TextView mTitle;
	RelativeLayout mBackView;
	EditText mPassword1,mPassword2;
	Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_password);
		mContext = this;
		mTitle = (TextView)findViewById(R.id.title);
		mTitle.setText("设置新密码");
		
		mConfirm = (Button)findViewById(R.id.ok);
		mConfirm.setOnClickListener(this);
		
		mBackView = (RelativeLayout)findViewById(R.id.back_view);
		mBackView.setVisibility(android.view.View.VISIBLE);
		mBackView.setOnClickListener(this);
		
		mPassword1 = (EditText)findViewById(R.id.set_password);
		mPassword2 = (EditText)findViewById(R.id.set_password2);
	}
	
	Handler mSetPassHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mSetPassJasonParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					//Log.i("aa", "result:" + result);
					ToastUtils.toast(mContext,"设置成功");
					finish();
					Intent intent = new Intent(mContext,TabActivity.class);
					startActivity(intent);
				} else {
					ToastUtils.toast(mContext, mSetPassJasonParser.entity.msg);
				}
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}

	};

	SimpleJasonParser mSetPassJasonParser;
	private void setNewPassword(){
		showProgressDialog();
		mSetPassJasonParser = new SimpleJasonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("password", mPassword1.getText().toString());
		parmas.put("re_password", mPassword2.getText().toString());
		MLHttpConnect.ResetPassword(this, parmas, mSetPassJasonParser,
				mSetPassHandler);
	}
	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch(id){
		case R.id.ok:
			if(!mPassword1.getText().toString().equals(mPassword2.getText().toString())){
				ToastUtils.toast(this, "两次输入密码不一致,请重新输入");
				return;
			}
			if(mPassword1.getText().toString().equals("")){
				ToastUtils.toast(this, "密码不能为空");
				return;
			}
			if(mPassword1.getText().toString().length()<6){
				ToastUtils.toast(this, "密码不能少于6位");
				return;
			}
			setNewPassword();
			break;
		case R.id.back_view:
			finish();
			break;
		}
	}

}
