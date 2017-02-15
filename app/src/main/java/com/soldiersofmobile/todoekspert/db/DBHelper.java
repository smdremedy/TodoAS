package com.soldiersofmobile.todoekspert.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import timber.log.Timber;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "todo.db";



    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = String.format("CREATE TABLE %s " +
                        "(%s TEXT PRIMARY KEY NOT NULL, %s TEXT, %s INT, %s INT, %s INT," +
                        " %s TEXT)", TodoDao.TABLE_NAME, TodoDao.C_ID,
                TodoDao.C_CONTENT, TodoDao.C_DONE, TodoDao.C_CREATED_AT,
                TodoDao.C_UPDATED_AT, TodoDao.C_USER_ID);
        Timber.d("onCreate sql:" + sql);
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TodoDao.TABLE_NAME));
        onCreate(db);

    }
}