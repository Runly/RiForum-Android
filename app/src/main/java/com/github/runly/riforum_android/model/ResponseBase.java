package com.github.runly.riforum_android.model;

/**
 * Created by ranly on 17-2-14.
 */

public class ResponseBase<T> {
    public String message;
    public String code;
    public int dateline;
    public T data;
}
