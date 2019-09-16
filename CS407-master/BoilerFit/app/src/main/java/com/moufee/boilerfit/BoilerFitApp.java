package com.moufee.boilerfit;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.SharedPreferences;

import com.moufee.boilerfit.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;

/**
 * The Application for this App
 * Allows Dagger Android dependency injection
 */

public class BoilerFitApp extends Application implements HasActivityInjector, HasServiceInjector {

    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<Service> mServiceDispatchingAndroidInjector;

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return mServiceDispatchingAndroidInjector;
    }

    @Inject
    SharedPreferences mSharedPreferences;

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return mDispatchingAndroidInjector;
    }

    @Override
    public void onCreate() {
        DaggerAppComponent.builder().application(this).build().inject(this);
        super.onCreate();

    }
}
