package com.strings.layne.mod_photo_manager.croper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.strings.layne.mod_photo_manager.camera.Constant;
import com.strings.layne.mod_photo_manager.method.PhotoSaveService;

import java.io.File;
import java.io.IOException;

import nucleus.presenter.Presenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by laynehuang on 2017/1/9.
 * CropPresenter
 */

public class CropPresenter extends Presenter<CropActivity> {

    private CropActivity activity = null;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
    }

    @Override
    protected void onDestroy() {
        activity = null;
        super.onDestroy();
    }

    public void start(CropActivity activity) {
        this.activity = activity;
    }

    public void startCrop() {
        Observable.just(activity.getCacheDir())
                .observeOn(Schedulers.io())
                .map(rootDir -> new File(rootDir, System.currentTimeMillis() + ".jpg"))
                .map(file -> {
                    Bitmap bitmap = activity.crop();
                    if (bitmap != null) {
                        PhotoSaveService photoSaveService = new PhotoSaveService(bitmap, file);
                        try {
                            photoSaveService.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return file;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(File file) {
                        Intent intent = new Intent();
                        intent.putExtra(Constant.RESULT_SINGLE_IMAGE_PATH, file.getAbsolutePath());
                        activity.setResult(RESULT_OK, intent);
                        activity.finish();
                    }
                });
    }
}
