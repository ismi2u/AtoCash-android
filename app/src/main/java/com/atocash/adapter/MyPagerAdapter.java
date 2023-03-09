package com.atocash.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by geniuS on 9/5/2019.
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> fragmentNames;
    private ArrayList<Fragment> fragmentList;

    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentNames = new ArrayList<>();
        fragmentList = new ArrayList<>();
    }

    //for adding fragments
    public void addFragment(String fragmentName, Fragment fragment) {
        fragmentNames.add(fragmentName);
        fragmentList.add(fragment);
    }

    //for retriving fragments
    public int getFragment(String fragmentName) {
        for (int i = 0; i < fragmentNames.size(); i++) {
            if (fragmentNames.get(i).equals(fragmentName)) {
                return i;
            }
        }
        return 0;
    }

    //to get pageTitle
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentNames.get(position);
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
