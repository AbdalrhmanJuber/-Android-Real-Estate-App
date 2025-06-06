package com.example.a1211769_courseproject;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> properties;
    private List<Property> propertiesFiltered;
    private Context context;
    private DatabaseHelper databaseHelper;
    private String currentUserEmail;
    private OnPropertyClickListener listener;

    public interface OnPropertyClickListener {
        void onPropertyClick(Property property);
        void onReserveClick(Property property);
    }    public PropertyAdapter(Context context, List<Property> properties, String userEmail) {
        this.context = context;
        this.properties = properties;
        this.propertiesFiltered = new ArrayList<>(properties);
        this.databaseHelper = new DatabaseHelper(context);        this.currentUserEmail = userEmail != null ? userEmail : "";
    }

    public void setOnPropertyClickListener(OnPropertyClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property_card, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertiesFiltered.get(position);
        holder.bind(property);

        // Apply fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        holder.itemView.startAnimation(fadeIn);
    }

    @Override
    public int getItemCount() {
        return propertiesFiltered.size();
    }

    public void filter(String query) {
        propertiesFiltered.clear();
        if (query.isEmpty()) {
            propertiesFiltered.addAll(properties);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Property property : properties) {
                if (property.getTitle().toLowerCase().contains(lowerQuery) ||
                    property.getLocation().toLowerCase().contains(lowerQuery) ||
                    property.getType().toLowerCase().contains(lowerQuery) ||
                    property.getDescription().toLowerCase().contains(lowerQuery)) {
                    propertiesFiltered.add(property);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByType(String type) {
        propertiesFiltered.clear();
        if (type.equals("All")) {
            propertiesFiltered.addAll(properties);
        } else {
            for (Property property : properties) {
                if (property.getType().equalsIgnoreCase(type)) {
                    propertiesFiltered.add(property);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByPriceRange(double minPrice, double maxPrice) {
        propertiesFiltered.clear();
        for (Property property : properties) {
            if (property.getPrice() >= minPrice && property.getPrice() <= maxPrice) {
                propertiesFiltered.add(property);
            }
        }
        notifyDataSetChanged();
    }

    public void filterByLocation(String location) {
        propertiesFiltered.clear();
        if (location.equals("All")) {
            propertiesFiltered.addAll(properties);
        } else {
            for (Property property : properties) {
                if (property.getLocation().toLowerCase().contains(location.toLowerCase())) {
                    propertiesFiltered.add(property);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateProperties(List<Property> newProperties) {
        this.properties = newProperties;
        this.propertiesFiltered = new ArrayList<>(newProperties);
        notifyDataSetChanged();
    }

    class PropertyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProperty;
        private TextView txtPropertyType;
        private ImageButton btnFavorite;
        private TextView txtPropertyTitle;
        private TextView txtPropertyPrice;
        private TextView txtPropertyLocation;
        private TextView txtPropertyArea;
        private TextView txtPropertyBedrooms;
        private TextView txtPropertyBathrooms;
        private TextView txtPropertyDescription;
        private Button btnViewDetails;
        private Button btnReserve;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProperty = itemView.findViewById(R.id.img_property);
            txtPropertyType = itemView.findViewById(R.id.txt_property_type);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            txtPropertyTitle = itemView.findViewById(R.id.txt_property_title);
            txtPropertyPrice = itemView.findViewById(R.id.txt_property_price);
            txtPropertyLocation = itemView.findViewById(R.id.txt_property_location);
            txtPropertyArea = itemView.findViewById(R.id.txt_property_area);
            txtPropertyBedrooms = itemView.findViewById(R.id.txt_property_bedrooms);
            txtPropertyBathrooms = itemView.findViewById(R.id.txt_property_bathrooms);
            txtPropertyDescription = itemView.findViewById(R.id.txt_property_description);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            btnReserve = itemView.findViewById(R.id.btn_reserve);
        }

        public void bind(Property property) {
            // Set property image (placeholder for now)
            imgProperty.setImageResource(R.drawable.property_placeholder);
            
            // Set property type
            txtPropertyType.setText(property.getType());
            
            // Set property details
            txtPropertyTitle.setText(property.getTitle());
            
            // Format price
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
            txtPropertyPrice.setText(formatter.format(property.getPrice()));
            
            txtPropertyLocation.setText(property.getLocation());
            txtPropertyArea.setText(property.getArea() + " m²");
            txtPropertyBedrooms.setText(String.valueOf(property.getBedrooms()));
            txtPropertyBathrooms.setText(String.valueOf(property.getBathrooms()));
            txtPropertyDescription.setText(property.getDescription());

            // Check if property is in favorites
            boolean isFavorite = databaseHelper.isPropertyInFavorites(currentUserEmail, property.getId());
            btnFavorite.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

            // Set click listeners
            btnFavorite.setOnClickListener(v -> {
                toggleFavorite(property);
            });

            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPropertyClick(property);
                }
            });

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
                
                if (listener != null) {
                    listener.onReserveClick(property);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPropertyClick(property);
                }
            });
        }        private void toggleFavorite(Property property) {
            // Validate user email
            if (currentUserEmail == null || currentUserEmail.trim().isEmpty()) {
                Toast.makeText(context, "Please log in to add favorites", Toast.LENGTH_SHORT).show();
                return;
            }
              // Validate property
            if (property == null || property.getId() <= 0) {
                Toast.makeText(context, "Invalid property", Toast.LENGTH_SHORT).show();
                return;
            }
            
            boolean isFavorite = databaseHelper.isPropertyInFavorites(currentUserEmail, property.getId());
              if (isFavorite) {
                // Remove from favorites
                boolean removed = databaseHelper.removeFromFavorites(currentUserEmail, property.getId());
                if (removed) {
                    btnFavorite.setImageResource(R.drawable.ic_heart_outline);
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                }            } else {
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
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
