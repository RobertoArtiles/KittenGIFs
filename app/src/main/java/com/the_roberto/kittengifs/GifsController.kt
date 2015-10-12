package com.the_roberto.kittengifs

import android.content.Context
import android.util.Base64
import android.view.Gravity
import android.widget.Toast
import com.the_roberto.kittengifs.event.GifFetchFailedEvent
import com.the_roberto.kittengifs.event.NewGifArrivedEvent
import com.the_roberto.kittengifs.giphy.GiphyService
import com.the_roberto.kittengifs.giphy.SearchGifResponse
import de.greenrobot.event.EventBus
import retrofit.Callback
import retrofit.RequestInterceptor
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.android.AndroidLog
import retrofit.client.Response
import java.util.*

object GifsController {
    private lateinit var service: GiphyService
    private lateinit var context: Context
    private var secret: String? = null
    private var toast: Toast? = null
    private val random = Random()
    private val eventBus: EventBus = EventBus.getDefault()


    fun init(context: Context) {
        GifsController.context = context;
        val restAdapter = RestAdapter.Builder()
                .setEndpoint("http://api.giphy.com/v1")
                .setRequestInterceptor(object : RequestInterceptor {
                    override fun intercept(request: RequestInterceptor.RequestFacade) {
                        if (secret == null) {
                            secret = String(Base64.decode(Config.GIPHY_API_KEY, Base64.DEFAULT))
                        }
                        request.addQueryParam("api_key", secret)
                        request.addQueryParam("rating", "g")
                    }
                }).setLogLevel(RestAdapter.LogLevel.FULL).setLog(AndroidLog("RETROFIT")).build()

        service = restAdapter.create(GiphyService::class.java)
    }

    fun nextKitten() {
        service.getGifs("kitten", random.nextInt(Settings.getMaxOffset(context)).toLong(), 1, object : Callback<SearchGifResponse> {
            override fun success(searchGifResponse: SearchGifResponse, response: Response) {
                toast?.cancel()
                if (searchGifResponse.data != null && searchGifResponse.data!!.isNotEmpty()) {
                    var original = searchGifResponse.data?.get(0)?.images?.original
                    eventBus.post(NewGifArrivedEvent(original?.url, original?.mp4))
                    Settings.setLastOpenedKitten(context, original?.mp4)
                    Settings.setKittenToShareUrl(context, original?.url)
                } else {
                    toast = Toast.makeText(context, "Meow! Something went wrong. Try again.", Toast.LENGTH_SHORT)
                    toast!!.setGravity(Gravity.CENTER, 0, 0)
                    toast!!.show()
                }
                Settings.setMaxOffset(context, searchGifResponse.pagination?.totalCount)
            }

            override fun failure(error: RetrofitError) {
                if (toast != null) {
                    toast!!.cancel()
                }
                when (error.kind) {
                    RetrofitError.Kind.NETWORK -> toast = Toast.makeText(context, "Meow! Check your Internet connection.", Toast.LENGTH_SHORT)
                    else -> toast = Toast.makeText(context, "Meow! Something went wrong. Try again.", Toast.LENGTH_SHORT)
                }
                toast!!.setGravity(Gravity.CENTER, 0, 0)
                toast!!.show()
                EventsTracker.trackFailedKitten()
                eventBus.post(GifFetchFailedEvent())
            }
        })
    }
}
