package com.the_roberto.kittengifs;

import android.app.Application;

import de.greenrobot.event.EventBus;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GifsController.init(this, EventBus.getDefault());
    }
}
