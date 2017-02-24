package com.github.runly.riforum_android.model;

/**
 * Created by ranly on 17-2-23.
 */

public class Comment {

    /**
     * content : 评论测试
     * entry_id : 1
     * uid : 10000001
     * time : 1487855140585
     * comment_id : -1
     * plate_id : 1
     * id : 5
     * user: {}
     */

    public String content;
    public int entry_id;
    public int uid;
    public long time;
    public int comment_id;
    public int plate_id;
    public int id;
    public User user;
    public Comment commented;
}
