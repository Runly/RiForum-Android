package com.github.runly.riforum_android.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by ranly on 17-2-7.
 */

public class Constant {
    public static int SCREEN_WIDTH;  // 屏幕宽度
    public static int SCREEN_HEIGHT;  // 屏幕高度
    public static int STATUS_HEIGHT;  // 状态栏高度

    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    // 拍照路径
    public final static String SAVED_IMAGE_DIR_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/DCIM/camera/";
}
