package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_map) {
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                } else if (id == R.id.navigation_addQR) {
                    startActivity(new Intent(getApplicationContext(), AddQRActivity.class));
                } else if (id == R.id.navigation_qrLibrary){
                    startActivity(new Intent(getApplicationContext(), LibraryActivity.class));
                }
                return true;
            }
        });
    }

}