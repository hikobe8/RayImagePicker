package com.ray.image_picker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ray.image_picker.R;
import com.ray.image_picker.bean.Photo;
import com.ray.image_picker.util.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-12 17:10
 *  description : 
 */
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Photo> mPhotos = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private static int sItemWidth;
    private OnPhotoClickListener mOnPhotoClickListener;

    public interface OnPhotoClickListener {
        void onPhotoItemClick(Photo photo);
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        mOnPhotoClickListener = onPhotoClickListener;
    }

    public PhotoAdapter(Context context, int spanCount) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        sItemWidth = (int) (DeviceUtil.getDeviceWidth(context) / spanCount + 0.5f);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PhotoHolder(mLayoutInflater.inflate(R.layout.item_photo, viewGroup, false), mContext, mOnPhotoClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof PhotoHolder) {
            ((PhotoHolder) viewHolder).bindData(mPhotos.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void setData(List<Photo> photos) {
        mPhotos.clear();
        mPhotos.addAll(photos);
        notifyDataSetChanged();
    }

    static class PhotoHolder extends RecyclerView.ViewHolder {

        private final SimpleTarget<Bitmap> mBitmapSimpleTarget;
        private Photo mPhoto;
        private ImageView mImageView;
        private Context mContext;
        private int mLoadState;

        public PhotoHolder(@NonNull final View itemView, Context context, final OnPhotoClickListener onPhotoClickListener) {
            super(itemView);
            mContext = context;
            mImageView = itemView.findViewById(R.id.iv_photo);
            mBitmapSimpleTarget = new SimpleTarget<Bitmap>(sItemWidth, sItemWidth) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mLoadState = 1;
                    if (mPhoto.getPath().equals(mImageView.getTag(R.id.iv_photo))) {
                        mImageView.setImageBitmap(resource);
                        //只有加载成功了才可以点击
                    }
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    mLoadState = 0;
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    mLoadState = 2;
                }
            };
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPhotoClickListener != null && mPhoto != null && mLoadState == 1) {
                        onPhotoClickListener.onPhotoItemClick(mPhoto);
                    }
                }
            });
        }

        public void bindData(final Photo photo) {
            if (photo != null && !photo.equals(mPhoto)) {
                mPhoto = photo;
                mImageView.setTag(R.id.iv_photo, mPhoto.getPath());
                mImageView.setImageResource(android.R.color.black);
                Glide.with(mContext).load("file:///" + mPhoto.path)
                        .asBitmap()
                        .centerCrop()
                        .crossFade()
                        .into(mBitmapSimpleTarget);
            }
        }

    }

}
