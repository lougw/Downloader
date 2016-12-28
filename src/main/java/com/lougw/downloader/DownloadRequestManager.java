
package com.lougw.downloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadRequestManager {

    private DownloadManager downloadManager = Downloader.getInstance().getDownloadManager();

    private static class DownloadRequestManagerInstance {
        private static final DownloadRequestManager instance = new DownloadRequestManager();
    }

    private DownloadRequestManager() {
        List<DownloadRequest> downloadRequests = downloadManager.queryAllDownLoads();
        if (null != downloadRequests) {
            for (DownloadRequest request : downloadRequests) {
                mDownloadMap.put(request.getGuid(), request);
            }
        }
    }

    private List<DownloadObserver> mObservers = new ArrayList<DownloadObserver>();
    private static Map<String, DownloadRequest> mDownloadMap = new ConcurrentHashMap<String, DownloadRequest>();

    public static DownloadRequestManager getInstance() {
        return DownloadRequestManagerInstance.instance;
    }

    /**
     * 注册观察者
     */
    public void registerObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (!mObservers.contains(observer)) {
                mObservers.add(observer);
            }
        }
    }

    /**
     * 反注册观察者
     */
    public void unRegisterObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (mObservers.contains(observer)) {
                mObservers.remove(observer);
            }
        }
    }


    /**
     * 当下载进度发送改变的时候回调
     */
    public void notifyDownloadProgressed(final DownloadRequest request) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadProgressed(request);
            }
        }
    }


    public interface DownloadObserver {
        public void onDownloadProgressed(DownloadRequest request);
    }


    public void add(DownloadRequest request) {
        synchronized (mDownloadMap) {
            mDownloadMap.put(request.getGuid(), request);
        }
    }

    public void remove(DownloadRequest request) {
        synchronized (mDownloadMap) {
            mDownloadMap.remove(request.getGuid());
        }
    }


    public DownloadRequest getRequestByPackageName(String packageName) {
        synchronized (mDownloadMap) {
            return mDownloadMap.get(packageName);
        }
    }

    public Map<String, DownloadRequest> getAllDownload() {
        synchronized (mDownloadMap) {
            return mDownloadMap;
        }
    }

    public boolean downloading() {
        boolean downloading = false;
        if (mDownloadMap.isEmpty()) {
            return downloading;
        }
        for (Map.Entry<String, DownloadRequest> entry : mDownloadMap.entrySet()) {
            DownloadRequest downloadRequest = entry.getValue();
            if (DownloadStatus.STATUS_START.equals(downloadRequest.getDownloadStatus()) || DownloadStatus.STATUS_IDLE.equals(downloadRequest.getDownloadStatus()) || DownloadStatus.STATUS_DOWNLOADING.equals(downloadRequest.getDownloadStatus())) {
                downloading = true;
                break;
            }
        }
        return downloading;
    }

}
