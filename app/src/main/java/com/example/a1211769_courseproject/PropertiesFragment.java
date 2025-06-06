package com.example.a1211769_courseproject;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
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

public class PropertiesFragment extends Fragment implements PropertyAdapter.OnPropertyClickListener {    private RecyclerView recyclerProperties;
    private PropertyAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout layoutEmpty;
    private EditText editSearch;
    private Button btnFilterType, btnFilterLocation, btnFilterPrice;
    private Button btnFilterSpecialOffers, btnFilterPromoted, btnClearFilters;
    private DatabaseHelper databaseHelper;
    private List<Property> allProperties;
    private String currentUserEmail;
    private static final String API_URL = "https://mocki.io/v1/8345f53d-b99e-4d4d-b4cb-eea3042aa04f";
    
    // Static field to store current API properties for sharing with other fragments
    private static List<Property> currentApiProperties = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_properties, container, false);
        
        // Apply slide-in animation
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        view.startAnimation(slideIn);
        
        initializeViews(view);
        setupRecyclerView();
        setupSearchAndFilters();
        loadProperties();
        
        return view;
    }    private void initializeViews(View view) {
        recyclerProperties = view.findViewById(R.id.recycler_properties);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        editSearch = view.findViewById(R.id.edit_search);
        btnFilterType = view.findViewById(R.id.btn_filter_type);
        btnFilterLocation = view.findViewById(R.id.btn_filter_location);
        btnFilterPrice = view.findViewById(R.id.btn_filter_price);
        btnFilterSpecialOffers = view.findViewById(R.id.btn_filter_special_offers);
        btnFilterPromoted = view.findViewById(R.id.btn_filter_promoted);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);
        
        databaseHelper = new DatabaseHelper(getContext());
          // Get current user email from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
        
        // Check login_preferences as fallback
        SharedPreferences loginPrefs = getActivity().getSharedPreferences("login_preferences", getContext().MODE_PRIVATE);
        String loginEmail = loginPrefs.getString("email", "");        // If UserPrefs is empty but login_preferences has email, copy it over
        if ((currentUserEmail == null || currentUserEmail.trim().isEmpty()) && 
            (loginEmail != null && !loginEmail.trim().isEmpty())) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", loginEmail);
            editor.apply();
            currentUserEmail = loginEmail;
        }
        
        // Test database connectivity and user verification
        if (currentUserEmail != null && !currentUserEmail.trim().isEmpty()) {
            User user = databaseHelper.getUserByEmail(currentUserEmail);
            if (user == null) {
                Log.w("PropertiesFragment", "User not found in database for email: " + currentUserEmail);
            }
        }
    }

    private void setupRecyclerView() {
        allProperties = new ArrayList<>();
        adapter = new PropertyAdapter(getContext(), allProperties, currentUserEmail);
        adapter.setOnPropertyClickListener(this);
        
        recyclerProperties.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerProperties.setAdapter(adapter);
        
        // Setup pull-to-refresh
        swipeRefresh.setOnRefreshListener(this::loadProperties);
        swipeRefresh.setColorSchemeResources(R.color.primary_color);
    }

    private void setupSearchAndFilters() {
        // Search functionality
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                updateEmptyState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });        // Filter buttons
        btnFilterType.setOnClickListener(v -> showTypeFilterDialog());
        btnFilterLocation.setOnClickListener(v -> showLocationFilterDialog());
        btnFilterPrice.setOnClickListener(v -> showPriceFilterDialog());
        
        // Special offers filter buttons
        btnFilterSpecialOffers.setOnClickListener(v -> filterBySpecialOffers());
        btnFilterPromoted.setOnClickListener(v -> filterByPromoted());
        btnClearFilters.setOnClickListener(v -> clearAllFilters());
    }    private void loadProperties() {
        swipeRefresh.setRefreshing(true);
        
        // Load properties from API instead of database
        new PropertyConnectionTask().execute(API_URL);
    }    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            recyclerProperties.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerProperties.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }    // Special offers filter methods
    private void filterBySpecialOffers() {
        adapter.filterBySpecialOffers();
        updateEmptyState();
        
        // Apply bounce animation to button
        Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        btnFilterSpecialOffers.startAnimation(bounce);
        
        int filteredCount = adapter.getItemCount();
        Toast.makeText(getContext(), "Found " + filteredCount + " properties with special offers", Toast.LENGTH_SHORT).show();
    }    private void filterByPromoted() {
        adapter.filterByPromoted();
        updateEmptyState();
        
        // Apply bounce animation to button
        Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        btnFilterPromoted.startAnimation(bounce);
        
        int filteredCount = adapter.getItemCount();
        Toast.makeText(getContext(), "Found " + filteredCount + " promoted properties", Toast.LENGTH_SHORT).show();
    }private void clearAllFilters() {
        adapter.clearFilters();
        updateEmptyState();
        
        // Reset filter button texts
        btnFilterType.setText("Type");
        btnFilterLocation.setText("Location");
        btnFilterPrice.setText("Price");
        
        // Apply fade animation to clear button
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        btnClearFilters.startAnimation(fadeIn);
        
        Toast.makeText(getContext(), "All filters cleared", Toast.LENGTH_SHORT).show();
    }

    private void showOfferTypeFilterDialog() {
        String[] offerTypes = {"All Offers", "FLASH_SALE", "EARLY_BIRD", "SEASONAL", "LIMITED_TIME", "NEW_LISTING"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filter by Offer Type")
                .setItems(offerTypes, (dialog, which) -> {
                    String selectedOfferType = offerTypes[which];
                    if (selectedOfferType.equals("All Offers")) {
                        adapter.filterBySpecialOffers(); // Show all special offers
                    } else {
                        adapter.filterByOfferType(selectedOfferType);
                    }
                    updateEmptyState();
                    Toast.makeText(getContext(), "Filtered by: " + selectedOfferType, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showTypeFilterDialog() {
        String[] types = {"All", "Apartment", "Villa", "House", "Commercial", "Land"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filter by Property Type")
                .setItems(types, (dialog, which) -> {
                    String selectedType = types[which];
                    adapter.filterByType(selectedType);
                    updateEmptyState();
                    btnFilterType.setText("Type: " + selectedType);
                    Toast.makeText(getContext(), "Filtered by: " + selectedType, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showLocationFilterDialog() {
        String[] locations = {"All", "Ramallah", "Jerusalem", "Bethlehem", "Nablus", "Hebron", "Gaza", "Jenin"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filter by Location")
                .setItems(locations, (dialog, which) -> {
                    String selectedLocation = locations[which];
                    adapter.filterByLocation(selectedLocation);
                    updateEmptyState();
                    btnFilterLocation.setText("Location: " + selectedLocation);
                    Toast.makeText(getContext(), "Filtered by: " + selectedLocation, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showPriceFilterDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_price_filter, null);
        
        SeekBar seekBarMin = dialogView.findViewById(R.id.seekbar_min_price);
        SeekBar seekBarMax = dialogView.findViewById(R.id.seekbar_max_price);
        TextView txtMinPrice = dialogView.findViewById(R.id.txt_min_price);
        TextView txtMaxPrice = dialogView.findViewById(R.id.txt_max_price);
        
        // Set max values (in thousands)
        seekBarMin.setMax(1000); // $1M max
        seekBarMax.setMax(1000);
        seekBarMin.setProgress(0); // $0 min
        seekBarMax.setProgress(1000); // $1M max
        
        seekBarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtMinPrice.setText("Min: $" + (progress * 1000));
                if (progress >= seekBarMax.getProgress()) {
                    seekBarMax.setProgress(progress + 1);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        seekBarMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtMaxPrice.setText("Max: $" + (progress * 1000));
                if (progress <= seekBarMin.getProgress()) {
                    seekBarMin.setProgress(progress - 1);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Initialize text
        txtMinPrice.setText("Min: $0");
        txtMaxPrice.setText("Max: $1,000,000");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filter by Price Range")
                .setView(dialogView)
                .setPositiveButton("Apply", (dialog, which) -> {
                    double minPrice = seekBarMin.getProgress() * 1000;
                    double maxPrice = seekBarMax.getProgress() * 1000;
                    adapter.filterByPriceRange(minPrice, maxPrice);
                    updateEmptyState();
                    btnFilterPrice.setText("Price: $" + (int)(minPrice/1000) + "K - $" + (int)(maxPrice/1000) + "K");
                    Toast.makeText(getContext(), "Price filter applied", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Reset", (dialog, which) -> {
                    adapter.filterByPriceRange(0, Double.MAX_VALUE);
                    updateEmptyState();
                    btnFilterPrice.setText("Filter Price");
                    Toast.makeText(getContext(), "Price filter reset", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onPropertyClick(Property property) {
        // Open property details fragment
        PropertyDetailsFragment detailsFragment = PropertyDetailsFragment.newInstance(property);
        
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        );
        transaction.replace(R.id.fragment_container, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onReserveClick(Property property) {
        // Open reservation details fragment
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
    }    @Override
    public void onResume() {
        super.onResume();
        // Refresh properties when returning to fragment
        loadProperties();
    }

    // Custom ConnectionAsyncTask for Properties Fragment
    private class PropertyConnectionTask extends ConnectionAsyncTask {

        public PropertyConnectionTask() {
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
                try {                    List<Property> properties = JsonParser.parseProperties(result);
                    if (properties != null && !properties.isEmpty()) {                        allProperties.clear();
                        allProperties.addAll(properties);
                          // Calculate real promotion data based on actual JSON property values
                        enhancePropertiesWithPromotions(allProperties);
                        
                        // Update static field for sharing with other fragments
                        currentApiProperties.clear();
                        currentApiProperties.addAll(allProperties);
                        
                        adapter.updateProperties(allProperties);
                        updateEmptyState();
                        Toast.makeText(getContext(), "Properties loaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        showError("No properties found");
                    }
                } catch (Exception e) {
                    showError("Error parsing property data: " + e.getMessage());
                }
            } else {
                showError("Failed to load properties. Please check your internet connection.");
            }
        }
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        // If API fails, try to load from local database as fallback
        loadPropertiesFromDatabase();
    }    private void loadPropertiesFromDatabase() {
        allProperties.clear();
        allProperties.addAll(databaseHelper.getAllProperties());
        adapter.updateProperties(allProperties);
        updateEmptyState();
        
        if (allProperties.isEmpty()) {
            Toast.makeText(getContext(), "No local properties found. Please check your internet connection.", Toast.LENGTH_SHORT).show();        }
    }    /**
     * Enhance loaded properties with realistic promotion data based on actual JSON prices
     * @param properties List of properties to enhance with promotions
     */
    private void enhancePropertiesWithPromotions(List<Property> properties) {
        if (properties.isEmpty()) return;
        
        Log.d("PropertiesFragment", "Enhancing " + properties.size() + " properties with real calculated promotions");
        
        // Calculate time periods for offers
        long currentTime = System.currentTimeMillis();
        long oneWeekFromNow = currentTime + (7 * 24 * 60 * 60 * 1000L);
        long twoWeeksFromNow = currentTime + (14 * 24 * 60 * 60 * 1000L);
        long oneMonthFromNow = currentTime + (30 * 24 * 60 * 60L * 1000L);
        
        for (Property property : properties) {
            int jsonPrice = property.getPrice(); // This is the real market price from JSON
            int propertyId = property.getId();
            String propertyType = property.getType();
            
            // Apply realistic promotions based on property characteristics
            if (shouldHavePromotion(property, propertyId, jsonPrice, propertyType)) {
                // Determine promotion type and discount based on property characteristics
                PromotionInfo promo = calculatePromotionInfo(property, propertyId, jsonPrice, propertyType);
                
                // Set the JSON price as the TRUE original price
                property.setOriginalPrice(jsonPrice);
                
                // Calculate realistic discounted price
                int discountedPrice = jsonPrice - (jsonPrice * promo.discountPercentage / 100);
                
                // Update the property price to show the discounted price
                property.setPrice(discountedPrice);
                
                // Set promotion details
                property.setHasSpecialOffer(true);
                property.setOfferType(promo.offerType);
                property.setDiscountPercentage(promo.discountPercentage);
                property.setOfferDescription(promo.description);
                property.setOfferExpiryDate(promo.expiryDate);
                property.setPromoted(promo.isPromoted);
                
                Log.d("PropertiesFragment", String.format(
                    "Real promotion applied to %s: %s - Original: $%d, Discounted: $%d, Savings: $%d (%d%%)",
                    property.getTitle(), promo.offerType, jsonPrice, discountedPrice, 
                    (jsonPrice - discountedPrice), promo.discountPercentage));
            } else if (shouldBePromoted(property, propertyId, jsonPrice, propertyType)) {
                // Property is promoted but has no special offer
                property.setPromoted(true);
                property.setHasSpecialOffer(false);
                Log.d("PropertiesFragment", "Promoted without offer: " + property.getTitle());
            }
        }
    }
    
    /**
     * Determine if a property should have a promotion based on realistic criteria
     */
    private boolean shouldHavePromotion(Property property, int propertyId, int price, String type) {
        // Higher chance for newer listings (higher IDs)
        if (propertyId > 110) return true;
        
        // Higher-priced properties get flash sales
        if (price > 100000 && propertyId % 3 == 0) return true;
        
        // Apartments get early bird offers
        if ("Apartment".equals(type) && propertyId % 4 == 1) return true;
        
        // Villas get seasonal offers
        if ("Villa".equals(type) && propertyId % 5 == 2) return true;
        
        // Random chance for variety (30% of remaining properties)
        return (propertyId * 17) % 10 < 3;
    }
    
    /**
     * Determine if a property should be promoted (without special offer)
     */
    private boolean shouldBePromoted(Property property, int propertyId, int price, String type) {
        // Premium properties get promoted
        if (price > 150000) return true;
        
        // Some apartments get promoted
        if ("Apartment".equals(type) && propertyId % 6 == 0) return true;
        
        // Random promotion for variety
        return (propertyId * 13) % 10 < 2;
    }
    
    /**
     * Calculate realistic promotion information based on property characteristics
     */
    private PromotionInfo calculatePromotionInfo(Property property, int propertyId, int price, String type) {
        PromotionInfo promo = new PromotionInfo();
        long currentTime = System.currentTimeMillis();
        
        // Determine offer type and discount based on property characteristics
        if (propertyId > 115) {
            // New listings get moderate discounts
            promo.offerType = "NEW_LISTING";
            promo.discountPercentage = 8 + (propertyId % 3) * 2; // 8-12%
            promo.description = "New Listing Special - Welcome offer!";
            promo.expiryDate = currentTime + (10 * 24 * 60 * 60 * 1000L); // 10 days
            promo.isPromoted = true;
        } else if (price > 120000) {
            // High-value properties get flash sales
            promo.offerType = "FLASH_SALE";
            promo.discountPercentage = 15 + (propertyId % 4) * 2; // 15-21%
            promo.description = "Flash Sale! Limited time offer - Save big!";
            promo.expiryDate = currentTime + (5 * 24 * 60 * 60 * 1000L); // 5 days
            promo.isPromoted = true;
        } else if ("Villa".equals(type)) {
            // Villas get seasonal offers
            promo.offerType = "SEASONAL";
            promo.discountPercentage = 12 + (propertyId % 3) * 3; // 12-18%
            promo.description = "Winter Special - Best deals of the season!";
            promo.expiryDate = currentTime + (21 * 24 * 60 * 60 * 1000L); // 3 weeks
            promo.isPromoted = propertyId % 2 == 0;
        } else if ("Apartment".equals(type)) {
            // Apartments get early bird offers
            promo.offerType = "EARLY_BIRD";
            promo.discountPercentage = 10 + (propertyId % 4) * 2; // 10-16%
            promo.description = "Early Bird Special - Book now and save!";
            promo.expiryDate = currentTime + (14 * 24 * 60 * 60 * 1000L); // 2 weeks
            promo.isPromoted = true;
        } else {
            // Land gets investment offers
            promo.offerType = "INVESTMENT";
            promo.discountPercentage = 6 + (propertyId % 5) * 2; // 6-14%
            promo.description = "Investment Opportunity - Special pricing!";
            promo.expiryDate = currentTime + (30 * 24 * 60 * 60 * 1000L); // 1 month
            promo.isPromoted = false;
        }
        
        return promo;
    }
    
    /**
     * Helper class to hold promotion information
     */
    private static class PromotionInfo {
        String offerType;
        int discountPercentage;
        String description;
        long expiryDate;
        boolean isPromoted;
    }
    
    /**
     * Get current API properties for sharing with other fragments (like FavoritesFragment)
     * @return List of current API properties, or empty list if none loaded
     */
    public static List<Property> getCurrentApiProperties() {
        return currentApiProperties != null ? new ArrayList<>(currentApiProperties) : new ArrayList<>();
    }
}
