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

import android.content.ContentValues;
import android.database.Cursor;

import com.lougw.downloader.db.DownloadColumns;

import java.io.Serializable;

/**
 * 下载任务的模型，开始下载，准备下载，暂停下载和取消下载需提交一个DownloadRequest的实例
 * 可以通过下载地址和文件的保存路径构造一个下载任务模型，也可以通过数据库的实体对象来创建
 */
public class DownloadRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    // 如果 mId == -1 表示该下载任务是一个新建的下载任务并且需要被插入到数据库中以作为下载记录
    private long mId = -1;
    private String mSrcUri;
    private String mDestUri;
    private long mTotalSize = 0;
    private long mDownloadSize = 0;
    private long mSpeed = 0;
    private DownloadStatus mDownloadStatus = DownloadStatus.STATUS_IDLE;
    private DownLoadItem downLoadItem;
    private String mGuid;

    /**
     * 通过下载地址和文件的保存路径构造一个下载任务模型
     *
     * @param srcUri  下载地址
     * @param destUrl 存储路径
     */
    public DownloadRequest(String srcUri, String destUrl, DownLoadItem item) {
        mSrcUri = srcUri;
        mDestUri = destUrl;
        downLoadItem = item;
        mGuid = item.getGuid();
    }

    /**
     * 通过数据库实体构造一个下载任务模型
     *
     * @param cursor
     */
    public DownloadRequest(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(DownloadColumns._ID));
        mGuid = cursor.getString(cursor
                .getColumnIndex(DownloadColumns.GUID));
        long mSrcType = cursor.getLong(cursor
                .getColumnIndex(DownloadColumns.SRC_TYPE));
        mSrcUri = cursor.getString(cursor
                .getColumnIndex(DownloadColumns.SRC_URI));
        mDestUri = cursor.getString(cursor
                .getColumnIndex(DownloadColumns.DEST_URI));
        mTotalSize = cursor.getLong(cursor
                .getColumnIndex(DownloadColumns.TOTAL_SIZE));
        mDownloadSize = cursor.getLong(cursor
                .getColumnIndex(DownloadColumns.DOWNLOAD_SIZE));
        mDownloadStatus = DownloadStatus.valueOf(cursor.getString(cursor
                .getColumnIndex(DownloadColumns.DOWNLOAD_STATUS)));
        long createTime = cursor.getLong(cursor
                .getColumnIndex(DownloadColumns.CREATE_TIME)) == 0 ? System.currentTimeMillis() : cursor.getLong(cursor
                .getColumnIndex(DownloadColumns.CREATE_TIME));
        long updateTime = cursor.getLong(cursor
                .getColumnIndex(DownloadColumns.UPDATE_TIME));
        String fileName = cursor.getString(cursor
                .getColumnIndex(DownloadColumns.FILE_NAME));
        String remarks = cursor.getString(cursor
                .getColumnIndex(DownloadColumns.REMARKS));
        boolean isMobileCanDownload = cursor.getInt(cursor
                .getColumnIndex(DownloadColumns.MONET_CAN_BE_DOWNLOADED)) == 0 ? false
                : true;
        boolean recoveryNetworkAutoDownload = cursor.getInt(cursor
                .getColumnIndex(DownloadColumns.RECOVERY_NETWORK_AUTO_DOWNLOAD)) == 0 ? false
                : true;
        String reservedField01 = cursor.getString(cursor
                .getColumnIndex(DownloadColumns.RESERVED_FIELD_01));
        String reservedField02 = cursor.getString(cursor
                .getColumnIndex(DownloadColumns.RESERVED_FIELD_02));
        String reservedField03 = cursor.getString(cursor
                .getColumnIndex(DownloadColumns.RESERVED_FIELD_03));
        long reservedField04 = cursor.getLong(cursor
                .getColumnIndex(DownloadColumns.RESERVED_FIELD_04));
        boolean reservedField05 = cursor.getInt(cursor
                .getColumnIndex(DownloadColumns.RESERVED_FIELD_05)) == 0 ? false
                : true;
        downLoadItem = new DownLoadItem(mGuid, mSrcType, createTime, updateTime,mSrcUri, remarks, isMobileCanDownload, recoveryNetworkAutoDownload);
