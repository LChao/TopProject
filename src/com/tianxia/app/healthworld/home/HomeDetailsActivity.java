package com.tianxia.app.healthworld.home;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.model.HomeDetailsAdCompanyInfo;
import com.tianxia.app.healthworld.utils.FinalBitmap;
import com.tianxia.lib.baseworld.activity.AdapterActivity;

public class HomeDetailsActivity extends AdapterActivity<String> {

	private static final String TAG = "HomeDetailsActivity";

	private SQLiteDatabase db;
	private Boolean isFavorite;
	private FinalBitmap fb;
	// 附图item宽度
	private int imageWidth;
	// 界面之间传送的数据
	private Bundle intentData;
	private String cid;
	private String[] sResUrl;
	private String name;
	private String price;
	private String tradeCount;
	private String desc;
	private String evaluation;
	private String mResUrl;
	private String spreadUrl;
	// 界面数据显示View控件
	private TextView goodsSales;
	private TextView goodsPrice;
	private TextView goodsName;
	private TextView goodsDesc;
	private TextView goodsEvaluate;
	// 顶部banner
	private ImageView mAppBackButton = null;
	private ProgressBar mAppLoadingPbar = null;
	private ImageView mAppLoadingImage = null;
	// 顶部广告栏
	private LinearLayout mAdContainer = null;
	private HomeDetailsAdCompanyInfo mAdCompanyInfo;
	private TextView mAdCompanyName = null;
	private TextView mAdCompanyContact = null;
	private TextView mAdCompanyAddress = null;
	private TextView mAdCompanyTel = null;
	private TextView mAdCompanyPhone = null;
	private TextView mAdCompanyBusiness = null;
	// 底部按钮栏
	private ImageView collect;
	private ImageView buy;

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

		imageWidth = (int) (AppApplication.screenWidth * 0.7);
		db = AppApplication.mSQLiteHelper.getWritableDatabase();

		intentData = getIntent().getExtras();
		cid = intentData.getString("cid");
		sResUrl = intentData.getStringArray("sResUrl");
		name = intentData.getString("name");
		price = intentData.getString("price");
		tradeCount = intentData.getString("tradeCount");
		desc = intentData.getString("desc");
		evaluation = intentData.getString("evaluation");
		mResUrl = intentData.getString("mResUrl");
		spreadUrl = intentData.getString("spreadUrl");

		goodsSales = (TextView) findViewById(R.id.home_details_goods_sales);
		goodsPrice = (TextView) findViewById(R.id.home_details_goods_price);
		goodsName = (TextView) findViewById(R.id.home_details_goods_name);
		goodsDesc = (TextView) findViewById(R.id.home_details_tv_describe);
		goodsEvaluate = (TextView) findViewById(R.id.home_details_tv_evaluate);
		goodsSales.setText(tradeCount + "件");
		goodsPrice.setText("￥" + price);
		goodsName.setText(name);
		goodsDesc.setText(desc);
		goodsEvaluate.setText(evaluation);

