package com.ray.image_picker;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-12 18:28
 *  description : 
 */
public class PhotoItemDecoration extends RecyclerView.ItemDecoration {

    private final static int DEFAULT_DIVIDER_SIZE = 12;

    private int mDividerSize = DEFAULT_DIVIDER_SIZE;

    public PhotoItemDecoration() {

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull final View view, @NonNull final RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final int spanCount = gridLayoutManager.getSpanCount();
            int minDistance = mDividerSize / spanCount;
            int lineCount = gridLayoutManager.getItemCount() / spanCount;
            int position = parent.getChildAdapterPosition(view);
            int left = 0;
            int top;
            int right = 0;
            int bottom;
            if (position / spanCount == 0) {
                //第一排
                top = 0;
                bottom = mDividerSize / 2;
            } else if (position / spanCount == lineCount - 1 && position % spanCount >= 0) {
                //最后一排
                top = mDividerSize / 2;
                bottom = 0;
            } else {
                //中间
                top = mDividerSize / 2;
                bottom = mDividerSize/2;
            }
            for (int i = 0; i < spanCount; i++) {
                if (position % spanCount == i) {
                    left = minDistance * i;
                    right = minDistance * (spanCount - 1 - i);
                    break;
                }
            }
            outRect.set(left, top, right, bottom);
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

    }
}
