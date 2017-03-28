package com.the_roberto.kittengifs.kittens

import com.the_roberto.kittengifs.dagger.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent()
interface KittensComponent {
    fun presenter(): KittensPresenter

    fun inject(kittensActivity: KittensActivity)
}