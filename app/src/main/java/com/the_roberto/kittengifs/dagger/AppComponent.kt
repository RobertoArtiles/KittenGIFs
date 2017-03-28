package com.the_roberto.kittengifs.dagger

import com.the_roberto.kittengifs.App
import com.the_roberto.kittengifs.kittens.KittensComponent
import com.the_roberto.kittengifs.kittens.RatingDialog
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(AppModule::class))
@Singleton
interface AppComponent {

    fun newKittensComponent(): KittensComponent

    fun inject(ratingDialog: RatingDialog)
    fun inject(app: App)
}