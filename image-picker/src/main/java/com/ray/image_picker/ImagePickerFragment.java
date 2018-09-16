package com.ray.image_picker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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
import android.widget.TextView;

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
    private PhotoAdapter mPhotoAdapter;
    private AlbumAdapter mAlbumAdapter;
    private ObjectAnimator mAlbumOpenAnim;
    private ObjectAnimator mAlbumCloseAnim;
    private boolean mAnimStarted;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {

    }


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
        final TextView tvAlbum = view.findViewById(R.id.tv_album);
        recyclerAlbum.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAlbumAdapter = new AlbumAdapter(getActivity());
        recyclerAlbum.setAdapter(mAlbumAdapter);
        albumLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (albumLayout.getHeight() > 0) {
                    albumLayout.setTranslationY(albumLayout.getHeight());
                    //set tag to close
                    albumLayout.setTag(R.id.layout_album, false);
                    mAlbumOpenAnim = ObjectAnimator.ofFloat(albumLayout, "translationY", albumLayout.getHeight(), 0);
                    mAlbumOpenAnim.setDuration(400);
                    AnimatorListenerAdapter animatorOpenListener = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            mAnimStarted = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAnimStarted = false;
                            albumLayout.setTag(R.id.layout_album, true);
                        }
                    };
                    AnimatorListenerAdapter animatorCloseListener = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            mAnimStarted = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAnimStarted = false;
                            albumLayout.setTag(R.id.layout_album, false);
                        }
                    };
                    mAlbumOpenAnim.addListener(animatorOpenListener);
                    mAlbumCloseAnim = ObjectAnimator.ofFloat(albumLayout, "translationY", 0, albumLayout.getHeight());
                    mAlbumCloseAnim.setDuration(400);
                    mAlbumCloseAnim.addListener(animatorCloseListener);
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
                if (mAnimStarted)
                    return;
                Object tag = albumLayout.getTag(R.id.layout_album);
                if (tag != null && tag instanceof  Boolean) {
                    if ((boolean)tag) {
                        if (mAlbumCloseAnim != null) {
                            mAlbumCloseAnim.start();
                        }
                    } else {
                        if (mAlbumOpenAnim != null) {
                            mAlbumOpenAnim.start();
                        }
                    }
                }
            }
        });
        mAlbumAdapter.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onAlbumClick(Album album) {
                tvAlbum.setText(album.getName());
                loadImages(album.getId());
            }
        });
    }

    private void loadImages(String id) {
        MediaLoader.getInstance(getActivity()).getPhotosByBucktIdObservable(id).subscribe(new Observer<List<Photo>>() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAlbumOpenAnim != null) {
            mAlbumOpenAnim.end();
            mAlbumOpenAnim = null;
        }
        if (mAlbumCloseAnim != null) {
            mAlbumCloseAnim.end();
            mAlbumCloseAnim = null;
        }
    }
}
