package com.the_roberto.kittengifs;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.the_roberto.kittengifs.event.GifFetchFailedEvent;
import com.the_roberto.kittengifs.event.NewGifArrivedEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class KittenActivity extends AppCompatActivity {

    @Bind(R.id.video_view) TextureVideoView videoView;
    @Bind(R.id.progress_bar) View progressBar;
    @Bind(R.id.counter) CounterView counterView;
    private EventBus eventBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitten);
        ButterKnife.bind(this);
        eventBus.register(this);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
        }
        String lastOpenedGif = Settings.getLastOpenedKitten(this);
        if (lastOpenedGif != null) {
            showVideo(lastOpenedGif);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_kitten, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                String lastGifUrl = Settings.getLastOpenedKitten(this);
                if (lastGifUrl != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Check out this kitty :) " + lastGifUrl + " \nFound via http://goo.gl/Rio4Ji");
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(lastGifUrl));
                    startActivity(intent);
                    EventsTracker.getInstance().trackShare();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.container})
    void nextKitten() {
        if (counterView.getCount() + 1 >= Settings.getKittensBeforeAskingToRate(this)) {
            getSupportFragmentManager().beginTransaction().add(new RatingFragment(), "rating_fragment").commitAllowingStateLoss();
        } else {
            GifsController.getInstance().nextKitten();
            setProgressBarEnabled(true);
            counterView.increment();
        }
        EventsTracker.getInstance().trackNextKitten();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    public void onEventMainThread(NewGifArrivedEvent event) {
        switch (Settings.getContentType(this)) {
            case MP4:
                showVideo(event.imageUrlMp4);
                break;
            case GIF:
                // not implemented
                break;
        }
    }

    public void onEventMainThread(GifFetchFailedEvent event) {
        setProgressBarEnabled(false);
    }

    private void showVideo(String url) {
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse(url));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
                setProgressBarEnabled(false);
                EventsTracker.getInstance().trackSuccessfulKitten();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                setProgressBarEnabled(false);
                Toast.makeText(getApplicationContext(), getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
                EventsTracker.getInstance().trackFailedKitten();
                return true;
            }
        });
    }

    private void setProgressBarEnabled(boolean enabled) {
        progressBar.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }
}
