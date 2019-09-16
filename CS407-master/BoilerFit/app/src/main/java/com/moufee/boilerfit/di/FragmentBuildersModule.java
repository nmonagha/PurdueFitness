package com.moufee.boilerfit.di;

import com.moufee.boilerfit.FriendsList;
import com.moufee.boilerfit.ui.CorecFragment;
import com.moufee.boilerfit.ui.HomePageFragment;
import com.moufee.boilerfit.ui.dining.DiningCourtMenuFragment;
import com.moufee.boilerfit.ui.dining.DiningFragment;
import com.moufee.boilerfit.ui.dining.MealFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Defines the Fragments that may be injected
 */

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract HomePageFragment contributeHomePageFragment();

    @ContributesAndroidInjector
    abstract DiningFragment contributeDiningFragment();

    @ContributesAndroidInjector
    abstract CorecFragment contributeCorecFragment();

    @ContributesAndroidInjector
    abstract DiningCourtMenuFragment contributeDiningDetailFragment();

    @ContributesAndroidInjector
    abstract MealFragment contributeMealFragment();

    @ContributesAndroidInjector
    abstract FriendsList contributeFriendlistFragment();
}
