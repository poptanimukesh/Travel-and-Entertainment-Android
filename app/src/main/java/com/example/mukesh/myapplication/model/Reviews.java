package com.example.mukesh.myapplication.model;

import java.util.Date;

/**
 * Created by mukesh on 4/14/18.
 */

public class Reviews {
    private String name;
    private String photoUrl;
    private float rating;
    private String comment;
    private Date date;
    private String url;

    public Reviews(String name, String photoUrl, float rating, String comment, Date date, String url) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
