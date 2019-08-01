package com.example.waves_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.example.waves_app.fragments.CalendarFragment;
import com.example.waves_app.fragments.FishTankFragment;
import com.example.waves_app.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.List;

// Home page
public class MainActivity extends AppCompatActivity {

    private VerticalViewPager viewPager;
    private PagerAdapter pagerAdapter;

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
    }
}