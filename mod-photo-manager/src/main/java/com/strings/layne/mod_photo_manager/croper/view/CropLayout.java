package com.strings.layne.mod_photo_manager.croper.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.strings.layne.mod_photo_manager.camera.Constant;
import com.strings.layne.mod_photo_manager.method.PhotosAccessor;


/**
 * Created by laynehuang on 2017/1/10.
 * CropLayout
 */

public class CropLayout extends RelativeLayout {


    private static final String TAG = "CropLayout";
    private CropZoomImageView cropZoomImageView;
    private CropBorderView cropBorderView;
    private int cropType;

    /**
     * 可提取为之定义属性
     */
    private int horizontalPadding = 100;

    public void setImagePath(Context context, String imagePath) {

        BitmapRequestBuilder bitmapRequestBuilder
                = Glide.with(context)
                .load(imagePath)
                .asBitmap()
                .skipMemoryCache(true);

        int degree = PhotosAccessor.getInstance().getDegree(imagePath);
        degree %= 360;
        Log.d(TAG, "degree : " + degree);
        // degree = 0 ;
        if (degree != 0) {
//            bitmapRequestBuilder = bitmapRequestBuilder.transform(new RotateTransformation(context, degree));
        }
        bitmapRequestBuilder.diskCacheStrategy(DiskCacheStrategy.NONE).into(cropZoomImageView);
    }

    public CropLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        cropZoomImageView = new CropZoomImageView(context);
        cropBorderView = new CropBorderView(context);

        ViewGroup.LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        addView(cropZoomImageView, lp);
        addView(cropBorderView, lp);

        Constant.REQUEST_TYPE_SAMPLE REQUEST_TYPE = Constant.REQUEST_TYPE_SAMPLE.values()[cropType];

        cropZoomImageView.setREQUEST_TYPE(REQUEST_TYPE);
        cropBorderView.setREQUEST_TYPE(REQUEST_TYPE);
        cropZoomImageView.setHorizontalPadding(horizontalPadding);
        cropBorderView.setHorizontalPadding(horizontalPadding);

        // Todo : set the crop type ( scale )
    }

    public void setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
        cropZoomImageView.setHorizontalPadding(horizontalPadding);
        cropBorderView.setHorizontalPadding(horizontalPadding);
    }

    public void setCropType(int cropType) {
        this.cropType = cropType;
        Constant.REQUEST_TYPE_SAMPLE request_type = Constant.REQUEST_TYPE_SAMPLE.values()[cropType];
        cropZoomImageView.setREQUEST_TYPE(request_type);
        cropBorderView.setREQUEST_TYPE(request_type);
    }

    public Bitmap crop() {
        return cropZoomImageView.crop();
    }
}
