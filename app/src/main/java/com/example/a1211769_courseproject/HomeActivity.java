package com.example.a1211769_courseproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String userEmail;
    private String jsonData;
    private DatabaseHelper databaseHelper;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);        // Get user data from intent
        userEmail = getIntent().getStringExtra("userEmail");
        jsonData = getIntent().getStringExtra("jsonData");

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);
        
        // Check if user is admin
        checkUserRole();

        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupNavigationHeader();

        // Load default fragment based on user role
        if (savedInstanceState == null) {
            if (isAdmin) {
                loadFragment(new AdminDashboardFragment());
                navigationView.setCheckedItem(R.id.nav_admin_dashboard);
            } else {
                loadFragment(new HomeFragment());
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }
    }

    private void checkUserRole() {
        if (userEmail != null && !userEmail.isEmpty()) {
            User user = databaseHelper.getUserByEmail(userEmail);
            if (user != null && user.isAdmin()) {
                isAdmin = true;
            }
        }
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isAdmin ? "Admin Panel" : "Real Estate App");
        }
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set appropriate menu based on user role
        if (isAdmin) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.nav_menu_admin);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.nav_menu);
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderEmail = headerView.findViewById(R.id.nav_header_email);
        TextView navHeaderWelcome = headerView.findViewById(R.id.nav_header_welcome);
        
        if (userEmail != null) {
            navHeaderEmail.setText(userEmail);
            navHeaderWelcome.setText("Welcome back!");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String title = "Real Estate App";        int itemId = item.getItemId();
        
        // Handle admin menu items
        if (isAdmin) {
            if (itemId == R.id.nav_admin_dashboard) {
                fragment = new AdminDashboardFragment();
                title = "Admin Dashboard";
            } else if (itemId == R.id.nav_admin_delete_customers) {
                fragment = new AdminDeleteCustomersFragment();
                title = "Delete Customers";
            } else if (itemId == R.id.nav_admin_all_reservations) {
                fragment = new AdminAllReservationsFragment();
                title = "All Reservations";
            } else if (itemId == R.id.nav_admin_manage_offers) {
                fragment = new AdminManageOffersFragment();
                title = "Manage Special Offers";
            } else if (itemId == R.id.nav_admin_add_admin) {
                fragment = new AdminAddAdminFragment();
                title = "Add New Admin";
            } else if (itemId == R.id.nav_admin_properties) {
                fragment = new PropertiesFragment();
                title = "Properties";
            } else if (itemId == R.id.nav_logout) {
                logout();
                return true;
            }
        } else {
            // Handle regular user menu items
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
                title = "Home";
            } else if (itemId == R.id.nav_properties) {
                fragment = new PropertiesFragment();
                title = "Properties";
            } else if (itemId == R.id.nav_reservations) {
                fragment = new ReservationsFragment();
                title = "Your Reservations";
            } else if (itemId == R.id.nav_favorites) {
                fragment = new FavoritesFragment();
                title = "Your Favorites";
            } else if (itemId == R.id.nav_featured) {
                fragment = new FeaturedPropertiesFragment();
                title = "Featured Properties";
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
                title = "Profile Management";
            } else if (itemId == R.id.nav_contact) {
                fragment = new ContactFragment();
                title = "Contact Us";
            } else if (itemId == R.id.nav_logout) {
                logout();
                return true;
            }
        }

        if (fragment != null) {
            loadFragment(fragment);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        // Pass data to fragments if needed
        Bundle bundle = new Bundle();
        bundle.putString("userEmail", userEmail);
        bundle.putString("jsonData", jsonData);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }    private void logout() {
        // Clear SharedPreferences
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("remember_me", false);
        editor.apply();

        // Also clear UserPrefs used by reservation system
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPrefs.edit();
        userEditor.clear();
        userEditor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate back to Welcome screen
        Intent intent = new Intent(HomeActivity.this, Welcome_layout.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
