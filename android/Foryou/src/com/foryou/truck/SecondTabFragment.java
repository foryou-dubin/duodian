package com.foryou.truck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kymjs.aframe.ui.fragment.BaseFragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.AgentListActivity.getAgentListAsynTask;
import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.adapter.MyListViewAdapter.CallBacks;
import com.foryou.truck.entity.QuoteListEntity.QuoteContent;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.OrderDetailJsonParser;
import com.foryou.truck.parser.QuoteListJsonParser;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.PullDownView;
import com.foryou.truck.view.PullDownView.OnItemClickListener;
import com.foryou.truck.view.PullDownView.OnRefreshListener;

public class SecondTabFragment extends BaseFragment {
	private PullDownView pullDownView;
	private ListView listView;
	private MyListViewAdapter mAdapter;
	private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
	private int offset;
	private TextView mTitle;
	private TabActivity mContext;
	private boolean isRefresh = false;
	private int PAGE_SIZE = 5;
	private int PAGE_INDEX = 1;
	private String TAG = "SecondTabFragment";

	//private boolean beginToRefresh = false; // 倒计时刷新和下拉刷新

	private View mRootView = null;

	private LinearLayout noContentView;

	OnScrollListener scorllListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			Log.i("aa", "firstVisibleItem" + firstVisibleItem
					+ "visibleItemCount" + visibleItemCount + "totalItemCount"
					+ totalItemCount);
			if (((firstVisibleItem + visibleItemCount) == totalItemCount)
					&& !isRefresh) {
				getQuoteList(true);
			} else {

			}
		}

	};

	private void InitData() {
		// adapterlist.clear();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < mQuoteListParser.entity.data.list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			QuoteContent mContent = mQuoteListParser.entity.data.list.get(i);
			map.put("order_number", "" + mContent.order_sn);
			map.put("order_id", mContent.id);
			map.put("order_status", mContent.status.text);
			map.put("send_time",
					sdf.format(new Date(
							Long.valueOf(mContent.goods_loadtime) * 1000))
							+ " " + mContent.goods_loadtime_desc);
			map.put("product_name", mContent.goods_name);
			map.put("place", mContent.start_place + "  >  "
					+ mContent.end_place);
			map.put("quote_n", "" + mContent.quote_n);
			map.put("quote_min", "" + mContent.quote_min);
			adapterlist.add(map);
		}

		mAdapter.notifyDataSetChanged();
	}

	QuoteListJsonParser mQuoteListParser;
	boolean isTaskRunning = false;
	public class getQuoteListTask extends
			AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mContext.showProgressDialog();
			mQuoteListParser = new QuoteListJsonParser();
			parmas = new HashMap<String, String>();
			parmas.put("page", "" + PAGE_INDEX);
			isTaskRunning = true;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.QuoteList2(mContext, parmas, mQuoteListParser);
			return msg.what;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pullDownView.notifyRefreshComplete();
			mContext.cancelProgressDialog();
			
			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mQuoteListParser.entity.status.equals("Y")) {
					//String result = (String) msg.obj;
					//Log.i("aa", "result:" + result);
					if (!isRefresh) {// 正常刷新加载
						adapterlist.clear();
						InitData();
					}else {// 加载更多
						isRefresh = false;
						InitData();
					}

					if (mQuoteListParser.entity.data.list.size() == PAGE_SIZE) {
						listView.setOnScrollListener(scorllListener);
						pullDownView.showFooterView(true);
					} else {
						listView.setOnScrollListener(null);
						pullDownView.showFooterView(false);
					}
				} else {
					listView.setOnScrollListener(null);
					pullDownView.showFooterView(false);
					ToastUtils.toast(mContext, mQuoteListParser.entity.msg);
				}
				break;
			case MLHttpConnect2.FAILED:
				listView.setOnScrollListener(null);
				pullDownView.showFooterView(false);
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			if (adapterlist.size() == 0) {
				noContentView.setVisibility(android.view.View.VISIBLE);
				((TextView) noContentView.findViewById(R.id.nocontent_text))
						.setText("还没有询价哦～");
			} else {
				noContentView.setVisibility(android.view.View.GONE);
			}
			isTaskRunning = false;
		}
	}
	
	private void getQuoteList(boolean getMore) {
		if (isTaskRunning) {
			Log.i(TAG, "getQuoteList task is not finish ....");
			return;
		} else {
			if (getMore) {
				PAGE_INDEX += 1;
				isRefresh = true;
				listView.setOnScrollListener(null);
			} else {
				PAGE_INDEX = 1;
			}
			new getQuoteListTask().execute();
		}
	}

	private void InitListView(View view) {
		pullDownView = (PullDownView) view.findViewById(R.id.feeds);
		pullDownView.init();
		pullDownView.setFooterView(R.layout.footer_item);

		pullDownView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (isTaskRunning) {
					pullDownView.notifyRefreshComplete();
				} else {
					getQuoteList(false);
				}
			}
		});
		// pullDownView.setPullDownEnabled(false);
		pullDownView.showFooterView(false);
		listView = pullDownView.getListView();
		mAdapter = new MyListViewAdapter(mContext, adapterlist,
				R.layout.query_list_item, new String[] { "order_number",
						"order_status", "send_time", "product_name", "place",
						"quote_n", "quote_min" }, new int[] {
						R.id.order_number, R.id.order_status, R.id.send_time,
						R.id.product_name, R.id.place }, false);
		mAdapter.setOnClickViewLisener(R.id.baojia_layout, new CallBacks() {
			@Override
			public void onViewClicked(int positon, int viewId) {
				// TODO Auto-generated method stub
				String mBaojiaNum = (String) adapterlist.get(positon).get(
						"quote_n");
				String mOrderId = (String) adapterlist.get(positon).get(
						"order_id");
				if (Integer.valueOf(mBaojiaNum) > 0) {
					Intent intent = new Intent(mContext,
							AgentListActivity.class);
					intent.putExtra("order_id", mOrderId);
					startActivity(intent);
				}
			}
		});
		listView.setAdapter(mAdapter);
		pullDownView
				.setPullDownViewOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext,
								OrderDetail2Activity.class);
						intent.putExtra(
								"order_id",
								(String) adapterlist.get(position).get(
										"order_id"));
						intent.putExtra(
								"order_sn",
								(String) adapterlist.get(position).get(
										"order_number"));
						startActivity(intent);
					}
				});
	}

	private Handler mRefleshHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.i(TAG, "mRefleshHandler ......");
			getQuoteList(false);
			mRefleshHandler.sendEmptyMessageDelayed(0, 15 * 1000);
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getQuoteList(false);
		mRefleshHandler.sendEmptyMessageDelayed(0, 15 * 1000);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mRefleshHandler.removeMessages(0);
	}

	@Override
	protected View inflaterView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle) {
		// TODO Auto-generated method stub
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.my_query_list, null);
			mTitle = (TextView) mRootView.findViewById(R.id.title);
			mTitle.setText("我的询价");
			mContext = (TabActivity) getActivity();

			noContentView = (LinearLayout) mRootView
					.findViewById(R.id.no_content_view);

			InitListView(mRootView);
		} else {
			ViewGroup viewgroup = (ViewGroup) mRootView.getParent();
			if (viewgroup != null) {
				viewgroup.removeView(mRootView);
			}
		}
		return mRootView;
	}

}
