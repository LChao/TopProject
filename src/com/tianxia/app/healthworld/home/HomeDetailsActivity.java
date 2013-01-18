package com.tianxia.app.healthworld.home;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.utils.FinalBitmap;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.utils.NetworkUtils;

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
	private String umPrice;
	private String tradeCount;
	private String desc;
	private String spreadUrl;
	// 界面数据显示View控件
	private TextView goodsSales;
	private TextView goodsPrice;
	private TextView goodsUMPrice;
	private TextView goodsName;
	private WebView goodsDesc;
	private WebView goodsEvaluate;
	// 顶部banner
	private ImageView mAppBackButton = null;
	// 顶部广告栏
	// private LinearLayout mAdContainer = null;
	// private HomeDetailsAdCompanyInfo mAdCompanyInfo;
	// private TextView mAdCompanyName = null;
	// private TextView mAdCompanyContact = null;
	// private TextView mAdCompanyAddress = null;
	// private TextView mAdCompanyTel = null;
	// private TextView mAdCompanyPhone = null;
	// private TextView mAdCompanyBusiness = null;
	// 底部按钮栏
	private ImageView collect;
	private ImageView buy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fb = FinalBitmap.create(this, AppApplication.mSdcardImageDir);
		fb.configLoadingImage(R.drawable.app_download_loading);
		fb.configCalculateBitmapSizeWhenDecode(true);
		// fb = new FinalBitmap(this).init();
		// fb.configLoadingImage(R.drawable.app_download_loading);
		// fb.configLoadfailImage(R.drawable.gallery_it);
		// 这里可以进行其他十几项的配置，也可以不用配置，配置之后必须调用init()函数,才生效
		// fb.configDiskCachePath(AppApplication.mSdcardImageDir);
		// fb.init();
		// fb.configBitmapLoadThreadSize(int size)

		imageWidth = (int) (AppApplication.screenWidth * 0.8);
		db = AppApplication.mSQLiteHelper.getWritableDatabase();

		intentData = getIntent().getExtras();
		cid = intentData.getString("cid");
		sResUrl = intentData.getStringArray("sResUrl");
		name = intentData.getString("name");
		price = intentData.getString("price");
		umPrice = intentData.getString("umPrice");
		tradeCount = intentData.getString("tradeCount");
		desc = intentData.getString("desc");
		spreadUrl = intentData.getString("spreadUrl");

		goodsSales = (TextView) findViewById(R.id.home_details_goods_sales);
		goodsPrice = (TextView) findViewById(R.id.home_details_goods_price);
		goodsPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		goodsUMPrice = (TextView) findViewById(R.id.home_details_goods_umPrice);
		goodsName = (TextView) findViewById(R.id.home_details_goods_name);
		goodsDesc = (WebView) findViewById(R.id.home_details_tv_describe);
		goodsEvaluate = (WebView) findViewById(R.id.home_details_tv_evaluate);
		if (umPrice.equals("")) {
			goodsUMPrice.setText("￥" + price);
			goodsPrice.setVisibility(View.GONE);
		} else {
			goodsUMPrice.setText("￥" + umPrice);
			goodsPrice.setText("￥" + price);
		}
		goodsName.setText(name);
		goodsSales.setText(tradeCount + "件");
		goodsDesc.getSettings().setDefaultTextEncodingName("utf-8");
		goodsDesc.loadData(desc, "text/html;charset=UTF-8", "utf-8");
		goodsEvaluate.getSettings().setJavaScriptEnabled(true);
		goodsEvaluate.setWebViewClient(new MyWebChrome());
		if (BaseApplication.mNetWorkState != NetworkUtils.NETWORN_NONE) {
			goodsEvaluate.loadUrl(HomeApi.HOME_GOODS_COMMENTS_URL + cid);
		} else {
			Toast.makeText(HomeDetailsActivity.this, "无可用网络连接", 0).show();
		}
		collect = (ImageView) findViewById(R.id.home_details_bt_collect);
		buy = (ImageView) findViewById(R.id.home_details_bt_buy);
		mAppBackButton = (ImageView) findViewById(R.id.app_back);

		mAppBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
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
	// private void setHeaderView() {
	// View view = LayoutInflater.from(this).inflate(
	// R.layout.home_ad_company_shop, null);
	// mAdCompanyName = (TextView) view.findViewById(R.id.ad_company_name);
	// mAdCompanyContact = (TextView) view
	// .findViewById(R.id.ad_company_contact);
	// mAdCompanyAddress = (TextView) view
	// .findViewById(R.id.ad_company_address);
	// mAdCompanyTel = (TextView) view.findViewById(R.id.ad_company_tel);
	// mAdCompanyPhone = (TextView) view.findViewById(R.id.ad_company_phone);
	// mAdCompanyBusiness = (TextView) view
	// .findViewById(R.id.ad_company_business);
	//
	// mAdCompanyName.setText(mAdCompanyInfo.name);
	// mAdCompanyContact.setText(mAdCompanyInfo.contact);
	// mAdCompanyAddress.setText(mAdCompanyInfo.address);
	// mAdCompanyTel.setText(mAdCompanyInfo.tel);
	// mAdCompanyPhone.setText(mAdCompanyInfo.phone);
	// mAdCompanyBusiness.setText(mAdCompanyInfo.business);
	// mAdContainer.removeAllViews();
	// mAdContainer.addView(view);
	// }

	@Override
	protected View getView(int position, View convertView, ViewGroup parent) {
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

		fb.display(holder.detailImage, listData.get(position));
		return convertView;
	}

	static class ViewHolder {
		ImageView detailImage;
	}

	@Override
	protected void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
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
				contentValue.put("mResUrl", sResUrl[0]);
				contentValue.put("price", price);
				contentValue.put("tradeCount", tradeCount);
				contentValue.put("spreadUrl", spreadUrl);
				// SimpleDateFormat formatter = new
				// SimpleDateFormat("yyyy年MM月dd日");
				// contentValue.put("date",
				// formatter.format(new Date(System.currentTimeMillis())));
				db.insert("collection", null, contentValue);
				isFavorite = true;
				Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
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

	class MyWebChrome extends WebViewClient {
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			// LinearLayout.LayoutParams params = (LayoutParams) view
			// .getLayoutParams();
			// params.bottomMargin = (int) (3 * BaseApplication.screenDensity);
			// view.setLayoutParams(params);
			super.onPageFinished(view, url);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		fb.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		fb.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// fb.onDestroy();
		super.onDestroy();
	}
}
