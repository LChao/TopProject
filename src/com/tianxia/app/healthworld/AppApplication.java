package com.tianxia.app.healthworld;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;

import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.collect.CollectTabActivity;
import com.tianxia.app.healthworld.forum.ForumTabActivity;
import com.tianxia.app.healthworld.home.HomeTabActivity;
import com.tianxia.app.healthworld.setting.SettingsTabActivity;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.utils.NetworkUtils;
import com.tianxia.lib.baseworld.utils.PreferencesUtils;

public class AppApplication extends BaseApplication {

	public static final String DOMAIN = "domain";
	public static final String DOMAIN_URL = "url";
	// public static String mDomain = "http://www.kaiyuanxiangmu.com/";
	// public static String mDomain = "http://doss.cn:8080/";
	public static String mDomain = "http://dmy.cn:8080/";
	// public static String mDomain = "http://183.129.179.77/";
	// public static String mDomain = "http://101.71.22.209/";
	public static String mBakeDomain = "http://1.kaiyuanxiangmu.sinaapp.com/";

	private static final String DB_NAME = "qingqubao.db";

	public static String mSdcardDataDir;
	public static String mSdcardImageDir;
	public static String mApkDownloadUrl = null;

	@Override
	public void fillTabs() {
		mTabActivitys.add(HomeTabActivity.class);
		mTabActivitys.add(CollectTabActivity.class);
		mTabActivitys.add(ForumTabActivity.class);
		mTabActivitys.add(SettingsTabActivity.class);

		mTabNormalImages.add(R.drawable.home_normal);
		mTabNormalImages.add(R.drawable.collect_normal);
		mTabNormalImages.add(R.drawable.forum_normal);
		mTabNormalImages.add(R.drawable.setting_normal);

		mTabPressImages.add(R.drawable.home_press);
		mTabPressImages.add(R.drawable.collect_press);
		mTabPressImages.add(R.drawable.forum_press);
		mTabPressImages.add(R.drawable.setting_press);

		mTabMoveImage = R.drawable.tab_bottom_float;
	}

	@Override
	public void initDb() {
		mSQLiteHelper = new AppSQLiteHelper(getApplicationContext(), DB_NAME, 1);
	}

	@Override
	public void initEnv() {
		mAppName = "qingqubao";
		mDownloadPath = "/qingqubao/download";
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			// Set up data cache path
			File file = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/qingqubao/config/");
			if (!file.exists()) {
				if (file.mkdirs()) {
					mSdcardDataDir = file.getAbsolutePath();
				}
			} else {
				mSdcardDataDir = file.getAbsolutePath();
			}
			// Set up image cache path
			mSdcardImageDir = Environment.getExternalStorageDirectory()
					.getPath() + "/" + mAppName + "/image/";
		} else {
			mSdcardImageDir = getCacheDir().getAbsolutePath() + "/" + mAppName
					+ "/image/";
		}
		// 更新网络状态
		mNetWorkState = NetworkUtils.getNetworkState(this);
		// checkDomain(mDomain, false);
	}

	@Override
	public void exitApp(final Context context) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setTitle(this.getString(R.string.app_exit_title))
				.setMessage(this.getString(R.string.app_exit_message))
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setNegativeButton("退出", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		alertBuilder.create().show();
	}

	public void checkDomain(final String domain, final boolean stop) {
		AppApplication.mDomain = PreferencesUtils.getStringPreference(
				getApplicationContext(), DOMAIN, DOMAIN_URL, mDomain);
		String cacheConfigString = ConfigCache
				.getUrlCache(domain + "host.json");
		if (cacheConfigString != null) {
			updateDomain(cacheConfigString);
		} else {
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(domain + "host.json", new AsyncHttpResponseHandler() {

				@Override
				public void onStart() {
				}

				@Override
				public void onSuccess(String result) {
					ConfigCache.setUrlCache(result, domain + "host.json");
					updateDomain(result);
				}

				@Override
				public void onFailure(Throwable arg0) {
					if (!stop) {
						checkDomain(mBakeDomain, true);
					}
				}

				@Override
				public void onFinish() {
				}
			});
		}
	}

	public void updateDomain(String result) {
		try {
			JSONObject appreciateConfig = new JSONObject(result);
			String domain = appreciateConfig.optString("domain");
			if (domain != null && !"".equals(domain)) {
				AppApplication.mDomain = domain;
				PreferencesUtils.setStringPreferences(getApplicationContext(),
						DOMAIN, DOMAIN_URL, domain);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
