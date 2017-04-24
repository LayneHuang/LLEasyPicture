package com.strings.layne.mod_photo_manager.method;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.strings.layne.mod_photo_manager.domain.Photo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by laynehuang on 2017/1/4.
 * PhotosAccessor
 */

public class PhotosAccessor {

    private ArrayList<Photo> photos = new ArrayList<>();
    private HashMap<String, Integer> degreeMap = new HashMap<>();
    private HashMap<Integer, Boolean> selectedPhotosIndex = new HashMap<>();

    private Cursor cursor = null;
    private boolean Initialed = false;                      // 是否初始化
    private int selectedPhotosCount = 0;
    private int maxSelectCount = 0;

    private PhotosAccessor() {
    }

    private static class SingletonHolder {
        private static final PhotosAccessor INSTANCE = new PhotosAccessor();
    }

    public static PhotosAccessor getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Context context, int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
        init(context);
    }

    public void init(Context context) {

        if (Initialed) {
            return;
        }
        Initialed = true;
        selectedPhotosCount = 0;
        Uri albumUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        cursor = cr.query(albumUri, null, null, null, MediaStore.Images.Media.DATA + " DESC");
        if (cursor == null) {
            Initialed = false;
            return;
        }
        getPhotosFromLocal();
        initPhotosDegree();
    }

    public int getDegree(String imagePath) {
        if (!degreeMap.containsKey(imagePath)) return 0;
        return degreeMap.get(imagePath);
    }

    private boolean getPhotosFromLocal() {

        if (cursor == null || cursor.isClosed()) {
            return false;
        }
        cursor.moveToFirst();
        photos.clear();

        int totalPhotosCount = cursor.getCount();
        if (totalPhotosCount > 0) {
            for (int i = 0; i < totalPhotosCount; ++i) {
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Photo photo = new Photo();
                photo.setLocalUrl(url);
                photo.setSelected(false);
                photo.setOriginal(false);
                photo.setDegree(0);
                photos.add(photo);
                if (!cursor.moveToNext()) {
                    cursor.close();
                    break;
                }
            }
        }
        return true;
    }

    private void initPhotosDegree() {
        Observable.from(photos)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Photo>() {
                    @Override
                    public void onCompleted() {


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Photo photo) {
                        File f = new File(photo.getLocalUrl());
                        if (f.exists() && !f.isDirectory()) {
                            int degree = readDegree(f.getAbsolutePath());
                            if (degree != 0) {
                                degreeMap.put(f.getAbsolutePath(), degree);
                            }
                            photo.setDegree(degree);
                        }
                    }
                });
    }

    public boolean isInitialed() {
        return Initialed;
    }

    public boolean isEmpty() {
        return photos.isEmpty();
    }

    private int readDegree(String absolutePath) {
        int degree = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(absolutePath);
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (orientation >= 360) orientation %= 360;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    /**
     * set single photo selected or not
     *
     * @return :
     * true ( set Success )
     * false( selected photos over max selected count )
     */
    public boolean setSelected(int position, boolean b) {

        if (photos.get(position).isSelected() == b) {
            return true;
        }

        if (b) {
            if (selectedPhotosCount < maxSelectCount) {
                selectedPhotosCount++;
            } else {
                return false;
            }
        } else {
            if (selectedPhotosCount > 0) {
                selectedPhotosCount--;
            } else {
                return false;
            }
        }
        photos.get(position).setSelected(b);
        if (b) {
            selectedPhotosIndex.put(position, true);
        } else {
            if (selectedPhotosIndex.containsKey(position)) {
                selectedPhotosIndex.remove(position);
            }
        }
        return true;
    }

    public void setOriginal(int position, boolean b) {
        if (photos.size() < position) {
            return;
        }
        photos.get(position).setOriginal(b);
    }

    /**
     * check photo selected or not
     */
    public Boolean isSelected(int position) {
        return photos.get(position).isSelected();
    }

    public Boolean isOriginal(int position) {
        return photos.get(position).isOriginal();
    }

    public void clearSelected() {
        for (Object o : selectedPhotosIndex.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Integer key = (Integer) entry.getKey();
            photos.get(key).setSelected(false);
            photos.get(key).setOriginal(false);
        }
        selectedPhotosIndex.clear();
        selectedPhotosCount = 0;
    }

    public ArrayList<Photo> getSelectedPhotos() {
        ArrayList<Photo> result = new ArrayList<>();
        if (!Initialed) return result;
        result.clear();
        for (Object o : selectedPhotosIndex.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Integer key = (Integer) entry.getKey();
            result.add(photos.get(key));
        }
        return result;
    }

    public int getTotalPhotosCount() {
        return photos.size();
    }

    public int getSelectedPhotosCount() {
        return selectedPhotosCount;
    }

    public int getMaxSelectCount() {
        return this.maxSelectCount;
    }
}
