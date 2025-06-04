package com.example.a1211769_courseproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.NumberFormat;
import java.util.Locale;

public class PropertyDetailsFragment extends Fragment {

    private static final String ARG_PROPERTY = "property";
    
    private Property property;
    private DatabaseHelper databaseHelper;
    private String currentUserEmail;
    
    // Views
    private ImageView imgProperty;
    private TextView txtPropertyTitle;
    private TextView txtPropertyPrice;
    private TextView txtPropertyType;
    private TextView txtPropertyLocation;
    private TextView txtPropertyArea;
    private TextView txtPropertyBedrooms;
    private TextView txtPropertyBathrooms;
    private TextView txtPropertyDescription;   
     private ImageButton btnFavorite;
    private Button btnReserve;
    private ImageButton btnBack;

    public static PropertyDetailsFragment newInstance(Property property) {
        PropertyDetailsFragment fragment = new PropertyDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY, property);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            property = (Property) getArguments().getSerializable(ARG_PROPERTY);
        }
        
        databaseHelper = new DatabaseHelper(getContext());
        
        // Get current user email
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property_details, container, false);
        
        // Apply fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(fadeIn);
        
        initializeViews(view);
        populateData();
        setupClickListeners();
        
        return view;
    }

    private void initializeViews(View view) {
        imgProperty = view.findViewById(R.id.img_property_detail);
        txtPropertyTitle = view.findViewById(R.id.txt_property_title_detail);
        txtPropertyPrice = view.findViewById(R.id.txt_property_price_detail);
        txtPropertyType = view.findViewById(R.id.txt_property_type_detail);
        txtPropertyLocation = view.findViewById(R.id.txt_property_location_detail);
        txtPropertyArea = view.findViewById(R.id.txt_property_area_detail);
        txtPropertyBedrooms = view.findViewById(R.id.txt_property_bedrooms_detail);
        txtPropertyBathrooms = view.findViewById(R.id.txt_property_bathrooms_detail);
        txtPropertyDescription = view.findViewById(R.id.txt_property_description_detail);
        btnFavorite = view.findViewById(R.id.btn_favorite_detail);
        btnReserve = view.findViewById(R.id.btn_reserve_detail);
        btnBack = view.findViewById(R.id.btn_back);
    }

    private void populateData() {
        if (property == null) return;
        
        // Set property image (placeholder for now)
        imgProperty.setImageResource(R.drawable.property_placeholder);
        
        // Set property details
        txtPropertyTitle.setText(property.getTitle());
        txtPropertyType.setText(property.getType());
        txtPropertyLocation.setText(property.getLocation());
        txtPropertyArea.setText(property.getArea() + " m²");
        txtPropertyBedrooms.setText(String.valueOf(property.getBedrooms()));
        txtPropertyBathrooms.setText(String.valueOf(property.getBathrooms()));
        txtPropertyDescription.setText(property.getDescription());
        
        // Format and set price
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        txtPropertyPrice.setText(formatter.format(property.getPrice()));
        
        // Check if property is in favorites
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        boolean isFavorite = databaseHelper.isPropertyInFavorites(currentUserEmail, property.getId());
        btnFavorite.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    private void setupClickListeners() {
        btnFavorite.setOnClickListener(v -> toggleFavorite());
        
        btnReserve.setOnClickListener(v -> {
            // Apply bounce animation
            ScaleAnimation bounceAnimation = new ScaleAnimation(
                1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            );
            bounceAnimation.setDuration(150);
            bounceAnimation.setRepeatCount(1);
            bounceAnimation.setRepeatMode(Animation.REVERSE);
            
            v.startAnimation(bounceAnimation);
            
            // Open reservation fragment
            openReservationFragment();
        });
        
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void toggleFavorite() {
        boolean isFavorite = databaseHelper.isPropertyInFavorites(currentUserEmail, property.getId());
        
        if (isFavorite) {
            // Remove from favorites
            boolean removed = databaseHelper.removeFromFavorites(currentUserEmail, property.getId());
            if (removed) {
                btnFavorite.setImageResource(R.drawable.ic_heart_outline);
                Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Add to favorites with heart animation
            boolean added = databaseHelper.addToFavorites(currentUserEmail, property.getId());
            if (added) {
                btnFavorite.setImageResource(R.drawable.ic_heart_filled);
                
                // Heart animation - scale up then back to normal
                ScaleAnimation heartAnimation = new ScaleAnimation(
                    1.0f, 1.5f, 1.0f, 1.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                );
                heartAnimation.setDuration(200);
                heartAnimation.setRepeatCount(1);
                heartAnimation.setRepeatMode(Animation.REVERSE);
                
                btnFavorite.startAnimation(heartAnimation);
                Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openReservationFragment() {
        ReservationDetailsFragment reservationFragment = ReservationDetailsFragment.newInstance(property);
        
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
            R.anim.slide_in_up,
            R.anim.slide_out_down,
            R.anim.slide_in_up,
            R.anim.slide_out_down
        );
        transaction.replace(R.id.fragment_container, reservationFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
