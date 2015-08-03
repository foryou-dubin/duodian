package com.foryou.truck;

import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ToastUtils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class getDiscountActivity extends BaseActivity {
	
	private EditText mCoupon;
	private Button mSaveBtn;

	private TextView mNotUseBaoxian;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_discount);
		ShowBackView();
		setTitle("填写代金券");

		mCoupon = (EditText) findViewById(R.id.discount_number);
		mCoupon.setText(getIntent().getStringExtra("coupon"));
		mSaveBtn = (Button) findViewById(R.id.save_btn);
		mSaveBtn.setOnClickListener(this);
		
		mNotUseBaoxian = (TextView)findViewById(R.id.not_use_baoxian);
		mNotUseBaoxian.setOnClickListener(this);
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.save_btn:
			if(mCoupon.getText().toString().equals("")){
				ToastUtils.toast(this, "请填写代金券");
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("coupon", mCoupon.getText().toString());
			this.setResult(Constant.GET_COUPON_CODE, intent);
			finish();
			
			break;
		case R.id.not_use_baoxian:
			this.setResult(Constant.NO_DISCOUNT_CODE);
			finish();
			break;
		}
	}

}
