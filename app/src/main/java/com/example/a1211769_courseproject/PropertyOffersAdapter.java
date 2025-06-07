package com.example.a1211769_courseproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PropertyOffersAdapter extends RecyclerView.Adapter<PropertyOffersAdapter.PropertyOfferViewHolder> {
    
    private List<Property> propertyList;
    private OnOfferClickListener offerClickListener;
    private OnPromoteClickListener promoteClickListener;
    
    public interface OnOfferClickListener {
        void onOfferClick(Property property);
    }
    
    public interface OnPromoteClickListener {
        void onPromoteClick(Property property);
    }
    
    public PropertyOffersAdapter(List<Property> propertyList, OnOfferClickListener offerClickListener, OnPromoteClickListener promoteClickListener) {
        this.propertyList = propertyList;
        this.offerClickListener = offerClickListener;
        this.promoteClickListener = promoteClickListener;
    }
    
    @NonNull
    @Override
    public PropertyOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_offer, parent, false);
        return new PropertyOfferViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PropertyOfferViewHolder holder, int position) {
        Property property = propertyList.get(position);
        holder.bind(property);
    }
    
    @Override
    public int getItemCount() {
        return propertyList.size();
    }
    
    class PropertyOfferViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvLocation, tvPrice, tvOfferStatus, tvPromotedStatus;
        private ImageView imgProperty;
        private Button btnManageOffer, btnTogglePromote;
        
        public PropertyOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_property_title);
            tvLocation = itemView.findViewById(R.id.tv_property_location);
            tvPrice = itemView.findViewById(R.id.tv_property_price);
            tvOfferStatus = itemView.findViewById(R.id.tv_offer_status);
            tvPromotedStatus = itemView.findViewById(R.id.tv_promoted_status);
            imgProperty = itemView.findViewById(R.id.img_property);
            btnManageOffer = itemView.findViewById(R.id.btn_manage_offer);
            btnTogglePromote = itemView.findViewById(R.id.btn_toggle_promote);
        }
          public void bind(Property property) {
            tvTitle.setText(property.getTitle());
            tvLocation.setText(property.getLocation());
            tvPrice.setText("$" + String.format("%.2f", (double)property.getPrice()) + "/night");
            
            // Load property image (you can implement image loading here)
            // For now, we'll use a placeholder
            imgProperty.setImageResource(R.drawable.ic_home);
              // Update offer status
            if (property.getOfferDescription() != null && !property.getOfferDescription().isEmpty()) {
                tvOfferStatus.setText("Has Special Offer");
                tvOfferStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.accent));
                btnManageOffer.setText("Edit Offer");
            } else {
                tvOfferStatus.setText("No Special Offer");
                tvOfferStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary));
                btnManageOffer.setText("Add Offer");
            }
            
            // Update promoted status
            if (property.isPromoted()) {
                tvPromotedStatus.setText("Featured");
                tvPromotedStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.primary));
                btnTogglePromote.setText("Remove from Featured");
                btnTogglePromote.setBackgroundResource(R.drawable.button_secondary);
            } else {
                tvPromotedStatus.setText("Not Featured");
                tvPromotedStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary));
                btnTogglePromote.setText("Add to Featured");
                btnTogglePromote.setBackgroundResource(R.drawable.button_primary);
            }
            
            // Set click listeners
            btnManageOffer.setOnClickListener(v -> {
                if (offerClickListener != null) {
                    offerClickListener.onOfferClick(property);
                }
            });
            
            btnTogglePromote.setOnClickListener(v -> {
                if (promoteClickListener != null) {
                    promoteClickListener.onPromoteClick(property);
                }
            });
            
            // Add animation
            itemView.setAlpha(0f);
            itemView.animate().alpha(1f).setDuration(300).setStartDelay(getAdapterPosition() * 50).start();
        }
    }
}
