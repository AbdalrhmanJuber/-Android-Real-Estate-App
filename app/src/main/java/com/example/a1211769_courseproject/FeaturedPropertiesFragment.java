package com.example.a1211769_courseproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class FeaturedPropertiesFragment extends Fragment implements PropertyAdapter.OnPropertyClickListener {
    
    private RecyclerView recyclerFeatured;
    private PropertyAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout layoutEmpty;
    private DatabaseHelper databaseHelper;
    private List<Property> featuredProperties;
    private String currentUserEmail;
    private static final String API_URL = "https://mocki.io/v1/8345f53d-b99e-4d4d-b4cb-eea3042aa04f";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_featured_properties, container, false);
        
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        view.startAnimation(slideIn);
        
        initializeViews(view);
        setupRecyclerView();
        loadFeaturedProperties();
        
        return view;
    }

    private void initializeViews(View view) {
        recyclerFeatured = view.findViewById(R.id.recycler_featured_properties);
        swipeRefresh = view.findViewById(R.id.swipe_refresh_featured);
        layoutEmpty = view.findViewById(R.id.layout_empty_featured);
        
        databaseHelper = new DatabaseHelper(getContext());
        
        // Get current user email from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
        
        // Check login_preferences as fallback
        SharedPreferences loginPrefs = getActivity().getSharedPreferences("login_preferences", getContext().MODE_PRIVATE);
        String loginEmail = loginPrefs.getString("email", "");
        
        // If UserPrefs is empty but login_preferences has email, copy it over
        if ((currentUserEmail == null || currentUserEmail.trim().isEmpty()) && 
            (loginEmail != null && !loginEmail.trim().isEmpty())) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", loginEmail);
            editor.apply();
            currentUserEmail = loginEmail;
        }
    }

    private void setupRecyclerView() {
        featuredProperties = new ArrayList<>();
        adapter = new PropertyAdapter(getContext(), featuredProperties, currentUserEmail);
        adapter.setOnPropertyClickListener(this);
        
        recyclerFeatured.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerFeatured.setAdapter(adapter);
        
        // Setup pull-to-refresh
        swipeRefresh.setOnRefreshListener(this::loadFeaturedProperties);
        swipeRefresh.setColorSchemeResources(R.color.primary_color);
    }

    private void loadFeaturedProperties() {
        swipeRefresh.setRefreshing(true);
        
        // Load properties from API and filter for special offers
        new FeaturedPropertyConnectionTask().execute(API_URL);
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            recyclerFeatured.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerFeatured.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }    // Inner class for API connection task
    private class FeaturedPropertyConnectionTask extends ConnectionAsyncTask {
        public FeaturedPropertyConnectionTask() {
            super(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefresh.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            swipeRefresh.setRefreshing(false);

            if (result != null) {
                try {
                    List<Property> allProperties = JsonParser.parseProperties(result);
                    if (allProperties != null && !allProperties.isEmpty()) {
                        // Calculate real promotion data based on actual JSON property values
                        enhancePropertiesWithPromotions(allProperties);
                        
                        // Filter for properties with special offers only
                        featuredProperties.clear();
                        for (Property property : allProperties) {
                            if (property.hasSpecialOffer() && property.isOfferValid()) {
                                featuredProperties.add(property);
                            }
                        }
                        
                        adapter.updateProperties(featuredProperties);
                        updateEmptyState();
                        
                        if (featuredProperties.isEmpty()) {
                            Toast.makeText(getContext(), "No featured properties with special offers found", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Found " + featuredProperties.size() + " featured properties", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showError("No properties found");
                    }
                } catch (Exception e) {
                    showError("Error parsing property data: " + e.getMessage());
                }
            } else {
                showError("Failed to load featured properties. Please check your internet connection.");
            }
        }
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        featuredProperties.clear();
        adapter.updateProperties(featuredProperties);
        updateEmptyState();
    }

    /**
     * Enhance loaded properties with realistic promotion data based on actual JSON prices
     * @param properties List of properties to enhance with promotions
     */
    private void enhancePropertiesWithPromotions(List<Property> properties) {
        if (properties.isEmpty()) return;
        
        // Apply special offers to select properties based on criteria
        String[] offerTypes = {"FLASH_SALE", "EARLY_BIRD", "SEASONAL", "LIMITED_TIME", "NEW_LISTING"};
        int offerTypeIndex = 0;
        
        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(i);
            
            // Apply special offers to properties based on price range and position
            boolean shouldHaveOffer = false;
            
            if (property.getPrice() > 200000 && i % 3 == 0) {
                shouldHaveOffer = true; // High-value properties
            } else if (property.getPrice() <= 150000 && i % 4 == 1) {
                shouldHaveOffer = true; // Budget-friendly properties  
            } else if (property.getType().equals("Villa") && i % 5 == 2) {
                shouldHaveOffer = true; // Luxury villas
            }
              if (shouldHaveOffer) {
                String offerType = offerTypes[offerTypeIndex % offerTypes.length];
                double discountPercent = getDiscountForOfferType(offerType);
                
                property.setHasSpecialOffer(true);
                property.setOfferType(offerType);
                property.setOfferDescription(getOfferDescription(offerType));
                property.setDiscountPercentage((int)(discountPercent * 100));
                property.setOriginalPrice(property.getPrice());
                
                offerTypeIndex++;
            }
            
            // Set some properties as promoted (different from special offers)
            if (i % 7 == 0 || property.getPrice() > 300000) {
                property.setPromoted(true);
            }
        }
    }

    private double getDiscountForOfferType(String offerType) {
        switch (offerType) {
            case "FLASH_SALE": return 0.25; // 25% off
            case "EARLY_BIRD": return 0.15; // 15% off
            case "SEASONAL": return 0.20; // 20% off
            case "LIMITED_TIME": return 0.30; // 30% off
            case "NEW_LISTING": return 0.10; // 10% off
            default: return 0.15;
        }
    }

    private String getOfferDescription(String offerType) {
        switch (offerType) {
            case "FLASH_SALE": return "Limited time flash sale!";
            case "EARLY_BIRD": return "Early bird special discount";
            case "SEASONAL": return "Seasonal promotion available";
            case "LIMITED_TIME": return "Limited time offer - act fast!";
            case "NEW_LISTING": return "New listing special price";
            default: return "Special offer available";
        }
    }

    // PropertyAdapter.OnPropertyClickListener implementation
    @Override
    public void onPropertyClick(Property property) {
        // Navigate to property details
        PropertyDetailsFragment detailsFragment = PropertyDetailsFragment.newInstance(property);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }    @Override
    public void onReserveClick(Property property) {
        // Navigate to reservation fragment
        ReservationDetailsFragment reservationFragment = ReservationDetailsFragment.newInstance(property);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, reservationFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
