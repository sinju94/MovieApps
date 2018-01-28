package com.iak.sinju.movieapps.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.iak.sinju.movieapps.App;
import com.iak.sinju.movieapps.event.MovieErrorEvent;
import com.iak.sinju.movieapps.event.MovieEvent;
import com.iak.sinju.movieapps.event.MovieReviewErrorEvent;
import com.iak.sinju.movieapps.event.MovieReviewEvent;
import com.iak.sinju.movieapps.event.MovieTrailerErrorEvent;
import com.iak.sinju.movieapps.event.MovieTrailerEvent;
import com.iak.sinju.movieapps.model.Movie;
import com.iak.sinju.movieapps.model.MovieResponse;
import com.iak.sinju.movieapps.model.ReviewResponse;
import com.iak.sinju.movieapps.model.VideoResponse;
import com.iak.sinju.movieapps.util.Constant;
import com.iak.sinju.movieapps.util.db.MovieContract;
import com.iak.sinju.movieapps.util.retrofit.RetrofitApi;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alodokter on 27-Jan-18.
 */

public class MovieController {
    private EventBus eventBus = App.getInstance().getEventBus();
    private Constant.Data.MOVIE_LIST_TITLE movieListTitle;

    private void getMovies(int type, int page) {
        Call<MovieResponse> movieResponseCall = App.getInstance().getApiService().getMovies(Constant.Data.MOVIE_LIST_TYPE[type], RetrofitApi.API_KEY, RetrofitApi.LANG_SOURCE, page, RetrofitApi.MOVIES_REGION);
        movieResponseCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.code() == 200) {
                    eventBus.post(new MovieEvent(response.message(), response.body().getResults(), movieListTitle));
                } else {
                    eventBus.post(new MovieErrorEvent(response.message()));
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                eventBus.post(new MovieErrorEvent(t.getMessage()));
            }
        });
    }

    public void getPopularMovies(int page) {
        movieListTitle = Constant.Data.MOVIE_LIST_TITLE.POPULAR;
        getMovies(0, page);
    }

    public void getTopRatedMovies(int page) {
        movieListTitle = Constant.Data.MOVIE_LIST_TITLE.TOP_RATED;
        getMovies(1, page);
    }

    public void getMovieTrailers(String movieId) {
        Call<VideoResponse> videoResponseCall = App.getInstance().getApiService().getTrailers(movieId, RetrofitApi.API_KEY, RetrofitApi.LANG_SOURCE);
        videoResponseCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.code() == 200) {
                    eventBus.post(new MovieTrailerEvent(response.message(), response.body()));
                } else {
                    eventBus.post(new MovieTrailerErrorEvent(response.message()));
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                eventBus.post(new MovieTrailerErrorEvent(t.getMessage()));
            }
        });
    }

    public void getMovieReviews(String movieId, int page) {
        Call<ReviewResponse> reviewResponseCall = App.getInstance().getApiService().getReviews(movieId, RetrofitApi.API_KEY, RetrofitApi.LANG_SOURCE, page);
        reviewResponseCall.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.code() == 200) {
                    eventBus.post(new MovieReviewEvent(response.message(), response.body()));
                } else {
                    eventBus.post(new MovieReviewErrorEvent(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                eventBus.post(new MovieReviewErrorEvent(t.getMessage()));
            }
        });
    }

    public void getFavoriteMovies(Context context) {
        movieListTitle = Constant.Data.MOVIE_LIST_TITLE.FAVORITE;
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        List<Movie> mMovieFavorites = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Movie movie = new Movie();
                movie.setId(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
                mMovieFavorites.add(movie);
            }
            cursor.close();
        }

        eventBus.post(new MovieEvent(Constant.Data.MOVIE_FAVORITE_SUCCESS_MESSAGE, mMovieFavorites, movieListTitle));
    }

    public boolean addFavoriteMovie(Context context, Movie movie) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

        Uri uri = context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        getFavoriteMovies(context);

        return (uri != null);
    }

    public boolean removeFavoriteMovie(Context context, Movie movie) {
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)).equals(movie.getId())) {
                    String id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(id).build();
                    int itemDeleted = context.getContentResolver().delete(uri, null, null);

                    getFavoriteMovies(context);
                    return (itemDeleted != 0);
                }
            }
            cursor.close();
        }
        return false;
    }

    public boolean isFavoriteMovie(@NonNull Context context, Movie movie) {
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)).equals(movie.getId())) {
                    return true;
                }
            }
            cursor.close();
        }
        return false;
    }
}
