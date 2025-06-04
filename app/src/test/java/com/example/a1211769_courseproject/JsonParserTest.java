package com.example.a1211769_courseproject;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class JsonParserTest {

    @Test
    public void testParseProperties() {
        String mockApiResponse = "{"
            + "\"categories\":[{\"id\":1,\"name\":\"Apartment\"}],"
            + "\"properties\":[{"
            + "\"id\":101,"
            + "\"title\":\"Modern 2-Bedroom Apartment\","
            + "\"type\":\"Apartment\","
            + "\"price\":85000,"
            + "\"location\":\"Ramallah, Palestine\","
            + "\"area\":\"120 m²\","
            + "\"bedrooms\":2,"
            + "\"bathrooms\":2,"
            + "\"image_url\":\"https://example.com/images/apartment1.jpg\","
            + "\"description\":\"Beautiful apartment with city view.\""
            + "}]}";

        List<Property> properties = JsonParser.parseProperties(mockApiResponse);
        
        assertNotNull("Properties list should not be null", properties);
        assertEquals("Should parse 1 property", 1, properties.size());
        
        Property property = properties.get(0);
        assertEquals("Property ID should match", 101, property.getId());
        assertEquals("Property title should match", "Modern 2-Bedroom Apartment", property.getTitle());
        assertEquals("Property type should match", "Apartment", property.getType());
        assertEquals("Property price should match", 85000, property.getPrice());
        assertEquals("Property location should match", "Ramallah, Palestine", property.getLocation());
        assertEquals("Property area should match", "120 m²", property.getArea());
        assertEquals("Property bedrooms should match", 2, property.getBedrooms());
        assertEquals("Property bathrooms should match", 2, property.getBathrooms());
        assertEquals("Property image URL should match", "https://example.com/images/apartment1.jpg", property.getImageUrl());
        assertEquals("Property description should match", "Beautiful apartment with city view.", property.getDescription());
    }
}
