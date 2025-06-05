package com.example.a1211769_courseproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment {    private RecyclerView reservationsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyStateText;
    private ImageView emptyStateImage;
    private ReservationAdapter reservationAdapter;
    private DatabaseHelper databaseHelper;
    private List<Reservation> reservationsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservations, container, false);
        
        // Initialize components
        initializeViews(view);
        setupRecyclerView();
        loadReservations();
        
        // Apply entrance animation
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        view.startAnimation(slideIn);
        
        return view;
    }    private void initializeViews(View view) {
        reservationsRecyclerView = view.findViewById(R.id.reservations_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        emptyStateText = view.findViewById(R.id.empty_state_text);
        emptyStateImage = view.findViewById(R.id.empty_state_image);
        
        databaseHelper = new DatabaseHelper(getContext());
        reservationsList = new ArrayList<>();
        
        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadReservations);        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary_color,
                R.color.accent
        );
    }

    private void setupRecyclerView() {
        reservationAdapter = new ReservationAdapter(getContext(), reservationsList, getParentFragmentManager());
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reservationsRecyclerView.setAdapter(reservationAdapter);
        
        // Add item animation
        reservationsRecyclerView.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());
    }    private void loadReservations() {
        try {
            // Get current user email from SharedPreferences
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("email", "");
            
            // Debug logging
            android.util.Log.d("ReservationsFragment", "User email from SharedPrefs: '" + userEmail + "'");
            
            if (userEmail.isEmpty()) {
                showEmptyState("Please login to view your reservations");
                return;
            }
            
            // Load reservations from database
            List<Reservation> reservations = databaseHelper.getUserReservations(userEmail);
            
            // Debug logging
            android.util.Log.d("ReservationsFragment", "Number of reservations found: " + reservations.size());
            for (int i = 0; i < reservations.size(); i++) {
                Reservation r = reservations.get(i);
                android.util.Log.d("ReservationsFragment", "Reservation " + i + ": " + r.getPropertyTitle() + " - " + r.getStatus());
            }
            
            // Update UI based on results
            if (reservations.isEmpty()) {
                showEmptyState("You have no reservations yet.\nStart exploring properties to make your first reservation!");
            } else {
                showReservations(reservations);
            }
            
        } catch (Exception e) {
            android.util.Log.e("ReservationsFragment", "Error loading reservations", e);
            Toast.makeText(getContext(), "Error loading reservations: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            showEmptyState("Error loading reservations.\nPlease try again.");
        } finally {
            // Stop refresh animation
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }    private void showReservations(List<Reservation> reservations) {
        reservationsList.clear();
        reservationsList.addAll(reservations);
        
        reservationsRecyclerView.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
        emptyStateImage.setVisibility(View.GONE);
        
        reservationAdapter.updateReservations(reservationsList);
        
        // Apply staggered animation to items
        for (int i = 0; i < Math.min(reservations.size(), 5); i++) {
            final int position = i;
            reservationsRecyclerView.postDelayed(() -> {
                if (reservationAdapter != null) {
                    reservationAdapter.notifyItemChanged(position);
                }
            }, i * 100);
        }
    }    private void showEmptyState(String message) {
        reservationsRecyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.VISIBLE);
        emptyStateImage.setVisibility(View.VISIBLE);
        emptyStateText.setText(message);
        
        // Apply fade-in animation to empty state
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        emptyStateText.startAnimation(fadeIn);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh reservations when fragment becomes visible
        loadReservations();
    }
}
