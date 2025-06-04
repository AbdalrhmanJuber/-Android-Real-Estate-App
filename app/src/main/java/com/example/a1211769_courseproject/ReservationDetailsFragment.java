package com.example.a1211769_courseproject;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationDetailsFragment extends Fragment {

    private static final String ARG_PROPERTY = "property";
    
    private Property property;
    private String currentUserEmail;
    
    // Views
    private TextView txtPropertyTitle;
    private TextView txtPropertyPrice;
    private TextView txtPropertyLocation;
    private EditText editReservationDate;
    private EditText editVisitTime;
    private EditText editContactPhone;
    private EditText editSpecialRequests;
    private Button btnConfirmReservation;
    private Button btnCancel;
    
    private Calendar selectedDate;

    public static ReservationDetailsFragment newInstance(Property property) {
        ReservationDetailsFragment fragment = new ReservationDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY, property);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            property = (Property) getArguments().getSerializable(ARG_PROPERTY);
        }
        
        // Get current user email
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", "");
        
        selectedDate = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_details, container, false);
        
        // Apply slide-up animation
        Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up);
        view.startAnimation(slideUp);
        
        initializeViews(view);
        populatePropertyData();
        setupClickListeners();
        
        return view;
    }

    private void initializeViews(View view) {
        txtPropertyTitle = view.findViewById(R.id.txt_reservation_property_title);
        txtPropertyPrice = view.findViewById(R.id.txt_reservation_property_price);
        txtPropertyLocation = view.findViewById(R.id.txt_reservation_property_location);
        editReservationDate = view.findViewById(R.id.edit_reservation_date);
        editVisitTime = view.findViewById(R.id.edit_visit_time);
        editContactPhone = view.findViewById(R.id.edit_contact_phone);
        editSpecialRequests = view.findViewById(R.id.edit_special_requests);
        btnConfirmReservation = view.findViewById(R.id.btn_confirm_reservation);
        btnCancel = view.findViewById(R.id.btn_cancel_reservation);
    }

    private void populatePropertyData() {
        if (property == null) return;
        
        txtPropertyTitle.setText(property.getTitle());
        txtPropertyLocation.setText(property.getLocation());
        
        // Format and set price
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        txtPropertyPrice.setText(formatter.format(property.getPrice()));
        
        // Set default date to tomorrow
        selectedDate.add(Calendar.DAY_OF_MONTH, 1);
        updateDateDisplay();
        
        // Set default time
        editVisitTime.setText("10:00 AM");
    }

    private void setupClickListeners() {
        editReservationDate.setOnClickListener(v -> showDatePicker());
        
        btnConfirmReservation.setOnClickListener(v -> confirmReservation());
        
        btnCancel.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateDisplay();
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        editReservationDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void confirmReservation() {
        // Validate input
        String visitTime = editVisitTime.getText().toString().trim();
        String contactPhone = editContactPhone.getText().toString().trim();
        
        if (visitTime.isEmpty()) {
            editVisitTime.setError("Please enter visit time");
            editVisitTime.requestFocus();
            return;
        }
        
        if (contactPhone.isEmpty()) {
            editContactPhone.setError("Please enter contact phone");
            editContactPhone.requestFocus();
            return;
        }
        
        // Validate phone number format
        if (!isValidPhoneNumber(contactPhone)) {
            editContactPhone.setError("Please enter a valid phone number");
            editContactPhone.requestFocus();
            return;
        }
        
        // Create reservation (in a real app, this would save to database)
        String specialRequests = editSpecialRequests.getText().toString().trim();
        
        // Show confirmation
        Toast.makeText(getContext(), 
            "Reservation confirmed!\nProperty: " + property.getTitle() + 
            "\nDate: " + editReservationDate.getText().toString() + 
            "\nTime: " + visitTime, 
            Toast.LENGTH_LONG).show();
        
        // In a real implementation, you would save this to the database
        // databaseHelper.insertReservation(currentUserEmail, property.getId(), selectedDate.getTime(), visitTime, contactPhone, specialRequests);
        
        // Go back to properties list
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        // Simple phone number validation
        // Should contain only digits, spaces, dashes, and parentheses
        // Should be at least 10 characters long
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return cleanPhone.matches("\\d{10,}");
    }
}
