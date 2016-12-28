
package com.lougw.downloader;

import java.io.Serializable;

/**
 * @Title:下载的内容
 */
public class DownLoadItem extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    public String guid;
    private long srcType;
    public long createTime;
    public long updateTime;
    private String downLoadUrl;
    public String fileName;
    public String remarks;
    private boolean monetCanBeDownloaded;
    private boolean recoveryNetworkAutoDownload;
    private String reservedField01;
    private String reservedField02;
    private String reservedField03;
    private long reservedField04;
    private boolean reservedField05;

    public DownLoadItem() {

    }

    public DownLoadItem(String guid, long srcType, long createTime, long updateTime, String downLoadUrl, String remarks, boolean monetCanBeDownloaded, boolean recoveryNetworkAutoDownload) {
        this.guid = guid;
        this.srcType = srcType;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.downLoadUrl = downLoadUrl;
        this.remarks = remarks;
        this.monetCanBeDownloaded = monetCanBeDownloaded;
        this.recoveryNetworkAutoDownload = recoveryNetworkAutoDownload;
    }

    public DownLoadItem(String guid, long srcType, String downLoadUrl, String remarks, boolean monetCanBeDownloaded, boolean recoveryNetworkAutoDownload) {
        this.guid = guid;
        this.srcType = srcType;
        this.downLoadUrl = downLoadUrl;
        this.remarks = remarks;
        this.monetCanBeDownloaded = monetCanBeDownloaded;
        this.recoveryNetworkAutoDownload = recoveryNetworkAutoDownload;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public long getSrcType() {
        return srcType;
    }

    public void setSrcType(long srcType) {
        this.srcType = srcType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isMonetCanBeDownloaded() {
        return monetCanBeDownloaded;
    }

    public void setMonetCanBeDownloaded(boolean monetCanBeDownloaded) {
        this.monetCanBeDownloaded = monetCanBeDownloaded;
    }

    public boolean isRecoveryNetworkAutoDownload() {
        return recoveryNetworkAutoDownload;
    }

    public void setRecoveryNetworkAutoDownload(boolean recoveryNetworkAutoDownload) {
        this.recoveryNetworkAutoDownload = recoveryNetworkAutoDownload;
    }

    public String getReservedField01() {
        return reservedField01;
    }

    public void setReservedField01(String reservedField01) {
        this.reservedField01 = reservedField01;
    }

    public String getReservedField02() {
        return reservedField02;
    }

    public void setReservedField02(String reservedField02) {
        this.reservedField02 = reservedField02;
    }

    public String getReservedField03() {
        return reservedField03;
    }

    public void setReservedField03(String reservedField03) {
        this.reservedField03 = reservedField03;
    }

    public long getReservedField04() {
        return reservedField04;
    }

    public void setReservedField04(long reservedField04) {
        this.reservedField04 = reservedField04;
    }

    public boolean isReservedField05() {
        return reservedField05;
    }

    public void setReservedField05(boolean reservedField05) {
        this.reservedField05 = reservedField05;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }
}
