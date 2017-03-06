package com.github.runly.riforum_android.utils;

import android.content.SharedPreferences;

import com.github.runly.riforum_android.application.App;
import com.github.runly.riforum_android.application.Constants;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ranly on 17-3-3.
 */

public class SharedPreferencesUtil {
    private static SharedPreferences sp;

    private SharedPreferencesUtil() {
        sp = App.getInstance().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
    }

    private static SharedPreferences getSp() {
        if (sp == null) {
            new SharedPreferencesUtil();
        }
        return sp;
    }

    public static void saveValue(String key, Object value) {
        SharedPreferences.Editor editor = getSp().edit();
        if (value instanceof String) {
            String s = (String) value;
            editor.putString(key, s);
            editor.apply();
            return;
        }

        if (value instanceof Integer) {
            int i = (int) value;
            editor.putInt(key, i);
            editor.apply();
            return;
        }

        if (value instanceof Boolean) {
            boolean b = (boolean) value;
            editor.putBoolean(key, b);
            editor.apply();
            return;
        }

        if (value instanceof Long) {
            Long l = (Long) value;
            editor.putLong(key, l);
            editor.apply();
            return;
        }

        if (value instanceof Float) {
            float f = (Float) value;
            editor.putFloat(key, f);
            editor.apply();
        }
    }

    public static void saveValueWithSet(String key, Set<String> set) {
        SharedPreferences.Editor editor = getSp().edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    public static String getString(String key) {
        return getSp().getString(key, "");
    }

    public static int getInt(String key) {
        return getSp().getInt(key, 0);
    }

    public static float getFloat(String key) {
        return getSp().getFloat(key, 0);
    }

    public static long getLong(String key) {
        return getSp().getLong(key, 0);
    }

    public static boolean getBoolean(String key) {
        return getSp().getBoolean(key, false);
    }
}
