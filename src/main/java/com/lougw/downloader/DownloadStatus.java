/*******************************************************************************
 * Copyright 2011-2013
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.lougw.downloader;

/**
 * 下载状态
 */
public enum DownloadStatus {
    /**
     * 正常状态，未加入队列
     */
    STATUS_NORMAL,
    /**
     * 下载任务处于挂起状态
     */
    STATUS_IDLE,
    /**
     * 下载任务处于开始状态
     */
    STATUS_START,
    /**
     * 下载任务处于下载状态
     */
    STATUS_DOWNLOADING,
    /**
     * 下载任务处于暂停状态
     */
    STATUS_PAUSE,
    /**
     * 下载任务处于结束状态
     */
    STATUS_COMPLETE,
    /**
     * 下载任务时出错 例如：网络错误，io读写错误等
     */
    STATUS_ERROR,
    /**
     * 下载任务被删除
     */
    STATUS_DELETE;
}
