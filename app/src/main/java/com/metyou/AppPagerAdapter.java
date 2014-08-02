package com.metyou;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.metyou.fragments.BuddiesFragment;

/**
 * Created by mihai on 7/28/14.
 */
public class AppPagerAdapter extends FragmentPagerAdapter {

    private static final int buddiesPosition = 0;
    private static final String buddiesTitle = "Buddies";
    private static final int fragmentsNum = 2;

    public AppPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
//        switch(i) {
//            case buddiesPosition:
                return new BuddiesFragment();
//            default:
//                return null;
//        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        switch (position){
//            case buddiesPosition:
                return buddiesTitle;
//            default:
//                return null;
//        }
    }


    @Override
    public int getCount() {
        return fragmentsNum;
    }
}
