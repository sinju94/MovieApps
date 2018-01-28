package com.iak.sinju.movieapps.event;

import com.iak.sinju.movieapps.model.ReviewResponse;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class MovieReviewEvent extends BaseEvent {
    private ReviewResponse body;

    public MovieReviewEvent(String message, ReviewResponse body) {
        super(message);
        this.body = body;
    }

    public ReviewResponse getBody() {
        return body;
    }
}