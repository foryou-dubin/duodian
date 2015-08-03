package com.foryou.truck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.OrderListJsonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.view.PullDownView;
import com.foryou.truck.view.PullDownView.OnItemClickListener;
import com.foryou.truck.view.PullDownView.OnRefreshListener;

public class MyOrderListActivity extends BaseActivity {

	private PullDownView pullDownView;
	private ListView listView;
	private MyListViewAdapter mAdapter;
	private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
	private int offset;
	private TextView mTitle;
	private Context mContext;

	private void InitListView() {
		pullDownView = (PullDownView) findViewById(R.id.feeds);
		pullDownView.init();
		pullDownView.setFooterView(R.layout.footer_item);

		pullDownView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				offset = 0;
				pullDownView.notifyRefreshComplete();
				// getData();
			}
		});

		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("我的订单");

		// pullDownView.setPullDownEnabled(false);
		pullDownView.showFooterView(false);
		listView = pullDownView.getListView();

		listView.setDividerHeight(0);
//		mAdapter = new MyListViewAdapter(this, adapterlist,
//				R.layout.order_list_item, new String[] { "driver_name",
//						"driver_number", "line" }, new int[] {
//						R.id.driver_name, R.id.driver_number, R.id.line }, true);
//		mAdapter.setOnClickViewLisener(R.id.trace_table_layout,
//				new CallBacks() {
//
//					@Override
//					public void onViewClicked(int positon, int viewId) {
//						// TODO Auto-generated method stub
//						Intent intent = new Intent(mContext,
//								TableDetailActivity.class);
//						startActivity(intent);
//					}
//
//				});
//		listView.setAdapter(mAdapter);
		pullDownView
				.setPullDownViewOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
					}
				});
		getData();
	}
	
	private OrderListJsonParser parser;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			if (MLHttpConnect2.SUCCESS == msg.what) {
				if (parser.entity.status.equals("Y")) {
					String result = (String) msg.obj;

					Log.i("aa", "result:" + Tools.UnicodeDecode(result));

				} else {
					ToastUtils.toast(mContext, parser.entity.msg);
				}
			} else {
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
			}
			super.handleMessage(msg);
		}

	};

	private void getData() {
		showProgressDialog();
		parser = new OrderListJsonParser();
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("uid", "" + 1);
		parmas.put("page", "" + 1);
		MLHttpConnect.GetOrderList(this, parmas, parser, mHandler);

		adapterlist.clear();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("line", true);
		map.put("driver_name", "高高");
		map.put("driver_number", "13584754125");
		adapterlist.add(map);

		map = new HashMap<String, Object>();
		map.put("line", false);
		map.put("driver_name", "高高");
		map.put("driver_number", "13584754125");
		adapterlist.add(map);

		map = new HashMap<String, Object>();
		map.put("line", false);
		map.put("driver_name", "高高");
		map.put("driver_number", "13584754125");
		adapterlist.add(map);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_query_list);

		mContext = this;
		InitListView();
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub

	}
}
