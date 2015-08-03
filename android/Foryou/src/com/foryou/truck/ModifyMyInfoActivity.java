package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.entity.UserDetailEntity;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.UserDetailJsonParser;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;

public class ModifyMyInfoActivity extends BaseActivity {
	private TextView mFemale;
	private String[] mPayTypeItem = { "男", "女" };
	private EditText mCompany, mName;
	private Button mSaveBtn;
	private Context mContext;
	private String TAG = "ModifyMyInfoActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_myinfo);

		mContext = this;
		ShowBackView();
		setTitle("个人信息");

		mFemale = (TextView) findViewById(R.id.female);
		String gender = SharePerfenceUtil.getUserGender(this);
		if(gender.equals("0")){
			mFemale.setText("未设置");
		}else if(gender.equals("1")){
			mFemale.setText("男");
		}else{
			mFemale.setText("女");
		}
		mFemale.setOnClickListener(this);

		mCompany = (EditText) findViewById(R.id.company);
		mName = (EditText) findViewById(R.id.name);
		mName.setText(SharePerfenceUtil.getName(this));
		mName.setSelection(mName.getText().toString().length());
		
		mSaveBtn = (Button) findViewById(R.id.save_btn);
		mSaveBtn.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	class AlertListener implements OnClickListener {
		@Override
		public void onClick(DialogInterface view, int position) {
			mFemale.setText(mPayTypeItem[position]);
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (parser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i(TAG, "result:" + result);
					ToastUtils.toast(mContext, "修改成功");
					SharePerfenceUtil.setName(mContext, mName.getText().toString());
					finish();
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

	private UserDetailJsonParser parser;

	private void ModifyUserInfo() {
		showProgressDialog();
		parser = new UserDetailJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("name", mName.getText().toString());
		parmas.put("gender", mFemale.getText().toString().equals("男") ? "1"
				: "2");
		MLHttpConnect.updateUserInfo(mContext, parmas, parser, mHandler);
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.female:
			new AlertDialog.Builder(this).setItems(mPayTypeItem,
					new AlertListener()).show();
			break;
		case R.id.save_btn:
			if(mName.getText().toString().equals("")){
				ToastUtils.toast(this, "姓名不能为空");
				return;
			}
			ModifyUserInfo();
			break;
		}
	}

}
