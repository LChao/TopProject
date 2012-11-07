package com.tianxia.app.healthworld.home;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tianxia.app.healthworld.R;
import com.tianxia.app.healthworld.cache.ConfigCache;
import com.tianxia.app.healthworld.model.AppreciateCategoryInfo;
import com.tianxia.lib.baseworld.activity.AdapterActivity;
import com.tianxia.lib.baseworld.main.MainTabFrame;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpClient;
import com.tianxia.lib.baseworld.sync.http.AsyncHttpResponseHandler;
import com.tianxia.widget.image.SmartImageView;

public class IdentificationTabActivity extends AdapterActivity<AppreciateCategoryInfo> {

	private Button mAppCategotyLeft = null;
    private Button mAppCategotyRight = null;
	
    private LinearLayout mAppLoadingLinearLayout;
    private TextView mAppLoadingTip = null;
    private ProgressBar mAppLoadingPbar = null;

    private SmartImageView mItemImageView = null;
    private TextView mItemTextView = null;
    private TextView mItemCount = null;

    private Intent mIdentificationIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAppCategotyLeft = (Button) findViewById(R.id.app_category_left);
        mAppCategotyRight = (Button) findViewById(R.id.app_category_right);
        mAppCategotyLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppCategotyLeft.setBackgroundResource(R.drawable.app_category_left_selected);
                mAppCategotyRight.setBackgroundResource(R.drawable.app_category_right_normal);
//                if (mFavoriteType == FavoriteType.ARTICLE) {
//                    mFavoriteType = FavoriteType.PICTURE;
//                    showFavoriteList(mFavoriteType);
//                }
            }
        });
        mAppCategotyRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppCategotyLeft.setBackgroundResource(R.drawable.app_category_left_normal);
                mAppCategotyRight.setBackgroundResource(R.drawable.app_category_right_selected);
//                if (mFavoriteType == FavoriteType.PICTURE) {
//                    mFavoriteType = FavoriteType.ARTICLE;
//                    showFavoriteList(mFavoriteType);
//                }
            }
        });

        mAppLoadingLinearLayout = (LinearLayout) findViewById(R.id.app_loading);
        mAppLoadingLinearLayout.getLayoutParams().height = MainTabFrame.mainTabContainerHeight;
        mAppLoadingTip = (TextView) findViewById(R.id.app_loading_tip);
        mAppLoadingPbar = (ProgressBar) findViewById(R.id.app_loading_pbar);

        loadGridView();
    }

    private void loadGridView(){
        String cacheConfigString = ConfigCache.getUrlCache(IdentificationApi.IDENTIFICATION_CONFIG_URL);
        if (cacheConfigString != null) {
            setAppreciateCategoryList(cacheConfigString);
            mAppLoadingLinearLayout.setVisibility(View.GONE);
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(IdentificationApi.IDENTIFICATION_CONFIG_URL, new AsyncHttpResponseHandler(){

                @Override
                public void onStart() {
                    mAppLoadingLinearLayout.setVisibility(View.VISIBLE);
                    mAppLoadingTip.setVisibility(View.VISIBLE);
                    mAppLoadingPbar.setVisibility(View.VISIBLE);
                    listView.setAdapter(null);
                }

                @Override
                public void onSuccess(String result) {
                    mAppLoadingLinearLayout.setVisibility(View.GONE);
                    ConfigCache.setUrlCache(result, IdentificationApi.IDENTIFICATION_CONFIG_URL);
                    setAppreciateCategoryList(result);
                }

                @Override
                public void onFailure(Throwable arg0) {
                    mAppLoadingPbar.setVisibility(View.GONE);
                    mAppLoadingTip.setText(R.string.app_loading_fail);
                }
            });
        }
    }

    private void setAppreciateCategoryList(String jsonString){
        mAppLoadingLinearLayout.setVisibility(View.GONE);
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray jsonArray = json.getJSONArray("list");
            listData = new ArrayList<AppreciateCategoryInfo>();
            AppreciateCategoryInfo appreciateCategoryInfo = null;
            for(int i = 0; i < jsonArray.length(); i++){
                appreciateCategoryInfo = new AppreciateCategoryInfo();
                appreciateCategoryInfo.filename = jsonArray.getJSONObject(i).optString("filename");
                appreciateCategoryInfo.category = jsonArray.getJSONObject(i).optString("category");
                appreciateCategoryInfo.thumbnail = jsonArray.getJSONObject(i).optString("thumbnail");
                appreciateCategoryInfo.count = jsonArray.getJSONObject(i).optString("count");
                listData.add(appreciateCategoryInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new Adapter(IdentificationTabActivity.this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void setLayoutView() {
        setContentView(R.layout.identification_tab_activity);
        setListView(R.id.identification_list);
    }

    @Override
    protected View getView(int position, View convertView) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.identification_tab_list_item, null);
        }

        mItemImageView = (SmartImageView) view.findViewById(R.id.item_image);
        if (listData != null && position < listData.size()){
            mItemImageView.setImageUrl(listData.get(position).thumbnail, R.drawable.app_download_fail, R.drawable.app_download_loading);
        }

        mItemTextView = (TextView) view.findViewById(R.id.item_category);
        mItemTextView.setText(listData.get(position).category + "(" + listData.get(position).count + ")");

        mItemCount = (TextView) view.findViewById(R.id.item_count);
        mItemCount.setText(listData.get(position).count);
        return view;
    }

    @Override
    protected void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//        mIdentificationIntent = new Intent(IdentificationTabActivity.this, AppreciateLatestActivity.class);
//        mIdentificationIntent.putExtra("url", AppreciateApi.APPRECIATE_CATEGORY_BASE_URL + listData.get(position).filename + ".json");
//        mIdentificationIntent.putExtra("title", listData.get(position).category);
//        startActivity(mIdentificationIntent);
    }
}
