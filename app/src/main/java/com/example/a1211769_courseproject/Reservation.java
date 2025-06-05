package com.example.a1211769_courseproject;

import java.io.Serializable;

public class Reservation implements Serializable {
    private int reservationId;
    private String userEmail;
    private int propertyId;
    private String reservationDate;
    private String checkInDate;
    private String checkOutDate;
    private String status;
    private String notes;
    private String visitTime;
    private String contactPhone;
    
    // Property details (for display purposes)
    private String propertyTitle;
    private String propertyLocation;
    private int propertyPrice;
    private String propertyType;
    private String propertyImageUrl;

    public Reservation() {}

    public Reservation(int reservationId, String userEmail, int propertyId, 
                      String reservationDate, String checkInDate, String checkOutDate,
                      String status, String notes, String visitTime, String contactPhone) {
        this.reservationId = reservationId;
        this.userEmail = userEmail;
        this.propertyId = propertyId;
        this.reservationDate = reservationDate;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.notes = notes;
        this.visitTime = visitTime;
        this.contactPhone = contactPhone;
    }

    // Getters and Setters
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int getPropertyId() { return propertyId; }
    public void setPropertyId(int propertyId) { this.propertyId = propertyId; }

    public String getReservationDate() { return reservationDate; }
    public void setReservationDate(String reservationDate) { this.reservationDate = reservationDate; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getVisitTime() { return visitTime; }
    public void setVisitTime(String visitTime) { this.visitTime = visitTime; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    // Property details getters and setters
    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }

    public String getPropertyLocation() { return propertyLocation; }
    public void setPropertyLocation(String propertyLocation) { this.propertyLocation = propertyLocation; }

    public int getPropertyPrice() { return propertyPrice; }
    public void setPropertyPrice(int propertyPrice) { this.propertyPrice = propertyPrice; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

    public String getPropertyImageUrl() { return propertyImageUrl; }
    public void setPropertyImageUrl(String propertyImageUrl) { this.propertyImageUrl = propertyImageUrl; }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", userEmail='" + userEmail + '\'' +
                ", propertyId=" + propertyId +
                ", reservationDate='" + reservationDate + '\'' +
                ", visitTime='" + visitTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
