package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etEmail, etPassword;
    private Button btnUpdate, btnSelectImage;
    private ImageView ivAvatar;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;
    private String userId;

    private SharedPreferences sharedPreferences;

    private ImageView loginButton, filterButton, profileButton, adminOptionsButton,homeButton;
    private TextView loginStatusTextView, adminOptionsTextView;
    private LinearLayout adminOptionsLayout;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivAvatar = findViewById(R.id.ivAvatar);

        loginStatusTextView = findViewById(R.id.login_status_text_view);
        loginButton = findViewById(R.id.imageView5);
        filterButton = findViewById(R.id.imageView4);
        profileButton = findViewById(R.id.imageView6);
        homeButton = findViewById(R.id.imageViewHome);
        adminOptionsButton = findViewById(R.id.imageViewAdmin);
        adminOptionsTextView = findViewById(R.id.textViewAdmin);
        adminOptionsLayout = findViewById(R.id.adminOptionsLayout);

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);
        isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        // Kết nối đến Firebase Realtime Database và Firebase Storage
        databaseReference = FirebaseDatabase.getInstance().getReference("Account").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference("avatars").child(userId);

        // Lấy thông tin người dùng hiện tại và hiển thị
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String password = dataSnapshot.child("pass").getValue(String.class);
                    String avatarUrl = dataSnapshot.child("image").getValue(String.class);

                    etName.setText(name);
                    etEmail.setText(email);
                    etPassword.setText(password);
                    if (!TextUtils.isEmpty(avatarUrl)) {
                        Glide.with(UpdateProfileActivity.this).load(avatarUrl).into(ivAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateProfileActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện chọn ảnh
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Xử lý sự kiện khi người dùng nhấn nút Update
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        //Xu ly navigator
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, FilterMoviesActivity.class);
                startActivity(intent);
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedIn){
                    Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(UpdateProfileActivity.this, Login.class);
                    startActivity(intent);
                }
            }
        });

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
                    Toast.makeText(UpdateProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            loginStatusTextView.setText("Log In");
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UpdateProfileActivity.this, Login.class);
                    startActivity(intent);
                }
            });
            adminOptionsLayout.setVisibility(View.GONE); // Hide admin options if not logged in
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivAvatar.setImageURI(imageUri);
        }
    }

    private void updateUserProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    updateDatabase(name, email, password, imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            updateDatabase(name, email, password, null);
        }
    }

    private void updateDatabase(String name, String email, String password, @Nullable String imageUrl) {
        databaseReference.child("name").setValue(name);
        databaseReference.child("email").setValue(email);
        databaseReference.child("pass").setValue(password);
        if (imageUrl != null) {
            databaseReference.child("image").setValue(imageUrl);
        }

        Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri) {
        String extension = getContentResolver().getType(uri);
        if (extension != null && extension.contains("/")) {
            extension = extension.substring(extension.lastIndexOf("/") + 1);
        }
        return extension;
    }
}
