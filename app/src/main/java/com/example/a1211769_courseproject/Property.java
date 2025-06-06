package com.example.a1211769_courseproject;

import java.io.Serializable;

public class Property implements Serializable {
    private int id;
    private String title;
    private String type;          // "Apartment" | "Villa" | "Land"
    private int price;
    private String location;      // e.g. "Ramallah, Palestine"
    private String area;          // e.g. "120 m²"
    private int bedrooms;
    private int bathrooms;
    private String imageUrl;      // "https://example.com/…"
    private String description;
    
    // Special offers and promotion fields
    private boolean isPromoted;      // Whether this property is promoted
    private boolean hasSpecialOffer; // Whether this property has a special offer
    private String offerType;        // "DISCOUNT", "LIMITED_TIME", "NEW_LISTING", "FEATURED"
    private int originalPrice;       // Original price before discount
    private int discountPercentage;  // Discount percentage (e.g., 10 for 10%)
    private String offerDescription; // Description of the offer
    private long offerExpiryDate;    // Offer expiry date in milliseconds

    // Default constructor
    public Property() {
        this.isPromoted = false;
        this.hasSpecialOffer = false;
        this.offerType = "";
        this.originalPrice = 0;
        this.discountPercentage = 0;
        this.offerDescription = "";
        this.offerExpiryDate = 0;
    }

    // Main constructor
    public Property(int id, String title, String type, int price,
                    String location, String area,
                    int bedrooms, int bathrooms,
                    String imageUrl, String description) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.price = price;
        this.location = location;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.imageUrl = imageUrl;
        this.description = description;
        
        // Initialize promotion fields with default values
        this.isPromoted = false;
        this.hasSpecialOffer = false;
        this.offerType = "";
        this.originalPrice = price;
        this.discountPercentage = 0;
        this.offerDescription = "";
        this.offerExpiryDate = 0;
    }

    // Constructor with promotion fields
    public Property(int id, String title, String type, int price,
                    String location, String area,
                    int bedrooms, int bathrooms,
                    String imageUrl, String description,
                    boolean isPromoted, boolean hasSpecialOffer,
                    String offerType, int originalPrice,
                    int discountPercentage, String offerDescription,
                    long offerExpiryDate) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.price = price;
        this.location = location;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.imageUrl = imageUrl;
        this.description = description;
        this.isPromoted = isPromoted;
        this.hasSpecialOffer = hasSpecialOffer;
        this.offerType = offerType;
        this.originalPrice = originalPrice;
        this.discountPercentage = discountPercentage;
        this.offerDescription = offerDescription;
        this.offerExpiryDate = offerExpiryDate;
    }

    // Standard getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public int getBedrooms() { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    public int getBathrooms() { return bathrooms; }
    public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Promotion getters and setters
    public boolean isPromoted() { return isPromoted; }
    public void setPromoted(boolean promoted) { isPromoted = promoted; }

    public boolean hasSpecialOffer() { return hasSpecialOffer; }
    public void setHasSpecialOffer(boolean hasSpecialOffer) { this.hasSpecialOffer = hasSpecialOffer; }

    public String getOfferType() { return offerType; }
    public void setOfferType(String offerType) { this.offerType = offerType; }

    public int getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(int originalPrice) { this.originalPrice = originalPrice; }

    public int getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getOfferDescription() { return offerDescription; }
    public void setOfferDescription(String offerDescription) { this.offerDescription = offerDescription; }

    public long getOfferExpiryDate() { return offerExpiryDate; }
    public void setOfferExpiryDate(long offerExpiryDate) { this.offerExpiryDate = offerExpiryDate; }

    // Utility methods for promotion functionality
    public boolean isOfferValid() {
        if (!hasSpecialOffer) return false;
        if (offerExpiryDate == 0) return true; // No expiry date
        return System.currentTimeMillis() < offerExpiryDate;
    }

    public int getDiscountedPrice() {
        if (!hasSpecialOffer || !isOfferValid()) {
            return price;
        }
        return originalPrice - (originalPrice * discountPercentage / 100);
    }

    public int getSavingsAmount() {
        if (!hasSpecialOffer || !isOfferValid()) {
            return 0;
        }
        return originalPrice - getDiscountedPrice();
    }

    public String getFormattedPrice() {
        if (hasSpecialOffer && isOfferValid()) {
            return "$" + getDiscountedPrice() + " (was $" + originalPrice + ")";
        }
        return "$" + price;
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", area='" + area + '\'' +
                ", bedrooms=" + bedrooms +
                ", bathrooms=" + bathrooms +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", isPromoted=" + isPromoted +
                ", hasSpecialOffer=" + hasSpecialOffer +
                ", offerType='" + offerType + '\'' +
                ", originalPrice=" + originalPrice +
                ", discountPercentage=" + discountPercentage +
                ", offerDescription='" + offerDescription + '\'' +
                ", offerExpiryDate=" + offerExpiryDate +
                '}';
    }
}
