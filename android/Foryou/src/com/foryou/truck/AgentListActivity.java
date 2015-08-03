package com.foryou.truck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.adapter.MyListViewAdapter.CallBacks;
import com.foryou.truck.entity.AgentListEntity.AgentInfo;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.AgentListJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.PullDownView;
import com.foryou.truck.view.PullDownView.OnItemClickListener;
import com.foryou.truck.view.PullDownView.OnRefreshListener;

public class AgentListActivity extends BaseActivity {
	private PullDownView pullDownView;
	private ListView listView;
	private MyListViewAdapter mAdapter;
	private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
	private int offset;
	private TextView mTitle;
	private Context mContext;
	private RelativeLayout mBackView;
	private String OrderId;
	private String TAG = "AgentListActivity";

	private void InitData() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < mAgentListParser.entity.data.list.size(); i++) {
			AgentInfo mInfo = mAgentListParser.entity.data.list.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mCover", mInfo.photo);
			map.put("name", "联系" + mInfo.name);
			map.put("chengdan_m", mInfo.order_m + "单");
			map.put("chengdan_t", mInfo.order_a + "单");
			map.put("pingjia", mInfo.comment + "条");
			if (Integer.valueOf(mInfo.comment) <= 0) {
				map.put("chakan", false);
			} else {
				map.put("chakan", true);
			}
			map.put("baojia", "¥" + mInfo.price);
			map.put("start_place", mAgentListParser.entity.data.start_place);
			map.put("hd_start_place",mAgentListParser.entity.data.hd_start_place);
			map.put("end_place", mAgentListParser.entity.data.end_place);
			map.put("hd_end_place", mAgentListParser.entity.data.hd_end_place);
			map.put("agent_id", mInfo.agent_id);
			map.put("id", mInfo.id);
			if (mInfo.remark == null) {
				mInfo.remark = "无";
			} else {
				if (mInfo.remark.equals("")) {
					mInfo.remark = "无";
				}
			}
			map.put("baojia_remark", mInfo.remark);
			Long remainTime = (Long) (Long.valueOf(mInfo.expire_time) - System
					.currentTimeMillis() / 1000);
			Log.i("aa", "remainTime:" + remainTime);
			// if (remainTime <= 0) {
			// map.put("remain_time_layout", false);
			// } else {
			map.put("remain_time_layout", true);
			// }
			map.put("expire_time", mInfo.expire_time);
			map.put("reopen", mInfo.reopen);
			map.put("phone_number", mInfo.mobile);

			map.put("remain_time", remainTime);
			map.put("xinyi", mInfo.credit_point);
			map.put("pay_type", mAgentListParser.entity.data.pay_type.key);
			// map.put("remain_time","03分05秒");

			adapterlist.add(map);
		}
		mAdapter.notifyDataSetChanged();
		Log.i(TAG, "InitData ........");
		mTimeDecreaseHandle.removeMessages(DECREASE_TIME);
		mTimeDecreaseHandle.sendEmptyMessageDelayed(DECREASE_TIME, 1000);
	}

	Handler mTimeDecreaseHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case REFLESH:
				getAgentListTask(false);
				mTimeDecreaseHandle.removeMessages(REFLESH);
				mTimeDecreaseHandle.sendEmptyMessageDelayed(REFLESH, 10 * 1000);
				break;
			case DECREASE_TIME:
				mAdapter.notifyDataSetChanged();
				mTimeDecreaseHandle.removeMessages(DECREASE_TIME);
				mTimeDecreaseHandle
						.sendEmptyMessageDelayed(DECREASE_TIME, 1000);
				break;
			}
		}
	};
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
				getAgentListTask(true);
			} else {

			}
		}

	};

	private void ShowAlertDialog(final int positon) {
		AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext,
				AlertDialog.THEME_HOLO_LIGHT);
		mDialog.setTitle("尝试下单");
		String name = (String) adapterlist.get(positon).get("name");
		String price = (String) adapterlist.get(positon).get("baojia");
		mDialog.setMessage("确定请求询问经纪人" + name + "的" + price + "元报价是否可以下单？");
		mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				QuoteReopenTask(positon);
			}

		});
		mDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}

		});
		mDialog.show();
	}

	private void InitListView() {
		pullDownView = (PullDownView) findViewById(R.id.feeds);
		pullDownView.init();
		pullDownView.setFooterView(R.layout.footer_item);

		pullDownView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				Log.i(TAG, "onRefresh .......");
				if (isTaskRunning) {
					pullDownView.notifyRefreshComplete();
				} else {
					getAgentListTask(false);
				}
			}
		});

		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("经纪人报价列表");

		// pullDownView.setPullDownEnabled(false);
		pullDownView.showFooterView(false);
		listView = pullDownView.getListView();

		listView.setDividerHeight(0);
		mAdapter = new MyListViewAdapter(this, adapterlist,
				R.layout.agent_list_item, new String[] { "mCover", "name",
						"chengdan_m", "chengdan_t", "baojia", "remain_time",
						"remain_time_layout", "xinyi", "baojia_remark",
						"pingjia", "chakan" }, new int[] { R.id.mCover,
						R.id.name, R.id.chengdan_m, R.id.chengdan_t,
						R.id.baojia, R.id.remain_time, R.id.remain_time_layout,
						R.id.xinyi, R.id.baojia_remark, R.id.pingjia,
						R.id.chakan }, true);

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

		mAdapter.setOnClickViewLisener(R.id.contact_layout, new CallBacks() {
			@Override
			public void onViewClicked(final int positon, int viewId) {
				// TODO Auto-generated method stub
				// ToastUtils.toast(mContext, "尝试下单");
				Constant.GotoDialPage(mContext,
						(String) adapterlist.get(positon).get("phone_number"));
			}
		});

		/*
		 * mAdapter.setOnClickViewLisener(R.id.phone_number, new CallBacks() {
		 * 
		 * @Override public void onViewClicked(int positon, int viewId) { //
		 * TODO Auto-generated method stub // ToastUtils.toast(mContext,
		 * "尝试下单"); GotoDialPage((String) adapterlist.get(positon).get(
		 * "phone_number"));
		 * 
		 * } });
		 */

		mAdapter.setOnClickViewLisener(R.id.confirm_btn, new CallBacks() {
			@Override
			public void onViewClicked(int positon, int viewId) {
				// TODO Auto-generated method stub
				String expire_time = (String) adapterlist.get(positon).get(
						"expire_time");
				Long remainTime = (Long) (Long.valueOf(expire_time) - System
						.currentTimeMillis() / 1000);
				if (remainTime <= 0) {
					String reopen = (String) adapterlist.get(positon).get(
							"reopen");
					if (reopen.equals("0")) {
						ShowAlertDialog(positon);
					}
				} else {
					Intent intent = new Intent(mContext, BeginSendProduct.class);
					intent.putExtra("order_id", OrderId);
					intent.putExtra("agent_id",
							(String) adapterlist.get(positon).get("agent_id"));
					intent.putExtra("mCover", (String) adapterlist.get(positon)
							.get("mCover"));
					intent.putExtra("name", (String) adapterlist.get(positon)
							.get("name"));
					intent.putExtra("xinyi", (String) adapterlist.get(positon)
							.get("xinyi"));
					intent.putExtra("chengdan_t",
							(String) adapterlist.get(positon).get("chengdan_t"));
					intent.putExtra("chengdan_m",
							(String) adapterlist.get(positon).get("chengdan_m"));
					intent.putExtra("pingjia", (String) adapterlist
							.get(positon).get("pingjia"));
					intent.putExtra("baojia", (String) adapterlist.get(positon)
							.get("baojia"));
					intent.putExtra("pay_type", (String) adapterlist.get(positon)
							.get("pay_type"));
					intent.putExtra(
							"phone_number",
							(String) adapterlist.get(positon).get(
									"phone_number"));
					intent.putExtra("expire_time",
							(String) adapterlist.get(positon)
									.get("expire_time"));
					intent.putExtra("start_place",
							(String) adapterlist.get(positon)
									.get("start_place"));
					intent.putExtra("end_place",
							(String) adapterlist.get(positon).get("end_place"));
					
					intent.putExtra("hd_start_place",
							(String) adapterlist.get(positon).get("hd_start_place"));
					intent.putExtra("hd_end_place",
							(String) adapterlist.get(positon).get("hd_end_place"));
					
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
					}
				});
	}

	private final int REFLESH = 1;
	private final int DECREASE_TIME = 2;
	public AgentListJsonParser mAgentListParser;
	private boolean isTaskRunning = false;
	private boolean isRefresh = false;
	private int PAGE_SIZE = 5;
	private int PAGE_INDEX = 1;

	public class getAgentListAsynTask extends
			AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mAgentListParser = new AgentListJsonParser();
			parmas = new HashMap<String, String>();
			parmas.put("page", "" + PAGE_INDEX);
			parmas.put("order_id", OrderId);
			isTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pullDownView.notifyRefreshComplete();
			cancelProgressDialog();
			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mAgentListParser.entity.status.equals("Y")) {
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
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				listView.setOnScrollListener(null);
				pullDownView.showFooterView(false);
				break;
			}
			isTaskRunning = false;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.QuoteAgentList(mContext, parmas,
					mAgentListParser);

			Log.i(TAG, "" + msg.obj);
			return msg.what;
		}

	}

	private void getAgentListTask(boolean getMore) {
		if (isTaskRunning) {
			Log.i(TAG, "getAgentListTask task is not finish ....");
			return;
		} else {
			if (getMore) {
				PAGE_INDEX += 1;
				isRefresh = true;
				listView.setOnScrollListener(null);
			} else {
				PAGE_INDEX = 1;
			}
			new getAgentListAsynTask().execute();
		}
	}

	private void QuoteReopenTask(int position) {
		if (isReopenTaskRunning) {
			Log.i(TAG, "QuoteReopenTask is not finish ...");
			return;
		}
		new QuoteReopenAsynTask().execute(position);
	}

	private SimpleJasonParser mSimpleParser;
	boolean isReopenTaskRunning = false;

	public class QuoteReopenAsynTask extends
			AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mSimpleParser = new SimpleJasonParser();
			parmas = new HashMap<String, String>();
			parmas.put("order_id", OrderId);
			isReopenTaskRunning = true;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Log.i(TAG, "params[0]:" + params[0]);
			parmas.put("agent_id",
					(String) adapterlist.get(params[0]).get("agent_id"));
			parmas.put("push_id", (String) adapterlist.get(params[0]).get("id"));
			Message msg;
			msg = MLHttpConnect.QuoteReopen(mContext, parmas, mSimpleParser);
			Log.i(TAG, "" + msg.obj);
			return msg.what;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			cancelProgressDialog();
			super.onPostExecute(result);
			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mSimpleParser.entity.status.equals("Y")) {
					Toast.makeText(mContext,
							"已成功给经纪人发送尝试下单需求,当经纪人激活报价后,会及时通知您~",
							Toast.LENGTH_LONG).show();
					getAgentListTask(false);

				} else {
					ToastUtils.toast(mContext, mSimpleParser.entity.msg);
				}
				break;
			case MLHttpConnect2.FAILED:
				ToastUtils.toast(mContext, "尝试下单失败");
				break;
			}
			isReopenTaskRunning = false;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getAgentListTask(false);
		// mTimeDecreaseHandle.removeMessages(REFLESH);
		mTimeDecreaseHandle.sendEmptyMessageDelayed(REFLESH, 10 * 1000);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mTimeDecreaseHandle.removeMessages(REFLESH);
		mTimeDecreaseHandle.removeMessages(DECREASE_TIME);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_query_list);
		mContext = this;

		mBackView = (RelativeLayout) findViewById(R.id.back_view);
		mBackView.setVisibility(android.view.View.VISIBLE);
		mBackView.setOnClickListener(this);

		OrderId = getIntent().getStringExtra("order_id");
		InitListView();
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.back_view:
			finish();
			break;
		}
	}

}
