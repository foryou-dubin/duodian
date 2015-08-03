package com.foryou.truck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kymjs.aframe.ui.fragment.BaseFragment;

import android.content.Intent;
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

import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.adapter.MyListViewAdapter.CallBacks;
import com.foryou.truck.entity.OrderListEntity.OrderContent;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.OrderListJsonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.PullDownView;
import com.foryou.truck.view.PullDownView.OnItemClickListener;
import com.foryou.truck.view.PullDownView.OnRefreshListener;

public class ThirdTabFragment extends BaseFragment {
	private TabActivity mContext;
	private PullDownView pullDownView;
	private ListView listView;
	private MyListViewAdapter mAdapter;
	private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
	private int offset;
	private TextView mTitle;

	private View mRootView = null;

	private boolean isRefresh = false;
	private boolean beginToRefresh = false; // 倒计时刷新和下拉刷新
	private int PAGE_SIZE = 5;
	private int PAGE_INDEX = 1;
	
	private LinearLayout noContentView;

	private void InitListView(View view) {
		pullDownView = (PullDownView) view.findViewById(R.id.feeds);
		pullDownView.init();
		pullDownView.setFooterView(R.layout.footer_item);

		pullDownView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (!beginToRefresh) {
					beginToRefresh = true;
					PAGE_INDEX = 1;
					// getData();
					getOrderList();
				}
			}
		});

		// pullDownView.setPullDownEnabled(false);
		pullDownView.showFooterView(false);
		listView = pullDownView.getListView();

		listView.setDividerHeight(0);
		mAdapter = new MyListViewAdapter(mContext, adapterlist,
				R.layout.order_list_item, new String[] { "order_number",
						"status", "send_time", "product_name", "place" },
				new int[] { R.id.order_number, R.id.status, R.id.send_time,
						R.id.product_name, R.id.place }, true);
		mAdapter.setOnClickViewLisener(R.id.baojia_layout, new CallBacks() {

			@Override
			public void onViewClicked(int positon, int viewId) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, OrderDetailActivity.class);
				intent.putExtra("order_id", (String) adapterlist.get(positon)
						.get("order_id"));
				startActivity(intent);
			}
		});
		listView.setAdapter(mAdapter);
		pullDownView
				.setPullDownViewOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
					}
				});
		// getData();
	}

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
				isRefresh = true;
				listView.setOnScrollListener(null);
				PAGE_INDEX += 1;
				getOrderList();
			} else {

			}
		}

	};

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mContext.cancelProgressDialog();
			pullDownView.notifyRefreshComplete();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mQrderListParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i("aa", "result:" + Tools.UnicodeDecode(result));
					if (!isRefresh) {// 正常刷新加载
						adapterlist.clear();
						InitData();
						beginToRefresh = false;
					}else if(beginToRefresh){
						adapterlist.clear();
						InitData();
						beginToRefresh = false;
					} else {// 加载更多
						isRefresh = false;
						InitData();
					}

					if (mQrderListParser.entity.data.list.size() == PAGE_SIZE) {
						listView.setOnScrollListener(scorllListener);
						pullDownView.showFooterView(true);
					} else {
						listView.setOnScrollListener(null);
						pullDownView.showFooterView(false);
					}
				} else {
					listView.setOnScrollListener(null);
					pullDownView.showFooterView(false);
					ToastUtils.toast(mContext, mQrderListParser.entity.msg);
				}
			} else {
				listView.setOnScrollListener(null);
				pullDownView.showFooterView(false);
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
			
			if(adapterlist.size() == 0){
				noContentView.setVisibility(android.view.View.VISIBLE);
				((TextView)noContentView.findViewById(R.id.nocontent_text)).setText("还没有运单哦～");
			}else{
				noContentView.setVisibility(android.view.View.GONE);
			}
		}

	};

	private void InitData() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < mQrderListParser.entity.data.list.size(); i++) {
			OrderContent mOrderContent = mQrderListParser.entity.data.list
					.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("order_number", "" + mOrderContent.order_sn);
			map.put("order_id", "" + mOrderContent.id);
			map.put("status", mOrderContent.status.text);
			map.put("send_time",
					sdf.format(new Date(Long
							.valueOf(mOrderContent.goods_loadtime) * 1000))
							+ " " + mOrderContent.goods_loadtime_desc);

			map.put("product_name", mOrderContent.goods_name);
			map.put("place", mOrderContent.start_place + "  >  "
					+ mOrderContent.end_place);
			adapterlist.add(map);
		}
		mAdapter.notifyDataSetChanged();
	}

	OrderListJsonParser mQrderListParser;

	private void getOrderList() {
		mContext.showProgressDialog();
		mQrderListParser = new OrderListJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("page", "" + PAGE_INDEX);
		MLHttpConnect
				.GetOrderList(mContext, parmas, mQrderListParser, mHandler);
	}

	@Override
	protected View inflaterView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle) {
		// TODO Auto-generated method stub
		mContext = (TabActivity) getActivity();
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.my_query_list, null);
			mTitle = (TextView) mRootView.findViewById(R.id.title);
			mTitle.setText("我的运单");
			
			noContentView =(LinearLayout)mRootView.findViewById(R.id.no_content_view);
			
			InitListView(mRootView);
		} else {
			ViewGroup viewgroup = (ViewGroup) mRootView.getParent();
			if (viewgroup != null) {
				viewgroup.removeView(mRootView);
			}
		}
		PAGE_INDEX = 1;
		getOrderList();
		return mRootView;
	}

}
