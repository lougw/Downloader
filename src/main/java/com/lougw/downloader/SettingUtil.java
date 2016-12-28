
package com.lougw.downloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.io.File;

/**
 * @Description:设置界面的相关工具方法
 */
public class SettingUtil {

    public static final String DOWNLOAD_IN_WIFI = "download_in_wifi";

    private static SharedPreferences sp;

    private static SharedPreferences getPreferences(Context context) {
        if (sp == null) {
            sp = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sp;
    }

    /**
     * @Description:设置只在wifi网络下下载
     */
    public static void setDownloadInWifi(Context context, boolean inWifi) {
        SharedPreferences sp = getPreferences(context);
        Editor editor = sp.edit();
        editor.putBoolean(DOWNLOAD_IN_WIFI, inWifi);
        editor.commit();
    }

    /**
     * @Description:设置 wifi网络下下载 值状态
     */
    public static boolean getDownloadInWifi(Context context) {
        SharedPreferences sp = getPreferences(context);
        return sp.getBoolean(DOWNLOAD_IN_WIFI, true);
    }


}
