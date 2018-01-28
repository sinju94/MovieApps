package com.iak.sinju.movieapps.event;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class MovieReviewErrorEvent extends BaseEvent {
    public MovieReviewErrorEvent(String message) {
        super(message);
    }
}
