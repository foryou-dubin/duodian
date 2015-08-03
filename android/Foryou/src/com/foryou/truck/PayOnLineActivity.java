package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.OrderPayJsonParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ToastUtils;

public class PayOnLineActivity extends BaseActivity {

	String order_id, order_price, coupon_value, order_sn;
	Button mPayBtn;
	TextView mOrignalPrice, mDiscountPrice, mTotalPrice;
	TextView mDanhao, mTimeNotice;
	boolean resultDisp = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pay_online);
		ShowBackView();
		setTitle("支付运费");

		mContext = this;
		order_id = getIntent().getStringExtra("order_id");

		if (!Constant.isNumeric(coupon_value)) {
			coupon_value = "0";
		}

		mPayBtn = (Button) findViewById(R.id.pay_btn);
		mPayBtn.setOnClickListener(this);

		mDanhao = (TextView) findViewById(R.id.danhao);
		mOrignalPrice = (TextView) findViewById(R.id.orignal_price);
		mDiscountPrice = (TextView) findViewById(R.id.discount_price);
		mTotalPrice = (TextView) findViewById(R.id.total_price);
		mTimeNotice = (TextView) findViewById(R.id.time_notice);

		getOrderPay();
	}

	private void getOrderPay() {
		if (isTaskRunning) {
			Log.i("aa", "getComentListTask task is not finish ....");
			return;
		} else {
			new getOrderPayTask().execute();
		}
	}

	private OrderPayJsonParser mParser;
	private boolean isTaskRunning = false;
	private Context mContext;

	public class getOrderPayTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mParser = new OrderPayJsonParser();
			parmas = new HashMap<String, String>();
			parmas.put("order_id", "" + order_id);
			parmas.put("pay_way", "" + 1);
			isTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();

			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mParser.entity.status.equals("Y")) {
					mDanhao.setText(mParser.entity.data.order_sn);
					mOrignalPrice
							.setText("¥" + mParser.entity.data.order_price);
					mDiscountPrice.setText("¥"
							+ mParser.entity.data.reduce_price);
					mTotalPrice.setText("¥" + mParser.entity.data.total_price);
					mTimeNotice.setText(mParser.entity.data.notify_date);

					if (resultDisp) {
						AlertDialog.Builder adb = new AlertDialog.Builder(
								mContext, AlertDialog.THEME_HOLO_LIGHT);
						adb.setTitle("支付运费");
						adb.setMessage("是否支付成功");
						adb.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Intent intent = new Intent(mContext,
												OrderDetailActivity.class);
										intent.putExtra("order_id", order_id);
										startActivity(intent);
									}
								});
						adb.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}

								});
						adb.show();
					}
				} else {
					ToastUtils.toast(mContext, mParser.entity.msg);
					//if (resultDisp) {
						finish();
					//}
				}
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			isTaskRunning = false;
			resultDisp = false;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.GetOrderPay(mContext, parmas, mParser);
			return msg.what;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			resultDisp = true;
			getOrderPay();
		}
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.pay_btn:
			Intent intent = new Intent(mContext, PayOnLineWebActivity.class);
			intent.putExtra("payurl", mParser.entity.data.payurl);
			this.startActivityForResult(intent, 100);
			// startActivity(intent);
			break;
		}
	}

}
