package com.strings.layne.mod_photo_manager.croper.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;

import com.strings.layne.mod_photo_manager.camera.Constant;
import com.strings.layne.mod_photo_manager.method.ScaleUtil;


/**
 * Created by laynehuang on 2017/1/10.
 * CropZoomImageView
 */


public class CropZoomImageView extends android.support.v7.widget.AppCompatImageView implements
        ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener,
        ViewTreeObserver.OnGlobalLayoutListener {


    private Constant.REQUEST_TYPE_SAMPLE request_type = Constant.REQUEST_TYPE_SAMPLE.AVATAR;

    static final float BIGGER = 1.07f;
    static final float ORGINAL = 1.00f;
    static final float SMALLER = 0.93f;
    public static float SCALE_MAX = 4.0f;
    private static float SCALE_MID = 2.0f;
    private static final float eps = 0.01f;
    private static final float mTouchSlop = eps * 10;
    private static final int DEFAULT_DELAY_TIME = 16;


    private static final int DEFUALT_CROP_WIDTH = 400;

    private float initScale = 1.0f;

    private boolean once = true;

    private final float[] maxtrixValues = new float[9];
    /**
     * 缩放手势检测
     */
    private ScaleGestureDetector scaleGestureDetector = null;
    private final Matrix mScaleMatrix = new Matrix();

    /**
     * 双击检测
     */
    private boolean isAutoScale;
    private float lastX;
    private float lastY;
    private int lastPointerCount;
    private boolean isCanDrag = false;
    /**
     * 水平方向,垂直方向的边距
     */
    private int horizontalPadding;
    private int verticalPadding;

    public CropZoomImageView(Context context) {
        this(context, null);
    }

    public CropZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setScaleType(ScaleType.MATRIX);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }

    /**
     * Crop picture
     *
     * @return Bitmap
     * @Modify 2017.4.18 laynehuang , compress before crop
     */
    public Bitmap crop() {


        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

        int originalWidth = getWidth() - (horizontalPadding << 1);
        int originalHeight = getHeight() - (verticalPadding << 1);

        int targetWidth = 400;
        int targetHeight = ScaleUtil.getHeight(request_type, targetWidth);

        double smallWidthRatio = 1.0f * targetWidth / originalWidth;
        double smallHeightRatio = 1.0f * targetHeight / originalHeight;

        Bitmap smallBitmap = ThumbnailUtils.extractThumbnail(
                bitmap,
                (int) (smallWidthRatio * getWidth()),
                (int) (smallHeightRatio * getHeight()),
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT
        );
        bitmap.recycle();

        Bitmap resultBitmap = Bitmap.createBitmap(
                smallBitmap,
                (smallBitmap.getWidth() - targetWidth) >> 1,
                (smallBitmap.getHeight() - targetHeight) >> 1,
                targetWidth,
                targetHeight
        );
        smallBitmap.recycle();

        return resultBitmap;
    }

    // -------监听器的接口----------

    /*
        双手指操作
     */

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null) {
            return true;
        }
        /*
            缩放的范围控制
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > SCALE_MID && scaleFactor < 1.0f)) {
            if (scaleFactor * scale < SCALE_MID) {
                scaleFactor = SCALE_MID / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            /*
                设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());
            checkBorder();
            setImageMatrix(mScaleMatrix);

        }

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        scaleGestureDetector.onTouchEvent(event);

        // 获取触点的均值
        float x = 0, y = 0;
        final int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; ++i) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;

        // 当触点变化时候,重置lastX , lastY
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            lastX = x;
            lastY = y;
        }

        lastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - lastX;
                float dy = y - lastY;
//                Log.d(TAG, "dx :" + dx + " dy : " + dy);
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {
                        RectF rectF = getMatrixRectF();
                        // 如果宽度小于屏幕宽度，禁止左右移动
                        //Log.d(TAG, "recf hei : " + rectF.height() + " , border hei : " + (getHeight() - (verticalPadding << 1)));
                        if (rectF.width() <= (getWidth() - (horizontalPadding << 1))) {
                            dx = 0;
                        }
                        // 同理
                        if (rectF.height() <= (getHeight() - (verticalPadding << 1))) {
                            dy = 0;
                        }
                        //做平移
                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorder();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }
        return true;
    }

    /**
     * 当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，所要调用的回调函数的接口类
     * 这是获得一个view的宽度和高度的方法。
     */
    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null) {
                return;
            }
            verticalPadding = ScaleUtil.getVerticalPadding(request_type, getWidth(), getHeight(), horizontalPadding);
            // 拿到图片的宽和高
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scaleW = 1.0f * (getWidth() - (horizontalPadding << 1)) / dw;
            float scaleH = 1.0f * (getHeight() - (verticalPadding << 1)) / dh;
            initScale = Math.max(scaleH, scaleW);
            SCALE_MID = 1.0f * initScale;
            SCALE_MAX = 4.0f * initScale;
            mScaleMatrix.postTranslate((getWidth() - dw) >> 1, (getHeight() - dh) >> 1);
            mScaleMatrix.postScale(initScale * BIGGER, initScale * BIGGER, getWidth() >> 1, getHeight() >> 1);
            setImageMatrix(mScaleMatrix);
            once = false;
        }
    }

    /**
     * 根据当前照片的Matrix 获取照片的范围
     * RectF 表示坐标系中的一块矩形区域 , 暂且代表图像截屏的区域...
     */

    public RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 边界检测
     */

    private void checkBorder() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rect.width() + eps >= width - (horizontalPadding << 1)) {

            if (rect.left > horizontalPadding) {
                deltaX = -rect.left + horizontalPadding;
            }
            if (rect.right < width - horizontalPadding) {
                deltaX = width - horizontalPadding - rect.right;
            }
        }

        if (rect.height() + eps >= height - (verticalPadding << 1)) {
            if (rect.top > verticalPadding) {
                deltaY = -(rect.top - verticalPadding);
            }
            if (rect.bottom < height - verticalPadding) {
                deltaY = height - verticalPadding - rect.bottom;
            }
        }
        // 矩阵做平移
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 获取当前缩放比例
     */
    public final float getScale() {
        mScaleMatrix.getValues(maxtrixValues);
        return maxtrixValues[Matrix.MSCALE_X];
    }

    /**
     * 是否是拖动行为
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    public void setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
    }

    public void setREQUEST_TYPE(Constant.REQUEST_TYPE_SAMPLE REQUEST_TYPE) {
        this.request_type = REQUEST_TYPE;
    }
}