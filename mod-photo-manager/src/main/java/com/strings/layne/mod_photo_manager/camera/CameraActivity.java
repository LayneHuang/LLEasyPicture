package com.strings.layne.mod_photo_manager.camera;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.strings.layne.mod_photo_manager.R;
import com.strings.layne.mod_photo_manager.R2;
import com.strings.layne.mod_photo_manager.croper.CropActivity;
import com.strings.layne.mod_photo_manager.method.AnimatorSetUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.strings.layne.mod_photo_manager.camera.CameraUtil.FLASH_LIGHT_AUTO;
import static com.strings.layne.mod_photo_manager.camera.CameraUtil.FLASH_LIGHT_OFF;
import static com.strings.layne.mod_photo_manager.camera.CameraUtil.FLASH_LIGHT_ON;
import static com.strings.layne.mod_photo_manager.camera.Constant.REQUEST_CROP;
import static com.strings.layne.mod_photo_manager.camera.Constant.REQUEST_SINGLE_SEND;

/**
 * Created by laynehuang on 2017/1/13.
 * CameraActivity
 */

public class CameraActivity extends AppCompatActivity {

    com.strings.layne.mod_photo_manager.camera.Constant.REQUEST_TYPE_SAMPLE request_type = Constant.REQUEST_TYPE_SAMPLE.AVATAR;

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "CameraActivity";
    /**
     * A requestCode for display picture.
     */

    private boolean isFlashAnimating = false;

    private boolean isOverTurning = false;

    @BindView(R2.id.texture)
    AutoFitTextureView textureView;

    @BindView(R2.id.layout_control_flash)
    LinearLayout mLinearLayout;

    @BindView(R2.id.mv_flash)
    ImageView mvFlash;

    @BindView(R2.id.tv_flash_on)
    TextView tvFlashOn;

    @BindView(R2.id.tv_flash_off)
    TextView tvFlashOff;

    @BindView(R2.id.tv_flash_auto)
    TextView tvFlashAuto;

    private MyCamera myCamera = null;

    private IOrientationEventListener mIOrientationEventListener = null;
    private int deviceOrientation = 0;
    boolean isFront = false;

