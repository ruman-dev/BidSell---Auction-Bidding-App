package com.rumanweb.bidsell.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.fragments.HomeFragment;
import com.rumanweb.bidsell.fragments.MyBidsFragment;
import com.rumanweb.bidsell.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView btm_nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btm_nav_view = findViewById(R.id.bottomNavView);

//        custom_fragment_fun(new HomeFragment());
        btm_nav_view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_home) custom_fragment_fun(new HomeFragment());
                else if (item.getItemId() == R.id.menu_myBids) custom_fragment_fun(new MyBidsFragment());
                else if (item.getItemId() == R.id.menu_profile) custom_fragment_fun(new ProfileFragment());
                else custom_fragment_fun(new HomeFragment());
                return true;
            }
        });
    }

    public void custom_fragment_fun(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}