package com.foryou.truck;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.foryou.truck.entity.UserContractEntity;
import com.foryou.truck.entity.UserContractEntity.ContractInfo;
import com.foryou.truck.entity.UserContractEntity.SenderContraceInfo;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;

public class GetSenderInfoActivity extends BaseActivity {
	private Button mSaveBtn;
	private EditText mSenderName, mSenderPhone2, mSenderAddress;
	private AutoCompleteTextView mSenderPhone;
	private Context mContext;
	private UserContractEntity entity;
	private TextView mBasePlace;
	private String hd_start_place;
	private String TAG = "GetSenderInfoActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_sender_info);

		mContext = this;
		ShowBackView();
		setTitle("填写发货人信息");
		Bundle mBundle = this.getIntent().getExtras();
		mSenderPhone = (AutoCompleteTextView) findViewById(R.id.sender_phone);
		mSenderPhone.setText(mBundle.getString("sender_mobile"));
		mSenderPhone.setOnFocusChangeListener(focusChangeListener);

		mSenderName = (EditText) findViewById(R.id.sender_name);
		mSenderName.setText(mBundle.getString("sender_name"));

		mSenderPhone2 = (EditText) findViewById(R.id.sender_phone2);
		mSenderPhone2.setText(mBundle.getString("sender_mobile2"));

		mSenderAddress = (EditText) findViewById(R.id.sender_address);
		mSenderAddress.setText(mBundle.getString("sender_address"));

		mSaveBtn = (Button) findViewById(R.id.save_btn);
		mSaveBtn.setOnClickListener(this);

		mBasePlace = (TextView) findViewById(R.id.base_location);
		mBasePlace.setText(mBundle.getString("start_place"));

		hd_start_place = mBundle.getString("hd_start_place");
		initAutoCompleteTextViewData();
	}

	private void initAutoCompleteTextViewData() {
		entity = SharePerfenceUtil.getUserConTactData(mContext);

		String[] hisArrays = new String[entity.data.sender.size()];
		for (int i = 0; i < hisArrays.length; i++) {
			hisArrays[i] = entity.data.sender.get(i).mobile + ","
					+ entity.data.sender.get(i).name;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				R.layout.dialog_listitem, R.id.text, hisArrays);
		mSenderPhone.setAdapter(adapter);

		mSenderPhone
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						List<SenderContraceInfo> list = entity.data.sender;
						int index;
						String selectContent = (String) parent
								.getItemAtPosition(position);
						Log.i("GetSenderInfoActivity", "selectMobile:"
								+ selectContent);
						for (index = 0; index < list.size(); index++) {
							String listContent = list.get(index).mobile + ","
									+ list.get(index).name;
							Log.i("GetSenderInfoActivity", "listMobile:"
									+ listContent);
							if (selectContent.equals(listContent)) {
								Log.i("GetSenderInfoActivity",
										"list.get(index).name:"
												+ list.get(index).name);
								break;
							}
						}
						if (index != list.size()) {
							mSenderPhone.setText(list.get(index).mobile);
							mSenderName.setText(list.get(index).name);
							mSenderPhone2.setText(list.get(index).mobile2);
							Log.i(TAG,"list.get(index).hd_start_place:"+list.get(index).hd_start_place
									+",hd_start_place:"+hd_start_place);
							if (list.get(index).hd_start_place
									.equals(hd_start_place)) {
								mSenderAddress.setText(list.get(index).address);
							}
						}
					}

				});
	}

	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			Log.i("GetSenderInfoActivity", "hasFocus:" + hasFocus);
			if (hasFocus == false) {
				List<SenderContraceInfo> list = entity.data.sender;
				for (int index = 0; index < list.size(); index++) {
					if (list.get(index).mobile.equals(mSenderPhone.getText()
							.toString())) {
						mSenderName.setText(list.get(index).name);
						mSenderPhone2.setText(list.get(index).mobile2);
						if (list.get(index).hd_start_place
								.equals(hd_start_place)) {
							mSenderAddress.setText(list.get(index).address);
						}
						break;
					}
				}
			}
		}

	};

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.save_btn:
			String sendphone = mSenderPhone.getText().toString();
			if (sendphone.equals("")) {
				ToastUtils.toast(this, "发货人手机号不能为空");
				return;
			}
			if (!Tools.isNumeric(sendphone) || !sendphone.startsWith("1")) {
				ToastUtils.toast(this, "发货人号码格式不正确!");
				return;
			}
			if (sendphone.length() < 11) {
				ToastUtils.toast(this, "发货人号码长度不正确");
				return;
			}

			if (mSenderName.getText().toString().equals("")) {
				ToastUtils.toast(this, "请输入发货人姓名");
				return;
			}
			if (mSenderAddress.getText().toString().equals("")) {
				ToastUtils.toast(this, "请输入发货人详细地址");
				return;
			}

			Intent intent = new Intent();
			intent.putExtra("sender_name", mSenderName.getText().toString());
			intent.putExtra("sender_mobile", mSenderPhone.getText().toString());
			intent.putExtra("sender_mobile2", mSenderPhone2.getText()
					.toString());
			intent.putExtra("sender_address", mSenderAddress.getText()
					.toString());
			this.setResult(Constant.GET_SENDER_CODE, intent);
			finish();
			break;
		}
	}
}
