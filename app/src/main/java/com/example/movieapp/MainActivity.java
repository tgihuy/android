package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movieapp.Adapters.MoviesAdapter;
import com.example.movieapp.Adapters.SliderAdapters;
import com.example.movieapp.Domain.SliderItems;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import models.Movie;


import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewBestMovies, recyclerViewUpcoming, recyclerViewCategory;
    private ProgressBar loading1, loading2, loading3;
    private ViewPager2 viewPager2;
    private DatabaseReference databaseReference;
    private EditText searchText;

    private ImageView loginButton, filterButton, profileButton, adminOptionsButton,homeButton;
    private TextView loginStatusTextView, adminOptionsTextView;

    private LinearLayout adminOptionsLayout;

    private SharedPreferences sharedPreferences;
    private boolean isLoggedIn,isGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        isGetStarted = sharedPreferences.getBoolean("is_get_started", false);
        if(!isGetStarted){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_get_started",true);
            editor.apply();
            setContentView(R.layout.intro_main);
            Button getinBtn = (Button)findViewById(R.id.getinBtn);
            getinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                }
            });
            return;
        }else{
            setContentView(R.layout.activity_main);
        }
        initView();
        setupBanners();
        fetchMoviesFromFirebase();
        checkLoginStatus();

        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String searchInput = searchText.getText().toString().trim();
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    private void initView() {
        viewPager2 = findViewById(R.id.viewpagerSlider);
        recyclerViewBestMovies = findViewById(R.id.view1);
        recyclerViewBestMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUpcoming = findViewById(R.id.view2);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory = findViewById(R.id.view3);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loading1 = findViewById(R.id.progressBar1);
        loading2 = findViewById(R.id.progressBar2);
        loading3 = findViewById(R.id.progressBar3);

        loginStatusTextView = findViewById(R.id.login_status_text_view);
        loginButton = findViewById(R.id.imageView5);
        filterButton = findViewById(R.id.imageView4);
        profileButton = findViewById(R.id.imageView6);
        homeButton = findViewById(R.id.imageViewHome);
        adminOptionsButton = findViewById(R.id.imageViewAdmin);
        adminOptionsTextView = findViewById(R.id.textViewAdmin);
        adminOptionsLayout = findViewById(R.id.adminOptionsLayout);
        isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        searchText = findViewById(R.id.editTextText2);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilterMoviesActivity.class);
                startActivity(intent);
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedIn){
                    Intent intent = new Intent(MainActivity.this, UpdateProfileActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
            }
        });


    }

    private void setupBanners() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide));
        sliderItems.add(new SliderItems(R.drawable.wide3));

        viewPager2.setAdapter(new SliderAdapters(sliderItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_ALWAYS);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    }

    private void fetchMoviesFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Movies");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Movie> bestMovies = new ArrayList<>();
                List<Movie> upcomingMovies = new ArrayList<>();
                List<Movie> categoryMovies = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Movie movie = dataSnapshot.getValue(Movie.class);
                    switch (movie.getCategoryId()) {
                        case 1:
                            bestMovies.add(movie);
                            break;
                        case 2:
                            upcomingMovies.add(movie);
                            break;
                        default:
                            categoryMovies.add(movie);
                            break;
                    }
                }

                setupRecyclerViews(bestMovies, upcomingMovies, categoryMovies);
                hideLoadingIndicators();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                hideLoadingIndicators();
            }
        });
    }

    private void setupRecyclerViews(List<Movie> bestMovies, List<Movie> upcomingMovies, List<Movie> categoryMovies) {
        MoviesAdapter adapterBestMovies = new MoviesAdapter(bestMovies, this);
        recyclerViewBestMovies.setAdapter(adapterBestMovies);

        MoviesAdapter adapterUpcomingMovies = new MoviesAdapter(upcomingMovies, this);
        recyclerViewUpcoming.setAdapter(adapterUpcomingMovies);

        MoviesAdapter adapterCategoryMovies = new MoviesAdapter(categoryMovies, this);
        recyclerViewCategory.setAdapter(adapterCategoryMovies);
    }

    private void hideLoadingIndicators() {
        loading1.setVisibility(View.GONE);
        loading2.setVisibility(View.GONE);
        loading3.setVisibility(View.GONE);
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
                    Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            loginStatusTextView.setText("Log In");
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
            });
            adminOptionsLayout.setVisibility(View.GONE); // Hide admin options if not logged in
        }
    }


}