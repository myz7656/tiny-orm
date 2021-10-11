package com.sp.tiny.orm.core;

import android.text.TextUtils;

import com.sp.tiny.orm.annotation.Entity;
import com.sp.tiny.orm.annotation.Id;
import com.sp.tiny.orm.annotation.Index;
import com.sp.tiny.orm.annotation.NotNull;
import com.sp.tiny.orm.annotation.Property;
import com.sp.tiny.orm.annotation.Unique;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * author: 后知后觉(307817387/myz7656)
 * email: whuzhanyuanmin@126.com
 */

public class TableManager {
    private final HashMap<String, Table> mSQLiteTable;

    public TableManager() {
        mSQLiteTable = new HashMap<>();
    }

    public Table getTable(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        return getTable(clazz);
    }

    public Table getTable(Class<?> clazz) {
        String name = getClassName(clazz);
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        if (isNotExist(name)) {
            return initTable(clazz);
        }
        return getTable(name);
    }

    private Table initTable(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        /**
         * first step: generate table.
         */
        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity == null) {
            return null;
        }
        String name = entity.name();
        if (TextUtils.isEmpty(name)) {
            name = clazz.getSimpleName();
        }
        Table table = new Table(name, clazz);

        /**
         * second step: generate indexes.
         */
        Index[] indexes = entity.indexes();
        for (Index value : indexes) {
            String indexName = value.name();
            String indexValue = value.value();
            if (TextUtils.isEmpty(indexName) || TextUtils.isEmpty(indexValue)) {
                continue;
            }
            boolean isUnique = value.unique();
            Table.Index index = new Table.Index(indexName, indexValue, isUnique);
            table.addIndex(index);
        }
        String className = getClassName(clazz);

        /**
         * third step: generate columns.
         */
        while (clazz != null && clazz != Object.class) {
            Field[] fs = clazz.getDeclaredFields();
            for (Field f : fs) {
                /**
                 * only parse Property Annotation.
                 */
                Property property = f.getAnnotation(Property.class);
                if (property == null) {
                    continue;
                }
                String columnName = property.name();
                if (TextUtils.isEmpty(property.name())) {
                    columnName = f.getName();
                }
                f.setAccessible(true);
                Column column = new Column(columnName, f);
                int classType = DataType.getFieldClassType(f);
                column.setClassType(classType);

                /**
                 * parse primary key.
                 */
                Id id = f.getAnnotation(Id.class);
                if (id != null) {
                    Column.Id idKey = new Column.Id();
                    boolean autoIncrement = id.autoincrement();
                    idKey.setAutoIncrement(autoIncrement && DataType.canAutoIncrement(classType));
                    column.setId(idKey);
                    column.setCanBeNull(false);
                    column.setUnique(true);
                }

                /**
                 * parse not null.
                 */
                NotNull notNull = f.getAnnotation(NotNull.class);
                if (notNull != null) {
                    column.setCanBeNull(false);
                }

                /**
                 * parse unique.
                 */
                Unique unique = f.getAnnotation(Unique.class);
                if (unique != null) {
                    column.setUnique(true);
                }
                table.addColumn(columnName, column);
                if (column.getId() != null) {
                    table.addPrimaryKey(column);
                }
            }
            clazz = clazz.getSuperclass();
        }
        addTable(className, table);
        return table;
    }

    private synchronized boolean isNotExist(String name) {
        return !mSQLiteTable.containsKey(name);
    }

    private synchronized void addTable(String name, Table table) {
        if (isNotExist(name)) {
            mSQLiteTable.put(name, table);
        }
    }

    private synchronized Table getTable(String name) {
        return mSQLiteTable.get(name);
    }

    private String getClassName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        return clazz.getName();
    }
}
