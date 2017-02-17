package com.github.runly.riforum_android.qiniu;

/**
 * Created by ranly on 17-2-15.
 */

public class QiniuToken {
    public String token;
    public String key;
    public String base_url;

    public String getUrl() {
        return base_url + key;
    }
}
