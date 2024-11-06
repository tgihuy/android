package com.example.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import models.Movie;
import com.example.movieapp.Adapters.MoviesAdapter;

public class SearchActivity extends AppCompatActivity {

    private EditText searchText;
    private RecyclerView searchResultsRecyclerView;
    private MoviesAdapter moviesAdapter;
    private List<Movie> movieList;
    private DatabaseReference db;

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchText = findViewById(R.id.searchText);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        backBtn = findViewById(R.id.buttonBack);

        // Change to StaggeredGridLayoutManager with 3 columns
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        searchResultsRecyclerView.setLayoutManager(layoutManager);

        movieList = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(movieList, this);
        searchResultsRecyclerView.setAdapter(moviesAdapter);

        db = FirebaseDatabase.getInstance().getReference("Movies");

        String keyword = getIntent().getStringExtra("searchInput");

        Log.d("SearchActivity", "Received keyword: " + (keyword != null ? keyword : "null"));

        if (!TextUtils.isEmpty(keyword)) {
            searchText.setText(keyword);
            searchMovies(keyword);
        } else {
            searchMovies("");
        }

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchMovies(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        backBtn.setOnClickListener(v -> {

            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void searchMovies(String keyword) {
        Log.d("SearchActivity", "Starting search for keyword: " + keyword);
        Query query = db.orderByChild("name");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SearchActivity", "onDataChange called. DataSnapshot exists: " + dataSnapshot.exists());
                Log.d("SearchActivity", "Number of children in dataSnapshot: " + dataSnapshot.getChildrenCount());

                movieList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("SearchActivity", "Processing child: " + postSnapshot.getKey());
                    Movie movie = postSnapshot.getValue(Movie.class);
                    if (movie != null && movie.getName() != null) {
                        if (TextUtils.isEmpty(keyword) || movie.getName().toLowerCase().contains(keyword.toLowerCase())) {
                            movieList.add(movie);
                            Log.d("SearchActivity", "Added movie: " + movie.getName());
                        }
                    } else {
                        Log.w("SearchActivity", "Failed to parse movie from snapshot: " + postSnapshot.getKey());
                    }
                }

                Log.d("SearchActivity", "Total movies found: " + movieList.size());
                if (movieList.isEmpty()) {
                    Log.d("SearchActivity", "No movies found for keyword: " + keyword);
                } else {
                    Log.d("SearchActivity", "Movies found:");
                    for (Movie movie : movieList) {
                        Log.d("SearchActivity", "- " + movie.getName());
                    }
                }

                moviesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SearchActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
