package com.ray.image_picker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.ray.image_picker.bean.Album;
import com.ray.image_picker.bean.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-12 15:46
 *  description : 
 */
public class MediaLoader {

    private List<String> mBucketIds = new ArrayList<>();
    private ContentResolver mContentResolver;
    private static MediaLoader sInstance;

    private MediaLoader(Context context){
        mContentResolver = context.getApplicationContext().getContentResolver();
    }

    public static MediaLoader getInstance(Context context) {
        if (sInstance == null) {
            synchronized (MediaLoader.class) {
                if (sInstance == null) {
                    sInstance = new MediaLoader(context);
                }
            }
        }
        return sInstance;
    }

    public List<Album> getAlbum() {
        mBucketIds.clear();
        List<Album> data = new ArrayList<>();
        String projects[] = new String[]{
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , projects
                , null
                , null
                , MediaStore.Images.Media.DATE_MODIFIED);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Album album = new Album();

                String buckedId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));

                if (mBucketIds.contains(buckedId)) continue;

                mBucketIds.add(buckedId);

                String buckedName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String coverPath = getFrontCoverData(buckedId);

                album.setId(buckedId);
                album.setName(buckedName);
                album.setCoverPath(coverPath);

                data.add(album);


            } while (cursor.moveToNext());

            cursor.close();
        }

        return data;

    }

    public List<Photo> getPhoto(String buckedId) {
        List<Photo> photos = new ArrayList<>();

        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.DATE_MODIFIED}
                , MediaStore.Images.Media.BUCKET_ID + "=?"
                , new String[]{buckedId}
                , MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Long dataAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                Long dataModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));

                Photo photo = new Photo(path, dataAdded, dataModified);

                photos.add(photo);

            } while (cursor.moveToNext());
            cursor.close();
        }

        return photos;
    }


    private String getFrontCoverData(String bucketId) {
        String path = "empty";
        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, MediaStore.Images.Media.BUCKET_ID + "=?", new String[]{bucketId}, MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor != null && cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }


    public List<Photo> getAllPhoto() {
        List<Photo> photos = new ArrayList<>();

        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.DATE_MODIFIED}
                , null
                , null
                , MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Long dataAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                Long dataModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));


                Photo photo = new Photo(path, dataAdded, dataModified);

                photos.add(photo);

            } while (cursor.moveToNext());
            cursor.close();
        }

        Collections.sort(photos, new Comparator<Photo>() {
            @Override
            public int compare(Photo lhs, Photo rhs) {
                long l = lhs.getDataModified();
                long r = rhs.getDataModified();
                return (int) (r - l);
            }
        });

        return photos;
    }

    public Observable<List<Photo>> getAllPhotoObservable(){
        return Observable.create(new ObservableOnSubscribe<List<Photo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Photo>> emitter) throws Exception {
                List<Photo> allPhoto = getAllPhoto();
                emitter.onNext(allPhoto);
                emitter.onComplete();
            }
        });
    }

    public Observable<List<Album>> getAlbumObservable(){
        return Observable.create(new ObservableOnSubscribe<List<Album>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Album>> emitter) throws Exception {
                List<Album> albums = getAlbum();
                emitter.onNext(albums);
                emitter.onComplete();
            }
        });
    }

    public Observable<List<Photo>> getPhotosByBucktIdObservable(final String bucketId) {
        return Observable.create(new ObservableOnSubscribe<List<Photo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Photo>> emitter) throws Exception {
                List<Photo> photos = getPhoto(bucketId);
                emitter.onNext(photos);
                emitter.onComplete();
            }
        });
    }

}
