package com.example.a1211769_courseproject;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    public static List<Property> parseProperties(String jsonString) {
        List<Property> result = new ArrayList<>();
        try {
            JSONObject top = new JSONObject(jsonString);

            JSONArray catArray = top.getJSONArray("categories");
            for (int i = 0; i < catArray.length(); i++) {
                JSONObject c = catArray.getJSONObject(i);
                int cid = c.getInt("id");
                String name = c.getString("name");
            }            JSONArray propArray = top.getJSONArray("properties");
            for (int i = 0; i < propArray.length(); i++) {
                JSONObject p = propArray.getJSONObject(i);
                int id        = p.getInt("id");
                String title  = p.getString("title");
                String type   = p.getString("type");
                int price     = p.getInt("price");
                String loc    = p.getString("location");
                String area   = p.getString("area");
                int beds      = p.getInt("bedrooms");
                int baths     = p.getInt("bathrooms");
                String image  = p.getString("image_url");
                String descr  = p.getString("description");

                // Parse optional promotion fields with defaults
                boolean isPromoted = p.optBoolean("is_promoted", false);
                boolean hasSpecialOffer = p.optBoolean("has_special_offer", false);
                String offerType = p.optString("offer_type", "");
                int originalPrice = p.optInt("original_price", price);
                int discountPercentage = p.optInt("discount_percentage", 0);
                String offerDescription = p.optString("offer_description", "");
                long offerExpiryDate = p.optLong("offer_expiry_date", 0);

                Property prop = new Property(
                        id, title, type, price,
                        loc, area, beds, baths,
                        image, descr,
                        isPromoted, hasSpecialOffer, offerType,
                        originalPrice, discountPercentage, offerDescription,
                        offerExpiryDate
                );
                result.add(prop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Category> parseCategories(String jsonString) {
        List<Category> categories = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray categoryArray = jsonObject.getJSONArray("categories");

            for (int i = 0; i < categoryArray.length(); i++) {
                JSONObject categoryObj = categoryArray.getJSONObject(i);
                int id = categoryObj.getInt("id");
                String name = categoryObj.getString("name");

                Category category = new Category(id, name);
                categories.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }
}
