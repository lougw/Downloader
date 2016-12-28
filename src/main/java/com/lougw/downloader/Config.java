
package com.lougw.downloader;

/**
 * 线程池配置类
 */
public class Config {
    public static int POOL_SIZE = 1;
    public static int DOWNLOAD_THREADS = 1;

    public static void setPoolSize(int poolSize) {
        POOL_SIZE = poolSize;
    }

    public static void setDownloadThreads(int threadNum) {
        DOWNLOAD_THREADS = threadNum;
    }

    public static int getPoolSize() {
        return POOL_SIZE;
    }

    public static int getDownloadThreads() {
        return DOWNLOAD_THREADS;
    }
}
