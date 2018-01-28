package com.iak.sinju.movieapps.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class MovieResponse {
    @SerializedName("results")
    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }
}