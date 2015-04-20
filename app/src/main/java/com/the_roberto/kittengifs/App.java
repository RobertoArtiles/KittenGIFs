package com.the_roberto.kittengifs;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;

import static com.the_roberto.kittengifs.Settings.getViewsCount;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        GifsController.init(this, EventBus.getDefault());
        EventsTracker.init(this);
        migrate();
    }

    private void migrate() {
        int oldVersion = Settings.getCurrentVersion(this);
        if (oldVersion < 13) {
            Settings.setKittensBeforeAskingToRate(this, (int) getViewsCount(this) + Settings.getKittensBeforeAskingToRate(this));
        }
        Settings.setCurrentVersion(this, BuildConfig.VERSION_CODE);
    }
}
