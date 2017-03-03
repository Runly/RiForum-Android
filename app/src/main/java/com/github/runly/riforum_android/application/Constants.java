package com.github.runly.riforum_android.application;

import android.os.Environment;

import java.io.File;

/**
 * Created by ranly on 17-2-7.
 */

public class Constants {
    public static int SCREEN_WIDTH;  // 屏幕宽度
    public static int SCREEN_HEIGHT;  // 屏幕高度
    public static int STATUS_HEIGHT;  // 状态栏高度

    public final static int MAIN_TOPBAR_HEIGHT = 50;  // MainActivity的标题栏高度(dp)

    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    public final static int START_USER_INFO = 4;
    // 拍照路径
    public final static String SAVED_IMAGE_DIR_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/DCIM/camera/";// 拍照路径
    public final static String SAVED_AVATAR_DIR_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/DCIM/riforum_avatar.jpg";

    // plates
    public static final String NEWS = "新奇趣事";
    public static final String MEDIA = "影音娱乐";
    public static final String TRAVEL_FOOD = "旅游美食";
    public static final String GAME = "游戏电竞";
    public static final String DAILY_LIFE = "生活日常";

    public static final String INTENT_USER_DATA = "user_data";

    public static final int MALE = 0;
    public static final int FEMALE = 1;
    public static final int NO_GENDER = -1;
}
