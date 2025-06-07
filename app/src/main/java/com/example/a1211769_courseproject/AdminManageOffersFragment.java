package com.example.a1211769_courseproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminManageOffersFragment extends Fragment {
    
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private PropertyOffersAdapter adapter;
    private List<Property> propertyList;
    private SwipeRefreshLayout swipeRefresh;
    private static final String API_URL = "https://mocki.io/v1/8345f53d-b99e-4d4d-b4cb-eea3042aa04f";    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_offers, container, false);
        
        databaseHelper = new DatabaseHelper(getContext());
        initializeViews(view);
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadProperties();
    }
    
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_properties_offers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this::loadProperties);
        swipeRefresh.setColorSchemeResources(R.color.primary_color);
        
        propertyList = new ArrayList<>();
        adapter = new PropertyOffersAdapter(propertyList, this::showSpecialOfferDialog, this::togglePromotedStatus);
        recyclerView.setAdapter(adapter);
    }
    
    private void loadProperties() {
        // Load properties from API
        new AdminPropertyConnectionTask().execute(API_URL);
    }
    
    private void showSpecialOfferDialog(Property property) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_special_offer, null);
        EditText etOfferDescription = dialogView.findViewById(R.id.et_offer_description);
        EditText etDiscountPercent = dialogView.findViewById(R.id.et_discount_percent);
          // Pre-fill current offer if exists
        if (property.getOfferDescription() != null && !property.getOfferDescription().isEmpty()) {
            etOfferDescription.setText(property.getOfferDescription());
        }
        
        new AlertDialog.Builder(getContext())
                .setTitle("Special Offer for " + property.getTitle())
                .setView(dialogView)
                .setPositiveButton("Save Offer", (dialog, which) -> {
                    String offerDescription = etOfferDescription.getText().toString().trim();
                    String discountText = etDiscountPercent.getText().toString().trim();
                    
                    if (!offerDescription.isEmpty()) {                        // Save special offer to database
                        boolean success = databaseHelper.updatePropertySpecialOffer(property.getId(), offerDescription);
                        if (success) {
                            property.setOfferDescription(offerDescription);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Special offer saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to save special offer", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please enter offer description", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Remove Offer", (dialog, which) -> removeSpecialOffer(property))
                .show();
    }
      private void removeSpecialOffer(Property property) {
        boolean success = databaseHelper.updatePropertySpecialOffer(property.getId(), null);
        if (success) {
            property.setOfferDescription(null);
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Special offer removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to remove special offer", Toast.LENGTH_SHORT).show();
        }
    }
      private void togglePromotedStatus(Property property) {
        boolean newPromotedStatus = !property.isPromoted();
        boolean success = databaseHelper.setPropertyPromoted(property.getId(), newPromotedStatus);
        
        if (success) {
            property.setPromoted(newPromotedStatus);
            adapter.notifyDataSetChanged();
            String message = newPromotedStatus ? "Property promoted to featured" : "Property removed from featured";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to update promotion status", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateEmptyState() {
        View emptyState = requireView().findViewById(R.id.empty_state);
        if (propertyList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    // Custom ConnectionAsyncTask for Admin Manage Offers Fragment
    private class AdminPropertyConnectionTask extends ConnectionAsyncTask {
        
        public AdminPropertyConnectionTask() {
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
                        propertyList.clear();
                        propertyList.addAll(properties);
                        
                        // Load existing offers and promotion status from database
                        for (Property property : propertyList) {
                            // Load offer from database if exists
                            String existingOffer = databaseHelper.getPropertySpecialOffer(property.getId());
                            if (existingOffer != null && !existingOffer.trim().isEmpty()) {
                                property.setOfferDescription(existingOffer);
                            }
                            
                            // Load promotion status from database
                            boolean isPromoted = databaseHelper.isPropertyPromoted(property.getId());
                            property.setPromoted(isPromoted);
                        }
                        
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                        
                        Toast.makeText(getContext(), "Properties loaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        showError("No properties found");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Error parsing properties data");
                }
            } else {
                showError("Failed to load properties. Please check your internet connection.");
            }
        }
    }
    
    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
        updateEmptyState();
    }
}
