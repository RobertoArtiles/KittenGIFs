package com.the_roberto.kittengifs;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Settings {

    public static final String PREFS_NAME = "pref_setting";
    public static final String PREF_CONTENT_TYPE = "pref_content_type";
    public static final String PREF_LAST_OPENED_GIF = "pref_last_opened_gif";
    public static final String PREF_MAX_OFFSET = "pref_max_offset";

    public static void setContentType(Context context, ContentType contentType) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(PREF_CONTENT_TYPE, contentType.ordinal()).apply();
    }

    public static ContentType getContentType(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int ordinal = prefs.getInt(PREF_CONTENT_TYPE, 0);
        return ContentType.values()[ordinal];
    }

    public static void setLastOpenedGif(Context context, String url) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(PREF_LAST_OPENED_GIF, url).apply();
    }

    public static String getLastOpenedGif(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(PREF_LAST_OPENED_GIF, null);
    }

    public static void setMaxOffset(Context context, int maxOffset) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(PREF_MAX_OFFSET, maxOffset).apply();
    }

    public static int getMaxOffset(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(PREF_MAX_OFFSET, 100);
    }

    public enum ContentType {
        GIF,
        MP4
    }
}
