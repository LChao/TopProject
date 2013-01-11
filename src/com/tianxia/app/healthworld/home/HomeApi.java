package com.tianxia.app.healthworld.home;

import com.tianxia.app.healthworld.AppApplication;

public class HomeApi {

	public static String HOME_GOODS_URL = AppApplication.mDomain
			+ "androidclient/bothsex/bothsex!queryCommodity";

	public static String HOME_GOODS_COMMENTS_URL = AppApplication.mDomain
			+ "androidclient/bothsex/bothsex-comment?x";

	public static String FEEDBACK_URL = AppApplication.mDomain
			+ "androidclient/bothsex/bothsex!addSuggestion";

	public static String CHECK_VERSION_URL = AppApplication.mDomain
			+ "upgradeClient.xml";
	// public static String HOME_DETAILS_URL =
	// "http://1.kaiyuanxiangmu.sinaapp.com/floworld/data/json/appreciate/category/shaoyao.json";
}