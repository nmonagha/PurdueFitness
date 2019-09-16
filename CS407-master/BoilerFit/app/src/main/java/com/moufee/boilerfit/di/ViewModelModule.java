package com.moufee.boilerfit.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.moufee.boilerfit.ui.CorecViewModel;
import com.moufee.boilerfit.ui.UserProfileViewModel;
import com.moufee.boilerfit.ui.dining.DiningViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Maps ViewModel classes to ViewModel (providers)
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(DiningViewModel.class)
    abstract ViewModel bindDiningViewModel(DiningViewModel diningViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel.class)
    abstract ViewModel bindUserProfileViewModel(UserProfileViewModel userProfileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CorecViewModel.class)
    abstract ViewModel bindCorecViewModel(CorecViewModel corecViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(AppViewModelFactory factory);
}
