package com.example.a1211769_courseproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private String jsonData;
    private TextView welcomeText;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the JSON data from the intent (passed from Welcome_layout or LoginActivity)
        if (getIntent().hasExtra("jsonData")) {
            jsonData = getIntent().getStringExtra("jsonData");
        }

        initializeViews();
        setupWelcomeMessage();
        setupLogoutButton();
    }    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupWelcomeMessage() {
        // Get user email from intent or SharedPreferences
        String userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail == null) {
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            userEmail = prefs.getString("saved_email", "User");
        }
        
        welcomeText.setText("Welcome, " + userEmail + "!");
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
    }

    private void setupLogoutButton() {
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }    private void logout() {
        // Clear SharedPreferences if needed
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("remember_me", false);
        editor.apply();

        // Also clear UserPrefs used by reservation system
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPrefs.edit();
        userEditor.clear();
        userEditor.apply();

        // Navigate back to Welcome screen
        Intent intent = new Intent(MainActivity.this, Welcome_layout.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

