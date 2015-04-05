package com.the_roberto.kittengifs.giphy;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GiphyService {

    @GET("/gifs/random")
    void getRandomGif(@Query("tag") String tag, Callback<RandomGifResponse> callback);
}
