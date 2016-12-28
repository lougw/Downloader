package com.lougw.downloader;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;


import java.io.File;

/**
 * Created by gaoweilou on 2016/4/1.
 */
public class DownloadUtils {
    public static final String CACHE_DIRECTORY_ROOT = "/Downloader/";
    public static final String VIDEOS_CACHE_DIRECTORY = "/Downloader/Videos/";
    public static final String SUFFIX = ".temp";

    public static boolean isMemoryLow() {
        // is there enough space in the file system of the given param 'root'.
        long bytesAvailable = getAvailableBytesInFileSystemAtGivenRoot(new File(
                getDownLoadFileHome(DownLoadType.OTHER)));
        if (bytesAvailable < DownloadThread.sDownloadDataDirLowSpaceThreshold) {
            /*
             * filesystem's available space is below threshold for low space
             * warning. threshold typically is 10% of download data dir space
             * quota. try to cleanup and see if the low space situation goes
             * away.
             */
            bytesAvailable = getAvailableBytesInFileSystemAtGivenRoot(new File(
                    getDownLoadFileHome(DownLoadType.OTHER)));
            if (bytesAvailable < DownloadThread.sDownloadDataDirLowSpaceThreshold) {
                /*
                 * available space is still below the threshold limit. If this
                 * is system cache dir, print a warning. otherwise, don't allow
                 * downloading until more space is available because
                 * downloadmanager shouldn't end up taking those last few MB of
                 * space left on the filesystem.
                 */
                return true;
            }
        }
        return false;
    }


    private static long getAvailableBytesInFileSystemAtGivenRoot(File root) {
        StatFs stat = new StatFs(root.getPath());
        // put a bit of margin (in case creating the file grows the system by a
        // few blocks)
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        long size = stat.getBlockSize() * availableBlocks;
        return size;
    }

    public static String getDownLoadFileHome() {
        return getPath(VIDEOS_CACHE_DIRECTORY);
    }

    /**
     * @Description:获取目录
     */
    public static String getPath(String path) {
        File file = null;
        boolean isHaveSDCard = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (isHaveSDCard) {
            file = new File(Environment.getExternalStorageDirectory() + path);
        } else {
            file = new File(Downloader.getInstance().getContext().getFilesDir() + path);
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/";
    }

    public static String getDownLoadFileHome(int fileType) {
        String path = null;
        try {
            if (DownLoadType.VIDEOS == fileType) {
                path = getPath(VIDEOS_CACHE_DIRECTORY);
            } else if (DownLoadType.OTHER == fileType) {
                path = getPath(CACHE_DIRECTORY_ROOT);
            }
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return path;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getPath(CACHE_DIRECTORY_ROOT);

    }

    /**
     * @Description:根据下载的数据来获取本地存储文件名（全路径）
     */
    public static String getFileName(BaseModel model) {
        return getDownLoadFileHome(DownLoadType.VIDEOS) + model.getFileName();
    }

    public static String getFileName(int type, String fileName) {
        return getDownLoadFileHome(type) + fileName;
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(String filePath) {
        return TextUtils.isEmpty(filePath) ? null : new File(filePath);
    }

    public static boolean getDownloadInWifi(Context context) {
        return true;
    }
}
