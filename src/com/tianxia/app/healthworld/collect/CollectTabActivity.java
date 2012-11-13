package com.tianxia.app.healthworld.collect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.feedback.UMFeedbackService;
import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.AppApplicationApi;
import com.tianxia.app.healthworld.AppSQLiteHelper;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.CollectInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.activity.AdapterActivity.Adapter;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class CollectTabActivity extends AdapterActivity<CollectInfo> {

	private TextView mItemTitleTextView = null;
	private SmartImageView mItemConverImageView = null;
	private TextView mItemSummaryTextView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getCollectList();
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
            SQLiteDatabase db = AppApplication.mSQLiteHelper.getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.query(AppSQLiteHelper.TABLE_COLLECT, null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                    	CollectInfo collectInfo = new CollectInfo();
                    	collectInfo.num_iid = cursor.getInt(cursor.getColumnIndex("num_iid"));
                    	collectInfo.thumbnail = cursor.getString(cursor.getColumnIndex("thumbnail"));
                    	collectInfo.title = cursor.getString(cursor.getColumnIndex("title"));
                    	collectInfo.date = cursor.getString(cursor.getColumnIndex("date"));
                        listData.add(collectInfo);
                    } while (cursor.moveToNext());
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
	protected View getView(int position, View convertView) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.digest_tab_list_item, null);
		}

		mItemConverImageView = (SmartImageView) view
				.findViewById(R.id.item_image);
		mItemConverImageView.setImageUrl(
				AppApplication.mDomain + listData.get(position).cover,
				R.drawable.icon, 0);

		mItemTitleTextView = (TextView) view.findViewById(R.id.item_text);
		mItemTitleTextView.setText(listData.get(position).title);

		mItemSummaryTextView = (TextView) view
				.findViewById(R.id.item_text_describe);
		mItemSummaryTextView.setText(listData.get(position).summary);

		return view;
	}

	protected void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
//		Intent intent = new Intent(this, ChapterListActivity.class);
//		intent.putExtra("title", listData.get(position).title);
//		intent.putExtra("url", listData.get(position).url);
//		startActivity(intent);
	}

}
