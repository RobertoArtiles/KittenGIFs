package com.the_roberto.kittengifs.giphy

import com.google.gson.annotations.SerializedName

class SearchGifResponse {
    @SerializedName("data") var data: Array<Data>? = null
    @SerializedName("pagination") var pagination: Pagination? = null

    class Data {
        @SerializedName("images") var images: Images? = null

        class Images {
            @SerializedName("original") var original: Gif? = null

            class Gif {
                @SerializedName("url") var url: String? = null
                @SerializedName("webp") var webp: String? = null
                @SerializedName("mp4") var mp4: String? = null
            }
        }
    }

    class Pagination {
        @SerializedName("total_count") var totalCount: Int = 0
    }
}
