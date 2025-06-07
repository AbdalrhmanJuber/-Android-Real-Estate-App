package com.example.a1211769_courseproject;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;

public class AdminDashboardFragment extends Fragment {
    
    private DatabaseHelper databaseHelper;
    private AdminStats adminStats;
    
    // Views
    private TextView tvTotalUsers, tvTotalReservations;
    private TextView tvMaleCount, tvFemaleCount, tvMalePercent, tvFemalePercent;
    private ProgressBar progressMale, progressFemale;
    private TextView tvTopCountry1, tvTopCountry2, tvTopCountry3;
    private TextView tvTopCountryCount1, tvTopCountryCount2, tvTopCountryCount3;
    private CardView cardUsers, cardReservations, cardGender, cardCountries;
      @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        
        databaseHelper = new DatabaseHelper(getContext());
        initializeViews(view);
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadStats();
    }
    
    private void initializeViews(View view) {
        // User and reservation cards
        cardUsers = view.findViewById(R.id.card_total_users);
        cardReservations = view.findViewById(R.id.card_total_reservations);
        cardGender = view.findViewById(R.id.card_gender_stats);
        cardCountries = view.findViewById(R.id.card_top_countries);
        
        tvTotalUsers = view.findViewById(R.id.tv_total_users);
        tvTotalReservations = view.findViewById(R.id.tv_total_reservations);
        
        // Gender statistics
        tvMaleCount = view.findViewById(R.id.tv_male_count);
        tvFemaleCount = view.findViewById(R.id.tv_female_count);
        tvMalePercent = view.findViewById(R.id.tv_male_percent);
        tvFemalePercent = view.findViewById(R.id.tv_female_percent);
        progressMale = view.findViewById(R.id.progress_male);
        progressFemale = view.findViewById(R.id.progress_female);
        
        // Top countries
        tvTopCountry1 = view.findViewById(R.id.tv_top_country_1);
        tvTopCountry2 = view.findViewById(R.id.tv_top_country_2);
        tvTopCountry3 = view.findViewById(R.id.tv_top_country_3);
        tvTopCountryCount1 = view.findViewById(R.id.tv_top_country_count_1);
        tvTopCountryCount2 = view.findViewById(R.id.tv_top_country_count_2);
        tvTopCountryCount3 = view.findViewById(R.id.tv_top_country_count_3);
    }
    
    private void loadStats() {
        adminStats = databaseHelper.getAdminStats();
        displayStats();
        animateCards();
    }
    
    private void displayStats() {
        DecimalFormat df = new DecimalFormat("#.#");
        
        // Display basic stats
        tvTotalUsers.setText(String.valueOf(adminStats.totalUsers));
        tvTotalReservations.setText(String.valueOf(adminStats.totalReservations));
        
        // Display gender statistics
        tvMaleCount.setText(String.valueOf(adminStats.maleUsers));
        tvFemaleCount.setText(String.valueOf(adminStats.femaleUsers));
        tvMalePercent.setText(df.format(adminStats.getMalePercentage()) + "%");
        tvFemalePercent.setText(df.format(adminStats.getFemalePercentage()) + "%");
        
        // Animate progress bars
        animateProgressBar(progressMale, (int) adminStats.getMalePercentage());
        animateProgressBar(progressFemale, (int) adminStats.getFemalePercentage());
        
        // Display top countries
        if (adminStats.topCountries.size() >= 1) {
            tvTopCountry1.setText(adminStats.topCountries.get(0).countryName);
            tvTopCountryCount1.setText(String.valueOf(adminStats.topCountries.get(0).reservationCount));
        } else {
            tvTopCountry1.setText("N/A");
            tvTopCountryCount1.setText("0");
        }
        
        if (adminStats.topCountries.size() >= 2) {
            tvTopCountry2.setText(adminStats.topCountries.get(1).countryName);
            tvTopCountryCount2.setText(String.valueOf(adminStats.topCountries.get(1).reservationCount));
        } else {
            tvTopCountry2.setText("N/A");
            tvTopCountryCount2.setText("0");
        }
        
        if (adminStats.topCountries.size() >= 3) {
            tvTopCountry3.setText(adminStats.topCountries.get(2).countryName);
            tvTopCountryCount3.setText(String.valueOf(adminStats.topCountries.get(2).reservationCount));
        } else {
            tvTopCountry3.setText("N/A");
            tvTopCountryCount3.setText("0");
        }
    }
    
    private void animateProgressBar(ProgressBar progressBar, int targetProgress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, targetProgress);
        animation.setDuration(1500);
        animation.start();
    }
    
    private void animateCards() {
        CardView[] cards = {cardUsers, cardReservations, cardGender, cardCountries};
        
        for (int i = 0; i < cards.length; i++) {
            final CardView card = cards[i];
            card.setAlpha(0f);
            card.setTranslationY(100f);
            
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(i * 150)
                .start();
        }
    }
}
