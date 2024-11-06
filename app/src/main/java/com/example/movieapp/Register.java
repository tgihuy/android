package com.example.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import models.Account;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private EditText rePasswordInput;
    private Button registerBtn;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseReference = FirebaseDatabase.getInstance().getReference("Account");

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        rePasswordInput = findViewById(R.id.repassword_input);
        registerBtn = findViewById(R.id.register_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String rePassword = rePasswordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Email is required");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(Register.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    emailInput.setError("Valid email is required");
                    emailInput.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    passwordInput.setError("Password is required");
                    passwordInput.requestFocus();
                } else if (!isPasswordValid(password)) {
                    Toast.makeText(Register.this, "Password must be at least 8 characters long and contain an uppercase letter, a lowercase letter, and a digit", Toast.LENGTH_LONG).show();
                    passwordInput.setError("Invalid password");
                    passwordInput.requestFocus();
                } else if (TextUtils.isEmpty(rePassword)) {
                    Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    rePasswordInput.setError("Re-Password is required");
                    rePasswordInput.requestFocus();
                } else if (!password.equals(rePassword)) {
                    Toast.makeText(Register.this, "Password and Re-Password must be same", Toast.LENGTH_SHORT).show();
                    rePasswordInput.setError("Password and Re-Password do not match");
                    rePasswordInput.requestFocus();
                } else {
                    checkEmailAndRegisterUser(email, password);
                }
            }
        });

        TextView toLoginBtn = findViewById(R.id.to_login_btn);
        toLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void checkEmailAndRegisterUser(String email, String password) {

        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(Register.this, "Email already registered", Toast.LENGTH_SHORT).show();
                    emailInput.setError("Email already registered");
                    emailInput.requestFocus();
                } else {
                    registerUser(email, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Register.this, "Error checking email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(String email, String password) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long maxId = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        long id = Long.parseLong(snapshot.getKey());
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException e) {

                    }
                }

                String newDocumentId = String.valueOf(maxId + 1);

                Account account = new Account("User " + newDocumentId, email, password, "", "", 2,"");  // Replace with actual user details
                databaseReference.push().setValue(account);
                Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Register.this, "User registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
        return pattern.matcher(password).matches();
    }
}
