package com.metyou;



import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.View;

import com.metyou.fragments.BuddiesFragment;
import com.metyou.fragments.SettingsFragment;


/**
 * Created by mihai on 7/28/14.
 */
public class AppPagerAdapter extends FragmentPagerAdapter {

    private static final int buddiesPosition = 0;
    private static final int settingsPosition = 1;
    private static final String buddiesTitle = "Buddies";
    private static final String settingsTitle = "Settings";
    private static final int fragmentsNum = 2;
    private BuddiesFragment buddiesFragment;
    private SettingsFragment settingsFragment;


    public AppPagerAdapter(FragmentManager fm) {
        super(fm);
        buddiesFragment = new BuddiesFragment();
        settingsFragment = new SettingsFragment();
    }

    @Override
    public Fragment getItem(int i) {
        switch(i) {
            case buddiesPosition:
                return buddiesFragment;
            case settingsPosition:
                return settingsFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case buddiesPosition:
                return buddiesTitle;
            case settingsPosition:
                return settingsTitle;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return fragmentsNum;
    }
}
