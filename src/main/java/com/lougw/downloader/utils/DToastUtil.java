package com.lougw.downloader.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.lougw.downloader.DownloadUtils;
import com.lougw.downloader.Downloader;

/**
 * 管理toast的类，整个app用该类来显示toast
 */
public class DToastUtil {


    public static final int LENGTH_LONG = Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    /**
     * The handler.
     */
    private static Handler handler = new Handler(Looper.getMainLooper());
    /**
     * The toast.
     */
    private static Toast toast = null;

    /**
     * 显示短Toast
     *
     * @param context
     * @param text
     */
    public static void showS(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示短Toast
     *
     * @param context
     * @param resId
     */
    public static void showS(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示长Toast
     *
     * @param context
     * @param resId
     */
    public static void showL(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示长Toast
     *
     * @param context
     * @param text
     */
    public static void showL(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(final Context act, final String msg) {
        showMessage(act, TextUtils.isEmpty(msg) ? "" : msg, DToastUtil.LENGTH_SHORT);
    }

    public static void showMessage(final Context act, final int msg) {
        showMessage(act, msg, DToastUtil.LENGTH_SHORT);
    }

    public static void showMessage(final int msg) {
        showMessage(Downloader.getInstance().getContext(), msg, DToastUtil.LENGTH_SHORT);

    }

    public static void showMessage(final int msg, Object... formatArgs) {
        showMessage(Downloader.getInstance().getContext().getResources().getString(msg, formatArgs));
    }

    public static void showMessage(String msg) {
        showMessage(Downloader.getInstance().getContext(), TextUtils.isEmpty(msg) ? "" : msg,
                DToastUtil.LENGTH_SHORT);
    }

    private static void showMessage(final Context act, final String msg,
                                    final int len) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast = Toast.makeText(act, msg, len);
                toast.show();
            }
        });
    }

    private static void showMessage(final Context act, final int msg,
                                    final int len) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast = Toast.makeText(act, msg, len);
                toast.show();
            }
        });
    }

    public static void showToast(final Context act, final String msg,
                                 final int len) {
        if (toast == null) {
            toast = Toast.makeText(act, msg, len);
        } else {
            toast.setText(msg);
            toast.setDuration(len);
        }
        toast.show();
        // Toast.makeText(act, msg, len).show();
    }

    public static void showToast(final Context act, final int msg,
                                 final int len) {
        if (toast == null) {
            toast = Toast.makeText(act, msg, len);
        } else {
            toast.setText(msg);
            toast.setDuration(len);
        }
        toast.show();
        // Toast.makeText(act, msg, len).show();
    }


}
