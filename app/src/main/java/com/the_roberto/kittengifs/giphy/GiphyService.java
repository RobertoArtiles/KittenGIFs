package com.the_roberto.kittengifs.giphy;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GiphyService {

    @GET("/gifs/random")
    void getRandomGif(@Query("tag") String tag, Callback<RandomGifResponse> callback);

    @GET("/gifs/search")
    void getGifs(@Query("q") String query, @Query("offset") long offset, @Query("limit") long limit, Callback<SearchGifResponse> callback);
}
