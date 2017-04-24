package com.strings.layne.mod_photo_manager.album;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.strings.layne.mod_photo_manager.R;
import com.strings.layne.mod_photo_manager.R2;
import com.strings.layne.mod_photo_manager.album.adapter.AlbumCropAdapter;
import com.strings.layne.mod_photo_manager.album.adapter.AlbumMultiAdapter;
import com.strings.layne.mod_photo_manager.album.adapter.AlbumSingleAdapter;
import com.strings.layne.mod_photo_manager.album.view.DividerGridItemDecoration;
import com.strings.layne.mod_photo_manager.camera.Constant;
import com.strings.layne.mod_photo_manager.camera.LocalDisplay;
import com.strings.layne.mod_photo_manager.domain.Photo;
import com.strings.layne.mod_photo_manager.method.SimpleDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusActivity;

import static com.strings.layne.mod_photo_manager.camera.Constant.DEFAULT_SELECT_COUNT;

/**
 * AlbumActivity
 * Created by laynehuang on 2017/1/4.
 */


@RequiresPresenter(AlbumPresenter.class)
public class AlbumActivity extends NucleusActivity<AlbumPresenter> {

    private AlbumPresenter presenter = null;
    private int maxSelectCount = 0;
    private boolean isCanDone = false;

    @BindView(R2.id.rv_photos)
    RecyclerView recyclerView;
    @BindView(R2.id.tv_done)
    TextView tvDone;

    private AlbumCropAdapter albumCropAdapter = null;
    private AlbumMultiAdapter albumMultiAdapter = null;
    private AlbumSingleAdapter albumSingleAdapter = null;

    private Constant.REQUEST_TYPE_SAMPLE request_type = Constant.REQUEST_TYPE_SAMPLE.AVATAR;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
        maxSelectCount = getIntent().getIntExtra(Constant.SELECT_COUNT, DEFAULT_SELECT_COUNT);
        int requestType = getIntent().getIntExtra(Constant.REQUEST_TYPE, Constant.REQUEST_TYPE_SAMPLE.AVATAR.ordinal());
        request_type = Constant.REQUEST_TYPE_SAMPLE.values()[requestType];
        LocalDisplay.init(this);
        isCanDone = false;
        viewInit();
        presenter = getPresenter();
        presenter.start(this, request_type, maxSelectCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update selected...
        presenter.refreshView();
    }

    @Override
    protected void onDestroy() {
        presenter = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CROP:
                switch (resultCode) {
                    case RESULT_OK:
                        this.setResult(RESULT_OK, data);
                        this.finish();
                        break;
                    case RESULT_CANCELED:
                        this.setResult(RESULT_CANCELED, data);
                        break;
                }
                break;
            case Constant.REQUEST_MULTI_SEND:
                switch (resultCode) {
                    case RESULT_OK:
                        clickDone();
                        break;
                    case RESULT_CANCELED:
                        break;
                }
        }
    }

    private void viewInit() {
        switch (request_type) {
            case PHOTO_SEND:                // 多选
                albumMultiAdapter = new AlbumMultiAdapter(AlbumActivity.this);
                recyclerViewInit(albumMultiAdapter);
                tvDone.setVisibility(View.VISIBLE);
                setBtnDoneText(0);
                break;
            case SINGLE_SELECT:             // 单选
                albumSingleAdapter = new AlbumSingleAdapter(AlbumActivity.this);
                recyclerViewInit(albumSingleAdapter);
                break;
            case COVER:
            case AVATAR:
            case PHOTO_WALL:
                albumCropAdapter = new AlbumCropAdapter(AlbumActivity.this);
                recyclerViewInit(albumCropAdapter);
                break;
        }
    }

    private void recyclerViewInit(RecyclerView.Adapter adapter) {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        recyclerView.setAdapter(adapter);
    }


    public void recyclerViewNotifyChanged() {
        switch (request_type) {
            case PHOTO_SEND:
                albumMultiAdapter.notifyDataSetChanged();
                break;
            case SINGLE_SELECT:
                albumSingleAdapter.notifyDataSetChanged();
                break;
            case COVER:
            case AVATAR:
            case PHOTO_WALL:
                albumCropAdapter.notifyDataSetChanged();
                break;
        }
    }

    public void refreshPhotos(ArrayList<Photo> photos) {
        switch (request_type) {
            case PHOTO_SEND:
                albumMultiAdapter.refreshPhotos(photos);
                break;
            case SINGLE_SELECT:
                albumSingleAdapter.refreshPhotos(photos);
                break;
            case COVER:
            case AVATAR:
            case PHOTO_WALL:
                albumCropAdapter.refreshPhotos(photos);
                break;
        }
    }

    @OnClick(R2.id.tv_cancel)
    void clickCancel() {
        switch (request_type) {
            case PHOTO_SEND:
                presenter.clearSelected();
                break;
            case SINGLE_SELECT:
            case COVER:
            case AVATAR:
            case PHOTO_WALL:
                this.finish();
                break;
        }
    }

    @OnClick(R2.id.tv_done)
    void clickDone() {
        if (!isCanDone) {
            return;
        }
        switch (request_type) {
            case PHOTO_SEND:
                presenter.doneSelected();
                break;
            case SINGLE_SELECT:
            case COVER:
            case AVATAR:
            case PHOTO_WALL:
                break;
        }
    }

    public void setBtnDoneText(int count) {
        if (request_type != Constant.REQUEST_TYPE_SAMPLE.PHOTO_SEND) {
            return;
        }

        if (count > 0) {
            isCanDone = true;
            tvDone.setText(String.format(getString(R.string.album_sends), count));
            tvDone.setTextColor(ContextCompat.getColor(this, R.color.colorActionOptAble));
        } else if (count == 0) {
            isCanDone = false;
            tvDone.setText(R.string.camera_send);
            tvDone.setTextColor(ContextCompat.getColor(this, R.color.colorActionOptDisable));
        }
    }

    public void selectOverHint() {
        if (request_type != Constant.REQUEST_TYPE_SAMPLE.PHOTO_SEND) {
            return;
        }
        String content = String.format(getString(R.string.album_max_sends), maxSelectCount);
        SimpleDialog simpleDialog = new SimpleDialog(this);
        simpleDialog.setContent(content);
        simpleDialog.setContentColor(Color.parseColor("#030303"));
        simpleDialog.addButton(getString(R.string.hint_i_know), Color.parseColor("#414141"));
        simpleDialog.setCancelable(false);
        simpleDialog.setOnButtonClickListener((self, buttonIndex, buttonString) -> self.dismiss());
        simpleDialog.show();
    }

    public Constant.REQUEST_TYPE_SAMPLE getREQUEST_TYPE() {
        return request_type;
    }

}
