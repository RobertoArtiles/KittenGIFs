package com.the_roberto.kittengifs

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.the_roberto.kittengifs.dagger.AppComponent
import com.the_roberto.kittengifs.dagger.AppModule
import com.the_roberto.kittengifs.dagger.DaggerAppComponent
import com.the_roberto.kittengifs.model.Settings
import io.fabric.sdk.android.Fabric
import javax.inject.Inject
import kotlin.properties.Delegates

class App : Application() {

    @Inject lateinit var settings: Settings

    var appComponent: AppComponent by Delegates.notNull()

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        EventsTracker.init(this)

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        appComponent.inject(this)

        migrate()
    }

    private fun migrate() {
        val oldVersion = settings.getCurrentVersion()
        if (oldVersion < 13) {
            settings.setKittensBeforeAskingToRate(settings.getViewsCount().toInt() + settings.getKittensBeforeAskingToRate())
        }
        if (oldVersion < 14) {
            settings.setLastOpenedKitten(null)
            settings.setKittenToShareUrl(null)
        }
        settings.setCurrentVersion(BuildConfig.VERSION_CODE)
    }

}
