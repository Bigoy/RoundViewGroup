package com.bigoy.lib;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bigoy.lib.custom.CustomAttributes;
import com.bigoy.lib.custom.CustomConstraintLayout;

/**
 * 基于ConstraintLayout实现圆角ViewGroup，不管子View什么样子都能统一实现圆角
 * 其它的ViewGroup圆角可以依葫芦画瓢
 * <p>
 * 支持设置圆角大小
 * 支持设置内边框：大小、颜色
 * <p>
 * 不要调用设置背景的任何方法
 * <p>
 * 相比使用clipPath实现圆角的方式具有抗锯齿效果
 *
 * @author Bigoy
 * @version 1.1
 */
public class RoundConstraintLayout extends CustomConstraintLayout {
    protected static final String TAG = "RoundConstraintLayout";

    private Path mPath;

    private RectF mRectF;

    private Attributes mAttributes;

    private Paint mPaint;

    private int mWidth, mHeight;


    public RoundConstraintLayout(Context context) {
        super(context);
    }

    public RoundConstraintLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundConstraintLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint();
        mPath = new Path();
        mRectF = new RectF();
        super.init(context, attrs, defStyleAttr);
    }

    @Override
    protected void initAttributeSet(TypedArray typedArray) {
        super.initAttributeSet(typedArray);
        mAttributes = new Attributes(typedArray, getContext());
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        mWidth = getWidth();
        mHeight = getHeight();
        drawLayer(canvas);
        super.dispatchDraw(canvas);
        drawPath(canvas);
        drawStroke(canvas, mAttributes.mBorderColor, mAttributes.mBorderWidth);
    }

    /*
    绘制整体的圆角路径
     */
    protected void drawPath(Canvas canvas) {
        resetPaint();
        drawTopLeft(canvas);
        drawTopRight(canvas);
        drawBottomLeft(canvas);
        drawBottomRight(canvas);
        mPaint.setXfermode(null);
        canvas.restore();
    }

    protected void drawLayer(Canvas canvas) {
        canvas.saveLayer(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), mPaint, Canvas.ALL_SAVE_FLAG);
    }

    /*
    绘制内边框
     */
    protected void drawStroke(Canvas canvas, int strokeColor, int strokeWidth) {
        if (strokeColor == 0 || strokeWidth == 0) {
            return;
        }
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(strokeColor);
        mPaint.setStrokeWidth(strokeWidth);
        canvas.drawPath(getStrokePath(), mPaint);
    }

    /*
    计算内边框的Path
     */
    protected Path getStrokePath() {
        mPath.reset();
        mPath.moveTo(mAttributes.mLeftTopCornerRadius + getStrokeOffset(), getStrokeOffset());
        if (mAttributes.mLeftTopCornerRadius > 0) {
            mRectF.set(getStrokeOffset(), getStrokeOffset(), mAttributes.mLeftTopCornerRadius * 2, mAttributes.mLeftTopCornerRadius * 2);
            mPath.arcTo(mRectF, -90, -90);
        }
        mPath.lineTo(getStrokeOffset(), mHeight - mAttributes.mLeftBottomCornerRadius - getStrokeOffset());
        if (mAttributes.mLeftBottomCornerRadius > 0) {
            mRectF.set(getStrokeOffset(), mHeight - 2 * mAttributes.mLeftBottomCornerRadius, mAttributes.mLeftBottomCornerRadius * 2, mHeight - getStrokeOffset());
            mPath.arcTo(mRectF, 180, -90);
        }
        mPath.lineTo(mWidth - mAttributes.mRightBottomCornerRadius - getStrokeOffset(), mHeight - getStrokeOffset());
        if (mAttributes.mRightBottomCornerRadius > 0) {
            mRectF.set(mWidth - 2 * mAttributes.mRightBottomCornerRadius, mHeight - 2 * mAttributes.mRightBottomCornerRadius, mWidth - getStrokeOffset(), mHeight - getStrokeOffset());
            mPath.arcTo(mRectF, 90, -90);
        }
        mPath.lineTo(mWidth - getStrokeOffset(), mAttributes.mRightTopCornerRadius + getStrokeOffset());
        if (mAttributes.mRightTopCornerRadius > 0) {
            mRectF.set(mWidth - 2 * mAttributes.mRightTopCornerRadius, getStrokeOffset(), mWidth - getStrokeOffset(), 2 * mAttributes.mRightTopCornerRadius);
            mPath.arcTo(mRectF, 0, -90);
        }
        mPath.close();
        return mPath;
    }

    /*
    由于Line线条的绘制中线其实是在View的边界上，导致边框的宽度少一半。当存在圆角时，会出现圆角处的边框宽度
    和直线边界不一致的问题。因此，在计算内边框Path时，将边界Path的位置向View中心收拢边框宽度的1/2。
     */
    protected float getStrokeOffset() {
        return mAttributes.mBorderWidth * 0.5f;
    }

    /*
    绘制左上角位置的圆角
     */
    protected void drawTopLeft(Canvas canvas) {
        if (mAttributes.mLeftTopCornerRadius > 0) {
            mPath.reset();
            mPath.moveTo(0, mAttributes.mLeftTopCornerRadius);
            mPath.lineTo(0, 0);
            mPath.lineTo(mAttributes.mLeftTopCornerRadius, 0);
            mRectF.set(0, 0, mAttributes.mLeftTopCornerRadius * 2, mAttributes.mLeftTopCornerRadius * 2);
            mPath.arcTo(mRectF, -90, -90);
            mPath.close();
            canvas.drawPath(mPath, mPaint);
        }
    }

    /*
    绘制右上角位置的圆角
     */
    protected void drawTopRight(Canvas canvas) {
        if (mAttributes.mRightTopCornerRadius > 0) {
            mPath.reset();
            mPath.moveTo(mWidth - mAttributes.mRightTopCornerRadius, 0);
            mPath.lineTo(mWidth, 0);
            mPath.lineTo(mWidth, mAttributes.mRightTopCornerRadius);
            mRectF.set(mWidth - 2 * mAttributes.mRightTopCornerRadius, 0, mWidth, mAttributes.mRightTopCornerRadius * 2);
            mPath.arcTo(mRectF, 0, -90);
            mPath.close();
            canvas.drawPath(mPath, mPaint);
        }
    }

    /*
    绘制左下角位置的圆角
     */
    protected void drawBottomLeft(Canvas canvas) {
        if (mAttributes.mLeftBottomCornerRadius > 0) {
            mPath.reset();
            mPath.moveTo(0, mHeight - mAttributes.mLeftBottomCornerRadius);
            mPath.lineTo(0, mHeight);
            mPath.lineTo(mAttributes.mLeftBottomCornerRadius, mHeight);
            mRectF.set(0, mHeight - 2 * mAttributes.mLeftBottomCornerRadius, mAttributes.mLeftBottomCornerRadius * 2, mHeight);
            mPath.arcTo(mRectF, 90, 90);
            mPath.close();
            canvas.drawPath(mPath, mPaint);
        }
    }

    /*
    绘制右下角位置的圆角
     */
    protected void drawBottomRight(Canvas canvas) {
        if (mAttributes.mRightBottomCornerRadius > 0) {
            mPath.reset();
            mPath.moveTo(mWidth - mAttributes.mRightBottomCornerRadius, mHeight);
            mPath.lineTo(mWidth, mHeight);
            mPath.lineTo(mWidth, mHeight - mAttributes.mRightBottomCornerRadius);
            mRectF.set(mWidth - 2 * mAttributes.mRightBottomCornerRadius, mHeight - 2 * mAttributes.mRightBottomCornerRadius, mWidth, mHeight);
            mPath.arcTo(mRectF, 0, 90);
            mPath.close();
            canvas.drawPath(mPath, mPaint);
        }
    }

    protected void resetPaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mAttributes.mBorderWidth);
        mPaint.setColor(mAttributes.mBorderColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    /**
     * 设置左上角圆角半径
     *
     * @param leftTopCornerRadius 左上角圆角半径
     */
    public void setLeftTopCornerRadius(int leftTopCornerRadius) {
        mAttributes.mLeftTopCornerRadius = leftTopCornerRadius;
        postInvalidate();
    }

    /**
     * 设置左下角圆角半径
     *
     * @param leftBottomCornerRadius 左下角圆角半径
     */
    public void setLeftBottomCornerRadius(int leftBottomCornerRadius) {
        mAttributes.mLeftBottomCornerRadius = leftBottomCornerRadius;
        postInvalidate();
    }

    /**
     * 设置右上角圆角半径
     *
     * @param rightTopCornerRadius 右上角圆角半径
     */
    public void setRightTopCornerRadius(int rightTopCornerRadius) {
        mAttributes.mRightTopCornerRadius = rightTopCornerRadius;
        postInvalidate();
    }

    /**
     * 设置右下角圆角半径
     *
     * @param rightBottomCornerRadius 右下角圆角半径
     */
    public void setRightBottomCornerRadius(int rightBottomCornerRadius) {
        mAttributes.mRightBottomCornerRadius = rightBottomCornerRadius;
        postInvalidate();
    }

    /**
     * 设置内边框宽度
     *
     * @param innerStrokeWidth 内边框宽度
     */
    public void setInnerStrokeWidth(int innerStrokeWidth) {
        mAttributes.mBorderWidth = innerStrokeWidth;
        postInvalidate();
    }

    /**
     * 设置内边框颜色
     *
     * @param innerStrokeColor 内边框颜色
     */
    public void setInnerStrokeColor(int innerStrokeColor) {
        mAttributes.mBorderColor = innerStrokeColor;
        postInvalidate();
    }

    @Override
    protected int[] getStyleableRes() {
        return R.styleable.RoundConstraintLayout;
    }

    @Override
    public void setBackground(Drawable background) {
        Log.w(TAG, "Not allow to call setBackground().");
    }

    @Override
    public void setBackgroundColor(int color) {
        Log.w(TAG, "Not allow to call setBackground().");
    }

    @Override
    public void setBackgroundResource(int resid) {
        Log.w(TAG, "Not allow to call setBackground().");
    }

    @Override
    public void setBackgroundTintBlendMode(@Nullable BlendMode blendMode) {
        Log.w(TAG, "Not allow to call setBackground().");
    }

    @Override
    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        Log.w(TAG, "Not allow to call setBackground().");
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        Log.w(TAG, "Not allow to call setBackground().");
    }

    @Deprecated
    @Override
    public void setBackgroundDrawable(Drawable background) {
        Log.w(TAG, "Not allow to call setBackground().");
    }

    protected static class Attributes extends CustomAttributes {

        /*
        左上角圆角半径
         */
        protected int mLeftTopCornerRadius;

        /*
        左下角圆角半径
         */
        protected int mLeftBottomCornerRadius;

        /*
        右上角圆角半径
         */
        protected int mRightTopCornerRadius;

        /*
        右下角圆角半径
         */
        protected int mRightBottomCornerRadius;

        /*
        内边框宽度
         */
        protected int mBorderWidth;

        /*
        内边框颜色
         */
        protected int mBorderColor;


        Attributes(TypedArray typedArray, Context context) {
            super(typedArray, context);
        }

        @Override
        protected void buildAttributes(TypedArray typedArray) {
            int unionCornerRadius = typedArray.getDimensionPixelSize(
                    R.styleable.RoundConstraintLayout_cl_cornerRadius, -1);
            mLeftTopCornerRadius = typedArray.getDimensionPixelSize(
                    R.styleable.RoundConstraintLayout_cl_cornerRadius_leftTop, -1);
            mLeftBottomCornerRadius = typedArray.getDimensionPixelSize(
                    R.styleable.RoundConstraintLayout_cl_cornerRadius_leftBottom, -1);
            mRightTopCornerRadius = typedArray.getDimensionPixelSize(
                    R.styleable.RoundConstraintLayout_cl_cornerRadius_rightTop, -1);
            mRightBottomCornerRadius = typedArray.getDimensionPixelSize(
                    R.styleable.RoundConstraintLayout_cl_cornerRadius_rightBottom, -1);

            //圆角值是否有效
            checkCornerRadiusValues(unionCornerRadius);

            mBorderWidth = Math.max(0, typedArray.getDimensionPixelSize(
                    R.styleable.RoundConstraintLayout_cl_borderWidth, 0));
            mBorderColor = typedArray.getColor(
                    R.styleable.RoundConstraintLayout_cl_borderColor, 0);
        }

        protected void checkCornerRadiusValues(int unionCornerRadius) {
            //a.当 unionCornerRadius 属性未设置时，如果设置其它四个圆角中任意一个或多个值，重设
            //  unionCornerRadius0，其它未设置值的圆角值重设为0。
            if (unionCornerRadius == -1) {
                mLeftTopCornerRadius = mLeftTopCornerRadius == -1 ? 0 : mLeftTopCornerRadius;
                mLeftBottomCornerRadius = mLeftBottomCornerRadius == -1 ? 0 : mLeftBottomCornerRadius;
                mRightTopCornerRadius = mRightTopCornerRadius == -1 ? 0 : mRightTopCornerRadius;
                mRightBottomCornerRadius = mRightBottomCornerRadius == -1 ? 0 : mRightBottomCornerRadius;
            }

            //b.当 unionCornerRadius 属性设置为 >=0时，其它四个圆角设置的值失效。
            if (unionCornerRadius >= 0) {
                mLeftTopCornerRadius = unionCornerRadius;
                mLeftBottomCornerRadius = unionCornerRadius;
                mRightTopCornerRadius = unionCornerRadius;
                mRightBottomCornerRadius = unionCornerRadius;
            }

            //c.当 unionCornerRadius 属性设置为 <0时，其它四个圆角的值重设为0。
            if (unionCornerRadius < -1) {
                mLeftTopCornerRadius = 0;
                mLeftBottomCornerRadius = 0;
                mRightTopCornerRadius = 0;
                mRightBottomCornerRadius = 0;
            }
        }
    }
}
