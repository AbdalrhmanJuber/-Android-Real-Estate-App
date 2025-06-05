package com.example.a1211769_courseproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

// Removed Picasso import - using simple image loading

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> reservations;
    private Context context;
    private FragmentManager fragmentManager;
    private DatabaseHelper databaseHelper;

    public ReservationAdapter(Context context, List<Reservation> reservations, FragmentManager fragmentManager) {
        this.context = context;
        this.reservations = reservations;
        this.fragmentManager = fragmentManager;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation_card, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        
        // Apply fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        holder.itemView.startAnimation(fadeIn);
          // Bind property data
        holder.propertyTitle.setText(reservation.getPropertyTitle());
        holder.propertyLocation.setText(reservation.getPropertyLocation());
        holder.propertyPrice.setText(String.format(Locale.getDefault(), "$%.0f/night", reservation.getPropertyPrice()));
          // Load property image - consistent with PropertyAdapter approach
        holder.propertyImage.setImageResource(R.drawable.property_placeholder);
          // Format and display dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.reservationDate.setText("Reserved: " + dateFormat.format(reservation.getReservationDate()));
        holder.checkInDate.setText("Check-in: " + dateFormat.format(reservation.getCheckInDate()));
        holder.checkOutDate.setText("Check-out: " + dateFormat.format(reservation.getCheckOutDate()));
        
        // Set reservation status
        String status = reservation.getStatus();
        holder.statusText.setText(status.toUpperCase());
          // Apply status styling
        switch (status.toLowerCase()) {
            case "confirmed":
                holder.statusText.setBackgroundResource(R.drawable.status_confirmed_background);
                break;
            case "pending":
                holder.statusText.setBackgroundResource(R.drawable.status_pending_background);
                break;
            case "cancelled":
                holder.statusText.setBackgroundResource(R.drawable.status_cancelled_background);
                break;
            default:
                holder.statusText.setBackgroundResource(R.drawable.status_pending_background);
                break;
        }
          // Display notes if available - remove this section since new layout doesn't have notes display
        
        // Set button click listeners
        holder.viewDetailsButton.setOnClickListener(v -> {
            // Navigate to property details
            Fragment propertyDetailsFragment = new PropertyDetailsFragment();
            Bundle args = new Bundle();
            args.putInt("property_id", reservation.getPropertyId());
            propertyDetailsFragment.setArguments(args);
            
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, propertyDetailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
        
        holder.cancelButton.setOnClickListener(v -> {
            if ("cancelled".equalsIgnoreCase(status)) {
                Toast.makeText(context, "This reservation is already cancelled", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Show confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Cancel Reservation")
                    .setMessage("Are you sure you want to cancel this reservation?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        cancelReservation(reservation, position);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    private void cancelReservation(Reservation reservation, int position) {
        try {
            // Update reservation status in database
            boolean success = databaseHelper.updateReservationStatus(reservation.getReservationId(), "cancelled");
            
            if (success) {
                // Update the reservation object
                reservation.setStatus("cancelled");
                
                // Notify adapter of the change
                notifyItemChanged(position);
                
                Toast.makeText(context, "Reservation cancelled successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error cancelling reservation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateReservations(List<Reservation> newReservations) {
        this.reservations = newReservations;
        notifyDataSetChanged();
    }    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        ImageView propertyImage;
        TextView propertyTitle, propertyLocation, propertyPrice;
        TextView reservationDate, checkInDate, checkOutDate;
        TextView statusText;
        Button viewDetailsButton, cancelButton;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            
            propertyImage = itemView.findViewById(R.id.img_reservation_property);
            propertyTitle = itemView.findViewById(R.id.txt_reservation_property_title);
            propertyLocation = itemView.findViewById(R.id.txt_reservation_property_location);
            propertyPrice = itemView.findViewById(R.id.txt_reservation_property_price);
            
            reservationDate = itemView.findViewById(R.id.txt_reservation_date);
            checkInDate = itemView.findViewById(R.id.txt_reservation_time);
            checkOutDate = itemView.findViewById(R.id.txt_reservation_phone);
            
            statusText = itemView.findViewById(R.id.txt_reservation_status);
            
            viewDetailsButton = itemView.findViewById(R.id.btn_view_property);
            cancelButton = itemView.findViewById(R.id.btn_cancel_reservation);
        }
    }
}
