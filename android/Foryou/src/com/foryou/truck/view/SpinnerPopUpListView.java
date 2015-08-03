package com.foryou.truck.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.util.ScreenInfo;

public class SpinnerPopUpListView {
	String[] mlistData;
	Context mContext;
	LinearLayout mlinera;
	View posView;
	PopupWindow mPopupWindow;
	SpOnClickListener mlistener;
	int mCurrentIndex = 0;
	ArrayList<String[]> mSecondLevelData;
	int mode = 1;

	ListView firstlist;
	ListView secondlist;
	int mFirstFocus = 0, mFirstTempFocus = 0, mSecondFocus = 0;
	MyListViewAdapter mAdapter1, mAdapter2;
	List<Map<String, Object>> data1, data2;

	public interface SpOnClickListener {
		abstract void onItemsClick(int pos);
	}

	public void setSpClickListener(SpOnClickListener listener) {
		mlistener = listener;
	}

	public class ItemsOnClickListener implements View.OnClickListener {
		int position;

		public ItemsOnClickListener(int index) {
			position = index;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mPopupWindow.dismiss();
			if (mlistener != null) {
				mlistener.onItemsClick(position);
				mCurrentIndex = position;
			}
		}
	};

	private LinearLayout getCommonListView() {
		mlinera = new LinearLayout(mContext);
		mlinera.removeAllViews();
		mlinera.setOrientation(LinearLayout.VERTICAL);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		mlinera.setBackgroundResource(R.drawable.frame);
	//	mlinera.setBackgroundColor(Color.BLACK);
		mlinera.setPadding(0, 30, 0, 0);
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				ScreenInfo.dip2px(mContext, 150), LayoutParams.WRAP_CONTENT);
		mlinera.setLayoutParams(lp);
		for (int i = 0; i < mlistData.length; i++) {
			View v = inflater.inflate(R.layout.spinner_list_item, null);
			v.setOnClickListener(new ItemsOnClickListener(i));
			TextView tx = (TextView) v.findViewById(R.id.text);
			tx.setText(mlistData[i]);
			
			TextView line = (TextView)v.findViewById(R.id.line);
			if(i == mlistData.length - 1){
				line.setVisibility(android.view.View.GONE);
			}
			mlinera.addView(v);
		}
		return mlinera;
	}

	public SpinnerPopUpListView(Context context, String[] mData, View view) {
		mContext = context;
		mlistData = mData;
		posView = view;
		mode = 1;

		mPopupWindow = new PopupWindow(getCommonListView(),
				ScreenInfo.dip2px(mContext, 145), LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		mPopupWindow.setTouchable(true); // 设置PopupWindow可触摸
		mPopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		ColorDrawable dw = new ColorDrawable(-00000);
		mPopupWindow.setBackgroundDrawable(dw);
	}
	
	public SpinnerPopUpListView(Context context, ArrayList<String> mData, View view) {
		mContext = context;
		mlistData = new String[mData.size()];
		for(int i = 0;i<mData.size();i++){
			mlistData[i] = mData.get(i);
		}
		posView = view;
		mode = 1;

		mPopupWindow = new PopupWindow(getCommonListView(),
				ScreenInfo.dip2px(mContext, 145), LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		mPopupWindow.setTouchable(true); // 设置PopupWindow可触摸
		mPopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		ColorDrawable dw = new ColorDrawable(-00000);
		mPopupWindow.setBackgroundDrawable(dw);
	}

	public void Show() {
		int width = ScreenInfo.getScreenInfo(((Activity)mContext)).widthPixels;
		mPopupWindow.showAsDropDown(posView, width - ScreenInfo.dip2px(mContext, 150),
				ScreenInfo.dip2px(mContext, (float) 8.5));
		// mPopupWindow.showAtLocation(posView, Gravity.BOTTOM, 0, 0);
	}
}
