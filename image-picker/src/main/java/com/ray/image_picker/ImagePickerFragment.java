package com.ray.image_picker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ray.image_picker.adapter.PhotoAdapter;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_picker,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerPicker = view.findViewById(R.id.recycler_picker);
        recyclerPicker.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerPicker.addItemDecoration(new PhotoItemDercoration());
        mPhotoAdapter = new PhotoAdapter();
        recyclerPicker.setAdapter(mPhotoAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof  OnClickListener) {
            mOnClickListener = (OnClickListener) context;
        }
    }

    private void loadData() {
        MediaLoader.getInstance(getContext()).getAllPhotoObservable().subscribe(new Observer<List<Photo>>() {
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

}
