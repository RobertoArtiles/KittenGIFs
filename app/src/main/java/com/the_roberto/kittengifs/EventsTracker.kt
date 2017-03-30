package com.the_roberto.kittengifs

import android.content.Context
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

object EventsTracker {
    lateinit var googleAnalytics: GoogleAnalytics
    private lateinit var tracker: Tracker

    fun init(context: Context) {
        googleAnalytics = GoogleAnalytics.getInstance(context)
        tracker = if (BuildConfig.DEBUG) googleAnalytics.newTracker("debug") else googleAnalytics.newTracker(R.xml.global_tracker)
    }

    fun trackNextKitten() {
        tracker.send(HitBuilders.EventBuilder().setCategory("User Actions").setAction("Next Kitten").build())
    }

    fun trackShare() {
        tracker.send(HitBuilders.EventBuilder().setCategory("User Actions").setAction("Share").build())
    }

    fun trackSuccessfulKitten() {
        tracker.send(HitBuilders.EventBuilder().setCategory("Action Results").setAction("Kitten Download").setLabel("success").build())
    }

    fun trackFailedKitten() {
        tracker.send(HitBuilders.EventBuilder().setCategory("Action Results").setAction("Kitten Download").setLabel("fail").build())
    }

    fun trackRatingLater(cancel: Boolean) {
        tracker.send(HitBuilders.EventBuilder().setCategory("Rating").setAction("Later").setLabel(if (cancel) "cancel" else "explicit").build())
    }

    fun trackRatingYes() {
        tracker.send(HitBuilders.EventBuilder().setCategory("Rating").setAction("Yes").build())
    }

    fun trackRatingNah() {
        tracker.send(HitBuilders.EventBuilder().setCategory("Rating").setAction("Nah").build())
    }

}
