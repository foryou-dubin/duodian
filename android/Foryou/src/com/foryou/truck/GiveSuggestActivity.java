package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.ToastUtils;

public class GiveSuggestActivity extends BaseActivity{
	private TextView mTitle;
	private EditText mContent;
	private Context mContext;
	private Button mFinishBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.give_suggest);
		
		mContext = this;
		
		mTitle = (TextView)findViewById(R.id.title);
		mTitle.setText("意见反馈");
		
		ShowBackView();
		mContent = (EditText)findViewById(R.id.give_content);
		
		mFinishBtn = (Button)findViewById(R.id.finish_btn);
		mFinishBtn.setOnClickListener(this);
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mSimpleParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i("aa", "result:" + result);
					ToastUtils.toast(mContext,"反馈成功");
					finish();
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

	SimpleJasonParser mSimpleParser;
	private void pushSuggest() {
		showProgressDialog();
		mSimpleParser = new SimpleJasonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("content", mContent.getText().toString());
		MLHttpConnect.GiveAdvice(this, parmas, mSimpleParser,
				mHandler);
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch(id){
		case R.id.finish_btn:
			if(mContent.getText().toString().equals("")){
				ToastUtils.toast(mContext, "您没有填写任何内容");
				return;
			}
			pushSuggest();
			break;
		}
	}

}
