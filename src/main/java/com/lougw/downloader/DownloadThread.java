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

import android.content.Intent;
import android.os.StatFs;
import android.text.TextUtils;

import com.lougw.downloader.service.DownloadService;
import com.lougw.downloader.utils.DLogUtil;
import com.lougw.downloader.utils.NetWorkUtil;
import com.lougw.downloader.utils.DToastUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * <b>下载线程
 */
@SuppressWarnings("deprecation")
public class DownloadThread extends Thread {
    /**
     * the max amount of space allowed to be taken up by the downloads data dir
     */
    private static final long sMaxdownloadDataDirSize =
            50 * 1024 * 1024;
    // private static final long sMaxdownloadDataDirSize =
    // Resources.getSystem().getInteger(R.integer.config_downloadDataDirSize) *
    // 1024 * 1024;
    /**
     * threshold (in bytes) beyond which the low space warning kicks in and
     * attempt is made to purge some downloaded files to make space
     */
    public static final long sDownloadDataDirLowSpaceThreshold =
            5 * sMaxdownloadDataDirSize;
    // private static final long sDownloadDataDirLowSpaceThreshold =
    // Resources.getSystem().getInteger(
    // R.integer.config_downloadDataDirLowSpaceThreshold)
    // * sMaxdownloadDataDirSize / 100;

    /* 下载地址 */
    private String downUrl;
    private String destPath;

    /* 下载开始位置 */
    private long startPos;
    /* 线程ID */
    private int threadId = -1;

    /* 已经下载的长度 */
    private long downLength;

    /* 下载线程是否完成了应该下载的文件 长度 */
    private boolean finish = false;

    /* 下载线程状态标记 */
    private Status state = Status.RUNING;

    /**
     * @see
     */
    private HttpDownloader downloader;
    private DownloadRequest request;

    /**
     * 下载线程是否出错
     *
     * @note 下载线程完成了下载但是在关闭流的时候出错，不属于下载出错
     */
    private boolean isError = false;
    private String location;
    private HttpClient httpClient;

    /**
     * @param downloader <a>HttpDownloader</a>
     * @param tid        线程ID
     */
    public DownloadThread(HttpDownloader downloader, DownloadRequest request,
                          int tid) {
        this.request = request;
        this.downUrl = request.getSrcUri();
        location = downUrl;
        this.downloader = downloader;
        this.threadId = tid;
        this.downLength = request.getDownloadSize();
        destPath = request.getDestUri();
    }

    @Override
    public void run() {
        doDownload();
    }

    private long getAvailableBytesInFileSystemAtGivenRoot(File root) {
        StatFs stat = new StatFs(root.getPath());
        // put a bit of margin (in case creating the file grows the system by a
        // few blocks)
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        long size = stat.getBlockSize() * availableBlocks;
        return size;
    }

    private void showToast() {
        DToastUtil.showMessage(R.string.download_no_space);
    }

