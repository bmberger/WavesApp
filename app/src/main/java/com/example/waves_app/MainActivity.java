package com.example.waves_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.waves_app.fragments.CalendarFragment;
import com.example.waves_app.fragments.FishTankFragment;
import com.example.waves_app.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Home page
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                switch (item.getItemId()) {
                    case R.id.miHome:
                        fragment = new HomeFragment();
                        Toast.makeText(MainActivity.this, "Switched to home!", Toast.LENGTH_LONG).show();
                        break;
                    case R.id. miCalendar:
                        fragment = new CalendarFragment();
                        Toast.makeText(MainActivity.this, "Switched to cal!", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.miFishTank:
                        fragment = new FishTankFragment();
                        Toast.makeText(MainActivity.this, "Switched to fish!", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        return true;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.miHome);
    }
}
