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
import java.util.List;
import java.util.Locale;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<Property> favoriteProperties;
    private Context context;
    private DatabaseHelper databaseHelper;
    private String currentUserEmail;
    private OnFavoriteActionListener listener;

    public interface OnFavoriteActionListener {
        void onPropertyClick(Property property);
        void onReserveClick(Property property);
        void onRemoveFromFavorites(Property property, int position);
    }

    public FavoritesAdapter(Context context, List<Property> favoriteProperties, String userEmail) {
        this.context = context;
        this.favoriteProperties = favoriteProperties;
        this.databaseHelper = new DatabaseHelper(context);
        this.currentUserEmail = userEmail;
    }

    public void setOnFavoriteActionListener(OnFavoriteActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property_card, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Property property = favoriteProperties.get(position);
        holder.bind(property, position);

        // Apply fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        holder.itemView.startAnimation(fadeIn);
    }

    @Override
    public int getItemCount() {
        return favoriteProperties.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < favoriteProperties.size()) {
            favoriteProperties.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favoriteProperties.size());
        }
    }

    public void updateFavorites(List<Property> newFavorites) {
        this.favoriteProperties = newFavorites;
        notifyDataSetChanged();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
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

        public FavoriteViewHolder(@NonNull View itemView) {
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

        public void bind(Property property, int position) {
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

            // Always show heart as filled since these are favorite properties
            btnFavorite.setImageResource(R.drawable.ic_heart_filled);

            // Set click listeners
            btnFavorite.setOnClickListener(v -> {
                removeFromFavorites(property, position);
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
        }

        private void removeFromFavorites(Property property, int position) {
            // Remove from database
            boolean removed = databaseHelper.removeFromFavorites(currentUserEmail, property.getId());
            if (removed) {
                // Apply heart animation before removing
                ScaleAnimation heartAnimation = new ScaleAnimation(
                    1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                );
                heartAnimation.setDuration(200);
                heartAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Notify the fragment to remove the item
                        if (listener != null) {
                            listener.onRemoveFromFavorites(property, position);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                
                btnFavorite.startAnimation(heartAnimation);
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
