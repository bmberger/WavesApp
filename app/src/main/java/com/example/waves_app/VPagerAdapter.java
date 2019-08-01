package com.example.waves_app;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class VPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;

    public VPagerAdapter(FragmentManager manager, List<Fragment> fragmentList) {
        super(manager);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}