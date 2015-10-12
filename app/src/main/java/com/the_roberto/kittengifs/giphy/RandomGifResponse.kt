package com.the_roberto.kittengifs.giphy

import com.google.gson.annotations.SerializedName

class RandomGifResponse() {
    var data: Data? = null

    class Data {
        @SerializedName("image_url") var imageUrl: String? = null
        @SerializedName("image_mp4_url") var imageUrlMp4: String? = null
    }
}
