package com.the_roberto.kittengifs.kittens

import android.content.Context
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.the_roberto.kittengifs.App
import com.the_roberto.kittengifs.Settings
import com.the_roberto.kittengifs.model.event.KittenFailedEvent
import com.the_roberto.kittengifs.model.event.KittenLoadedEvent
import com.the_roberto.kittengifs.model.kittens.KittensManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class KittensPresenter : MvpBasePresenter<KittensView>() {
    private val eventBus = EventBus.getDefault()
    private var context: Context = App.context
    private val kittensManager: KittensManager = KittensManager(App.context)

    override fun attachView(attachedView: KittensView?) {
        super.attachView(attachedView)
        eventBus.register(this)

        showViewCount()

        val lastOpenedGif = Settings.getLastOpenedKitten(context)
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
        if (Settings.getViewsCount(context) + 1 >= Settings.getKittensBeforeAskingToRate(context)) {
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
        var viewCount = Settings.getViewsCount(context)
        Settings.setViewsCount(context, ++viewCount)
        showViewCount()
    }

    private fun showViewCount() {
        val viewCount = Settings.getViewsCount(context)
        view?.updateViewCount(viewCount)
    }

}
