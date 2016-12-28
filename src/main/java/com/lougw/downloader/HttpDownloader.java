/*******************************************************************************
 * Copyright 2011-2013
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.lougw.downloader;

import android.content.Context;

import com.lougw.downloader.db.DownloadDataBase;
import com.lougw.downloader.utils.DLogUtil;

import java.io.File;


/**
 * 下载器
 */
public class HttpDownloader implements Runnable {
    private static final String TAG = HttpDownloader.class.getSimpleName();
    private static HttpDownloader httpDownloader;
    private DownloadThread thread;
    private DownloadStatus status;
    private volatile long downloadSize;
    private boolean isCancel;
    private String mSrcUri;
    private DownloadRequest mDownloadRequest;
    private DownloadDataBase downloadDataBase;
    ;

    private HttpDownloader(Context context, DownloadRequest request) {
        mDownloadRequest = request;
        downloadSize = mDownloadRequest.getDownloadSize();
        mSrcUri = request.getSrcUri();
    }

    public static HttpDownloader create(Context context, DownloadRequest request) {
        httpDownloader = new HttpDownloader(context, request);
        return httpDownloader;
    }

    public HttpDownloader setDownloadDatabase(DownloadDataBase downloadDataBase) {
        this.downloadDataBase = downloadDataBase;
        return httpDownloader;
    }

    public void doDownload() {
        DLogUtil.i(TAG, getStatus().toString() + "----do download---"
                + mDownloadRequest.getDownLoadItem().getGuid());
        if (getStatus() == DownloadStatus.STATUS_IDLE) {
            mDownloadRequest.setDownloadStatus(DownloadStatus.STATUS_START);
            // LogUtil.e("CAQ", "doDownload  " + mDownloadRequest.getSrcUri() +
            // " start");
            updateRequest(mDownloadRequest);
            thread = new DownloadThread(this, mDownloadRequest, 0);
            thread.start();
            long lastDownloadSize = 0;
            long downloadLength = 0;
            while (true) {
                sleep(500);
                status = getStatus();
                DLogUtil.i(TAG, status.toString() + "----request name---"
                        + mDownloadRequest.getDownLoadItem().getGuid());
                if (DownloadStatus.STATUS_PAUSE == status
                        || DownloadStatus.STATUS_ERROR == status
                        || DownloadStatus.STATUS_COMPLETE == status
                        || DownloadStatus.STATUS_DELETE == status
                        || isAllFinish()) {

                    break;
                }

                downloadLength = downloadSize - lastDownloadSize;
                if (downloadLength >= 0) {
                    if (lastDownloadSize == 0) {
                        mDownloadRequest.setSpeed(0);
                        if (status == DownloadStatus.STATUS_START) {
                            downloadDataBase.progress(mDownloadRequest);
                            setStatus(DownloadStatus.STATUS_DOWNLOADING);
                            updateRequest(mDownloadRequest);
                        }
                    } else {
                        mDownloadRequest.setSpeed(downloadLength * 2);// downloadlength*1000/500
                    }
                    mDownloadRequest.setDownloadStatus(status);
                    downloadDataBase.progress(mDownloadRequest);
                }
                lastDownloadSize = downloadSize;
                checkThread();

            }
        }
        DLogUtil.e(TAG, "@" + hashCode() + "===> HttpDownload task finish");
        if (isFinish()) {
            mDownloadRequest.setDownloadStatus(DownloadStatus.STATUS_COMPLETE);
            updateRequest(mDownloadRequest);
        } else {
            downloadDataBase.update(mDownloadRequest);
        }
    }

    private void updateRequest(DownloadRequest request) {
        downloadDataBase.update(request);
    }

    /**
     * 睡眠当前线程
     *
     * @param time 睡眠时间
     */
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取下载器状态
     *
     * @return
     */
    public DownloadStatus getStatus() {
        return mDownloadRequest.getDownloadStatus();
    }

    /**
     * 设置下载器状态
     *
     * @param status
     */
    public void setStatus(DownloadStatus status) {
        this.status = status;
        mDownloadRequest.setDownloadStatus(status);
    }

    /**
     * 累计已下载大小
     *
     * @param size
     */
    synchronized protected void append(long size) {
        if (DownloadStatus.STATUS_ERROR != status) {
            downloadSize += size;
        }
        if (mDownloadRequest.getTotalSize() != 0 && downloadSize >= mDownloadRequest.getTotalSize()) {
            mDownloadRequest.setDownloadSize(downloadSize);
            mDownloadRequest.setDownloadStatus(DownloadStatus.STATUS_COMPLETE);
            // updateRequest(mDownloadRequest);
        } else if (downloadSize - mDownloadRequest.getDownloadSize() > 128 * 1024) {
            mDownloadRequest.setDownloadSize(downloadSize);
            updateRequest(mDownloadRequest);
        }

    }

    /**
     * 是否取消下载
     *
     * @return
     */
    public boolean isCancel() {
        return isCancel;
    }

    /**
     * 判断是否完成下载
     *
     * @return
     */
    public boolean isFinish() {
        if (downloadSize != 0 && mDownloadRequest.getTotalSize() != 0
                && downloadSize == mDownloadRequest.getTotalSize()) {
            try {
                File toBeRenamed = new File(mDownloadRequest.getDestUri());
                if (toBeRenamed.exists() && !toBeRenamed.isDirectory()) {
                    if (mDownloadRequest.getDestUri().endsWith(DownloadUtils.SUFFIX)) {
                        String temp = mDownloadRequest.getDestUri().substring(0, mDownloadRequest.getDestUri().lastIndexOf(DownloadUtils.SUFFIX));
                        File newFile = new File(temp);
                        if (toBeRenamed.renameTo(newFile)) {
                            mDownloadRequest.setDestUri(temp);
                        }
                    }
                }
            } catch (Exception e) {

            }
            return true;
        }
        if (downloadSize > mDownloadRequest.getTotalSize()) {
            Downloader.getInstance().getDownloadManager().delete(mDownloadRequest);
            Downloader.getInstance().getDownloadManager().downLoad(
                    mDownloadRequest.getDownLoadItem());
        }
        return false;
    }

    /**
     * 判断下载线程是否已经全部结束，结束不代表下载完成，异常可能导致结束
     *
     * @return
     */
    public boolean isAllFinish() {
        if (!thread.isFinish())
            return false;
        return true;
    }

    public String getDownloadSrcUri() {
        return mSrcUri;
    }

    public DownloadRequest getDownloadRequest() {
        return mDownloadRequest;
    }

    /**
     * 检查下载子线程的状态如果没有结束并且没有出错则继续下载
     */
    private void checkThread() {
        if (!thread.isFinish() && !thread.isError()
                && thread.getStatus() == DownloadThread.Status.FINISH) {
            thread = new DownloadThread(this, mDownloadRequest, 1);
            thread.start();
        }
    }

    public void setReDownload() {
        if (null != mDownloadRequest.getDownLoadItem()) {
            mDownloadRequest.getDownLoadItem().setRecoveryNetworkAutoDownload(true);
        }
    }

    @Override
    public void run() {
        doDownload();
    }

}
