package com.the_roberto.kittengifs;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.the_roberto.kittengifs.event.GifFetchFailedEvent;
import com.the_roberto.kittengifs.event.NewGifArrivedEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class KittenActivity extends ActionBarActivity {

    @InjectView(R.id.gif) ImageView gifView;
    @InjectView(R.id.video_view) VideoView videoView;
    @InjectView(R.id.progress_bar) View progressBar;
    @InjectView(R.id.counter) CounterView counterView;
    private EventBus eventBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitten);
        ButterKnife.inject(this);
        eventBus.register(this);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            private int errorCount;

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (++errorCount == 2) {
                    Settings.setContentType(getApplicationContext(), Settings.ContentType.GIF);
                }
                nextGif();
                return true;
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
        }
        String lastOpenedGif = Settings.getLastOpenedGif(this);
        if (lastOpenedGif != null) {
            showGif(lastOpenedGif);
        } else {
            nextGif();
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
                String lastGifUrl = Settings.getLastOpenedGif(this);
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
    void nextGif() {
        if (counterView.getCount() + 1 >= Settings.getKittensBeforeAskingToRate(this)) {
            getSupportFragmentManager().beginTransaction().add(new RatingFragment(), "rating_fragment").commitAllowingStateLoss();
        } else {
            GifsController.getInstance().nextGif();
            progressBar.setVisibility(View.VISIBLE);
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
                showGif(event.imageUrl);
                break;
        }
    }

    public void onEventMainThread(GifFetchFailedEvent event) {
        setProgressBarEnabled(false);
    }

    private void showGif(String url) {
        videoView.setVisibility(View.GONE);
        gifView.setVisibility(View.VISIBLE);
        Glide.with(this).load(url)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .crossFade()
                .listener(new RequestListener<String, GifDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                        setProgressBarEnabled(false);
                        Toast.makeText(getApplicationContext(), "Meow! Check your Internet connection.", Toast.LENGTH_SHORT).show();
                        EventsTracker.getInstance().trackFailedKitten();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        setProgressBarEnabled(false);
                        EventsTracker.getInstance().trackSuccessfulKitten();
                        return false;
                    }
                })
                .into(gifView);

    }

    private void showVideo(String url) {
        gifView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse("http://s3.amazonaws.com/giphygifs/media/dYO5GaHYm4PrG/giphy.mp4"));
        videoView.start();
    }

    private void setProgressBarEnabled(boolean b) {
        progressBar.setVisibility(View.GONE);
    }
}
