package com.the_roberto.kittengifs.giphy

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyService {

    @GET("gifs/random")
    fun getRandomGif(@Query("tag") tag: String): Call<RandomGifResponse>

    @GET("gifs/search")
    fun getGifs(@Query("q") query: String, @Query("offset") offset: Long, @Query("limit") limit: Long): Call<SearchGifResponse>
}
