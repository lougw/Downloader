
package com.lougw.downloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 断点续传的数据库管理
 */
public class DownloadDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "downloads.db";

    public static final String TABLE_NAME = "downloads";

    private static final int DATABASE_VERSION = 1;

    private static DownloadDBHelper instance;

    private DownloadDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized static DownloadDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadDBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTable(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(TABLE_NAME).append("(")
                .append(DownloadColumns._ID)
                .append(" INTEGER PRIMARY KEY autoincrement,")
                .append(DownloadColumns.GUID).append(" TEXT,")
                .append(DownloadColumns.SRC_TYPE).append(" Long,")
                .append(DownloadColumns.SRC_URI).append(" TEXT,")
                .append(DownloadColumns.DEST_URI).append(" TEXT,")
                .append(DownloadColumns.FILE_NAME).append(" TEXT,")
                .append(DownloadColumns.TOTAL_SIZE).append(" TEXT,")
                .append(DownloadColumns.DOWNLOAD_SIZE).append(" TEXT,")
                .append(DownloadColumns.DOWNLOAD_STATUS).append(" TEXT,")
                .append(DownloadColumns.CREATE_TIME).append(" Long,")
                .append(DownloadColumns.UPDATE_TIME).append(" Long,")
                .append(DownloadColumns.REMARKS).append(" TEXT,")
                .append(DownloadColumns.MONET_CAN_BE_DOWNLOADED).append(" BOOL,")
                .append(DownloadColumns.RECOVERY_NETWORK_AUTO_DOWNLOAD).append(" BOOL,")
                .append(DownloadColumns.RESERVED_FIELD_01).append(" TEXT,")
                .append(DownloadColumns.RESERVED_FIELD_02).append(" TEXT,")
                .append(DownloadColumns.RESERVED_FIELD_03).append(" TEXT,")
                .append(DownloadColumns.RESERVED_FIELD_04).append(" Long,")
                .append(DownloadColumns.RESERVED_FIELD_05).append(" BOOL")
                .append(");");

        db.execSQL(sb.toString());
    }

}
