package com.bigoy.lib.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;


public class CustomConstraintLayout extends ConstraintLayout {

    public CustomConstraintLayout(Context context) {
        super(context);
        init(context, null, -1);
    }

    public CustomConstraintLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);
    }

    public CustomConstraintLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    protected void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, getStyleableRes());
            initAttributeSet(ta);
            ta.recycle();
        }
    }

    protected void initAttributeSet(TypedArray typedArray) {
    }

    protected int[] getStyleableRes() {
        return new int[]{};
    }

}
