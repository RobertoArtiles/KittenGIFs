package com.the_roberto.kittengifs;

import android.content.Context;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Toast;

import com.the_roberto.kittengifs.event.GifFetchFailedEvent;
import com.the_roberto.kittengifs.event.NewGifArrivedEvent;
import com.the_roberto.kittengifs.giphy.GiphyService;
import com.the_roberto.kittengifs.giphy.SearchGifResponse;

import java.util.Random;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

import static com.the_roberto.kittengifs.Config.GIPHY_API_KEY;

public class GifsController {
    private static GifsController INSTANCE;
    private final Context context;
    private final GiphyService service;
    private final EventBus eventBus;
    private Toast toast;
    private String secret;
    private Random random = new Random();

    public static void init(Context context, EventBus eventBus) {
        INSTANCE = new GifsController(context, eventBus);
    }

    public static GifsController getInstance() {
        return INSTANCE;
    }

    public GifsController(Context context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.giphy.com/v1")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (secret == null) {
                            secret = new String(Base64.decode(GIPHY_API_KEY, Base64.DEFAULT));
                        }
                        request.addQueryParam("api_key", secret);
                        request.addQueryParam("rating", "g");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("RETROFIT"))
                .build();

        service = restAdapter.create(GiphyService.class);
    }

    public void nextGif() {
        service.getGifs("kitten", random.nextInt(Settings.getMaxOffset(context)), 1, new Callback<SearchGifResponse>() {
            @Override
            public void success(SearchGifResponse searchGifResponse, Response response) {
                if (toast != null) {
                    toast.cancel();
                }
                if (searchGifResponse.data != null && searchGifResponse.data.length > 0) {
                    SearchGifResponse.Data.Images.Gif original = searchGifResponse.data[0].images.original;
                    eventBus.post(new NewGifArrivedEvent(original.url, original.mp4));
                    Settings.setLastOpenedGif(context, original.url);
                } else {
                    toast = Toast.makeText(context, "Meow! Something went wrong. Try again.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                Settings.setMaxOffset(context, searchGifResponse.pagination.totalCount);
            }

            @Override
            public void failure(RetrofitError error) {
                if (toast != null) {
                    toast.cancel();
                }
                switch (error.getKind()) {
                    case NETWORK:
                        toast = Toast.makeText(context, "Meow! Check your Internet connection.", Toast.LENGTH_SHORT);
                        break;
                    default:
                        toast = Toast.makeText(context, "Meow! Something went wrong. Try again.", Toast.LENGTH_SHORT);
                }
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                EventsTracker.getInstance().trackFailedKitten();
                eventBus.post(new GifFetchFailedEvent());
            }
        });
    }

}
