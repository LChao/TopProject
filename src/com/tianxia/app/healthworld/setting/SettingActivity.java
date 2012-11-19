package com.tianxia.app.healthworld.setting;

import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends BaseActivity {

	LinearLayout tv1;
	LinearLayout tv2;
	LinearLayout tv3;
	LinearLayout tv4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);

		tv1 = (LinearLayout) findViewById(R.id.textView1);
		tv2 = (LinearLayout) findViewById(R.id.textView2);
		tv3 = (LinearLayout) findViewById(R.id.textView3);
		tv4 = (LinearLayout) findViewById(R.id.textView4);

		tv1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingActivity.this, "textview 1", 0).show();
			}
		});
		tv2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingActivity.this, "textview 2", 0).show();
			}
		});
		tv3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingActivity.this, "textview 3", 0).show();
			}
		});
		tv4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingActivity.this, "textview 4", 0).show();
			}
		});
	}
}
