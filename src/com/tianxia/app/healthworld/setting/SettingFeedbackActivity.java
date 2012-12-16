package com.tianxia.app.healthworld.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.BaseActivity;

public class SettingFeedbackActivity extends BaseActivity {

	private String[] sexData = { "男", "女" };
	private String[] marriageData = { "已婚", "恋爱中", "单身" };

	private ImageView mAppBackButton;
	private Button submit;

	private EditText content;
	private EditText age;
	private EditText job;
	private EditText city;
	private EditText phoneNumber;

	private Spinner sex;
	private Spinner marriage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_feedback_activity);

		mAppBackButton = (ImageView) findViewById(R.id.app_back);
		mAppBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		submit = (Button) findViewById(R.id.setting_feedback_submit);

		content = (EditText) findViewById(R.id.setting_feedback_edittext_content);
		age = (EditText) findViewById(R.id.setting_feedback_edittext_age);
		job = (EditText) findViewById(R.id.setting_feedback_edittext_job);
		city = (EditText) findViewById(R.id.setting_feedback_edittext_city);
		phoneNumber = (EditText) findViewById(R.id.setting_feedback_edittext_phonenumber);

		sex = (Spinner) findViewById(R.id.setting_feedback_spinner_sex);
		marriage = (Spinner) findViewById(R.id.setting_feedback_spinner_marriage);

		// 第二个参数表示spinner没有展开前的UI类型
		ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, sexData);
		sex.setAdapter(sexAdapter);
		sexAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<String> marriageAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, marriageData);
		marriage.setAdapter(marriageAdapter);
		marriageAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

}
