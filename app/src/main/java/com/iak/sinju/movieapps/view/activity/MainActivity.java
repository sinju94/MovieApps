package com.iak.sinju.movieapps.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.iak.sinju.movieapps.App;
import com.iak.sinju.movieapps.R;
import com.iak.sinju.movieapps.controller.MovieController;
import com.iak.sinju.movieapps.event.MovieErrorEvent;
import com.iak.sinju.movieapps.event.MovieEvent;
import com.iak.sinju.movieapps.model.Movie;
import com.iak.sinju.movieapps.util.Constant;
import com.iak.sinju.movieapps.view.adapter.MoviesAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rv_main_movies)
    RecyclerView mMainMovies;

    @BindView(R.id.pb_loading_bar)
    ProgressBar loadingBar;

    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    private EventBus mEventBus;
    private MoviesAdapter mMoviesAdapter;
    private MovieController mMovieController;
    private int mPage;
    private Constant.Data.MOVIE_LIST_TITLE movieListTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEventBus = App.getInstance().getEventBus();
        mEventBus.register(this);

        mMovieController = new MovieController();

        initView();

        if (savedInstanceState != null) {
            List<Movie> movieList = Arrays.asList(App.getInstance().getGson().fromJson(savedInstanceState.getString(Constant.Data.EXTRA_MOVIE_LIST), Movie[].class));
            mMoviesAdapter.setData(movieList);

            MainActivity.this.setTitle(savedInstanceState.getString(Constant.Data.EXTRA_TITLE));
            mMainMovies.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(Constant.Data.EXTRA_MOVIE_LIST_STATE));
            mPage = savedInstanceState.getInt(Constant.Data.EXTRA_PAGE);
            return;
        }

        errorLayout.setVisibility(View.GONE);
        loadingBar.setVisibility(View.VISIBLE);

        mPage = 1;
        setPopularMovies(mPage);
    }

    private void initView() {
        ButterKnife.bind(this);

        int columns = Constant.Function.getColumnsCount(this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, columns);
        mMainMovies.setLayoutManager(layoutManager);
        mMainMovies.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter();
        mMainMovies.setAdapter(mMoviesAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_popular:
                errorLayout.setVisibility(View.GONE);
                mMoviesAdapter.setData(new ArrayList<Movie>());
                loadingBar.setVisibility(View.VISIBLE);
                mPage = 1;
                setPopularMovies(mPage);
                break;
            case R.id.action_top_rated:
                errorLayout.setVisibility(View.GONE);
                mMoviesAdapter.setData(new ArrayList<Movie>());
                loadingBar.setVisibility(View.VISIBLE);
                mPage = 1;
                setTopRatedMovies(mPage);
                break;
            case R.id.action_favorite:
                errorLayout.setVisibility(View.GONE);
                mMoviesAdapter.setData(new ArrayList<Movie>());
                loadingBar.setVisibility(View.VISIBLE);
                mPage = 1;
                setFavoriteMovies();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFavoriteMovies() {
        MainActivity.this.setTitle("Favorite");

        this.movieListTitle = Constant.Data.MOVIE_LIST_TITLE.FAVORITE;
        mMovieController.getFavoriteMovies(this);
    }

    private void setTopRatedMovies(int page) {
        MainActivity.this.setTitle("Highest Rated");

        this.mPage = page;
        this.movieListTitle = Constant.Data.MOVIE_LIST_TITLE.TOP_RATED;

        mMovieController.getTopRatedMovies(page);
    }

    private void setPopularMovies(int page) {
        MainActivity.this.setTitle("Most Papular");

        this.mPage = page;
        this.movieListTitle = Constant.Data.MOVIE_LIST_TITLE.POPULAR;

        mMovieController.getPopularMovies(page);
    }

    @Override
    protected void onDestroy() {
        mEventBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMovieList(MovieEvent event) {
        if (event.getMovieListTitle().equals(movieListTitle)) {
            loadingBar.setVisibility(View.GONE);
            mMoviesAdapter.setData(event.getResults());
            mMainMovies.scrollToPosition(0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMovieListError(MovieErrorEvent event) {
        loadingBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        Log.e("ErrorResultData", event.getMessage());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        List<Movie> movieList = mMoviesAdapter.getData();
        String movieListJson = App.getInstance().getGson().toJson(movieList);
        outState.putString(Constant.Data.EXTRA_MOVIE_LIST, movieListJson);
        outState.putInt(Constant.Data.EXTRA_PAGE, mPage);
        outState.putString(Constant.Data.EXTRA_TITLE, getTitle().toString());
        outState.putParcelable(Constant.Data.EXTRA_MOVIE_LIST_STATE, mMainMovies.getLayoutManager().onSaveInstanceState());
    }
}