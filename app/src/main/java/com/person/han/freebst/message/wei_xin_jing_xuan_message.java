package com.person.han.freebst.message;

import android.graphics.Bitmap;

/**
 * Created by han on 2016/11/6.
 */

public class wei_xin_jing_xuan_message {
    private String firstImg;
    private String id;
    private String source;
    private String title;
    private String url;
    private String mark;
    private Bitmap bitmap=null;
    private Boolean isGetBitmap=false;

    public wei_xin_jing_xuan_message(String mark, String firstImg, String id, String source, String title, String url) {
        this.mark = mark;
        this.firstImg = firstImg;
        this.id = id;
        this.source = source;
        this.title = title;
        this.url = url;
    }

    public String getFirstImg() {
        return firstImg;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getMark() {
        return mark;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Boolean getGetBitmap() {
        return isGetBitmap;
    }

    public void setGetBitmap(Boolean getBitmap) {
        isGetBitmap = getBitmap;
    }
}
