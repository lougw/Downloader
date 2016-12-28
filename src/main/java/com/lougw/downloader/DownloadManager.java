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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.lougw.downloader.db.DownloadColumns;
import com.lougw.downloader.db.DownloadDataBaseIml;
import com.lougw.downloader.service.DownloadService;
import com.lougw.downloader.utils.DLogUtil;
import com.lougw.downloader.utils.DToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 单例模式的下载管理器类
 * <p/>
 * <strong>注意：只支持HTTP/HTTPS协议的下载</strong>
 * <p/>
 * 所有的下载任务在一个线程池中. 当第一次创建该类对象是需要设置线程池核心线程数, 默认的线程数为2
 * <p/>
 * 下载历史将会被记录作为将来查询使用
 */
public class DownloadManager {
    private static final String TAG = DownloadManager.class.getSimpleName();
    private DownloadThreadPool mDownloadThreadPool;
    private static DownloadDataBaseIml mDownloadIml;
    private static DownloadManager mInstance;
    private static Context context;

    /**
     * 下载建造器类
     * <p/>
     * <strong>注意：必须在调用 {@link Builder#build()} 之前，调用
     * {@link Builder#setContext(Context)}</strong>
     */
    public static class Builder {

        private Context mContext;

        /**
         * 设置上下文对象
         *
         * @param context 上下文对象
         * @return Builder对象
         */
        public Builder setContext(Context context) {
            mContext = context;
            DownloadManager.context = mContext;
            return this;
        }

        /**
         * 设置多线程下载的线程数
         * <p/>
         * 当在移动设备上使用时，建议最大线程数不要超过3.
         *
         * @param threadNum 多线程下载时的线程数
         * @return Builder对象
         */
        public Builder setDownloadThreadNum(int threadNum) {
            Config.setDownloadThreads(threadNum);
            return this;
        }

        /**
         * 设置线程池的核心线程数
         * <p/>
         * 当在移动设备上使用时，建议最大线程数不要超过5.
         *
         * @param poolSize 线程池核心线程数
         * @return Builder对象
         */
        public Builder setPoolSize(int poolSize) {
            Config.setPoolSize(poolSize);
            return this;
        }

        /**
         * 通过Builder参数 构造一个DownloadManager对象
         *
         * @return DownloadManager
         */
        public DownloadManager build() {
            if (mContext == null) {
                throw new IllegalArgumentException();
            }
            return new DownloadManager(this);
        }
    }


    /**
     * 获取或创建一个DownloadManager单例对象
     *
     * @param context
     * @param maxThread
     * @return DownloadManager
     * @deprecated 使用 {@link Builder} 作为替代，来创建DownloadManager
     */
    public static DownloadManager getInstance(Context context, int maxThread) {
        synchronized (DownloadManager.class) {
            if (mInstance == null) {
                mInstance = new DownloadManager(
                        context.getApplicationContext(), maxThread);
            }
        }
        return mInstance;
    }

    private DownloadManager(Context context, int maxThread) {
        if (mInstance != null) {
            throw new IllegalStateException(
                    "Can not create singlton object Duplicate");
        }
        mDownloadThreadPool = new DownloadThreadPool(maxThread, context);
        mDownloadIml = new DownloadDataBaseIml(context);
        mDownloadThreadPool.setDownLoadDatabase(mDownloadIml);
        setDownLoadListener(new DownloadCallBack(context));
    }

    private DownloadManager(Builder builder) {
        mDownloadThreadPool = new DownloadThreadPool(Config.POOL_SIZE, context);
        mDownloadIml = new DownloadDataBaseIml(builder.mContext);
        mDownloadThreadPool.setDownLoadDatabase(mDownloadIml);
        setDownLoadListener(new DownloadCallBack(builder.mContext));
    }

    public void setDownLoadListener(DownloadListener listener) {
        mDownloadIml.setDownloadListener(listener);
    }

    /**
     * 查询下载任务的历史记录 参数请说明
     * {@linkplain SQLiteDatabase#query(String, String[], String, String[], String, String, String)}
     *
     * @param selection     A filter declaring which rows to return, formatted as an SQL
     *                      Where clause, passing null will return all rows.
     * @param selectionArgs You may include ?s in selection, which will be replace by the
     *                      values from this parameters, in the order that they appear in
     *                      the selection. The values will be bound as Strings
     * @param orderBy       How to order the rows, formatted as and SQL ORDER BY clause.
     *                      Passing null will use the default sort order, which may be
     *                      unordered.
     * @return The result set according to selection.
     */
    public synchronized List<DownloadRequest> query(String selection,
                                                    String[] selectionArgs, String orderBy) {
        Cursor cursor = null;
        try {
            cursor = mDownloadIml.query(null, selection, selectionArgs,
                    orderBy);
            return getDownLoadRequests(cursor);
        } catch (RuntimeException e) {
            if (null != cursor)
                cursor.close();
            cursor = null;
            return new ArrayList<DownloadRequest>();
        } finally {
            if (null != cursor) {
                // Log.e("CAQ","Close");
                cursor.close();
            }
            cursor = null;
        }
    }

