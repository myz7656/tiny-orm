package com.sp.tiny.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.sp.tiny.orm.core.Column;
import com.sp.tiny.orm.core.ResultValue;
import com.sp.tiny.orm.core.SQLBuilder;
import com.sp.tiny.orm.core.Table;
import com.sp.tiny.orm.core.TableManager;

import java.util.List;

/**
 * author: 后知后觉(307817387/myz7656)
 * email: whuzhanyuanmin@126.com
 */

public class TinyORM {
    public static final int CONFLICT_REPLACE = 5;

    private static TinyORM sInstance;
    private final TableManager mTableManager;
    private final SQLBuilder mSQLBuilder;

    private TinyORM() {
        mTableManager = new TableManager();
        mSQLBuilder = new SQLBuilder(mTableManager);
    }

    public static TinyORM getInstance() {
        if (sInstance == null) {
            synchronized (TableManager.class) {
                if (sInstance == null) {
                    sInstance = new TinyORM();
                }
            }
        }
        return sInstance;
    }

    public SQLBuilder getSQLBuilder() {
        return this.mSQLBuilder;
    }

    /**
     * 根据 Bean 定义创建数据库表
     *
     * @param db DBDatabase 引擎
     * @param clazz Bean 对应的 class
     * @return 创建是否成功
     */
    public boolean createTable(SQLiteDatabase db, Class<?> clazz) {
        try {
            if (db == null) {
                return false;
            }
            String sql;
            sql = mSQLBuilder.buildCreateTableSQL(clazz);
            if (!TextUtils.isEmpty(sql)) {
                db.execSQL(sql);
            }
            String[] indexSQLs = mSQLBuilder.buildCreateIndexSQL(clazz);
            if (indexSQLs != null && indexSQLs.length > 0) {
                for (String indexSQL : indexSQLs) {
                    if (!TextUtils.isEmpty(indexSQL)) {
                        db.execSQL(indexSQL);
                    }
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * 根据 Bean 定义删除数据库表
     *
     * @param db DBDatabase 引擎
     * @param clazz Bean 对应的 class
     * @return 删除是否成功
     */
    public boolean deleteTable(SQLiteDatabase db, Class<?> clazz) {
        try {
            if (db == null) {
                return false;
            }
            String sql;
            sql = mSQLBuilder.buildDeleteTableSQL(clazz);
            if (!TextUtils.isEmpty(sql)) {
                db.execSQL(sql);
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * 根据 Bean 定义创建某个索引
     *
     * @param db DBDatabase 引擎
     * @param clazz Bean 对应的 class
     * @param indexName 指定索引名
     * @return 创建是否成功
     */
    public boolean createIndex(SQLiteDatabase db, Class<?> clazz, String indexName) {
        try {
            if (db == null) {
                return false;
            }
            String indexSQL = mSQLBuilder.buildCreateIndexSQL(clazz, indexName);
            if (!TextUtils.isEmpty(indexSQL)) {
                db.execSQL(indexSQL);
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * 根据 Bean 定义创建所有索引
     *
     * @param db DBDatabase 引擎
     * @param clazz Bean 对应的 class
     * @return 创建是否成功
     */
    public boolean createIndex(SQLiteDatabase db, Class<?> clazz) {
        try {
            if (db == null) {
                return false;
            }
            String[] indexSQLs = mSQLBuilder.buildCreateIndexSQL(clazz);
            if (indexSQLs != null && indexSQLs.length > 0) {
                for (String indexSQL : indexSQLs) {
                    if (!TextUtils.isEmpty(indexSQL)) {
                        db.execSQL(indexSQL);
                    }
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    /**
     * 往数据库表中插入一行
     *
     * @param db DBDatabase 引擎
     * @param object 需要插入的对象
     * @return 新行 id，如果出错，返回 －1
     */
    public long insert(SQLiteDatabase db, Object object) {
        if (db == null) {
            return -1;
        }
        long row = -1;
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return row;
        }

        ContentValues values = mSQLBuilder.buildInsertValues(object);
        if (values != null) {
            String name = table.getName();
            row = db.insertWithOnConflict(name, null, values, CONFLICT_REPLACE);
        }
        if (row <= 0) {
            Log.e("TinyORM", "insert row = " + row + " , object:" + object);
        }
        return row;
    }

    /**
     * 删除一行
     *
     * @param db db DBDatabase 引擎
     * @param object object 需要删除的对象
     * @return 执行是否成功
     */
    public boolean delete(SQLiteDatabase db, Object object) {
        if (db == null) {
            return false;
        }
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return false;
        }

        int count = 0;
        String sql = mSQLBuilder.buildWhereSQL(object);

        if (!TextUtils.isEmpty(sql)) {
            String name = table.getName();
            count = db.delete(name, sql, null);
        }
        return count > 0;
    }

    /**
     * 判断对象是否存在数据库中
     *
     * @param db db DBDatabase 引擎
     * @param object object 需要判断的对象
     * @return 是否存在
     */
    public boolean exist(SQLiteDatabase db, Object object) {
        if (db == null) {
            return false;
        }
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return false;
        }

        List<Column> column = table.getPrimaryKey();
        if (column == null || column.isEmpty()) {
            return false;
        }

        int count = 0;
        String sql = mSQLBuilder.buildWhereSQL(object);
        if (!TextUtils.isEmpty(sql)) {
            String name = table.getName();
            Cursor cursor = null;
            try {
                cursor = db.query(name,
                                         null,
                                         sql,
                                         null, null, null, null, "1");
                count = cursor.getCount();
            } catch (Throwable ignored) {

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return count > 0;
    }


    /**
     * 查询一行的信息
     *
     * @param db DBDatabase 引擎
     * @param object 需要查询的旧对象
     * @return 查询到的新对象，如果查不到，返回 null
     */
    public Object query(SQLiteDatabase db, Object object) {
        if (db == null) {
            return null;
        }
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return null;
        }
        Cursor cursor = null;
        try {
            String sql = mSQLBuilder.buildWhereSQL(object);
            if (!TextUtils.isEmpty(sql)) {
                String name = table.getName();
                cursor = db.query(name, null, sql, null, null, null, null);
            }

            if (cursor == null) {
                return null;
            }
            if (cursor.getCount() <= 0) {
                return null;
            }
            cursor.moveToFirst();
            return query(table.getClazz(), cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 根据 Cursor 的值加载对象
     *
     * @param clazz 需要加载的对象 class
     * @param cursor 数据库游标
     * @return 新对象，如果加载失败返回 null
     */
    public Object query(Class<?> clazz, Cursor cursor) {
        return mSQLBuilder.buildQueryValues(clazz, cursor);
    }

    /**
     * 更新数据库中的一行
     *
     * @param db DBDatabase 引擎
     * @param object 需要更新的对象
     * @return 受到影响的行数
     */
    public int update(SQLiteDatabase db, Object object) {
        return update(db, object, null, null);
    }

    /**
     * 按条件更新数据库中的一行
     * 如果列集不为 null, 则说明指更新了某些列，如果有需要获取更新后的完整数据，则通过传入的 ResultValue 对象返回
     *
     * @param db DBDatabase 引擎
     * @param object 需要更新的对象
     * @param columns 选中的列集合，如果为 null，则更新所有列
     * @param result 更新成功之后完整的数据 A，如果执行失败，则不设置 B，如果执行成功，columns 为 null, 则返回原对象 C，如果执行成功，columns 不为 null，则调用 query 获取更新后的值
     * @return 受到影响的行数
     */
    public int update(SQLiteDatabase db, Object object, String[] columns,
                      ResultValue<Object> result) {
        if (db == null) {
            return -1;
        }
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return -1;
        }
        int count = 0;
        ContentValues values = mSQLBuilder.buildUpdateValues(object, columns);
        String sql = mSQLBuilder.buildWhereSQL(object);

        if (!TextUtils.isEmpty(sql) && values != null) {
            String name = table.getName();
            count = db.update(name, values, sql, null);
        }

        if (count > 0 && result != null) {
            if (columns == null) {
                result.setValue(object);
            } else {
                Object value = query(db, object);
                result.setValue(value);
            }
        }
        return count;
    }

    /**
     * 往数据库中插入一行
     * A，如果不存在，与 insert 行为一致
     * B，如果存在，与 update 行为一致
     *
     * @param db DBDatabase 引擎
     * @param object 需要插入的对象
     * @return 执行是否成功
     */
    public boolean insertOrUpdate(SQLiteDatabase db, Object object) {
        return insertOrUpdate(db, object, null, null);
    }

    /**
     * 往数据库中插入一行
     * A，如果不存在，与 insert 行为一致
     * B，如果存在，与 update 行为一致
     *
     * @param db DBDatabase 引擎
     * @param object 需要插入的对象
     * @param columns 需要更新的列，如果是 insert，则忽略
     * @param result 更新后的完整结果
     * @return 执行是否成功
     */
    public boolean insertOrUpdate(SQLiteDatabase db, Object object, String[] columns,
                                  ResultValue<Object> result) {
        if (db == null) {
            return false;
        }
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return false;
        }

        if (exist(db, object)) {
            return update(db, object, columns, result) > 0;
        } else {
            long rowId = insert(db, object);
            if (rowId != -1 && result != null) {
                result.setValue(object);
            }
            return rowId != -1;
        }
    }

    /**
     * 往数据库中插入一行
     * A，如果不存在，与 insert 行为一致
     * B，如果存在，则丢弃
     *
     * @param db DBDatabase 引擎
     * @param object 需要插入的对象
     * @return 执行是否成功
     */
    public boolean insertOrDiscard(SQLiteDatabase db, Object object) {
        if (db == null) {
            return false;
        }
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return false;
        }

        return exist(db, object) || insert(db, object) != -1;
    }
}
