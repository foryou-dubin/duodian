package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.ContractJsonParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ToastUtils;

public class XieyiActivity extends BaseActivity{
	private TextView mContent;
	private Button mAggree;
	private String TAG = "XieyiActivity";
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xieyi);
		
		mContext = this;
		ShowBackView();
		setTitle("福佑卡车托运协议");
		
		mContent = (TextView)findViewById(R.id.content);
		
		mAggree = (Button)findViewById(R.id.aggree);
		mAggree.setOnClickListener(this);
		
		Log.i(TAG,"onCreate ........");
		getXieyi();
	}
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mSimpleParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i("aa", "result:" + result);
					mContent.setText(mSimpleParser.entity.data.contract);
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
	
	ContractJsonParser mSimpleParser;
	private void getXieyi() {
		showProgressDialog();
		mSimpleParser = new ContractJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		MLHttpConnect.GetContract(this, parmas, mSimpleParser,
				mHandler);
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch(id){
		case R.id.aggree:
			Log.i(TAG,"aggree ........");
			setResult(Constant.AGREEMENT_CODE);
			finish();
			break;
		}
	}

}
