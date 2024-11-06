package com.example.movieapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;

import java.util.List;

import models.Movie;

public class MovieImageAdapter extends RecyclerView.Adapter<MovieImageAdapter.MovieImageViewHolder> {
    private List<Movie> movieList;

    public MovieImageAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_image_item, parent, false);
        return new MovieImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieImageViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Glide.with(holder.imageView.getContext()).load(movie.getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MovieImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
