package com.strings.layne.mod_photo_manager.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.strings.layne.mod_photo_manager.R;
import com.strings.layne.mod_photo_manager.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.strings.layne.mod_photo_manager.camera.Constant.REQUEST_SINGLE_IMAGE_PATH;
import static com.strings.layne.mod_photo_manager.camera.Constant.RESULT_SINGLE_IMAGE_PATH;

/**
 * Created by laynehuang on 2017/1/18.
 * CameraDisplayActivity
 */
public class CameraDisplayActivity extends Activity {

    @BindView(R2.id.mv_photo)
    ImageView imageView;

    private String path;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_camera_display);
        ButterKnife.bind(this);
        path = getIntent().getStringExtra(REQUEST_SINGLE_IMAGE_PATH);
        Glide.with(this).load(path).asBitmap().into(imageView);
    }

    @OnClick(R2.id.tv_done)
    void clickDone() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_SINGLE_IMAGE_PATH, path);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R2.id.tv_cancel)
    void clickCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

}
