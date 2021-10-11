package com.sp.tiny.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: 后知后觉(307817387/myz7656)
 * email: whuzhanyuanmin@126.com
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    /**
     * Comma-separated list of properties that should be indexed, e.g. "propertyA, propertyB,
     * propertyC" To specify order, add ASC or DESC after column name, e.g.: "propertyA DESC,
     * propertyB ASC" This should be only set if this annotation is used in {@link
     * Entity#indexes()}
     */
    String value();

    /**
     * Optional name of the index. If omitted, then generated automatically by greenDAO with base on
     * property/properties column name(s)
     */
    String name();

    /**
     * Whether the unique constraint should be created with base on this index
     */
    boolean unique() default false;
}
