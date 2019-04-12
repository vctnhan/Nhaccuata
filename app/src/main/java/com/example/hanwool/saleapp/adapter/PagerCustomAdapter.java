package com.example.hanwool.saleapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.hanwool.saleapp.fragment.HomeFragment;
import com.example.hanwool.saleapp.fragment.NewSongFragment;
import com.example.hanwool.saleapp.fragment.ThemeFragment;

public class PagerCustomAdapter extends FragmentStatePagerAdapter {

    public PagerCustomAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        switch (position){
            case 0:
                frag = new HomeFragment();
                return frag;

            case 1:
                frag = new ThemeFragment();
                return frag;
            case 2:
                frag = new NewSongFragment();
                return frag;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Home";
                break;
            case 1:
                title = "Theme";
                break;
            case 2:
                title = "New";
                break;
        }
        return title;
    }
}
