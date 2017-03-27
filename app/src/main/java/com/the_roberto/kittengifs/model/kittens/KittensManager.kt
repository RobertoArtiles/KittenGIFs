package com.the_roberto.kittengifs.model.kittens

import android.content.Context
import android.util.Base64
import com.the_roberto.kittengifs.Config
import com.the_roberto.kittengifs.EventsTracker
import com.the_roberto.kittengifs.Settings
import com.the_roberto.kittengifs.giphy.GiphyService
import com.the_roberto.kittengifs.giphy.SearchGifResponse
import com.the_roberto.kittengifs.model.event.KittenFailedEvent
import com.the_roberto.kittengifs.model.event.KittenLoadedEvent
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class KittensManager(val context: Context) {

    private var service: GiphyService
    private val random = Random()
    private var secret: String? = null
    private val eventBus = EventBus.getDefault()

    init {
        val client = OkHttpClient.Builder().addNetworkInterceptor { chain ->
            if (secret == null) {
                secret = String(Base64.decode(Config.GIPHY_API_KEY, Base64.DEFAULT))
            }
            val request = chain.request()
            val url = request.url()
                    .newBuilder()
                    .addQueryParameter("api_key", secret)
                    .addQueryParameter("rating", "g")
                    .build()
            val newRequest = request.newBuilder().url(url).build()

            return@addNetworkInterceptor chain.proceed(newRequest)
        }.build()

        val restAdapter = Retrofit.Builder()
                .client(client)
                .baseUrl("http://api.giphy.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = restAdapter.create(GiphyService::class.java)
    }

    fun loadNextKitten() {
        service.getGifs("kitten", random.nextInt(Math.max(1, Settings.getMaxOffset(context))).toLong(), 1)
                .enqueue(object : Callback<SearchGifResponse> {
                    override fun onResponse(call: Call<SearchGifResponse>, response: Response<SearchGifResponse>) {
                        if (response.isSuccessful) {
                            val original = response.body().data?.get(0)?.images?.original!!
                            eventBus.post(KittenLoadedEvent(original.url!!, original.mp4!!))
                            Settings.setLastOpenedKitten(context, original.mp4)
                            Settings.setKittenToShareUrl(context, original.url)
                            Settings.setMaxOffset(context, response.body().pagination?.totalCount)
                        } else {
                            eventBus.post(KittenFailedEvent())
                        }
                    }

                    override fun onFailure(call: Call<SearchGifResponse>, error: Throwable) {
                        EventsTracker.trackFailedKitten()
                        eventBus.post(KittenFailedEvent())
                    }

                })
    }
}