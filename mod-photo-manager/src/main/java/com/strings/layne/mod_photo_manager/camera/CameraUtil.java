package com.strings.layne.mod_photo_manager.camera;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;

import java.io.File;
import java.util.List;

/**
 * Created by laynehuang on 2017/3/7.
 * CameraUtil
 */

public class CameraUtil {

//    private final static String TAG = "CameraUtil";

    public static final int FLASH_LIGHT_OFF = 0;
    public static final int FLASH_LIGHT_ON = 1;
    public static final int FLASH_LIGHT_AUTO = 2;

    private CameraActivity activity;

    private CameraUtil() {
    }

    private static class SingletonHolder {
        private static final CameraUtil INSTANCE = new CameraUtil();
    }

    public static CameraUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public void init(CameraActivity activity) {
        this.activity = activity;
    }

    public File createFile() {
//        return new File(SandboxDirectory.get(activity).getCameraCacheDir(), System.currentTimeMillis() + ".jpg");
        return new File(activity.getCacheDir(), System.currentTimeMillis() + ".jpg");
    }


    public Bitmap orientationFix(Bitmap oldBitmap, int orientation, Boolean isFront) {

        int dynamicOrientation = 0;
        if( activity != null ) {
            dynamicOrientation = activity.getOrientation();;
        }
        Matrix matrix = new Matrix();

        if (isFront) {
            int width = oldBitmap.getWidth();
            float matrixValues[] = {-1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f};
            matrix.setValues(matrixValues);
            //表示将图片左右倒置
            matrix.postTranslate(width * 2, 0);
        }

        int resultOrientation = orientation;
//        Timber.i("hl camera orientation %d", resultOrientation);
//        Timber.i("hl dynamic orientation %d", dynamicOrientation);
        resultOrientation = (resultOrientation + dynamicOrientation) % 360;
        if (resultOrientation != 0) {
            matrix.postRotate(resultOrientation);
        }
        return Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, true);
    }

    private static double eps = 1e-8;

    private int fabs(double x) {
        if (Math.abs(x) < eps) {
            return 0;
        }
        return x > 0 ? 1 : -1;
    }

    private double aspectRatioDif(int width1, int height1, int width2, int height2) {
        if (fabs(height1) == 0 || fabs(height2) == 0) return 1e8;
        return Math.abs(1.0 * width1 / height1 - (1.0 * width2 / height2));
    }

    private double balanceParameter(int width1, int height1, int width2, int height2) {
        double result = Math.abs(width1 + height1 - width2 - height2) / 10;
        result = result + aspectRatioDif(width1, height1, width2, height2) * 100;
        return result;
    }

    public int chooseOptimalSize(List<Camera.Size> choices, int viewWidth, int viewHeight) {
        // template choice
        int pw, ph;
        // result choice
        int cw = -1, ch = -1;
        // result choice index in choices
        int index = -1;
        if (choices.size() == 0) {
            return Integer.parseInt(null);
        }
        for (int i = 0; i < choices.size(); ++i) {
            pw = choices.get(i).width;
            ph = choices.get(i).height;
            if (pw > ph) {
                int temp = pw;
                pw = ph;
                ph = temp;
            }
//            Log.d(TAG, "hl now " + pw + " " + ph + " " + choices.get(i).width + " " + choices.get(i).height);
            int dif = -1;
            if (index != -1) {
                dif = fabs(balanceParameter(pw, ph, viewWidth, viewHeight)
                        - balanceParameter(cw, ch, viewWidth, viewHeight));
//                Log.d(TAG, "hl dif " +
//                        (balanceParameter(pw, ph, viewWidth, viewHeight) - balanceParameter(cw, ch, viewWidth, viewHeight))
//                );
//                Log.d(TAG, "hl dif " + dif);
            }
            if (dif < 0) {
                cw = pw;
                ch = ph;
                index = i;
            }
        }
        return index;
    }
}
