package com.tianxia.app.healthworld.setting;

import org.json.JSONException;
import org.json.JSONObject;

import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.home.HomeApi;
import com.tianxia.app.healthworld.home.HomeTabActivity;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.sync.http.RequestParams;
import com.tianxia.lib.baseworld.utils.NetworkUtils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingsFeedbackActivity extends Activity {
	public static final String TAG = "SettingsFeedbackActivity";

	private EditText feedback;
	private RadioGroup gender;
	private EditText phoneNum;
	private Button submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_feedback_activity);

		feedback = (EditText) findViewById(R.id.settings_feedback_editText);
		feedback.requestFocus();
		gender = (RadioGroup) findViewById(R.id.settings_feedback_radioGroup);
		phoneNum = (EditText) findViewById(R.id.settings_feedback_editText_phoneNum);
		submit = (Button) findViewById(R.id.settings_feedback_submit);

		// gender.setOnCheckedChangeListener(new
		// RadioGroup.OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(RadioGroup group, int checkedId) {
		// // TODO Auto-generated method stub
		// if (checkedId == R.id.settings_feedback_radioButton_male) {
		//
		// } else {
		//
		// }
		// }
		// });
	}

	public void goBack(View v) {
		finish();
	}

	public void submit(View v) {
		if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
			Toast.makeText(SettingsFeedbackActivity.this, "无可用网络连接", 0).show();
			return;
		}
		String feedbackText = feedback.getText().toString().trim();
		int sex = 0;
		String phone = phoneNum.getText().toString().trim();

		if (feedbackText == null || feedbackText.equals("")) {
			Toast.makeText(SettingsFeedbackActivity.this, "意见反馈不可为空", 0).show();
		} else {
			if (gender.getCheckedRadioButtonId() == R.id.settings_feedback_radioButton_female) {
				sex = 1;
			}
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			params.put("params", getCurParams(feedbackText, sex, phone));
			client.post(HomeApi.FEEDBACK_URL, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							submit.setEnabled(false);

						}

						@Override
						public void onSuccess(String content) {
							// TODO Auto-generated method stub
							Toast.makeText(SettingsFeedbackActivity.this,
									"提交成功", 0).show();
							Log.d(TAG, content);
							finish();
						}

						@Override
						public void onFailure(Throwable error) {
							// TODO Auto-generated method stub
							Toast.makeText(SettingsFeedbackActivity.this,
									"提交失败,请重试", 0).show();
						}

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							super.onFinish();
						}
					});
		}
	}

	private String getCurParams(String feedback, int gender, String phoneNum) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("Content", feedback);
			jb.put("ContactWay", phoneNum);
			jb.put("sSex", gender);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "params: " + jb);
		return jb.toString();
	}

	// 参数：Content 内容 需要utf-8编码
	// ContactWay 联系方式
	// sIsMarry 是否已婚（0：否1：是）
	// sSex
}
