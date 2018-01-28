package com.iak.sinju.movieapps.event;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class BaseEvent {
    private String message;

    BaseEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}