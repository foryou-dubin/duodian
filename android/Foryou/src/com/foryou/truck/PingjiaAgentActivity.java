package com.foryou.truck;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.ui.AnnotateUtil;
import org.kymjs.aframe.ui.BindView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.entity.CommentEntity.ComtentData;
import com.foryou.truck.net.MLHttpConnect;
import com.foryou.truck.net.MLHttpConnect2;
import com.foryou.truck.parser.CommentJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.TimeUtils;
import com.foryou.truck.util.ToastUtils;

public class PingjiaAgentActivity extends BaseActivity {

	private String order_id;
	private Context mContext;
	@BindView(id = R.id.danhao)
	private TextView mDanhao;
	@BindView(id = R.id.place)
	private TextView mPlace;
	@BindView(id = R.id.time)
	private TextView mTime;
	@BindView(id = R.id.product_name)
	private TextView mProductName;

	@BindView(id = R.id.agent_name)
	private TextView mAgentComment;
	@BindView(id = R.id.comment_time)
	private TextView mAgentCommentTime;
	@BindView(id = R.id.content)
	private TextView mAgentCommentContent;
	@BindView(id = R.id.a2c_star_img)
	private ImageView mA2cStarImg;

	@BindView(id = R.id.agent_star1, click = true)
	private ImageView mAgentStar1;
	@BindView(id = R.id.agent_star2, click = true)
	private ImageView mAgentStar2;
	@BindView(id = R.id.agent_star3, click = true)
	private ImageView mAgentStar3;
	@BindView(id = R.id.agent_star4, click = true)
	private ImageView mAgentStar4;
	@BindView(id = R.id.agent_star5, click = true)
	private ImageView mAgentStar5;

	@BindView(id = R.id.driver_star1, click = true)
	private ImageView mDriverStar1;
	@BindView(id = R.id.driver_star2, click = true)
	private ImageView mDriverStar2;
	@BindView(id = R.id.driver_star3, click = true)
	private ImageView mDriverStar3;
	@BindView(id = R.id.driver_star4, click = true)
	private ImageView mDriverStar4;
	@BindView(id = R.id.driver_star5, click = true)
	private ImageView mDriverStar5;

	@BindView(id = R.id.pingjia_agent_edit)
	private EditText mAgentEdit;
	@BindView(id = R.id.pingjia_driver_edit)
	private EditText mDriverEdit;

	@BindView(id = R.id.save_btn, click = true)
	private Button mSaveBtn;

	@BindView(id = R.id.agent_comment_layout)
	private RelativeLayout mAgentCommentLayout;
	@BindView(id = R.id.comment_driver_layout)
	private RelativeLayout mDriverCommentLayout;

	private int[] startImgArr = { R.drawable.star1, R.drawable.star2,
			R.drawable.star3, R.drawable.star4, R.drawable.star5 };

	private ImageView[] mAgentStarViewArr, mDriverStarViewArr;
	private int mAgentStarLevel = 0, mDriverStarLevel = 0;
	private boolean driver_array;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.pingjia_agent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ShowBackView();
		setTitle("评价经纪人");

		mAgentStarViewArr = new ImageView[5];
		mAgentStarViewArr[0] = mAgentStar1;
		mAgentStarViewArr[1] = mAgentStar2;
		mAgentStarViewArr[2] = mAgentStar3;
		mAgentStarViewArr[3] = mAgentStar4;
		mAgentStarViewArr[4] = mAgentStar5;

		mDriverStarViewArr = new ImageView[5];
		mDriverStarViewArr[0] = mDriverStar1;
		mDriverStarViewArr[1] = mDriverStar2;
		mDriverStarViewArr[2] = mDriverStar3;
		mDriverStarViewArr[3] = mDriverStar4;
		mDriverStarViewArr[4] = mDriverStar5;

