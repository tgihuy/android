package com.example.movieapp;

import android.content.Intent;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

import models.FilmCrew;
import models.Movie;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView imageViewDetail, imageViewCrew;
    private TextView textViewMovieTitle, textViewMovieDescription, textViewCrewName, textViewCrewRole;
    private TextView textViewReleaseDate, textViewMovieDirector;
    private LinearLayout crewContainer;
    private PlayerView playerView;
    private ExoPlayer player;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        imageViewDetail = findViewById(R.id.imageViewDetail);
        textViewMovieTitle = findViewById(R.id.textViewMovieTitle);
        textViewMovieDescription = findViewById(R.id.textViewMovieDescription);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewMovieDirector = findViewById(R.id.textViewDirector);
        imageViewCrew = findViewById(R.id.imageViewCrew);
        textViewCrewName = findViewById(R.id.textViewCrewName);
        textViewCrewRole = findViewById(R.id.textViewCrewRole);
        crewContainer = findViewById(R.id.crewContainer);
        playerView = findViewById(R.id.playerView);
        ImageButton buttonBack = findViewById(R.id.buttonBack);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Get JSON string from intent
        String movieJson = getIntent().getStringExtra("movieJson");
        Log.d("MovieDetailActivity", "Received movieJson: " + movieJson);
        if (movieJson != null) {
            // Convert JSON string to Movie object
            Gson gson = new Gson();
            Movie movie = gson.fromJson(movieJson, Movie.class);
            Log.d("MovieDetailActivity", "Parsed Movie object: " + movie.toString());

            // Log the video link
            Log.d("MovieDetailActivity", "Video link: " + movie.getLink());

            // Display movie details
            Glide.with(this).load(movie.getImage()).into(imageViewDetail);
            textViewMovieTitle.setText(movie.getName());
            textViewMovieDescription.setText(movie.getDescription());
            textViewReleaseDate.setText("Release Date: "+ movie.getReleaseDate());
            textViewMovieDirector.setText("Director: "+ movie.getDirector());

            // Get Film Crew information from Firebase Database
            List<FilmCrew> filmCrews = movie.getFilmCrews();
            if (filmCrews != null && !filmCrews.isEmpty()) {
                for (final FilmCrew crew : filmCrews) {
                    DatabaseReference crewRef = databaseReference.child("FilmCrew").child(String.valueOf(crew.getId()));
                    Log.d("MovieDetailActivity", "FilmCrew id: " + crew.getName());
                    crewRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                FilmCrew crewMember = dataSnapshot.getValue(FilmCrew.class);
                                if (crewMember != null) {
                                    // Display this crew member
                                    Glide.with(MovieDetailActivity.this).load(crewMember.getImage()).into(imageViewCrew);
                                    textViewCrewName.setText(crewMember.getName());
                                    textViewCrewRole.setText(crewMember.getRole());
                                    crewContainer.setVisibility(View.VISIBLE); // Show crewContainer
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                }
            } else {
                // If no crew members, hide crewContainer
                crewContainer.setVisibility(View.GONE);
            }

            // Play video from link
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

            if (isLoggedIn) {
                // Play video from link
                if (movie.getLink() != null && !movie.getLink().isEmpty()) {
                    MediaItem mediaItem = MediaItem.fromUri(movie.getLink());
                    DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, "exoplayer-sample");
                    ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(mediaItem);
                    player.setMediaSource(mediaSource);
                    player.prepare();
                    player.setPlayWhenReady(true);
                }
            } else {
                playerView.setVisibility(View.GONE);
                Toast.makeText(MovieDetailActivity.this, "Bạn cần đăng nhập để xem video", Toast.LENGTH_SHORT).show();
            }
        }

        // Set click listener for back button
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the player when the activity is destroyed
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
