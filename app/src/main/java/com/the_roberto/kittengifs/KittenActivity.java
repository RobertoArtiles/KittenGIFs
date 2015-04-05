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
import com.the_roberto.kittengifs.event.NewGifArrivedEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class KittenActivity extends ActionBarActivity {

    @InjectView(R.id.gif) ImageView gifView;
    @InjectView(R.id.video_view) VideoView videoView;
    @InjectView(R.id.progress_bar) View progressBar;
    private EventBus eventBus = EventBus.getDefault();
    private GifsController gifsController = GifsController.getInstance();
    private String lastGifUrl;

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
        nextGif();
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
                if (lastGifUrl != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Checkout this kitty :) " + lastGifUrl);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(lastGifUrl));
                    startActivity(intent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.container})
    void nextGif() {
        gifsController.nextGif();
        progressBar.setVisibility(View.VISIBLE);
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

    private void showGif(String url) {
        lastGifUrl = url;
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
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
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
