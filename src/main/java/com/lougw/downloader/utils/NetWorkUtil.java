package com.lougw.downloader.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetWorkUtil {
    /**
     * 获取是否有网络
     *
     * @return
     */
    public static boolean hasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isAvailable()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * WiFi是否已连接
     *
     * @param context 上下文
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        // type mobile = 0, type WIFI = 1;
        if (wifiState == State.CONNECTED
                && (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
            return true;
        }
        return false;
    }

    /**
     * Mobile是否已连接
     *
     * @param context 上下文
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();
        NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
        // type mobile = 0, type WIFI = 1;
        if (wifiState == State.CONNECTED
                && (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
            return true;
        }
        return false;
    }

}
