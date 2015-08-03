package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.OrderDetailJsonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.TimeUtils;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.MutiChooseBtn;

//没有报价的订单详情
public class OrderDetail2Activity extends BaseActivity {
	private TextView mSendLocal, mReceLocal, mSendTime, mReceTime,
			mProductName, mPayWay, mOrderNumber, mTruckModel, mTruckLenth;

	private MutiChooseBtn mZhengche, mFapiao, mHuidan;

	private String order_id;
	private Context mContext;
	private String order_sn;
	private TextView mExpertPrice;

	private LinearLayout mZhanyongLayout;
	private TextView mZhangyongline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail2);
		mContext = this;
		ShowBackView();
		setTitle("询价详情");

		order_id = getIntent().getStringExtra("order_id");
		order_sn = getIntent().getStringExtra("order_sn");

		mOrderNumber = (TextView) findViewById(R.id.order_number);
		mZhengche = (MutiChooseBtn) findViewById(R.id.zhengche_or_pinche);
		mZhengche.init(new String[] { "整车", "拼车" });
		mZhengche.clearClickListener();

		mFapiao = (MutiChooseBtn) findViewById(R.id.kaifapiao);
		mFapiao.init(new String[] { "开发票", "不开发票" });
		mFapiao.clearClickListener();

		mHuidan = (MutiChooseBtn) findViewById(R.id.huidan);
		mHuidan.init(new String[] { "不需要回单", "需要回单" });
		mHuidan.clearClickListener();

		mSendLocal = (TextView) findViewById(R.id.start_place);
		mReceLocal = (TextView) findViewById(R.id.end_place);
		mSendTime = (TextView) findViewById(R.id.send_time);
		mReceTime = (TextView) findViewById(R.id.rece_time);
		mPayWay = (TextView) findViewById(R.id.pay_way);
		mProductName = (TextView) findViewById(R.id.product_name);

		mTruckModel = (TextView) findViewById(R.id.truck_type);
		mTruckLenth = (TextView) findViewById(R.id.truck_length);

		mExpertPrice = (TextView) findViewById(R.id.expert_price);

		mZhanyongLayout = (LinearLayout) findViewById(R.id.zhanyong_layout);
		mZhangyongline = (TextView) findViewById(R.id.zhanyong_line);

		getOrderDetail();
	}

	private void initData() {
		mOrderNumber.setText(order_sn);
		mSendLocal.setText(parser.entity.data.sender.start_place);
		mReceLocal.setText(parser.entity.data.receiver.end_place);

		String producename = parser.entity.data.goods.goods_name + ","
				+ parser.entity.data.goods.goods_weight + "吨";
		if (parser.entity.data.goods.goods_cubage.equals("")) {
			mProductName.setText(producename);
		} else {
			mProductName.setText(parser.entity.data.goods.goods_name + ","
					+ parser.entity.data.goods.goods_weight + "吨" + ","
					+ parser.entity.data.goods.goods_cubage + "方");
		}

		mSendTime.setText(TimeUtils
				.FormatSnapTime(parser.entity.data.goods.goods_loadtime)
				+ "  "
				+ parser.entity.data.goods.goods_loadtime_desc);
		if (!parser.entity.data.goods.goods_unloadtime.equals("")) {
			mReceTime.setText(TimeUtils
					.FormatSnapTime(parser.entity.data.goods.goods_unloadtime)
					+ "  " + parser.entity.data.goods.goods_unloadtime_desc);
		} else {
			mReceTime.setText("无");
		}
		mTruckModel.setText(parser.entity.data.goods.car_model);
		if (!parser.entity.data.goods.car_length.equals("无")) {
			mTruckLenth.setText(parser.entity.data.goods.car_length + "米");
		} else {
			mTruckLenth.setText("无");
		}
		mPayWay.setText(parser.entity.data.goods.pay_type.text);

		if (parser.entity.data.goods.expect_price.equals("0")
				|| parser.entity.data.goods.expect_price.equals("")) {
			mExpertPrice.setText("无");
		} else {
			mExpertPrice.setText(parser.entity.data.goods.expect_price + "元");
		}

		if (parser.entity.data.goods.receipt.equals("需要")) {
			mFapiao.setChooseStatus(true, false);
		} else {
			mFapiao.setChooseStatus(false, true);
		}

		if (parser.entity.data.goods.need_back.equals("1")) {
			mHuidan.setChooseStatus(false, true);
		} else {
			mHuidan.setChooseStatus(true, false);
		}

		if (Integer.valueOf(parser.entity.data.goods.car_type).equals(
				Constant.PIN_CHE)) {
			mZhengche.setChooseStatus(false, true);
//			if (parser.entity.data.goods.occupy_length.equals("0")) {
//				mZhanyongLayout.setVisibility(android.view.View.GONE);
//				mZhangyongline.setVisibility(android.view.View.GONE);
//			} else {
				mZhanyongLayout.setVisibility(android.view.View.VISIBLE);
				((TextView) mZhanyongLayout.findViewById(R.id.zhanyong_length))
						.setText(parser.entity.data.goods.occupy_length+"米");
				mZhangyongline.setVisibility(android.view.View.VISIBLE);
//			}
		} else {
			mZhengche.setChooseStatus(true, false);
			mZhanyongLayout.setVisibility(android.view.View.GONE);
			mZhangyongline.setVisibility(android.view.View.GONE);
		}
	}

	private OrderDetailJsonParser parser;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (parser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i("aa", "result:" + Tools.UnicodeDecode(result));
					// if (parser.entity.data.location.latest != null) {
					// mLatitude = Long
					// .valueOf(parser.entity.data.location.latest.lat);
					// mLongitude = Long
					// .valueOf(parser.entity.data.location.latest.lng);
					// }
					initData();
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

	private void getOrderDetail() {
		showProgressDialog();
		parser = new OrderDetailJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("order_id", "" + order_id);
		MLHttpConnect.GetOrderDetail(this, parmas, parser, mHandler);
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub

	}

}
