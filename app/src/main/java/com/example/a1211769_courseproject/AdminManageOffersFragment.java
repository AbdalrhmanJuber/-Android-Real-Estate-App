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

import java.util.ArrayList;
import java.util.List;

public class AdminManageOffersFragment extends Fragment {
    
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private PropertyOffersAdapter adapter;
    private List<Property> propertyList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_offers, container, false);
        
        databaseHelper = new DatabaseHelper(getContext());
        initializeViews(view);
        loadProperties();
        
        return view;
    }
    
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_properties_offers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        propertyList = new ArrayList<>();
        adapter = new PropertyOffersAdapter(propertyList, this::showSpecialOfferDialog, this::togglePromotedStatus);
        recyclerView.setAdapter(adapter);
    }
    
    private void loadProperties() {
        // For now, we'll create sample properties. In a real app, you'd load from database
        propertyList.clear();
        
        // Add sample properties (you can modify this to load from your JSON data)
        try {
            if (getArguments() != null && getArguments().getString("jsonData") != null) {
                String jsonData = getArguments().getString("jsonData");
                JsonParser parser = new JsonParser();
                List<Property> properties = parser.parseProperties(jsonData);
                propertyList.addAll(properties);
            }
        } catch (Exception e) {
            // If JSON parsing fails, show empty state
            e.printStackTrace();
        }
        
        adapter.notifyDataSetChanged();
        
        // Show empty state if no properties
        View emptyState = getView().findViewById(R.id.empty_state);
        if (propertyList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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
}