downLoadItem.setFileName(fileName);
    }

    /**
     * 通过DownloadRequest创建一个数据ContentValues对象 用来增删改查操作
     *
     * @return ContentValues
     */
    synchronized public ContentValues toContentValues() {
        ContentValues value = new ContentValues();
        if (mId != -1) {
            value.put(DownloadColumns._ID, mId);
        }
        value.put(DownloadColumns.GUID, mGuid);
        value.put(DownloadColumns.SRC_URI, mSrcUri);
        value.put(DownloadColumns.DEST_URI, mDestUri);
        value.put(DownloadColumns.TOTAL_SIZE, mTotalSize);
        value.put(DownloadColumns.DOWNLOAD_SIZE, mDownloadSize);
        value.put(DownloadColumns.DOWNLOAD_STATUS, mDownloadStatus.toString());
        value.put(DownloadColumns.UPDATE_TIME, System.currentTimeMillis());
        value.put(DownloadColumns.CREATE_TIME, downLoadItem.getCreateTime());
        value.put(DownloadColumns.REMARKS, downLoadItem.getRemarks());
        value.put(DownloadColumns.FILE_NAME, downLoadItem.getFileName());
        value.put(DownloadColumns.MONET_CAN_BE_DOWNLOADED, downLoadItem.isMonetCanBeDownloaded());
        value.put(DownloadColumns.RECOVERY_NETWORK_AUTO_DOWNLOAD, downLoadItem.isRecoveryNetworkAutoDownload());
        value.put(DownloadColumns.RESERVED_FIELD_01, downLoadItem.getReservedField01());
        value.put(DownloadColumns.RESERVED_FIELD_02, downLoadItem.getReservedField02());
        value.put(DownloadColumns.RESERVED_FIELD_03, downLoadItem.getReservedField03());
        value.put(DownloadColumns.RESERVED_FIELD_04, downLoadItem.getReservedField04());
        value.put(DownloadColumns.RESERVED_FIELD_05, downLoadItem.isReservedField05());
        return value;
    }

    /**
     * 获取数据库主键
     *
     * @return 数据库主键
     */
    synchronized public long getId() {
        return mId;
    }

    /**
     * 设置数据库主键值
     *
     * @param id 主键
     * @return 主键值
     */
    synchronized long setId(long id) {
        return mId = id;
    }

    synchronized public String getSrcUri() {
        return mSrcUri;
    }

    synchronized String setSrcUri(String srcUri) {
        return mSrcUri = srcUri;
    }

    synchronized public String getDestUri() {
        return mDestUri;
    }

    synchronized String setDestUri(String destUri) {
        return mDestUri = destUri;
    }

    synchronized public long getTotalSize() {
        return mTotalSize;
    }

    synchronized public long setTotalSize(long size) {
        return mTotalSize = size;
    }

    synchronized public long getDownloadSize() {
        return mDownloadSize;
    }

    public int getProgress() {
        if (mTotalSize == 0) {
            return 0;
        }
        return (int) (mDownloadSize * 100 / mTotalSize);
    }

    synchronized long setDownloadSize(long size) {
        return mDownloadSize = size;
    }

    synchronized public DownloadStatus getDownloadStatus() {
        return mDownloadStatus;
    }

    synchronized public void setDownloadStatus(DownloadStatus status) {
        mDownloadStatus = status;
    }


    synchronized public DownLoadItem getDownLoadItem() {
        return downLoadItem;
    }


    synchronized public long getSpeed() {
        return mSpeed;
    }

    synchronized public void setSpeed(long mSpeed) {
        this.mSpeed = mSpeed;
    }


    @Override
    public boolean equals(Object o) {
        if (mGuid != null && null != o && o instanceof DownloadRequest) {
            if (mGuid.equals(
                    ((DownloadRequest) o).getGuid())) {
                return true;
            }
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[mId=").append(mId).append(", mSrcUri=").append(mSrcUri)
                .append(", mDestUri=").append(mDestUri).append(", mTotalSize=")
                .append(mTotalSize).append(", mDownloadSize=")
                .append(mDownloadSize).append(", mDownloadStatus=")
                .append(mDownloadStatus).append(",iteminfo").append("]");
        return sb.toString();
    }

    public String getGuid() {
        return mGuid;
    }

    public void setGuid(String mGuid) {
        this.mGuid = mGuid;
    }
}
