package com.sp.tiny.orm.core;

import android.support.annotation.NonNull;

/**
 * author: 后知后觉(307817387/myz7656)
 * email: whuzhanyuanmin@126.com
 */

public class ResultValue<T> {

    private T mValue;

    public ResultValue() {
        mValue = null;
    }

    public ResultValue(@NonNull T value) {
        mValue = value;
    }

    public T getValue() {
        return mValue;
    }

    public void setValue(@NonNull T value) {
        mValue = value;
    }
}
