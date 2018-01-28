package com.iak.sinju.movieapps.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iak.sinju.movieapps.App;
import com.iak.sinju.movieapps.R;
import com.iak.sinju.movieapps.model.Movie;
import com.iak.sinju.movieapps.util.Constant;
import com.iak.sinju.movieapps.util.retrofit.RetrofitApi;
import com.iak.sinju.movieapps.view.activity.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    private List<Movie> mMovies;

    public MoviesAdapter() {
        mMovies = new ArrayList<>();
    }

    public void setData(List<Movie> movies) {
        this.mMovies.clear();
        this.mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public List<Movie> getData() {
        return this.mMovies;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_movies, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesViewHolder holder, int position) {
        Constant.Function.setImageResource(holder.itemView.getContext(), RetrofitApi.BASE_URL_IMAGE + mMovies.get(position).getPosterPath(), holder.mMoviePoster);
    }

    private void detailMovie(Context context, int position) {
        Intent i = new Intent(context, DetailActivity.class);
        i.putExtra(Constant.Data.MOVIE_INTENT, App.getInstance().getGson().toJson(mMovies.get(position)));
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.adapter_movies_poster)
        ImageView mMoviePoster;

        MoviesViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mMoviePoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailMovie(itemView.getContext(), getAdapterPosition());
                }
            });
        }
    }
}