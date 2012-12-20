package com.tianxia.app.healthworld;

import android.content.Context;

import com.tianxia.lib.baseworld.db.BaseSQLiteHelper;

public class AppSQLiteHelper extends BaseSQLiteHelper {

	public static final String TABLE_COLLECT = "collection";

	public AppSQLiteHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void InitCreateSql() {
		mCreateSql = "create table if not exists " + TABLE_COLLECT + "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT," + "num_iid INTEGER,"
				+ "mResUrl TEXT," + "spreadUrl TEXT," + "price TEXT,"
				+ "tradeCount TEXT)";

	}
}
