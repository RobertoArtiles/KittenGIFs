package com.the_roberto.kittengifs

import android.content.Context
import android.content.Context.MODE_PRIVATE

object Settings {
    const val PREFS_NAME = "pref_setting"
    const val PREF_LAST_OPENED_KITTEN = "pref_last_opened_kitten"
    const val PREF_KITTEN_TO_SHARE_URL = "pref_kitten_to_share_url"
    const val PREF_MAX_OFFSET = "pref_max_offset"
    const val PREF_VIEWS_COUNT = "pref_views_count"
    const val PREF_KITTENS_BEFORE_ASKING_TO_RATE = "pref_kittens_before_asking_to_rate"
    const val PREF_CURRENT_VERSION = "pref_current_version"

    fun setLastOpenedKitten(context: Context, url: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putString(PREF_LAST_OPENED_KITTEN, url).apply()
    }

    fun getLastOpenedKitten(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getString(PREF_LAST_OPENED_KITTEN, null)
    }

    fun setKittenToShareUrl(context: Context, url: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putString(PREF_KITTEN_TO_SHARE_URL, url).apply()
    }

    fun getKittenToShareUrl(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getString(PREF_KITTEN_TO_SHARE_URL, null)
    }


    fun setMaxOffset(context: Context, maxOffset: Int?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putInt(PREF_MAX_OFFSET, maxOffset ?: 0).apply()
    }

    fun getMaxOffset(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getInt(PREF_MAX_OFFSET, 100)
    }

    fun getViewsCount(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getLong(PREF_VIEWS_COUNT, 0)
    }

    fun setViewsCount(context: Context, viewsCount: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putLong(PREF_VIEWS_COUNT, viewsCount).apply()
    }

    fun setKittensBeforeAskingToRate(context: Context, kittens: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putInt(PREF_KITTENS_BEFORE_ASKING_TO_RATE, kittens).apply()
    }

    fun getKittensBeforeAskingToRate(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getInt(PREF_KITTENS_BEFORE_ASKING_TO_RATE, 10)
    }

    fun setCurrentVersion(context: Context, version: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putInt(PREF_CURRENT_VERSION, version).apply()
    }

    fun getCurrentVersion(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getInt(PREF_CURRENT_VERSION, 0)
    }


    enum class ContentType {
        GIF,
        MP4
    }
}
