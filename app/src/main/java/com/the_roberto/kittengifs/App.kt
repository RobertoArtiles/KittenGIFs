package com.the_roberto.kittengifs

import android.app.Application

import com.crashlytics.android.Crashlytics

import io.fabric.sdk.android.Fabric

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        EventsTracker.init(this)
        GifsController.init(this)
        migrate()
    }

    private fun migrate() {
        val oldVersion = Settings.getCurrentVersion(this)
        if (oldVersion < 13) {
            Settings.setKittensBeforeAskingToRate(this, Settings.getViewsCount(this).toInt() + Settings.getKittensBeforeAskingToRate(this))
        }
        Settings.setCurrentVersion(this, BuildConfig.VERSION_CODE)
    }
}
