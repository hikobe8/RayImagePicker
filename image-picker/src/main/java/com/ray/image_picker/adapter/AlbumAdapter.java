package com.ray.image_picker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ray.image_picker.R;
import com.ray.image_picker.bean.Album;

import java.util.ArrayList;
import java.util.List;

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-14 17:08
 *  description : 
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Album> mAlbumList;

    public AlbumAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mAlbumList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AlbumHolder(mLayoutInflater.inflate(R.layout.item_album, viewGroup, false), mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder albumHolder, int i) {
        albumHolder.bindData(mAlbumList.get(i));
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    public void setData(List<Album> albums) {
        mAlbumList.clear();
        mAlbumList.addAll(albums);
        notifyDataSetChanged();
    }

    static class AlbumHolder extends RecyclerView.ViewHolder{

        ImageView mIvCover;
        TextView mTvAlbum;
        Context mContext;

        AlbumHolder(@NonNull final View itemView, Context context) {
            super(itemView);
            mContext = context;
            mIvCover = itemView.findViewById(R.id.iv_album);
            mTvAlbum = itemView.findViewById(R.id.tv_album);
        }

        void bindData(Album album) {
            Glide.with(mContext).load("file:///" + album.getCoverPath())
                    .asBitmap()
                    .centerCrop()
                    .crossFade()
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mIvCover.setImageBitmap(resource);
                        }
                    });
            mTvAlbum.setText(album.getName());
        }
    }

}
