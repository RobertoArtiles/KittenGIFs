package com.the_roberto.kittengifs.giphy

import retrofit.Callback
import retrofit.http.GET
import retrofit.http.Query

interface GiphyService {

    @GET("/gifs/random")
    fun getRandomGif(@Query("tag") tag: String, callback: Callback<RandomGifResponse>)

    @GET("/gifs/search")
    fun getGifs(@Query("q") query: String, @Query("offset") offset: Long, @Query("limit") limit: Long, callback: Callback<SearchGifResponse>)
}
