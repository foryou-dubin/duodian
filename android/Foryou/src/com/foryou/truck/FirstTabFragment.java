package com.foryou.truck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kymjs.aframe.ui.fragment.BaseFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.OrderDetailActivity.RefreshLocTask;
import com.foryou.truck.entity.CommonConfigEntity.BaseKeyValue;
import com.foryou.truck.model.CityModel;
import com.foryou.truck.model.DistrictModel;
import com.foryou.truck.model.ProvinceModel;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.net.MLHttpConstant;
import com.foryou.truck.parser.AreasJsonParser;
import com.foryou.truck.parser.CommonConfigJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.parser.UserDetailJsonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.AreasPickDialog;
import com.foryou.truck.view.AreasPickDialog.AreasDataPickLisener;
import com.foryou.truck.view.DataAndTimePickDialog;
import com.foryou.truck.view.MutiChooseBtn;

public class FirstTabFragment extends BaseFragment {

	private TextView mTitle;
	private DataAndTimePickDialog mDataSendDialog = null,
			mDataReceDialog = null;

	private String TAG = "FirstTabFragment";
	private Button mConfirmQuery;
	private AlertDialog mTruckDialog = null, mTruckLenDialog = null;

	protected String[] mProvinceDatas;
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
	protected String mCurrentStartProviceName;
	protected String mCurrentStartCityName;
	protected String mCurrentStartDistrictName = "";
	protected String mCurrentZipCode = "";

	private int mStartProvinceIndex = 0, mStartCityIndex = 0,
			mStartDistrictIndex = 0;
	private int mEndProvinceIndex = 0, mEndCityIndex = 0,
			mEndDistrictIndex = 0;
	private TabActivity mContext;

	private AreasJsonParser parser;

	private ArrayList<ProvinceModel> provinceList;
	private String mCache;

	private GridView mTruckGridView, mTruckLenGridView;
	private LinearLayout mBaseContent;
	private LayoutInflater mInflater;
	private View mFirstPage = null, mSecondPage = null, mThirdPage = null;
	private int mCurrentPage = 1;
	private RelativeLayout mBackView;

	// firstPage
	private TextView mEndPlace, mStartPlace;
	private RelativeLayout mPinChelayout, mZhengcheLayout;
	private TextView mChooseSendTime, mChooseReceiverTime;
	// private ImageView mPincheImg, mZhengcheImg;
	private TextView mPincheTx, mZhengcheTx;

	// second page
	private Button mSecondNextStep;
	private TextView mChooseTruck;
	private TextView mChooseLength;
	private EditText mProductWeight, mProductName, mProductVolume;
	private TextView mlengthX, mTypeX;
	private RelativeLayout mUseTruckLenLayout;
	private TextView mUseTruckLenLine;
	private EditText mWriteUseTruckLen;

	// third page
	private Button mThirdBtn;
	private UserDetailJsonParser mUserDetailParser;
	private MutiChooseBtn mFapiao, mHuidan;
	private EditText mBeizu, mExpertPrice;
	private TextView mPayType;

	private View mRootView = null;

	private String[] mPayTypeKeyItem, mPayTypeValueItem;
	// private String[] mPayTypeItem1 = {"收货人付款", "合同客户结算"}; // 合同客户
	// private String[] mPayTypeItem2 = { "发货人付", "收货人付款"}; // 非合同客户

	FirstTabInitValue mInitValue;

	// receipt 0:不开票 1:开票
	// order_type 1:为普通客户新增，2:为合同客户新增 3:经纪人新增
	// car_type 1:整车 2:拼车
	// 1:收货人付款 2:现金支付（发货人付） 3:月结 4:货到打款司机

