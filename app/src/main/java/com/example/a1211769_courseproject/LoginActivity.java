package com.example.a1211769_courseproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    
    private EditText editTextEmail, editTextPassword;
    private CheckBox checkBoxRememberMe;
    private Button buttonLogin;
    private TextView textViewRegister;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "login_preferences";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REMEMBER = "remember";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        
        loadSavedEmail();
        setClickListeners();
    }
    
    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
    }
      private void loadSavedEmail() {
        // Check if there's a registered email from the registration flow
        String registeredEmail = getIntent().getStringExtra("registered_email");
        if (registeredEmail != null && !registeredEmail.isEmpty()) {
            editTextEmail.setText(registeredEmail);
            checkBoxRememberMe.setChecked(false);
            return;
        }
        
        // Otherwise, load saved email from SharedPreferences
        boolean remember = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        if (remember) {
            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            editTextEmail.setText(savedEmail);
            checkBoxRememberMe.setChecked(true);
        }
    }
    
    private void setClickListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
    
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
          if (!ValidationUtils.isValidEmail(email)) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }
        
        // Animate button
        Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        buttonLogin.startAnimation(bounceAnimation);
          if (databaseHelper.checkUser(email, password)) {
            // Save email if remember me is checked
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (checkBoxRememberMe.isChecked()) {
                editor.putString(KEY_EMAIL, email);
                editor.putBoolean(KEY_REMEMBER, true);
            } else {
                editor.putString(KEY_EMAIL, "");
                editor.putBoolean(KEY_REMEMBER, false);
            }
            editor.apply();
            
            // Also save user email in UserPrefs for reservation system
            SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPrefs.edit();
            userEditor.putString("email", email);
            userEditor.apply();
            
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            // Navigate to home activity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("userEmail", email);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            
            // Shake animation for error
            Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
            editTextPassword.startAnimation(shakeAnimation);
        }    }
}
