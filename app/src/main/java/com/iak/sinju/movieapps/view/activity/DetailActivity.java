package com.iak.sinju.movieapps.view.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iak.sinju.movieapps.App;
import com.iak.sinju.movieapps.R;
import com.iak.sinju.movieapps.controller.MovieController;
import com.iak.sinju.movieapps.event.MovieReviewErrorEvent;
import com.iak.sinju.movieapps.event.MovieReviewEvent;
import com.iak.sinju.movieapps.event.MovieTrailerErrorEvent;
import com.iak.sinju.movieapps.event.MovieTrailerEvent;
import com.iak.sinju.movieapps.model.Movie;
import com.iak.sinju.movieapps.model.Review;
import com.iak.sinju.movieapps.model.Video;
import com.iak.sinju.movieapps.util.Constant;
import com.iak.sinju.movieapps.util.retrofit.RetrofitApi;
import com.iak.sinju.movieapps.view.adapter.ReviewsAdapter;
import com.iak.sinju.movieapps.view.adapter.TrailersAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.detail_poster)
    ImageView mDetailPoster;

    @BindView(R.id.detail_title)
    TextView mDetailTitle;

    @BindView(R.id.detail_overview)
    TextView mDetailOverview;

    @BindView(R.id.detail_rating)
    TextView mDetailRating;

    @BindView(R.id.detail_release)
    TextView mDetailRelease;

    @BindView(R.id.detail_overview_title)
    TextView mDetailOverviewTitle;

    @BindView(R.id.detail_release_title)
    TextView mDetailReleaseTitle;

    @BindView(R.id.detail_review_list)
    RecyclerView mDetailReviewList;

    @BindView(R.id.detail_trailer_list)
    RecyclerView mDetailTrailerList;

    private MovieController mController;

    private EventBus mEventBus;
    private int mReviewPage = 1;

    private Movie mMovieDetail;

    private ReviewsAdapter mReviewsAdapter;
    private TrailersAdapter mTrailersAdapter;
    private String reviewJsonKey = "reviews-json";
    private String videosJsonKey = "videos-json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieDetail = App.getInstance().getGson().fromJson(this.getIntent().getStringExtra(Constant.Data.MOVIE_INTENT), Movie.class);

        initView();

        setTitle(mMovieDetail.getTitle());

        Constant.Function.setImageResource(this, RetrofitApi.BASE_URL_IMAGE + mMovieDetail.getPosterPath(), mDetailPoster);

        mDetailTitle.setText(mMovieDetail.getTitle());

        if (mMovieDetail.getOverview() != null && !mMovieDetail.getOverview().equals("")) {
            mDetailOverview.setText(mMovieDetail.getOverview());
            mDetailOverviewTitle.setVisibility(View.VISIBLE);
        } else {
            mDetailOverview.setText("");
            mDetailOverviewTitle.setVisibility(View.GONE);
        }

        String ratingStr = String.valueOf(mMovieDetail.getVoteAverage()) + " " + "of 10";
        mDetailRating.setText(ratingStr);

        try {
            mDetailRelease.setText(new SimpleDateFormat(Constant.Data.MOVIE_RELEASE_DATE_FORMAT_AFTER, Locale.getDefault()).format(new SimpleDateFormat(Constant.Data.MOVIE_RELEASE_DATE_FORMAT_BEFORE, Locale.getDefault()).parse(mMovieDetail.getReleaseDate())));
            mDetailReleaseTitle.setVisibility(View.VISIBLE);
        } catch (ParseException e) {
            e.printStackTrace();
            mDetailRelease.setText("");
            mDetailReleaseTitle.setVisibility(View.GONE);
        }

        RecyclerView.LayoutManager reviewsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mDetailReviewList.setLayoutManager(reviewsLayoutManager);
        mDetailReviewList.setHasFixedSize(true);

        mReviewsAdapter = new ReviewsAdapter();
        mDetailReviewList.setAdapter(mReviewsAdapter);

        ViewCompat.setNestedScrollingEnabled(mDetailReviewList, false);

        RecyclerView.LayoutManager trailersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mDetailTrailerList.setLayoutManager(trailersLayoutManager);
        mDetailTrailerList.setHasFixedSize(true);

        mTrailersAdapter = new TrailersAdapter();
        mDetailTrailerList.setAdapter(mTrailersAdapter);

        ViewCompat.setNestedScrollingEnabled(mDetailTrailerList, false);

        if (savedInstanceState != null) {
            String reviewJson = savedInstanceState.getString(reviewJsonKey);
            Review[] reviews = App.getInstance().getGson().fromJson(reviewJson, Review[].class);
            mReviewsAdapter.setData(Arrays.asList(reviews));

            String videosJson = savedInstanceState.getString(videosJsonKey);
            Video[] videos = App.getInstance().getGson().fromJson(videosJson, Video[].class);
            mTrailersAdapter.setData(Arrays.asList(videos));
        }

        mController = new MovieController();
        mController.getMovieReviews(mMovieDetail.getId(), mReviewPage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus = App.getInstance().getEventBus();
        mEventBus.register(this);
    }

    @Override
    protected void onPause() {
        mEventBus.unregister(this);
        super.onPause();
    }

    private void initView() {
        ButterKnife.bind(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMovieReviewList(MovieReviewEvent event) {
        mReviewsAdapter.setData(event.getBody().getResults());
        mController.getMovieTrailers(mMovieDetail.getId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMovieReviewListError(MovieReviewErrorEvent event) {
        Log.e(getString(R.string.error_result_data), event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMovieTrailerList(MovieTrailerEvent event) {
        mTrailersAdapter.setData(event.getBody().getResults());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMovieTrailerListError(MovieTrailerErrorEvent event) {
        Log.e(getString(R.string.error_result_data), event.getMessage());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.action_favorite_star);

        boolean isFavorite = mController.isFavoriteMovie(this, mMovieDetail);
        item.setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_favorite_off));
        if (isFavorite) {
            item.setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_favorite_on));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_favorite_star) {
            if (item.getIcon().getConstantState() == (ContextCompat.getDrawable(this, R.mipmap.ic_favorite_off).getConstantState())) {
                boolean success = mController.addFavoriteMovie(this, mMovieDetail);

                if (success) {
                    Snackbar.make(mDetailPoster, getString(R.string.toast_favorite_success), Snackbar.LENGTH_LONG).show();
                    item.setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_favorite_on));
                } else {
                    Snackbar.make(mDetailPoster, getString(R.string.toast_favorite_failed), Snackbar.LENGTH_LONG).show();
                }
            } else {
                boolean success = mController.removeFavoriteMovie(this, mMovieDetail);
                if (success) {
                    Snackbar.make(mDetailPoster, getString(R.string.toast_unfavorite_success), Snackbar.LENGTH_LONG).show();
                    item.setIcon(ContextCompat.getDrawable(this, R.mipmap.ic_favorite_off));
                } else {
                    Snackbar.make(mDetailPoster, getString(R.string.toast_unfavorite_failed), Snackbar.LENGTH_LONG).show();
                }
            }
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getShareContent());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private String getShareContent() {
        String content = "Popular Movies" + "\n";
        content += mMovieDetail.getTitle() + " ";
        content += Constant.Function.getYoutubeUrl(mTrailersAdapter.getData().get(0).getVideoKey()) + "\n";
        content += "Watch it now.";
        return content;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<Review> reviews = mReviewsAdapter.getData();
        String reviewsJson = App.getInstance().getGson().toJson(reviews);
        outState.putString(reviewJsonKey, reviewsJson);
        List<Video> videos = mTrailersAdapter.getData();
        String videosJson = App.getInstance().getGson().toJson(videos);
        outState.putString(videosJsonKey, videosJson);
    }
}
