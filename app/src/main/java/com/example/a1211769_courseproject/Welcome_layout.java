package com.example.a1211769_courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class Welcome_layout extends AppCompatActivity {

    private Button buttonConnect;
    private TextView textStatus;
    private ProgressBar progressBar;
    private static final String API_URL = "https://mocki.io/v1/8345f53d-b99e-4d4d-b4cb-eea3042aa04f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        buttonConnect = findViewById(R.id.buttonConnect);
        textStatus = findViewById(R.id.textStatus);
        progressBar = findViewById(R.id.progressBar);

        // Set click listener for Connect button
        buttonConnect.setOnClickListener(v -> {
            // Hide any previous error message
            textStatus.setVisibility(View.GONE);
            // Start the API connection
            new WelcomeConnectionTask(Welcome_layout.this).execute(API_URL);
        });
    }

    // Methods for UI updates
    public void setButtonText(String text) {
        buttonConnect.setText(text);
    }

    public void setProgress(boolean inProgress) {
        buttonConnect.setEnabled(!inProgress);
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
    }

    // Custom ConnectionAsyncTask for Welcome screen
    private class WelcomeConnectionTask extends ConnectionAsyncTask {

        public WelcomeConnectionTask(Activity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setButtonText("Connecting...");
            setProgress(true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    List<Category> categories = JsonParser.parseCategories(result);

                    if (categories != null && !categories.isEmpty()) {
                        // Navigate to login/register screen
                        Intent intent = new Intent(Welcome_layout.this, MainActivity.class);
                        intent.putExtra("jsonData", result);
                        startActivity(intent);
                        finish();
                    } else {
                        showError("No categories found");
                    }
                } catch (Exception e) {
                    showError("Error parsing data: " + e.getMessage());
                }
            } else {
                showError("Connection failed. Please check your internet connection.");
            }
        }
    }

    private void showError(String message) {
        textStatus.setText(message);
        textStatus.setVisibility(View.VISIBLE);
        setButtonText("Connect");
        setProgress(false);
    }
}

