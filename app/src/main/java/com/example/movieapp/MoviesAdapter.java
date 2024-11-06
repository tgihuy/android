package com.example.movieapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import models.Movie;

public class MoviesAdapter extends FirebaseRecyclerAdapter<Movie, MoviesAdapter.MyViewHolder> {
    private static final String TAG = "MoviesAdapter";

    private final Context context;
    private final String selectedGenre;
    private final String selectedYear;

    public MoviesAdapter(@NonNull FirebaseRecyclerOptions<Movie> options, Context context, String selectedGenre, String selectedYear) {
        super(options);
        this.context = context;
        this.selectedGenre = selectedGenre;
        this.selectedYear = selectedYear;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Movie movie) {
        if (matchesFilter(movie)) {
            holder.titleTxt.setText(movie.getName());
            Glide.with(holder.pic.getContext())
                    .load(movie.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.pic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    Gson gson = new Gson();
                    String movieJson = gson.toJson(movie);
                    intent.putExtra("movieJson", movieJson);
                    context.startActivity(intent);
                }
            });
        } else {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = 0;
            layoutParams.width = 0;
            holder.itemView.setLayoutParams(layoutParams);
            holder.itemView.setVisibility(View.GONE);
        }
    }

    private boolean matchesFilter(Movie movie) {
        boolean matchesYear = selectedYear.equals("All") || (movie.getReleaseDate() != null && movie.getReleaseDate().split("/")[2].equals(selectedYear));
        return matchesYear;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_film_with_title, parent, false);
        return new MyViewHolder(view);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
        }
    }
}
