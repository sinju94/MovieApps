package com.iak.sinju.movieapps.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class Review {
    @SerializedName("id")
    private String reviewId;

    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    @SerializedName("url")
    private String url;

    public String getReviewId() {
        return reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}