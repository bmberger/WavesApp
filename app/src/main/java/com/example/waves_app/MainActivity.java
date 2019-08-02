/*
 * Project: Waves
 *
 * Purpose: To travel between different fragments via gesture-based navigation
 *
 * Reference(s): Briana Berger, Angela Liu
 */

package com.example.waves_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.waves_app.fragments.CalendarFragment;
import com.example.waves_app.fragments.FishTankFragment;
import com.example.waves_app.fragments.HomeFragment;
import com.example.waves_app.fragments.OceanViewFragment;
import com.example.waves_app.fragments.WebViewFragment;

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

        // Enables us to have gesture-based navigation between the three main fragments
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new CalendarFragment());
        fragmentList.add(new FishTankFragment());

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new VPagerAdapter(getSupportFragmentManager(), fragmentList);

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                // Utilized when you scroll to a different fragment and need the updated data
                viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        OceanViewFragment oceanView = (OceanViewFragment) getSupportFragmentManager().findFragmentByTag("OceanView");
        WebViewFragment webView = (WebViewFragment) getSupportFragmentManager().findFragmentByTag("WebView");

        if (currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else if ((oceanView != null && oceanView.isVisible()) || (webView != null && webView.isVisible())) {
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}