    private List<DownloadRequest> getDownLoadRequests(Cursor cursor) {
        List<DownloadRequest> resultList = new ArrayList<DownloadRequest>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor
                        .getColumnIndex(DownloadColumns._ID));
                DownloadRequest executingRequest = mDownloadThreadPool
                        .getDownloadRequest(id);
                if (executingRequest != null) {
                    DLogUtil.v(TAG, "query executingRequest != null");
                    resultList.add(executingRequest);
                } else {
                    DLogUtil.v(TAG, " query executingRequest == null");
                    DownloadRequest request = new DownloadRequest(cursor);
                    if (request.getDownloadStatus() == DownloadStatus.STATUS_START
                            || request.getDownloadStatus() == DownloadStatus.STATUS_DOWNLOADING
                            || request.getDownloadStatus() == DownloadStatus.STATUS_IDLE) {
                        request.setDownloadStatus(DownloadStatus.STATUS_PAUSE);
                    }
                    resultList.add(request);
                }
            } while (cursor.moveToNext());
        }
        return resultList;
    }

    /**
     * 提交一个任务到线程池的任务队列 如果线程池中有空闲线程它将会立即开始任务
     *
     * @param request DownloadRequest对象
     *                <p/>
     *                <strong>注意： 该DownloadRequest对象 可能被修改. 例如当DownloadRequest开始时,
     *                {@link DownloadRequest#mDownloadStatus} 将被修改 </strong>
     */
    @SuppressLint("UseSparseArrays")
    public void enqueue(DownloadRequest request) {
        if (DownloadStatus.STATUS_NORMAL == request.getDownloadStatus())
            request.setDownloadStatus(DownloadStatus.STATUS_IDLE);
        if (mDownloadThreadPool.enqueue(request)) {
            if (request.getId() == -1) {
                request.setDownloadSize(0);
                long id = mDownloadIml.insert(request);
                request.setId(id);
            } else {
                mDownloadIml.update(request);
            }
            DLogUtil.v(TAG, "enqueue()  " + request.toString());
        } else {
            if (DownloadUtils.isMemoryLow()) {
                DToastUtil.showMessage(R.string.download_no_space);
            }
        }
    }


    /**
     * @param model 下载的对象
     * @Description:下载任务
     */
    public void downLoad(BaseModel model) {
        DownloadRequest request = getRequestByBaseModel(model);
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.DOWNLOAD_REQUEST, request);
        intent.putExtra(DownloadService.OP_STATUS,
                DownloadService.OP_STATUS_START);
        context.startService(intent);

    }

    /**
     * @Description:暂停
     */
    public void pause(BaseModel model) {
        DownloadRequest request = getRequestByBaseModel(model);
        DownloadStatus status = request.getDownloadStatus();
        if (status.equals(DownloadStatus.STATUS_IDLE)
                || status.equals(DownloadStatus.STATUS_START)
                || status.equals(DownloadStatus.STATUS_DOWNLOADING)
                || status.equals(DownloadStatus.STATUS_PAUSE)) {
            request.setDownloadStatus(DownloadStatus.STATUS_PAUSE);
            mDownloadIml.update(request);
        }
        mDownloadThreadPool.getDownloadRequestInPool().remove(request);
    }

    /**
     * @Description:暂停
     */
    public void pause(DownloadRequest request) {

        DownloadStatus status = request.getDownloadStatus();
        if (status.equals(DownloadStatus.STATUS_IDLE)
                || status.equals(DownloadStatus.STATUS_START)
                || status.equals(DownloadStatus.STATUS_DOWNLOADING)
                || status.equals(DownloadStatus.STATUS_PAUSE)) {
            request.setDownloadStatus(DownloadStatus.STATUS_PAUSE);
            mDownloadIml.update(request);
        }
        mDownloadThreadPool.getDownloadRequestInPool().remove(request);
    }

    /**
     * @Description:恢复
     */
    public void resume(DownloadRequest request) {
        DownloadStatus status = request.getDownloadStatus();
        if (status.equals(DownloadStatus.STATUS_PAUSE)
                || status.equals(DownloadStatus.STATUS_ERROR)) {
            downLoad(request.getDownLoadItem());
        }
    }

    public void deleteTask(DownloadRequest request) {
        pause(request.getDownLoadItem());
        request.setDownloadStatus(DownloadStatus.STATUS_DELETE);
        mDownloadThreadPool.getDownloadRequestInPool().remove(request);
        mDownloadIml.delete(request);
    }

    public void delete(DownloadRequest request) {
        pause(request.getDownLoadItem());
        request.setDownloadStatus(DownloadStatus.STATUS_DELETE);
        mDownloadThreadPool.getDownloadRequestInPool().remove(request);
        mDownloadIml.delete(request);
        try {
            File file = new File(request.getDestUri());
            if (null != file && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @Description:清除所有任务
     */
    public void clear() {
        List<DownloadRequest> requests = query(null, null, null);
        delete(requests);
    }

    public void delete(List<DownloadRequest> list) {
        for (DownloadRequest request : list) {
            delete(request);
        }
    }

    public void deleteRequest(DownloadRequest request) {
        mDownloadThreadPool.getDownloadRequestInPool().remove(request);
    }

    /**
     * @param model 下载对象
     * @Description:通过一个apk 对象得到一个下载的实例
     */
    public DownloadRequest getRequestByBaseModel(BaseModel model) {
        String downLoadUrl = model.getDownLoadUrl();
        List<DownloadRequest> requests = query("src_url=?",
                new String[]{
                        downLoadUrl
                }, null);
        if (requests != null && requests.size() != 0) {
            DownloadRequest r = requests.get(0);
            /* 在线程池中的任务 */
            DownloadRequest rInPool = mDownloadThreadPool.getDownloadRequest(r
                    .getId());
            // 如果下载任务不再线程中，返回从数据库中取的线程，并将正在下载和等待的下载状态置为暂停
            if (rInPool == null) {
                if (r.getDownloadStatus() == DownloadStatus.STATUS_COMPLETE) {
                    try {
                        File file = new File(r.getDestUri());
                        if (null == file || !file.exists()) {
                            r.setDownloadStatus(DownloadStatus.STATUS_DELETE);
                            mDownloadThreadPool.getDownloadRequestInPool().remove(r);
                            mDownloadIml.delete(r);
                            return getDownloadRequest(model, downLoadUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (r.getDownloadStatus() == DownloadStatus.STATUS_START ||
                        r.getDownloadStatus() == DownloadStatus.STATUS_DOWNLOADING
                        || r.getDownloadStatus() == DownloadStatus.STATUS_IDLE) {
                    r.setDownloadStatus(DownloadStatus.STATUS_PAUSE);
                }
                return r;
            } else {
                if (rInPool.getDownloadStatus() == DownloadStatus.STATUS_COMPLETE) {
                    try {
                        File file = new File(rInPool.getDestUri());
                        if (null == file || !file.exists()) {
                            rInPool.setDownloadStatus(DownloadStatus.STATUS_DELETE);
                            mDownloadThreadPool.getDownloadRequestInPool().remove(rInPool);
                            mDownloadIml.delete(rInPool);
                            return getDownloadRequest(model, downLoadUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (rInPool.getDownloadStatus() == DownloadStatus.STATUS_ERROR) {
                    rInPool.setDownloadStatus(DownloadStatus.STATUS_NORMAL);
                }

                return rInPool;
            }
        }
        return getDownloadRequest(model, downLoadUrl);
    }

    public DownloadRequest getDownloadRequest(BaseModel model, String downLoadUrl) {
        String desPath = DownloadUtils.getFileName(model);
        DownLoadItem downLoadItem = new DownLoadItem(model.getGuid(), model.getSrcType(), model.getDownLoadUrl(), model.getRemarks(), model.isMonetCanBeDownloaded(), model.isRecoveryNetworkAutoDownload());
        if (DownloadUtils.isFileExists(desPath)) {
            DownloadRequest request = new DownloadRequest(downLoadUrl, desPath,
                    downLoadItem);
            request.setDownloadStatus(DownloadStatus.STATUS_COMPLETE);
            return request;
        } else {
            desPath = desPath + DownloadUtils.SUFFIX;
            DownloadRequest request = new DownloadRequest(downLoadUrl, desPath,
                    downLoadItem);
            request.setDownloadStatus(DownloadStatus.STATUS_NORMAL);
            return request;
        }
    }

    public DownloadRequest getRequestByGuid(String guid) {
        String selection = DownloadColumns.GUID + "='" + guid + "'";
        List<DownloadRequest> requests = query(selection, null, null);
        if (requests != null && requests.size() != 0) {
            return requests.get(0);
        }
        return null;
    }

    /**
     * @Description:查询下载完成的列表
     */
    public List<DownloadRequest> queryDownLoaded() {
        String selection = DownloadColumns.DOWNLOAD_STATUS + "='"
                + DownloadStatus.STATUS_COMPLETE + "'";
        return query(selection, null, null);
    }

    public void updateStatus(DownloadRequest request) {
        if (null != request) {
            mDownloadIml.update(request);
        }
    }

    public void insertDownloadRequest(DownloadRequest request) {
        if (null != request) {
            mDownloadIml.insert(request);
        }
    }

    /**
     * @Description:查询正在下载的列表
     */
    public synchronized List<DownloadRequest> queryAllDownLoads() {
        return query(null, null, null);
    }
}
