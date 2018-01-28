package com.iak.sinju.movieapps.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class ReviewResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Review> results;

    public int getId() {
        return id;
    }

    public List<Review> getResults() {
        return results;
    }
}
