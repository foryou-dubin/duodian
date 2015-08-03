package com.foryou.truck.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.tools.ImageTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MyListViewAdapter extends BaseAdapter {
	Activity mContext;
	LayoutInflater inflater;
	String TAG = "MyListViewAdapter";
	String[] mFrom;
	int[] mTo;
	List<Map<String, Object>> dataList;
	int colorResId, marginResId;
	int setColorViewId, marginLeftLen;
	int layoutId;
	private ArrayList<CallBacks> mCallBacklist;
	private ArrayList<Integer> mlistenerId;
	private ArrayList<CallBacks> mLongClickCallBacklist;
	private ArrayList<Integer> mLongClickListenerId;
	// private CallBacks mCallBacks;
	// private int mListenerId = -1;
	private boolean mIsRoundImg;
	private boolean mGalleryListlayoutParam = false;

	private MyTimer mMytime;
	private ArrayList<TextView> mTextViewlist;

	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	private ArrayList<ChangeImage> mArrayList = new ArrayList<ChangeImage>();

	public class ChangeImage {
		int mPosition, mViewId, mImageId;

		public ChangeImage(int position, int viewid, int imageid) {
			mPosition = position;
			mViewId = viewid;
			mImageId = imageid;
		}
	}

	public MyListViewAdapter(Context context, List<Map<String, Object>> mlist,
			int layout, String[] str, int[] ids, boolean roundImg) {
		this.mContext = (Activity) context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFrom = str;
		mTo = ids;
		dataList = mlist;
		layoutId = layout;
		mIsRoundImg = roundImg;

		mCallBacklist = new ArrayList<CallBacks>();
		mlistenerId = new ArrayList<Integer>();

		mLongClickCallBacklist = new ArrayList<CallBacks>();
		mLongClickListenerId = new ArrayList<Integer>();

		mMytime = new MyTimer();
		//mTextViewlist = new ArrayList<TextView>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private void setViewText(TextView v, Boolean flag) {
		if (flag)
			v.setVisibility(android.view.View.VISIBLE);
		else
			v.setVisibility(android.view.View.GONE);
	}

	private void setViewText(TextView v, String text, int position) {
		if (text.equals("") || text.equals("null")) {
			v.setVisibility(android.view.View.GONE);
			return;
		}
		v.setText(text);

	}

	private void setViewBackGround(final View v, String value) {
		imageLoader.loadImage(value, new ImageLoadingListener() {
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				// TODO Auto-generated method stub
				v.setBackgroundDrawable(ImageTools.bitmapToDrawable(arg2));
			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void setViewImage(final ImageView v, String value) {
		v.setBackgroundDrawable(null);
		v.setImageBitmap(null);
		if (value.equals("") || value.equals("null")) {
			// v.setBackgroundResource(R.drawable.default_img);
			return;
		}
		try {
			// v.setImageResource(Integer.parseInt(value));
			if (mIsRoundImg) {
				imageLoader.displayImage(value, v,options, new ImageLoadingListener() {
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						// TODO Auto-generated method stub
						Bitmap tmp = ImageTools.toRoundBitmap(arg2);
						v.setImageBitmap(tmp);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

				});
			} else {
				imageLoader.displayImage(value, v, options);
			}
		} catch (NumberFormatException nfe) {
			v.setImageURI(Uri.parse(value));

		}
	}

	public void setChangeImageView(int position, int resid, int imgid) {
		ChangeImage mImg = new ChangeImage(position, resid, imgid);
		for (int i = 0; i < mArrayList.size(); i++) {
			if (position == mArrayList.get(i).mPosition
					&& resid == mArrayList.get(i).mViewId)
				mArrayList.remove(i);
		}
		mArrayList.add(mImg);
	}

	public void setTextMarginleft(int resId, int marginleftlen) {
		marginResId = resId;
		marginLeftLen = marginleftlen;

	}

	public void setTextColor(int resId, int colorId) {
		setColorViewId = resId;
		colorResId = colorId;
	}

	private void setViewImage(ImageView v, int value, int position) {
		if (value == 0) {
			v.setVisibility(android.view.View.GONE);
			return;
		} else {
			v.setVisibility(android.view.View.VISIBLE);
		}

		if (mIsRoundImg) {
			Drawable drawable = mContext.getResources().getDrawable(value);
			v.setImageBitmap(ImageTools.toRoundBitmap(ImageTools
					.toRoundBitmap(ImageTools.drawableToBitmap(drawable))));
		} else {
			v.setBackgroundResource(value);
		}
	}

	private void bindView(int position, View view) {
		if (dataList.size() == 0)
			return;
		final Map dataSet = dataList.get(position);
		if (dataSet == null) {
			return;
		}

		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = view.findViewById(to[i]);
			v.setVisibility(android.view.View.VISIBLE);
			if (v != null) {
				final Object data = dataSet.get(from[i]);
				String text = data == null ? "" : data.toString();
				if (text == null) {
					text = "";
				}

				if (v instanceof Checkable) {
					if (data instanceof Boolean) {
						((Checkable) v).setChecked((Boolean) data);
					} else if (v instanceof TextView) {
						// Note: keep the instanceof TextView check at the
						// bottom of these
						// ifs since a lot of views are TextViews (e.g.
						// CheckBoxes).
						setViewText((TextView) v, text, position);
					} else {
						throw new IllegalStateException(v.getClass().getName()
								+ " should be bound to a Boolean, not a "
								+ (data == null ? "<unknown type>"
										: data.getClass()));
					}
				} else if (v instanceof TextView) {
					// Note: keep the instanceof TextView check at the bottom of
					// these
					// ifs since a lot of views are TextViews (e.g. CheckBoxes).
					if (data instanceof Boolean) {
						setViewText((TextView) v, (Boolean) data);
					} else
						setViewText((TextView) v, text, position);
				} else if (v instanceof ImageView) {
					if (data instanceof Integer) {
						setViewImage((ImageView) v, (Integer) data, position);
					} else {
						setViewImage((ImageView) v, text);
					}
				} else if (v instanceof RelativeLayout) {
					if (data instanceof Integer) {
						v.setBackgroundResource((Integer) data);
					} else if (data instanceof String) {
						setViewBackGround(v, (String) data);
					} else {
						setViewVisable((ViewGroup) v, (Boolean) data);
					}
				} else if (v instanceof LinearLayout) {
					if (data instanceof Integer) {
						v.setBackgroundResource((Integer) data);
					} else {
						setViewVisable((ViewGroup) v, (Boolean) data);
					}
				} else {
					throw new IllegalStateException(v.getClass().getName()
							+ " is not a "
							+ " view that can be bounds by this SimpleAdapter");
				}

			}
		}
	}

	private void setViewVisable(ViewGroup v, boolean flag) {
		if (flag) {
			v.setVisibility(android.view.View.VISIBLE);
		} else {
			v.setVisibility(android.view.View.GONE);
		}
	}

	public void setGalleryListLayoutParam(boolean flag) {
		mGalleryListlayoutParam = flag;
	}
	
//	private Handler mHander = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			int what = msg.what;
//			Long remaintime = (Long)msg.obj;
//			remaintime--;
//			//UpdateRemainTimeText(mRemainTime,mElapseTime);
//			msg.obj = remaintime;
//			mHander.sendMessageDelayed(msg, 1000);
//		}
//	};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v;
		if (convertView == null) {
			v = inflater.inflate(layoutId, parent, false);
		} else {
			v = convertView;
		}
		bindView(position, v);

		if (R.layout.query_list_item == layoutId) {
			int quoteN = Integer.valueOf((String) dataList.get(position).get(
					"quote_n"));
			TextView mBaojia = (TextView) v.findViewById(R.id.baojia_text);
			TextView mWuBaojia = (TextView) v.findViewById(R.id.wubaojia_text);
			RelativeLayout mBaojiaLayout = (RelativeLayout) v
					.findViewById(R.id.baojia_layout);
			ImageView mFanhuiArrow = (ImageView) v.findViewById(R.id.fanhui2);

			if (quoteN > 0) {
				mWuBaojia.setVisibility(android.view.View.GONE);
				mBaojia.setVisibility(android.view.View.VISIBLE);
				mFanhuiArrow.setVisibility(android.view.View.VISIBLE);
				mBaojia = (TextView) v.findViewById(R.id.baojia_text);
				mBaojiaLayout.setBackgroundColor(mContext.getResources().getColor(R.color.tishi_bg_color));
				String content = "已有" + quoteN + "份经纪人报价,最低报价"
						+ dataList.get(position).get("quote_min") + "元";
				/*SpannableString msp = new SpannableString(content);
				int index0 = content.indexOf("元");
				int index1 = content.indexOf("最低报价");
				msp.setSpan(new ForegroundColorSpan(Color.RED), index1 + 4,
						index0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				msp.setSpan(new RelativeSizeSpan(1.4f), index1 + 4, index0,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);*/
				mBaojia.setText(content);
			} else {
				mWuBaojia.setVisibility(android.view.View.VISIBLE);
				mBaojia.setVisibility(android.view.View.GONE);
				mFanhuiArrow.setVisibility(android.view.View.GONE);
				mBaojiaLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.wubaojia_bg));
			}
		} else if (R.layout.agent_list_item == layoutId) {
			TextView mRemainTime = (TextView) v.findViewById(R.id.remain_time);
			String timeDue = (String)dataList.get(position).get("expire_time");
			Long mElapseTime = Long.valueOf(timeDue) - System
					.currentTimeMillis() / 1000;
			if(mElapseTime>=0){
				Log.i(TAG,"getView postion:"+position);
				UpdateRemainTimeText(mRemainTime,mElapseTime);

			}else{
				UpdateRemainTimeText(mRemainTime,0);
				//dataList.get(position).put("remain_time_layout", false);
			}
			
			TextView mTryAgain = (TextView)v.findViewById(R.id.try_again);
			SpannableString msp = new SpannableString("尝试下单");
			msp.setSpan(new UnderlineSpan(), 0, msp.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			mTryAgain.setText(msp);
			
			Button mConfirmBtn = (Button) v.findViewById(R.id.confirm_btn);
			boolean flag = mElapseTime<=0?false:true;
			if (flag) {
				mConfirmBtn.setText("立即下单");
				mConfirmBtn.setBackgroundResource(R.drawable.login_btn);
				mTryAgain.setVisibility(android.view.View.GONE);
			} else {
				String reopen = (String)dataList.get(position).get("reopen");
				if(reopen.equals("2")){
					mConfirmBtn.setText("等待激活报价");
					mTryAgain.setVisibility(android.view.View.GONE);
					mConfirmBtn.setBackgroundResource(R.drawable.anniuhui);
				}else if(reopen.equals("0")){
					mConfirmBtn.setText("激活报价");
					mConfirmBtn.setBackgroundResource(R.drawable.login_btn);
					mTryAgain.setVisibility(android.view.View.GONE);
				}else{
					mConfirmBtn.setText("报价已失效");
					mTryAgain.setVisibility(android.view.View.GONE);
					mConfirmBtn.setBackgroundResource(R.drawable.anniuhui);
				}
				
				//mTryAgain.setVisibility(android.view.View.VISIBLE);
			}
		}
		for (int i = 0; i < mCallBacklist.size(); i++) {
			ItemListener mlistener = new ItemListener(position,
					mCallBacklist.get(i));
			v.findViewById(mlistenerId.get(i)).setOnClickListener(mlistener);
		}
		// if (mListenerId != -1) {
		// v.findViewById(mListenerId).setOnClickListener(mlistener);
		// }
		for (int j = 0; j < mLongClickCallBacklist.size(); j++) {
			LongClickItemListener mLongClicklistener = new LongClickItemListener(
					position, mLongClickCallBacklist.get(j));
			v.findViewById(mLongClickListenerId.get(j)).setOnLongClickListener(
					mLongClicklistener);
		}
		return v;
	}
	
	private class MyTimer extends Timer {
		Map<Integer, MyTimerTask> map = new HashMap<Integer, MyTimerTask>();
		
		public void schedule(MyTimerTask task,long delay,long period,int position){
			MyTimerTask mytask = (MyTimerTask)map.get(position);
			if(mytask == null){
				Log.i(TAG,"mytask == null");
				super.schedule(task, delay, period);
				map.put(position, task);
			}else{
				Log.i(TAG,"mytask !=null");
				mytask.cancel();
				super.schedule(task, delay, period);
				map.put(position, task);
			}
		}
	}

	class MyTimerTask extends TimerTask {
		private TextView view;
		private Long time;
		private int position;

		public MyTimerTask(TextView v, Long t,int pos) {
			view = v;
			time = t;
			position = pos;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			time--;
			mContext.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					UpdateRemainTimeText(view, time);
					
				}
			});
			if (time < 1) {
				this.cancel();
				// 刷新列表
			}
		}
	}
	private void UpdateRemainTimeText(TextView v, int seconds) {
		if(seconds == 0){
			v.setText("报价已失效");
			return;
		}
		
		int number = seconds;
		String content = "";
		if (seconds / (60 * 60) > 0) {
			content += seconds / 3600 + "小时";
		} else {
			content += 0 + "小时";
		}
		number = number % 3600;
		if (number / 60 > 0) {
			content += number / 60 + "分";
		} else {
			content += 0 + "分";
		}
		content += number % 60 + "秒";
		SpannableString msp = new SpannableString(content);
		msp.setSpan(new ForegroundColorSpan(0xff91c150), 0,
				content.indexOf("小时"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), 0, content.indexOf("小时"),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		msp.setSpan(new ForegroundColorSpan(0xff91c150),
				content.indexOf("小时") + 2, content.indexOf("分"),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), content.indexOf("小时") + 2,
				content.indexOf("分"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		msp.setSpan(new ForegroundColorSpan(0xff91c150),
				content.indexOf("分") + 1, content.indexOf("秒"),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), content.indexOf("分") + 1,
				content.indexOf("秒"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		v.setText(msp);
	}

	private void UpdateRemainTimeText(TextView v, Long seconds) {
		if(seconds == 0){
			v.setText("报价已失效");
			return;
		}
		
		Long number = seconds;
		String content = "";
		if (seconds / (60 * 60) > 0) {
			content += seconds / 3600 + "小时";
		} else {
			content += 0 + "小时";
		}
		number = number % 3600;
		if (number / 60 > 0) {
			content += number / 60 + "分";
		} else {
			content += 0 + "分";
		}
		content += number % 60 + "秒";
		SpannableString msp = new SpannableString(content);
		int numberColor = mContext.getResources().getColor(R.color.tishi_bg_color);
		msp.setSpan(new ForegroundColorSpan(numberColor), 0,
				content.indexOf("小时"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), 0, content.indexOf("小时"),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		msp.setSpan(new ForegroundColorSpan(numberColor),
				content.indexOf("小时") + 2, content.indexOf("分"),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), content.indexOf("小时") + 2,
				content.indexOf("分"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		msp.setSpan(new ForegroundColorSpan(numberColor),
				content.indexOf("分") + 1, content.indexOf("秒"),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), content.indexOf("分") + 1,
				content.indexOf("秒"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		v.setText(msp);
	}

	public void setOnClickViewLisener(int viewid, CallBacks callbacks) {
		mCallBacklist.add(callbacks);
		mlistenerId.add(viewid);

	}

	public void setOnLongClickViewLisener(int viewid, CallBacks callbacks) {
		mLongClickCallBacklist.add(callbacks);
		mLongClickListenerId.add(viewid);

	}

	public interface CallBacks {
		void onViewClicked(int positon, int viewId);
	}

	class LongClickItemListener implements View.OnLongClickListener {
		private int m_position;
		private CallBacks mCallBacks;

		LongClickItemListener(int pos, CallBacks callback) {
			m_position = pos;
			mCallBacks = callback;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			Log.i(TAG, "m_position:" + m_position);
			mCallBacks.onViewClicked(m_position, v.getId());
			return true;
		}
	}

	class ItemListener implements View.OnClickListener {
		private int m_position;
		private CallBacks mCallBacks;

		ItemListener(int pos, CallBacks callback) {
			m_position = pos;
			mCallBacks = callback;
		}

		@Override
		public void onClick(View viewId) {
			Log.i(TAG, "m_position:" + m_position);
			mCallBacks.onViewClicked(m_position, viewId.getId());
		}
	}
}
