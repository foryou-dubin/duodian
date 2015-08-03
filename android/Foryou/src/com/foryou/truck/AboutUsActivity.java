package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.AboutUsJsonParser;
import com.foryou.truck.util.ToastUtils;

public class AboutUsActivity extends BaseActivity{
	private TextView mContent;
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		mContext = this;
		
		setTitle("关于我们");
		ShowBackView();
		mContent = (TextView)findViewById(R.id.about_us);
		
		getAboutUs();
	}

	
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mSimpleParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i("aa", "result:" + result);
					mContent.setText(mSimpleParser.entity.data.about);
				} else {
					ToastUtils.toast(mContext, mSimpleParser.entity.msg);
				}
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}

	};

	AboutUsJsonParser mSimpleParser;
	private void getAboutUs() {
		showProgressDialog();
		mSimpleParser = new AboutUsJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		MLHttpConnect.GetAboutUs(this, parmas, mSimpleParser,
				mHandler);
	}

	
	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		
	}
	
}
