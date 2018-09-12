package com.ray.image_picker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-12 18:28
 *  description : 
 */
public class PhotoItemDercoration extends RecyclerView.ItemDecoration {

    public PhotoItemDercoration() {
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            int lineCount = gridLayoutManager.getItemCount() / spanCount;
            if (gridLayoutManager.getItemCount() % spanCount > 0) {
                lineCount += 1;
            }
            int position = parent.getChildAdapterPosition(view) + 1;
            if (position == 1) {
                //first
                outRect.set(16, 16, 8, 8);
            } else if (position == gridLayoutManager.getItemCount()) {
                //last
                outRect.set(8, 8, 16, 16);
            } else if (position / spanCount == 0){
                outRect.set(8, 16, 8, 8);
            } else if (position / spanCount == lineCount - 1 && position % spanCount > 0){
                outRect.set(8, 8, 8, 16);
            } else if (position % spanCount == 0) {
                outRect.set(8, 16, 16, 8);
            } else {
                outRect.set(8, 8, 8, 8);
            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

    }
}
