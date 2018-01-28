package com.iak.sinju.movieapps.event;

import com.iak.sinju.movieapps.model.VideoResponse;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class MovieTrailerEvent extends BaseEvent {
    private VideoResponse body;

    public MovieTrailerEvent(String message, VideoResponse body) {
        super(message);
        this.body = body;
    }

    public VideoResponse getBody() {
        return body;
    }
}