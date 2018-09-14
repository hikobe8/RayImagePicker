package com.ray.image_picker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-12 17:38
 *  description : 
 */
public class SquareImageView extends android.support.v7.widget.AppCompatImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width;
        setMeasuredDimension(size, size);
    }
}
