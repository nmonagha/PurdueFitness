package com.moufee.boilerfit.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.moufee.boilerfit.menus.DiningCourtMenu;
import com.moufee.boilerfit.ui.dining.MealFragment;

public class MealPagerAdapter extends FragmentStatePagerAdapter {

    private DiningCourtMenu mDiningCourtMenu;

    public void setDiningCourtMenu(DiningCourtMenu menu) {
        mDiningCourtMenu = menu;
        notifyDataSetChanged();
    }

    public MealPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return MealFragment.newInstance(position);
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
        return mDiningCourtMenu.getMeals().get(position).getName();
    }

}
