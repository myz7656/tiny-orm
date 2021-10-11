package com.sp.tiny.orm.core;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * author: 后知后觉(307817387/myz7656)
 * email: whuzhanyuanmin@126.com
 */

public class Table {
    private String mName;
    private Class<?> mClazz;
    private final LinkedList<Column> mPrimaryKey;
    private final LinkedHashMap<String, Column> mColumns;
    private final LinkedList<Index> mIndexes;

    public Table(String name, Class<?> clazz) {
        mName = name;
        mClazz = clazz;
        mPrimaryKey = new LinkedList<>();
        mColumns = new LinkedHashMap<>();
        mIndexes = new LinkedList<>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Class<?> getClazz() {
        return mClazz;
    }

    public void setClazz(Class<?> clazz) {
        mClazz = clazz;
    }

    public Map<String, Column> getColumns() {
        return mColumns;
    }

    public void addColumn(String name, Column column) {
        mColumns.put(name, column);
    }

    public List<Index> getIndexes() {
        return mIndexes;
    }

    public void addIndex(Index index) {
        mIndexes.add(index);
    }

    public List<Column> getPrimaryKey() {
        return mPrimaryKey;
    }

    public void addPrimaryKey(Column column) {
        mPrimaryKey.add(column);
    }

    public static class Index {
        String mName;
        String mValues;
        boolean mIsUnique;

        public Index(String name, String values, boolean isUnique) {
            mName = name;
            mValues = values;
            mIsUnique = isUnique;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getValues() {
            return mValues;
        }

        public void setValues(String values) {
            mValues = values;
        }

        public boolean isUnique() {
            return mIsUnique;
        }

        public void setUnique(boolean unique) {
            mIsUnique = unique;
        }
    }
}
