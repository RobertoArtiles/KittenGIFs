package com.the_roberto.kittengifs.model.kittens

import android.content.Context
import com.the_roberto.kittengifs.EventsTracker
import com.the_roberto.kittengifs.R
import com.the_roberto.kittengifs.dagger.ForApplication
import com.the_roberto.kittengifs.giphy.GiphyService
import com.the_roberto.kittengifs.giphy.SearchGifResponse
import com.the_roberto.kittengifs.model.Settings
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KittensManager @Inject constructor(@ForApplication val context: Context, val settings: Settings, val eventBus: EventBus) {

    private var service: GiphyService
    private val random = Random()

    init {
        val client = OkHttpClient.Builder().addNetworkInterceptor { chain ->
            val request = chain.request()
            val url = request.url()
                    .newBuilder()
                    .addQueryParameter("api_key", context.getString(R.string.api_key))
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
        service.getGifs("kitten", random.nextInt(Math.max(1, settings.getMaxOffset())).toLong(), 1)
                .enqueue(object : Callback<SearchGifResponse> {
                    override fun onResponse(call: Call<SearchGifResponse>, response: Response<SearchGifResponse>) {
                        if (response.isSuccessful) {
                            val original = response.body().data?.get(0)?.images?.original!!
                            eventBus.post(KittenLoadedEvent(original.url!!, original.mp4!!))
                            settings.setLastOpenedKitten(original.mp4)
                            settings.setKittenToShareUrl(original.url)
                            settings.setMaxOffset(response.body().pagination?.totalCount)
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