		mContext = this;
		order_id = getIntent().getStringExtra("order_id");
		driver_array = getIntent().getBooleanExtra("driver_array", false);
		if (driver_array) {
			mDriverCommentLayout.setVisibility(android.view.View.VISIBLE);
		} else {
			mDriverCommentLayout.setVisibility(android.view.View.GONE);
		}
		getComment();
	}

	private void initData() {
		ComtentData data = mCommentParser.entity.data;
		if (data.a2c_id.equals("")) {
			mAgentCommentLayout.setVisibility(android.view.View.GONE);
		} else {
			mAgentCommentLayout.setVisibility(android.view.View.VISIBLE);
			mAgentComment.setText("经纪人" + data.agent_name + "评价我");
			mAgentCommentTime.setText(data.a2c_time);
			mAgentCommentContent.setText(data.a2c_comment);
			if (Constant.isNumeric(data.a2c_level)) {
				if ((Integer.valueOf(data.a2c_level) > 0)
						&& (Integer.valueOf(data.a2c_level) <= 5)) {
					mA2cStarImg.setBackgroundResource(startImgArr[Integer
							.valueOf(data.a2c_level) - 1]);
				}
			}
		}
		mDanhao.setText("单号:" + data.order_sn);
		mPlace.setText(data.start_place + " 至 " + data.end_place);
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(data.confirm_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		mTime.setText(re_StrTime);
		mProductName.setText(data.goods_name);

		if (Constant.isNumeric(data.c2a_level)) {
			setAgentStar(mAgentStarViewArr, Integer.valueOf(data.c2a_level));
		}
		mAgentEdit.setText(data.c2a_comment);

		if (driver_array) {
			if (Constant.isNumeric(data.c2d_level)) {
				setAgentStar(mDriverStarViewArr,
						Integer.valueOf(data.c2d_level));
			}
			mDriverEdit.setText(data.c2d_comment);
		}
	}

	private SimpleJasonParser mCommentAddParser;
	private boolean isCommentAddTaskRunning = false;

	private void CommentAdd() {
		if (isCommentAddTaskRunning) {
			Log.i("aa", "CommentAdd task is not finish ....");
			return;
		} else {
			new CommentAddTask().execute();
		}
	}

	public class CommentAddTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mCommentAddParser = new SimpleJasonParser();
			parmas = new HashMap<String, String>();
			parmas.put("order_id", "" + order_id);
			parmas.put("c2a_id", mCommentParser.entity.data.c2a_id);
			parmas.put("c2a_level", "" + mAgentStarLevel);
			parmas.put("c2a_comment", mAgentEdit.getText().toString());
			parmas.put("c2d_id", "" + mCommentParser.entity.data.c2d_id);
			parmas.put("c2d_level", "" + mDriverStarLevel);
			parmas.put("c2d_comment", mDriverEdit.getText().toString());

			isCommentAddTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();

			switch (result) {
			case MLHttpConnect2.SUCCESS:
				ToastUtils.toast(mContext, mCommentAddParser.entity.msg);
				if (mCommentAddParser.entity.status.equals("Y")) {
					finish();
				}
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			isCommentAddTaskRunning = false;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.CommentAdd(mContext, parmas, mCommentAddParser);
			return msg.what;
		}

	}

	private CommentJsonParser mCommentParser;
	private boolean isCommentTaskRunning = false;

	private void getComment() {
		if (isCommentTaskRunning) {
			Log.i("aa", "getComment task is not finish ....");
			return;
		} else {
			new GetCommentTask().execute();
		}
	}

	public class GetCommentTask extends AsyncTask<Integer, Integer, Integer> {
		private Map<String, String> parmas;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
			mCommentParser = new CommentJsonParser();
			parmas = new HashMap<String, String>();
			parmas.put("order_id", "" + order_id);
			isCommentTaskRunning = true;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cancelProgressDialog();

			switch (result) {
			case MLHttpConnect2.SUCCESS:
				if (mCommentParser.entity.status.equals("Y")) {
					initData();
				} else {
					ToastUtils.toast(mContext, mCommentParser.entity.msg);
				}
				break;
			case MLHttpConnect2.FAILED:
				Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			isCommentTaskRunning = false;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Message msg;
			msg = MLHttpConnect.GetCommentContent(mContext, parmas,
					mCommentParser);
			return msg.what;
		}

	}

	private void setAgentStar(ImageView[] arr, int num) {

		if (arr == mAgentStarViewArr) {
			mAgentStarLevel = num;
		} else {
			mDriverStarLevel = num;
		}

		for (int i = 0; i < 5; i++) {
			if (i < num) {
				arr[i].setBackgroundResource(R.drawable.star_haiglight);
			} else {
				arr[i].setBackgroundResource(R.drawable.star);
			}
		}
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.agent_star1:
			setAgentStar(mAgentStarViewArr, 1);
			break;
		case R.id.agent_star2:
			setAgentStar(mAgentStarViewArr, 2);
			break;
		case R.id.agent_star3:
			setAgentStar(mAgentStarViewArr, 3);
			break;
		case R.id.agent_star4:
			setAgentStar(mAgentStarViewArr, 4);
			break;
		case R.id.agent_star5:
			setAgentStar(mAgentStarViewArr, 5);
			break;
		case R.id.driver_star1:
			setAgentStar(mDriverStarViewArr, 1);
			break;
		case R.id.driver_star2:
			setAgentStar(mDriverStarViewArr, 2);
			break;
		case R.id.driver_star3:
			setAgentStar(mDriverStarViewArr, 3);
			break;
		case R.id.driver_star4:
			setAgentStar(mDriverStarViewArr, 4);
			break;
		case R.id.driver_star5:
			setAgentStar(mDriverStarViewArr, 5);
			break;
		case R.id.save_btn:
			if (mAgentStarLevel <= 0) {
				ToastUtils.toast(mContext, "请评价经纪人星级");
				return;
			}
			if (mAgentEdit.getText().toString().length() < 10) {
				ToastUtils.toast(mContext, "对经纪人的评价不能少于10个字符");
				return;
			}
			if (!mDriverEdit.getText().toString().equals("")) {
				if (mDriverEdit.getText().toString().length() < 10) {
					ToastUtils.toast(mContext, "对司机的评价不能少于10个字符");
					return;
				}
			}
			CommentAdd();
			break;
		}
	}

}
