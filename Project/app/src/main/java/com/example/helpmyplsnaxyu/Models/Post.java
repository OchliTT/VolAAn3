package com.example.helpmyplsnaxyu.Models;

import java.util.Date;

public class Post {
    private String type;
    private String text;
    private Date time;

    public Post() {
    }

    public Post(String type, String text, Date time) {
        this.type = type;
        this.text = text;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTime() {
        return time;
    }

    public void Date(Date time) {
        this.time = time;
    }
}