	public void initValue(FirstTabInitValue value) {
		mStartPlace.setText(value.sender_regions_text);
		mEndPlace.setText(value.receiver_regions_text);

		value.goods_loadtime = "";
		value.goods_loadtime_desc = "";
		value.goods_unloadtime = "";
		value.goods_unloadtime_desc = "";
		
		mChooseSendTime.setText("");
		mChooseReceiverTime.setText("");

		if (value.car_type.equals("1")) {
			updateTruckWayImage(1);
		} else {
			updateTruckWayImage(2);
		}
		mChooseTruck.setText(value.car_model_text);
		mChooseLength.setText(value.car_length_text);
		mProductWeight.setText(value.goods_weight);
		mProductName.setText(value.goods_name);
		mProductVolume.setText(value.goods_cubage);
		if(value.occupy_length.equals("0")){
			mWriteUseTruckLen.setText("");
		}else{
			mWriteUseTruckLen.setText(value.occupy_length);
		}
		mExpertPrice.setText(value.expect_price);
		mPayType.setText(value.pay_type_text);

		if (value.receipt.equals("1")) {
			mFapiao.setChooseStatus(false, true);
		} else {
			mFapiao.setChooseStatus(true, false);
		}

		if (value.need_back.equals("1")) {
			mHuidan.setChooseStatus(false, true);
		} else {
			mHuidan.setChooseStatus(true, false);
		}
		mBeizu.setText(value.remark);

		mInitValue = value;

		Log.i(TAG, "mInitValue:" + mInitValue.receiver_regions_id
				+ ",goods_unloadtime:" + mInitValue.goods_unloadtime);

		mBaseContent.removeAllViews();
		mBaseContent.addView(mFirstPage);
		mCurrentPage = 1;
		mBackView.setVisibility(android.view.View.GONE);
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// mContext.cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				String result = (String) msg.obj;
				initProvinceDatas();
				// InitDataList();
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}
	};

	private void getAreaData() {
		// mContext.showProgressDialog();
		parser = new AreasJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		MLHttpConnect.getAreasData(mContext, parmas, parser, mHandler);
	}

	public void onBackPressed() {
		if (mCurrentPage == 2) {
			mBaseContent.removeAllViews();
			mBaseContent.addView(mFirstPage);
			mCurrentPage = 1;
			mBackView.setVisibility(android.view.View.GONE);
		} else if (mCurrentPage == 3) {
			mBaseContent.removeAllViews();
			mBaseContent.addView(mSecondPage);
			mCurrentPage = 2;
		} else {
			mContext.superOnBackPress();
		}
	}

	protected void initProvinceDatas() {
		provinceList = new ArrayList<ProvinceModel>();
		// AssetManager asset = getAssets();
		// try {
		// InputStream input = asset.open("province_data.xml");
		// SAXParserFactory spf = SAXParserFactory.newInstance();
		// SAXParser parser = spf.newSAXParser();
		// XmlParserHandler handler = new XmlParserHandler();
		// parser.parse(input, handler);
		// input.close();
		// provinceList = handler.getDataList();
		//
		// } catch (Exception e) {
		//
		// }
		for (int i = 0; i < parser.entity.data.size(); i++) {
			ProvinceModel provice = new ProvinceModel();
			provice.setName(parser.entity.data.get(i).name);
			provice.setId(parser.entity.data.get(i).id);
			ArrayList<CityModel> citylist = new ArrayList<CityModel>();
			for (int j = 0; j < parser.entity.data.get(i).city.size(); j++) {
				CityModel city = new CityModel();
				city.setName(parser.entity.data.get(i).city.get(j).name);
				city.setId(parser.entity.data.get(i).city.get(j).id);
				ArrayList<DistrictModel> districtlist = new ArrayList<DistrictModel>();
				for (int k = 0; k < parser.entity.data.get(i).city.get(j).district
						.size(); k++) {
					DistrictModel district = new DistrictModel();
					district.setName(parser.entity.data.get(i).city.get(j).district
							.get(k).name);
					district.setId(parser.entity.data.get(i).city.get(j).district
							.get(k).id);
					districtlist.add(district);
				}
				if (districtlist.size() == 0) {
					DistrictModel district = new DistrictModel();
					district.setName(city.getName());
					district.setId(city.getId());
					Log.i(TAG, "id:" + city.getId() + ",name:" + city.getName());
					districtlist.add(district);
				}
				city.setDistrictList(districtlist);
				citylist.add(city);
			}
			provice.setCityList(citylist);
			provinceList.add(provice);
		}
	}

	private DataAndTimePickDialog.onDataAndTimeSetListener Datelistener = new DataAndTimePickDialog.onDataAndTimeSetListener() {
		@Override
		public void onDataSet(DialogInterface dialog, int dayindex,
				int time1index, int time2index) {
			String dataString;
			// TODO Auto-generated method stub
			if (dialog == mDataSendDialog) {
				dataString = mDataSendDialog.getDataString(dayindex);
				mInitValue.goods_loadtime = dataString;
				mInitValue.goods_loadtime_desc = mDataSendDialog
						.getTime1String(time1index)
						+ "-"
						+ mDataSendDialog.getTime2String(time2index);
				mChooseSendTime.setText(mInitValue.goods_loadtime.subSequence(
						5, dataString.length())
						+ "  "
						+ mInitValue.goods_loadtime_desc);
			} else if (dialog == mDataReceDialog) {
				dataString = mDataReceDialog.getDataString(dayindex);
				mInitValue.goods_unloadtime = dataString;
				if (time1index == 0) {
					mChooseReceiverTime.setText(dataString);
					mInitValue.goods_unloadtime_desc = "";
				} else {
					mInitValue.goods_unloadtime_desc = mDataReceDialog
							.getTime1String(time1index)
							+ "-"
							+ mDataReceDialog.getTime2String(time2index);
					mChooseReceiverTime.setText(dataString.substring(5,
							dataString.length())
							+ "  "
							+ mInitValue.goods_unloadtime_desc);
				}
			}
		}
	};

	private OnItemClickListener mGridItemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			GridView mGridView = (GridView) parent;
			if (mGridView == mTruckLenGridView) {
				mChooseLength.setText(mConfigParser.entity.data.car_length
						.get(position).value + "米");
				mInitValue.car_length = mConfigParser.entity.data.car_length
						.get(position).key;
				mTruckLenDialog.dismiss();
			} else if (mGridView == mTruckGridView) {
				mChooseTruck.setText(mConfigParser.entity.data.car_model
						.get(position).value);
				mInitValue.car_model = mConfigParser.entity.data.car_model
						.get(position).key;
				mTruckDialog.dismiss();
			}
		}
	};

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private List<BaseKeyValue> mArrayData;

		public ImageAdapter(Context context, List<BaseKeyValue> data) {
			mContext = context;
			mArrayData = data;
		}

		public int getCount() {
			return mArrayData.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView img;
			if (convertView == null) {
				TextView mText = new TextView(mContext);
				if (mArrayData == mConfigParser.entity.data.car_length) {
					mText.setText(mArrayData.get(position).value + "米");
				} else {
					mText.setText(mArrayData.get(position).value);
				}
				mText.setTextSize(16);
				mText.setGravity(Gravity.CENTER_HORIZONTAL);
				convertView = mText;
			} else {
				img = (TextView) convertView;
			}
			return convertView;
		}
	}

	private AnimationListener mAnimationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if (mCurrentPage == 1) {
				mBaseContent.removeAllViews();
				mBaseContent.addView(mSecondPage);
				mCurrentPage = 2;
				mBackView.setVisibility(android.view.View.VISIBLE);

				if (mZhengcheTx.getTag().equals(1)) {
					mUseTruckLenLayout.setVisibility(android.view.View.GONE);
					mUseTruckLenLine.setVisibility(android.view.View.GONE);
				} else {
					mUseTruckLenLayout.setVisibility(android.view.View.VISIBLE);
					mUseTruckLenLine.setVisibility(android.view.View.VISIBLE);
				}
			} else if (mCurrentPage == 2) {
				mBaseContent.removeAllViews();
				mBaseContent.addView(mThirdPage);
				mCurrentPage = 3;
				mBackView.setVisibility(android.view.View.VISIBLE);
			} else if (mCurrentPage == 3) {
				mBaseContent.removeAllViews();
				mBaseContent.addView(mFirstPage);
				mCurrentPage = 1;
				mBackView.setVisibility(android.view.View.GONE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}
	};

	private AnimationSet getAnimationSet() {
		TranslateAnimation translateAnimation = new TranslateAnimation(0.1f,
				1000.0f, 0.1f, 200.0f);
		// 设置动画时间
		translateAnimation.setDuration(500);

		AlphaAnimation mAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		mAlphaAnimation.setDuration(200);
		AnimationSet mAnimationSet = new AnimationSet(false);
		mAnimationSet.addAnimation(translateAnimation);
		mAnimationSet.addAnimation(mAlphaAnimation);
		mAnimationSet.setAnimationListener(mAnimationListener);
		return mAnimationSet;
	}

	@Override
	protected void widgetClick(View v) {
		// TODO Auto-generated method stub
		AlertDialog.Builder adb;
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.choose_send_time:
			Calendar cal = Calendar.getInstance();
			if (isConfigTaskRunning) {
				ToastUtils.toast(mContext, "正在获取用户配置,请稍后再试");
				return;
			}

			if (mDataSendDialog == null) {
				mDataSendDialog = new DataAndTimePickDialog(mContext,
						DatePickerDialog.THEME_HOLO_LIGHT, Datelistener,
						mConfigParser, false);
			}
			mDataSendDialog.show();
			break;
		case R.id.choose_receiver_time:
			if (isConfigTaskRunning) {
				ToastUtils.toast(mContext, "正在获取用户配置,请稍后再试");
				return;
			}
			if (mDataReceDialog == null) {
				mDataReceDialog = new DataAndTimePickDialog(mContext,
						DatePickerDialog.THEME_HOLO_LIGHT, Datelistener,
						mConfigParser, true);
			}
			mDataReceDialog.show();
			break;
		case R.id.confirm_query:
			// Intent intent = new Intent(mContext, MyQueryPriceList.class);
			// startActivity(intent);

			if (!SharePerfenceUtil.IsLogin(mContext)) {
				Intent intent = new Intent(mContext, EntryLoginActivity.class);
				startActivity(intent);
			} else {
				if (mStartPlace.getText().equals("")) {
					ToastUtils.toast(mContext, "请选择始发地");
					return;
				}

				if (mEndPlace.getText().equals("")) {
					ToastUtils.toast(mContext, "请选择目的地");
					return;
				}

				if (mStartPlace.getText()
						.equals(mEndPlace.getText().toString())) {
					ToastUtils.toast(mContext, "始发地和目的地不能相同");
					return;
				}

				if (mChooseSendTime.getText().equals("")) {
					ToastUtils.toast(mContext, "请选择发货日期");
					return;
				}
				// if (mChooseReceiverTime.getText().equals("")) {
				// ToastUtils.toast(mContext, "请选择收货日期");
				// return;
				// }

				if (mZhengcheTx.getTag() == null) {
					ToastUtils.toast(mContext, "请选择用车方式");
					return;
				}

				if (mDataReceDialog != null
						&& (!mChooseReceiverTime.getText().equals(""))) {
					if (mDataSendDialog.getDataIndex() > mDataReceDialog
							.getDataIndex()
							|| (mDataSendDialog.getDataIndex() == mDataReceDialog
									.getDataIndex() && mDataSendDialog
									.getTime1Index() > mDataReceDialog
									.getTime1Index())) {
						ToastUtils.toast(mContext, "收货日期不能早于发货日期");
						return;
					}
				}

				AnimationSet mAnimationSet = getAnimationSet();
				mFirstPage.startAnimation(mAnimationSet);
			}
			break;
		case R.id.start_place:
			if (provinceList == null) {
				ToastUtils.toast(mContext, "正在获取城市列表,请稍后再试");
				return;
			}
			AreasPickDialog mAreasStartDialog = new AreasPickDialog(mContext,
					provinceList);
			mAreasStartDialog.setTitle("请选择目的地");
			mAreasStartDialog
					.SetDataSelectOnClickListener(new AreasDataPickLisener() {
						@Override
						public void onAreasDataSelect(int proviceindex,
								int cityid, int districtid) {
							// TODO Auto-generated method stub
							mStartProvinceIndex = proviceindex;
							mStartCityIndex = cityid;
							mStartDistrictIndex = districtid;
							String province = provinceList.get(proviceindex)
									.getName();
							mInitValue.sender_regions_id = ""
									+ provinceList.get(proviceindex).getId();
							String city = provinceList.get(proviceindex)
									.getCityList().get(mStartCityIndex)
									.getName();
							mInitValue.sender_regions_id += ","
									+ provinceList.get(proviceindex)
											.getCityList().get(mStartCityIndex)
											.getId();
							String district = provinceList.get(proviceindex)
									.getCityList().get(mStartCityIndex)
									.getDistrictList().get(mStartDistrictIndex)
									.getName();
							mInitValue.sender_regions_id += ","
									+ provinceList.get(proviceindex)
											.getCityList().get(mStartCityIndex)
											.getDistrictList()
											.get(mStartDistrictIndex).getId();
							mStartPlace.setText(province + "-" + city + "-"
									+ district);
						}
					});
			mAreasStartDialog.setCurrentArea(mStartProvinceIndex,
					mStartCityIndex, mStartDistrictIndex);
			mAreasStartDialog.show();
			break;
		case R.id.end_place:
			if (provinceList == null) {
				ToastUtils.toast(mContext, "正在获取城市列表,请稍后再试");
				return;
			}
			AreasPickDialog mAreasEndDialog = new AreasPickDialog(mContext,
					provinceList);
			mAreasEndDialog.setTitle("请选择目的地");
			mAreasEndDialog
					.SetDataSelectOnClickListener(new AreasDataPickLisener() {
						@Override
						public void onAreasDataSelect(int proviceindex,
								int cityid, int districtid) {
							// TODO Auto-generated method stub
							mEndProvinceIndex = proviceindex;
							mEndCityIndex = cityid;
							mEndDistrictIndex = districtid;
							String province = provinceList.get(proviceindex)
									.getName();
							mInitValue.receiver_regions_id = provinceList.get(
									proviceindex).getId();
							String city = provinceList.get(proviceindex)
									.getCityList().get(mEndCityIndex).getName();
							mInitValue.receiver_regions_id += ","
									+ provinceList.get(proviceindex)
											.getCityList().get(mEndCityIndex)
											.getId();

							String district = provinceList.get(proviceindex)
									.getCityList().get(mEndCityIndex)
									.getDistrictList().get(mEndDistrictIndex)
									.getName();
							mInitValue.receiver_regions_id += ","
									+ provinceList.get(proviceindex)
											.getCityList().get(mEndCityIndex)
											.getDistrictList()
											.get(mEndDistrictIndex).getId();
							mEndPlace.setText(province + "-" + city + "-"
									+ district);
						}
					});
			mAreasEndDialog.setCurrentArea(mEndProvinceIndex, mEndCityIndex,
					mEndDistrictIndex);
			mAreasEndDialog.show();
			break;
		case R.id.choose_truck:
			if (mTruckDialog == null) {
				adb = new AlertDialog.Builder(mContext,
						AlertDialog.THEME_HOLO_LIGHT);
				adb.setTitle("请选择需要的车型");
				View mTruckView = mInflater
						.inflate(R.layout.gallery_view, null);
				mTruckGridView = (GridView) mTruckView
						.findViewById(R.id.mygridview);
				mTruckGridView
						.setSelector(new ColorDrawable(Color.TRANSPARENT));
				mTruckGridView.setAdapter(new ImageAdapter(mContext,
						mConfigParser.entity.data.car_model));
				mTruckGridView.setOnItemClickListener(mGridItemListener);
				adb.setView(mTruckView);
				mTruckDialog = adb.create();
			}
			mTruckDialog.show();
			break;
		case R.id.choose_length:
			if (mTruckLenDialog == null) {
				adb = new AlertDialog.Builder(mContext,
						AlertDialog.THEME_HOLO_LIGHT);
				adb.setTitle("请选择需要的车长");
				View mTruckLenView = mInflater.inflate(R.layout.gallery_view,
						null);
				mTruckLenGridView = (GridView) mTruckLenView
						.findViewById(R.id.mygridview);
				mTruckLenGridView.setSelector(new ColorDrawable(
						Color.TRANSPARENT));
				mTruckLenGridView.setAdapter(new ImageAdapter(mContext,
						mConfigParser.entity.data.car_length));
				mTruckLenGridView.setOnItemClickListener(mGridItemListener);
				adb.setView(mTruckLenView);
				mTruckLenDialog = adb.create();
			}
			mTruckLenDialog.show();
			break;
		case R.id.second_next_step:
			if (mProductName.getText().toString().equals("")) {
				ToastUtils.toast(mContext, "请填写货物品名");
				return;
			}

			if (mProductWeight.getText().toString().equals("")) {
				ToastUtils.toast(mContext, "请填写货物重量");
				return;
			}

			if (mPincheTx.getTag().equals(1)) {
				if (mWriteUseTruckLen.getText().toString().equals("")) {
					ToastUtils.toast(mContext, "请填写预计占用车长");
					return;
				}
			}

			// if (mProductVolume.getText().toString().equals("")) {
			// ToastUtils.toast(mContext, "请填写货物体积");
			// return;
			// }
			if (mZhengcheTx.getTag().equals(1)) {
				if (mChooseTruck.getText().equals("")) {
					ToastUtils.toast(mContext, "请选择需求车长");
					return;
				}
				if (mChooseTruck.getText().equals("")) {
					ToastUtils.toast(mContext, "请选择需求车型");
					return;
				}
			}
			mSecondPage.startAnimation(getAnimationSet());
			break;
		case R.id.third_confirm_btn:
			// mThirdPage.startAnimation(getAnimationSet());
			// if(mChoosePay.getChooseIndex() == -1){
			// ToastUtils.toast(mContext, "请选择支付类型");
			// return;
			// }
			// Intent intent = new Intent(mContext, MyQueryPriceList.class);
			// startActivity(intent);

			if (mPayType.getText().toString().equals("")) {
				ToastUtils.toast(mContext, "请选择结算方式");
				return;
			}

			if (mFapiao.getChooseIndex() == -1) {
				ToastUtils.toast(mContext, "请选择是否开发票");
				return;
			}

			if (mHuidan.getChooseIndex() == -1) {
				ToastUtils.toast(mContext, "请选择是否回单");
				return;
			}

			pushQueryPriceToServer();
			break;
		case R.id.pinche_layout:
			updateTruckWayImage(2);
			break;
		case R.id.zhengche_layout:
			updateTruckWayImage(1);
			break;
		case R.id.pay_type:
			if (!SharePerfenceUtil.IsLogin(mContext)) {
				Intent intent = new Intent(mContext, EntryLoginActivity.class);
				startActivity(intent);
				return;
			}
			new AlertDialog.Builder(mContext).setItems(mPayTypeValueItem,
					new AlertListener()).show();
			break;
		case R.id.back_view:
			onBackPressed();
			break;
		}
	}

	class AlertListener implements OnClickListener {
		@Override
		public void onClick(DialogInterface view, int position) {
			mPayType.setText(mPayTypeValueItem[position]);
			mInitValue.pay_type = mPayTypeKeyItem[position];
		}
	}

	private void updateTruckWayImage(int index) {
		if (index == 2) {
			mPinChelayout.setBackgroundResource(R.drawable.choose_red);
			// mPincheImg.setBackgroundResource(R.drawable.gouhao);
			// mPincheImg.setVisibility(android.view.View.VISIBLE);
			mPincheTx.setTextColor(getResources()
					.getColor(R.color.choose_color));
			mPincheTx.setTag(1);
			mZhengcheTx.setTag(0);
			// mZhengcheImg.setVisibility(android.view.View.GONE);
			mZhengcheTx.setTextColor(this.getResources().getColor(
					R.color.unchoose_color));
			mZhengcheLayout.setBackgroundResource(R.drawable.choose_gray);

			mlengthX.setVisibility(android.view.View.INVISIBLE);
			mTypeX.setVisibility(android.view.View.INVISIBLE);
		} else if (index == 1) {
			mZhengcheLayout.setBackgroundResource(R.drawable.choose_red);
			// mZhengcheImg.setBackgroundResource(R.drawable.gouhao);
			// mZhengcheImg.setVisibility(android.view.View.VISIBLE);
			mZhengcheTx.setTextColor(this.getResources().getColor(
					R.color.choose_color));
			mPincheTx.setTag(0);
			mZhengcheTx.setTag(1);
			// mPincheImg.setVisibility(android.view.View.GONE);
			mPincheTx.setTextColor(this.getResources().getColor(
					R.color.unchoose_color));
			mPinChelayout.setBackgroundResource(R.drawable.choose_gray);

			mlengthX.setVisibility(android.view.View.VISIBLE);
			mTypeX.setVisibility(android.view.View.VISIBLE);
		}
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
	}

	private void initView(View parentView) {
		mTitle = (TextView) parentView.findViewById(R.id.title);
		mTitle.setText("在线发货");
		mBackView = (RelativeLayout) parentView.findViewById(R.id.back_view);
		mBackView.setOnClickListener(this);

		// first page
		mFirstPage = mInflater.inflate(R.layout.query_first_page, null);
		mChooseSendTime = (TextView) mFirstPage
				.findViewById(R.id.choose_send_time);
		mChooseSendTime.setOnClickListener(this);

		mChooseReceiverTime = (TextView) mFirstPage
				.findViewById(R.id.choose_receiver_time);
		mChooseReceiverTime.setOnClickListener(this);

		mStartPlace = (TextView) mFirstPage.findViewById(R.id.start_place);
		mStartPlace.setOnClickListener(this);
		mEndPlace = (TextView) mFirstPage.findViewById(R.id.end_place);
		mEndPlace.setOnClickListener(this);
		mConfirmQuery = (Button) mFirstPage.findViewById(R.id.confirm_query);
		mConfirmQuery.setOnClickListener(this);

		mPinChelayout = (RelativeLayout) mFirstPage
				.findViewById(R.id.pinche_layout);
		mPinChelayout.setOnClickListener(this);

		// mPincheImg = (ImageView) mFirstPage.findViewById(R.id.gou_img);
		mPincheTx = (TextView) mFirstPage.findViewById(R.id.pinche_tx);

		// mZhengcheImg = (ImageView) mFirstPage.findViewById(R.id.gou_img2);
		mZhengcheTx = (TextView) mFirstPage.findViewById(R.id.zhengche_tx);

		mZhengcheLayout = (RelativeLayout) mFirstPage
				.findViewById(R.id.zhengche_layout);
		mZhengcheLayout.setOnClickListener(this);

		// second page
		mSecondPage = mInflater.inflate(R.layout.query_second_page, null);
		mChooseTruck = (TextView) mSecondPage.findViewById(R.id.choose_truck);
		mChooseTruck.setOnClickListener(this);
		mChooseLength = (TextView) mSecondPage.findViewById(R.id.choose_length);
		mChooseLength.setOnClickListener(this);
		mSecondNextStep = (Button) mSecondPage
				.findViewById(R.id.second_next_step);
		mSecondNextStep.setOnClickListener(this);
		mProductWeight = (EditText) mSecondPage
				.findViewById(R.id.write_product_weight);
		mProductName = (EditText) mSecondPage.findViewById(R.id.produce_name);
		mProductVolume = (EditText) mSecondPage
				.findViewById(R.id.write_product_volume);

		mlengthX = (TextView) mSecondPage.findViewById(R.id.length_x);
		mTypeX = (TextView) mSecondPage.findViewById(R.id.type_x);
		mUseTruckLenLayout = (RelativeLayout) mSecondPage
				.findViewById(R.id.zhanyongchechang_layout);
		mUseTruckLenLine = (TextView) mSecondPage
				.findViewById(R.id.user_truck_len_line);
		mWriteUseTruckLen = (EditText) mSecondPage
				.findViewById(R.id.write_use_truck_len);

		// third page
		mThirdPage = mInflater.inflate(R.layout.query_third_page, null);
		mThirdBtn = (Button) mThirdPage.findViewById(R.id.third_confirm_btn);
		mThirdBtn.setOnClickListener(this);

		mHuidan = (MutiChooseBtn) mThirdPage.findViewById(R.id.huidan);
		mHuidan.init(new String[] { "不需要回单", "需要回单" });
		// mChoosePay = (MutiChooseBtn)
		// mThirdPage.findViewById(R.id.choose_pay);
		// mChoosePay.init(new String[] { "发货人付款", "收货人付款" });

		mPayType = (TextView) mThirdPage.findViewById(R.id.pay_type);
		mPayType.setOnClickListener(this);

		mFapiao = (MutiChooseBtn) mThirdPage.findViewById(R.id.kaifapiao);
		mFapiao.init(new String[] { "不开发票", "开发票" });

		mBeizu = (EditText) mThirdPage.findViewById(R.id.beizu);
		mExpertPrice = (EditText) mThirdPage
				.findViewById(R.id.write_expect_price);

		// others
		mBaseContent = (LinearLayout) parentView
				.findViewById(R.id.base_content);
		Log.i("aa", "mCurrentPage:" + mCurrentPage);
		mBaseContent.removeAllViews();
		if (mCurrentPage == 2) {
			mBaseContent.addView(mSecondPage);
		} else if (mCurrentPage == 3) {
			mBaseContent.addView(mThirdPage);
		} else {
			mBaseContent.addView(mFirstPage);
		}
	}

	@Override
	protected void initWidget(View parentView) {
		// TODO Auto-generated method stub
		super.initWidget(parentView);
	}

	private CommonConfigJsonParser mConfigParser;
	private boolean isConfigTaskRunning = false;

	private void getConfigData() {
		if (isConfigTaskRunning) {
			Log.i("aa", "getConfigData task is not finish ....");
			return;
		} else {
			new GetConfigTask().execute();
		}
	}

	public class GetConfigTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mConfigParser = new CommonConfigJsonParser();
			parmas = new HashMap<String, String>();
			isConfigTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mConfigParser.entity.status.equals("Y")) {
					SharePerfenceUtil.SaveConfigData(mContext,
							mConfigParser.entity);
					if (SharePerfenceUtil.IsLogin(mContext)) {
						getUserDetail();
					}
				}
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			isConfigTaskRunning = false;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.getConfigData2(mContext, parmas, mConfigParser);
			return msg.what;
		}

	}

	Handler mUserDetailHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// mContext.cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mUserDetailParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					// Log.i(TAG, "result:" + Tools.UnicodeDecode(result));
					SharePerfenceUtil.setName(mContext,
							mUserDetailParser.entity.data.name);
					SharePerfenceUtil.setMobile(mContext,
							mUserDetailParser.entity.data.mobile);
					SharePerfenceUtil.setUserType(mContext,
							mUserDetailParser.entity.data.type);
					SharePerfenceUtil.setUserGender(mContext,
							mUserDetailParser.entity.data.gender);
					SharePerfenceUtil.setUserAccountType(mContext,
							mUserDetailParser.entity.data.account_type);

					if (mUserDetailParser.entity.data.type.equals("1")) {
						mPayTypeValueItem = new String[mConfigParser.entity.data.type2pay.type1
								.size()];
						mPayTypeKeyItem = new String[mConfigParser.entity.data.type2pay.type1
								.size()];
						for (int i = 0; i < mConfigParser.entity.data.type2pay.type1
								.size(); i++) {
							int type = mConfigParser.entity.data.type2pay.type1
									.get(i);
							mPayTypeValueItem[i] = mConfigParser.entity.data.pay_type
									.get(type - 1).value;
							mPayTypeKeyItem[i] = mConfigParser.entity.data.pay_type
									.get(type - 1).key;
						}
					} else {
						mPayTypeValueItem = new String[mConfigParser.entity.data.type2pay.type0
								.size()];
						mPayTypeKeyItem = new String[mConfigParser.entity.data.type2pay.type0
								.size()];
						for (int i = 0; i < mConfigParser.entity.data.type2pay.type0
								.size(); i++) {
							int type = mConfigParser.entity.data.type2pay.type0
									.get(i);
							mPayTypeValueItem[i] = mConfigParser.entity.data.pay_type
									.get(type - 1).value;
							mPayTypeKeyItem[i] = mConfigParser.entity.data.pay_type
									.get(type - 1).key;
						}
					}
				} else {
					ToastUtils.toast(mContext, mUserDetailParser.entity.msg);
				}
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}

	};

	private void getUserDetail() {
		// mContext.showProgressDialog();
		mUserDetailParser = new UserDetailJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		MLHttpConnect.GetUserDetail(mContext, parmas, mUserDetailParser,
				mUserDetailHandler);
	}

	private SimpleJasonParser mSimpleJsonParser;
	Handler mPushHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mContext.cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mSimpleJsonParser.entity.status.equals("Y")) {
					ToastUtils.toast(mContext, "新增询价成功");
					mRootView = null;
					mCurrentPage = 1;
					mContext.changFragment(2);
				} else {
					ToastUtils.toast(mContext, mSimpleJsonParser.entity.msg);
				}
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}

	};

	private void pushQueryPriceToServer() {
		mContext.showProgressDialog();
		mSimpleJsonParser = new SimpleJasonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("sender_regions_id", mInitValue.sender_regions_id);
		parmas.put("receiver_regions_id", mInitValue.receiver_regions_id);

		parmas.put("goods_loadtime", mInitValue.goods_loadtime);
		parmas.put("goods_loadtime_desc", mInitValue.goods_loadtime_desc);

		if (!mChooseReceiverTime.getText().equals("")) {
			parmas.put("goods_unloadtime", mInitValue.goods_unloadtime);
			parmas.put("goods_unloadtime_desc",
					mInitValue.goods_unloadtime_desc);
		} else {
			parmas.put("goods_unloadtime", "");
			parmas.put("goods_unloadtime_desc", "");
		}
		parmas.put("goods_name", mProductName.getText().toString());
		parmas.put("goods_cubage", mProductVolume.getText().toString());
		parmas.put("goods_weight", mProductWeight.getText().toString());
		parmas.put("car_model", mInitValue.car_model);
		parmas.put("car_length", mInitValue.car_length);
		parmas.put("pay_type", "" + mInitValue.pay_type);
		parmas.put("receipt", "" + mFapiao.getChooseIndex());
		parmas.put("need_back", "" + mHuidan.getChooseIndex());
		parmas.put("expect_price", mExpertPrice.getText().toString());
		parmas.put("occupy_length", mWriteUseTruckLen.getText().toString());

		int userType = Integer.valueOf(SharePerfenceUtil.getUserType(mContext));
		if (userType == 1) {
			parmas.put("order_type", "" + 2);
		} else {
			parmas.put("order_type", "" + 1);
		}

		parmas.put("remark", mBeizu.getText().toString());

		if (mPincheTx.getTag().equals(1)) {
			parmas.put("car_type", "2");
		} else {
			parmas.put("car_type", "1");
		}

		MLHttpConnect.QuoteCreate(mContext, parmas, mSimpleJsonParser,
				mPushHandler);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getConfigData();
	}

	@Override
	protected View inflaterView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle) {
		// TODO Auto-generated method stub
		Log.i("aa", "inflaterView ..........");
		mContext = (TabActivity) getActivity();
		mInflater = inflater;
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.query_price, null);
			mInitValue = new FirstTabInitValue();
			String cacheUrl = MLHttpConstant.URL_START
					+ "/common/region?version="
					+ Tools.getVersionCode(mContext);
			/*
			 * mCache = MLHttpConnect2.readCacheDirectly(cacheUrl, mContext);
			 * Log.i(TAG, "mCache:" + mCache); if (mCache != null &&
			 * !TextUtils.isEmpty(mCache)) { parser = new AreasJsonParser();
			 * parser.parser(mCache); initProvinceDatas(); } else {
			 * getAreaData(); }
			 */
			getAreaData();
			initView(mRootView);
		} else {
			ViewGroup parent = (ViewGroup) mRootView.getParent();
			if (parent != null) {
				parent.removeView(mRootView);
			}
		}
		return mRootView;
	}
}
