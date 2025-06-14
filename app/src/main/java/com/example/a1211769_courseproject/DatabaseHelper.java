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
    private static final int DATABASE_VERSION = 6; // Incremented to add role field
    
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
    private static final String KEY_ROLE = "role";
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
    
    // Promotion fields
    private static final String PROPERTY_IS_PROMOTED = "is_promoted";
    private static final String PROPERTY_HAS_SPECIAL_OFFER = "has_special_offer";
    private static final String PROPERTY_OFFER_TYPE = "offer_type";
    private static final String PROPERTY_ORIGINAL_PRICE = "original_price";
    private static final String PROPERTY_DISCOUNT_PERCENTAGE = "discount_percentage";
    private static final String PROPERTY_OFFER_DESCRIPTION = "offer_description";
    private static final String PROPERTY_OFFER_EXPIRY_DATE = "offer_expiry_date";
    
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
    public void onCreate(SQLiteDatabase db) {        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_FIRST_NAME + " TEXT,"
                + KEY_LAST_NAME + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_GENDER + " TEXT,"
                + KEY_COUNTRY + " TEXT,"
                + KEY_CITY + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_ROLE + " TEXT DEFAULT 'USER'" + ")";
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
                + PROPERTY_DESCRIPTION + " TEXT,"
                + PROPERTY_IS_PROMOTED + " INTEGER DEFAULT 0,"
                + PROPERTY_HAS_SPECIAL_OFFER + " INTEGER DEFAULT 0,"
                + PROPERTY_OFFER_TYPE + " TEXT,"
                + PROPERTY_ORIGINAL_PRICE + " INTEGER,"
                + PROPERTY_DISCOUNT_PERCENTAGE + " INTEGER DEFAULT 0,"
                + PROPERTY_OFFER_DESCRIPTION + " TEXT,"
                + PROPERTY_OFFER_EXPIRY_DATE + " INTEGER DEFAULT 0" + ")";
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
                + "FOREIGN KEY(" + RESERVATION_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + KEY_EMAIL + ")" + ")";        db.execSQL(CREATE_RESERVATIONS_TABLE);
          // Create pre-defined admin user
        createDefaultAdmin(db);
        
        // Note: Properties are now loaded from API, not inserted locally
    }
    
    // Create default admin user
    private void createDefaultAdmin(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(KEY_EMAIL, "admin@admin.com");
        adminValues.put(KEY_FIRST_NAME, "Admin");
        adminValues.put(KEY_LAST_NAME, "User");
        adminValues.put(KEY_PASSWORD, "Admin123!");
        adminValues.put(KEY_GENDER, "Other");
        adminValues.put(KEY_COUNTRY, "System");
        adminValues.put(KEY_CITY, "System");
        adminValues.put(KEY_PHONE, "000-000-0000");
        adminValues.put(KEY_ROLE, "ADMIN");
        
        db.insert(TABLE_USERS, null, adminValues);
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
        values.put(KEY_ROLE, user.getRole() != null ? user.getRole() : "USER");
        
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
                           KEY_GENDER, KEY_COUNTRY, KEY_CITY, KEY_PHONE, KEY_ROLE};
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
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROLE)));
        }
          cursor.close();
        db.close();
        return user;
    }

    // ===== ADMIN METHODS =====
    
    // Get all users (for admin)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {KEY_ID, KEY_EMAIL, KEY_FIRST_NAME, KEY_LAST_NAME, 
                           KEY_GENDER, KEY_COUNTRY, KEY_CITY, KEY_PHONE, KEY_ROLE};
        
        Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, KEY_EMAIL);
        
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
                user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LAST_NAME)));
                user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(KEY_GENDER)));
                user.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COUNTRY)));
                user.setCity(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CITY)));
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE)));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROLE)));
                users.add(user);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return users;
    }
    
    // Delete user (for admin)
    public boolean deleteUser(String email) {
        if ("admin@admin.com".equals(email)) {
            return false; // Cannot delete admin user
        }
        
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            // Delete user's reservations first
            db.delete(TABLE_RESERVATIONS, RESERVATION_USER_EMAIL + " = ?", new String[]{email});
            
            // Delete user's favorites
            db.delete(TABLE_FAVORITES, FAVORITE_USER_EMAIL + " = ?", new String[]{email});
            
            // Delete user
            int result = db.delete(TABLE_USERS, KEY_EMAIL + " = ?", new String[]{email});
            
            return result > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error deleting user: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }
    
    // Get statistics (for admin dashboard)
    public AdminStats getAdminStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        AdminStats stats = new AdminStats();
        
        try {
            // Count total users (excluding admin)
            Cursor userCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + 
                " WHERE " + KEY_ROLE + " = 'USER'", null);
            if (userCursor.moveToFirst()) {
                stats.totalUsers = userCursor.getInt(0);
            }
            userCursor.close();
            
            // Count total reservations
            Cursor reservationCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RESERVATIONS, null);
            if (reservationCursor.moveToFirst()) {
                stats.totalReservations = reservationCursor.getInt(0);
            }
            reservationCursor.close();
            
            // Get gender statistics
            Cursor genderCursor = db.rawQuery("SELECT " + KEY_GENDER + ", COUNT(*) FROM " + TABLE_USERS + 
                " WHERE " + KEY_ROLE + " = 'USER' GROUP BY " + KEY_GENDER, null);
            while (genderCursor.moveToNext()) {
                String gender = genderCursor.getString(0);
                int count = genderCursor.getInt(1);
                if ("Male".equals(gender)) {
                    stats.maleUsers = count;
                } else if ("Female".equals(gender)) {
                    stats.femaleUsers = count;
                }
            }
            genderCursor.close();
            
            // Get most reserving countries
            Cursor countryCursor = db.rawQuery("SELECT u." + KEY_COUNTRY + ", COUNT(r." + RESERVATION_ID + ") as reservation_count " +
                "FROM " + TABLE_USERS + " u " +
                "LEFT JOIN " + TABLE_RESERVATIONS + " r ON u." + KEY_EMAIL + " = r." + RESERVATION_USER_EMAIL + " " +
                "WHERE u." + KEY_ROLE + " = 'USER' " +
                "GROUP BY u." + KEY_COUNTRY + " " +
                "ORDER BY reservation_count DESC " +
                "LIMIT 5", null);
            
            while (countryCursor.moveToNext()) {
                String country = countryCursor.getString(0);
                int count = countryCursor.getInt(1);
                stats.topCountries.add(new AdminStats.CountryStats(country, count));
            }
            countryCursor.close();
            
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting admin stats: " + e.getMessage());
        } finally {
            db.close();
        }
        
        return stats;
    }
    
    // Get all reservations (for admin)
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT r.*, u." + KEY_FIRST_NAME + ", u." + KEY_LAST_NAME + " " +
                      "FROM " + TABLE_RESERVATIONS + " r " +
                      "JOIN " + TABLE_USERS + " u ON r." + RESERVATION_USER_EMAIL + " = u." + KEY_EMAIL + " " +
                      "ORDER BY r." + RESERVATION_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation();
                reservation.setReservationId(cursor.getInt(cursor.getColumnIndexOrThrow(RESERVATION_ID)));
                reservation.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_USER_EMAIL)));
                reservation.setPropertyId(cursor.getInt(cursor.getColumnIndexOrThrow(RESERVATION_PROPERTY_ID)));
                reservation.setReservationDate(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_DATE)));
                reservation.setCheckInDate(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_CHECK_IN)));
                reservation.setCheckOutDate(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_CHECK_OUT)));
                reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_STATUS)));
                reservation.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(RESERVATION_NOTES)));
                
                // Property details
                reservation.setPropertyTitle(cursor.getString(cursor.getColumnIndexOrThrow("property_title")));
                reservation.setPropertyLocation(cursor.getString(cursor.getColumnIndexOrThrow("property_location")));
                reservation.setPropertyPrice(cursor.getInt(cursor.getColumnIndexOrThrow("property_price")));
                reservation.setPropertyType(cursor.getString(cursor.getColumnIndexOrThrow("property_type")));
                reservation.setPropertyImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("property_image_url")));
                
                // Customer name
                String customerName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FIRST_NAME)) + " " +
                                     cursor.getString(cursor.getColumnIndexOrThrow(KEY_LAST_NAME));
                reservation.setCustomerName(customerName);
                
                reservations.add(reservation);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return reservations;
    }
    
    // Set property as promoted (for admin)
    public boolean setPropertyPromoted(int propertyId, boolean isPromoted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROPERTY_IS_PROMOTED, isPromoted ? 1 : 0);
        
        int result = db.update(TABLE_PROPERTIES, values, PROPERTY_ID + " = ?", 
                              new String[]{String.valueOf(propertyId)});
        db.close();
        return result > 0;
    }

    // Simplified updatePropertySpecialOffer for admin interface
    public boolean updatePropertySpecialOffer(int propertyId, String offerDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        if (offerDescription != null && !offerDescription.trim().isEmpty()) {
            // Add/update offer
            values.put(PROPERTY_HAS_SPECIAL_OFFER, 1);
            values.put(PROPERTY_OFFER_DESCRIPTION, offerDescription.trim());
            values.put(PROPERTY_OFFER_TYPE, "ADMIN_SPECIAL");
        } else {
            // Remove offer
            values.put(PROPERTY_HAS_SPECIAL_OFFER, 0);
            values.put(PROPERTY_OFFER_DESCRIPTION, "");
            values.put(PROPERTY_OFFER_TYPE, "");
        }
          int result = db.update(TABLE_PROPERTIES, values, PROPERTY_ID + " = ?", 
                              new String[]{String.valueOf(propertyId)});
        db.close();
        return result > 0;
    }

    // Get property special offer description (for admin)
    public String getPropertySpecialOffer(int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String offerDescription = null;
        
        String[] columns = {PROPERTY_OFFER_DESCRIPTION};
        String selection = PROPERTY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(propertyId)};
        
        Cursor cursor = db.query(TABLE_PROPERTIES, columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            offerDescription = cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_OFFER_DESCRIPTION));
        }
        
        cursor.close();
        db.close();
        return offerDescription;
    }

    // Check if property is promoted (for admin)
    public boolean isPropertyPromoted(int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isPromoted = false;
        
        String[] columns = {PROPERTY_IS_PROMOTED};
        String selection = PROPERTY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(propertyId)};
        
        Cursor cursor = db.query(TABLE_PROPERTIES, columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            isPromoted = cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_IS_PROMOTED)) == 1;
        }
        
        cursor.close();
        db.close();
        return isPromoted;
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
    }    // Favorites CRUD Operations
    public boolean addToFavorites(String userEmail, int propertyId) {
        SQLiteDatabase db = null;
        try {
            // Validate inputs first
            if (userEmail == null || userEmail.trim().isEmpty()) {
                return false;
            }
            
            if (propertyId <= 0) {
                return false;
            }
            
            db = this.getWritableDatabase();
            
            // Check if already in favorites (reuse existing db connection)
            boolean alreadyFavorite = isPropertyInFavorites(userEmail, propertyId, db);
            if (alreadyFavorite) {
                return false; // Already in favorites
            }
            
            // Check if user exists in database (reuse existing db connection)
            if (!checkUserExists(userEmail, db)) {
                return false;
            }
            
            ContentValues values = new ContentValues();
            values.put(FAVORITE_USER_EMAIL, userEmail);
            values.put(FAVORITE_PROPERTY_ID, propertyId);
            values.put(FAVORITE_DATE_ADDED, System.currentTimeMillis() / 1000); // Unix timestamp
            
            long result = db.insert(TABLE_FAVORITES, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding to favorites: " + e.getMessage());
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
        return getFavoriteProperties(userEmail, null);
    }
      // New method that supports API-based properties
    public List<Property> getFavoriteProperties(String userEmail, List<Property> apiProperties) {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = this.getReadableDatabase();
            
            // Get favorite property IDs for the user
            String query = "SELECT " + FAVORITE_PROPERTY_ID + " FROM " + TABLE_FAVORITES + 
                          " WHERE " + FAVORITE_USER_EMAIL + " = ?";
            
            cursor = db.rawQuery(query, new String[]{userEmail});
            
            List<Integer> favoritePropertyIds = new ArrayList<>();
            while (cursor.moveToNext()) {
                int propertyId = cursor.getInt(cursor.getColumnIndexOrThrow(FAVORITE_PROPERTY_ID));
                favoritePropertyIds.add(propertyId);
            }
            
            if (!favoritePropertyIds.isEmpty()) {
                if (apiProperties != null && !apiProperties.isEmpty()) {
                    // Filter API properties based on favorite IDs
                    for (Property property : apiProperties) {
                        if (favoritePropertyIds.contains(property.getId())) {
                            properties.add(property);
                        }
                    }
                } else {
                    // Fallback: try to get properties from local database (for backward compatibility)
                    cursor.close();
                    cursor = null;
                    
                    String localQuery = "SELECT p.* FROM " + TABLE_PROPERTIES + " p " +
                                      "INNER JOIN " + TABLE_FAVORITES + " f ON p." + PROPERTY_ID + " = f." + FAVORITE_PROPERTY_ID +
                                      " WHERE f." + FAVORITE_USER_EMAIL + " = ?";
                    
                    cursor = db.rawQuery(localQuery, new String[]{userEmail});
                    
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
                }
            }
            
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting favorite properties: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        
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

    // Method to get promoted properties
    public List<Property> getPromotedProperties() {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = PROPERTY_IS_PROMOTED + " = ?";
        String[] selectionArgs = {"1"};
        
        Cursor cursor = db.query(TABLE_PROPERTIES, null, selection, selectionArgs, null, null, PROPERTY_PRICE);
        
        if (cursor.moveToFirst()) {
            do {
                Property property = createPropertyFromCursor(cursor);
                properties.add(property);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return properties;
    }
    
    // Method to get properties with special offers
    public List<Property> getSpecialOfferProperties() {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = PROPERTY_HAS_SPECIAL_OFFER + " = ? AND (" + 
                          PROPERTY_OFFER_EXPIRY_DATE + " = 0 OR " + 
                          PROPERTY_OFFER_EXPIRY_DATE + " > ?)";
        String[] selectionArgs = {"1", String.valueOf(System.currentTimeMillis())};
        
        Cursor cursor = db.query(TABLE_PROPERTIES, null, selection, selectionArgs, null, null, PROPERTY_DISCOUNT_PERCENTAGE + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Property property = createPropertyFromCursor(cursor);
                properties.add(property);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return properties;
    }
    
    // Method to get properties by offer type
    public List<Property> getPropertiesByOfferType(String offerType) {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = PROPERTY_HAS_SPECIAL_OFFER + " = ? AND " + PROPERTY_OFFER_TYPE + " = ?";
        String[] selectionArgs = {"1", offerType};
        
        Cursor cursor = db.query(TABLE_PROPERTIES, null, selection, selectionArgs, null, null, PROPERTY_PRICE);
        
        if (cursor.moveToFirst()) {
            do {
                Property property = createPropertyFromCursor(cursor);
                properties.add(property);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return properties;
    }
    
    // Helper method to create Property object from cursor
    private Property createPropertyFromCursor(Cursor cursor) {
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
        
        // Load promotion fields
        property.setPromoted(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_IS_PROMOTED)) == 1);
        property.setHasSpecialOffer(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_HAS_SPECIAL_OFFER)) == 1);
        property.setOfferType(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_OFFER_TYPE)));
        property.setOriginalPrice(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_ORIGINAL_PRICE)));
        property.setDiscountPercentage(cursor.getInt(cursor.getColumnIndexOrThrow(PROPERTY_DISCOUNT_PERCENTAGE)));
        property.setOfferDescription(cursor.getString(cursor.getColumnIndexOrThrow(PROPERTY_OFFER_DESCRIPTION)));
        property.setOfferExpiryDate(cursor.getLong(cursor.getColumnIndexOrThrow(PROPERTY_OFFER_EXPIRY_DATE)));
        
        return property;
    }
    
    // Profile Management Methods
    
    /**
     * Update user profile information (excluding password)
     */
    public boolean updateUserProfile(String email, String firstName, String lastName, String phoneNumber) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_FIRST_NAME, firstName);
            values.put(KEY_LAST_NAME, lastName);
            values.put(KEY_PHONE, phoneNumber);
            
            String whereClause = KEY_EMAIL + " = ?";
            String[] whereArgs = {email};
            
            int rowsAffected = db.update(TABLE_USERS, values, whereClause, whereArgs);
            Log.d("DatabaseHelper", "Profile update - rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating user profile: " + e.getMessage());
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
    
    /**
     * Update user password after validating current password
     */
    public boolean updateUserPassword(String email, String currentPassword, String newPassword) {
        SQLiteDatabase db = null;
        try {
            // First verify current password
            if (!checkUser(email, currentPassword)) {
                Log.w("DatabaseHelper", "Current password verification failed for user: " + email);
                return false;
            }
            
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_PASSWORD, newPassword);
            
            String whereClause = KEY_EMAIL + " = ?";
            String[] whereArgs = {email};
            
            int rowsAffected = db.update(TABLE_USERS, values, whereClause, whereArgs);
            Log.d("DatabaseHelper", "Password update - rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating user password: " + e.getMessage());
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
    
    /**
     * Add profile picture path column if not exists (for future enhancement)
     */
    private void addProfilePictureColumn(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN profile_picture_path TEXT");
        } catch (Exception e) {
            // Column might already exist, ignore
            Log.d("DatabaseHelper", "Profile picture column might already exist");
        }
    }
    
    /**
     * Update user profile picture path
     */
    public boolean updateUserProfilePicture(String email, String profilePicturePath) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            
            // Ensure profile picture column exists
            addProfilePictureColumn(db);
            
            ContentValues values = new ContentValues();
            values.put("profile_picture_path", profilePicturePath);
            
            String whereClause = KEY_EMAIL + " = ?";
            String[] whereArgs = {email};
            
            int rowsAffected = db.update(TABLE_USERS, values, whereClause, whereArgs);
            Log.d("DatabaseHelper", "Profile picture update - rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating profile picture: " + e.getMessage());
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
    
    /**
     * Get user profile picture path
     */
    public String getUserProfilePicture(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            
            // Ensure profile picture column exists
            addProfilePictureColumn(db);
            
            String[] columns = {"profile_picture_path"};
            String selection = KEY_EMAIL + " = ?";
            String[] selectionArgs = {email};
            
            cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
            
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("profile_picture_path");
                if (columnIndex != -1) {
                    return cursor.getString(columnIndex);
                }
            }
            return null;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting profile picture: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
    
    // Method to insert/sync API properties into local database
    public boolean insertOrUpdateProperty(Property property) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(PROPERTY_ID, property.getId());
        values.put(PROPERTY_TITLE, property.getTitle());
        values.put(PROPERTY_TYPE, property.getType());
        values.put(PROPERTY_PRICE, property.getPrice());
        values.put(PROPERTY_LOCATION, property.getLocation());
        values.put(PROPERTY_AREA, property.getArea());
        values.put(PROPERTY_BEDROOMS, property.getBedrooms());
        values.put(PROPERTY_BATHROOMS, property.getBathrooms());
        values.put(PROPERTY_IMAGE_URL, property.getImageUrl());
        values.put(PROPERTY_DESCRIPTION, property.getDescription());
        
        // Set promotion fields (preserving existing values or setting defaults)
        values.put(PROPERTY_IS_PROMOTED, property.isPromoted() ? 1 : 0);
        values.put(PROPERTY_HAS_SPECIAL_OFFER, property.hasSpecialOffer() ? 1 : 0);
        values.put(PROPERTY_OFFER_TYPE, property.getOfferType() != null ? property.getOfferType() : "");
        values.put(PROPERTY_ORIGINAL_PRICE, property.getOriginalPrice());
        values.put(PROPERTY_DISCOUNT_PERCENTAGE, property.getDiscountPercentage());
        values.put(PROPERTY_OFFER_DESCRIPTION, property.getOfferDescription() != null ? property.getOfferDescription() : "");
        values.put(PROPERTY_OFFER_EXPIRY_DATE, property.getOfferExpiryDate());
        
        try {
            // Try to insert first
            long result = db.insertWithOnConflict(TABLE_PROPERTIES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            
            if (result == -1) {
                // Property already exists, update it (but preserve existing offer data)
                ContentValues updateValues = new ContentValues();
                updateValues.put(PROPERTY_TITLE, property.getTitle());
                updateValues.put(PROPERTY_TYPE, property.getType());
                updateValues.put(PROPERTY_PRICE, property.getPrice());
                updateValues.put(PROPERTY_LOCATION, property.getLocation());
                updateValues.put(PROPERTY_AREA, property.getArea());
                updateValues.put(PROPERTY_BEDROOMS, property.getBedrooms());
                updateValues.put(PROPERTY_BATHROOMS, property.getBathrooms());
                updateValues.put(PROPERTY_IMAGE_URL, property.getImageUrl());
                updateValues.put(PROPERTY_DESCRIPTION, property.getDescription());
                // Don't update promotion fields on sync to preserve admin settings
                
                int updateResult = db.update(TABLE_PROPERTIES, updateValues, PROPERTY_ID + " = ?", 
                                           new String[]{String.valueOf(property.getId())});
                return updateResult > 0;
            }
            return result > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting/updating property: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }
    
    // Bulk sync method for multiple properties
    public void syncPropertiesFromAPI(List<Property> apiProperties) {
        if (apiProperties == null || apiProperties.isEmpty()) return;
        
        for (Property property : apiProperties) {
            insertOrUpdateProperty(property);
        }
    }
}
