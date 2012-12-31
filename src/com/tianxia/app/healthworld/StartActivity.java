package com.tianxia.app.healthworld;

import com.tianxia.lib.baseworld.activity.BaseActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class StartActivity extends BaseActivity implements Handler.Callback {

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_start_layout);
		handler = new Handler(this);

		handler.sendMessageDelayed(new Message(), 1500);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, MainTabFrame.class);
		startActivity(intent);
		finish();
		return false;
	}
}
