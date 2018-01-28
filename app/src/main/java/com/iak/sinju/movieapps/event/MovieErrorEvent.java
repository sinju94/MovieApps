package com.iak.sinju.movieapps.event;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class MovieErrorEvent extends BaseEvent {
    public MovieErrorEvent(String message) {
        super(message);
    }
}