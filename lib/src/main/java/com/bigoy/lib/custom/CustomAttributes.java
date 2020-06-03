package com.bigoy.lib.custom;

import android.content.Context;
import android.content.res.TypedArray;

public abstract class CustomAttributes {
    protected Context mContext;

    public CustomAttributes(TypedArray typedArray, Context context) {
        this.mContext = context;
        if (typedArray != null) {
            buildAttributes(typedArray);
        }
    }

    protected abstract void buildAttributes(TypedArray typedArray);
}
