package com.the_roberto.kittengifs

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import butterknife.bindView
import com.the_roberto.kittengifs.event.GifFetchFailedEvent
import com.the_roberto.kittengifs.event.NewGifArrivedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class KittenActivity : AppCompatActivity() {
    val TAG = "KittenActivity"
    val videoView: TextureVideoView by bindView(R.id.video_view)
    val progressBar: View by bindView(R.id.progress_bar)
    val counterView: CounterView by bindView(R.id.counter)
    val container: View by bindView(R.id.container)
    private val eventBus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitten)
        eventBus.register(this)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = null
        }
        val lastOpenedGif = Settings.getLastOpenedKitten(this)
        if (lastOpenedGif != null) {
            showVideo(lastOpenedGif)
        } else {
            nextKitten()
        }

        container.setOnClickListener {
            nextKitten()
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
                val lastGifUrl = Settings.getKittenToShareUrl(this)
                if (lastGifUrl != null) {
                    val intent = Intent()
                    intent.setAction(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, "Check out this kitty :) $lastGifUrl \nFound via http://goo.gl/Rio4Ji")
                    intent.setType("text/plain")
                    startActivity(intent)
                    EventsTracker.trackShare()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun nextKitten() {
        if (counterView.count + 1 >= Settings.getKittensBeforeAskingToRate(this)) {
            supportFragmentManager.beginTransaction().add(RatingFragment(), "rating_fragment").commitAllowingStateLoss()
        } else {
            GifsController.nextKitten()
            setProgressBarEnabled(true)
            counterView.increment()
        }
        EventsTracker.trackNextKitten()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    @Subscribe
    fun onEventMainThread(event: NewGifArrivedEvent) {
        Log.d(TAG, "onEventMainThread(NewGifArrivedEvent)")
        val imageUrlMp4 = event.imageUrlMp4
        if (imageUrlMp4 != null) {
            showVideo(imageUrlMp4)
        }
    }

    @Subscribe
    fun onEventMainThread(event: GifFetchFailedEvent) {
        setProgressBarEnabled(false)
    }

    private fun showVideo(url: String) {
        Log.d(TAG, "showVideo: $url")
        videoView.setVideoURI(Uri.parse(url))
        videoView.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: MediaPlayer) {
                Log.d(TAG, "onPrepared")
                mp.isLooping = true
                videoView.start()
                setProgressBarEnabled(false)
                EventsTracker.trackSuccessfulKitten()
            }
        })
        videoView.setOnErrorListener(object : MediaPlayer.OnErrorListener {

            override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
                Log.d(TAG, "onError")
                setProgressBarEnabled(false)
                Toast.makeText(applicationContext, getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show()
                EventsTracker.trackFailedKitten()
                return true
            }
        })
    }

    private fun setProgressBarEnabled(enabled: Boolean) {
        progressBar.visibility = if (enabled) View.VISIBLE else View.GONE
    }
}
