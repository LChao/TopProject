package com.tianxia.app.healthworld.home;

import android.annotation.SuppressLint;
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

public class HomeTaobaoWebview extends BaseActivity {

	private static final String TAG = "HomeTaobaoWebview";
	private WebView taobao;
	private ImageView mAppBackButton = null;
	private Handler handler;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_taobao_webview_activity);
		handler = new Handler();
		taobao = (WebView) findViewById(R.id.home_taobao_webview);
		mAppBackButton = (ImageView) findViewById(R.id.app_back);
		mAppBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		WebSettings taobaoSettings = taobao.getSettings();
		taobaoSettings.setJavaScriptEnabled(true);
		taobao.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		taobao.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("22222222222222: " + url);
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
							webView.loadUrl("javascript:document.body.removeChild(document.getElementById(\"J_Taojia\"));");
						}
					}, 50);
				}
			}
		});
		taobao.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
				super.onProgressChanged(view, progress);
				// Activity和Webview根据加载程度决定进度条的进度大小
				// 当加载到100%的时候 进度条自动消失
				HomeTaobaoWebview.this.setProgress(progress * 100);
			}
		});
		taobao.loadUrl(getIntent().getStringExtra("url"));
		// taobao.loadUrl("http://s.click.taobao.com/t?e=zGU34CA7K%2BPkqB07S4%2FK0CFcRfH0GoT805sipKvI1SjJA3Am3nGgIDDrQ5bY7COBEklLhc5vmGi8NotnbrwBIONc9GJ1Y3AgeTbJJSDDAz9YpA%3D%3D");
		Log.d(TAG, "URL: " + getIntent().getStringExtra("url"));
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && taobao.canGoBack()) {
			taobao.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
