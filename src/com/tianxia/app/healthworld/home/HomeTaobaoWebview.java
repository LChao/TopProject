package com.tianxia.app.healthworld.home;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.activity.BaseActivity;

public class HomeTaobaoWebview extends BaseActivity {
	private static final String TAG = "HomeTaobaoWebview";

	private ImageView mAppBackButton = null;
	private ProgressBar mAppPB;

	private WebView taobao;
	private ImageButton backBut;
	private ImageButton forwardBut;
	private ImageButton refreshBut;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_taobao_webview_activity);
		taobao = (WebView) findViewById(R.id.home_taobao_webview);
		mAppPB = (ProgressBar) findViewById(R.id.home_taobao_webview_pb);
		backBut = (ImageButton) findViewById(R.id.home_taobao_button_back);
		forwardBut = (ImageButton) findViewById(R.id.home_taobao_button_forward);
		refreshBut = (ImageButton) findViewById(R.id.home_taobao_button_refresh);
		mAppBackButton = (ImageView) findViewById(R.id.app_back);
		mAppBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		WebSettings taobaoSettings = taobao.getSettings();
		taobaoSettings.setJavaScriptEnabled(true);
		// taobaoSettings.setAppCacheEnabled(false);
		// taobaoSettings.setCacheMode(2);
		taobao.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		taobao.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			@Override
			public void onPageFinished(final WebView webView, String url) {
				// Log.d(TAG, "onPageFinished,URL is: " + url);
				// document.getElementById('J_Taojia').style.display='none';
				// document.body.removeChild(document.getElementById(\"J_Taojia\"));
				if (url.contains("m.taobao.com") || url.contains("m.tmall.com")) {
					mAppPB.startAnimation(AnimationUtils.loadAnimation(
							HomeTaobaoWebview.this, android.R.anim.fade_out));
					mAppPB.setVisibility(View.GONE);
					if (taobao.canGoBack()) {
						backBut.setClickable(true);
						backBut.setBackgroundResource(R.drawable.taobao_bottom_left);
					}
					if (taobao.canGoForward()) {
						forwardBut.setClickable(true);
						forwardBut
								.setBackgroundResource(R.drawable.taobao_bottom_right);
					}
					refreshBut.setClickable(true);
					refreshBut
							.setBackgroundResource(R.drawable.taobao_bottom_refresh);
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				if (url.contains("m.taobao.com") || url.contains("m.tmall.com")) {
					backBut.setClickable(false);
					forwardBut.setClickable(false);
					refreshBut.setClickable(false);
					backBut.setBackgroundResource(R.drawable.taobao_bottom_left_not);
					forwardBut
							.setBackgroundResource(R.drawable.taobao_bottom_right_not);
					refreshBut
							.setBackgroundResource(R.drawable.taobao_bottom_refresh_not);
				}
				super.onPageStarted(view, url, favicon);
			}
		});
		// taobao.setWebChromeClient(new WebChromeClient() {
		// public void onProgressChanged(WebView view, int progress) {
		// // 载入进度改变而触发
		// super.onProgressChanged(view, progress);
		// // Activity和Webview根据加载程度决定进度条的进度大小
		// // 当加载到100%的时候 进度条自动消失
		// HomeTaobaoWebview.this.setProgress(progress * 100);
		// }
		// });
		taobao.loadUrl(getIntent().getStringExtra("url") + "&ttid="
				+ "400000_21247503@qingqubao_Android_"
				+ BaseApplication.mVersionName);
		Toast msg = Toast.makeText(HomeTaobaoWebview.this, "亲,进入淘宝商品页,请放心购买!",
				Toast.LENGTH_LONG);
		msg.setGravity(Gravity.CENTER, 0, 0);
		msg.show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && taobao.canGoBack()) {
			taobao.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void refreshClick(View paramView) {
		this.taobao.reload();
	}

	public void backClick(View paramView) {
		this.taobao.goBack();
	}

	public void forwardClick(View paramView) {
		this.taobao.goForward();
	}
}
