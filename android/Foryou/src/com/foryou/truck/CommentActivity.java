package com.foryou.truck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.entity.AgentCommentListEntity.AgentComment;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.AgentCommentListJsonParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.PullDownView;
import com.foryou.truck.view.PullDownView.OnItemClickListener;
import com.foryou.truck.view.PullDownView.OnRefreshListener;

public class CommentActivity extends BaseActivity {

	private PullDownView pullDownView;
	private ListView listView;
	private MyListViewAdapter mAdapter;
	private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
	private int offset;
	private TextView mTitle;
	private boolean isRefresh = false;
	private int PAGE_SIZE = 5;
	private int PAGE_INDEX = 1;
	private boolean isTaskRunning = false;
	private String agent_id, name;
	private Context mContext;
	private int[] StarArray = { R.drawable.star1, R.drawable.star2,
			R.drawable.star3, R.drawable.star4, R.drawable.star5 };

	private LinearLayout noContentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.comment_layout);
		noContentView = (LinearLayout) findViewById(R.id.no_content_view);

		mContext = this;
		agent_id = getIntent().getStringExtra("agent_id");
		name = getIntent().getStringExtra("name");

		ShowBackView();
		setTitle(name + "的评价列表");
		InitListView();

	}

	private void InitListView() {
		pullDownView = (PullDownView) findViewById(R.id.feeds);
		pullDownView.init();
		pullDownView.setFooterView(R.layout.footer_item);

		pullDownView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getComentList(false);

			}
		});
		pullDownView.showFooterView(false);
		listView = pullDownView.getListView();
		mAdapter = new MyListViewAdapter(this, adapterlist,
				R.layout.comment_list_item, new String[] { "danhao", "place",
						"comment_content", "star" }, new int[] { R.id.danhao,
						R.id.place, R.id.comment_content, R.id.star }, false);

		listView.setAdapter(mAdapter);
		pullDownView
				.setPullDownViewOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
					}
				});

		getComentList(false);
	}

	private void initData() {
		for (int i = 0; i < mParser.entity.data.list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			AgentComment mComment = mParser.entity.data.list.get(i);
			map.put("danhao", "单号：  " + mComment.order_sn);
			map.put("place", mComment.start_place + "  至  "
					+ mComment.end_place);
			map.put("comment_content", mComment.c2a_comment);
			map.put("star", StarArray[4]);
			if (Constant.isNumeric(mComment.c2a_level)) {
				if (Integer.valueOf(mComment.c2a_level) > 0
						&& Integer.valueOf(mComment.c2a_level) <= 5) {
					map.put("star",
							StarArray[Integer.valueOf(mComment.c2a_level) - 1]);
				}
			}
			adapterlist.add(map);
		}
		mAdapter.notifyDataSetChanged();
	}

	private void getComentList(boolean getMore) {
		if (isTaskRunning) {
			Log.i("aa", "getComentListTask task is not finish ....");
			return;
		} else {
			if (getMore) {
				PAGE_INDEX += 1;
				isRefresh = true;
				listView.setOnScrollListener(null);
			} else {
				PAGE_INDEX = 1;
			}
			new getCommentListTask().execute();
		}
	}

	private AgentCommentListJsonParser mParser;

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
				getComentList(true);
			} else {

			}
		}

	};

	public class getCommentListTask extends
			AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mParser = new AgentCommentListJsonParser();
			parmas = new HashMap<String, String>();
			parmas.put("page", "" + PAGE_INDEX);
			parmas.put("pagesize", "" + PAGE_SIZE);
			parmas.put("agent_id", agent_id);
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
				if (mParser.entity.status.equals("Y")) {
					if (!isRefresh) {
						// 正常刷新加载
						adapterlist.clear();
						initData();
					} else {
						// 加载更多
						isRefresh = false;
						initData();
					}
					if (mParser.entity.data.list.size() == PAGE_SIZE) {
						listView.setOnScrollListener(scorllListener);
						pullDownView.showFooterView(true);
					} else {
						listView.setOnScrollListener(null);
						pullDownView.showFooterView(false);
					}
				} else {
					ToastUtils.toast(mContext, mParser.entity.msg);
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
			if (adapterlist.size() == 0) {
				noContentView.setVisibility(android.view.View.VISIBLE);
				((TextView) noContentView.findViewById(R.id.nocontent_text))
						.setText("还没有评价哦～");
			} else {
				noContentView.setVisibility(android.view.View.GONE);
			}
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.CommentAgentList(mContext, parmas, mParser);

			return msg.what;
		}

	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub

	}

}
