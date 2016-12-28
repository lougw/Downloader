
package com.lougw.downloader.db;


import com.lougw.downloader.DownloadRequest;

public interface DownloadDataBase {
    long insert(DownloadRequest request);

    int update(DownloadRequest request);

    void delete(DownloadRequest request);

    void progress(DownloadRequest request);

}
