package com.sp.tiny.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TinyORMTest {
    private static final String TAG = "TinyORMTest";

    public static final int TEST_COUNT = 100;

    private TestDBProxy mTestDBHelper;

    @Before
    public void init() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        mTestDBHelper = TestDBProxy.getInstance();
        mTestDBHelper.init(appContext);
    }

    @Test
    public void testAll() {
        Log.i(TAG, "----------begin----------");

        Log.i(TAG, "-----insert-----");
        SQLiteDatabase db = mTestDBHelper.getWritableDatabase();
        db.beginTransaction();
        TableFourColumn lastRow = null;
        TableFourColumn firstRow = null;
        for (int i = 0; i < TEST_COUNT; i++) {
            TableFourColumn row = new TableFourColumn();
            row.setColumn1((int)System.currentTimeMillis());
            long rowIndex = TinyORM.getInstance().insert(db, row);
            Log.i(TAG, "row is " + rowIndex);
            lastRow = row;
            if (firstRow == null) {
                firstRow = row;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        Log.i(TAG, "-----delete-----");
        db.beginTransaction();
        boolean deleted = TinyORM.getInstance().delete(db, lastRow);
        Log.i(TAG, "deleted result is " + deleted);
        db.setTransactionSuccessful();
        db.endTransaction();

        Log.i(TAG, "-----query-----");
        TableFourColumn row = (TableFourColumn) TinyORM.getInstance().query(db, firstRow);
        Log.i(TAG, "query result is " + row.toString());

        Log.i(TAG, "-----update-----");
        row.setColumn4("xxxxxxx4");
        int updated = TinyORM.getInstance().update(db, row);
        Log.i(TAG, "update count is " + updated);
        TableFourColumn rowUpdated  = (TableFourColumn) TinyORM.getInstance().query(db, row);
        Log.i(TAG, "update result is " + rowUpdated.toString());

        Log.i(TAG,"----------end----------");
    }
}
