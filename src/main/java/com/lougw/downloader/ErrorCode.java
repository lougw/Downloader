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

public class ErrorCode {

    public static final int NORMAL = 0;
    public static final int CLIENT_PROTOCOL_ERROR = 1;
    public static final int FILE_NOT_FOUND_ERROR = 2;
    public static final int IO_ERROR = 3;
    public static final int NETWORK_ERROR = 4;

    public static final int UNKNOW_ERROR = 9999;
}
