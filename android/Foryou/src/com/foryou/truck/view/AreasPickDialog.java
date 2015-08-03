package com.foryou.truck.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.foryou.truck.R;
import com.foryou.truck.model.CityModel;
import com.foryou.truck.model.DistrictModel;
import com.foryou.truck.model.ProvinceModel;

public class AreasPickDialog extends AlertDialog {
	private WheelView mViewProvince, mViewCity, mViewDistrict;
	private Context mContext;
	private List<ProvinceModel> mProvinceList;
	protected String[] mProvinceDatas;
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
	private AreasDataPickLisener mDataSelectLister;

	private String mCurrentProviceName, mCurrentCityName, mCurrentDistrictName,
			mCurrentZipCode;

	private void initProvinceDatas() {
		mProvinceDatas = new String[mProvinceList.size()];
		for (int i = 0; i < mProvinceList.size(); i++) {
			mProvinceDatas[i] = mProvinceList.get(i).getName();
			List<CityModel> cityList = mProvinceList.get(i).getCityList();
			String[] cityNames = new String[cityList.size()];
			for (int j = 0; j < cityList.size(); j++) {
				cityNames[j] = cityList.get(j).getName();
				List<DistrictModel> districtList = cityList.get(j)
						.getDistrictList();
				String[] distrinctNameArray = new String[districtList.size()];
				DistrictModel[] distrinctArray = new DistrictModel[districtList
						.size()];
				for (int k = 0; k < districtList.size(); k++) {
					DistrictModel districtModel = new DistrictModel(
							districtList.get(k).getName(), districtList.get(k)
									.getZipcode());
					mZipcodeDatasMap.put(districtList.get(k).getName(),
							districtList.get(k).getZipcode());
					distrinctArray[k] = districtModel;
					distrinctNameArray[k] = districtModel.getName();
				}
				mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
			}
			mCitisDatasMap.put(mProvinceList.get(i).getName(), cityNames);
		}
	}

	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(mContext,
				areas));
		mViewDistrict.setCurrentItem(0);
		mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
		mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
	}

	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity
				.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	private OnWheelChangedListener mWheelChangedListener = new OnWheelChangedListener() {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			// TODO Auto-generated method stub
			if (wheel == mViewProvince) {
				updateCities();
			} else if (wheel == mViewCity) {
				updateAreas();
			} else if (wheel == mViewDistrict) {
				mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
				mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
			}
		}
	};

	public interface AreasDataPickLisener {
		void onAreasDataSelect(int proviceindex, int cityid, int districtid);
	}

	private void InitWheelView(View view) {
		mViewProvince = (WheelView) view.findViewById(R.id.id_province);
		mViewCity = (WheelView) view.findViewById(R.id.id_city);
		mViewDistrict = (WheelView) view.findViewById(R.id.id_district);

		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext,
				mProvinceDatas));
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);

		// mViewProvince.setCurrentItem(mCurrentProvinceIndex);

		// mViewProvince.setCurrentItem(mCurrentProvinceIndex);
		updateCities();
		// mViewCity.setCurrentItem(mCurrentCityIndex);
		// mCurrentCityName =
		// mCitisDatasMap.get(mCurrentProviceName)[mCurrentCityIndex];

		updateAreas();
		// mViewDistrict.setCurrentItem(mCurrentDistrictIndex);
		// mCurrentDistrictName =
		// mDistrictDatasMap.get(mCurrentCityName)[mCurrentDistrictIndex];
		mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);

		mViewProvince.addChangingListener(mWheelChangedListener);
		mViewCity.addChangingListener(mWheelChangedListener);
		mViewDistrict.addChangingListener(mWheelChangedListener);

		setButton(BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (mDataSelectLister != null) {
					// 避免当区数据为空时程序崩溃
					mDataSelectLister.onAreasDataSelect(
							mViewProvince.getCurrentItem(),
							mViewCity.getCurrentItem(),
							mViewDistrict.getCurrentItem());
				}
			}
		});
	}

	public AreasPickDialog(Context context, List<ProvinceModel> provinceList) {
		super(context, THEME_HOLO_LIGHT);
		// TODO Auto-generated constructor stub
		mContext = context;
		mProvinceList = provinceList;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.choose_place, null);
		InitWheelView(view);
		setView(view);
	}

	public void SetDataSelectOnClickListener(AreasDataPickLisener listener) {
		mDataSelectLister = listener;
	}

	public void setCurrentArea(int provinceIndex, int cityIndex,
			int districtIndex) {
		mViewProvince.setCurrentItem(provinceIndex);
		updateCities();
		mViewCity.setCurrentItem(cityIndex);
		updateAreas();
		mViewDistrict.setCurrentItem(districtIndex);
	}
}
