package com.example.movieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.TextUtils;
import android.util.Log;
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


public class Login extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginBtn;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);
        TextView toRegisterBtn = findViewById(R.id.to_register_btn);
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    emailInput.setError("Email is required");
                    emailInput.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(Login.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    emailInput.setError("Valid email is required");
                    emailInput.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    passwordInput.setError("Password is required");
                    passwordInput.requestFocus();
                } else {
                    validateLogin(email, password);
                }
            }
        });

        toRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private void validateLogin(String email, String password) {
        databaseReference.child("Account").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                                Account account = accountSnapshot.getValue(Account.class);
                                if (account != null && account.getPass().equals(password)) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("is_logged_in", true);
                                    editor.putString("userId", accountSnapshot.getKey());
                                    editor.putInt("role", account.getRoleId());
                                    editor.apply();
                                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.putExtra("account", account);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(Login.this, "Email not registered", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Login.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
