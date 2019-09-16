package com.moufee.boilerfit.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.moufee.boilerfit.menus.DiningCourtMenu;
import com.moufee.boilerfit.ui.CorecFragment;

public class CorecPagerAdapter extends FragmentStatePagerAdapter {

    private DiningCourtMenu mDiningCourtMenu;

    public CorecPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {

        return CorecFragment.newInstance();
    }

    @Override
    public int getCount() {
        if (mDiningCourtMenu != null) {
            return mDiningCourtMenu.getMeals().size();
        }
        return 0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Favorites";
        }
        else {
            return "Exercises";
        }
    }

}