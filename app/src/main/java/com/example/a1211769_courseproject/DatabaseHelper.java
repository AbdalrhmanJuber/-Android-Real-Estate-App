package com.example.a1211769_courseproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CourseProject.db";
    private static final int DATABASE_VERSION = 4; // Incremented to remove foreign key constraint for API properties
    
    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PROPERTIES = "properties";
    private static final String TABLE_FAVORITES = "favorites";
    private static final String TABLE_RESERVATIONS = "reservations";
    
    // User table columns
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_CITY = "city";
    private static final String KEY_PHONE = "phone_number";
    
    // Property table columns
    private static final String PROPERTY_ID = "property_id";
    private static final String PROPERTY_TITLE = "title";
    private static final String PROPERTY_TYPE = "type";
    private static final String PROPERTY_PRICE = "price";
    private static final String PROPERTY_LOCATION = "location";
    private static final String PROPERTY_AREA = "area";
    private static final String PROPERTY_BEDROOMS = "bedrooms";
    private static final String PROPERTY_BATHROOMS = "bathrooms";
    private static final String PROPERTY_IMAGE_URL = "image_url";
    private static final String PROPERTY_DESCRIPTION = "description";
    
    // Favorites table columns
    private static final String FAVORITE_ID = "favorite_id";
    private static final String FAVORITE_USER_EMAIL = "user_email";
    private static final String FAVORITE_PROPERTY_ID = "property_id";
    private static final String FAVORITE_DATE_ADDED = "date_added";
    
    // Reservations table columns
    private static final String RESERVATION_ID = "reservation_id";
    private static final String RESERVATION_USER_EMAIL = "user_email";
    private static final String RESERVATION_PROPERTY_ID = "property_id";
    private static final String RESERVATION_DATE = "reservation_date";
    private static final String RESERVATION_CHECK_IN = "check_in_date";
    private static final String RESERVATION_CHECK_OUT = "check_out_date";
    private static final String RESERVATION_STATUS = "status";
    private static final String RESERVATION_NOTES = "notes";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
      @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_FIRST_NAME + " TEXT,"
                + KEY_LAST_NAME + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_GENDER + " TEXT,"
                + KEY_COUNTRY + " TEXT,"
                + KEY_CITY + " TEXT,"
                + KEY_PHONE + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);
        
        // Create properties table
        String CREATE_PROPERTIES_TABLE = "CREATE TABLE " + TABLE_PROPERTIES + "("
                + PROPERTY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PROPERTY_TITLE + " TEXT,"
                + PROPERTY_TYPE + " TEXT,"
                + PROPERTY_PRICE + " INTEGER,"
                + PROPERTY_LOCATION + " TEXT,"
                + PROPERTY_AREA + " TEXT,"
                + PROPERTY_BEDROOMS + " INTEGER,"
                + PROPERTY_BATHROOMS + " INTEGER,"
                + PROPERTY_IMAGE_URL + " TEXT,"
                + PROPERTY_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_PROPERTIES_TABLE);
          // Create favorites table - removed foreign key constraint for property_id to support API properties
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FAVORITE_USER_EMAIL + " TEXT,"
                + FAVORITE_PROPERTY_ID + " INTEGER,"
                + FAVORITE_DATE_ADDED + " TEXT,"
                + "FOREIGN KEY(" + FAVORITE_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + KEY_EMAIL + ")" + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);// Create reservations table with property details stored directly
        String CREATE_RESERVATIONS_TABLE = "CREATE TABLE " + TABLE_RESERVATIONS + "("
                + RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RESERVATION_USER_EMAIL + " TEXT,"
                + RESERVATION_PROPERTY_ID + " INTEGER,"
                + RESERVATION_DATE + " TEXT,"
                + RESERVATION_CHECK_IN + " TEXT,"
                + RESERVATION_CHECK_OUT + " TEXT,"
                + RESERVATION_STATUS + " TEXT,"
                + RESERVATION_NOTES + " TEXT,"
                + "property_title TEXT,"
                + "property_location TEXT,"
                + "property_price INTEGER,"
                + "property_type TEXT,"
                + "property_image_url TEXT,"
                + "FOREIGN KEY(" + RESERVATION_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + KEY_EMAIL + ")" + ")";
        db.execSQL(CREATE_RESERVATIONS_TABLE);
        
        // Note: Properties are now loaded from API, not inserted locally
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
    
    // Add user
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_FIRST_NAME, user.getFirstName());
        values.put(KEY_LAST_NAME, user.getLastName());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_GENDER, user.getGender());
        values.put(KEY_COUNTRY, user.getCountry());
        values.put(KEY_CITY, user.getCity());
        values.put(KEY_PHONE, user.getPhoneNumber());
        
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }
    
    // Check if user exists and password matches
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID};
        String selection = KEY_EMAIL + " = ?" + " AND " + KEY_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        
        return cursorCount > 0;
    }    // Check if user exists
    public boolean checkUser(String email) {
        return checkUserExists(email, null);
    }
    
    public boolean checkUserExists(String email, SQLiteDatabase existingDb) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean shouldCloseDb = false;
        
        try {
            if (existingDb != null) {
                db = existingDb;
                shouldCloseDb = false; // Don't close a database connection we didn't create
            } else {
                db = this.getReadableDatabase();
                shouldCloseDb = true; // Close the database connection we created
            }
            
            String[] columns = {KEY_ID};
            String selection = KEY_EMAIL + " = ?";
            String[] selectionArgs = {email};
            
            cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (shouldCloseDb && db != null) {
                db.close();
            }
        }
    }
    
    // Get user by email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID, KEY_EMAIL, KEY_FIRST_NAME, KEY_LAST_NAME, 
                           KEY_GENDER, KEY_COUNTRY, KEY_CITY, KEY_PHONE};
        String selection = KEY_EMAIL + " = ?";
        String[] selectionArgs = {email};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
            user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FIRST_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LAST_NAME)));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GENDER)));
            user.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COUNTRY)));
            user.setCity(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CITY)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE)));
        }
        
        cursor.close();
        db.close();
        return user;
    }
      // Public method to insert sample properties
    public void insertSampleProperties() {
        // Properties are now loaded from API in PropertiesFragment
        // This method is kept for backward compatibility but does nothing
        Log.d("DatabaseHelper", "Properties are loaded from API, not inserted locally");
    }

    // Insert sample properties from API (deprecated - kept for compatibility)
    private void insertSampleProperties(SQLiteDatabase db) {
        // No longer inserting static data - properties come from API
        Log.d("DatabaseHelper", "Static property insertion removed - using API data");
    }
    
    // Properties CRUD Operations
    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_PROPERTIES, null, null, null, null, null, PROPERTY_TYPE + ", " + PROPERTY_PRICE);
        
        if (cursor.moveToFirst()) {
            do {
                Property property = new Property();
                property.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_ID)));
                property.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_TITLE)));
                property.setType(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_TYPE)));
                property.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_PRICE)));
                property.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_LOCATION)));
                property.setArea(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_AREA)));
                property.setBedrooms(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_BEDROOMS)));
                property.setBathrooms(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_BATHROOMS)));
                property.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_IMAGE_URL)));
                property.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_DESCRIPTION)));
                properties.add(property);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return properties;
    }
    
    public List<Property> getPropertiesByType(String type) {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = PROPERTY_TYPE + " = ?";
        String[] selectionArgs = {type};
        
        Cursor cursor = db.query(TABLE_PROPERTIES, null, selection, selectionArgs, null, null, PROPERTY_PRICE);
        
        if (cursor.moveToFirst()) {
            do {
                Property property = new Property();
                property.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_ID)));
                property.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_TITLE)));
                property.setType(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_TYPE)));
                property.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_PRICE)));
                property.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_LOCATION)));
                property.setArea(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_AREA)));
                property.setBedrooms(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_BEDROOMS)));
                property.setBathrooms(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_BATHROOMS)));
                property.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_IMAGE_URL)));
                property.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_DESCRIPTION)));
                properties.add(property);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return properties;
    }
    
    public List<Property> searchProperties(String location, int minPrice, int maxPrice, String type) {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgsList = new ArrayList<>();
        
        selection.append("1=1"); // Always true base condition
        
        if (location != null && !location.trim().isEmpty()) {
            selection.append(" AND ").append(PROPERTY_LOCATION).append(" LIKE ?");
            selectionArgsList.add("%" + location + "%");
        }
        
        if (minPrice > 0) {
            selection.append(" AND ").append(PROPERTY_PRICE).append(" >= ?");
            selectionArgsList.add(String.valueOf(minPrice));
        }
        
        if (maxPrice > 0 && maxPrice > minPrice) {
            selection.append(" AND ").append(PROPERTY_PRICE).append(" <= ?");
            selectionArgsList.add(String.valueOf(maxPrice));
        }
        
        if (type != null && !type.equals("All")) {
            selection.append(" AND ").append(PROPERTY_TYPE).append(" = ?");
            selectionArgsList.add(type);
        }
        
        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        
        Cursor cursor = db.query(TABLE_PROPERTIES, null, selection.toString(), selectionArgs, null, null, PROPERTY_PRICE);
        
        if (cursor.moveToFirst()) {
            do {
                Property property = new Property();
                property.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_ID)));
                property.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_TITLE)));
                property.setType(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_TYPE)));
                property.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_PRICE)));
                property.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_LOCATION)));
                property.setArea(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_AREA)));
                property.setBedrooms(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_BEDROOMS)));
                property.setBathrooms(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_BATHROOMS)));
                property.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_IMAGE_URL)));
                property.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_DESCRIPTION)));
                properties.add(property);
            } while (cursor.moveToNext());
        }
          cursor.close();
        db.close();
        return properties;
    }

    // Favorites CRUD Operations
    public boolean addToFavorites(String userEmail, int propertyId) {
        SQLiteDatabase db = null;
        try {
            Log.d("DatabaseHelper", "=== ADD TO FAVORITES DEBUG ===");
            Log.d("DatabaseHelper", "Attempting to add favorite - Email: '" + userEmail + "', PropertyID: " + propertyId);
            
            // Validate inputs first
            if (userEmail == null || userEmail.trim().isEmpty()) {
                Log.e("DatabaseHelper", "Invalid user email for favorites: '" + userEmail + "'");
                return false;
            }
            
            if (propertyId <= 0) {
                Log.e("DatabaseHelper", "Invalid property ID: " + propertyId);
                return false;
            }
            
            db = this.getWritableDatabase();
            Log.d("DatabaseHelper", "Got writable database successfully");
              // Check if already in favorites (reuse existing db connection)
            boolean alreadyFavorite = isPropertyInFavorites(userEmail, propertyId, db);
            Log.d("DatabaseHelper", "Already in favorites check: " + alreadyFavorite);
            if (alreadyFavorite) {
                Log.d("DatabaseHelper", "Property already in favorites");
                return false; // Already in favorites
            }            // Check if user exists in database (reuse existing db connection)
            if (!checkUserExists(userEmail, db)) {
                Log.e("DatabaseHelper", "User does not exist in database: " + userEmail);
                return false;
            }
            Log.d("DatabaseHelper", "User exists in database: " + userEmail);
            
            ContentValues values = new ContentValues();
            values.put(FAVORITE_USER_EMAIL, userEmail);
            values.put(FAVORITE_PROPERTY_ID, propertyId);
            values.put(FAVORITE_DATE_ADDED, System.currentTimeMillis() / 1000); // Unix timestamp
            
            Log.d("DatabaseHelper", "Prepared ContentValues: " + values.toString());
            
            long result = db.insert(TABLE_FAVORITES, null, values);
            Log.d("DatabaseHelper", "Insert result: " + result);
            
            if (result != -1) {
                Log.d("DatabaseHelper", "Successfully added to favorites");
                return true;
            } else {
                Log.e("DatabaseHelper", "Failed to insert favorite - database insert returned -1");
                
                // Check table structure
                Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_FAVORITES + ")", null);
                StringBuilder tableInfo = new StringBuilder("Favorites table structure: ");
                while (cursor.moveToNext()) {
                    String columnName = cursor.getString(1);
                    String columnType = cursor.getString(2);
                    tableInfo.append(columnName).append("(").append(columnType).append(") ");
                }
                cursor.close();
                Log.d("DatabaseHelper", tableInfo.toString());
                
                return false;
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding to favorites: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
      public boolean removeFromFavorites(String userEmail, int propertyId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            
            String selection = FAVORITE_USER_EMAIL + " = ? AND " + FAVORITE_PROPERTY_ID + " = ?";
            String[] selectionArgs = {userEmail, String.valueOf(propertyId)};
            
            int result = db.delete(TABLE_FAVORITES, selection, selectionArgs);
            return result > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error removing from favorites: " + e.getMessage());
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
      public boolean isPropertyInFavorites(String userEmail, int propertyId) {
        return isPropertyInFavorites(userEmail, propertyId, null);
    }
    
    public boolean isPropertyInFavorites(String userEmail, int propertyId, SQLiteDatabase existingDb) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean shouldCloseDb = false;
        
        try {
            if (existingDb != null) {
                db = existingDb;
                shouldCloseDb = false; // Don't close a database connection we didn't create
            } else {
                db = this.getReadableDatabase();
                shouldCloseDb = true; // Close the database connection we created
            }
            
            String selection = FAVORITE_USER_EMAIL + " = ? AND " + FAVORITE_PROPERTY_ID + " = ?";
            String[] selectionArgs = {userEmail, String.valueOf(propertyId)};
            
            cursor = db.query(TABLE_FAVORITES, null, selection, selectionArgs, null, null, null);
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking favorites: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (shouldCloseDb && db != null) {
                db.close();
            }
        }
    }
    
    public List<Property> getFavoriteProperties(String userEmail) {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT p.* FROM " + TABLE_PROPERTIES + " p " +
                      "INNER JOIN " + TABLE_FAVORITES + " f ON p." + PROPERTY_ID + " = f." + FAVORITE_PROPERTY_ID +
                      " WHERE f." + FAVORITE_USER_EMAIL + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});
        
        if (cursor.moveToFirst()) {
            do {
                Property property = new Property();
                property.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_ID)));
                property.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_TITLE)));
                property.setType(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_TYPE)));
                property.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_PRICE)));
                property.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_LOCATION)));
                property.setArea(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_AREA)));
                property.setBedrooms(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_BEDROOMS)));
                property.setBathrooms(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_BATHROOMS)));
                property.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_IMAGE_URL)));
                property.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_DESCRIPTION)));
                properties.add(property);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return properties;
    }
      // Reservations CRUD Operations
    public long insertReservation(String userEmail, Property property, String reservationDate, 
                                String visitTime, String contactPhone, String specialRequests) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(RESERVATION_USER_EMAIL, userEmail);
        values.put(RESERVATION_PROPERTY_ID, property.getId());
        values.put(RESERVATION_DATE, reservationDate);
        values.put(RESERVATION_CHECK_IN, visitTime); // Using check_in for visit time
        values.put(RESERVATION_CHECK_OUT, contactPhone); // Using check_out for contact phone
        values.put(RESERVATION_STATUS, "Pending");
        values.put(RESERVATION_NOTES, specialRequests);
        
        // Store property details directly in the reservation
        values.put("property_title", property.getTitle());
        values.put("property_location", property.getLocation());
        values.put("property_price", property.getPrice());
        values.put("property_type", property.getType());
        values.put("property_image_url", property.getImageUrl());
        
        long result = db.insert(TABLE_RESERVATIONS, null, values);
        db.close();
        return result;
    }    public List<Reservation> getUserReservations(String userEmail) {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Query reservations table directly with property details stored in it
        String query = "SELECT * FROM " + TABLE_RESERVATIONS + 
                      " WHERE " + RESERVATION_USER_EMAIL + " = ?" +
                      " ORDER BY " + RESERVATION_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});
        
        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation();
                reservation.setReservationId(cursor.getInt(cursor.getColumnIndexOrThrow(RESERVATION_ID)));
                reservation.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_USER_EMAIL)));
                reservation.setPropertyId(cursor.getInt(cursor.getColumnIndexOrThrow(RESERVATION_PROPERTY_ID)));
                reservation.setReservationDate(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_DATE)));
                reservation.setVisitTime(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_CHECK_IN))); // Visit time stored in check_in
                reservation.setContactPhone(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_CHECK_OUT))); // Contact phone stored in check_out
                reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_STATUS)));
                reservation.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_NOTES)));
                
                // Get property details from the reservation record itself
                int titleIndex = cursor.getColumnIndex("property_title");
                if (titleIndex != -1 && !cursor.isNull(titleIndex)) {
                    reservation.setPropertyTitle(cursor.getString(titleIndex));
                    reservation.setPropertyLocation(cursor.getString(cursor.getColumnIndexOrThrow("property_location")));
                    reservation.setPropertyPrice(cursor.getInt(cursor.getColumnIndexOrThrow("property_price")));
                    reservation.setPropertyType(cursor.getString(cursor.getColumnIndexOrThrow("property_type")));
                    reservation.setPropertyImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("property_image_url")));
                }
                
                reservations.add(reservation);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return reservations;
    }

    public boolean updateReservationStatus(int reservationId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RESERVATION_STATUS, status);
        
        String selection = RESERVATION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(reservationId)};
        
        int result = db.update(TABLE_RESERVATIONS, values, selection, selectionArgs);
        db.close();
        return result > 0;
    }

    public boolean deleteReservation(int reservationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        String selection = RESERVATION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(reservationId)};
        
        int result = db.delete(TABLE_RESERVATIONS, selection, selectionArgs);
        db.close();
        return result > 0;
    }

    public int getReservationCount(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = RESERVATION_USER_EMAIL + " = ?";
        String[] selectionArgs = {userEmail};
        
        Cursor cursor = db.query(TABLE_RESERVATIONS, null, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
}
