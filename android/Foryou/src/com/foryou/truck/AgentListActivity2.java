package com.foryou.truck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.adapter.MyListViewAdapter.CallBacks;
import com.foryou.truck.entity.AgentListEntity.AgentInfo;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.AgentListJsonParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.PullDownView;
import com.foryou.truck.view.PullDownView.OnItemClickListener;
import com.foryou.truck.view.PullDownView.OnRefreshListener;

//已成单经纪人报价列表
public class AgentListActivity2 extends BaseActivity {

	private String OrderId;
	private PullDownView pullDownView;
	private ListView listView;
	private MyListViewAdapter mAdapter;
	private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
	private String TAG = "AgentListActivity2";
	private Context mContext;
	private int PAGE_INDEX = 1;
	private int PAGE_SIZE = 5;
	private boolean isRefresh = false;

	private void InitListView() {
		pullDownView = (PullDownView) findViewById(R.id.feeds);
		pullDownView.init();
		pullDownView.setFooterView(R.layout.footer_item);

		pullDownView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				Log.i(TAG, "onRefresh .......");
				PAGE_INDEX = 1;
				getAgentList();
			}
		});

		// pullDownView.setPullDownEnabled(false);
		pullDownView.showFooterView(false);
		listView = pullDownView.getListView();

		listView.setDividerHeight(0);
		mAdapter = new MyListViewAdapter(this, adapterlist,
				R.layout.order_agent_list_item, new String[] { "mCover",
						"name", "chengdan_m", "chengdan_t", "baojia", "baojia_text",
						"remain_time", "try_order_layout", "xinyi",
						"baojia_remark", "pingjia", "chakan" }, new int[] {
						R.id.mCover, R.id.name, R.id.chengdan_m,
						R.id.chengdan_t, R.id.baojia, R.id.baojia_text,
						R.id.remain_time, R.id.try_order_layout, R.id.xinyi,
						R.id.baojia_remark, R.id.pingjia, R.id.chakan}, true);

		listView.setAdapter(mAdapter);
		
		mAdapter.setOnClickViewLisener(R.id.contact_layout, new CallBacks() {
			@Override
			public void onViewClicked(final int positon, int viewId) {
				// TODO Auto-generated method stub
				// ToastUtils.toast(mContext, "尝试下单");
				Constant.GotoDialPage(mContext,
						(String) adapterlist.get(positon).get("phone_number"));
			}
		});

		mAdapter.setOnClickViewLisener(R.id.chakan, new CallBacks() {
			@Override
			public void onViewClicked(final int positon, int viewId) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, CommentActivity.class);
				intent.putExtra("agent_id", (String) adapterlist.get(positon)
						.get("agent_id"));
				String name = (String) adapterlist.get(positon)
						.get("name");
				intent.putExtra("name", name.substring(2, name.length()));
				startActivity(intent);
			}
		});

		pullDownView
				.setPullDownViewOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
					}
				});
	}

	void GotoDialPage(String num) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private AgentListJsonParser mAgentListParser;

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
				getAgentList();
			} else {

			}
		}

	};

	private void InitData() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < mAgentListParser.entity.data.list.size(); i++) {
			AgentInfo mInfo = mAgentListParser.entity.data.list.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mCover", mInfo.photo);
			map.put("name", "联系"+ mInfo.name);
			map.put("chengdan_m", mInfo.order_m + "单");
			map.put("chengdan_t", mInfo.order_a + "单");
			map.put("pingjia", mInfo.comment + "条");
			if(Integer.valueOf(mInfo.comment) <= 0){
				map.put("chakan", false);
			}else{
				map.put("chakan", true);
			}
			
			map.put("baojia", mInfo.price);
			if (mInfo.remark == null) {
				mInfo.remark = "无";
			} else {
				if (mInfo.remark.equals("")) {
					mInfo.remark = "无";
				}
			}
			map.put("baojia_remark", mInfo.remark);
			map.put("start_place", mAgentListParser.entity.data.start_place);
			map.put("end_place", mAgentListParser.entity.data.end_place);
			map.put("agent_id", mInfo.agent_id);

			map.put("baojia_text", "报价时间:");
			map.put("confirm_btn", false);
			map.put("try_order_layout", false);
			map.put("xinyi", mInfo.credit_point);
			map.put("phone_number", mInfo.mobile);

			String re_StrTime = null;
			long lcc_time = Long.valueOf(mInfo.time);
			re_StrTime = sdf.format(new Date(lcc_time * 1000L));
			map.put("remain_time", re_StrTime);
			adapterlist.add(map);
		}
		mAdapter.notifyDataSetChanged();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			pullDownView.notifyRefreshComplete();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (mAgentListParser.entity.status.equals("Y")) {
					String result = (String) msg.obj;
					Log.i("aa", "result:" + result);
					if (!isRefresh) {// 正常刷新加载
						adapterlist.clear();
						InitData();
					} else {// 加载更多
						isRefresh = false;
						InitData();
					}
					if (mAgentListParser.entity.data.list.size() == PAGE_SIZE) {
						listView.setOnScrollListener(scorllListener);
						pullDownView.showFooterView(true);
					} else {
						listView.setOnScrollListener(null);
						pullDownView.showFooterView(false);
					}
				} else {
					ToastUtils.toast(mContext, mAgentListParser.entity.msg);
					listView.setOnScrollListener(null);
					pullDownView.showFooterView(false);
				}
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				listView.setOnScrollListener(null);
				pullDownView.showFooterView(false);
			}
			super.handleMessage(msg);
		}

	};

	private void getAgentList() {
		showProgressDialog();
		mAgentListParser = new AgentListJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("page", "" + PAGE_INDEX);
		parmas.put("order_id", OrderId);
		MLHttpConnect.OrderQuoteList(mContext, parmas, mAgentListParser,
				mHandler);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.my_query_list);
		ShowBackView();
		setTitle("报价记录");
		mContext = this;

		OrderId = getIntent().getStringExtra("order_id");
		InitListView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getAgentList();
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub

	}

}
