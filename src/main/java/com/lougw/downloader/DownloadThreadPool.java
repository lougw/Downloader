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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DownloadThreadPool {
    private final static String TAG = DownloadThreadPool.class.getSimpleName();
    private int mCorePoolSize = 2;
    private ExecutorService mThreadPool;
    private Context mContext;
    private DownloadDataBase downLoadDataBase;
    private CopyOnWriteArrayList<DownloadRequest> mDownloadRequests;

    DownloadThreadPool(Context context) {
        this(2, context);
    }

    DownloadThreadPool(int corePoolSize, Context context) {
        mCorePoolSize = corePoolSize;
        mThreadPool = Executors.newFixedThreadPool(mCorePoolSize);
        mContext = context;
        mDownloadRequests = new CopyOnWriteArrayList<DownloadRequest>();
    }

    private boolean isInQueue(DownloadRequest request) {
        boolean result = false;
        if (null == request)
            return result;
        String srcUri = null;
        String srcUriNew = request.getSrcUri();
        if (null == srcUriNew)
            return result;
        DownloadRequest tmpRequest = null;
        for (int i = 0; i < mDownloadRequests.size(); i++) {
            tmpRequest = mDownloadRequests.get(i);
            if (null == tmpRequest)
                continue;
            srcUri = tmpRequest.getSrcUri();
            if (null != srcUri && srcUri.equalsIgnoreCase(srcUriNew)) {
                result = true;
                break;
            }
        }
        return result;
    }

    synchronized boolean enqueue(DownloadRequest request) {
        if (!isInQueue(request)) {
            DLogUtil.v(
                    TAG,
                    "DownloadThreadPool enqueue() request="
                            + request.toString());
            request.setDownloadStatus(DownloadStatus.STATUS_IDLE);
            HttpDownloader downloader = HttpDownloader
                    .create(mContext, request).setDownloadDatabase(
                            downLoadDataBase);
            mDownloadRequests.add(request);
            mThreadPool.execute(downloader);
            return true;
        } else {
            return false;
        }
    }

    public void setDownLoadDatabase(DownloadDataBase downLoadDataBase) {
        this.downLoadDataBase = downLoadDataBase;

    }

    /**
     * 获取线程池的所有任务 不包括已经完成的任务
     *
     * @return List<DownloadRequest>
     */
    List<DownloadRequest> getDownloadRequestInPool() {
        return mDownloadRequests;
    }

    /**
     * 根据id获取正在执行的任务中与之对应的任务
     *
     * @param id
     * @return
     */
    synchronized DownloadRequest getDownloadRequest(long id) {
        for (DownloadRequest r : mDownloadRequests) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

}
