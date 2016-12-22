package com.example.sqlitedbhelper;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shailendrapsp on 19/12/16.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "ServiceAppData.db";
    public static final int DB_VERSION = 1;
    private Context mContext;

    public SQLiteHelper(Context mContext){
        super(mContext,DB_NAME,null,DB_VERSION);
        this.mContext=mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MATRIX_TABLE = "CREATE TABLE " + DB_CONSTANTS.MatrixEntry.TABLE_NAME + "("
                + DB_CONSTANTS.MatrixEntry.TABLE_PRIMARY_KEY + " INTEGER PRIMARY KEY," + DB_CONSTANTS.MatrixEntry.COLUMN_UNIT_ID + " UNIT_ID,"
                + KEY_PH_NO + " TEXT" + ")";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
