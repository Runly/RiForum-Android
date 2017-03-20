package com.github.runly.riforum_android.model;

import java.util.List;

/**
 * Created by ranly on 17-2-15.
 */

public class Entry extends ModelBase {
    public int sort;
    public int plate_id;
    public int uid;
    public int comment_num;
    public int like_num;
    public String file;
    public int read_num;
    public String title;
    public int id;
    public String content;
    public long time;
    public List<String> image;
    public User user;
    public Plate plate;
}
