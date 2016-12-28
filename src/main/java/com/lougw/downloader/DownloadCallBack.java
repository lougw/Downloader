
package com.lougw.downloader;

import android.content.Context;
import android.content.Intent;

import com.lougw.downloader.service.DownloadService;


public class DownloadCallBack implements DownloadListener {

    public static final String DOWNLOAD_REQUEST = "DOWNLOAD_REQUEST";
    private Intent intent = new Intent();
    private Context mContext;

    public DownloadCallBack(Context context) {
        mContext = context;
    }

    @Override
    public void onEnqueue(DownloadRequest request) {
        DownloadRequestManager.getInstance().add(request);
        DownloadRequestManager.getInstance().notifyDownloadProgressed(request);
//        intent.setAction(DownloadStateBroadcastConfig.ACTION);
//        intent.putExtra(DownloadStateBroadcastConfig.ACTION_MEAN,
//                DownloadStateBroadcastConfig.ON_ENQUEUE);
//        intent.putExtra(DOWNLOAD_REQUEST, request);
//        mContext.sendBroadcast(intent);
    }

    @Override
    public void onStart(DownloadRequest request) {
        DownloadRequestManager.getInstance().add(request);
//        intent.setAction(DownloadStateBroadcastConfig.ACTION);
//        intent.putExtra(DownloadStateBroadcastConfig.ACTION_MEAN,
//                DownloadStateBroadcastConfig.ON_START);
//        intent.putExtra(DOWNLOAD_REQUEST, request);
//        mContext.sendBroadcast(intent);
    }

    @Override
    public void onDownloading(DownloadRequest request) {
        DownloadRequestManager.getInstance().add(request);
    }

    @Override
    public void onError(DownloadRequest request) {
        Downloader.getInstance().getDownloadManager().deleteRequest(request);
        DownloadRequestManager.getInstance().add(request);
        DownloadRequestManager.getInstance().notifyDownloadProgressed(request);
        intent.setAction(DownloadStateBroadcastConfig.ACTION);
        intent.putExtra(DownloadStateBroadcastConfig.ACTION_MEAN,
                DownloadStateBroadcastConfig.ON_ERROR);
        intent.putExtra(DOWNLOAD_REQUEST, request);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onComplete(DownloadRequest request) {
        Downloader.getInstance().getDownloadManager().deleteRequest(request);
        DownloadRequestManager.getInstance().add(request);
        DownloadRequestManager.getInstance().notifyDownloadProgressed(request);
        intent.setAction(DownloadStateBroadcastConfig.ACTION);
        intent.putExtra(DownloadStateBroadcastConfig.ACTION_MEAN,
                DownloadStateBroadcastConfig.ON_COMPLETE);
        intent.putExtra(DOWNLOAD_REQUEST, request);
        mContext.sendBroadcast(intent);
        Intent install = new Intent(mContext, DownloadService.class);
        install.putExtra(DownloadService.DOWNLOAD_REQUEST, request);
        install.putExtra(DownloadService.OP_STATUS,
                DownloadService.OP_STATUS_COMPLETE);
        mContext.startService(install);
    }

    @Override
    public void onPause(DownloadRequest request) {
        Downloader.getInstance().getDownloadManager().deleteRequest(request);
        DownloadRequestManager.getInstance().add(request);
        DownloadRequestManager.getInstance().notifyDownloadProgressed(request);
        intent.setAction(DownloadStateBroadcastConfig.ACTION);
        intent.putExtra(DownloadStateBroadcastConfig.ACTION_MEAN,
                DownloadStateBroadcastConfig.ON_PAUSED);
        intent.putExtra(DOWNLOAD_REQUEST, request);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onDequeue(DownloadRequest request) {
        DownloadRequestManager.getInstance().remove(request);
        intent.setAction(DownloadStateBroadcastConfig.ACTION);
        intent.putExtra(DownloadStateBroadcastConfig.ACTION_MEAN,
                DownloadStateBroadcastConfig.ON_DEQUEUE);
        intent.putExtra(DOWNLOAD_REQUEST, request);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onProgress(DownloadRequest request) {
//        intent.setAction(DownloadStateBroadcastConfig.ACTION);
//        intent.putExtra(DownloadStateBroadcastConfig.ACTION_MEAN,
//                DownloadStateBroadcastConfig.ON_PROGRESS);
//        intent.putExtra(DOWNLOAD_REQUEST, request);
//        mContext.sendBroadcast(intent);
        DownloadRequestManager.getInstance().add(request);
        DownloadRequestManager.getInstance().notifyDownloadProgressed(request);
    }
}
