package com.example.a1211769_courseproject;

import java.io.Serializable;

// Property.java
public class Property implements Serializable {
    private int id;
    private String title;
    private String type;          // “Apartment” | “Villa” | “Land”
    private int price;
    private String location;      // e.g. “Ramallah, Palestine”
    private String area;          // e.g. “120 m²”
    private int bedrooms;
    private int bathrooms;
    private String imageUrl;      // “https://example.com/…”
    private String description;

    public Property() { }

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
    }

    // … Generate all getters and setters below …
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
                '}';
    }
}
