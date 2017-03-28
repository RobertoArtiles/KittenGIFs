package com.the_roberto.kittengifs.kittens

import android.content.Context
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.the_roberto.kittengifs.dagger.ForApplication
import com.the_roberto.kittengifs.model.Settings
import com.the_roberto.kittengifs.model.event.KittenFailedEvent
import com.the_roberto.kittengifs.model.event.KittenLoadedEvent
import com.the_roberto.kittengifs.model.kittens.KittensManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class KittensPresenter @Inject constructor(@ForApplication val context: Context,
                                           val eventBus: EventBus,
                                           val settings: Settings,
                                           val kittensManager: KittensManager): MvpBasePresenter<KittensView>() {

    override fun attachView(attachedView: KittensView?) {
        super.attachView(attachedView)
        eventBus.register(this)

        showViewCount()

        val lastOpenedGif = settings.getLastOpenedKitten()
        if (lastOpenedGif != null) {
            view?.showKitten(lastOpenedGif)
        } else {
            loadNextKitten()
        }

    }

    override fun detachView(retainInstance: Boolean) {
        super.detachView(retainInstance)
        eventBus.unregister(this)
    }

    fun loadNextKitten() {
        if (settings.getViewsCount() + 1 >= settings.getKittensBeforeAskingToRate()) {
            view?.showRatingDialog()
        } else {
            view?.showLoading()
            kittensManager.loadNextKitten()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: KittenLoadedEvent) {
        view?.showKitten(event.imageUrlMp4)
        incrementViewCount()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: KittenFailedEvent) {
        view?.showError()
    }

    private fun incrementViewCount() {
        var viewCount = settings.getViewsCount()
        settings.setViewsCount(++viewCount)
        showViewCount()
    }

    private fun showViewCount() {
        val viewCount = settings.getViewsCount()
        view?.updateViewCount(viewCount)
    }

}
