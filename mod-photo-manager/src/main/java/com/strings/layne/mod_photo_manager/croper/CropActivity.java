package com.strings.layne.mod_photo_manager.croper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;

import com.strings.layne.mod_photo_manager.R;
import com.strings.layne.mod_photo_manager.R2;
import com.strings.layne.mod_photo_manager.croper.view.CropLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusActivity;

import static com.strings.layne.mod_photo_manager.camera.Constant.ALBUM_CROP;
import static com.strings.layne.mod_photo_manager.camera.Constant.CAMERA_CROP;
import static com.strings.layne.mod_photo_manager.camera.Constant.CROP_FROM;
import static com.strings.layne.mod_photo_manager.camera.Constant.CROP_TYPE;
import static com.strings.layne.mod_photo_manager.camera.Constant.REQUEST_SINGLE_IMAGE_PATH;
import static com.strings.layne.mod_photo_manager.camera.Constant.REQUEST_TYPE_SAMPLE.AVATAR;

/**
 * Created by laynehuang on 2017/1/11.
 * CropActivity
 */

@RequiresPresenter(CropPresenter.class)
public class CropActivity extends NucleusActivity<CropPresenter> {

    @BindView(R2.id.view_crop_edit)
    CropLayout cropLayout;

    @BindView(R2.id.tv_crop_cancel)
    TextView tvCancel;

    @BindView(R2.id.tv_crop_done)
    TextView tvDone;

    CropPresenter presenter = null;
    private String imagePath = null;
    private int cropType = 0;
    private int cropActivity = ALBUM_CROP;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_crop);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra(REQUEST_SINGLE_IMAGE_PATH);
        cropActivity = intent.getIntExtra(CROP_FROM, ALBUM_CROP);
        cropType = intent.getIntExtra(CROP_TYPE, AVATAR.ordinal());

        cropLayout.setCropType(cropType);
        cropLayout.setHorizontalPadding(20);
        cropLayout.setImagePath(this, imagePath);

        if (cropActivity == CAMERA_CROP) {
            tvCancel.setText(R.string.camera_retake);
            tvDone.setText(R.string.camera_use);
        }
        presenter = getPresenter();
        presenter.start(this);
    }

    @Override
    protected void onDestroy() {
        presenter = null;
        super.onDestroy();
    }

    @OnClick(R2.id.tv_crop_cancel)
    void clickCancel() {
        this.setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R2.id.tv_crop_done)
    void clickCrop() {
        presenter.startCrop();
    }

    public Bitmap crop() {
        return cropLayout.crop();
    }
}
