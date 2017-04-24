package com.strings.layne.mod_photo_manager.album;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.strings.layne.mod_photo_manager.method.PhotosAccessor;

import nucleus.presenter.Presenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * DisplayPresenter
 * Created by laynehuang on 2017/1/4.
 */

public class DisplayPresenter extends Presenter<DisplayActivity> {

    private static final String TAG = "DisplayPresenter";
    private DisplayActivity activity;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
    }

    public void start(DisplayActivity activity, int beginPosition) {
        this.activity = activity;
        // update UI...
        Log.w(TAG, "isInitialed : " + PhotosAccessor.getInstance().isInitialed());
        if (!PhotosAccessor.getInstance().isInitialed()) {
            init(beginPosition);
        } else {
            activity.viewPagerInit();
            setUISelected(beginPosition);
            setUIOriginal(beginPosition);
            setTitle(beginPosition);
            setBtnDone();

        }
    }

    private void init(final int beginPosition) {
        Observable.just("RxJava")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(s -> {
                    PhotosAccessor.getInstance().init(activity, 9);
                    return s;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        activity.viewPagerInit();
                        setUISelected(beginPosition);
                        setTitle(beginPosition);
                        setBtnDone();
                        Log.d(TAG, "init finish..");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String string) {

                    }
                });
    }

    public void setUISelected(final int position) {
        activity.refreshCheckBox(PhotosAccessor.getInstance().isSelected(position));
    }

    public void setUIOriginal(final int position) {
        activity.refreshOriginalMv(PhotosAccessor.getInstance().isOriginal(position));
    }

    public void setTitle(final int position) {
        // 位置是下标+1
        activity.refreshTitle(position + 1, PhotosAccessor.getInstance().getTotalPhotosCount());
    }

    public void setBtnDone() {
        activity.refreshBtnDone(PhotosAccessor.getInstance().getSelectedPhotosCount());
    }

    public void setDBSelected(final int position, final boolean selected) {
        if (!PhotosAccessor.getInstance().setSelected(position, selected)) {
            activity.selectOverHint();
        }
    }

    public void setDBOriginal(final int position, final boolean selected) {
        if (!PhotosAccessor.getInstance().setSelected(position, selected)) {
            activity.selectOverHint();
        }
    }


    public void doneSelected(boolean isDone) {
        if (isDone) {
            activity.setResult(Activity.RESULT_OK);
        } else {
            activity.setResult(Activity.RESULT_CANCELED);
        }
        activity.finish();
    }

}