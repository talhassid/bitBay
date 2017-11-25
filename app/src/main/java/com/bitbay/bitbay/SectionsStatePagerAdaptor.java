package com.bitbay.bitbay;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roey Semesh on 11/25/2017.
 * this class maneging the fragments list
 */

public class SectionsStatePagerAdaptor extends FragmentStatePagerAdapter{

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public SectionsStatePagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title){

        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
