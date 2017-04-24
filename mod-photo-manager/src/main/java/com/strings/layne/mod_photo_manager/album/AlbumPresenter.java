package com.strings.layne.mod_photo_manager.album;

import android.content.Intent;
import android.os.Bundle;

import com.strings.layne.mod_photo_manager.camera.Constant;
import com.strings.layne.mod_photo_manager.method.PhotosAccessor;

import nucleus.presenter.Presenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by laynehuang on 2017/1/4.
 * AlbumPresenter
 */
public class AlbumPresenter extends Presenter<AlbumActivity> {

    private AlbumActivity activity = null;
    private Constant.REQUEST_TYPE_SAMPLE request_type = null;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
    }

    @Override
    protected void onDestroy() {
        activity = null;
        super.onDestroy();
    }

    public void start(AlbumActivity activity, Constant.REQUEST_TYPE_SAMPLE request_type, int maxSelectCount) {
        this.activity = activity;
        this.request_type = request_type;
        switch (request_type) {
            case PHOTO_SEND:
                multiInit(maxSelectCount);
                break;
            case SINGLE_SELECT:
            case COVER:
            case AVATAR:
            case PHOTO_WALL:
                cropInit();
                break;
        }
    }

    /**
     * 多选模式初始化
     *
     * @param maxSelectCount
     */

    private void multiInit(final int maxSelectCount) {

        Observable.just("RxJava")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(s -> {
                    PhotosAccessor.getInstance().init(activity, maxSelectCount);
                    return s;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        refreshView();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String string) {

                    }
                });

    }

    /**
     * 裁剪模式初始化
     */
    private void cropInit() {

        Observable.just("RxJava")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(s -> {
                    PhotosAccessor.getInstance().init(activity);
                    return s;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        refreshRecyclerView();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String string) {

                    }
                });

    }

    public void refreshView() {
        if (activity == null) {
            return;
        }
        activity.setBtnDoneText(PhotosAccessor.getInstance().getSelectedPhotosCount());
        activity.refreshPhotos(PhotosAccessor.getInstance().getPhotos());
        activity.recyclerViewNotifyChanged();
    }

    /**
     * AlbumActivity init & DisplayActivity finish ( AlbumActivity onResume ) to invoke to update UI..
     */

    private void refreshRecyclerView() {
        if (activity == null) {
            return;
        }
        activity.refreshPhotos(PhotosAccessor.getInstance().getPhotos());
        activity.recyclerViewNotifyChanged();
    }

    /**
     * 取消按键的方法，清除所有图片的已选
     */
    public void clearSelected() {
        PhotosAccessor.getInstance().clearSelected();
        Intent intent = new Intent();
        activity.setResult(RESULT_CANCELED, intent);
        activity.finish();
    }

    public void doneSelected() {
        switch (request_type) {
            case PHOTO_SEND:
                final Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.RESULT_MULTI_IMAGES, PhotosAccessor.getInstance().getSelectedPhotos());
                intent.putExtras(bundle);
                activity.setResult(RESULT_OK, intent);
                PhotosAccessor.getInstance().clearSelected();
                activity.finish();
                break;
            case SINGLE_SELECT:
            case COVER:
            case AVATAR:
            case PHOTO_WALL:
                break;
        }
    }
}