    @Override
    protected void onCreate(Bundle savedState) {

        super.onCreate(savedState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        mIOrientationEventListener = new IOrientationEventListener(this);
        mIOrientationEventListener.enable();

        setResult(Activity.RESULT_CANCELED);

        int requestType = getIntent()
                .getIntExtra(Constant.REQUEST_TYPE, Constant.REQUEST_TYPE_SAMPLE.AVATAR.ordinal());
        request_type = Constant.REQUEST_TYPE_SAMPLE.values()[requestType];

        CameraUtil.getInstance().init(this);
        versionSelect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (textureView.isAvailable()) {
            Log.i(TAG, "hl resume and create new camera");
            if (myCamera == null) {
                myCamera = new MyCamera(CameraActivity.this, isFront, textureWidth, textureHeight);
            }
            myCamera.open();
        } else {
            Log.i(TAG, "hl resume set listener and create new camera");
            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIOrientationEventListener != null) {
            mIOrientationEventListener.disable();
            mIOrientationEventListener = null;
        }
        if (myCamera != null) {
            myCamera.close();
            myCamera = null;
        }
    }

    private void versionSelect() {

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            // your code using Camera API here - is between 1-20
////            Timber.i("hl Phone version below 5.0");
//            isCamera2 = false;
//
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // your code using Camera2 API here - is api 21 or higher
////            Timber.i("hl Phone version upper 5.0");
//            isCamera2 = true;
//        }
        switch (request_type) {
            case AVATAR: {
                isFront = true;
                mvFlash.setVisibility(View.INVISIBLE);
            }
            break;
            case COVER:
            case PHOTO_WALL:
            case PHOTO_SEND: {
                isFront = false;
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CROP: {
//                Timber.i("hl crop finish");
                if (resultCode == RESULT_OK) {
//                    Timber.i("hl crop result ok");
                    setResult(RESULT_OK, data);
                    finish();
                } else if (resultCode == RESULT_CANCELED) {
//                    Timber.i("hl crop result cancel");
                }
                break;
            }
            case REQUEST_SINGLE_SEND: {
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
            }
        }
    }

    public class IOrientationEventListener extends OrientationEventListener {

        public IOrientationEventListener(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onOrientationChanged(int orientation) {
            // TODO Auto-generated method stub
            if (ORIENTATION_UNKNOWN == orientation) {
                return;
            }
            deviceOrientation = ((orientation + 45) / 90 * 90) % 360;
        }

    }

    public int getOrientation() {
        return deviceOrientation;
    }

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */

    private int textureWidth = 0;
    private int textureHeight = 0;

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            textureWidth = width;
            textureHeight = height;
            Log.d(TAG, "hl " + width + " " + height);
            if (myCamera == null) {
                myCamera = new MyCamera(CameraActivity.this, isFront, width, height);
            }
            myCamera.setViewWidth(width);
            myCamera.setViewHeight(height);
            myCamera.open();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            Log.d(TAG, "hl texture is change");
            Log.d(TAG, "hl w , h " + width + " " + height);
            textureWidth = width;
            textureHeight = height;
            if (myCamera == null) {
                myCamera = new MyCamera(CameraActivity.this, isFront, width, height);
            }
            myCamera.setViewWidth(width);
            myCamera.setViewHeight(height);
            myCamera.open();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            if (myCamera != null) {
                myCamera.close();
                myCamera = null;
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    public void setTextureViewAspectRatio(int previewWidth, int previewHeight) {
        textureView.setAspectRatio(previewWidth, previewHeight);
        // 当高度不足以铺满屏幕的时候需要用到
        int[] location = new int[2];
        textureView.getLocationInWindow(location);
        int emptyAreaHeight = LocalDisplay.SCREEN_HEIGHT_PIXELS - location[1] - LocalDisplay.dp2px(135) - previewHeight;

        if (emptyAreaHeight > 0) {
            textureViewMarginTop((emptyAreaHeight + 1) / 2);
        }
        textureView.invalidate();
    }

    private void textureViewMarginTop(int distance) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textureView.getLayoutParams();
        lp.topMargin = distance;
        textureView.setLayoutParams(lp);
    }

    /**
     * Listener of UI button.
     */

    @OnClick(R2.id.mv_take_picture)
    void clickTakePicture() {
        if (myCamera != null) {
            myCamera.takePicture();
        }
    }

    @OnClick(R2.id.mv_camera_cancel)
    void clickCancel() {
        finish();
    }

    @OnClick(R2.id.mv_camera_overturn)
    void clickOverturn() {

        if (isOverTurning) {
            return;
        }
        isOverTurning = true;

        isFront = !isFront;

        if (myCamera != null) {
            myCamera.close();
            myCamera = null;
        }

        myCamera = new MyCamera(this, isFront, textureWidth, textureHeight);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFront) {
                    mvFlash.setVisibility(View.INVISIBLE);
                    mLinearLayout.setVisibility(View.INVISIBLE);
                } else {
                    mvFlash.setVisibility(View.VISIBLE);
                }
                if (textureView.isAvailable()) {
                    myCamera.open();
                } else {
                    textureView.setSurfaceTextureListener(mSurfaceTextureListener);
                }
                isOverTurning = false;

            }
        }, 100);
    }


    @OnClick(R2.id.mv_flash)
    void clickFlashIcon() {

        if (isFlashAnimating) return;
        mLinearLayout.setPivotX(0.f);
        if (mLinearLayout.getVisibility() == View.VISIBLE) {
            isFlashAnimating = true;
            new AnimatorSetUtil().refresh().setDuration(300)
                    .play(ObjectAnimator.ofFloat(mLinearLayout, "scaleX", 1.f, 0.f))
                    .setOnAnimationEndListener(() -> {
                        isFlashAnimating = false;
                        mLinearLayout.setVisibility(View.GONE);
                    })
                    .start();
        } else {
            mLinearLayout.setVisibility(View.VISIBLE);
            isFlashAnimating = true;
            new AnimatorSetUtil().refresh().setDuration(300)
                    .play(ObjectAnimator.ofFloat(mLinearLayout, "scaleX", 0.f, 1.f))
                    .setOnAnimationEndListener(() -> isFlashAnimating = false)
                    .start();
        }
    }

    @OnClick(R2.id.tv_flash_on)
    void clickFlashLightOn() {
        // 前置不需要闪光灯
        if (isFront) {
            return;
        }
        if (myCamera.isFlashSupported()) {
            myCamera.setFlashLightState(FLASH_LIGHT_ON);
            tvFlashOff.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvFlashAuto.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvFlashOn.setTextColor(ContextCompat.getColor(this, R.color.yellow));
            mvFlash.setImageResource(R.drawable.ic_camera_flash_on);
            clickFlashIcon();
        }
    }

    @OnClick(R2.id.tv_flash_off)
    void clickFlashLightOff() {
        // 前置不需要闪光灯
        if (isFront) {
            return;
        }
        if (myCamera.isFlashSupported()) {
            myCamera.setFlashLightState(FLASH_LIGHT_OFF);
            tvFlashOff.setTextColor(ContextCompat.getColor(this, R.color.yellow));
            tvFlashAuto.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvFlashOn.setTextColor(ContextCompat.getColor(this, R.color.white));
            mvFlash.setImageResource(R.drawable.ic_camera_flash_off);
            clickFlashIcon();
        }
    }

    @OnClick(R2.id.tv_flash_auto)
    void clickFlashLightAuto() {
        // 前置不需要闪光灯
        if (isFront) {
            return;
        }
        if (myCamera.isFlashSupported()) {
            myCamera.setFlashLightState(FLASH_LIGHT_AUTO);
            tvFlashOff.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvFlashAuto.setTextColor(ContextCompat.getColor(this, R.color.yellow));
            tvFlashOn.setTextColor(ContextCompat.getColor(this, R.color.white));
            mvFlash.setImageResource(R.drawable.ic_camera_flash_off);
            mvFlash.setImageResource(R.drawable.ic_camera_flash_auto);
            clickFlashIcon();
        }
    }

    public void dealResult(String path) {
        switch (request_type) {
            case COVER: {
                Intent intent = new Intent(this, CropActivity.class);
                intent.putExtra(Constant.CROP_TYPE, Constant.REQUEST_TYPE_SAMPLE.COVER.ordinal());
                intent.putExtra(Constant.CROP_FROM, Constant.CAMERA_CROP);
                intent.putExtra(Constant.REQUEST_SINGLE_IMAGE_PATH, path);
                startActivityForResult(intent, REQUEST_CROP);
                break;
            }
            case AVATAR: {
                Intent intent = new Intent(this, CropActivity.class);
                intent.putExtra(Constant.CROP_TYPE, Constant.REQUEST_TYPE_SAMPLE.AVATAR.ordinal());
                intent.putExtra(Constant.CROP_FROM, Constant.CAMERA_CROP);
                intent.putExtra(Constant.REQUEST_SINGLE_IMAGE_PATH, path);
                startActivityForResult(intent, REQUEST_CROP);
                break;
            }
            case PHOTO_WALL: {
                Intent intent = new Intent(this, CropActivity.class);
                intent.putExtra(Constant.CROP_TYPE, Constant.REQUEST_TYPE_SAMPLE.PHOTO_WALL.ordinal());
                intent.putExtra(Constant.CROP_FROM, Constant.CAMERA_CROP);
                intent.putExtra(Constant.REQUEST_SINGLE_IMAGE_PATH, path);
                startActivityForResult(intent, REQUEST_CROP);
                break;
            }
            case PHOTO_SEND: {
                Intent intent = new Intent(this, CameraDisplayActivity.class);
                intent.putExtra(Constant.REQUEST_SINGLE_IMAGE_PATH, path);
                startActivityForResult(intent, REQUEST_SINGLE_SEND);
                break;
            }
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        if (textureView != null) {
            return textureView.getSurfaceTexture();
        } else {
            return null;
        }
    }
}