package com.example.a1211769_courseproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Apply fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(fadeIn);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get user email from arguments
        Bundle args = getArguments();
        if (args != null) {
            String userEmail = args.getString("userEmail");
            TextView welcomeText = view.findViewById(R.id.welcome_message);
            if (userEmail != null && welcomeText != null) {
                welcomeText.setText("Welcome, " + userEmail.split("@")[0] + "!");
            }
        }
        
        // Apply pulse animation to company logo
        View companyLogo = view.findViewById(R.id.company_logo);
        if (companyLogo != null) {
            Animation pulse = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
            companyLogo.startAnimation(pulse);
        }
    }
}
