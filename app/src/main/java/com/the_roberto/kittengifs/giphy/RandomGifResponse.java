package com.the_roberto.kittengifs.giphy;

import com.google.gson.annotations.SerializedName;

public class RandomGifResponse {
    public Data data;

    public static class Data {
        @SerializedName("image_url") public String imageUrl;
        @SerializedName("image_mp4_url") public String imageUrlMp4;
    }
}
