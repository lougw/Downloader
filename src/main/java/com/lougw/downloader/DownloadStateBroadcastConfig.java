
package com.lougw.downloader;

public class DownloadStateBroadcastConfig {
    public static final String ACTION = "com.lougw.app.ACTION_DOWNLOAD";

    public static final String ACTION_MEAN = "action_mean";

    public static final String ON_ENQUEUE = "onEnqueue";
    public static final String ON_START = "onStart";
    public static final String ON_COMPLETE = "onComplete";
    public static final String ON_ERROR = "onError";
    public static final String ON_PAUSED = "onPaused";
    public static final String ON_DEQUEUE = "onDequeue";
    public static final String ON_PROGRESS = "onProgress";

    public static final String ACTION_INSTALLED_APP_CHANGE = "action_installed_app_change"; // 已安装列表有更新

    public static final String ACTION_APK_FILE_DELETE = "action_apk_file_delete";// apk

    public static final String ACTION_APK_INSTALLING = "action_apk_installing";// 安装中

    public static final String ACTION_APK_CLICK_INSTALLING = "action_apk_click_installing";// 安装中

    public static final String ACTION_NEW_APP_UPDATE = "action_new_app_update";// 已有更新

}
