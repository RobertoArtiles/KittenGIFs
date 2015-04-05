package com.the_roberto.kittengifs.event;

public class NewGifArrivedEvent {
    public String imageUrl;
    public String imageUrlMp4;

    public NewGifArrivedEvent(String imageUrl, String imageUrlMp4) {
        this.imageUrl = imageUrl;
        this.imageUrlMp4 = imageUrlMp4;
    }
}
