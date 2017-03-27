package com.the_roberto.kittengifs.kittens

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import butterknife.bindView
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.the_roberto.kittengifs.EventsTracker
import com.the_roberto.kittengifs.R
import com.the_roberto.kittengifs.shareKitten
import com.the_roberto.kittengifs.ui.TextureVideoView

class KittensActivity : MvpActivity<KittensView, KittensPresenter>(), KittensView {
    val TAG = "KittensActivity2"

    val container: View by bindView(R.id.container)

    val progressBar: View by bindView(R.id.progress_bar)
    val counterView: TextView by bindView(R.id.counter)
    val videoView: TextureVideoView by bindView(R.id.video_view)
    var errorToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_kitten)
        super.onCreate(savedInstanceState)

        supportActionBar?.title = null

        container.setOnClickListener {
            if (progressBar.visibility != View.VISIBLE) {
                presenter.loadNextKitten()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_kitten, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                shareKitten()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun createPresenter() = KittensPresenter()

    override fun showKitten(url: String) {
        playVideo(url)
    }

    override fun showError() {
        setProgressBarVisible(false)
        showErrorToast()
    }

    override fun showLoading() {
        setProgressBarVisible(true)
    }

    override fun updateViewCount(count: Long) {
        counterView.text = count.toString()
    }

    override fun showRatingDialog() {
        supportFragmentManager.beginTransaction()
                .add(RatingDialog(), "rating_fragment")
                .commitAllowingStateLoss()
    }

    private fun playVideo(url: String) {
        Log.d(TAG, "showVideo: $url")
        videoView.setVideoURI(Uri.parse(url))
        videoView.setOnPreparedListener { mp ->
            Log.d(TAG, "onPrepared")
            mp.isLooping = true
            videoView.start()
            setProgressBarVisible(false)
            EventsTracker.trackSuccessfulKitten()
        }
        videoView.setOnErrorListener { _, _, _ ->
            Log.d(TAG, "onError")
            setProgressBarVisible(false)
            showErrorToast()
            EventsTracker.trackFailedKitten()
            true
        }
    }

    fun showErrorToast() {
        errorToast?.cancel()
        errorToast = Toast.makeText(applicationContext, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT)
        errorToast?.show()
    }

    fun setProgressBarVisible(visible: Boolean) {
        progressBar.visibility = if (visible) View.VISIBLE else View.GONE
    }

}
