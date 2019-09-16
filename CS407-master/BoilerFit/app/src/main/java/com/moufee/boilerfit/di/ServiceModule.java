package com.moufee.boilerfit.di;

import com.moufee.boilerfit.util.NotificationService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract NotificationService contributeNotificationService();
}
