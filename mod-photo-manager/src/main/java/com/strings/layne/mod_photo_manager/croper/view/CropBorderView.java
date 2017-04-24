package com.strings.layne.mod_photo_manager.croper.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.strings.layne.mod_photo_manager.camera.Constant;
import com.strings.layne.mod_photo_manager.method.ScaleUtil;


/**
 * Created by laynehuang on 2017/1/9.
 * CropBorderView
 */

public class CropBorderView extends View {

    private static final int DEFUALT_PX = 2;
    /*
        水平方向与View的边距
     */
    private int horizontalPadding;
    /*
        垂直方向与View的边距
     */
    private int verticalPadding;
    /*
        绘制的矩形的宽度
     */
    private int rectWidth;
    private int rectHeight;
    /*
        绘制的圆的半径
     */
    private int radius;
    /*
        边界线宽度，（ dp ）
     */
    private int borderWidth = 1;
    /*
        是否为封面剪切
     */
    private Constant.REQUEST_TYPE_SAMPLE request_type = Constant.REQUEST_TYPE_SAMPLE.AVATAR;

    private Paint mPaint;
    private Paint mPaintOval;

    public CropBorderView(Context context) {
        this(context, null);
    }

    public CropBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropBorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundColor(Color.TRANSPARENT);
        borderWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, borderWidth, getResources().getDisplayMetrics()
        );
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaintOval = new Paint();
        mPaintOval.setStyle(Paint.Style.STROKE);
        mPaintOval.setAntiAlias(true);
        mPaintOval.setColor(Color.parseColor("#99000000"));
        mPaintOval.setStrokeWidth(400);
    }

    public void setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
    }

    public void setREQUEST_TYPE(Constant.REQUEST_TYPE_SAMPLE REQUEST_TYPE) {
        this.request_type = REQUEST_TYPE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置画笔类型
        mPaint.setColor(Color.parseColor("#99000000"));
        mPaint.setStyle(Paint.Style.FILL);

        int screenWith = getWidth();
        int screenHeight = getHeight();
        rectWidth = screenWith - 2 * horizontalPadding;
        rectHeight = ScaleUtil.getHeight(request_type, rectWidth);
        verticalPadding = ScaleUtil.getVerticalPadding(request_type, screenWith, screenHeight, horizontalPadding);

        switch (request_type) {
            case AVATAR:                                                                                 // circle
                int radius = (rectWidth - (borderWidth << 1)) >> 1;
                mPaintOval.setStrokeWidth(screenHeight);
                mPaintOval.setColor(Color.parseColor("#99000000"));
                canvas.drawCircle(screenWith >> 1, screenHeight >> 1, radius + (screenHeight >> 1), mPaintOval);
                // Todo: px change
                mPaintOval.setStrokeWidth(DEFUALT_PX);
                mPaintOval.setColor(Color.parseColor("#CCFFFFFF"));
                canvas.drawCircle(screenWith >> 1, screenHeight >> 1, radius, mPaintOval);
                break;
            case COVER:                                                                                  // rectangle
            case PHOTO_WALL:
                // left
                canvas.drawRect(0, 0, horizontalPadding, screenHeight, mPaint);
                // up
                canvas.drawRect(horizontalPadding, 0, screenWith - horizontalPadding, verticalPadding, mPaint);
                // right
                canvas.drawRect(screenWith - horizontalPadding, 0, screenWith, screenHeight, mPaint);
                // bottom
                canvas.drawRect(horizontalPadding, screenHeight - verticalPadding, screenWith - horizontalPadding, screenHeight, mPaint);
                // Todo: px change
                mPaintOval.setStrokeWidth(DEFUALT_PX);
                mPaintOval.setColor(Color.parseColor("#CCFFFFFF"));
                canvas.drawRect(horizontalPadding, verticalPadding, screenWith - horizontalPadding, screenHeight - verticalPadding, mPaintOval);
                break;
            case PHOTO_SEND:
                break;
        }
    }
}
