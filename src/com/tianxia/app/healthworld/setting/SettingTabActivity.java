package com.tianxia.app.healthworld.setting;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.BaseActivity;
import com.tianxia.lib.baseworld.activity.SettingAboutActivity;
import com.tianxia.lib.baseworld.utils.PreferencesUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingTabActivity extends BaseActivity {

	private static final String PERSONAL = "personalData";
	private static final String PASSWORD = "password";
	private String password;
	private EditText pw;
	private EditText verifyPw;
	private EditText cancelPw;

	private TextView tvAccount;
	private Button btLogin;
	private LinearLayout layoutPassword;
	private LinearLayout layoutFeedback;
	private LinearLayout layoutUpdate;
	private LinearLayout layoutAbout;
	private TextView tvPassword;
	private TextView tvPasswordHint;

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
		tvPassword = (TextView) findViewById(R.id.setting_textview_password);
		tvPasswordHint = (TextView) findViewById(R.id.setting_textview_password_hint);

		password = PreferencesUtils.getStringPreference(
				getApplicationContext(), PERSONAL, PASSWORD, "");
		if (password.equals("")) {
			tvPassword.setText("设置密码");
			tvPasswordHint.setVisibility(View.VISIBLE);
		} else {
			tvPassword.setText("取消密码");
			tvPasswordHint.setVisibility(View.GONE);
		}

		btLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingTabActivity.this, "绑定账号", 0).show();
				Intent in = new Intent(SettingTabActivity.this,
						SettingLoginWebview.class);
				startActivity(in);
			}
		});
		layoutPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(SettingTabActivity.this, "设置密码", 0).show();
				if (tvPasswordHint.getVisibility() == View.GONE) {
					buildPasswordDialog(1);
				} else {
					buildPasswordDialog(0);
				}
			}
		});
		layoutFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(SettingTabActivity.this, "意见反馈", 0).show();
				Intent intent = new Intent(SettingTabActivity.this,
						SettingFeedbackActivity.class);
				startActivity(intent);
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

	private void buildPasswordDialog(int flag) {

		LayoutInflater li = getLayoutInflater();
		if (flag == 0) {
			View dialogView = li.inflate(
					R.layout.setting_tab_dialog_layout_setpassword, null);
			pw = (EditText) dialogView
					.findViewById(R.id.setting_tab_dialog_password);
			verifyPw = (EditText) dialogView
					.findViewById(R.id.setting_tab_dialog_verifyPassword);
			Button cancelButton = (Button) dialogView
					.findViewById(R.id.setting_tab_dialog_cancel);
			Button confirmButton = (Button) dialogView
					.findViewById(R.id.setting_tab_dialog_confirm);

			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("设置密码");
			builder.setView(dialogView);

			final AlertDialog ad = builder.create();
			ad.setCancelable(false);
			ad.show();

			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ad.dismiss();
				}
			});
			confirmButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String pwText = pw.getText().toString().trim();
					String verifyPwText = verifyPw.getText().toString().trim();
					if (pwText.equals("") || pwText == null) {
						Toast.makeText(SettingTabActivity.this, "亲，设置密码不可为空", 1)
								.show();
					} else if (verifyPwText.equals("") || verifyPwText == null) {
						Toast.makeText(SettingTabActivity.this, "亲，重复确认密码不可为空",
								1).show();
					} else if (pwText.equals(verifyPwText)) {
						PreferencesUtils.setStringPreferences(
								getApplicationContext(), PERSONAL, PASSWORD,
								pwText);
						Toast.makeText(SettingTabActivity.this, "亲，密码设置成功！", 1)
								.show();
						tvPassword.setText("取消密码");
						tvPasswordHint.setVisibility(View.GONE);
						ad.dismiss();
					} else {
						Toast.makeText(SettingTabActivity.this,
								"亲，设置密码和重复密码不一致，请重新输入", 1).show();
					}
				}
			});
		} else {
			View dialogView = li.inflate(
					R.layout.setting_tab_dialog_layout_cancelpassword, null);
			cancelPw = (EditText) dialogView
					.findViewById(R.id.setting_tab_dialog_cancelPassword);
			Button cancelButton = (Button) dialogView
					.findViewById(R.id.setting_tab_dialog_cancel);
			Button confirmButton = (Button) dialogView
					.findViewById(R.id.setting_tab_dialog_confirm);

			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("取消密码");
			builder.setView(dialogView);

			final AlertDialog ad = builder.create();
			ad.setCancelable(false);
			ad.show();

			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ad.dismiss();
				}
			});
			confirmButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String pwText = cancelPw.getText().toString().trim();
					if (pwText.equals("") || pwText == null) {
						Toast.makeText(SettingTabActivity.this, "亲，原始密码输入不可为空",
								1).show();
					} else if ((PreferencesUtils.getStringPreference(
							getApplicationContext(), PERSONAL, PASSWORD, ""))
							.equals(pwText)) {
						PreferencesUtils
								.setStringPreferences(getApplicationContext(),
										PERSONAL, PASSWORD, "");
						Toast.makeText(SettingTabActivity.this, "亲，取消密码成功！", 1)
								.show();
						tvPassword.setText("设置密码");
						tvPasswordHint.setVisibility(View.VISIBLE);
						ad.dismiss();
					} else {
						Toast.makeText(SettingTabActivity.this, "亲，原始密码输入不正确",
								1).show();
					}
				}
			});
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		((AppApplication) getApplication()).exitApp(this);
	}
}
