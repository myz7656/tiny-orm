package com.sp.tiny.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class TestDBProxy {
    private static final String DB_NAME = "test_db.db";
    private static final int DB_VERSION = 1;

    private TestDBHelper mTestDBHelper;

    private static class HOLDER {
        static TestDBProxy INSTANCE = new TestDBProxy();
    }

    public static TestDBProxy getInstance() {
        return HOLDER.INSTANCE;
    }

    private TestDBProxy() {
        mTestDBHelper = null;
    }

    public void init(Context context) {
        mTestDBHelper = new TestDBHelper(context, DB_NAME, null, DB_VERSION);
    }

    public SQLiteDatabase getWritableDatabase() {
        return mTestDBHelper.getWritableDatabase();
    }
}
