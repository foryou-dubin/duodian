package com.foryou.truck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.entity.CommonConfigEntity.BaseKeyValue;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.ToastUtils;

public class GetBaoxianActivity extends BaseActivity {

	public static List<BaseKeyValue> insurance_type;
	private TextView mGetBaoxianType;
	private AlertDialog mProductDialog;
	private String mBaoxianKey = "";
	private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
	private Button mSaveBtn;
	private EditText mBaoxianPerson,mBaoxianCount,mBaoxianPackage;
	private TextView mNotUseBaoxian;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.get_baoxian);

		ShowBackView();
		setTitle("填写保险信息");
		
		Intent intent = this.getIntent();
		mGetBaoxianType = (TextView) findViewById(R.id.product_type);
		mGetBaoxianType.setOnClickListener(this);
		
		mSaveBtn = (Button)findViewById(R.id.save_btn);
		mSaveBtn.setOnClickListener(this);
		
		mBaoxianPerson = (EditText)findViewById(R.id.baoxian_name);
		mBaoxianPerson.setText(intent.getStringExtra("insurance_name"));
		
		mBaoxianCount = (EditText)findViewById(R.id.product_number);
		mBaoxianCount.setText(intent.getStringExtra("insurance_num"));
		
		mBaoxianPackage = (EditText)findViewById(R.id.baozhuang);
		mBaoxianPackage.setText(intent.getStringExtra("insurance_package"));
		
		mGetBaoxianType.setText(intent.getStringExtra("insurance_value"));
		mBaoxianKey = intent.getStringExtra("insurance_type");
		
		mNotUseBaoxian = (TextView)findViewById(R.id.not_use_baoxian);
		mNotUseBaoxian.setOnClickListener(this);
	}

	private OnItemClickListener mDialogItemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			mGetBaoxianType.setText(insurance_type.get(position).value);
			mBaoxianKey = insurance_type.get(position).key;
			mProductDialog.dismiss();
		}

	};

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.product_type:
			if (mProductDialog == null) {
				mProductDialog = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT).create();
				adapterlist.clear();
				for (int i = 0; i < insurance_type.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("text",insurance_type.get(i).value);
					adapterlist.add(map);
				}

				ListView list = new ListView(this);
				MyListViewAdapter mAdapter = new MyListViewAdapter(this, adapterlist,
				R.layout.dialog_listitem, new String[] { "text" },
				new int[] { R.id.text}, false);
				list.setOnItemClickListener(mDialogItemListener);
				list.setAdapter(mAdapter);
				
				mProductDialog.setView(list);
			}
			mProductDialog.show();
			int width = ScreenInfo.getScreenInfo(this).widthPixels;
			int height = ScreenInfo.getScreenInfo(this).heightPixels;
			mProductDialog.getWindow().setLayout(width - 40, height - 500);
			break;
		case R.id.save_btn:
			if(mBaoxianPerson.getText().toString().equals("")){
				ToastUtils.toast(this, "请输入被保险人");
				return;
			}
			if(mBaoxianCount.getText().toString().equals("")){
				ToastUtils.toast(this, "请输入货物件数");
				return;
			}
			if(mBaoxianPackage.getText().toString().equals("")){
				ToastUtils.toast(this, "请输入货物包装");
				return;
			}
			if(mBaoxianKey.equals("")){
				ToastUtils.toast(this, "请选择保险类别");
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("insurance_name", mBaoxianPerson.getText().toString());
			intent.putExtra("insurance_num", mBaoxianCount.getText().toString());
			intent.putExtra("insurance_package", mBaoxianPackage.getText().toString());
			intent.putExtra("insurance_type", mBaoxianKey);
			intent.putExtra("insurance_value", mGetBaoxianType.getText().toString());
			this.setResult(Constant.GET_BAOXIAN_CODE, intent);
			finish();
			break;
		case R.id.not_use_baoxian:
			this.setResult(Constant.NO_BAOXIAN_CODE);
			finish();
			break;
		}
	}

}
