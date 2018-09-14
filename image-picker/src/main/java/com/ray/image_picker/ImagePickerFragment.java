package com.ray.image_picker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.ray.image_picker.adapter.AlbumAdapter;
import com.ray.image_picker.adapter.PhotoAdapter;
import com.ray.image_picker.bean.Album;
import com.ray.image_picker.bean.Photo;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-12 11:15
 *  description : 
 */
public class ImagePickerFragment extends Fragment {

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {

    }

    private PhotoAdapter mPhotoAdapter;
    private AlbumAdapter mAlbumAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerPicker = view.findViewById(R.id.recycler_picker);
        final RecyclerView recyclerAlbum = view.findViewById(R.id.recycler_album);
        initAlbumLayout(view, recyclerAlbum);
        initPhotosLayout(recyclerPicker);
    }

    private void initPhotosLayout(RecyclerView recyclerPicker) {
        recyclerPicker.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerPicker.addItemDecoration(new PhotoItemDecoration());
        mPhotoAdapter = new PhotoAdapter(getActivity(), 4);
        recyclerPicker.setAdapter(mPhotoAdapter);
    }

    private void initAlbumLayout(@NonNull View view, RecyclerView recyclerAlbum) {
        final View albumLayout = view.findViewById(R.id.layout_album);
        recyclerAlbum.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAlbumAdapter = new AlbumAdapter(getActivity());
        recyclerAlbum.setAdapter(mAlbumAdapter);
        albumLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (albumLayout.getHeight() > 0) {
                        albumLayout.setTranslationY(albumLayout.getHeight());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        albumLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        albumLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        view.findViewById(R.id.ll_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumLayout.setTranslationY(albumLayout.getTranslationY() == 0 ? albumLayout.getHeight() : 0 );
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClickListener) {
            mOnClickListener = (OnClickListener) context;
        }
    }

    private void loadData() {
        MediaLoader.getInstance(getActivity()).getAllPhotoObservable().subscribe(new Observer<List<Photo>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Photo> photos) {
                mPhotoAdapter.setData(photos);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        MediaLoader.getInstance(getActivity()).getAlbumObservable().subscribe(new Observer<List<Album>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Album> albums) {
                mAlbumAdapter.setData(albums);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

}
