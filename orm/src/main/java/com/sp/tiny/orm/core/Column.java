package com.sp.tiny.orm.core;

import java.lang.reflect.Field;

/**
 * author: 后知后觉(307817387/myz7656)
 * email: whuzhanyuanmin@126.com
 */

public class Column {
    private String mName;
    private Field mField;
    private int mClassType;

    private Id mId;
    private boolean mCanBeNull;
    private boolean mIsUnique;

    public Column(String name, Field field) {
        this(name, field, DataType.CLASS_TYPE_UNKNOWN);
    }

    public Column(String name, Field field, int classType) {
        mName = name;
        mField = field;
        mClassType = classType;

        mId = null;
        mCanBeNull = true;
        mIsUnique = false;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Field getField() {
        return mField;
    }

    public void setField(Field field) {
        mField = field;
    }

    public int getClassType() {
        return mClassType;
    }

    public void setClassType(int classType) {
        mClassType = classType;
    }

    public Id getId() {
        return mId;
    }

    public void setId(Id id) {
        mId = id;
    }

    public boolean isCanBeNull() {
        return mCanBeNull;
    }

    public void setCanBeNull(boolean canBeNull) {
        mCanBeNull = canBeNull;
    }

    public boolean isUnique() {
        return mIsUnique;
    }

    public void setUnique(boolean unique) {
        mIsUnique = unique;
    }

    public static class Id {
        boolean mAutoIncrement;
        public Id() {
            mAutoIncrement = false;
        }

        public boolean isAutoIncrement() {
            return mAutoIncrement;
        }

        public void setAutoIncrement(boolean autoIncrement) {
            mAutoIncrement = autoIncrement;
        }
    }
}