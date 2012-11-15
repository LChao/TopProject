package com.tianxia.app.healthworld.home;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.youmi.android.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.HomeDetailsAdCompanyInfo;
import com.tianxia.app.healthworld.model.HomeDetailsInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class HomeDetailsActivity extends AdapterActivity<HomeDetailsInfo> {

	private String mUrl = null;
	private String mHomeDetailsTitle = null;
	private SQLiteDatabase db;
	private Boolean isFavorite;

	// 顶部banner
	private TextView mHomeDetailsTitleView = null;
	private Button mAppBackButton = null;
	private TextView mAppLoadingTip = null;
	private ProgressBar mAppLoadingPbar = null;
	private ImageView mAppLoadingImage = null;

	private SmartImageView mItemSmartImageView = null;

	// 顶部广告栏
	private LinearLayout mAdContainer = null;
	private HomeDetailsAdCompanyInfo mAdCompanyInfo;
	private TextView mAdCompanyName = null;
	private TextView mAdCompanyContact = null;
	private TextView mAdCompanyAddress = null;
	private TextView mAdCompanyTel = null;
	private TextView mAdCompanyPhone = null;
	private TextView mAdCompanyBusiness = null;

	public AdView mAdView = null;
	private Button collect;
	private Button buy;

	private int width;
	private int height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;

		db = AppApplication.mSQLiteHelper.getWritableDatabase();

		mUrl = getIntent().getStringExtra("url");
		mHomeDetailsTitle = getIntent().getStringExtra("title");

		mHomeDetailsTitleView = (TextView) findViewById(R.id.top_banner_title);
		if (mHomeDetailsTitle != null) {
			mHomeDetailsTitleView.setText(mHomeDetailsTitle);
		}
		collect = (Button) findViewById(R.id.home_details_bt_collect);
		buy = (Button) findViewById(R.id.home_details_bt_buy);
		mAdContainer = (LinearLayout) findViewById(R.id.home_details_ad);

		mAppBackButton = (Button) findViewById(R.id.app_back);
		mAppLoadingTip = (TextView) findViewById(R.id.app_loading_tip);
		mAppLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar_top);
		mAppLoadingImage = (ImageView) findViewById(R.id.app_loading_btn_top);
		mAppBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mAppLoadingImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadGridView();
			}
		});

		isFavorite = queryDB();
		if (isFavorite) {
			collect.setText("取消收藏");
		} else {
			collect.setText("收藏");
		}
		collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				favorite();
			}
		});
		buy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(HomeDetailsActivity.this, "购买商品.", 0).show();
			}
		});

		loadGridView();
	}

	private void loadGridView() {
		mAppLoadingTip.setText(R.string.app_loading);
		String cacheConfigString = ConfigCache.getUrlCache(mUrl);
		if (cacheConfigString != null) {
			setAppreciateLatestList(cacheConfigString);
			mAppLoadingTip.setVisibility(View.GONE);
			mAppLoadingPbar.setVisibility(View.GONE);
			mAppLoadingImage.setVisibility(View.VISIBLE);
		} else {
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(mUrl, new AsyncHttpResponseHandler() {

				@Override
				public void onStart() {
					listView.setAdapter(null);
					mAppLoadingTip.setVisibility(View.VISIBLE);
					mAppLoadingPbar.setVisibility(View.VISIBLE);
					mAppLoadingImage.setVisibility(View.GONE);
				}

				@Override
				public void onSuccess(String result) {
					mAppLoadingTip.setVisibility(View.GONE);
					ConfigCache.setUrlCache(result, mUrl);
					setAppreciateLatestList(result);
				}

				@Override
				public void onFailure(Throwable arg0) {
					mAppLoadingTip.setText(R.string.app_loading_fail);
				}

				@Override
				public void onFinish() {
					mAppLoadingPbar.setVisibility(View.GONE);
					mAppLoadingImage.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	private void setAppreciateLatestList(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			JSONObject adCompanyJsonObject = json.optJSONObject("ad-company");
			if (adCompanyJsonObject != null) {
				mAdCompanyInfo = new HomeDetailsAdCompanyInfo();
				mAdCompanyInfo.name = adCompanyJsonObject.optString("name");
				mAdCompanyInfo.contact = adCompanyJsonObject
						.optString("contact");
				mAdCompanyInfo.address = adCompanyJsonObject
						.optString("address");
				mAdCompanyInfo.tel = adCompanyJsonObject.optString("tel");
				mAdCompanyInfo.phone = adCompanyJsonObject.optString("phone");
				mAdCompanyInfo.business = adCompanyJsonObject
						.optString("business");
				setHeaderView();
			}

			JSONArray jsonArray = json.getJSONArray("list");
			listData = new ArrayList<HomeDetailsInfo>();
			HomeDetailsInfo appreciateLatestInfo = null;

			for (int i = 0; i < jsonArray.length(); i++) {
				appreciateLatestInfo = new HomeDetailsInfo();
				appreciateLatestInfo.prefix = jsonArray.getJSONObject(i)
						.optString("prefix");
				appreciateLatestInfo.title = jsonArray.getJSONObject(i)
						.optString("title");
				appreciateLatestInfo.origin = jsonArray.getJSONObject(i)
						.optString("origin");
				appreciateLatestInfo.thumbnail = jsonArray.getJSONObject(i)
						.optString("thumbnail");
				appreciateLatestInfo.tag = jsonArray.getJSONObject(i)
						.optString("tag");
				appreciateLatestInfo.category = jsonArray.getJSONObject(i)
						.optString("category");
				listData.add(appreciateLatestInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		adapter = new Adapter(HomeDetailsActivity.this);
		listView.setAdapter(adapter);
		showAd();
	}

	@Override
	protected void setLayoutView() {
		setContentView(R.layout.home_details_activity);
		setListView(R.id.home_details_gallery);
	}

	/**
	 * 顶部广告栏初始化
	 */
	private void setHeaderView() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.home_ad_company_shop, null);
		mAdCompanyName = (TextView) view.findViewById(R.id.ad_company_name);
		mAdCompanyContact = (TextView) view
				.findViewById(R.id.ad_company_contact);
		mAdCompanyAddress = (TextView) view
				.findViewById(R.id.ad_company_address);
		mAdCompanyTel = (TextView) view.findViewById(R.id.ad_company_tel);
		mAdCompanyPhone = (TextView) view.findViewById(R.id.ad_company_phone);
		mAdCompanyBusiness = (TextView) view
				.findViewById(R.id.ad_company_business);

		mAdCompanyName.setText(mAdCompanyInfo.name);
		mAdCompanyContact.setText(mAdCompanyInfo.contact);
		mAdCompanyAddress.setText(mAdCompanyInfo.address);
		mAdCompanyTel.setText(mAdCompanyInfo.tel);
		mAdCompanyPhone.setText(mAdCompanyInfo.phone);
		mAdCompanyBusiness.setText(mAdCompanyInfo.business);
		mAdContainer.removeAllViews();
		mAdContainer.addView(view);
	}

	@Override
	protected View getView(int position, View convertView) {
		View view;
		view = View.inflate(HomeDetailsActivity.this,
				R.layout.home_details_gallery_item, null);
		mItemSmartImageView = (SmartImageView) view
				.findViewById(R.id.item_image);
		mItemSmartImageView.setLayoutParams(new LinearLayout.LayoutParams(
				width, LayoutParams.FILL_PARENT));
		// mItemSmartImageView.setScaleType(ScaleType.CENTER_CROP);
		// mItemSmartImageView.setImageUrl(listData.get(position).origin,
		// R.drawable.app_download_fail_large,
		// R.drawable.app_download_loading_large, true);
		mItemSmartImageView.setImageUrl(listData.get(position).origin,
				R.drawable.app_download_fail_large,
				R.drawable.app_download_loading_large, true);
		mItemSmartImageView.setScaleType(ScaleType.FIT_XY);
		return view;
	}

	@Override
	protected void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Toast.makeText(this, "position" + position, 0).show();
	}

	/**
	 * 改变商品再数据库中的收藏状态
	 */
	public void favorite() {
		synchronized (AppApplication.mSQLiteHelper) {
			db = AppApplication.mSQLiteHelper.getWritableDatabase();
			if (!isFavorite) {
				ContentValues contentValue = new ContentValues();
				contentValue.put("num_iid", "123456");
				contentValue.put("thumbnail", "http://t2.qpic.cn/mblogpic/d37a7d6365d47d0808fe/160");
				contentValue.put("title", "米奇**，美国进口，舒适耐用");
				// contentValue.put("url", "");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
				contentValue.put("date",
						formatter.format(new Date(System.currentTimeMillis())));
				db.insert("collection", null, contentValue);
				isFavorite = true;
				Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
				collect.setText("取消收藏");
			} else {
				db.execSQL("delete from collection where num_iid = '"
						+ "123456" + "'");
				isFavorite = false;
				Toast.makeText(this, "删除收藏", Toast.LENGTH_SHORT).show();
				collect.setText("收藏");
			}
		}
	}

	/**
	 * 查询数据库中商品收藏状态
	 * 
	 * @return
	 */
	public boolean queryDB() {
		boolean result = false;
		synchronized (AppApplication.mSQLiteHelper) {
			db = AppApplication.mSQLiteHelper.getWritableDatabase();
			Cursor cursor = db.query("collection", new String[] { "num_iid" },
					"num_iid = ?", new String[] { "123456" }, null, null, null);
			if (cursor == null || cursor.getCount() == 0) {
				result = false;
			} else {
				result = true;
			}
			cursor.close();
		}
		return result;
	}

	public void showAd() {
		// 初始化广告视图
		if (listData.size() < 10 && mAdView == null) {
			mAdView = new AdView(this);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			// 设置广告出现的位置(悬浮于屏幕右下角)
			params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
			// 将广告视图加入 Activity 中
			addContentView(mAdView, params);
		}
	}
}
