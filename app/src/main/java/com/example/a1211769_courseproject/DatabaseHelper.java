package com.example.a1211769_courseproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CourseProject.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table names
    private static final String TABLE_USERS = "users";
    
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
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
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
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
    }
    
    // Check if user exists
    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID};
        String selection = KEY_EMAIL + " = ?";
        String[] selectionArgs = {email};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        
        return cursorCount > 0;
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
}
