
package com.lougw.downloader.utils;

import android.util.Log;

public final class DLogUtil {

    private DLogUtil() {
        super();
    }

    private static boolean isLogable(String tag, int log_level) {
        return true;
        // return Log.isLoggable(tag, log_level);
    }

    public static void log(String msg) {
        String tag = "LOUGW";
        if (isLogable(tag, Log.VERBOSE)) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isLogable(tag, Log.VERBOSE)) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (isLogable(tag, Log.VERBOSE)) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (isLogable(tag, Log.DEBUG)) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (isLogable(tag, Log.DEBUG)) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (isLogable(tag, Log.INFO)) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (isLogable(tag, Log.INFO)) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (isLogable(tag, Log.WARN)) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (isLogable(tag, Log.WARN)) {
            Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (isLogable(tag, Log.ERROR)) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isLogable(tag, Log.ERROR)) {
            Log.e(tag, msg, tr);
        }
    }

}
