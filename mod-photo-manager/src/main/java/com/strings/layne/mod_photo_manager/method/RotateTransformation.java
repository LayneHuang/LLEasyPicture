package com.strings.layne.mod_photo_manager.method;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by laynehuang on 2017/1/10.
 * resource : http://www.jianshu.com/p/a0eb280af7ae
 */

public class RotateTransformation extends BitmapTransformation {

    private float rotateAngle = 0f;

    public RotateTransformation(Context context, float angle) {
        super(context);
        this.rotateAngle = angle;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateAngle);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public String getId() {
        return "rotate" + rotateAngle;
    }
}
