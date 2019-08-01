package com.example.waves_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.example.waves_app.fragments.CalendarFragment;
import com.example.waves_app.fragments.FishTankFragment;
import com.example.waves_app.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

// Home page
public class MainActivity extends AppCompatActivity {

    private VerticalViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new CalendarFragment());
        fragmentList.add(new FishTankFragment());

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new VPagerAdapter(getSupportFragmentManager(), fragmentList);

        viewPager.setAdapter(pagerAdapter);

//        final FragmentManager fragmentManager = getSupportFragmentManager();
//
//        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//
//        bottomNavigationView.setBackground(getResources().getDrawable(R.drawable.bnv_sandy));
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment fragment;
//                switch (item.getItemId()) {
//                    case R.id.miHome:
//                        fragment = new HomeFragment();
//                        break;
//                    case R.id. miCalendar:
//                        fragment = new CalendarFragment();
//                        break;
//                    case R.id.miFishTank:
//                        fragment = new FishTankFragment();
//                        break;
//                    default:
//                        return true;
//                }
//
//                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
//                return true;
//            }
//        });
//
//        // Set default selection
//        bottomNavigationView.setSelectedItemId(R.id.miHome);
    }

//    @Override
//    public void onBackPressed() {
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//        int selectedItemId = bottomNavigationView.getSelectedItemId();
//
//        if (R.id.miHome != selectedItemId) {
//            setHomeItem(MainActivity.this);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    public static void setHomeItem(Activity activity) {
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) activity.findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.miHome);
//    }
}