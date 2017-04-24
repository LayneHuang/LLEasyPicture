package com.strings.layne.mod_photo_manager.method;


import com.strings.layne.mod_photo_manager.camera.Constant;

/**
 * Created by laynehuang on 2017/1/11.
 * ScaleUtil
 */

public class ScaleUtil {

    public static int getHeight(Constant.REQUEST_TYPE_SAMPLE request_type, int width) {
        int height = width;
        switch (request_type) {
            case COVER:
                height /= 3;
                break;
            case AVATAR:
                break;
            case PHOTO_WALL:
                break;
            case PHOTO_SEND:
                break;
        }
        return height;
    }

    public static int getVerticalPadding(Constant.REQUEST_TYPE_SAMPLE request_type, int sreenWidth, int sreenHeight, int horizontalPadding) {
        int cropWidth = sreenWidth - (horizontalPadding << 1);
        int cropHeight = getHeight(request_type, cropWidth);
        return (sreenHeight - cropHeight) >> 1;
    }

}

