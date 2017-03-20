package com.github.runly.riforum_android.model;

/**
 * Created by ranly on 17-2-14.
 */

public class ResponseBase<T> extends ModelBase {
    public String message;
    public String code;
    public long dateline;
    public T data;
}
