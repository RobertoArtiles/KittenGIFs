package com.the_roberto.kittengifs

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlin.properties.Delegates

class App : Application() {

    //remove this hack with Dagger
    companion object {
        var context: Context by Delegates.notNull()
    }

    init {
        context = this
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        EventsTracker.init(this)
        migrate()
    }

    private fun migrate() {
        val oldVersion = Settings.getCurrentVersion(this)
        if (oldVersion < 13) {
            Settings.setKittensBeforeAskingToRate(this, Settings.getViewsCount(this).toInt() + Settings.getKittensBeforeAskingToRate(this))
        }
        if (oldVersion < 14) {
            Settings.setLastOpenedKitten(this, null);
            Settings.setKittenToShareUrl(this, null);
        }
        Settings.setCurrentVersion(this, BuildConfig.VERSION_CODE)
    }
}
