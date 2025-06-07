package com.example.a1211769_courseproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {
    
    private static final String TAG = "ProfileFragment";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int IMAGE_PICK_CODE = 102;    // UI Components
    private ImageView profileImage;
    private FloatingActionButton fabEditImage;
    private TextInputEditText etFirstName, etLastName, etPhoneNumber;    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;    private LinearLayout layoutPersonalInfo, layoutPasswordButtons;
    private CardView layoutPasswordFields;
    private Button btnEdit, btnSave, btnChangePassword, btnCancelPassword;
    private Button btnRefresh, btnClearCache;
    
    // Data
    private DatabaseHelper databaseHelper;
    private String currentUserEmail;
    private User currentUser;
    private boolean isEditMode = false;
    private boolean isPasswordMode = false;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Initialize database and get current user
        initializeData();
        
        // Initialize UI components
        initializeViews(view);
        
        // Setup click listeners
        setupClickListeners();
        
        // Load user data
        loadUserData();
        
        // Setup animations
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        view.startAnimation(slideIn);
        
        return view;
    }
      private void initializeData() {
        // Check if fragment is properly attached
        if (getContext() == null || getActivity() == null) {
            Log.e(TAG, "Fragment not properly attached to activity");
            return;
        }
        
        databaseHelper = new DatabaseHelper(getContext());
        
        // Get current user email from SharedPreferences with null check
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
        
        Log.d(TAG, "Retrieved user email from SharedPreferences: '" + currentUserEmail + "'");
        
        // If UserPrefs is empty, try login_preferences as fallback
        if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
            SharedPreferences loginPrefs = getActivity().getSharedPreferences("login_preferences", getContext().MODE_PRIVATE);
            currentUserEmail = loginPrefs.getString("email", "");
            Log.d(TAG, "Fallback: Retrieved user email from login_preferences: '" + currentUserEmail + "'");
        }
        
        if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "User session expired. Please login again.", Toast.LENGTH_LONG).show();
            }
            return;
        }
    }    private void initializeViews(View view) {
        // Profile image section
        profileImage = view.findViewById(R.id.profile_image);
        fabEditImage = view.findViewById(R.id.fab_edit_picture);
        
        // Personal information fields
        etFirstName = view.findViewById(R.id.et_first_name);
        etLastName = view.findViewById(R.id.et_last_name);
        etPhoneNumber = view.findViewById(R.id.et_phone);
        
        // Password fields
        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
          // Layout containers
        tilCurrentPassword = view.findViewById(R.id.til_current_password);
        tilNewPassword = view.findViewById(R.id.til_new_password);
        tilConfirmPassword = view.findViewById(R.id.til_confirm_password);
        layoutPersonalInfo = view.findViewById(R.id.layout_profile_buttons);
        layoutPasswordFields = view.findViewById(R.id.card_password);
        layoutPasswordButtons = view.findViewById(R.id.layout_password_buttons);
        
        // Buttons
        btnEdit = view.findViewById(R.id.btn_edit_profile);
        btnSave = view.findViewById(R.id.btn_save_profile);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnCancelPassword = view.findViewById(R.id.btn_save_password);
        btnRefresh = view.findViewById(R.id.btn_refresh_profile);
        btnClearCache = view.findViewById(R.id.btn_clear_cache);
        
        // Initially hide edit mode components
        setEditMode(false);
        setPasswordMode(false);
    }
      private void setupClickListeners() {
        // Profile image editing
        fabEditImage.setOnClickListener(v -> showImagePickerDialog());
        
        // Edit/Save buttons - using toggle functionality
        btnEdit.setOnClickListener(v -> {
            if (!isEditMode) {
                setEditMode(true);
                btnEdit.setText("Cancel");
                btnSave.setVisibility(View.VISIBLE);
                btnSave.setText("Save Changes");
            } else {
                setEditMode(false);
                loadUserData(); // Reload original data
                btnEdit.setText("Edit Profile");
                btnSave.setVisibility(View.GONE);
            }
        });
        
        btnSave.setOnClickListener(v -> {
            if (isPasswordMode) {
                savePasswordChange();
            } else {
                saveProfileChanges();
            }
        });
        
        // Password change button - using toggle functionality
        btnChangePassword.setOnClickListener(v -> {
            if (!isPasswordMode) {
                setPasswordMode(true);
                btnChangePassword.setText("Cancel");
                btnCancelPassword.setVisibility(View.VISIBLE);
                btnCancelPassword.setText("Update Password");
            } else {
                setPasswordMode(false);
                clearPasswordFields();
                btnChangePassword.setText("Change Password");
                btnCancelPassword.setVisibility(View.GONE);
            }
        });
        
        btnCancelPassword.setOnClickListener(v -> savePasswordChange());
        
        // Account actions
        btnRefresh.setOnClickListener(v -> refreshUserData());
        btnClearCache.setOnClickListener(v -> clearAppCache());
    }
    
    private void loadUserData() {
        if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
            return;
        }
        
        try {
            currentUser = databaseHelper.getUserByEmail(currentUserEmail);
            if (currentUser != null) {
                // Populate form fields
                etFirstName.setText(currentUser.getFirstName());
                etLastName.setText(currentUser.getLastName());
                etPhoneNumber.setText(currentUser.getPhoneNumber());
                
                // Load profile picture
                loadProfilePicture();
                
                Log.d(TAG, "User data loaded successfully");
            } else {
                Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data: " + e.getMessage());
            Toast.makeText(getContext(), "Error loading profile data", Toast.LENGTH_SHORT).show();
        }
    }    private void loadProfilePicture() {
        try {
            String picturePath = databaseHelper.getUserProfilePicture(currentUserEmail);
            if (picturePath != null && !picturePath.isEmpty()) {
                File imageFile = new File(picturePath);
                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    profileImage.setImageBitmap(bitmap);
                    return;
                }
            }
            // Set default profile image
            profileImage.setImageResource(R.drawable.ic_person);
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile picture: " + e.getMessage());
            profileImage.setImageResource(R.drawable.ic_person);
        }
    }
      private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        
        // Enable/disable form fields
        etFirstName.setEnabled(editMode);
        etLastName.setEnabled(editMode);
        etPhoneNumber.setEnabled(editMode);
        
        // Show/hide buttons and update text
        if (editMode) {
            btnEdit.setText("Cancel");
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setText("Save Changes");
            fabEditImage.setVisibility(View.VISIBLE);
            etFirstName.requestFocus();
        } else {
            btnEdit.setText("Edit Profile");
            btnSave.setVisibility(View.GONE);
            fabEditImage.setVisibility(View.GONE);
        }
    }
    
    private void setPasswordMode(boolean passwordMode) {
        isPasswordMode = passwordMode;
        
        // Show/hide password fields with animation
        animatePasswordFieldsVisibility(passwordMode);
        
        // Update button text and visibility
        if (passwordMode) {
            btnChangePassword.setText("Cancel");
            btnCancelPassword.setVisibility(View.VISIBLE);
            btnCancelPassword.setText("Update Password");
            etCurrentPassword.requestFocus();
        } else {
            btnChangePassword.setText("Change Password");
            btnCancelPassword.setVisibility(View.GONE);
        }
    }
      private void saveProfileChanges() {
        // Validate input
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        
        if (!validateProfileInput(firstName, lastName, phoneNumber)) {
            return;
        }
        
        // Save profile changes to database
        try {
            boolean success = databaseHelper.updateUserProfile(currentUserEmail, firstName, lastName, phoneNumber);
            
            if (success) {
                // Update current user object
                currentUser.setFirstName(firstName);
                currentUser.setLastName(lastName);
                currentUser.setPhoneNumber(phoneNumber);
                
                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                setEditMode(false);
                
                // Success animation
                Animation successAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
                layoutPersonalInfo.startAnimation(successAnimation);
                
            } else {
                Toast.makeText(getContext(), "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving profile changes: " + e.getMessage());
            Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }
      private void savePasswordChange() {
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        
        if (!validatePasswordInput(currentPassword, newPassword, confirmPassword)) {
            return;
        }
        
        try {
            boolean success = databaseHelper.updateUserPassword(currentUserEmail, currentPassword, newPassword);
            
            if (success) {
                Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                setPasswordMode(false);
                clearPasswordFields();
                
                // Success animation
                Animation successAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
                btnChangePassword.startAnimation(successAnimation);
                
            } else {
                etCurrentPassword.setError("Current password is incorrect");
                etCurrentPassword.requestFocus();
                Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating password: " + e.getMessage());
            Toast.makeText(getContext(), "Error updating password", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateProfileInput(String firstName, String lastName, String phoneNumber) {
        // Validate first name
        if (!ValidationUtils.isValidName(firstName)) {
            etFirstName.setError(ValidationUtils.getNameValidationMessage());
            etFirstName.requestFocus();
            return false;
        }
        
        // Validate last name
        if (!ValidationUtils.isValidName(lastName)) {
            etLastName.setError(ValidationUtils.getNameValidationMessage());
            etLastName.requestFocus();
            return false;
        }
        
        // Validate phone number
        if (!ValidationUtils.isValidPhone(phoneNumber)) {
            etPhoneNumber.setError("Please enter a valid phone number");
            etPhoneNumber.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean validatePasswordInput(String currentPassword, String newPassword, String confirmPassword) {
        // Validate current password
        if (currentPassword.isEmpty()) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return false;
        }
        
        // Validate new password
        if (!ValidationUtils.isValidPassword(newPassword)) {
            etNewPassword.setError(ValidationUtils.getPasswordStrengthMessage());
            etNewPassword.requestFocus();
            return false;
        }
        
        // Validate password confirmation
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void clearPasswordFields() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
        etCurrentPassword.setError(null);
        etNewPassword.setError(null);
        etConfirmPassword.setError(null);
    }
    
    private void animateViewTransition(View fromView, View toView) {
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                fromView.setVisibility(View.GONE);
                toView.setVisibility(View.VISIBLE);
                toView.startAnimation(fadeIn);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        
        fromView.startAnimation(fadeOut);
    }
      private void animatePasswordFieldsVisibility(boolean show) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), 
            show ? R.anim.slide_down : R.anim.slide_up);
        
        if (show) {
            tilCurrentPassword.setVisibility(View.VISIBLE);
            tilNewPassword.setVisibility(View.VISIBLE);
            tilConfirmPassword.setVisibility(View.VISIBLE);
        }
        
        tilCurrentPassword.startAnimation(animation);
        tilNewPassword.startAnimation(animation);
        tilConfirmPassword.startAnimation(animation);
        
        if (!show) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    tilCurrentPassword.setVisibility(View.GONE);
                    tilNewPassword.setVisibility(View.GONE);
                    tilConfirmPassword.setVisibility(View.GONE);
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
    }
    
    private void showImagePickerDialog() {
        // Create intent chooser for image selection
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Profile Picture");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        
        startActivityForResult(chooserIntent, IMAGE_PICK_CODE);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            handleImageResult(data);
        }
    }
    
    private void handleImageResult(Intent data) {
        try {
            Bitmap bitmap = null;
            
            if (data.getData() != null) {
                // Gallery image
                Uri imageUri = data.getData();
                InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
            } else if (data.getExtras() != null) {
                // Camera image
                bitmap = (Bitmap) data.getExtras().get("data");
            }
            
            if (bitmap != null) {                // Save image and update profile
                String imagePath = saveImageToInternalStorage(bitmap);
                if (imagePath != null) {
                    // Update database
                    boolean success = databaseHelper.updateUserProfilePicture(currentUserEmail, imagePath);
                    if (success) {
                        // Update UI
                        profileImage.setImageBitmap(bitmap);
                        
                        // Success animation
                        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                        profileImage.startAnimation(scaleAnimation);
                        
                        Toast.makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling image result: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String saveImageToInternalStorage(Bitmap bitmap) {
        try {
            File directory = new File(getContext().getFilesDir(), "profile_pictures");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String fileName = "profile_" + currentUserEmail.hashCode() + ".jpg";
            File file = new File(directory, fileName);
            
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image: " + e.getMessage());
            return null;
        }
    }
    
    private void refreshUserData() {
        // Animate refresh button
        Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_360);
        btnRefresh.startAnimation(rotateAnimation);
        
        // Reload user data
        loadUserData();
        
        Toast.makeText(getContext(), "Profile data refreshed", Toast.LENGTH_SHORT).show();
    }
      private void clearAppCache() {
        try {
            // Check if fragment is properly attached
            if (getContext() == null || getActivity() == null) {
                Log.e(TAG, "Fragment not properly attached - cannot clear cache");
                return;
            }
            
            // Clear SharedPreferences cache (except login info)
            SharedPreferences prefs = getActivity().getSharedPreferences("AppCache", getContext().MODE_PRIVATE);
            prefs.edit().clear().apply();
            
            // Animate clear button
            Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            btnClearCache.startAnimation(shakeAnimation);
            
            Toast.makeText(getContext(), "App cache cleared", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing cache: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to clear cache", Toast.LENGTH_SHORT).show();
        }
    }
}
