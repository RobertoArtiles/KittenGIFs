package com.the_roberto.kittengifs;

import android.app.Application;

import de.greenrobot.event.EventBus;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        GifsController.init(this, EventBus.getDefault());
        EventsTracker.init(this);
    }
}
