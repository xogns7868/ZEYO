package com.zeyo.android.ClinetManage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginMaintain{

    static final String PREF_USER_ID = "userID";
    static final String PREF_USER_PW = "userPW";

    static SharedPreferences getLogin(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    // 계정 정보 저장
    public static void setUserSignInfo(Context ctx, String userID,String userPW) {
        SharedPreferences.Editor editor = getLogin(ctx).edit();
        editor.putString(PREF_USER_ID, userID);
        editor.putString(PREF_USER_PW, userPW);
        editor.commit();
    }

    // 저장된 정보 가져오기
    public static JSONObject getUserSignInfo(Context ctx) throws JSONException {
        String ID = getLogin(ctx).getString(PREF_USER_ID, "");
        String PW = getLogin(ctx).getString(PREF_USER_PW,"");
        JSONObject infoReturn=new JSONObject();// = getLogin(ctx).getString(PREF_USER_ID, "")+getLogin(ctx).getString(PREF_USER_PW,"");

        infoReturn.put("ID",ID);
        infoReturn.put("PW",PW);
        return infoReturn;
    }

    // 로그아웃
    public static void clearUserSignInfo(Context ctx) {
        SharedPreferences.Editor editor = getLogin(ctx).edit();
        editor.clear();
        editor.commit();
    }
}