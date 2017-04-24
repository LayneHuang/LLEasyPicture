package com.strings.layne.mod_photo_manager.camera;

/**
 * Constant
 * Created by laynehuang on 2017/4/24.
 */

public class Constant {

    /**
     * requestCode for crop , single send(camera), multi send(album)
     */

    public static final int REQUEST_CROP = 3210;
    public static final int REQUEST_SINGLE_SEND = 3211;
    public static final int REQUEST_MULTI_SEND = 3212;

    /**
     * intent 请求相册功能的 key
     */
    public static final String REQUEST_TYPE = "requestType";

    /**
     * request type sample  相册功能请求的例子
     */
    public enum REQUEST_TYPE_SAMPLE {
        COVER,                      // crop , scale 1:3  preview : rect ,  封面
        AVATAR,                     // crop , scale 1:1  preview : circle  ,  头像
        PHOTO_WALL,                 // crop , scale 1:1  preview : rect     , 照片墙
        SINGLE_SELECT,              // no crop
        PHOTO_SEND                  // multi select   , 发送的图片
    }

    public static final String RESULT_MULTI_IMAGES = "resultMultiImages";

    /**
     * SELECT_COUNT         ( intent key    请求相册多选图片的最大数目 )
     * DEFAULT_SELECT_COUNT ( intent value  多选默认数目 )
     */
    public static final String SELECT_COUNT = "selectCount";
    public static final int DEFAULT_SELECT_COUNT = 9;

    /** 个别样例
     * usage (在相册选头像):
     * Intent intent = new Intent(this, AlbumActivity.class);
     * intent.putExtra(AlbumConstant.REQUEST_TYPE, AlbumConstant.REQUEST_TYPE_SAMPLE.AVATAR.ordinal());
     * startActivityForResult(intent, REQUEST_CROP);
     * <p>
     * usage (相机拍头像):
     * Intent intent = new Intent(this, CameraActivity.class);
     * intent.putExtra(AlbumConstant.REQUEST_TYPE, AlbumConstant.REQUEST_TYPE_SAMPLE.AVATAR.ordinal());
     * startActivityForResult(intent, REQUEST_CROP);
     * <p>
     * usage (在相册选多张图发送):
     * Intent intent = new Intent(this, AlbumActivity.class);
     * intent.putExtra(AlbumConstant.REQUEST_TYPE, AlbumConstant.REQUEST_TYPE_SAMPLE.PHOTO_SEND.ordinal());
     * intent.putExtra(AlbumConstant.SELECT_COUNT, AlbumConstant.DEFAULT_SELECT_COUNT);
     * startActivityForResult(intent, REQUEST_MULTI_SEND);
     */

    /**
     * onActivityResult 返回的 intent key , 获取单张图片路径  value : String
     */
    public static final String RESULT_SINGLE_IMAGE_PATH = "resultImagePath";
    /**
     * onActivityResult 返回的 intent key , 获取多张图片路径 value : ArrayList<String>
     */
    public static final String RESULT_MULTI_IMAGES_PATH = "resultImagesPath";
    /**
     * onActivityResult 返回的 intent key , 获取多张图片中各图是否是原图 value : ArrayList<Integer>
     * 1 为原图 , 0 为略缩图
     */
    public static final String RESULT_MULTI_IMAGES_ORIGINAL = "isOriginal";

    //----------------------------模块内常数-----module-constant-----------------------------------
    public static final String CROP_FROM = "cropFrom";

    public static final int CAMERA_CROP = 0;
    public static final int ALBUM_CROP = 1;
    public static final String CROP_TYPE = "cropType";
    public static final String DISPLAY_POSITION = "beginPosition";
    public static final String REQUEST_SINGLE_IMAGE_PATH = "requestImagePath";
    public static final String SEND_PHOTOS_URL = "netPhotosUrl";

}

