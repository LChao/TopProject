package com.tianxia.app.healthworld.setting;

import java.io.ByteArrayInputStream;

import org.xmlpull.v1.XmlPullParser;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.home.HomeApi;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.upgrade.AppUpgradeService;
import com.tianxia.lib.baseworld.utils.NetworkUtils;
import com.tianxia.lib.baseworld.utils.PreferencesUtils;

import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
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
	private String apkDownloadPath = "";
	private boolean isChecking = false;

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
				if (isChecking) {
					Toast.makeText(SettingsTabActivity.this, "正在为您检查更新...", 0)
							.show();
				} else {
					checkNewVersionInfo();
				}
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

	public void checkNewVersion() {
		isChecking = false;
		if (apkDownloadPath.equals("")) {
			return;
		}
		if (BaseApplication.mVersionCode < BaseApplication.mLastestVersionCode) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.check_new_version)
					.setMessage(BaseApplication.mLatestVersionUpdate)
					.setPositiveButton(R.string.app_upgrade_confirm,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											SettingsTabActivity.this,
											AppUpgradeService.class);
									intent.putExtra("downloadUrl",
											apkDownloadPath);
									startService(intent);
								}
							})
					.setNeutralButton(R.string.app_upgrade_cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create().show();
		} else {
			Toast.makeText(SettingsTabActivity.this, "您已是最新版本", 0).show();
		}
	}

	public void checkNewVersionInfo() {
		if (BaseApplication.mNetWorkState == NetworkUtils.NETWORN_NONE) {
			Toast.makeText(SettingsTabActivity.this, "检查版本更新失败", 0).show();
			return;
		}
		isChecking = true;
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(HomeApi.CHECK_VERSION_URL, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(String result) {
				if (result == null || result.trim().equals("")) {
					return;
				}
				// 由android.util.Xml创建一个XmlPullParser实例
				try {

					XmlPullParser parser = Xml.newPullParser();
					// 设置输入流 并指明编码方式
					parser.setInput(
							new ByteArrayInputStream(result.getBytes()),
							"UTF-8");

					int eventType = parser.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						switch (eventType) {
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:
							if (parser.getName().equals("name")) {
								eventType = parser.next();
								BaseApplication.mLatestVersionUpdate = parser
										.getText();
								// book.setId(Integer.parseInt(parser.getText()));
							} else if (parser.getName().equals("version")) {
								eventType = parser.next();
								BaseApplication.mLastestVersionCode = Integer
										.parseInt(parser.getText());
								// book.setName(parser.getText());
							} else if (parser.getName().equals("description")) {
								eventType = parser.next();
								BaseApplication.mLastestVersionName = parser
										.getText();
								// book.setPrice(Float.parseFloat(parser.getText()));
							} else if (parser.getName().equals("url")) {
								eventType = parser.next();
								apkDownloadPath = parser.getText();
							}
							break;
						case XmlPullParser.END_TAG:
							// if (parser.getName().equals("book")) {
							// books.add(book);
							// book = null;
							// }
							break;
						}
						eventType = parser.next();
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("AppApplication",
							"checkNewVersion exception:" + e.toString());
				}
				checkNewVersion();
			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.d("AppApplication", "checkNewVersion onFailure");
			}

			@Override
			public void onFinish() {

			}
		});
	}
}