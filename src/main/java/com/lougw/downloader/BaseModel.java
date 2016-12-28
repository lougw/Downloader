
package com.lougw.downloader;

/**
 * @Title:
 * @Description:所有在线 apk model的基类
 * @Version:
 */
public abstract class BaseModel {
    public abstract String getDownLoadUrl();

    public abstract String getGuid();

    public abstract String getFileName();

    public abstract long getSrcType();

    public abstract long getCreateTime();

    public abstract long getUpdateTime();

    public abstract String getRemarks();

    public abstract boolean isMonetCanBeDownloaded();

    public abstract boolean isRecoveryNetworkAutoDownload();


}
