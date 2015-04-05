package com.the_roberto.kittengifs;

import android.content.Context;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Toast;

import com.the_roberto.kittengifs.event.NewGifArrivedEvent;
import com.the_roberto.kittengifs.giphy.GiphyService;
import com.the_roberto.kittengifs.giphy.RandomGifResponse;

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
    private EventsTracker eventsTracker = EventsTracker.getInstance();

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
        service.getRandomGif("cat", new Callback<RandomGifResponse>() {

            @Override
            public void success(RandomGifResponse randomGifResponse, Response response) {
                if (toast != null) {
                    toast.cancel();
                }
                eventBus.post(new NewGifArrivedEvent(randomGifResponse.data.imageUrl, randomGifResponse.data.imageUrlMp4));
                Settings.setLastOpenedGif(context, randomGifResponse.data.imageUrl);
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
                eventsTracker.trackFailedKitten();
            }
        });
    }
}
