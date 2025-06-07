package com.example.a1211769_courseproject;

public class User {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String gender;
    private String country;
    private String city;
    private String phoneNumber;
    private String role; // "USER" or "ADMIN"    // Constructors
    public User() {
        this.role = "USER"; // Default role
    }

    public User(String email, String firstName, String lastName, String password, 
                String gender, String country, String city, String phoneNumber) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.gender = gender;
        this.country = country;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.role = "USER"; // Default role
    }

    public User(String email, String firstName, String lastName, String password, 
                String gender, String country, String city, String phoneNumber, String role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.gender = gender;
        this.country = country;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
