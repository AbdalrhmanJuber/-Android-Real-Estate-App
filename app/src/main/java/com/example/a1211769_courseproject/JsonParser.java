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
            }

            JSONArray propArray = top.getJSONArray("properties");
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

                Property prop = new Property(
                        id, title, type, price,
                        loc, area, beds, baths,
                        image, descr
                );
                result.add(prop);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
