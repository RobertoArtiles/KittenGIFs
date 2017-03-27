package com.the_roberto.kittengifs.kittens

import com.hannesdorfmann.mosby3.mvp.MvpView

interface KittensView : MvpView {
    fun showKitten(url: String)
    fun showError()
    fun showLoading()
    fun updateViewCount(count: Long)
    fun showRatingDialog()
}