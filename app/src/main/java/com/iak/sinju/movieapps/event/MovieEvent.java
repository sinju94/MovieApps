package com.iak.sinju.movieapps.event;

import com.iak.sinju.movieapps.model.Movie;
import com.iak.sinju.movieapps.util.Constant;

import java.util.List;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class MovieEvent extends BaseEvent {
    private final List<Movie> results;
    private final Constant.Data.MOVIE_LIST_TITLE movieListTitle;

    public MovieEvent(String message, List<Movie> results, Constant.Data.MOVIE_LIST_TITLE movieListTitle) {
        super(message);
        this.results = results;
        this.movieListTitle = movieListTitle;
    }

    public List<Movie> getResults() {
        return results;
    }

    public Constant.Data.MOVIE_LIST_TITLE getMovieListTitle() {
        return movieListTitle;
    }
}