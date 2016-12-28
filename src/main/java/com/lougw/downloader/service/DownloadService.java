
package com.lougw.downloader.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.lougw.downloader.DownloadManager;
import com.lougw.downloader.DownloadRequest;
import com.lougw.downloader.DownloadUtils;
import com.lougw.downloader.Downloader;
import com.lougw.downloader.R;
import com.lougw.downloader.utils.NetWorkUtil;
import com.lougw.downloader.utils.DToastUtil;

import java.lang.ref.WeakReference;

public class DownloadService extends IntentService {
    private final String TAG = getClass().getSimpleName();
    public static final String DOWNLOAD_REQUEST = "DOWNLOAD_REQUEST";
    public static final String OP_STATUS = "OP_STATUS";
    public static final int OP_STATUS_START = 0;
    public static final int OP_STATUS_COMPLETE = 1;
    /**
     * 大容量限制
     */
    public static final int OP_STATUS_DOWNLOAD_LIMIT = 3;
    public static final int OP_STATUS_DOWNLOAD_WIFI_LIMIT = 4;
    private IncomingHandler mHandler;
    private DownloadManager downloadManager;

    public DownloadService() {
        super("downloadservice");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadManager = Downloader.getInstance().getDownloadManager();
        mHandler = new IncomingHandler(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final DownloadRequest request = (DownloadRequest) intent
                    .getSerializableExtra(DOWNLOAD_REQUEST);
            int operationStatus = intent.getIntExtra(OP_STATUS, 0);
            switch (operationStatus) {
                case OP_STATUS_START:
                    downloadManager.enqueue(request);
                    break;
                case OP_STATUS_COMPLETE:
                    break;
                case OP_STATUS_DOWNLOAD_LIMIT:
                    Downloader.getInstance().getDownloadManager().pause(request.getDownLoadItem());
                    break;
                case OP_STATUS_DOWNLOAD_WIFI_LIMIT:
                    Downloader.getInstance().getDownloadManager().pause(request.getDownLoadItem());
                    mHandler.sendMessage(mHandler.obtainMessage(
                            OP_STATUS_DOWNLOAD_WIFI_LIMIT, request));
                    break;
            }
        }
    }


    /**
     * @Title:handler static WeakReference 避免内存泄露
     * @Description:
     */
    private static class IncomingHandler extends Handler {
        private final WeakReference<DownloadService> mClass;

        IncomingHandler(DownloadService className) {
            mClass = new WeakReference<DownloadService>(className);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadService className = mClass.get();
            if (className != null) {
                className.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message m) {
        if (m.what == OP_STATUS_DOWNLOAD_LIMIT) {
            if (!NetWorkUtil.hasNetwork(Downloader.getInstance().getContext())) {
                DToastUtil.showL(Downloader.getInstance().getContext(), R.string.download_no_network);
            } else {
                DToastUtil.showL(Downloader.getInstance().getContext(), R.string.download_limit_flow);

            }
        } else if (m.what == OP_STATUS_DOWNLOAD_WIFI_LIMIT) {
            DToastUtil.showS(DownloadService.this, R.string.wlan_notice);
        }
    }

}
