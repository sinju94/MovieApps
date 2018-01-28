package com.iak.sinju.movieapps.event;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class MovieTrailerErrorEvent extends BaseEvent {
    public MovieTrailerErrorEvent(String message) {
        super(message);
    }
}