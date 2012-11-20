package com.tianxia.app.healthworld.setting;

import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.BaseActivity;
import com.tianxia.lib.baseworld.activity.SettingAboutActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingTabActivity extends BaseActivity {

	private TextView tvAccount;
	private Button btLogin;
	private LinearLayout layoutPassword;
	private LinearLayout layoutFeedback;
	private LinearLayout layoutUpdate;
	private LinearLayout layoutAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_tab_activity);

		tvAccount = (TextView) findViewById(R.id.setting_account);
		btLogin = (Button) findViewById(R.id.setting_login);
		layoutPassword = (LinearLayout) findViewById(R.id.setting_password);
		layoutFeedback = (LinearLayout) findViewById(R.id.setting_feedback);
		layoutUpdate = (LinearLayout) findViewById(R.id.setting_update);
		layoutAbout = (LinearLayout) findViewById(R.id.setting_about);

		btLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingTabActivity.this, "绑定账号", 0).show();
			}
		});
		layoutPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingTabActivity.this, "设置密码", 0).show();
			}
		});
		layoutFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingTabActivity.this, "意见反馈", 0).show();
			}
		});
		layoutUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingTabActivity.this, "检查更新", 0).show();
			}
		});
		layoutAbout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(SettingTabActivity.this, "关于我们", 0).show();
				Intent intent = new Intent(SettingTabActivity.this,
						SettingAboutActivity.class);
				startActivity(intent);
			}
		});
	}
}
