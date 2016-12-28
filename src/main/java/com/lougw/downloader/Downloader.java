package com.lougw.downloader;

import android.content.Context;


/**
 * Created by gaoweilou on 2016/8/20.
 */
public class Downloader {

    private static DownloadManager downloadManager;
    private static Context mContext;

    private static class DownloaderInstance {
        private static final Downloader instance = new Downloader();
    }

    private Downloader() {
    }

    public void Init(Context context) {
        this.mContext = context;
        getDownloadManager();

    }

    public DownloadManager getDownloadManager() {
        if (downloadManager == null) {
            downloadManager = new DownloadManager.Builder()
                    .setContext(mContext)
                    .setDownloadThreadNum(Config.DOWNLOAD_THREADS)
                    .setPoolSize(Config.POOL_SIZE).build();
        }
        return downloadManager;
    }

    public Context getContext() {
        return mContext;
    }

    public static Downloader getInstance() {
        return DownloaderInstance.instance;
    }
}
