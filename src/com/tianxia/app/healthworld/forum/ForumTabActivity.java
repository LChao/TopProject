package com.tianxia.app.healthworld.forum;

import android.os.Bundle;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.lib.baseworld.activity.BaseActivity;

public class ForumTabActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forum_tab_activity);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		((AppApplication) getApplication()).exitApp(this);
	}
}
