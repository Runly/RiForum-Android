package com.github.runly.richedittext.utils;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * @author liye
 * @version 4.1.0
 * @since: 16/4/20 下午3:30
 */
public class ContextUtils {
    private static WeakReference<Context> mContext;

    /**
     * @param context context
     */
    public static void setContext(Context context) {
        mContext = new WeakReference<>(context);
    }

    /**
     * @return Context
     */
    public static Context getContext() {
        return mContext.get();
    }
}
