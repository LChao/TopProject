package com.tianxia.app.healthworld.setting;

import com.tianxia.app.healthworld.R;

import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SettingsTabActivity extends Activity {

	UITableView tableView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tableView = (UITableView) findViewById(R.id.tableView);
		createList();
		tableView.commit();
	}

	private void createList() {
		CustomClickListener listener = new CustomClickListener();
		tableView.setClickListener(listener);
		tableView.addBasicItem("设置密码", "亲,保护隐私哦~");
		tableView.addBasicItem("建议反馈", "亲,有机会获得意外惊喜哦~");
		tableView.addBasicItem("检查更新");
		tableView.addBasicItem("关于我们");

	}

	private class CustomClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			Log.d("MainActivity", "item clicked: " + index);
			// if(index == 0) {
			// Intent i = new Intent(SettingsTabActivity.this,
			// Example1Activity.class);
			// startActivity(i);
			// }
			// else if(index == 1) {
			// Intent i = new Intent(SettingsTabActivity.this,
			// Example2Activity.class);
			// startActivity(i);
			// }
			// else if(index == 2) {
			// Intent i = new Intent(SettingsTabActivity.this,
			// Example3Activity.class);
			// startActivity(i);
			// }
			// else if(index == 3) {
			// Intent i = new Intent(SettingsTabActivity.this,
			// Example4Activity.class);
			// startActivity(i);
			// }
			// else if(index == 4) {
			// Intent i = new Intent(SettingsTabActivity.this,
			// Example5Activity.class);
			// startActivity(i);
			// }
			// else if(index == 5) {
			// Intent i = new Intent(SettingsTabActivity.this,
			// Example6Activity.class);
			// startActivity(i);
			// }
			// else if(index == 6) {
			// Intent i = new Intent(SettingsTabActivity.this,
			// Example7Activity.class);
			// startActivity(i);
			// }
			// else if(index == 7) {
			// tableView.clear();
			// }

		}

	}

}