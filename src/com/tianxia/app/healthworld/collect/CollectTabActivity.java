package com.tianxia.app.healthworld.collect;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.AppSQLiteHelper;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.model.CollectInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.activity.AdapterActivity.Adapter;
import com.tianxia.widget.image.SmartImageView;

public class CollectTabActivity extends AdapterActivity<CollectInfo> {

	private TextView mItemTitleTextView = null;
	private SmartImageView mItemConverImageView = null;
	private TextView mItemDateTextView = null;
	private Button mItemDeleteView = null;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new Adapter(CollectTabActivity.this);
		listView.setAdapter(adapter);
	}

	@Override
	protected void setLayoutView() {
		setContentView(R.layout.collect_tab_activity);
		setListView(R.id.collect_list);
	}

	private void getCollectList() {
		listData.clear();
		synchronized (AppApplication.mSQLiteHelper) {
			db = AppApplication.mSQLiteHelper.getReadableDatabase();
			Cursor cursor = null;
			try {
				cursor = db.query(AppSQLiteHelper.TABLE_COLLECT, null, null,
						null, null, null, null);
				if (cursor.moveToFirst()) {
					do {
						CollectInfo collectInfo = new CollectInfo();
						collectInfo.num_iid = cursor.getInt(cursor
								.getColumnIndex("num_iid"));
						collectInfo.mResUrl = cursor.getString(cursor
								.getColumnIndex("mResUrl"));
						collectInfo.price = cursor.getString(cursor
								.getColumnIndex("price"));
						collectInfo.tradeCount = cursor.getString(cursor
								.getColumnIndex("tradeCount"));
						collectInfo.spreadUrl = cursor.getString(cursor
								.getColumnIndex("spreadUrl"));
						listData.add(collectInfo);
					} while (cursor.moveToNext());
				} else {
					Toast.makeText(CollectTabActivity.this, "您的收藏为空", 1).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}
	}

	@Override
	protected View getView(final int position, View convertView) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.collect_tab_list_item, null);
		}

		mItemConverImageView = (SmartImageView) view
				.findViewById(R.id.collect_listitem_image);
		mItemConverImageView.setImageUrl(
				AppApplication.mDomain + listData.get(position).mResUrl,
				R.drawable.icon, R.drawable.app_download_loading);

		mItemTitleTextView = (TextView) view
				.findViewById(R.id.collect_listitem_title);
		// mItemTitleTextView.setText(listData.get(position).title);

		mItemDateTextView = (TextView) view
				.findViewById(R.id.collect_listitem_date);
		// mItemDateTextView.setText(listData.get(position).date);

		mItemDeleteView = (Button) view
				.findViewById(R.id.collect_listitem_delete);
		mItemDeleteView.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				db.execSQL("delete from collection where num_iid = '"
						+ listData.get(position).num_iid + "'");
				Toast.makeText(CollectTabActivity.this, "删除收藏",
						Toast.LENGTH_SHORT).show();
				showFavoriteList();
			}
		});

		return view;
	}

	protected void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// Intent intent = new Intent(this, ChapterListActivity.class);
		// intent.putExtra("title", listData.get(position).title);
		// intent.putExtra("url", listData.get(position).url);
		// startActivity(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showFavoriteList();
	}

	private void showFavoriteList() {
		getCollectList();
		adapter.notifyDataSetChanged();
	}

}
