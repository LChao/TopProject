package com.tianxia.app.healthworld.collect;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.AppSQLiteHelper;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.home.HomeTaobaoWebview;
import com.tianxia.app.healthworld.model.CollectInfo;
import com.tianxia.app.healthworld.utils.FinalBitmap;
import com.tianxia.lib.baseworld.activity.AdapterActivity;

public class CollectTabActivity extends AdapterActivity<CollectInfo> {
	public static final String TAG = "CollectTabActivity";

	private SQLiteDatabase db;
	// item相关
	private int gridItemHeight;
	private FinalBitmap fb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = AppApplication.mSQLiteHelper.getWritableDatabase();

		fb = new FinalBitmap(this).init();
		fb.configLoadingImage(R.drawable.app_download_loading);
		// fb.configLoadfailImage(R.drawable.gallery_it);
		// 这里可以进行其他十几项的配置，也可以不用配置，配置之后必须调用init()函数,才生效
		fb.configDiskCachePath(AppApplication.mSdcardImageDir);
		fb.init();
		// fb.configBitmapLoadThreadSize(int size)

		int gridColumn = (int) Math.floor(AppApplication.screenWidth / 320.0);
		if (gridColumn <= 2) {
			gridColumn = 2;
		}
		((GridView) getListView()).setNumColumns(gridColumn);
		// ((GridView) getListView()).setOnScrollListener(l)
		gridItemHeight = (AppApplication.screenWidth
				- (int) Math.floor(4 * (gridColumn + 1)
						* AppApplication.screenDensity) - 2 * gridColumn)
				/ gridColumn;
		Log.d(TAG, "gridview gridItemHeight: " + gridItemHeight
				+ " gridColumn: " + gridColumn);

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
			Cursor cursor = null;
			try {
				cursor = db.query(AppSQLiteHelper.TABLE_COLLECT, null, null,
						null, null, null, null);
				if (cursor.moveToFirst()) {
					do {
						CollectInfo collectInfo = new CollectInfo();
						collectInfo.num_iid = cursor.getString(cursor
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
			cursor.close();
		}
	}

	@Override
	protected View getView(final int position, View convertView,
			ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.collect_tab_list_item, null);
			holder = new ViewHolder();

			holder.imageLayout = (RelativeLayout) convertView
					.findViewById(R.id.collect_listitem_imageLayout);
			holder.price = (TextView) convertView
					.findViewById(R.id.collect_listitem_price);
			holder.sales = (TextView) convertView
					.findViewById(R.id.collect_listitem_sales);
			holder.cover = (ImageView) convertView
					.findViewById(R.id.item_image);
			holder.cover.setLayoutParams(new RelativeLayout.LayoutParams(
					gridItemHeight, gridItemHeight));
			holder.cover.setScaleType(ScaleType.CENTER_CROP);
			holder.delete = (Button) convertView
					.findViewById(R.id.collect_listitem_delete);
			holder.goBuy = (Button) convertView
					.findViewById(R.id.collect_listitem_goBuy);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// bitmap加载就这一行代码，display还有其他重载，详情查看源码
		fb.display(holder.cover, listData.get(position).mResUrl
				+ "_310x310.jpg");
		holder.sales.setText("销量:" + listData.get(position).tradeCount);
		holder.price.setText("￥" + listData.get(position).price);
		holder.delete.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				db.execSQL("delete from collection where num_iid = '"
						+ listData.get(position).num_iid + "'");
				Toast.makeText(CollectTabActivity.this,
						"删除收藏" + listData.get(position).num_iid,
						Toast.LENGTH_SHORT).show();
				showFavoriteList();
			}
		});
		holder.goBuy.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(CollectTabActivity.this,
						HomeTaobaoWebview.class);
				in.putExtra("url", listData.get(position).spreadUrl);
				startActivity(in);
			}
		});
		holder.imageLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(CollectTabActivity.this,
						HomeTaobaoWebview.class);
				in.putExtra("url", listData.get(position).spreadUrl);
				startActivity(in);
			}
		});

		return convertView;
	}

	static class ViewHolder {

		RelativeLayout imageLayout;
		TextView price;
		TextView sales;
		ImageView cover;
		Button delete;
		Button goBuy;

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		db.close();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		((AppApplication) getApplication()).exitApp(this);
	}

	@Override
	protected void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// TODO Auto-generated method stub
	}
}
