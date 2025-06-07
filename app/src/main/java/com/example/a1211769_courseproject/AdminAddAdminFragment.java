package com.example.a1211769_courseproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminAddAdminFragment extends Fragment {
    
    private DatabaseHelper databaseHelper;
    private EditText etEmail, etPassword, etConfirmPassword, etFirstName, etLastName, etCountry;
    private RadioGroup rgGender;
    private Button btnCreateAdmin;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_add_admin, container, false);
        
        databaseHelper = new DatabaseHelper(getContext());
        initializeViews(view);
        setupClickListeners();
        
        return view;
    }
    
    private void initializeViews(View view) {
        etEmail = view.findViewById(R.id.et_admin_email);
        etPassword = view.findViewById(R.id.et_admin_password);
        etConfirmPassword = view.findViewById(R.id.et_admin_confirm_password);
        etFirstName = view.findViewById(R.id.et_admin_first_name);
        etLastName = view.findViewById(R.id.et_admin_last_name);
        etCountry = view.findViewById(R.id.et_admin_country);
        rgGender = view.findViewById(R.id.rg_admin_gender);
        btnCreateAdmin = view.findViewById(R.id.btn_create_admin);
    }
    
    private void setupClickListeners() {
        btnCreateAdmin.setOnClickListener(v -> createNewAdmin());
    }
    
    private void createNewAdmin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        
        // Get selected gender
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId != -1) {
            RadioButton selectedGender = getView().findViewById(selectedGenderId);
            gender = selectedGender.getText().toString();
        }
        
        // Validate input
        if (!validateInput(email, password, confirmPassword, firstName, lastName, country, gender)) {
            return;
        }
        
        // Check if email already exists
        User existingUser = databaseHelper.getUserByEmail(email);
        if (existingUser != null) {
            Toast.makeText(getContext(), "Email already exists", Toast.LENGTH_SHORT).show();
            return;
        }
          // Create new admin user
        User newAdmin = new User(email, firstName, lastName, password, gender, country, "System", "000-000-0000", "ADMIN");
        long result = databaseHelper.addUser(newAdmin);
        
        if (result > 0) {
            Toast.makeText(getContext(), "New admin created successfully", Toast.LENGTH_SHORT).show();
            clearForm();
            
            // Add success animation
            btnCreateAdmin.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(100)
                    .withEndAction(() -> 
                            btnCreateAdmin.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start())
                    .start();
        } else {
            Toast.makeText(getContext(), "Failed to create admin account", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateInput(String email, String password, String confirmPassword, 
                                 String firstName, String lastName, String country, String gender) {
        
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }
        
        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return false;
        }
        
        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return false;
        }
        
        if (country.isEmpty()) {
            etCountry.setError("Country is required");
            etCountry.requestFocus();
            return false;
        }
        
        if (gender.isEmpty()) {
            Toast.makeText(getContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
        etFirstName.setText("");
        etLastName.setText("");
        etCountry.setText("");
        rgGender.clearCheck();
        
        // Clear any error messages
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        etFirstName.setError(null);
        etLastName.setError(null);
        etCountry.setError(null);
    }
}
