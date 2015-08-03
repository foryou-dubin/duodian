package com.foryou.truck;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.umeng.update.UmengUpdateAgent;

public class MyInfoActivity extends BaseActivity {
	private TextView mTitle, mAboutPhone;
	private TextView mCheckUpdate, mCurrentVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_info);

		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("个人中心");

		mCheckUpdate = (TextView) findViewById(R.id.check_update);
		mCheckUpdate.setOnClickListener(this);

//		mCurrentVersion = (TextView) findViewById(R.id.current_version);
//		String versionName = "";
//		try {
//			versionName = getPackageManager().getPackageInfo(getPackageName(),
//					PackageManager.GET_CONFIGURATIONS).versionName;
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		mCurrentVersion.setText(versionName);

//		mAboutPhone = (TextView) findViewById(R.id.about_phone);
//		mAboutPhone.setOnClickListener(this);
	}

	void GotoDialPage() {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ mAboutPhone.getText()));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.check_update:
			UmengUpdateAgent.forceUpdate(this);
			break;
//		case R.id.about_phone:
//			/*
//			 * Intent intent = new Intent(); // 系统默认的action，用来打开默认的电话界面
//			 * intent.setAction(Intent.ACTION_CALL); // 需要拨打的号码
//			 * intent.setData(Uri.parse("tel:" + mAboutPhone.getText()));
//			 * startActivity(intent);
//			 */
//			GotoDialPage();
//			break;
		}
	}
}
