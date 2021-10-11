package com.sp.tiny.orm.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author: 后知后觉(307817387/myz7656)
 * email: whuzhanyuanmin@126.com
 */

public class SQLBuilder {
    public static final String CREATE = "CREATE ";
    public static final String DROP = "DROP ";
    public static final String TABLE = "TABLE ";
    public static final String TABLE_IF_NOT_EXISTS = "TABLE IF NOT EXISTS ";
    public static final String INDEX_IF_NOT_EXISTS = "INDEX IF NOT EXISTS ";
    public static final String PARENTHESES_LEFT = "(";
    public static final String PARENTHESES_RIGHT = ")";
    public static final String COMMA = ", ";
    public static final String BLANK = " ";
    public static final String PRIMARY_KEY = "PRIMARY KEY ";
    public static final String AUTOINCREMENT = "AUTOINCREMENT ";
    public static final String NOT_NULL = "NOT NULL ";
    public static final String UNIQUE = "UNIQUE ";
    public static final String ON = "ON ";
    public static final String SEMICOLON = ";";
    public static final String EQUAL = "=";
    public static final String SINGLE_QUOTE = "'";
    public static final String AND = " AND ";

    private final TableManager mTableManager;

    public SQLBuilder(@NonNull TableManager manager) {
        mTableManager = manager;
    }

