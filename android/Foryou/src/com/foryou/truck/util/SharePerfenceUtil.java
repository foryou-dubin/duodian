package com.foryou.truck.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.entity.UserContractEntity;
import com.foryou.truck.entity.UserDetailEntity.UserDetail.AccountType;
import com.google.gson.Gson;

public class SharePerfenceUtil {
	public final static String BASE_PERFENCE = "base_perfence";
	public final static String LOG_PERFEENCE = "log_perfence";
	public final static String FIRST_LOGIN = "first_login";
	public final static String KEY = "key";
	public final static String UID = "uid";
	public final static String NAME = "name";
	public final static String MOBILE = "mobile";
	public final static String USER_TYPE = "user_type";
	public final static String USER_GENDER = "user_gender";
	public final static String ACCOUNT_TYPE_KEY = "account_type_key";
	public final static String ACCOUNT_TYPE_TEXT = "account_type_text";

	public final static String GT_CID = "cid";
	public final static String GT_LOADCLASS = "load_class";
	public final static String GT_FYID = "GT_ORDER_ID";
	public final static String SAVE_CALSS_NAME = "class_name";

	public static void SetFirstLogin(Context context, boolean flag) {
		SharedPreferences sp = context.getSharedPreferences(LOG_PERFEENCE, 0);
		sp.edit().putBoolean(FIRST_LOGIN, flag).commit();
	}

	public static boolean getFirstLogin(Context context) {
		SharedPreferences sp = context.getSharedPreferences(LOG_PERFEENCE, 0);
		return sp.getBoolean(FIRST_LOGIN, true);
	}

	public static String getKey(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		return sp.getString(KEY, "");
	}

	public static void setKey(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		sp.edit().putString(KEY, key).commit();
	}

	public static String getUid(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		return sp.getString(UID, "");
	}

	public static void setUid(Context context, String uid) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		sp.edit().putString(UID, uid).commit();
	}

	public static void setName(Context context, String name) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		sp.edit().putString(NAME, name).commit();
	}

	public static String getName(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		return sp.getString(NAME, "");
	}

	public static void setUserType(Context context, String type) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		sp.edit().putString(USER_TYPE, type).commit();
	}

	public static String getUserType(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		return sp.getString(USER_TYPE, "");
	}

	public static void setUserGender(Context context, String gender) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		sp.edit().putString(USER_GENDER, gender).commit();
	}

	public static void setUserAccountType(Context context,
			AccountType accounttype) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		sp.edit().putString(ACCOUNT_TYPE_KEY, accounttype.key)
				.putString(ACCOUNT_TYPE_TEXT, accounttype.text).commit();
	}

	public static AccountType getUserAccountType(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		AccountType accountType = new AccountType();
		accountType.key = sp.getString(ACCOUNT_TYPE_KEY, "");
		accountType.text = sp.getString(ACCOUNT_TYPE_TEXT, "");
		return accountType;
	}

	public static String getUserGender(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		return sp.getString(USER_GENDER, "0");
	}

	public static void setMobile(Context context, String mobile) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		sp.edit().putString(MOBILE, mobile).commit();
	}

	public static String getMobile(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		return sp.getString(MOBILE, "");
	}

	public static boolean IsLogin(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		return !sp.getString(KEY, "").equals("")
				&& !sp.getString(UID, "").equals("");
	}

	public static void ClearAll(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		sp.edit().clear().commit();
	}

	public static void SaveConfigData(Context context, CommonConfigEntity entity) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		Gson gson = new Gson();
		String str = gson.toJson(entity);
		// Log.i("aa","gson:"+str);
		sp.edit().putString("config", str).commit();
	}

	public static CommonConfigEntity getConfigData(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		String config = sp.getString("config", "");
		Gson gson = new Gson();
		CommonConfigEntity entity = gson.fromJson(config,
				CommonConfigEntity.class);
		return entity;
	}
	
	public static void SaveUserContactData(Context context, UserContractEntity entity) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		Gson gson = new Gson();
		String str = gson.toJson(entity);
		// Log.i("aa","gson:"+str);
		sp.edit().putString("user_contact", str).commit();
	}

	public static UserContractEntity getUserConTactData(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		String config = sp.getString("user_contact", "");
		Gson gson = new Gson();
		UserContractEntity entity = gson.fromJson(config,
				UserContractEntity.class);
		return entity;
	}

	public static void SaveGtCid(Context context, String cid) {
		SharedPreferences sp = context.getSharedPreferences(LOG_PERFEENCE, 0);
		sp.edit().putString(GT_CID, cid).commit();
	}

	public static void SaveGtStartClassAndCid(Context context,
			String classname, String id) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		Editor edit = sp.edit();
		edit.putString(GT_LOADCLASS, classname);
		edit.putString(GT_FYID, id).commit();
	}

	public static String getLoadClass(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		return sp.getString(GT_LOADCLASS, "");
	}

	public static String getFYID(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		return sp.getString(GT_FYID, "");
	}

	public static void clearGTMESSAGE(Context context) {
		SharedPreferences sp = context.getSharedPreferences(BASE_PERFENCE, 0);
		Editor edit = sp.edit();
		edit.putString(GT_LOADCLASS, "");
		edit.putString(GT_FYID, "").commit();
	}

	public static String GetGtCid(Context context) {
		SharedPreferences sp = context.getSharedPreferences(LOG_PERFEENCE, 0);
		return sp.getString(GT_CID, "");
	}

}
