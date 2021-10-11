package com.sp.tiny.orm.core;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * author: 后知后觉(307817387/myz7656)
 * email: whuzhanyuanmin@126.com
 */

public class DataType {
    /**
     * INTEGER. The name is a signed integer, stored in 1, 2, 3, 4, 6, or 8
     * bytes depending on the magnitude of the name.
     */
    public static final String INTEGER = " INTEGER ";

    /**
     * REAL. The name is a floating point name, stored as an 8-byte IEEE
     * floating point number.
     */
    public static final String REAL = " REAL ";

    /**
     * TEXT. The name is a text string, stored using the database encoding
     * (UTF-8, UTF-16BE or UTF-16LE).
     */
    public static final String TEXT = " TEXT ";

    /**
     * BLOB. The name is a blob of data, stored exactly as it was input.
     */
    public static final String BLOB = " BLOB ";


    public static final int CLASS_TYPE_UNKNOWN = 0;
    public static final int CLASS_TYPE_STRING = 1;
    public static final int CLASS_TYPE_BOOLEAN = 2;
    public static final int CLASS_TYPE_DOUBLE = 3;
    public static final int CLASS_TYPE_FLOAT = 4;
    public static final int CLASS_TYPE_LONG = 5;
    public static final int CLASS_TYPE_INT = 6;
    public static final int CLASS_TYPE_SHORT = 7;
    public static final int CLASS_TYPE_BYTE = 8;
    public static final int CLASS_TYPE_BYTE_ARRAY = 9;
    public static final int CLASS_TYPE_CHAR = 10;
    public static final int CLASS_TYPE_DATE = 11;

    public static final int SQL_TYPE_INTEGER = 0;
    public static final int SQL_TYPE_REAL = 1;
    public static final int SQL_TYPE_TEXT = 2;
    public static final int SQL_TYPE_BLOB = 3;

    public static int getSQLDataType(int classType) {
        switch (classType) {
            case CLASS_TYPE_STRING:
            case CLASS_TYPE_CHAR:
                return SQL_TYPE_TEXT;
            case CLASS_TYPE_DOUBLE:
            case CLASS_TYPE_FLOAT:
                return SQL_TYPE_REAL;
            case CLASS_TYPE_BOOLEAN:
            case CLASS_TYPE_LONG:
            case CLASS_TYPE_INT:
            case CLASS_TYPE_SHORT:
            case CLASS_TYPE_BYTE:
            case CLASS_TYPE_DATE:
                return SQL_TYPE_INTEGER;
            case CLASS_TYPE_BYTE_ARRAY:
            default:
                return SQL_TYPE_BLOB;
        }
    }

    public static String getSQLDataTypeString(int classType) {
        int sqlType = getSQLDataType(classType);
        switch (sqlType) {
            case SQL_TYPE_INTEGER:
                return INTEGER;
            case SQL_TYPE_TEXT:
                return TEXT;
            case SQL_TYPE_REAL:
                return REAL;
            case SQL_TYPE_BLOB:
            default:
                return BLOB;
        }
    }

    public static boolean canAutoIncrement(int classType) {
        int sqlType = getSQLDataType(classType);
        switch (sqlType) {
            case SQL_TYPE_INTEGER:
                return true;
            case SQL_TYPE_TEXT:
            case SQL_TYPE_REAL:
            case SQL_TYPE_BLOB:
            default:
                return false;
        }
    }

    public static int getFieldClassType(@NonNull Field f) {
        Class<?> type = f.getType();
        if (CharSequence.class.isAssignableFrom(type)) {
            return CLASS_TYPE_STRING;
        } else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return CLASS_TYPE_BOOLEAN;
        } else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            return CLASS_TYPE_DOUBLE;
        } else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
            return CLASS_TYPE_FLOAT;
        } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return CLASS_TYPE_LONG;
        } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return CLASS_TYPE_INT;
        } else if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
            return CLASS_TYPE_SHORT;
        } else if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
            return CLASS_TYPE_BYTE;
        } else if (byte[].class.isAssignableFrom(type) || Byte[].class.isAssignableFrom(type)) {
            return CLASS_TYPE_BYTE_ARRAY;
        } else if (char.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type)) {
            return CLASS_TYPE_CHAR;
        } else if (Date.class.isAssignableFrom(type)) {
            return CLASS_TYPE_DATE;
        }
        return CLASS_TYPE_UNKNOWN;
    }
}
