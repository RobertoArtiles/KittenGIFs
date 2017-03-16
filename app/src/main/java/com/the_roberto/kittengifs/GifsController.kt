package com.the_roberto.kittengifs

import android.content.Context
import android.util.Base64
import android.view.Gravity
import android.widget.Toast
import com.the_roberto.kittengifs.event.GifFetchFailedEvent
import com.the_roberto.kittengifs.event.NewGifArrivedEvent
import com.the_roberto.kittengifs.giphy.GiphyService
import com.the_roberto.kittengifs.giphy.SearchGifResponse
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

object GifsController {
    private lateinit var service: GiphyService
    private lateinit var context: Context
    private var secret: String? = null
    private var toast: Toast? = null
    private val random = Random()
    private val eventBus: EventBus = EventBus.getDefault()


    fun init(context: Context) {
        GifsController.context = context

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

    fun nextKitten() {
        service.getGifs("kitten", random.nextInt(Math.max(1, Settings.getMaxOffset(context))).toLong(), 1)
                .enqueue(object : Callback<SearchGifResponse> {
                    override fun onResponse(call: Call<SearchGifResponse>, response: retrofit2.Response<SearchGifResponse>) {
                        toast?.cancel()
                        if (response.isSuccessful) {
                            val original = response.body().data?.get(0)?.images?.original
                            eventBus.post(NewGifArrivedEvent(original?.url, original?.mp4))
                            Settings.setLastOpenedKitten(context, original?.mp4)
                            Settings.setKittenToShareUrl(context, original?.url)
                            Settings.setMaxOffset(context, response.body().pagination?.totalCount)
                        } else {
                            toast = Toast.makeText(context, "Meow! Something went wrong. Try again.", Toast.LENGTH_SHORT)
                            toast?.setGravity(Gravity.CENTER, 0, 0)
                            toast?.show()
                        }
                    }

                    override fun onFailure(call: Call<SearchGifResponse>, error: Throwable) {
                        toast?.cancel()
                        when (error) {
                            is IOException -> toast = Toast.makeText(context, "Meow! Check your Internet connection.", Toast.LENGTH_SHORT)
                            else -> toast = Toast.makeText(context, "Meow! Something went wrong. Try again.", Toast.LENGTH_SHORT)
                        }
                        toast?.apply {
                            setGravity(Gravity.CENTER, 0, 0)
                            show()
                        }

                        EventsTracker.trackFailedKitten()
                        eventBus.post(GifFetchFailedEvent())
                    }

                })
    }
}
