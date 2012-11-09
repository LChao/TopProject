package com.tianxia.app.healthworld.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.AppreciateCategoryInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class HomeTabActivity extends AdapterActivity<AppreciateCategoryInfo> {
	public static final String TAG = "HomeTabActivity";
	private int screenWidth;
	// banner下拉菜单部分
	private TextView mBannerTitle;
	private ImageView mBannerArrow;
	private RelativeLayout mBannerLayout;
	private boolean isOpenPop = false;
	private PopupWindow popwindow;
	private static final String KEY = "key";
	private ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

	// 新品、热门按钮
	private Button mAppCategotyLeft = null;
	private Button mAppCategotyRight = null;

	// 主界面布局content部分的数据加载指示控件
	private LinearLayout mAppLoadingLinearLayout;
	private TextView mAppLoadingTip = null;
	private ProgressBar mAppLoadingPbar = null;
	// 顶部刷新按钮
	private ProgressBar mTopLoadingPbar = null;
	private ImageView mTopLoadingImage = null;

	// 网格item控件
	private SmartImageView mItemImageView = null;
	private TextView mItemTextView = null;
	private TextView mItemCount = null;

	private Intent mIdentificationIntent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppCategotyLeft = (Button) findViewById(R.id.home_category_left);
		mAppCategotyRight = (Button) findViewById(R.id.home_category_right);
		mAppCategotyLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAppCategotyLeft
						.setBackgroundResource(R.drawable.app_category_left_selected);
				mAppCategotyRight
						.setBackgroundResource(R.drawable.app_category_right_normal);
				// if (mFavoriteType == FavoriteType.ARTICLE) {
				// mFavoriteType = FavoriteType.PICTURE;
				// showFavoriteList(mFavoriteType);
				// }
			}
		});
		mAppCategotyRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAppCategotyLeft
						.setBackgroundResource(R.drawable.app_category_left_normal);
				mAppCategotyRight
						.setBackgroundResource(R.drawable.app_category_right_selected);
				// if (mFavoriteType == FavoriteType.PICTURE) {
				// mFavoriteType = FavoriteType.ARTICLE;
				// showFavoriteList(mFavoriteType);
				// }
			}
		});

		mAppLoadingLinearLayout = (LinearLayout) findViewById(R.id.app_loading);
		mAppLoadingTip = (TextView) findViewById(R.id.app_loading_tip);
		mAppLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar);
		mTopLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar_top);
		mTopLoadingImage = (ImageView) findViewById(R.id.app_loading_btn_top);

		mBannerLayout = (RelativeLayout) findViewById(R.id.home_tab_banner);
		mBannerTitle = (TextView) findViewById(R.id.home_tab_banner_title);
		mBannerArrow = (ImageView) findViewById(R.id.home_tab_banner_arrow);

		mBannerTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "mBannerTitle clicked...");
				changPopState(v);
			}
		});

		mTopLoadingImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadGridView();
			}
		});

		loadGridView();

		// 获取当前屏幕属性
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		screenWidth = metric.widthPixels; // 屏幕宽度（像素）
		// int height = metric.heightPixels; // 屏幕高度（像素）
		// float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		// int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）

	}

	private void loadGridView() {
		String cacheConfigString = ConfigCache
				.getUrlCache(IdentificationApi.IDENTIFICATION_CONFIG_URL);
		if (cacheConfigString != null) {
			setAppreciateCategoryList(cacheConfigString);
			mAppLoadingLinearLayout.setVisibility(View.GONE);
			mTopLoadingPbar.setVisibility(View.GONE);
			mTopLoadingImage.setVisibility(View.VISIBLE);
		} else {
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(IdentificationApi.IDENTIFICATION_CONFIG_URL,
					new AsyncHttpResponseHandler() {

						@Override
						public void onStart() {
							mAppLoadingTip.setText(R.string.app_loading);
							mAppLoadingLinearLayout.setVisibility(View.VISIBLE);
							// mAppLoadingTip.setVisibility(View.VISIBLE);
							// mAppLoadingPbar.setVisibility(View.VISIBLE);
							mTopLoadingPbar.setVisibility(View.VISIBLE);
							mTopLoadingImage.setVisibility(View.GONE);
							listView.setAdapter(null);
						}

						@Override
						public void onSuccess(String result) {
							mAppLoadingLinearLayout.setVisibility(View.GONE);
							ConfigCache
									.setUrlCache(
											result,
											IdentificationApi.IDENTIFICATION_CONFIG_URL);
							setAppreciateCategoryList(result);
						}

						@Override
						public void onFailure(Throwable arg0) {
							mAppLoadingPbar.setVisibility(View.GONE);
							mAppLoadingTip.setText(R.string.app_loading_fail);
						}

						@Override
						public void onFinish() {
							mTopLoadingPbar.setVisibility(View.GONE);
							mTopLoadingImage.setVisibility(View.VISIBLE);
						}
					});
		}
	}

	/**
	 * 更改Pop状态
	 * */

	public void changPopState(View v) {

		isOpenPop = !isOpenPop;
		if (isOpenPop) {
			mBannerArrow.setBackgroundResource(R.drawable.home_arrow_up);
			popAwindow(v);
		} else {
			mBannerArrow.setBackgroundResource(R.drawable.home_arrow_up);
			if (popwindow != null) {
				popwindow.dismiss();
			}
		}
	}

	/**
	 * popwindow初始化
	 * 
	 * @param parent
	 */
	private void popAwindow(View parent) {
		Log.d(TAG, "popAwindow intered...");
		if (popwindow == null) {
			LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = lay.inflate(R.layout.home_popwindow, null);
			ListView list = (ListView) v.findViewById(R.id.home_popwindow_list);

			SimpleAdapter adapter = new SimpleAdapter(this,
					popwindowInitData(), R.layout.home_pop_list_item,
					new String[] { KEY },
					new int[] { R.id.home_pop_list_item_title });

			list.setAdapter(adapter);
			list.setItemsCanFocus(false);
			list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			list.setOnItemClickListener(PopwindowlistClickListener);
			popwindow = new PopupWindow(v, (int) getResources().getDimension(
					R.dimen.home_popwindow_width), (int) getResources()
					.getDimension(R.dimen.home_popwindow_height));
		}
		popwindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.home_popwindow_bg));
		popwindow.setFocusable(true);
		popwindow.setOutsideTouchable(false);
		popwindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				isOpenPop = false;
				mBannerArrow.setBackgroundResource(R.drawable.home_arrow_down);
			}
		});
		popwindow.update();
		popwindow.showAsDropDown(mBannerLayout,
				(screenWidth - popwindow.getWidth()) / 2, 0);
		// window.showAtLocation(parent, Gravity.CENTER_HORIZONTAL,
		// 0, (int) getResources().getDimension(R.dimen.pop_layout_y));

	}

	private void setAppreciateCategoryList(String jsonString) {
		mAppLoadingLinearLayout.setVisibility(View.GONE);
		try {
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonArray = json.getJSONArray("list");
			listData = new ArrayList<AppreciateCategoryInfo>();
			AppreciateCategoryInfo appreciateCategoryInfo = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				appreciateCategoryInfo = new AppreciateCategoryInfo();
				appreciateCategoryInfo.filename = jsonArray.getJSONObject(i)
						.optString("filename");
				appreciateCategoryInfo.category = jsonArray.getJSONObject(i)
						.optString("category");
				appreciateCategoryInfo.thumbnail = jsonArray.getJSONObject(i)
						.optString("thumbnail");
				appreciateCategoryInfo.count = jsonArray.getJSONObject(i)
						.optString("count");
				listData.add(appreciateCategoryInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		adapter = new Adapter(HomeTabActivity.this);
		listView.setAdapter(adapter);
	}

	@Override
	protected void setLayoutView() {
		setContentView(R.layout.home_tab_activity);
		setListView(R.id.home_list);
	}

	@Override
	protected View getView(int position, View convertView) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.home_tab_list_item, null);
		}

		mItemImageView = (SmartImageView) view.findViewById(R.id.item_image);
		if (listData != null && position < listData.size()) {
			mItemImageView.setImageUrl(listData.get(position).thumbnail,
					R.drawable.app_download_fail,
					R.drawable.app_download_loading);
		}

		mItemTextView = (TextView) view.findViewById(R.id.item_category);
		mItemTextView.setText(listData.get(position).category + "("
				+ listData.get(position).count + ")");

		mItemCount = (TextView) view.findViewById(R.id.item_count);
		mItemCount.setText(listData.get(position).count);
		return view;
	}

	@Override
	protected void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// mIdentificationIntent = new Intent(IdentificationTabActivity.this,
		// AppreciateLatestActivity.class);
		// mIdentificationIntent.putExtra("url",
		// AppreciateApi.APPRECIATE_CATEGORY_BASE_URL +
		// listData.get(position).filename + ".json");
		// mIdentificationIntent.putExtra("title",
		// listData.get(position).category);
		// startActivity(mIdentificationIntent);
	}

	OnItemClickListener PopwindowlistClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Map<String, Object> map = (Map<String, Object>) parent
					.getItemAtPosition(position);

			if (!((CharSequence) map.get(KEY)).equals(mBannerTitle.getText())) {
				Toast.makeText(HomeTabActivity.this, map.get(KEY) + "",
						Toast.LENGTH_SHORT).show();
				mBannerTitle.setText((CharSequence) map.get(KEY));
				// 刷新数据

			}
			if (popwindow != null) {
				popwindow.dismiss();

			}

		}
	};

	/**
	 * home页面popwindow弹出框内容初始化
	 * 
	 * @return
	 */
	public ArrayList<Map<String, Object>> popwindowInitData() {

		Map<String, Object> map;

		map = new HashMap<String, Object>();
		map.put(KEY, "全部商品");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "情趣内衣");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "计生用品");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "情趣用品");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "性保健品");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "男用器具");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "女用器具");
		items.add(map);

		return items;

	}
}
