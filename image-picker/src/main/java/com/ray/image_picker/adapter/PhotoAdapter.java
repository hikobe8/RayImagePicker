package com.ray.image_picker.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ray.image_picker.R;
import com.ray.image_picker.bean.Photo;

import java.util.ArrayList;
import java.util.List;

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-12 17:10
 *  description : 
 */
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Photo> mPhotos = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PhotoHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof PhotoHolder) {
            ((PhotoHolder)viewHolder).bindData(mPhotos.get(i));
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

        private Photo mPhoto;
        private ImageView mImageView;

        PhotoHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_photo);
        }

        public void bindData(Photo photo) {
            if (photo != null && !photo.equals(mPhoto)) {
                Glide.with(itemView.getContext()).load("file:///" + photo.path).asBitmap().into(mImageView);
                mPhoto = photo;
            }
        }
    }

}
