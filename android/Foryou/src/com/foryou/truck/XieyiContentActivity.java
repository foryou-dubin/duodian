package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.ui.BindView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.AgreeMentJsonParser;
import com.foryou.truck.util.ToastUtils;

public class XieyiContentActivity extends BaseActivity {

	private Context mContext;
	private String TAG = "XieyiContentActivity";
	private String order_id = "";

	private TextView mCustomerName, mDriverName, mDriverIdCard, mDriverPlate,
			mDriverMobile, mData1, mData2, mData3,mJiaFang,mYiFang;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.xieyi_content);
		mContext = this;
		order_id = getIntent().getStringExtra("order_id");

		mCustomerName = (TextView) findViewById(R.id.customerName);
		mDriverName = (TextView) findViewById(R.id.driverName);
		mDriverIdCard = (TextView) findViewById(R.id.driverIdCard);
		mDriverPlate = (TextView) findViewById(R.id.driverPlate);
		mDriverMobile = (TextView) findViewById(R.id.driverMobile);
		mData1 = (TextView) findViewById(R.id.data1);
		mData2 = (TextView) findViewById(R.id.data2);
		mData3 = (TextView) findViewById(R.id.data3);
		
		mJiaFang = (TextView)findViewById(R.id.jiafang);
		mYiFang = (TextView)findViewById(R.id.yifang);

		ShowBackView();
		setTitle("三方协议");
	}

	private void InitData() {
		mCustomerName.setText(mAgreeMentParser.entity.data.customerName);
		mDriverName.setText(mAgreeMentParser.entity.data.driverName);
		mDriverIdCard.setText(mAgreeMentParser.entity.data.driverIdCard);
		mDriverPlate.setText(mAgreeMentParser.entity.data.driverPlate);
		mDriverMobile.setText(mAgreeMentParser.entity.data.driverMobile);
		mData1.setText(mAgreeMentParser.entity.data.confirmTime);
		mData2.setText(mAgreeMentParser.entity.data.arrangeTime);
		mData3.setText(mAgreeMentParser.entity.data.confirmTime);
		mJiaFang.setText(mAgreeMentParser.entity.data.customerName);
		mYiFang.setText(mAgreeMentParser.entity.data.driverName);
	}

	public AgreeMentJsonParser mAgreeMentParser;
	private boolean isTaskRunning = false;

	public class getAgreeMentTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mAgreeMentParser = new AgreeMentJsonParser();
			parmas = new HashMap<String, String>();
			parmas.put("order_id", order_id);
			isTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();
			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mAgreeMentParser.entity.status.equals("Y")) {
					InitData();
				} else {
					ToastUtils.toast(mContext, mAgreeMentParser.entity.msg);
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
			msg = MLHttpConnect
					.GetAgreeMent(mContext, parmas, mAgreeMentParser);

			Log.i(TAG, "" + msg.obj);
			return msg.what;
		}

	}

	private void getAgreeMent() {
		if (isTaskRunning) {
			return;
		}
		new getAgreeMentTask().execute();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getAgreeMent();
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub

	}
}
