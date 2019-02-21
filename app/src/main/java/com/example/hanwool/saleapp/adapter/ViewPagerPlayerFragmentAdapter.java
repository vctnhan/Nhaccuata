package com.example.hanwool.saleapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.hanwool.saleapp.fragment.Disc_Fragment;

import java.util.ArrayList;

public class ViewPagerPlayerFragmentAdapter extends FragmentPagerAdapter {
    public final ArrayList<Fragment> arrayFragment = new ArrayList<>();
    public ViewPagerPlayerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Disc_Fragment disc_fragment = new Disc_Fragment();
                return disc_fragment;

        }

        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }
//    public void AddFragment(Fragment fragment){
//        arrayFragment.add(fragment);
//    }
}