		collect = (ImageView) findViewById(R.id.home_details_bt_collect);
		buy = (ImageView) findViewById(R.id.home_details_bt_buy);
		mAdContainer = (LinearLayout) findViewById(R.id.home_details_ad);
		mAppBackButton = (ImageView) findViewById(R.id.app_back);
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
				// loadGridView();
			}
		});

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
				Intent in = new Intent(HomeDetailsActivity.this,
						HomeTaobaoWebview.class);
				in.putExtra("url", spreadUrl);
				startActivity(in);
			}
		});

		isFavorite = queryDB();
		if (isFavorite) {
			collect.setBackgroundResource(R.drawable.home_details_bt_delete_bg);
		} else {
			collect.setBackgroundResource(R.drawable.home_details_bt_collect_bg);
		}

		for (int i = 0; i < sResUrl.length; i++) {
			listData.add(sResUrl[i]);
		}
		adapter = new Adapter(HomeDetailsActivity.this);
		listView.setAdapter(adapter);
		// loadGridView();
	}

	// private void loadGridView() {
	// mAppLoadingTip.setText(R.string.app_loading);
	// String cacheConfigString = ConfigCache.getUrlCache(mUrl);
	// if (cacheConfigString != null) {
	// setAppreciateLatestList(cacheConfigString);
	// mAppLoadingTip.setVisibility(View.GONE);
	// mAppLoadingPbar.setVisibility(View.GONE);
	// mAppLoadingImage.setVisibility(View.VISIBLE);
	// } else {
	// AsyncHttpClient client = new AsyncHttpClient();
	// client.get(mUrl, new AsyncHttpResponseHandler() {
	//
	// @Override
	// public void onStart() {
	// listView.setAdapter(null);
	// mAppLoadingTip.setVisibility(View.VISIBLE);
	// mAppLoadingPbar.setVisibility(View.VISIBLE);
	// mAppLoadingImage.setVisibility(View.GONE);
	// }
	//
	// @Override
	// public void onSuccess(String result) {
	// mAppLoadingTip.setVisibility(View.GONE);
	// ConfigCache.setUrlCache(result, mUrl);
	// setAppreciateLatestList(result);
	// }
	//
	// @Override
	// public void onFailure(Throwable arg0) {
	// mAppLoadingTip.setText(R.string.app_loading_fail);
	// }
	//
	// @Override
	// public void onFinish() {
	// mAppLoadingPbar.setVisibility(View.GONE);
	// mAppLoadingImage.setVisibility(View.VISIBLE);
	// }
	// });
	// }
	// }

	// private void setAppreciateLatestList(String jsonString) {
	// try {
	// JSONObject json = new JSONObject(jsonString);
	// JSONObject adCompanyJsonObject = json.optJSONObject("ad-company");
	// if (adCompanyJsonObject != null) {
	// mAdCompanyInfo = new HomeDetailsAdCompanyInfo();
	// mAdCompanyInfo.name = adCompanyJsonObject.optString("name");
	// mAdCompanyInfo.contact = adCompanyJsonObject
	// .optString("contact");
	// mAdCompanyInfo.address = adCompanyJsonObject
	// .optString("address");
	// mAdCompanyInfo.tel = adCompanyJsonObject.optString("tel");
	// mAdCompanyInfo.phone = adCompanyJsonObject.optString("phone");
	// mAdCompanyInfo.business = adCompanyJsonObject
	// .optString("business");
	// setHeaderView();
	// }
	//
	// JSONArray jsonArray = json.getJSONArray("list");
	// listData = new ArrayList<HomeDetailsInfo>();
	// HomeDetailsInfo appreciateLatestInfo = null;
	//
	// for (int i = 0; i < jsonArray.length(); i++) {
	// appreciateLatestInfo = new HomeDetailsInfo();
	// appreciateLatestInfo.prefix = jsonArray.getJSONObject(i)
	// .optString("prefix");
	// appreciateLatestInfo.title = jsonArray.getJSONObject(i)
	// .optString("title");
	// appreciateLatestInfo.origin = jsonArray.getJSONObject(i)
	// .optString("origin");
	// appreciateLatestInfo.thumbnail = jsonArray.getJSONObject(i)
	// .optString("thumbnail");
	// appreciateLatestInfo.tag = jsonArray.getJSONObject(i)
	// .optString("tag");
	// appreciateLatestInfo.category = jsonArray.getJSONObject(i)
	// .optString("category");
	// listData.add(appreciateLatestInfo);
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// adapter = new Adapter(HomeDetailsActivity.this);
	// listView.setAdapter(adapter);
	// showAd();
	// }

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
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.home_details_gallery_item, null);
			holder = new ViewHolder();

			holder.detailImage = (ImageView) convertView
					.findViewById(R.id.item_image);
			holder.detailImage.setLayoutParams(new LinearLayout.LayoutParams(
					imageWidth, imageWidth));

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// mItemSmartImageView.setLayoutParams(new LinearLayout.LayoutParams(
		// width, LayoutParams.FILL_PARENT));
		// mItemSmartImageView.setScaleType(ScaleType.CENTER_CROP);
		// mItemSmartImageView.setImageUrl(listData.get(position).origin,
		// R.drawable.app_download_fail_large,
		// R.drawable.app_download_loading_large, true);
		// mItemSmartImageView.setImageUrl(listData.get(position).origin,
		// R.drawable.app_download_fail_large,
		// R.drawable.app_download_loading_large, true);
		// mItemSmartImageView.setScaleType(ScaleType.CENTER);
		fb.display(holder.detailImage, listData.get(position));
		// holder.detailImage
		// .setImageResource((position & 1) == 1 ? R.drawable.gallery_it
		// : R.drawable.griditem1);
		return convertView;
	}

	static class ViewHolder {

		ImageView detailImage;

	}

	@Override
	protected void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// Toast.makeText(this, "position" + position, 0).show();
	}

	/**
	 * 改变商品再数据库中的收藏状态
	 */
	public void favorite() {
		synchronized (AppApplication.mSQLiteHelper) {
			db = AppApplication.mSQLiteHelper.getWritableDatabase();
			if (!isFavorite) {
				ContentValues contentValue = new ContentValues();
				contentValue.put("num_iid", cid);
				contentValue.put("mResUrl", mResUrl);
				contentValue.put("price", price);
				contentValue.put("tradeCount", tradeCount);
				contentValue.put("spreadUrl", spreadUrl);
				// SimpleDateFormat formatter = new
				// SimpleDateFormat("yyyy年MM月dd日");
				// contentValue.put("date",
				// formatter.format(new Date(System.currentTimeMillis())));
				db.insert("collection", null, contentValue);
				isFavorite = true;
				Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
				collect.setBackgroundResource(R.drawable.home_details_bt_delete_bg);
			} else {
				db.execSQL("delete from collection where num_iid = '" + cid
						+ "'");
				isFavorite = false;
				Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
				collect.setBackgroundResource(R.drawable.home_details_bt_collect_bg);
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
					"num_iid = ?", new String[] { cid }, null, null, null);
			if (cursor == null || cursor.getCount() == 0) {
				result = false;
			} else {
				result = true;
			}
			cursor.close();
		}
		return result;
	}

}
