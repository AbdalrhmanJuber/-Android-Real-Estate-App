package com.example.a1211769_courseproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminDeleteCustomersFragment extends Fragment {
    
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private CustomerManagementAdapter adapter;
    private List<User> customerList;
      @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_delete_customers, container, false);
        
        databaseHelper = new DatabaseHelper(getContext());
        initializeViews(view);
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCustomers();
    }
    
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_customers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        customerList = new ArrayList<>();
        adapter = new CustomerManagementAdapter(customerList, this::showDeleteConfirmation);
        recyclerView.setAdapter(adapter);
    }
    
    private void loadCustomers() {
        List<User> allUsers = databaseHelper.getAllUsers();
        customerList.clear();
        
        // Filter out admin users - only show regular customers
        for (User user : allUsers) {
            if (!user.isAdmin()) {
                customerList.add(user);
            }
        }
        
        adapter.notifyDataSetChanged();
          // Show empty state if no customers
        View emptyState = requireView().findViewById(R.id.empty_state);
        if (customerList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void showDeleteConfirmation(User user) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Customer")
                .setMessage("Are you sure you want to delete " + user.getEmail() + "?\n\nThis action cannot be undone and will also delete all their reservations.")
                .setPositiveButton("Delete", (dialog, which) -> deleteCustomer(user))
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.ic_warning)
                .show();
    }
    
    private void deleteCustomer(User user) {
        boolean success = databaseHelper.deleteUser(user.getEmail());
        
        if (success) {
            Toast.makeText(getContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show();
            loadCustomers(); // Refresh the list
        } else {
            Toast.makeText(getContext(), "Failed to delete customer", Toast.LENGTH_SHORT).show();
        }
    }
}
