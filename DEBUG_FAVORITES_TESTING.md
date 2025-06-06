# Debug Favorites Testing Instructions

## Testing Steps:

1. **Launch the app** on your emulator/device
2. **Log in** with a valid user account
3. **Navigate to Properties tab**
4. **Click on a favorite button** for any property
5. **Check the logcat output** for debugging information

## Expected Debug Output:

### PropertiesFragment Logs:
```
D/PropertiesFragment: SharedPreferences debug:
D/PropertiesFragment: Current user email from UserPrefs: '[email]'
D/PropertiesFragment: All UserPrefs keys: [email]
D/PropertiesFragment: Email from login_preferences: '[email]'
```

### PropertyAdapter Logs:
```
D/PropertyAdapter: Constructor - Received user email: '[email]'
D/PropertyAdapter: Constructor - Set currentUserEmail to: '[email]'
D/PropertyAdapter: User email is valid for favorites functionality
D/PropertyAdapter: toggleFavorite called - UserEmail: '[email]', Property: [ID]
```

### DatabaseHelper Logs:
```
D/DatabaseHelper: === ADD TO FAVORITES DEBUG ===
D/DatabaseHelper: Attempting to add favorite - Email: '[email]', PropertyID: [ID]
D/DatabaseHelper: Got writable database successfully
D/DatabaseHelper: Already in favorites check: false
D/DatabaseHelper: User exists in database: [email]
D/DatabaseHelper: Prepared ContentValues: [values]
D/DatabaseHelper: Insert result: [number]
D/DatabaseHelper: Successfully added to favorites
```

## How to Check Logs:

### If using Android Studio:
1. Open Android Studio
2. Go to View → Tool Windows → Logcat
3. Make sure your device is selected
4. Use filter: `package:com.example.a1211769_courseproject`
5. Look for the debug messages above

### If using Command Line:
```bash
adb logcat -s "PropertiesFragment:D" "PropertyAdapter:D" "DatabaseHelper:D"
```

## What to Look For:

1. **User Email Issues**: If you see empty user email in logs
2. **Database Issues**: If insert result is -1
3. **User Not Found**: If user doesn't exist in database
4. **Already Favorite**: If property is already in favorites

## If User Email is Empty:

The app will automatically try to copy from `login_preferences` to `UserPrefs`. If this doesn't work:

1. Check if you're properly logged in
2. Try logging out and logging in again
3. Check if SharedPreferences are being saved correctly

## Common Issues:

1. **Empty User Email**: User not logged in properly
2. **Database Insert Fails**: Foreign key constraints or table issues
3. **User Doesn't Exist**: User email not matching what's in database
4. **Already in Favorites**: Property already added (this is normal)

## Next Steps:

After testing, provide the logcat output so we can identify the exact issue.
