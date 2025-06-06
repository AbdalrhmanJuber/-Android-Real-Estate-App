package com.example.a1211769_courseproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnFavoriteActionListener {

    private RecyclerView recyclerFavorites;
    private LinearLayout layoutEmptyState;
    private ImageView imgEmptyState;
    private TextView txtFavoritesTitle;
    
    private FavoritesAdapter favoritesAdapter;
    private DatabaseHelper databaseHelper;
    private String currentUserEmail;
    private List<Property> favoriteProperties;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        
        // Apply slide-in animation
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        view.startAnimation(slideIn);
        
        initializeViews(view);
        setupRecyclerView();
        loadFavoriteProperties();
        
        return view;
    }    private void initializeViews(View view) {
        recyclerFavorites = view.findViewById(R.id.recycler_favorites);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        imgEmptyState = view.findViewById(R.id.img_empty_state);
        txtFavoritesTitle = view.findViewById(R.id.txt_favorites_title);
        
        // Initialize database helper
        databaseHelper = new DatabaseHelper(getContext());
        
        // Get current user email from SharedPreferences (same as PropertiesFragment)
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
        
        Log.d("FavoritesFragment", "Retrieved user email from SharedPreferences: '" + currentUserEmail + "'");
        
        // If UserPrefs is empty, try login_preferences as fallback
        if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
            SharedPreferences loginPrefs = getActivity().getSharedPreferences("login_preferences", getContext().MODE_PRIVATE);
            currentUserEmail = loginPrefs.getString("email", "");
            Log.d("FavoritesFragment", "Fallback: Retrieved user email from login_preferences: '" + currentUserEmail + "'");
        }
        
        // Initialize favorites list
        favoriteProperties = new ArrayList<>();
    }

    private void setupRecyclerView() {
        // Setup RecyclerView with LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerFavorites.setLayoutManager(layoutManager);
        
        // Initialize adapter
        favoritesAdapter = new FavoritesAdapter(getContext(), favoriteProperties, currentUserEmail);
        favoritesAdapter.setOnFavoriteActionListener(this);
        recyclerFavorites.setAdapter(favoritesAdapter);
    }    private void loadFavoriteProperties() {
        Log.d("FavoritesFragment", "Loading favorite properties for user: " + currentUserEmail);
        
        if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
            // Get current API properties from PropertiesFragment
            List<Property> apiProperties = PropertiesFragment.getCurrentApiProperties();
            Log.d("FavoritesFragment", "Retrieved " + apiProperties.size() + " API properties");
            
            if (!apiProperties.isEmpty()) {
                // Use the new overloaded method that works with API properties
                favoriteProperties = databaseHelper.getFavoriteProperties(currentUserEmail, apiProperties);
                Log.d("FavoritesFragment", "Found " + (favoriteProperties != null ? favoriteProperties.size() : 0) + " favorite properties");
            } else {
                // Fallback to old method if no API properties available
                Log.d("FavoritesFragment", "No API properties available, using fallback method");
                favoriteProperties = databaseHelper.getFavoriteProperties(currentUserEmail);
            }
            
            if (favoriteProperties != null && !favoriteProperties.isEmpty()) {
                showFavoritesList();
                favoritesAdapter.updateFavorites(favoriteProperties);
            } else {
                showEmptyState();
            }
        } else {
            Log.w("FavoritesFragment", "No user email available for loading favorites");
            showEmptyState();
        }
    }

    private void showFavoritesList() {
        recyclerFavorites.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
        txtFavoritesTitle.setText("Favorite Properties (" + favoriteProperties.size() + ")");
        
        // Apply fade-in animation to RecyclerView
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        recyclerFavorites.startAnimation(fadeIn);
    }

    private void showEmptyState() {
        recyclerFavorites.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
        txtFavoritesTitle.setText("Favorite Properties");
        
        // Apply fade-in animation to empty state
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        layoutEmptyState.startAnimation(fadeIn);
    }

    @Override
    public void onPropertyClick(Property property) {
        // Navigate to property details
        PropertyDetailsFragment detailsFragment = new PropertyDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("property", property);
        detailsFragment.setArguments(args);
        
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
    }    @Override
    public void onReserveClick(Property property) {
        // Navigate to reservation details fragment
        ReservationDetailsFragment reservationFragment = ReservationDetailsFragment.newInstance(property);
        
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, reservationFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRemoveFromFavorites(Property property, int position) {
        // Remove item from adapter with animation
        favoritesAdapter.removeItem(position);
        
        // Update the favorites list
        favoriteProperties.remove(position);
        
        // Check if list is now empty
        if (favoriteProperties.isEmpty()) {
            showEmptyState();
        } else {
            txtFavoritesTitle.setText("Favorite Properties (" + favoriteProperties.size() + ")");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh favorites when returning to fragment
        loadFavoriteProperties();
    }
}
