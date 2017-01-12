package com.person.han.freebst.message;


/**
 * Created by han on 2016/10/2.
 */

public class joke_message {
    private String jokeText;
    private String jokeImgText;
    private String jokeImgUrl;
    private Object jokeImg;
    private byte[] jokeImgByte;
    private int isGetImg=0;

    public joke_message(String jokeText) {
        this.jokeText = jokeText;
    }

    public joke_message(String jokeImgText, String jokeImgUrl) {
        this.jokeImgText = jokeImgText;
        this.jokeImgUrl = jokeImgUrl;
    }

    public String getJokeText() {
        return jokeText;
    }

    public String getJokeImgText() {
        return jokeImgText;
    }

    public String getJokeImgUrl() {
        return jokeImgUrl;
    }

    public void setJokeImg(Object jokeImg) {
        this.jokeImg = jokeImg;
    }

    public Object getJokeImg() {
        return jokeImg;
    }

    public byte[] getJokeImgByte() {
        return jokeImgByte;
    }

    public void setJokeImgByte(byte[] jokeImgByte) {
        this.jokeImgByte = jokeImgByte;
    }

    public int getIsGetImg() {
        return isGetImg;
    }

    public void setIsGetImg(int isGetImg) {
        this.isGetImg = isGetImg;
    }
}
