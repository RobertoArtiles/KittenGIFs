package com.the_roberto.kittengifs;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Settings {

    public static final String PREFS_NAME = "pref_setting";
    public static final String PREF_CONTENT_TYPE = "pref_content_type";

    public static void setContentType(Context context, ContentType contentType) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(PREF_CONTENT_TYPE, contentType.ordinal()).apply();
    }

    public static ContentType getContentType(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int ordinal = prefs.getInt(PREF_CONTENT_TYPE, 0);
        return ContentType.values()[ordinal];
    }

    public enum ContentType {
        GIF,
        MP4
    }
}
