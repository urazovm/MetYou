package com.metyou;



import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import com.metyou.fragments.friends.BuddiesFragment;
import com.metyou.fragments.settings.SettingsFragment;
import com.metyou.util.ImageFetcher;


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
    private ImageFetcher imageFetcher;


    public AppPagerAdapter(FragmentManager fm, ImageFetcher imageFetcher) {
        super(fm);
        this.imageFetcher = imageFetcher;
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
