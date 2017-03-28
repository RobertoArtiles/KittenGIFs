package com.the_roberto.kittengifs.model

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.the_roberto.kittengifs.dagger.ForApplication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Settings @Inject constructor(@ForApplication private val context: Context) {

    private val PREFS_NAME = "pref_setting"
    private val PREF_LAST_OPENED_KITTEN = "pref_last_opened_kitten"
    private val PREF_KITTEN_TO_SHARE_URL = "pref_kitten_to_share_url"
    private val PREF_MAX_OFFSET = "pref_max_offset"
    private val PREF_VIEWS_COUNT = "pref_views_count"
    private val PREF_KITTENS_BEFORE_ASKING_TO_RATE = "pref_kittens_before_asking_to_rate"
    private val PREF_CURRENT_VERSION = "pref_current_version"

    fun setLastOpenedKitten(url: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putString(PREF_LAST_OPENED_KITTEN, url).apply()
    }

    fun getLastOpenedKitten(): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getString(PREF_LAST_OPENED_KITTEN, null)
    }

    fun setKittenToShareUrl(url: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putString(PREF_KITTEN_TO_SHARE_URL, url).apply()
    }

    fun getKittenToShareUrl(): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getString(PREF_KITTEN_TO_SHARE_URL, null)
    }


    fun setMaxOffset(maxOffset: Int?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putInt(PREF_MAX_OFFSET, maxOffset ?: 0).apply()
    }

    fun getMaxOffset(): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return Math.min(prefs.getInt(PREF_MAX_OFFSET, 100), 4998) //5000 is a limit for the public API
    }

    fun getViewsCount(): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getLong(PREF_VIEWS_COUNT, 0)
    }

    fun setViewsCount(viewsCount: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putLong(PREF_VIEWS_COUNT, viewsCount).apply()
    }

    fun setKittensBeforeAskingToRate(kittens: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putInt(PREF_KITTENS_BEFORE_ASKING_TO_RATE, kittens).apply()
    }

    fun getKittensBeforeAskingToRate(): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getInt(PREF_KITTENS_BEFORE_ASKING_TO_RATE, 10)
    }

    fun setCurrentVersion(version: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putInt(PREF_CURRENT_VERSION, version).apply()
    }

    fun getCurrentVersion(): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getInt(PREF_CURRENT_VERSION, 0)
    }


    enum class ContentType {
        GIF,
        MP4
    }
}
