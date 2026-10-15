package com.termux.termcore.ftp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.xh_lib.utils.LogUtils;
import com.example.xh_lib.utils.UUtils;
import com.termux.termcore.ftp.server.FtpUser;


public class SaveDataEngine {
    private static String TAG = "SaveDataEngine";

    public static synchronized void putStringData(Context mContext, String key, String values) {
        LogUtils.d(TAG, "putStringData putStringData-----:" + values);
        SharedPreferences mEngineData = mContext.getSharedPreferences("EngineData", Context.MODE_PRIVATE);
        mEngineData.edit().putString(key, values).apply();
    }

    public static String getStringData(Context mContext, String key) {
        SharedPreferences mEngineData = mContext.getSharedPreferences("EngineData", Context.MODE_PRIVATE);
        return mEngineData.getString(key, "def");
    }

    public static boolean isEmpty(String key) {
        Context appContext = UUtils.getContext();
        if (appContext == null) {
            LogUtils.d(TAG, "getFtpUser appContext is null return false");
            return false;
        }
        String stringData = SaveDataEngine.getStringData(appContext, key);
        return TextUtils.isEmpty(stringData) || "def".equals(stringData);
    }

    public static FtpUser getFtpUser() {
        LogUtils.d(TAG, "getFtpUser context:" + UUtils.getContext());
        Context appContext = UUtils.getContext();
        if (appContext == null) {
            LogUtils.d(TAG, "getFtpUser appContext is null return def FtpUser.");
            return new FtpUser("ftp", "ftp", "def");
        }
        String ftpUserName = SaveDataEngine.getStringData(appContext, "ftpUserName");
        String ftpPassWord = SaveDataEngine.getStringData(appContext, "ftpPassWord");
        String ftpChroot = SaveDataEngine.getStringData(appContext, "ftpChroot");
        return new FtpUser(ftpUserName, ftpPassWord, ftpChroot);
    }

    public static int getPort() {
        Context appContext = UUtils.getContext();
        if (appContext == null) {
            LogUtils.d(TAG, "getFtpUser appContext is null return def FtpUser.");
            return 2121;
        }
        String ftpPort = SaveDataEngine.getStringData(appContext, "ftpPort");
        LogUtils.d(TAG,"getPort:" + ftpPort);
        if (isEmpty("ftpPort")) {
            return 2121;
        }
        int port = 2121;
        try {
            port = Integer.parseInt(ftpPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return port;
    }


}
