package com.example.a1211769_courseproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminAllReservationsFragment extends Fragment {
    
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private AdminReservationAdapter adapter;
    private List<Reservation> reservationList;
    private TextView tvTotalReservations;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_all_reservations, container, false);
        
        databaseHelper = new DatabaseHelper(getContext());
        initializeViews(view);
        loadAllReservations();
        
        return view;
    }
    
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_all_reservations);
        tvTotalReservations = view.findViewById(R.id.tv_total_reservations_count);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        reservationList = new ArrayList<>();
        adapter = new AdminReservationAdapter(reservationList);
        recyclerView.setAdapter(adapter);
    }
    
    private void loadAllReservations() {
        List<Reservation> allReservations = databaseHelper.getAllReservations();
        reservationList.clear();
        reservationList.addAll(allReservations);
        
        adapter.notifyDataSetChanged();
        
        // Update total count
        tvTotalReservations.setText(String.valueOf(reservationList.size()));
        
        // Show empty state if no reservations
        View emptyState = getView().findViewById(R.id.empty_state);
        if (reservationList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
