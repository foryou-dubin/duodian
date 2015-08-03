package com.foryou.truck;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.SharePerfenceUtil;

public class WelcomeViewPage extends BaseActivity {

	private ViewPager mViewPager;
	private int[] mImageArr = { R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, };
	private int mCurrentIndex;
	private ImageView mIndex1, mIndex2, mIndex3;
	private ImageView mStartApp;

	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	public class MyOnPageChangeListener implements
			ViewPager.OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			mCurrentIndex = arg0;
			updateIndexImage(arg0 + 1);
		}
	}

	private void updateIndexImage(int index) {
		switch (index) {
		case 1:
			mStartApp.setVisibility(android.view.View.GONE);
			mIndex1.setBackgroundResource(R.drawable.circle2);
			mIndex2.setBackgroundResource(R.drawable.circle1);
			mIndex3.setBackgroundResource(R.drawable.circle1);
			break;
		case 2:
			mStartApp.setVisibility(android.view.View.GONE);
			mIndex1.setBackgroundResource(R.drawable.circle1);
			mIndex2.setBackgroundResource(R.drawable.circle2);
			mIndex3.setBackgroundResource(R.drawable.circle1);
			break;
		case 3:
			mStartApp.setVisibility(android.view.View.VISIBLE);
			mIndex1.setBackgroundResource(R.drawable.circle1);
			mIndex2.setBackgroundResource(R.drawable.circle1);
			mIndex3.setBackgroundResource(R.drawable.circle2);
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome_viewpage);

		mViewPager = (ViewPager) findViewById(R.id.vPager);

		ArrayList<View> views = new ArrayList<View>();
		for (int i = 0; i < mImageArr.length; i++) {
			ImageView view = new ImageView(this);
			view.setBackgroundResource(mImageArr[i]);
			views.add(view);
		}
		mViewPager.setAdapter(new MyViewPagerAdapter(views));
		mViewPager.setCurrentItem(0);
		mCurrentIndex = 0;

		mIndex1 = (ImageView) findViewById(R.id.index1);
		mIndex2 = (ImageView) findViewById(R.id.index2);
		mIndex3 = (ImageView) findViewById(R.id.index3);

		mStartApp = (ImageView) findViewById(R.id.startapp);
		int screenHeight = ScreenInfo.getScreenInfo(this).heightPixels;
		Log.i("aa", "screenWidth:" + screenHeight);
		float canshu = (float)screenHeight/(float)1920;
		Log.i("aa","canshu:"+canshu);
		// mStartApp.setL
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.bottomMargin = (int)(65*3*canshu);
		Log.i("aa","bottomMargin:"+params.bottomMargin);
		mStartApp.setLayoutParams(params);
		mStartApp.setOnClickListener(this);

		updateIndexImage(1);

		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.startapp:
			SharePerfenceUtil.SetFirstLogin(this, false);
			Intent intent = new Intent(this, TabActivity.class);
			startActivity(intent);
			finish();
			break;
		}
	}
}
