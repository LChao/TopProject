package com.tianxia.app.healthworld.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.PopupWindow.OnDismissListener;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.HomeGoodsInfo;
import com.tianxia.app.healthworld.utils.FinalBitmap;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.lib.baseworld.sync.http.RequestParams;
import com.tianxia.lib.baseworld.utils.NetworkUtils;
import com.tianxia.lib.baseworld.utils.PreferencesUtils;

public class HomeTabActivity extends AdapterActivity<HomeGoodsInfo> implements
		OnScrollListener {
	public static final String TAG = "HomeTabActivity";

	// 当前页面属性
	private String curSortType = "1";
	private String curTypeId = "";
	private int curPage = 1;
	private int lastVisibleItem = 0;
	// item相关
	private int gridItemHeight;
	private FinalBitmap fb;
	// 软件密码
	private String password;
	// banner下拉菜单部分
	private TextView mBannerTitle;
	private RelativeLayout mBannerLayout;
	private boolean isOpenPop = false;
	private PopupWindow popwindow;
	private static final String KEY = "key";
	private static final String VALUE = "value";
	private ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
	// 新品、热门
	private boolean isHot = true;
	private TextView categotyTv = null;
	// 主界面布局content部分的数据加载指示控件
	private TextView mAppLoadingTip = null;
	// 顶部刷新按钮
	private ProgressBar mTopLoadingPbar = null;
	private ImageView mTopLoadingImage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fb = new FinalBitmap(this).init();
		fb.configLoadingImage(R.drawable.app_download_loading);
		// fb.configLoadfailImage(R.drawable.gallery_it);
		// 这里可以进行其他十几项的配置，也可以不用配置，配置之后必须调用init()函数,才生效
		fb.configDiskCachePath(AppApplication.mSdcardImageDir);
		fb.init();
		// fb.configBitmapLoadThreadSize(int size)

		categotyTv = (TextView) findViewById(R.id.home_textview_category);

		mAppLoadingTip = (TextView) findViewById(R.id.app_loading_tip);
		mTopLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar_top);
		mTopLoadingImage = (ImageView) findViewById(R.id.app_loading_btn_top);

		mBannerLayout = (RelativeLayout) findViewById(R.id.home_tab_banner);
		mBannerTitle = (TextView) findViewById(R.id.top_banner_title);

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
				if (BaseApplication.mNetWorkState != NetworkUtils.NETWORN_NONE) {
					loadGridView(true, 1);
				} else {
					Toast.makeText(HomeTabActivity.this, "无可用网络连接", 0).show();
				}
			}
		});

		categotyTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (BaseApplication.mNetWorkState != NetworkUtils.NETWORN_NONE) {
					if (isHot) {
						isHot = false;
						curSortType = "0";
						categotyTv.setText("新品");
						loadGridView(true, 1);
					} else {
						isHot = true;
						curSortType = "1";
						categotyTv.setText("热门");
						loadGridView(false, 1);
					}
				} else {
					Toast.makeText(HomeTabActivity.this, "无可用网络连接", 0).show();
				}
			}
		});

		int gridColumn = (int) Math.ceil(AppApplication.screenWidth / 315.0);
		((GridView) getListView()).setNumColumns(gridColumn);
		((GridView) getListView()).setOnScrollListener(this);
		gridItemHeight = (AppApplication.screenWidth
				- (int) Math.floor(4 * (gridColumn + 1)
						* AppApplication.screenDensity) - 10 * gridColumn)
				/ gridColumn;
		Log.d(TAG, "gridview gridItemHeight: " + gridItemHeight
				+ " gridColumn: " + gridColumn);

		password = PreferencesUtils.getStringPreference(
				getApplicationContext(), "personalData", "password", "");
		if (password.equals("")) {
			loadGridView(false, 1);
		} else {
			LayoutInflater li = getLayoutInflater();
			View dialogView = li.inflate(
					R.layout.home_tab_dialog_layout_inputpassword, null);
			final EditText inputPw = (EditText) dialogView
					.findViewById(R.id.home_tab_dialog_inputPassword);
			Button cancelButton = (Button) dialogView
					.findViewById(R.id.setting_tab_dialog_cancel);
			Button confirmButton = (Button) dialogView
					.findViewById(R.id.setting_tab_dialog_confirm);

			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("输入密码");
			builder.setView(dialogView);

			final AlertDialog ad = builder.create();
			ad.setCancelable(false);
			ad.show();

			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onBackPressed();
				}
			});
			confirmButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String pwText = inputPw.getText().toString().trim();
					if (pwText.equals("") || pwText == null) {
						Toast.makeText(HomeTabActivity.this, "亲，输入密码不可为空", 1)
								.show();
					} else if (password.equals(pwText)) {
						Toast.makeText(HomeTabActivity.this, "亲，欢迎回来！", 1)
								.show();
						ad.dismiss();
						loadGridView(false, 1);
					} else {
						Toast.makeText(HomeTabActivity.this, "亲，密码输入错误", 1)
								.show();
					}
				}
			});
		}

	}

	private void loadGridView(boolean isRefresh, final int page) {
		categotyTv.setClickable(false);
		mBannerTitle.setClickable(false);
		if (isRefresh) {
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			params.put("params", getCurParams(page));

			client.post(HomeApi.HOME_GOODS_URL, params,
					new AsyncHttpResponseHandler() {

						@Override
						public void onStart() {
							mAppLoadingTip.setText(R.string.app_loading);
							mAppLoadingTip.setVisibility(View.VISIBLE);
							mTopLoadingPbar.setVisibility(View.VISIBLE);
							mTopLoadingImage.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onSuccess(String result) {
							if (curTypeId.equals("") && curSortType.equals("1")
									&& page == 1) {
								ConfigCache.setUrlCache(result,
										HomeApi.HOME_GOODS_URL);
							}
							setAppreciateCategoryList(result, page);
							curPage = page;
						}

						@Override
						public void onFailure(Throwable arg0) {
							Toast.makeText(HomeTabActivity.this, "加载失败..", 0)
									.show();
							mAppLoadingTip.setVisibility(View.VISIBLE);
							mAppLoadingTip.setText(R.string.app_loading_fail);
						}

						@Override
						public void onFinish() {
							categotyTv.setClickable(true);
							mBannerTitle.setClickable(true);
							mAppLoadingTip.setVisibility(View.GONE);
							mTopLoadingPbar.setVisibility(View.GONE);
							mTopLoadingImage.setVisibility(View.VISIBLE);
						}
					});
		} else {

			String cacheConfigString = ConfigCache
					.getUrlCache(HomeApi.HOME_GOODS_URL);
			if (curTypeId.equals("") && curSortType.equals("1")
					&& cacheConfigString != null && page == 1) {
				setAppreciateCategoryList(cacheConfigString, page);
				curPage = page;
				mAppLoadingTip.setVisibility(View.GONE);
				mTopLoadingPbar.setVisibility(View.GONE);
				mTopLoadingImage.setVisibility(View.VISIBLE);
				categotyTv.setClickable(true);
				mBannerTitle.setClickable(true);
			} else {
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams params = new RequestParams();
				params.put("params", getCurParams(page));
				client.post(HomeApi.HOME_GOODS_URL, params,
						new AsyncHttpResponseHandler() {

							@Override
							public void onStart() {
								mAppLoadingTip.setText(R.string.app_loading);
								mAppLoadingTip.setVisibility(View.VISIBLE);
								mTopLoadingPbar.setVisibility(View.VISIBLE);
								mTopLoadingImage.setVisibility(View.INVISIBLE);
							}

							@Override
							public void onSuccess(String result) {
								if (curTypeId.equals("")
										&& curSortType.equals("1") && page == 1) {
									ConfigCache.setUrlCache(result,
											HomeApi.HOME_GOODS_URL);
								}
								setAppreciateCategoryList(result, page);
								curPage = page;
							}

							@Override
							public void onFailure(Throwable arg0) {
								mAppLoadingTip
										.setText(R.string.app_loading_fail);
							}

							@Override
							public void onFinish() {
								categotyTv.setClickable(true);
								mBannerTitle.setClickable(true);
								mAppLoadingTip.setVisibility(View.GONE);
								mTopLoadingPbar.setVisibility(View.GONE);
								mTopLoadingImage.setVisibility(View.VISIBLE);
							}
						});
			}
		}
	}

	/**
	 * 更改Pop状态
	 * */

	public void changPopState(View v) {

		isOpenPop = !isOpenPop;
		if (isOpenPop) {
			mBannerTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.home_arrow_up, 0);
			popAwindow(v);
		} else {
			mBannerTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.home_arrow_down, 0);
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
				mBannerTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.home_arrow_down, 0);
			}
		});
		popwindow.update();
		popwindow.showAsDropDown(mBannerLayout,
				(AppApplication.screenWidth - popwindow.getWidth()) / 2, -11);
		// window.showAtLocation(parent, Gravity.CENTER_HORIZONTAL,
		// 0, (int) getResources().getDimension(R.dimen.pop_layout_y));

	}

	private void setAppreciateCategoryList(String jsonString, int page) {
		try {
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonArray = json.getJSONArray("list");
			if (page == 1) {
				listData.clear();
			}
			HomeGoodsInfo appreciateCategoryInfo = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				appreciateCategoryInfo = new HomeGoodsInfo();
				appreciateCategoryInfo.cid = jsonArray.getJSONObject(i)
						.optString("C_CID");
				appreciateCategoryInfo.mResUrl = jsonArray.getJSONObject(i)
						.optString("C_MRES_URL");
				appreciateCategoryInfo.price = jsonArray.getJSONObject(i)
						.optString("C_PRICE");
				appreciateCategoryInfo.tradeCount = jsonArray.getJSONObject(i)
						.optString("C_TRADE_COUNT");
				appreciateCategoryInfo.sResUrl = jsonArray.getJSONObject(i)
						.optString("C_SRES_URL");
				appreciateCategoryInfo.name = jsonArray.getJSONObject(i)
						.optString("C_NAME");
				appreciateCategoryInfo.desci = jsonArray.getJSONObject(i)
						.optString("C_DESCI");
				appreciateCategoryInfo.spreadUrl = jsonArray.getJSONObject(i)
						.optString("C_SPREAD_URL");
				appreciateCategoryInfo.evaluation = jsonArray.getJSONObject(i)
						.optString("evaluation");
				listData.add(appreciateCategoryInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (page == 1) {
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void setLayoutView() {
		setContentView(R.layout.home_tab_activity);
		setListView(R.id.home_list);
		adapter = new Adapter(HomeTabActivity.this);
		listView.setAdapter(adapter);
	}

	@Override
	protected View getView(int position, View convertView) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.home_tab_list_item, null);
			holder = new ViewHolder();

			holder.price = (TextView) convertView
					.findViewById(R.id.home_tab_galleryitem_price);
			holder.sales = (TextView) convertView
					.findViewById(R.id.home_tab_galleryitem_sales);
			holder.cover = (ImageView) convertView
					.findViewById(R.id.item_image);
			holder.cover.setLayoutParams(new RelativeLayout.LayoutParams(
					gridItemHeight, gridItemHeight));
			holder.cover.setScaleType(ScaleType.CENTER_CROP);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// bitmap加载就这一行代码，display还有其他重载，详情查看源码
		fb.display(holder.cover, listData.get(position).mResUrl
				+ "_310x310.jpg");
		holder.sales.setText("销量:" + listData.get(position).tradeCount);
		holder.price.setText("￥" + listData.get(position).price);

		return convertView;
	}

	static class ViewHolder {

		TextView price;
		TextView sales;
		ImageView cover;

	}

	@Override
	protected void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Intent goodsDetailsIntent = new Intent(HomeTabActivity.this,
				HomeDetailsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("cid", listData.get(position).cid);
		String[] urls = listData.get(position).sResUrl.split(";");
		bundle.putStringArray("sResUrl", urls);
		bundle.putString("name", listData.get(position).name);
		bundle.putString("price", listData.get(position).price);
		bundle.putString("tradeCount", listData.get(position).tradeCount);
		bundle.putString("desc", listData.get(position).desci);
		bundle.putString("evaluation", listData.get(position).evaluation);
		bundle.putString("mResUrl", listData.get(position).mResUrl);
		bundle.putString("spreadUrl", listData.get(position).spreadUrl);

		goodsDetailsIntent.putExtras(bundle);
		startActivity(goodsDetailsIntent);
	}

	OnItemClickListener PopwindowlistClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Map<String, Object> map = (Map<String, Object>) parent
					.getItemAtPosition(position);

			if (!((CharSequence) map.get(KEY)).equals(mBannerTitle.getText())) {
				if (BaseApplication.mNetWorkState != NetworkUtils.NETWORN_NONE) {
					mBannerTitle.setText((CharSequence) map.get(KEY));
					// 刷新数据
					curTypeId = (String) map.get(VALUE);
					loadGridView(false, 1);
				} else {
					Toast.makeText(HomeTabActivity.this, "无可用网络连接", 0).show();
				}
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
		map.put(KEY, "全部精选");
		map.put(VALUE, "");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "安全套");
		map.put(VALUE, "50003114");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "情趣内衣");
		map.put(VALUE, "50019652,50019653,50019656,50019657,50019658,50019659");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "男欢世界");
		map.put(VALUE,
				"50019618,50019619,50019623,50019626,50019627,50019628,50019629");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "女爱欢心");
		map.put(VALUE, "50019631,50019636,50019637,50019638,50019639,50019640");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(KEY, "情趣用品");
		map.put(VALUE,
				"50019642,50019643,50019644,50019645,50019646,50019647,50019700");
		items.add(map);

		return items;

	}

	private String getCurParams(int page) {
		JSONObject jb = new JSONObject();
		try {
			jb.put("CurPage", page);
			jb.put("SortType", curSortType);
			// jb.put("PageSize", "3");
			if (!curTypeId.equals("")) {
				jb.put("TypeId", curTypeId);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "params: " + jb);
		return jb.toString();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		lastVisibleItem = firstVisibleItem + visibleItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleItem == adapter.getCount()) {
			Toast.makeText(HomeTabActivity.this, "加载更多", 0).show();
			loadGridView(false, curPage + 1);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		((AppApplication) getApplication()).exitApp(this);
	}
}
