package com.example.a1211769_courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    
    private EditText editTextEmail, editTextFirstName, editTextLastName;
    private EditText editTextPassword, editTextConfirmPassword, editTextPhone;
    private Spinner spinnerGender, spinnerCountry, spinnerCity;
    private Button buttonRegister;
    private TextView textViewLogin;
    private DatabaseHelper databaseHelper;
    
    // Country codes mapping
    private Map<String, String> countryCodesMap = new HashMap<>();
    private Map<String, String[]> countryCitiesMap = new HashMap<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        setupCountryData();
        databaseHelper = new DatabaseHelper(this);
        setupSpinners();
        setClickListeners();
        setupPhoneNumberWatcher();
    }
    
    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmailReg);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextPassword = findViewById(R.id.editTextPasswordReg);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        spinnerCity = findViewById(R.id.spinnerCity);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
    }
    
    private void setupCountryData() {
        // Setup country codes
        countryCodesMap.put("Palestine", "+970");
        countryCodesMap.put("Jordan", "+962");
        countryCodesMap.put("Lebanon", "+961");
        
        // Setup cities for each country
        countryCitiesMap.put("Palestine", new String[]{"Select City", "Jerusalem", "Ramallah", "Gaza"});
        countryCitiesMap.put("Jordan", new String[]{"Select City", "Amman", "Irbid", "Aqaba"});
        countryCitiesMap.put("Lebanon", new String[]{"Select City", "Beirut", "Tripoli", "Sidon"});
    }
    
    private void setupSpinners() {
        // Gender spinner
        String[] genders = {"Select Gender", "Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        
        // Country spinner
        String[] countries = {"Select Country", "Palestine", "Jordan", "Lebanon"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);
        
        // Country selection listener
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = parent.getItemAtPosition(position).toString();
                updateCitySpinner(selectedCountry);
                updatePhoneCode(selectedCountry);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    private void updateCitySpinner(String country) {
        String[] cities = countryCitiesMap.get(country);
        if (cities == null) {
            cities = new String[]{"Select City"};
        }
        
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);
    }
    
    private void updatePhoneCode(String country) {
        String countryCode = countryCodesMap.get(country);
        if (countryCode != null) {
            String currentPhone = editTextPhone.getText().toString();
            // Remove any existing country code
            if (currentPhone.startsWith("+")) {
                int spaceIndex = currentPhone.indexOf(" ");
                if (spaceIndex > 0) {
                    currentPhone = currentPhone.substring(spaceIndex + 1);
                } else {
                    currentPhone = "";
                }
            }
            editTextPhone.setText(countryCode + " " + currentPhone);
        }
    }
    
    private void setupPhoneNumberWatcher() {
        editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.startsWith("+") && !text.isEmpty()) {
                    String selectedCountry = spinnerCountry.getSelectedItem().toString();
                    String countryCode = countryCodesMap.get(selectedCountry);
                    if (countryCode != null) {
                        editTextPhone.removeTextChangedListener(this);
                        editTextPhone.setText(countryCode + " " + text);
                        editTextPhone.setSelection(editTextPhone.getText().length());
                        editTextPhone.addTextChangedListener(this);
                    }
                }
            }
        });
    }
    
    private void setClickListeners() {
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
    
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String gender = spinnerGender.getSelectedItem().toString();
        String country = spinnerCountry.getSelectedItem().toString();
        String city = spinnerCity.getSelectedItem().toString();
        String phone = editTextPhone.getText().toString().trim();
        
        if (!validateInput(email, firstName, lastName, password, confirmPassword, 
                          gender, country, city, phone)) {
            return;
        }
        
        // Check if user already exists
        if (databaseHelper.checkUser(email)) {
            editTextEmail.setError("User with this email already exists");
            editTextEmail.requestFocus();
            return;
        }
        
        // Animate button
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
        buttonRegister.startAnimation(pulseAnimation);
        
        // Create user object and save to database
        User user = new User(email, firstName, lastName, password, gender, country, city, phone);
        long result = databaseHelper.addUser(user);
        
        if (result != -1) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            
            // Navigate to login
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra("registered_email", email);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateInput(String email, String firstName, String lastName, 
                                String password, String confirmPassword, String gender, 
                                String country, String city, String phone) {
        
        // Email validation
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }
          if (!ValidationUtils.isValidEmail(email)) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return false;
        }
        
        // First name validation
        if (firstName.isEmpty()) {
            editTextFirstName.setError("First name is required");
            editTextFirstName.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isValidName(firstName)) {
            editTextFirstName.setError(ValidationUtils.getNameValidationMessage());
            editTextFirstName.requestFocus();
            return false;
        }
        
        // Last name validation
        if (lastName.isEmpty()) {
            editTextLastName.setError("Last name is required");
            editTextLastName.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isValidName(lastName)) {
            editTextLastName.setError(ValidationUtils.getNameValidationMessage());
            editTextLastName.requestFocus();
            return false;
        }
        
        // Password validation
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            editTextPassword.setError(ValidationUtils.getPasswordStrengthMessage());
            editTextPassword.requestFocus();
            return false;
        }
        
        // Confirm password validation
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return false;
        }
        
        // Gender validation
        if (gender.equals("Select Gender")) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Country validation
        if (country.equals("Select Country")) {
            Toast.makeText(this, "Please select country", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // City validation
        if (city.equals("Select City")) {
            Toast.makeText(this, "Please select city", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Phone validation
        if (phone.isEmpty()) {
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return false;
        }
        
        return true;    }
}