    public String buildCreateTableSQL(Class<?> clazz) {
        Table table = mTableManager.getTable(clazz);
        if (table == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(CREATE).append(TABLE_IF_NOT_EXISTS);
        builder.append(table.getName());
        builder.append(PARENTHESES_LEFT);

        boolean singlePrimaryKey = table.getPrimaryKey().size() == 1;
        Map<String, Column> columnMap = table.getColumns();
        Set<String> keys = columnMap.keySet();
        for (String key : keys) {
            Column column = columnMap.get(key);
            if (column == null) {
                continue;
            }
            builder.append(column.getName());
            builder.append(DataType.getSQLDataTypeString(column.getClassType()));
            if (column.getId() != null && singlePrimaryKey) {
                builder.append(PRIMARY_KEY);
                if (column.getId().isAutoIncrement()) {
                    builder.append(AUTOINCREMENT);
                }
            }
            if (!column.isCanBeNull()) {
                builder.append(NOT_NULL);
            }
            if (column.isUnique()) {
                builder.append(UNIQUE);
            }
            builder.append(COMMA);
        }
        if (table.getPrimaryKey().size() > 1) {
            builder.append(PRIMARY_KEY);
            builder.append(PARENTHESES_LEFT);
            for (Column column : table.getPrimaryKey()) {
                builder.append(column.getName());
                builder.append(COMMA);
            }
            builder.deleteCharAt(builder.lastIndexOf(COMMA));
            builder.append(PARENTHESES_RIGHT);
        } else {
            builder.deleteCharAt(builder.lastIndexOf(COMMA));
        }
        builder.append(PARENTHESES_RIGHT);
        return builder.toString();
    }

    public String buildDeleteTableSQL(Class<?> clazz) {
        Table table = mTableManager.getTable(clazz);
        if (table == null) {
            return null;
        }

        return DROP + TABLE + table.getName() + SEMICOLON;
    }

    public String[] buildCreateIndexSQL(Class<?> clazz) {
        Table table = mTableManager.getTable(clazz);
        if (table == null) {
            return null;
        }

        List<Table.Index> indexList = table.getIndexes();
        if (indexList == null || indexList.isEmpty()) {
            return null;
        }

        String[] indexSQL = new String[indexList.size()];
        int count = 0;
        for (Table.Index index : indexList) {
            StringBuilder builder = new StringBuilder();
            String name = index.getName();
            String value = index.getValues();
            boolean unique = index.isUnique();
            builder.append(CREATE);
            if (unique) {
                builder.append(UNIQUE);
            }
            builder.append(INDEX_IF_NOT_EXISTS);
            builder.append(name);
            builder.append(BLANK);
            builder.append(ON);
            builder.append(table.getName());
            builder.append(PARENTHESES_LEFT);
            builder.append(value);
            builder.append(PARENTHESES_RIGHT);
            builder.append(SEMICOLON);
            indexSQL[count++] = builder.toString();
        }
        return indexSQL;
    }

    public String buildCreateIndexSQL(Class<?> clazz, String indexName) {
        Table table = mTableManager.getTable(clazz);
        if (table == null || TextUtils.isEmpty(indexName)) {
            return null;
        }

        List<Table.Index> indexList = table.getIndexes();
        if (indexList == null || indexList.size() <= 0) {
            return null;
        }

        for (Table.Index index : indexList) {
            String name = index.getName();
            if (TextUtils.equals(name, indexName)) {
                String value = index.getValues();
                boolean unique = index.isUnique();
                StringBuilder builder = new StringBuilder();
                builder.append(CREATE);
                if (unique) {
                    builder.append(UNIQUE);
                }
                builder.append(INDEX_IF_NOT_EXISTS);
                builder.append(name);
                builder.append(BLANK);
                builder.append(ON);
                builder.append(table.getName());
                builder.append(PARENTHESES_LEFT);
                builder.append(value);
                builder.append(PARENTHESES_RIGHT);
                builder.append(SEMICOLON);
                return builder.toString();
            }
        }
        return null;
    }

    public ContentValues buildInsertValues(Object object) {
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        Map<String, Column> columnMap = table.getColumns();
        try {
            for (String key : columnMap.keySet()) {
                Column column = columnMap.get(key);
                if (column == null) {
                    continue;
                }
                /**
                 * 如果是自增长主键，则不赋值。
                 */
                if (column.getId() != null && column.getId().isAutoIncrement()) {
                    continue;
                }
                String columnName = column.getName();
                Field columnField = column.getField();

                Object columnValue = columnField.get(object);
                if (columnValue == null) {
                    values.putNull(columnName);
                    continue;
                }

                int columnClassType = column.getClassType();
                convertToValue(values, columnName, columnValue, columnClassType);
            }
        } catch (IllegalAccessException ignored) {}
        return values;
    }

    public ContentValues buildUpdateValues(Object object, String[] columns) {
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        Map<String, Column> columnMap = table.getColumns();
        try {
            for (String key : columnMap.keySet()) {
                Column column = columnMap.get(key);
                if (column == null) {
                    continue;
                }
                if (column.getId() != null) {
                    continue;
                }
                String columnName = column.getName();
                if (columns != null && !contains(columnName, columns)) {
                    continue;
                }

                Field columnField = column.getField();

                Object columnValue = columnField.get(object);
                if (columnValue == null) {
                    values.putNull(columnName);
                    continue;
                }

                int columnClassType = column.getClassType();
                convertToValue(values, columnName, columnValue, columnClassType);
            }
        } catch (IllegalAccessException ignored) {}
        return values;
    }

    public Object buildQueryValues(Class<?> clazz, Cursor cursor) {
        Table table = mTableManager.getTable(clazz);
        if (table == null) {
            return null;
        }
        Object objectValue = null;
        try {
            objectValue = clazz.newInstance();
            Map<String, Column> columnMap = table.getColumns();
            for (String key : columnMap.keySet()) {
                Column column = columnMap.get(key);
                if (column == null) {
                    continue;
                }
                String columnName = column.getName();
                int index = cursor.getColumnIndex(columnName);
                if (index == -1 || cursor.isNull(index)) {
                    continue;
                }

                int columnClassType = column.getClassType();
                Object value = null;
                switch (columnClassType) {
                    case DataType.CLASS_TYPE_STRING:
                        value = cursor.getString(index);
                        break;
                    case DataType.CLASS_TYPE_BOOLEAN:
                        int intValue = cursor.getInt(index);
                        value = (intValue != 0);
                        break;
                    case DataType.CLASS_TYPE_CHAR:
                        String strValue = cursor.getString(index);
                        if (strValue.length() > 0) {
                            value = strValue.charAt(0);
                        }
                        break;
                    case DataType.CLASS_TYPE_DOUBLE:
                        value = cursor.getDouble(index);
                        break;
                    case DataType.CLASS_TYPE_FLOAT:
                        value = cursor.getFloat(index);
                        break;
                    case DataType.CLASS_TYPE_LONG:
                        value = cursor.getLong(index);
                        break;
                    case DataType.CLASS_TYPE_INT:
                        value = cursor.getInt(index);
                        break;
                    case DataType.CLASS_TYPE_SHORT:
                        value = cursor.getShort(index);
                        break;
                    case DataType.CLASS_TYPE_BYTE:
                        value = (byte) cursor.getShort(index);
                        break;
                    case DataType.CLASS_TYPE_DATE:
                        long longValue = cursor.getLong(index);
                        value = new Date(longValue);
                        break;
                    case DataType.CLASS_TYPE_BYTE_ARRAY:
                        value = cursor.getBlob(index);
                        break;
                }
                Field columnField = column.getField();
                columnField.set(objectValue, value);
            }
        } catch (InstantiationException | IllegalAccessException ignored) {}
        return objectValue;
    }

    public String buildWhereSQL(Object object) {
        Table table = mTableManager.getTable(object);
        if (table == null) {
            return null;
        }

        List<Column> primaryKeys = table.getPrimaryKey();
        if (primaryKeys == null || primaryKeys.isEmpty()) {
            return null;
        }

        StringBuilder whereCase = new StringBuilder();
        for (Column primaryKey : primaryKeys) {
            whereCase.append(buildWhereSQLiteForPrimaryKey(object, primaryKey));
            whereCase.append(AND);
        }

        if (whereCase.toString().endsWith(AND)) {
            whereCase.delete(whereCase.lastIndexOf(AND), whereCase.length() - 1);
        }
        return whereCase.toString();
    }

    private String buildWhereSQLiteForPrimaryKey(Object object,
                                                 Column primaryKey) {
        String id = "";
        Object value = null;
        String whereCase = "";
        int sqlType = DataType.SQL_TYPE_BLOB;
        try {
            if (primaryKey != null && primaryKey.getId() != null) {
                id = primaryKey.getName();
                value = primaryKey.getField().get(object);
                sqlType = DataType.getSQLDataType(primaryKey.getClassType());
            }
        } catch (IllegalAccessException ignored) {}

        if (TextUtils.isEmpty(id)) {
            return whereCase;
        }

        switch (sqlType) {
            case DataType.SQL_TYPE_BLOB:
                break;
            case DataType.SQL_TYPE_TEXT:
                whereCase = id + EQUAL + SINGLE_QUOTE + value + SINGLE_QUOTE;
                break;
            case DataType.SQL_TYPE_REAL:
            case DataType.SQL_TYPE_INTEGER:
                whereCase = id + EQUAL + value;
                break;
        }
        return whereCase;
    }

    private boolean contains(String column, String[] columns) {
        if (TextUtils.isEmpty(column) || columns == null || columns.length <= 0) {
            return false;
        }

        for (String s : columns) {
            if (s.compareToIgnoreCase(column) == 0) {
                return true;
            }
        }
        return false;
    }

    private void convertToValue(ContentValues values, String columnName, Object columnValue,
                                int columnClassType) {
        switch (columnClassType) {
            case DataType.CLASS_TYPE_STRING:
                values.put(columnName, (String) columnValue);
                break;
            case DataType.CLASS_TYPE_BOOLEAN:
                boolean boolValue = (boolean) columnValue;
                values.put(columnName, boolValue ? 1 : 0);
                break;
            case DataType.CLASS_TYPE_CHAR:
                String strValue = columnValue.toString();
                values.put(columnName, strValue);
                break;
            case DataType.CLASS_TYPE_DOUBLE:
                values.put(columnName, (Double) columnValue);
                break;
            case DataType.CLASS_TYPE_FLOAT:
                values.put(columnName, (Float) columnValue);
                break;
            case DataType.CLASS_TYPE_LONG:
                values.put(columnName, (Long) columnValue);
                break;
            case DataType.CLASS_TYPE_INT:
                values.put(columnName, (Integer) columnValue);
                break;
            case DataType.CLASS_TYPE_SHORT:
                values.put(columnName, (Short) columnValue);
                break;
            case DataType.CLASS_TYPE_BYTE:
                values.put(columnName, (Byte) columnValue);
                break;
            case DataType.CLASS_TYPE_DATE:
                Date dateValue = (Date) columnValue;
                values.put(columnName, dateValue.getTime());
                break;
            case DataType.CLASS_TYPE_BYTE_ARRAY:
                values.put(columnName, (byte[]) columnValue);
                break;
        }
    }
}
