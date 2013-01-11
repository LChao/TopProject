package com.tianxia.app.healthworld.setting;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.SettingAboutActivity;
import com.tianxia.lib.baseworld.utils.PreferencesUtils;

import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsTabActivity extends Activity {

	private static final String PERSONAL = "personalData";
	private static final String PASSWORD = "password";

	private String password;
	private Boolean hasPW = false;
	private UITableView tableView;
	private EditText pw;
	private EditText verifyPw;
	private EditText cancelPw;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tableView = (UITableView) findViewById(R.id.tableView);
		createList();
		tableView.commit();

		password = PreferencesUtils.getStringPreference(
				getApplicationContext(), PERSONAL, PASSWORD, "");
		if (password.equals("")) {
			tableView.setBasicItem(new BasicItem("设置密码", "亲,保护隐私哦~"), 0);
			hasPW = false;
		} else {
			tableView.setBasicItem(new BasicItem("取消密码"), 0);
			hasPW = true;
		}
	}

	private void createList() {
		CustomClickListener listener = new CustomClickListener();
		tableView.setClickListener(listener);
		tableView.addBasicItem("", "");
		tableView.addBasicItem("建议反馈", "亲,有机会获得意外惊喜哦~");
		tableView.addBasicItem("检查更新");
		tableView.addBasicItem("关于我们");

	}

	private class CustomClickListener implements ClickListener {
		Intent intent;

		@Override
		public void onClick(int index) {
			switch (index) {
			case 0:
				buildPasswordDialog(hasPW);
				break;
			case 1:
				intent = new Intent(SettingsTabActivity.this,
						SettingsFeedbackActivity.class);
				startActivity(intent);
				break;
			case 2:
				Toast.makeText(SettingsTabActivity.this, "检查更新", 0).show();
				break;
			case 3:
				intent = new Intent(SettingsTabActivity.this,
						SettingAboutActivity.class);
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	}

	private void buildPasswordDialog(Boolean hasPass) {

		LayoutInflater li = getLayoutInflater();
		if (!hasPass) {
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
						Toast.makeText(SettingsTabActivity.this, "亲，设置密码不可为空",
								1).show();
					} else if (verifyPwText.equals("") || verifyPwText == null) {
						Toast.makeText(SettingsTabActivity.this,
								"亲，重复确认密码不可为空", 1).show();
					} else if (pwText.equals(verifyPwText)) {
						PreferencesUtils.setStringPreferences(
								getApplicationContext(), PERSONAL, PASSWORD,
								pwText);
						Toast.makeText(SettingsTabActivity.this, "亲，密码设置成功！", 1)
								.show();
						tableView.setBasicItem(new BasicItem("取消密码"), 0);
						hasPW = true;
						ad.dismiss();
					} else {
						Toast.makeText(SettingsTabActivity.this,
								"亲，设置密码和重复密码不一致", 1).show();
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
						Toast.makeText(SettingsTabActivity.this,
								"亲，原始密码输入不可为空", 1).show();
					} else if ((PreferencesUtils.getStringPreference(
							getApplicationContext(), PERSONAL, PASSWORD, ""))
							.equals(pwText)) {
						PreferencesUtils
								.setStringPreferences(getApplicationContext(),
										PERSONAL, PASSWORD, "");
						Toast.makeText(SettingsTabActivity.this, "亲，取消密码成功！", 1)
								.show();
						tableView.setBasicItem(
								new BasicItem("设置密码", "亲,保护隐私哦~"), 0);
						hasPW = false;
						ad.dismiss();
					} else {
						Toast.makeText(SettingsTabActivity.this, "亲，原始密码输入不正确",
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