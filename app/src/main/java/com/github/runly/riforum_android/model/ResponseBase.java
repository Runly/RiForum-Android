package com.github.runly.riforum_android.model;

import java.io.Serializable;

/**
 * Created by ranly on 17-2-14.
 */

public class ResponseBase<T> implements Serializable{
    private static final long serialVersionUID = 1L;
    public String message;
    public String code;
    public long dateline;
    public T data;
}
