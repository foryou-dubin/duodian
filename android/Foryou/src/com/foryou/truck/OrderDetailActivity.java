package com.foryou.truck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.ui.BindView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.net.HttpApi;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.AgentCommentListJsonParser;
import com.foryou.truck.parser.BdrenderReverseParser;
import com.foryou.truck.parser.OrderDetailJsonParser;
import com.foryou.truck.parser.ReOrderJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.tools.ImageTools;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.TimeUtils;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.MutiChooseBtn;
import com.foryou.truck.view.SpinnerPopUpListView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class OrderDetailActivity extends BaseActivity {
	// private double mLatitude, mLongitude;
	private Context mContext;

	private Button mDetailPlace;
	RelativeLayout mMapLayout;
	private Button mConfirmBtn;
	LinearLayout mDriverInfoLayout, mNotArrayDriverLayout;

	RelativeLayout mAgentPhoneLayout, mDriverPhoneLayout;

	@BindView(id = R.id.bmapView)
	private ImageView mMapView;
	@BindView(id = R.id.right, click = true)
	private Button mRightBtn;

	@BindView(id = R.id.title_layout)
	RelativeLayout mTopView;

	@BindView(id = R.id.zhengche_or_pinche)
	private MutiChooseBtn mZhengche;
	@BindView(id = R.id.kaifapiao)
	private MutiChooseBtn mFapiao;
	@BindView(id = R.id.huidan)
	private MutiChooseBtn mHuidan;

	@BindView(id = R.id.driver_name)
	private TextView mDriverName;
	@BindView(id = R.id.driver_phone)
	private TextView mDriverPhone;
	@BindView(id = R.id.jijiren_name)
	private TextView mJinjiName;
	@BindView(id = R.id.jijiren_phone)
	private TextView mJinjiPhone;
	@BindView(id = R.id.send_name)
	private TextView mSendName;
	@BindView(id = R.id.send_phone)
	private TextView mSendPhone;
	@BindView(id = R.id.send_address)
	private TextView mSendAddr;
	@BindView(id = R.id.rece_name)
	private TextView mReceName;
	@BindView(id = R.id.rece_phone)
	private TextView mRecePhone;
	@BindView(id = R.id.rece_address)
	private TextView mReceAddr;
	@BindView(id = R.id.product_name)
	private TextView mProductName;
	@BindView(id = R.id.send_time)
	private TextView mSendTime;
	@BindView(id = R.id.rece_time)
	private TextView mReceTime;
	@BindView(id = R.id.price)
	private TextView mPrice;
	@BindView(id = R.id.pay_way)
	private TextView mPayWay;
	@BindView(id = R.id.send_local)
	private TextView mSendLocal;
	@BindView(id = R.id.rece_local)
	private TextView mReceLocal;
	@BindView(id = R.id.truck_type)
	private TextView mTruckModel;
	@BindView(id = R.id.truck_length)
	private TextView mTruckLenth;
	@BindView(id = R.id.send_phone2)
	private TextView mSendPhone2;
	@BindView(id = R.id.rece_phone2)
	private TextView mRecePhone2;
	@BindView(id = R.id.driver_plate)
	private TextView mDriverPlate;

	private TextView mMapline;
	private TextView mRefreshLocate;

	private TextView mCuichuArray;
	private String order_id;

	private LinearLayout mMainMapLayout;
	private LinearLayout mCouponLayout;
	private String TAG = "OrderDetailActivity";
	private LinearLayout mZhanyongLayout;
	private TextView mZhangyongline;

	private OrderDetailJsonParser parser;
	public class getOrderDetailTask extends
			AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			parser = new OrderDetailJsonParser();
			parmas = new HashMap<String, String>();
			parmas.put("order_id", "" + order_id);
			isTaskRunning = true;
		}
		
		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.GetOrderDetail2(mContext, parmas, parser);
			return msg.what;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();

			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (parser.entity.status.equals("Y")) {
					InitData();
				} else {
					ToastUtils.toast(mContext, parser.entity.msg);
				}
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			isTaskRunning = false;
		}
	}

	private void getOrderDetail() {
		if(isTaskRunning){
			Log.i(TAG,"getOrderDetail task is running");
			return;
		}else{
			new getOrderDetailTask().execute();
		}
	}

	Handler mGetAddressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			mDetailPlace.setText(mBdParser.entity.result.formatted_address);
		}
	};

	private BdrenderReverseParser mBdParser;

	private void getCurrentAddress() {
		Thread t = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					String mStringresult = HttpApi.getString((Tools
							.getMapUrlWitchLat(Double
									.valueOf(parser.entity.data.location
											.get(parser.entity.data.location
													.size() - 1).lat), Double
									.valueOf(parser.entity.data.location
											.get(parser.entity.data.location
													.size() - 1).lng))));
					// Log.i("aa", "mStringresult:" + mStringresult);
					mBdParser = new BdrenderReverseParser();
					mBdParser.parser(mStringresult.substring(
							mStringresult.indexOf("{"),
							mStringresult.length() - 1));
					Log.i("aa", "mStringresult:"
							+ mBdParser.entity.result.formatted_address);
					mGetAddressHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		t.start();
	}

	private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			// TODO Auto-generated method stub
			Log.i("aa", "load map finish() .........");
			mMapView.setBackgroundDrawable(ImageTools.bitmapToDrawable(arg2));
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			// TODO Auto-generated method stub
		}
	};

	private void InitData() {
		if (parser.entity.data.agent.driver_name.equals("")) {
			mDriverInfoLayout.setVisibility(android.view.View.GONE);
			mNotArrayDriverLayout.setVisibility(android.view.View.VISIBLE);
		} else {
			mDriverInfoLayout.setVisibility(android.view.View.VISIBLE);
			mNotArrayDriverLayout.setVisibility(android.view.View.GONE);

			mDriverName.setText(parser.entity.data.agent.driver_name);
			if (parser.entity.data.agent.bs_locate.equals("0")) {
				mDriverName.setText(mDriverName.getText().toString()
						+ "  (未开启定位)");
				mRefreshLocate.setVisibility(android.view.View.GONE);
			} else {
				mRefreshLocate.setVisibility(android.view.View.VISIBLE);
			}

			mDriverPhone.setText(parser.entity.data.agent.driver_mobile);
			mDriverPlate.setText(parser.entity.data.agent.driver_plate);
		}

		if (mJinjiName == null) {
			Log.i(TAG, "mJinjiName == null");
		}
		mJinjiName.setText(parser.entity.data.agent.agent_name);
		mJinjiPhone.setText(parser.entity.data.agent.agent_mobile);

		mSendName.setText(parser.entity.data.sender.name);
		mSendPhone.setText(parser.entity.data.sender.tel);
		if (parser.entity.data.sender.tel2 != null) {
			if (!parser.entity.data.sender.tel2.equals("")) {
				mSendPhone2.setText(parser.entity.data.sender.tel2);
			} else {
				mSendPhone2.setText("无");
			}
		} else {
			mSendPhone2.setText("无");
		}

		mSendLocal.setText(parser.entity.data.sender.start_place);
		mSendAddr.setText(parser.entity.data.sender.start_address);

		mReceName.setText(parser.entity.data.receiver.name);
		mRecePhone.setText(parser.entity.data.receiver.tel);
		if (parser.entity.data.receiver.tel2 != null) {
			if (!parser.entity.data.receiver.tel2.equals("")) {
				mRecePhone2.setText(parser.entity.data.receiver.tel2);
			} else {
				mRecePhone2.setText("无");
			}
		} else {
			mRecePhone2.setText("无");
		}

		mReceLocal.setText(parser.entity.data.receiver.end_place);
		mReceAddr.setText(parser.entity.data.receiver.end_address);

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
		mPrice.setText("¥" + parser.entity.data.goods.order_price);
		mPayWay.setText(parser.entity.data.goods.pay_type.text);
		mTruckModel.setText(parser.entity.data.goods.car_model);
		if (!parser.entity.data.goods.car_length.equals("无")
				&& !parser.entity.data.goods.car_length.equals("")) {
			mTruckLenth.setText(parser.entity.data.goods.car_length + "米");
		} else {
			mTruckLenth.setText("无");
		}

		CommonConfigEntity entity = SharePerfenceUtil.getConfigData(mContext);
		mConfirmBtn.setText("评价经纪人");
		mConfirmBtn.setTag("0");
		for (int i = 0; i < entity.data.system_switch.size(); i++) {
			String key = entity.data.system_switch.get(i).key;
			String value = entity.data.system_switch.get(i).value;
			if (key.equals("payment_switch")) {
				if (value.equals("1")
						&& Constant.needPayOnLine(mContext,
								parser.entity.data.goods.pay_type.key)
						&& parser.entity.data.goods.pay_online_success
								.equals("0")) {
					mConfirmBtn.setText("支付运费");
					mConfirmBtn.setTag("1");
					break;
				} else {
				}
			}
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
			// if (parser.entity.data.goods.occupy_length.equals("0")) {
			// mZhanyongLayout.setVisibility(android.view.View.GONE);
			// mZhangyongline.setVisibility(android.view.View.GONE);
			// } else {
			mZhanyongLayout.setVisibility(android.view.View.VISIBLE);
			((TextView) mZhanyongLayout.findViewById(R.id.zhanyong_length))
					.setText(parser.entity.data.goods.occupy_length + "米");
			mZhangyongline.setVisibility(android.view.View.VISIBLE);
			// }

		} else {
			mZhengche.setChooseStatus(true, false);
			mZhanyongLayout.setVisibility(android.view.View.GONE);
			mZhangyongline.setVisibility(android.view.View.GONE);
		}

		if (parser.entity.data.location.size() > 0) {
			String url = Tools.getMutiMarkMapImageUrl(
					parser.entity.data.location, 0, 0);
			Log.i(TAG, "map url:" + url);
			imageLoader.loadImage(url, mImageLoadingListener);
			mMainMapLayout.setVisibility(android.view.View.VISIBLE);
			mMapline.setVisibility(android.view.View.VISIBLE);
			getCurrentAddress();
		} else {
			mMainMapLayout.setVisibility(android.view.View.GONE);
			mMapline.setVisibility(android.view.View.GONE);
		}

		if (parser.entity.data.goods.coupon_code == null) {
			mCouponLayout.setVisibility(android.view.View.GONE);
		} else {
			if (parser.entity.data.goods.coupon_code.equals("")) {
				mCouponLayout.setVisibility(android.view.View.GONE);
			} else {
				mCouponLayout.setVisibility(android.view.View.VISIBLE);
				((TextView) mCouponLayout.findViewById(R.id.coupon_code))
						.setText(parser.entity.data.goods.coupon_code);
				((TextView) mCouponLayout.findViewById(R.id.coupon_value))
						.setText(parser.entity.data.goods.coupon_value + "元");
			}
		}
	}

	private void initView() {

		mMainMapLayout = (LinearLayout) findViewById(R.id.map_main_layout);

		mDriverInfoLayout = (LinearLayout) findViewById(R.id.driver_detail_layout);
		mNotArrayDriverLayout = (LinearLayout) findViewById(R.id.not_array_driver_layout);
	}

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.order_detail);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mContext = this;

		// imageLoader.displayImage(Tools.getStaticMapImageUrl(mLatitude,
		// mLongitude, 0, 0), mMapView);

		ShowBackView();
		setTitle("运单详情");

		mRightBtn.setVisibility(android.view.View.VISIBLE);
		mRightBtn.setBackgroundResource(R.drawable.more_icon);

		order_id = getIntent().getStringExtra("order_id");

		mZhengche.init(new String[] { "整车", "拼车" });
		mZhengche.clearClickListener();

		mFapiao.init(new String[] { "开发票", "不开发票" });
		mFapiao.clearClickListener();

		mHuidan.init(new String[] { "不需要回单", "需要回单" });
		mHuidan.clearClickListener();

		mDetailPlace = (Button) findViewById(R.id.detail_place);

		mRefreshLocate = (TextView) findViewById(R.id.refresh_locate);
		mRefreshLocate.setOnClickListener(this);

		mMapline = (TextView) findViewById(R.id.map_line);
		mMapView = (ImageView) findViewById(R.id.bmapView);
		mDetailPlace = (Button) findViewById(R.id.detail_place);

		mMapLayout = (RelativeLayout) findViewById(R.id.map_layout);
		mMapLayout.setOnClickListener(this);

		mAgentPhoneLayout = (RelativeLayout) findViewById(R.id.agent_phone_layout);
		mAgentPhoneLayout.setOnClickListener(this);
		mDriverPhoneLayout = (RelativeLayout) findViewById(R.id.driver_phone_layout);
		mDriverPhoneLayout.setOnClickListener(this);

		mConfirmBtn = (Button) findViewById(R.id.confirm_btn);
		mConfirmBtn.setOnClickListener(this);

		mCouponLayout = (LinearLayout) findViewById(R.id.coupon_layout);

		mCuichuArray = (TextView) findViewById(R.id.cuichu_array);
		mCuichuArray.setOnClickListener(this);

		mZhanyongLayout = (LinearLayout) findViewById(R.id.zhanyong_layout);
		mZhangyongline = (TextView) findViewById(R.id.zhanyong_line);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initView();
		getOrderDetail();
	}

	private SpinnerPopUpListView mPopList;

	private void getReOrder() {
		if (isTaskRunning) {
			Log.i("aa", "getComentListTask task is not finish ....");
			return;
		} else {
			new getReOrderTask().execute();
		}
	}

	private ReOrderJsonParser mReOrderParser;
	private boolean isTaskRunning = false;

	public class getReOrderTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mReOrderParser = new ReOrderJsonParser();
			parmas = new HashMap<String, String>();
			parmas.put("order_id", "" + order_id);
			isTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();

			// public String sender_regions_id; //省市区,逗号分割(如 3,2,1)
			// public String sender_regions_text;
			// public String receiver_regions_id;
			// public String receiver_regions_text;
			// public String goods_loadtime;
			// public String goods_loadtime_desc;
			// public String goods_unloadtime;
			// public String goods_unloadtime_desc;
			// public String goods_name;
			// public String goods_cubage;
			// public String goods_weight;
			// public String car_model;
			// public String car_model_text;
			// public String car_length;
			// public String car_length_text;
			// public String pay_type;
			// public String pay_type_text;
			// public String receipt; //是否开票
			// public String car_type; //运输类型
			// public String occupy_length;
			// public String remark;
			// public String expect_price;
			// public String need_back;
			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mReOrderParser.entity.status.equals("Y")) {
					Intent intent = new Intent(mContext, TabActivity.class);
					TabActivity.mCurrentTabIndex = 1;
					FirstTabInitValue value = new FirstTabInitValue();
					value.sender_regions_id = mReOrderParser.entity.data.hd_start_place;
					value.sender_regions_text = parser.entity.data.sender.start_place;
					value.receiver_regions_id = mReOrderParser.entity.data.hd_end_place;
					value.receiver_regions_text = parser.entity.data.receiver.end_place;
					value.goods_loadtime = mReOrderParser.entity.data.goods_loadtime;
					value.goods_loadtime_desc = mReOrderParser.entity.data.goods_loadtime_desc;
					value.goods_unloadtime = mReOrderParser.entity.data.goods_unloadtime;
					value.goods_unloadtime_desc = mReOrderParser.entity.data.goods_unloadtime_desc;
					value.goods_name = mReOrderParser.entity.data.goods_name;
					value.goods_cubage = mReOrderParser.entity.data.goods_cubage;
					value.goods_weight = mReOrderParser.entity.data.goods_weight;

					value.car_model = mReOrderParser.entity.data.car_model;
					value.car_model_text = parser.entity.data.goods.car_model;
					value.car_length = mReOrderParser.entity.data.car_length;
					value.car_length_text = parser.entity.data.goods.car_length;
					value.pay_type = mReOrderParser.entity.data.pay_type;
					value.pay_type_text = parser.entity.data.goods.pay_type.text;

					value.receipt = parser.entity.data.goods.receipt;
					value.car_type = parser.entity.data.goods.car_type;
					value.occupy_length = mReOrderParser.entity.data.occupy_length;
					value.remark = mReOrderParser.entity.data.remark;
					value.expect_price = mReOrderParser.entity.data.expect_price;
					value.need_back = mReOrderParser.entity.data.need_back;

					Log.i(TAG, "value:" + value);
					intent.putExtra("firstTabValue", value);
					startActivity(intent);
					overridePendingTransition(R.anim.fade, R.anim.hold);
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
			msg = MLHttpConnect.GetReOrder(mContext, parmas, mReOrderParser);
			return msg.what;
		}

	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (id) {
		case R.id.right:
			if (parser.entity != null) {
				final ArrayList<String> datalist = new ArrayList<String>();
				datalist.add("支付运费");
				datalist.add("查看三方协议");
				datalist.add("评价经纪人");
				datalist.add("查看保险单");
				datalist.add("查看报价列表");
				datalist.add("再下一单");

				if (parser.entity.data.insurance.equals("0")) {
					datalist.remove("查看保险单");
				}

				if (!mConfirmBtn.getText().toString().equals("支付运费")) {
					datalist.remove("支付运费");
				}

				if (mPopList == null) {
					mPopList = new SpinnerPopUpListView(mContext, datalist,
							mTopView);
					mPopList.setSpClickListener(new SpinnerPopUpListView.SpOnClickListener() {
						@Override
						public void onItemsClick(int pos) {
							// TODO Auto-generated method stub
							Intent intent;
							if (datalist.get(pos).equals("支付运费")) {
								finish();
								intent = new Intent(mContext,
										PayOnLineActivity.class);
								intent.putExtra("order_id", order_id);
								startActivity(intent);
							} else if (datalist.get(pos).equals("查看三方协议")) {
								if (SharePerfenceUtil.getName(mContext).equals(
										"")) {
									alertDialog(
											"",
											"用户姓名为空,请填写用户姓名后查看",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method
													// stub
													Intent i = new Intent(
															mContext,
															ModifyMyInfoActivity.class);
													startActivity(i);
												}

											});
								} else {
									intent = new Intent(mContext,
											XieyiContentActivity.class);
									intent.putExtra("order_id", order_id);
									startActivity(intent);
								}
							} else if (datalist.get(pos).equals("评价经纪人")) {
								intent = new Intent(mContext,
										PingjiaAgentActivity.class);
								if (mNotArrayDriverLayout.getVisibility() == android.view.View.VISIBLE) {
									intent.putExtra("driver_array", false);
								} else {
									intent.putExtra("driver_array", true);
								}
								intent.putExtra("order_id", order_id);
								startActivity(intent);
							} else if (datalist.get(pos).equals("查看报价列表")) {
								intent = new Intent(mContext,
										AgentListActivity2.class);
								intent.putExtra("order_id", order_id);
								startActivity(intent);
							} else if (datalist.get(pos).equals("再下一单")) {
								getReOrder();
							} else if (datalist.get(pos).equals("查看保险单")) {
								if (parser.entity.data.insurance_image
										.equals("")) {
									ToastUtils.toast(mContext,
											"系统暂未上传保险单，请稍后再试");
								} else {
									intent = new Intent(mContext,
											DisplayImageActivity.class);

									intent.putExtra("image",
											parser.entity.data.insurance_image);
									startActivity(intent);
								}
							}

						}
					});
				}
				mPopList.Show();
			}

			break;
		case R.id.map_layout:
			intent = new Intent(this, FullScreenMapActivity.class);
			String[] lngArray = new String[parser.entity.data.location.size()];
			String[] latArray = new String[parser.entity.data.location.size()];
			for (int i = 0; i < parser.entity.data.location.size(); i++) {
				lngArray[i] = parser.entity.data.location.get(i).lng;
				latArray[i] = parser.entity.data.location.get(i).lat;
			}
			intent.putExtra("lngArray", lngArray);
			intent.putExtra("latArray", latArray);
			startActivity(intent);
			break;
		/*
		 * case R.id.query_list: intent = new Intent(this,
		 * AgentListActivity2.class); intent.putExtra("order_id", order_id);
		 * startActivity(intent); break; case R.id.xieyi_layout: intent = new
		 * Intent(this, XieyiContentActivity.class); intent.putExtra("order_id",
		 * order_id); startActivity(intent); break;
		 */
		case R.id.agent_phone_layout:
			Constant.GotoDialPage(mContext, mJinjiPhone.getText().toString());
			break;
		case R.id.driver_phone_layout:
			Constant.GotoDialPage(mContext, mDriverPhone.getText().toString());
			break;
		case R.id.confirm_btn:
			if (mConfirmBtn.getTag().equals("1")) {
				finish();
				intent = new Intent(mContext, PayOnLineActivity.class);
				intent.putExtra("order_id", order_id);
				startActivity(intent);
			} else {
				// 评价经纪人
				intent = new Intent(mContext, PingjiaAgentActivity.class);
				intent.putExtra("order_id", order_id);
				if (mNotArrayDriverLayout.getVisibility() == android.view.View.VISIBLE) {
					intent.putExtra("driver_array", false);
				} else {
					intent.putExtra("driver_array", true);
				}
				startActivity(intent);
			}
			break;
		case R.id.cuichu_array:
			CuiChuArrayDriver();
			break;
		case R.id.refresh_locate:
			RefreshLocation();
			break;
		}
	}

	private void CuiChuArrayDriver() {
		if (isCuichuTaskRunning) {
			Log.i("aa", "CuiChuArrayDriver task is not finish ....");
			return;
		} else {
			new CuichuArrayTask().execute();
		}
	}

	private SimpleJasonParser mCuichuParser;
	private boolean isCuichuTaskRunning = false;

	public class CuichuArrayTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mCuichuParser = new SimpleJasonParser();
			parmas = new HashMap<String, String>();
			parmas.put("order_id", "" + order_id);
			isCuichuTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();

			switch (result) {
			case MLHttpConnect2.SUCCESS:
				// if (mCuichuParser.entity.status.equals("Y")) {
				ToastUtils.toast(mContext, mCuichuParser.entity.msg);
				// }
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			isCuichuTaskRunning = false;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.CuiChuArrayDriver(mContext, parmas,
					mCuichuParser);
			return msg.what;
		}

	}

	private SimpleJasonParser mRefreshLocParser;
	private boolean isRefreshLocTaskRunning = false;

	private void RefreshLocation() {
		if (isRefreshLocTaskRunning) {
			Log.i("aa", "RefreshLocation task is not finish ....");
			return;
		} else {
			new RefreshLocTask().execute();
		}
	}

	public class RefreshLocTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mRefreshLocParser = new SimpleJasonParser();
			parmas = new HashMap<String, String>();
			parmas.put("order_id", "" + order_id);
			isRefreshLocTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();

			switch (result) {
			case MLHttpConnect2.SUCCESS:
				// if (mCuichuParser.entity.status.equals("Y")) {
				// alertDialog("更新当前位置", mRefreshLocParser.entity.msg, false);
				AlertDialog.Builder adb = new AlertDialog.Builder(mContext,
						AlertDialog.THEME_HOLO_LIGHT);
				adb.setTitle("更新当前位置");
				adb.setMessage(mRefreshLocParser.entity.msg);
				adb.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								getOrderDetail();
							}
						});
				adb.show();
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			isRefreshLocTaskRunning = false;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.RefreshLoc(mContext, parmas, mRefreshLocParser);
			return msg.what;
		}

	}
}
