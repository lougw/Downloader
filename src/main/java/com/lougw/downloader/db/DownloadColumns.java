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

package com.lougw.downloader.db;

import android.provider.BaseColumns;

/**
 * 数据库列信息
 */
public final class DownloadColumns implements BaseColumns {

    /**
     * 全局唯一标识
     */
    public static final String GUID = "guid";
    /**
     * 下载类型
     */
    public static final String SRC_TYPE = "src_type";
    /**
     * 下载地址
     */
    public static final String SRC_URI = "src_url";
    /**
     * 文件下载后保存的路径
     */
    public static final String DEST_URI = "dest_url";
    /**
     * 文件名称
     */
    public static final String FILE_NAME = "file_name";
    /**
     * 文件总大小
     */
    public static final String TOTAL_SIZE = "total_size";
    /**
     * 已经下载的文件长度 当调用pause方法后会记录当前下载的长度
     */
    public static final String DOWNLOAD_SIZE = "download_size";
    /**
     * 下载状态. 例如： IDLE, START,STATUS_DOWNLOADING, PAUSE, COMPLETE, ERROR.
     */
    public static final String DOWNLOAD_STATUS = "status";
    /**
     * 下载时间
     */
    public static final String CREATE_TIME = "create_time";
    /**
     * 下载完成的时间戳，也可以用来记录下载开始的时间戳，可选
     */
    public static final String UPDATE_TIME = "update_time";
    /**
     * 备注
     */
    public static final String REMARKS = "remarks";
    /**
     * 移动网络 下可以下载
     */
    public static final String MONET_CAN_BE_DOWNLOADED = "monet_can_be_downloaded";
    /**
     * 断网wifi恢复后是否会自动下载
     */
    public static final String RECOVERY_NETWORK_AUTO_DOWNLOAD = "recovery_network_auto_download";

    /**
     * 预留字段01
     */
    public static final String RESERVED_FIELD_01 = "reserved_field_01";
    /**
     * 预留字段02
     */
    public static final String RESERVED_FIELD_02 = "reserved_field_02";
    /**
     * 预留字段03
     */
    public static final String RESERVED_FIELD_03 = "reserved_field_03";
    /**
     * 预留字段04
     */
    public static final String RESERVED_FIELD_04 = "reserved_field_04";
    /**
     * 预留字段05
     */
    public static final String RESERVED_FIELD_05 = "reserved_field_05";

}
