package com.moufee.boilerfit.di;

import com.moufee.boilerfit.ui.LoginActivity;
import com.moufee.boilerfit.ui.MainActivity;
import com.moufee.boilerfit.ui.settings.SettingsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * The Dagger Module for MenuActivity
 * Allows MenuActivity and Fragments it hosts to use dependency injection
 */
@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract SettingsActivity contributeSettingsActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();
}
