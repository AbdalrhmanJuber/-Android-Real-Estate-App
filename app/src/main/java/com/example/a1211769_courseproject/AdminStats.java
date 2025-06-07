package com.example.a1211769_courseproject;

import java.util.ArrayList;
import java.util.List;

public class AdminStats {
    public int totalUsers;
    public int totalReservations;
    public int maleUsers;
    public int femaleUsers;
    public List<CountryStats> topCountries;
    
    public AdminStats() {
        this.totalUsers = 0;
        this.totalReservations = 0;
        this.maleUsers = 0;
        this.femaleUsers = 0;
        this.topCountries = new ArrayList<>();
    }
    
    public double getMalePercentage() {
        if (totalUsers == 0) return 0;
        return (double) maleUsers / totalUsers * 100;
    }
    
    public double getFemalePercentage() {
        if (totalUsers == 0) return 0;
        return (double) femaleUsers / totalUsers * 100;
    }
    
    public static class CountryStats {
        public String countryName;
        public int reservationCount;
        
        public CountryStats(String countryName, int reservationCount) {
            this.countryName = countryName;
            this.reservationCount = reservationCount;
        }
    }
}
