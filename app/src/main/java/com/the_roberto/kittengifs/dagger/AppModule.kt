package com.the_roberto.kittengifs.dagger

import android.app.Application
import android.content.Context
import com.the_roberto.kittengifs.IntentStarter
import com.the_roberto.kittengifs.model.Settings
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

@Module
class AppModule(val app: Application) {
    @Provides @Singleton fun provideEventBus(): EventBus = EventBus.getDefault()
    @Provides @Singleton fun provideSettings(@ForApplication context: Context) = Settings(context)
    @Provides @Singleton @ForApplication fun provideApplicationContext(): Context = app.applicationContext
    @Provides @Singleton fun provideIntentStarter(settings: Settings) = IntentStarter(settings)

}