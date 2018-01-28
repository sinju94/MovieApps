package com.iak.sinju.movieapps;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iak.sinju.movieapps.util.retrofit.RetrofitApi;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class App extends Application {
    private static App instance;
    private Retrofit retrofit;
    private EventBus eventBus;
    private Gson gson;

    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createEventBus();
        createRetrofitClient();
        createGson();
    }

    private void createGson() {
        gson = new GsonBuilder().create();
    }

    private void createEventBus() {
        eventBus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .build();
    }

    private void createRetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Retrofit getRetrofitClient() {
        return retrofit;
    }

    public Gson getGson() {
        return gson;
    }

    public RetrofitApi getApiService() {
        return getRetrofitClient().create(RetrofitApi.class);
    }
}