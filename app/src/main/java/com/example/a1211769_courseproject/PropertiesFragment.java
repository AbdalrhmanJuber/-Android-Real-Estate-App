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

public class PropertiesFragment extends Fragment implements PropertyAdapter.OnPropertyClickListener {

    private RecyclerView recyclerProperties;
    private PropertyAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout layoutEmpty;
    private EditText editSearch;
    private Button btnFilterType, btnFilterLocation, btnFilterPrice;
    private DatabaseHelper databaseHelper;
    private List<Property> allProperties;
    private String currentUserEmail;
    private static final String API_URL = "https://mocki.io/v1/8345f53d-b99e-4d4d-b4cb-eea3042aa04f";

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
    }

    private void initializeViews(View view) {
        recyclerProperties = view.findViewById(R.id.recycler_properties);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        editSearch = view.findViewById(R.id.edit_search);
        btnFilterType = view.findViewById(R.id.btn_filter_type);
        btnFilterLocation = view.findViewById(R.id.btn_filter_location);
        btnFilterPrice = view.findViewById(R.id.btn_filter_price);
          databaseHelper = new DatabaseHelper(getContext());
        
        // Get current user email from SharedPreferences with debug logging
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
        
        // Debug logging for SharedPreferences
        Log.d("PropertiesFragment", "SharedPreferences debug:");
        Log.d("PropertiesFragment", "Current user email from UserPrefs: '" + currentUserEmail + "'");
        Log.d("PropertiesFragment", "All UserPrefs keys: " + prefs.getAll().keySet().toString());
        
        // Also check login_preferences for comparison
        SharedPreferences loginPrefs = getActivity().getSharedPreferences("login_preferences", getContext().MODE_PRIVATE);
        String loginEmail = loginPrefs.getString("email", "");
        Log.d("PropertiesFragment", "Email from login_preferences: '" + loginEmail + "'");
          // If UserPrefs is empty but login_preferences has email, copy it over
        if ((currentUserEmail == null || currentUserEmail.trim().isEmpty()) && 
            (loginEmail != null && !loginEmail.trim().isEmpty())) {
            Log.d("PropertiesFragment", "Copying email from login_preferences to UserPrefs");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", loginEmail);
            editor.apply();
            currentUserEmail = loginEmail;
        }
        
        // Test database connectivity and user verification
        if (currentUserEmail != null && !currentUserEmail.trim().isEmpty()) {
            User user = databaseHelper.getUserByEmail(currentUserEmail);
            if (user != null) {
                Log.d("PropertiesFragment", "User found in database: " + user.getEmail() + " (ID: " + user.getId() + ")");
            } else {
                Log.w("PropertiesFragment", "User NOT found in database for email: " + currentUserEmail);
            }
        }

        // Call debug method
        debugSharedPreferences();
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
        });

        // Filter buttons
        btnFilterType.setOnClickListener(v -> showTypeFilterDialog());
        btnFilterLocation.setOnClickListener(v -> showLocationFilterDialog());
        btnFilterPrice.setOnClickListener(v -> showPriceFilterDialog());
    }    private void loadProperties() {
        swipeRefresh.setRefreshing(true);
        
        // Load properties from API instead of database
        new PropertyConnectionTask().execute(API_URL);
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            recyclerProperties.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerProperties.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
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
                try {
                    List<Property> properties = JsonParser.parseProperties(result);
                    if (properties != null && !properties.isEmpty()) {
                        allProperties.clear();
                        allProperties.addAll(properties);
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
    }

    private void loadPropertiesFromDatabase() {
        allProperties.clear();
        allProperties.addAll(databaseHelper.getAllProperties());
        adapter.updateProperties(allProperties);
        updateEmptyState();
        
        if (allProperties.isEmpty()) {
            Toast.makeText(getContext(), "No local properties found. Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    // Debug method to check all SharedPreferences
    private void debugSharedPreferences() {
        Log.d("PropertiesFragment", "=== SHARED PREFERENCES DEBUG ===");
        
        // Check UserPrefs
        SharedPreferences userPrefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        Log.d("PropertiesFragment", "UserPrefs all keys: " + userPrefs.getAll().toString());
        
        // Check login_preferences
        SharedPreferences loginPrefs = getActivity().getSharedPreferences("login_preferences", getContext().MODE_PRIVATE);
        Log.d("PropertiesFragment", "login_preferences all keys: " + loginPrefs.getAll().toString());
        
        // Check if there are any other SharedPreferences files
        try {
            SharedPreferences defaultPrefs = getActivity().getSharedPreferences(getActivity().getPackageName() + "_preferences", getContext().MODE_PRIVATE);
            Log.d("PropertiesFragment", "Default prefs all keys: " + defaultPrefs.getAll().toString());
        } catch (Exception e) {
            Log.d("PropertiesFragment", "No default prefs found");
        }
        
        Log.d("PropertiesFragment", "Final currentUserEmail: '" + currentUserEmail + "'");
    }
}