    private void doDownload() {
        boolean lowMemToastDisplayed = false;
        DLogUtil.e("@" + downloader.hashCode() + " THREAD #" + threadId,
                "THREAD #" + threadId + "正在执行...");

        // LogUtil.log( "doDownload  " + request.getSrcUri() + " downloading");

        // 如果当前线程没有结束并且下载任务也没有被取消则下载
        if (!downloader.isCancel()) {
            RandomAccessFile threadfile = null;
            InputStream inStream = null;
            try {
                startPos += downLength;
                DLogUtil.e("THREAD", "" + startPos);

                DownloadUtils.getDownLoadFileHome();
                if (DownloadUtils.isMemoryLow()) {
                    if (!lowMemToastDisplayed) {
                        showToast();
                        lowMemToastDisplayed = true;
                    }
                    downloader.setStatus(DownloadStatus.STATUS_PAUSE);
                    stopDownload();
                    return;
                }

                HttpResponse response = httpGetRedirect(downUrl);
                if (response == null || TextUtils.isEmpty(location)) {
                    downloader.setStatus(DownloadStatus.STATUS_ERROR);
                    isError = true;
                    return;
                }

                File file = new File(destPath);
                // 设置线程下载的开始位置，应该为初始下载位置startPos + 已下载长度downLength
                HttpEntity entry = response.getEntity();
                long contentLength = entry.getContentLength();
                DLogUtil.e("THREAD", "" + contentLength);
                if (downloader.getDownloadRequest().getTotalSize() == 0) {
                    downloader.getDownloadRequest().setTotalSize(contentLength);
                }
                inStream = entry.getContent();
                byte[] buffer = new byte[8192];
                threadfile = new RandomAccessFile(file, "rwd");
                threadfile.seek(startPos);
                int length = -1;
                while ((length = inStream.read(buffer)) != -1) {
                    if (DownloadUtils.isMemoryLow()) {
                        if (!lowMemToastDisplayed) {
                            showToast();
                            lowMemToastDisplayed = true;
                        }
                        downloader.setStatus(DownloadStatus.STATUS_PAUSE);
                        stopDownload();
                        // LogUtil.log( "doDownload  isMemoryLow");
                        break;
                    }

                    if (NetWorkUtil.hasNetwork(Downloader.getInstance().getContext())) {

                        if (SettingUtil.getDownloadInWifi(Downloader.getInstance().getContext())
                                && !NetWorkUtil.isWifiConnected(Downloader.getInstance().getContext())
                                && NetWorkUtil.isMobileConnected(Downloader.getInstance().getContext())
                                && !request.getDownLoadItem().isMonetCanBeDownloaded()) {
                            Intent intent = new Intent(Downloader.getInstance().getContext(),
                                    DownloadService.class);
                            intent.putExtra(DownloadService.DOWNLOAD_REQUEST,
                                    request);
                            intent.putExtra(DownloadService.OP_STATUS,
                                    DownloadService.OP_STATUS_DOWNLOAD_WIFI_LIMIT);
                            Downloader.getInstance().getContext().startService(intent);
                            downloader.setStatus(DownloadStatus.STATUS_PAUSE);
                            downloader.setReDownload();
                            stopDownload();
                            DLogUtil.log("doDownload  is hasNetwork");
                            break;
                        }

                    } else {
                        DToastUtil.showMessage(R.string.download_no_network);
                        downloader.setStatus(DownloadStatus.STATUS_PAUSE);
                        downloader.setReDownload();
                        stopDownload();
                        break;
                    }

                    DownloadStatus status = downloader.getStatus();
                    if (DownloadStatus.STATUS_PAUSE == status
                            || DownloadStatus.STATUS_ERROR == status
                            || DownloadStatus.STATUS_COMPLETE == status
                            || DownloadStatus.STATUS_DELETE == status
                            || state == Status.FINISH) {
                        stopDownload();
                        break;
                    }
                    threadfile.write(buffer, 0, length);
                    downLength += length;
                    downloader.append(length);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
                downloader.setStatus(DownloadStatus.STATUS_ERROR);
                isError = true;
                // LogUtil.log( "doDownload:  " + " ProtocolException");
            } catch (FileNotFoundException e) {
                downloader.setStatus(DownloadStatus.STATUS_ERROR);
                isError = true;
                // LogUtil.log( "doDownload:  " + " FileNotFoundException");
            } catch (IOException e) {
                if (e instanceof SocketException || e instanceof UnknownHostException
                        || e instanceof SocketTimeoutException
                        || e instanceof SSLPeerUnverifiedException
                        || e instanceof org.apache.http.conn.ConnectTimeoutException) {
                    downloader.setStatus(DownloadStatus.STATUS_PAUSE);
                    isError = false;
                    downloader.setReDownload();
                } else {
                    if (DownloadUtils.isMemoryLow()) {
                        if (!lowMemToastDisplayed) {
                            showToast();
                            lowMemToastDisplayed = true;
                        }
                        downloader.setStatus(DownloadStatus.STATUS_PAUSE);
                    } else {
                        downloader.setStatus(DownloadStatus.STATUS_ERROR);
                        isError = true;
                    }
                }
                // LogUtil.log( "doDownload:  " + " IOException");

            } catch (Exception e) {
                if (DownloadUtils.isMemoryLow()) {
                    if (!lowMemToastDisplayed) {
                        showToast();
                        lowMemToastDisplayed = true;
                    }
                }
                downloader.setStatus(DownloadStatus.STATUS_ERROR);
                isError = true;
                DLogUtil.log("doDownload:  " + " Exception" + e.toString());
            } finally {
                try {
                    if (threadfile != null) {
                        threadfile.close();
                    }
                    if (inStream != null) {
                        inStream.close();
                    }
                } catch (IOException e) {
                    if (e instanceof SocketException || e instanceof UnknownHostException
                            || e instanceof SocketTimeoutException
                            || e instanceof SSLPeerUnverifiedException
                            || e instanceof org.apache.http.conn.ConnectTimeoutException) {
                        if (downloader.getStatus() != DownloadStatus.STATUS_DELETE) {
                            downloader.setStatus(DownloadStatus.STATUS_PAUSE);
                        }
                    } else {
                        if (DownloadUtils.isMemoryLow()) {
                            if (!lowMemToastDisplayed) {
                                showToast();
                                lowMemToastDisplayed = true;
                            }
                            downloader.setStatus(DownloadStatus.STATUS_ERROR);
                        }
                        e.printStackTrace();
                        // LogUtil.log( "doDownload:  " + " finally Exception");
                    }
                }

                if (downLength >= downloader.getDownloadRequest().getTotalSize()) {
                    DLogUtil.e("@" + downloader.hashCode() + " THREAD #"
                            + threadId, "THREAD #" + threadId + " 下载完成 "
                            + "downLength" + downLength);
                    finish = true;
                    if (downLength == 0 && DownloadUtils.isMemoryLow()) {
                        if (!lowMemToastDisplayed) {
                            showToast();
                            lowMemToastDisplayed = true;
                        }
                        downloader.setStatus(DownloadStatus.STATUS_PAUSE);
                    }
                } else {
                    DLogUtil.e("@" + downloader.hashCode() + " THREAD #"
                            + threadId, "THREAD #" + threadId
                            + " 没有下载完成   ---- 继续下载" + "downLength" + downLength);
                    // doDownload();
                    if (DownloadUtils.isMemoryLow()) {
                        if (!lowMemToastDisplayed) {
                            showToast();
                            lowMemToastDisplayed = true;
                        }
                        downloader.setStatus(DownloadStatus.STATUS_PAUSE);
                    } else {
                        if (DownloadStatus.STATUS_START == downloader.getStatus()) {
                            downloader.setStatus(DownloadStatus.STATUS_ERROR);
                            isError = true;
                        }
                    }
                    DLogUtil.log("doDownload:  " + " finish but size is < total size");
                }
                state = Status.FINISH;
            }
        } else {
            DLogUtil.log("doDownload  downloader cancel");
        }
    }

    private HttpResponse httpGetRedirect(String url) throws ClientProtocolException, IOException {
        httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Range", "bytes=" + startPos + "-");
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter("http.protocol.handle-redirects", false); // 默认不让重定向
        httpGet.setParams(httpParams);
        HttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                || response.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT) {
            if (response.getEntity() != null && response.getEntity().getContentType() != null &&
                    response.getEntity().getContentType().getValue() != null) {
                String value = response.getEntity().getContentType().getValue();
                // String value =
                // response.getEntity().getContentType().getValue();
                if (value.contains("text/xml") || value.contains("text/html")) {
                    return null;
                }
            }
            return response;
        } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
                || response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY) {
            // 从头中取出转向的地址
            Header locationHeader = response.getLastHeader("location");
            location = locationHeader.getValue();
            return httpGetRedirect(location);
        }
        return null;
    }

    /**
     * @Description:
     * @Author
     */
    private HttpClient getHttpClient() {
        if (null == httpClient) {
            httpClient = new DefaultHttpClient();
        }
        return httpClient;
    }

    /**
     * @Description:
     */
    private void stopDownload() {
        getHttpClient().getConnectionManager().shutdown();
    }

    /**
     * 下载是否完成
     *
     * @return
     */
    public boolean isFinish() {
        return finish;
    }

    /**
     * 已经下载的内容大小
     *
     * @return 如果返回值为-1,代表下载失败
     */
    public long getDownLength() {
        return downLength;
    }

    /**
     * 下载线程是否发生错误
     *
     * @return
     */
    public boolean isError() {
        return isError;
    }

    public Status getStatus() {
        return state;
    }

    public enum Status {
        RUNING, FINISH
    }

}
