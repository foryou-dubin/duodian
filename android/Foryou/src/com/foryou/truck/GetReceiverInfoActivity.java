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
import com.foryou.truck.entity.UserContractEntity.ReceiverContraceInfo;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;

public class GetReceiverInfoActivity extends BaseActivity {
	private Button mSaveBtn;
	private EditText mReceiverName, mReceiverPhone2, mReceiverAddress;
	private AutoCompleteTextView mReceiverPhone;
	private Context mContext;
	private UserContractEntity entity;
	private TextView mBasePlace;
	private String hd_end_place;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_receiver_info);
		mContext = this;

		ShowBackView();
		setTitle("填写收货人信息");
		Bundle mBundle = this.getIntent().getExtras();
		mReceiverPhone = (AutoCompleteTextView) findViewById(R.id.receiver_phone);
		mReceiverPhone.setOnFocusChangeListener(focusChangeListener);
		mReceiverPhone.setText(mBundle.getString("receiver_mobile"));

		mReceiverName = (EditText) findViewById(R.id.receiver_name);
		mReceiverName.setText(mBundle.getString("receiver_name"));

		mReceiverPhone2 = (EditText) findViewById(R.id.receiver_phone2);
		mReceiverPhone2.setText(mBundle.getString("receiver_mobile2"));

		mReceiverAddress = (EditText) findViewById(R.id.receiver_address);
		mReceiverAddress.setText(mBundle.getString("receiver_address"));

		mSaveBtn = (Button) findViewById(R.id.save_btn);
		mSaveBtn.setOnClickListener(this);

		mBasePlace = (TextView) findViewById(R.id.base_location);
		mBasePlace.setText(mBundle.getString("end_place"));

		hd_end_place = mBundle.getString("hd_end_place");
		initAutoCompleteTextViewData();
	}

	private void initAutoCompleteTextViewData() {
		entity = SharePerfenceUtil.getUserConTactData(mContext);

		String[] hisArrays = new String[entity.data.receiver.size()];
		for (int i = 0; i < hisArrays.length; i++) {
			hisArrays[i] = entity.data.receiver.get(i).mobile + ","
					+ entity.data.receiver.get(i).name;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				R.layout.dialog_listitem, R.id.text, hisArrays);
		mReceiverPhone.setAdapter(adapter);

		mReceiverPhone
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						List<ReceiverContraceInfo> list = entity.data.receiver;
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
							mReceiverPhone.setText(list.get(index).mobile);
							mReceiverName.setText(list.get(index).name);
							mReceiverPhone2.setText(list.get(index).mobile2);
							if (list.get(index).hd_end_place
									.equals(hd_end_place)) {
								mReceiverAddress.setText(list.get(index).address);
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
				List<ReceiverContraceInfo> list = entity.data.receiver;
				for (int index = 0; index < list.size(); index++) {
					if (list.get(index).mobile.equals(mReceiverPhone.getText()
							.toString())) {
						mReceiverName.setText(list.get(index).name);
						mReceiverPhone2.setText(list.get(index).mobile2);
						if (list.get(index).hd_end_place.equals(hd_end_place)) {
							mReceiverAddress.setText(list.get(index).address);
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
			String recephone = mReceiverPhone.getText().toString();
			if (recephone.equals("")) {
				ToastUtils.toast(this, "收货人手机号不能为空");
				return;
			}
			if (!Tools.isNumeric(recephone) || !recephone.startsWith("1")) {
				ToastUtils.toast(this, "收货人号码格式不正确");
				return;
			}
			if (recephone.length() < 11) {
				ToastUtils.toast(this, "收货人号码长度不正确");
				return;
			}

			if (mReceiverName.getText().toString().equals("")) {
				ToastUtils.toast(this, "请输入收货人姓名");
				return;
			}

			if (mReceiverAddress.getText().toString().equals("")) {
				ToastUtils.toast(this, "请输入收货人详细地址");
				return;
			}

			Intent intent = new Intent();
			intent.putExtra("receiver_name", mReceiverName.getText().toString());
			intent.putExtra("receiver_mobile", mReceiverPhone.getText()
					.toString());
			intent.putExtra("receiver_mobile2", mReceiverPhone2.getText()
					.toString());
			intent.putExtra("receiver_address", mReceiverAddress.getText()
					.toString());
			this.setResult(Constant.GET_RECEIVER_CODE, intent);
			finish();
			break;
		}
	}

}
