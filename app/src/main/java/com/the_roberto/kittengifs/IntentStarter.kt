package com.the_roberto.kittengifs

import android.app.Activity
import android.content.Intent
import com.the_roberto.kittengifs.model.Settings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntentStarter @Inject constructor(val settings: Settings) {

    fun shareKitten(activity: Activity) {
        val lastGifUrl = settings.getKittenToShareUrl()
        if (lastGifUrl != null) {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "Check out this kitty :) $lastGifUrl \nFound via http://goo.gl/Rio4Ji")
            intent.type = "text/plain"
            activity.startActivity(intent)
            EventsTracker.trackShare()
        }
    }
}
