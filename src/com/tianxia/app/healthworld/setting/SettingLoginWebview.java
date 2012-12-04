package com.tianxia.app.healthworld.setting;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.BaseActivity;

public class SettingLoginWebview extends BaseActivity {

	private static final String TAG = "SettingLoginWebview";
	private WebView login;
	private ImageView mAppBackButton = null;
	private Handler handler;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_login_webview_activity);
		handler = new Handler();
		login = (WebView) findViewById(R.id.setting_login_webview);
		mAppBackButton = (ImageView) findViewById(R.id.app_back);
		mAppBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		WebSettings taobaoSettings = login.getSettings();
		taobaoSettings.setJavaScriptEnabled(true);
		login.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		login.setWebViewClient(new WebViewClient() {
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				
				super.onPageStarted(view, url, favicon);
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(TAG, "shouldOverrideUrlLoading,URL is: " + url);
				return false;
			}

			@Override
			public void onPageFinished(final WebView webView, String url) {
				Log.d(TAG, "onPageFinished,URL is: " + url);
				// document.getElementById('J_Taojia').style.display='none';
				// document.body.removeChild(document.getElementById(\"J_Taojia\"));
				if (url.contains("m.taobao.com")) {
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							// webView.loadUrl("javascript:document.body.removeChild(document.getElementById(\"J_Taojia\"));");
						}
					}, 50);
				}
			}
		});
		login.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
				super.onProgressChanged(view, progress);
				// Activity和Webview根据加载程度决定进度条的进度大小
				// 当加载到100%的时候 进度条自动消失
				SettingLoginWebview.this.setProgress(progress * 100);
			}
		});
		login.loadUrl("https://oauth.taobao.com/authorize?response_type=token&client_id=21247503&view=wap");
	}

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && login.canGoBack()) {
	// login.goBack();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
}
