package com.the_roberto.kittengifs.giphy;

import com.google.gson.annotations.SerializedName;

public class SearchGifResponse {
    @SerializedName("data") public Data[] data;
    @SerializedName("pagination") public Pagination pagination;

    public static class Data {
        @SerializedName("images") public Images images;

        public static class Images {
            @SerializedName("original") public Gif original;

            public static class Gif {
                @SerializedName("url") public String url;
                @SerializedName("webp") public String webp;
                @SerializedName("mp4") public String mp4;
            }
        }
    }

    public static class Pagination {
        @SerializedName("total_count") public int totalCount;
    }
}
