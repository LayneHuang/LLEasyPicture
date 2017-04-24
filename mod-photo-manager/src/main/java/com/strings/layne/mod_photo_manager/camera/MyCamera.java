package com.strings.layne.mod_photo_manager.camera;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.support.v4.content.ContextCompat;
import android.view.Surface;

import com.strings.layne.mod_photo_manager.R;
import com.strings.layne.mod_photo_manager.method.PhotoSaveService;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.strings.layne.mod_photo_manager.camera.CameraUtil.FLASH_LIGHT_AUTO;
import static com.strings.layne.mod_photo_manager.camera.CameraUtil.FLASH_LIGHT_OFF;
import static com.strings.layne.mod_photo_manager.camera.CameraUtil.FLASH_LIGHT_ON;

/**
 * Created by laynehuang on 2017/3/7.
 * MyCamera
 * use when photo version below 5.0
 */
public class MyCamera {

    private static final String TAG = "MyCamera";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private int viewWidth;
    private int viewHeight;
    private int flashLightState = FLASH_LIGHT_AUTO;
    private Boolean isFront;
    private CameraActivity activity = null;
    private boolean isFlashSupported = true;
    private boolean isCanDeal = false;
    private File mFile;
    private int resultOrientation;

    public MyCamera(CameraActivity activity, boolean isFront, int viewWidth, int viewHeight) {
        this.activity = activity;
        this.isFront = isFront;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.mFile = CameraUtil.getInstance().createFile();
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    void open() {
        try {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "hl have no permission");
                return;
            }
            if (!checkCamera()) {
                return;
            }

            if (mCamera != null) {
                close();
            }

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                if (isFront && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
//                    try {
                    mCamera = Camera.open(i);
//                    } catch (Exception e) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                        builder.setMessage(R.string.permission_camera);
//                        builder.setNegativeButton(R.string.okay, (dialog, which) -> {
//                            dialog.dismiss();
//                            activity.finish();
//                        });
//                        builder.setCancelable(false);
//                        builder.create().show();
//                        Timber.w(e, e.toString());
//                        return;
//                    }
                    break;
                } else if (!isFront && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCamera = Camera.open(i);
                    break;
                }
            }

            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);
            /**
             * 相机翻转设置
             */
            int degrees = getDisplayRotation();

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                resultOrientation = (cameraInfo.orientation + degrees) % 360;
                resultOrientation = (360 - resultOrientation) % 360; // compensate the mirror
            } else { // back-facing
                resultOrientation = (cameraInfo.orientation - degrees + 360) % 360;
            }
