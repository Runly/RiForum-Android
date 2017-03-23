package com.github.runly.riforum_android.model;

import java.util.List;

/**
 * Created by ranly on 17-2-14.
 */

public class ResponseBase<T> extends ModelBase {
    public String message;
    public String code;
    public long dateline;
    public T data;
    public List<Entry> entry_list;
    public List<User> user_list;
    public int entry_number;
    public int user_number;
}
