package com.example.a1211769_courseproject;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private String jsonData;

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

        // Get the JSON data from the intent (passed from Welcome_layout)
        if (getIntent().hasExtra("jsonData")) {
            jsonData = getIntent().getStringExtra("jsonData");
        }

        // Load the login fragment by default
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    // Called when login is successful
    public void onLoginSuccess(String email) {
        Toast.makeText(this, "Welcome, " + email, Toast.LENGTH_SHORT).show();
        // TODO: Navigate to the property listing screen or dashboard
        // For now, just display a toast
    }
}

