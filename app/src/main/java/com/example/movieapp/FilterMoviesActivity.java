package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import models.Movie;

public class FilterMoviesActivity extends AppCompatActivity {

    private Spinner spinnerGenre;
    private Spinner spinnerYear;
    private Button btnFilter;
    private RecyclerView recyclerViewMovies;
    private MoviesAdapter movieAdapter;
    private List<String> genreList;
    private List<String> yearList;
    private List<String> genreIds;
    private ImageView loginButton, filterButton, profileButton, adminOptionsButton,homeButton;
    private TextView loginStatusTextView, adminOptionsTextView;
    private LinearLayout adminOptionsLayout;
    private SharedPreferences sharedPreferences;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_movies);

        spinnerGenre = findViewById(R.id.spinner_genre);
        spinnerYear = findViewById(R.id.spinner_year);
        btnFilter = findViewById(R.id.btn_filter);
        recyclerViewMovies = findViewById(R.id.recycler_view_movies);

        loginStatusTextView = findViewById(R.id.login_status_text_view);
        loginButton = findViewById(R.id.imageView5);
        filterButton = findViewById(R.id.imageView4);
        profileButton = findViewById(R.id.imageView6);
        homeButton = findViewById(R.id.imageViewHome);
        adminOptionsButton = findViewById(R.id.imageViewAdmin);
        adminOptionsTextView = findViewById(R.id.textViewAdmin);
        adminOptionsLayout = findViewById(R.id.adminOptionsLayout);

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        // Sử dụng GridLayoutManager với 3 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerViewMovies.setLayoutManager(gridLayoutManager);

        genreList = new ArrayList<>();
        yearList = new ArrayList<>();
        genreIds = new ArrayList<>();

        loadGenresFromFirebase();
        loadYearsFromMovies();

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMovies();
            }
        });

        //Xu ly navigator
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMoviesActivity.this, FilterMoviesActivity.class);
                startActivity(intent);
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMoviesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedIn){
                    Intent intent = new Intent(FilterMoviesActivity.this, UpdateProfileActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(FilterMoviesActivity.this, Login.class);
                    startActivity(intent);
                }
            }
        });
        adminOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedIn){
                    Intent intent = new Intent(FilterMoviesActivity.this, Login.class);
                    startActivity(intent);
                }
            }
        });
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        if (isLoggedIn) {
            // Get user role (assuming it's stored as 'role' in SharedPreferences)
            int role = sharedPreferences.getInt("role", 0); // Default value if not found

            if (role == 1) { // Assuming role 2 is admin
                // Show admin options layout
                adminOptionsLayout.setVisibility(View.VISIBLE);
            } else {
                // Hide admin options layout
                adminOptionsLayout.setVisibility(View.GONE);
            }

            loginStatusTextView.setText("Log Out");
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle log out action
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.putBoolean("is_logged_in", false);
                    editor.putBoolean("is_get_started",true);
                    editor.apply();
                    loginStatusTextView.setText("Log In");
                    adminOptionsLayout.setVisibility(View.GONE); // Make sure to hide on logout
                    Toast.makeText(FilterMoviesActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FilterMoviesActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            loginStatusTextView.setText("Log In");
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FilterMoviesActivity.this, Login.class);
                    startActivity(intent);
                }
            });
            adminOptionsLayout.setVisibility(View.GONE); // Hide admin options if not logged in
        }
    }

    private void loadGenresFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                genreList.clear();
                genreIds.clear();
                genreList.add("All");
                genreIds.add("All");
                for (DataSnapshot genreSnapshot : snapshot.getChildren()) {
                    String genreName = genreSnapshot.child("name").getValue(String.class);
                    genreList.add(genreName);
                    genreIds.add(genreSnapshot.getKey());
                }
                ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(FilterMoviesActivity.this,
                        android.R.layout.simple_spinner_item, genreList);
                genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGenre.setAdapter(genreAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadYearsFromMovies() {
        FirebaseDatabase.getInstance().getReference("Movies").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> years = new HashSet<>();
                for (DataSnapshot movieSnapshot : snapshot.getChildren()) {
                    Movie movie = movieSnapshot.getValue(Movie.class);
                    if (movie != null && movie.getReleaseDate() != null) {
                        String year = movie.getReleaseDate().split("/")[2];
                        years.add(year);
                    }
                }
                List<String> sortedList = years.stream()
                        .sorted()
                        .collect(Collectors.toList());
                sortedList.add(0,"All");
                yearList.clear();
                yearList.addAll(sortedList);
                ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(FilterMoviesActivity.this,
                        android.R.layout.simple_spinner_item, yearList);
                yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerYear.setAdapter(yearAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void filterMovies() {
        String selectedGenre = spinnerGenre.getSelectedItem().toString();
        String selectedYear = spinnerYear.getSelectedItem().toString();

        Query query = FirebaseDatabase.getInstance().getReference("Movies");

        if (!selectedGenre.equals("All")) {
            String selectedGenreId = genreIds.get(genreList.indexOf(selectedGenre));
            query = query.orderByChild("categoryId").equalTo(Integer.parseInt(selectedGenreId));
        }

        FirebaseRecyclerOptions<Movie> options = new FirebaseRecyclerOptions.Builder<Movie>()
                .setQuery(query, Movie.class)
                .build();

        if (movieAdapter != null) {
            movieAdapter.stopListening();
        }

        movieAdapter = new MoviesAdapter(options, FilterMoviesActivity.this, selectedGenre, selectedYear);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerViewMovies.setLayoutManager(gridLayoutManager);
        recyclerViewMovies.setAdapter(movieAdapter);
        movieAdapter.startListening();
    }
}
