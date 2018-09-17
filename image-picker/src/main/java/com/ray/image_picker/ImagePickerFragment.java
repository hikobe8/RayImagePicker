package com.ray.image_picker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.ray.image_picker.adapter.AlbumAdapter;
import com.ray.image_picker.adapter.PhotoAdapter;
import com.ray.image_picker.bean.Album;
import com.ray.image_picker.bean.Photo;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
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
    private ValueAnimator mOpenValueAnim;
    private ValueAnimator mCloseValueAnim;
    private boolean mAnimStarted;
    private CompositeDisposable mCompositeDisposable;
    private View mIvArrow;
    private View mAlbumLayout;
    private boolean mIntercept;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();
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
        mIvArrow = view.findViewById(R.id.iv_arrow);
    }

    private void initPhotosLayout(RecyclerView recyclerPicker) {
        recyclerPicker.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerPicker.addItemDecoration(new PhotoItemDecoration());
        mPhotoAdapter = new PhotoAdapter(getActivity(), 4);
        mPhotoAdapter.setOnPhotoClickListener(new PhotoAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoItemClick(Photo photo) {
                Toast.makeText(getActivity(), photo.getPath(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerPicker.setAdapter(mPhotoAdapter);
    }

    private void initAlbumLayout(@NonNull final View view, RecyclerView recyclerAlbum) {
        mAlbumLayout = view.findViewById(R.id.layout_album);
        final TextView tvAlbum = view.findViewById(R.id.tv_album);
        final View maskView = view.findViewById(R.id.layout_mask);
        maskView.setAlpha(0);
        maskView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeAlbum();
                return mIntercept;
            }
        });
        recyclerAlbum.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAlbumAdapter = new AlbumAdapter(getActivity());
        recyclerAlbum.setAdapter(mAlbumAdapter);
        mAlbumLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mAlbumLayout.getHeight() > 0) {
                    mAlbumLayout.setTranslationY(mAlbumLayout.getHeight());
                    //set tag to close
                    mAlbumLayout.setTag(R.id.layout_album, false);
                    //open
                    AnimatorListenerAdapter animatorOpenListener = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            mAnimStarted = true;
                            mIntercept = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAnimStarted = false;
                            mAlbumLayout.setTag(R.id.layout_album, true);
                        }
                    };
                    mOpenValueAnim = ValueAnimator.ofFloat(mAlbumLayout.getHeight(), 0);
                    mOpenValueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mAlbumLayout.setTranslationY((Float) animation.getAnimatedValue());
                            maskView.setAlpha((1 - mAlbumLayout.getTranslationY()/ mAlbumLayout.getHeight()) * 1f);
                            mIvArrow.setRotation(animation.getAnimatedFraction() * 180);
                        }
                    });
                    mOpenValueAnim.setDuration(400);
                    mOpenValueAnim.addListener(animatorOpenListener);

                    //close
                    AnimatorListenerAdapter animatorCloseListener = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            mAnimStarted = true;
                            mIntercept = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAnimStarted = false;
                            mIntercept = false;
                            mAlbumLayout.setTag(R.id.layout_album, false);
                        }
                    };
                    mCloseValueAnim = ValueAnimator.ofFloat(0, mAlbumLayout.getHeight());
                    mCloseValueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mAlbumLayout.setTranslationY((Float) animation.getAnimatedValue());
                            maskView.setAlpha((1 - animation.getAnimatedFraction()) * 1f);
                            mIvArrow.setRotation(180 + animation.getAnimatedFraction() * 180);
                        }
                    });
                    mCloseValueAnim.setDuration(400);
                    mCloseValueAnim.addListener(animatorCloseListener);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mAlbumLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mAlbumLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        view.findViewById(R.id.ll_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnimStarted)
                    return;
                Object tag = mAlbumLayout.getTag(R.id.layout_album);
                if (tag != null && tag instanceof  Boolean) {
                    if ((boolean)tag) {
                        if (mCloseValueAnim != null) {
                            mCloseValueAnim.start();
                        }
                    } else {
                        if (mOpenValueAnim != null) {
                            mOpenValueAnim.start();
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
                closeAlbum();
            }
        });
    }

    private void closeAlbum() {
        if (mAnimStarted)
            return;
        Object tag = mAlbumLayout.getTag(R.id.layout_album);
        if (tag != null && tag instanceof  Boolean) {
            if ((boolean)tag) {
                if (mCloseValueAnim != null) {
                    mCloseValueAnim.start();
                }
            }
        }
    }

    private void loadImages(String id) {
        MediaLoader.getInstance(getActivity()).getPhotosByBucktIdObservable(id).subscribe(new Observer<List<Photo>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
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
                mCompositeDisposable.add(d);
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
                mCompositeDisposable.add(d);
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
        if (mOpenValueAnim != null) {
            mOpenValueAnim.end();
            mOpenValueAnim = null;
        }
        if (mCloseValueAnim != null) {
            mCloseValueAnim.end();
            mCloseValueAnim = null;
        }
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }
}
