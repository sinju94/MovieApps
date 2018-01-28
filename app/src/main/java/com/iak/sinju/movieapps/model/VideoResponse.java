package com.iak.sinju.movieapps.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class VideoResponse {
    @SerializedName("id")
    private int movieId;

    @SerializedName("results")
    private List<Video> results;

    public int getMovieId() {
        return movieId;
    }

    public List<Video> getResults() {
        return results;
    }
}