//        Timber.i("hl cameraInfo %d , camera orientation %d， original degree %d", cameraInfo.orientation, resultOrientation, degrees);
            mCamera.setDisplayOrientation(resultOrientation);
            cameraSizeInit();
            mParams = mCamera.getParameters(); //重新get一次
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.permission_camera);
            builder.setNegativeButton(R.string.i_know, (dialog, which) -> {
                dialog.dismiss();
                activity.finish();
            });
            builder.setCancelable(false);
            builder.create().show();
            return;
        }
    }

    public int getResultOrientation() {
        return resultOrientation;
    }

    public void cameraSizeInit() {

        int resultPictureIndex = -1, resultPreviewIndex = -1;
        if (mParams.getSupportedPreviewSizes() != null && mParams.getSupportedPreviewSizes().size() != 0) {
            resultPreviewIndex = CameraUtil.getInstance().chooseOptimalSize(
                    mParams.getSupportedPreviewSizes(),
                    viewWidth,
                    viewHeight
            );
            mParams.setPreviewSize(
                    mParams.getSupportedPreviewSizes().get(resultPreviewIndex).width,
                    mParams.getSupportedPreviewSizes().get(resultPreviewIndex).height
            );
        }

        if (mParams.getSupportedPictureSizes() != null && mParams.getSupportedPictureSizes().size() != 0) {
            resultPictureIndex = CameraUtil.getInstance().chooseOptimalSize(
                    mParams.getSupportedPictureSizes(),
                    viewWidth,
                    viewHeight
            );
            mParams.setPictureSize(
                    mParams.getSupportedPictureSizes().get(resultPictureIndex).width,
                    mParams.getSupportedPictureSizes().get(resultPictureIndex).height
            );
        }

//        Log.d(TAG, "hl view size : " + viewWidth + " " + viewHeight);
//        for (Camera.Size option : mParams.getSupportedPreviewSizes()) {
//            Log.d(TAG, "hl pre " + option.width + " " + option.height);
//        }
//        Log.d(TAG, "hl pre size : " + mParams.getSupportedPreviewSizes().get(resultPreviewIndex).width + " " + mParams.getSupportedPreviewSizes().get(resultPreviewIndex).height);
//
//        for (Camera.Size option : mParams.getSupportedPictureSizes()) {
//            Log.d(TAG, "hl pic " + option.width + " " + option.height);
//        }
//        Log.d(TAG, "hl pic size : " + mParams.getSupportedPictureSizes().get(resultPictureIndex).width + " " + mParams.getSupportedPictureSizes().get(resultPictureIndex).height);

        int tempHeight = mParams.getSupportedPreviewSizes().get(resultPreviewIndex).height;
        int tempWidth = mParams.getSupportedPreviewSizes().get(resultPreviewIndex).width;

//        Log.d(TAG, "hl camera option w , h " + tempWidth + " " + tempHeight);
//        Log.d(TAG, "hl texture now w , h " + viewWidth + " " + viewHeight);

        activity.setTextureViewAspectRatio(tempHeight, tempWidth);
        /**
         * 闪光灯参数设置
         */
        if (mParams.getSupportedFlashModes() != null && mParams.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        if (mParams.getSupportedFocusModes() != null && mParams.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        mCamera.setParameters(mParams);
        try {
            mCamera.setPreviewTexture(activity.getSurfaceTexture());
            mCamera.startPreview();//开启预览
            isPreviewing = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            isPreviewing = false;
            e.printStackTrace();
        }

        isCanDeal = true;
    }

    void close() {
        if (null != mCamera) {

            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParams);

            mCamera.setPreviewCallback(null);
            if (isPreviewing) {
                mCamera.stopPreview();
                isPreviewing = false;
            }

            isCanDeal = false;
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * take picture
     * param  isCanDeal : make sure the first click useful
     * isPreViewing : make sure texture view is previewing before taking picture
     */


    void takePicture() {
        setFlash();
        if (isCanDeal && isPreviewing && (mCamera != null)) {
            isCanDeal = false;
            try {
                mCamera.takePicture(null, null, mJpegPictureCallback);//mShutterCallback
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.permission_camera);
                builder.setNegativeButton(R.string.i_know, (dialog, which) -> {
                    dialog.dismiss();
                    activity.finish();
                });
                builder.setCancelable(false);
                builder.create().show();
            }
        }

    }

    public boolean checkCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return true;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return true;
            }
        }
        return false;
    }

    private Camera.PictureCallback mJpegPictureCallback = (data, camera) -> {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        options.inSampleSize = 2;
        Bitmap oldBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        Observable.just(CameraUtil.getInstance().orientationFix(oldBitmap, resultOrientation, isFront))
                .observeOn(Schedulers.io())
                .map(bitmap -> {
                    PhotoSaveService saveService = new PhotoSaveService(bitmap, mFile);
                    try {
                        saveService.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        bitmap.recycle();
                        bitmap = null;
                    }
//                    Log.d(TAG, "hl " + mFile);
                    return mFile;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                        isCanDeal = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        isCanDeal = true;
                    }

                    @Override
                    public void onNext(File file) {
                        if (activity != null) {
                            activity.dealResult(file.getAbsolutePath());
                        }
                    }
                });

    };

    private int getDisplayRotation() {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    private void setFlash() {
        if (isFront || !isFlashSupported) {
            return;
        }
        String flashMode = Camera.Parameters.FLASH_MODE_AUTO;
        switch (flashLightState) {
            case FLASH_LIGHT_AUTO:
                flashMode = Camera.Parameters.FLASH_MODE_AUTO;
                break;
            case FLASH_LIGHT_ON:
                flashMode = Camera.Parameters.FLASH_MODE_ON;
                break;
            case FLASH_LIGHT_OFF:
                flashMode = Camera.Parameters.FLASH_MODE_OFF;
                break;
            default:
                break;
        }
        mParams = mCamera.getParameters();
        mParams.setFlashMode(flashMode);
        mCamera.setParameters(mParams);
    }

    public boolean isFlashSupported() {
        return isFlashSupported;
    }

    public void setFlashLightState(int flashLightState) {
        this.flashLightState = flashLightState;
    }

}