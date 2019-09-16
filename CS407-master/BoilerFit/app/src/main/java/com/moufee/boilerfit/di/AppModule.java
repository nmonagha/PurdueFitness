package com.moufee.boilerfit.di;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moufee.boilerfit.api.AllergensTypeConverter;
import com.moufee.boilerfit.api.CorecService;
import com.moufee.boilerfit.api.LocalTimeTypeConverter;
import com.moufee.boilerfit.api.MenuService;
import com.moufee.boilerfit.menus.Allergens;
import com.moufee.boilerfit.util.AppExecutors;

import org.joda.time.LocalTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The Dagger App Module, Provides dependencies for injection
 */

@Module(includes = {ViewModelModule.class})
class AppModule {

    @Singleton
    @Provides
    FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    FirebaseFirestore provideFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Singleton
    @Provides
    FirebaseStorage provideFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    @Singleton
    @Provides
    MenuService provideMenuService(AppExecutors executors, Gson gson, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("https://api.hfs.purdue.edu")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executors.diskIO())
                .build()
                .create(MenuService.class);
    }

    @Singleton
    @Provides
    CorecService provideCorecService(AppExecutors executors, Gson gson, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("https://www.purdue.edu/drsfacilityusage/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executors.diskIO())
                .build()
                .create(CorecService.class);
    }

    @Singleton
    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeConverter())
                .registerTypeAdapter(Allergens.class, new AllergensTypeConverter())
                .create();
    }

    @Singleton
    @Provides
    OkHttpClient provideHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    @Singleton
    @Provides
    SharedPreferences sharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Singleton
    @Provides
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    AlarmManager provideAlarmManager(Context context) {
        return context.getSystemService(AlarmManager.class);
    }
}
