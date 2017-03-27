package com.the_roberto.kittengifs

import android.app.Activity
import android.content.Intent
import com.the_roberto.kittengifs.EventsTracker
import com.the_roberto.kittengifs.Settings
import com.the_roberto.kittengifs.kittens.KittensActivity

fun KittensActivity.shareKitten() {
    val lastGifUrl = Settings.getKittenToShareUrl(this)
    if (lastGifUrl != null) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this kitty :) $lastGifUrl \nFound via http://goo.gl/Rio4Ji")
        intent.type = "text/plain"
        startActivity(intent)
        EventsTracker.trackShare()
    }
}