package com.github.runly.riforum_android.qiniu;

import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;

/**
 * Created by ranly on 17-2-15.
 */

public class UploadManagerFactory {
    private static UploadManager uploadManager;

    public static UploadManager getUploadManager() {
        if (null == uploadManager) {
            Configuration config = new Configuration.Builder()
                    .connectTimeout(10) // 链接超时。默认10秒
                    .responseTimeout(60) // 服务器响应超时。默认60秒
                    .build();
            // 重用uploadManager。一般地，只需要创建一个uploadManager对象
            uploadManager = new UploadManager(config);
        }
        return uploadManager;
    }
}
