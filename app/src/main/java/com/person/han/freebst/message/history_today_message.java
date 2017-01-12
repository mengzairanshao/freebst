package com.person.han.freebst.message;

/**
 * Created by han on 2016/9/30.
 */

public class history_today_message {
    private String date;
    private String title;
    private String id;

    public history_today_message(String date, String title,String id) {
        this.date = date;
        this.title = title;
        this.id=id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}
