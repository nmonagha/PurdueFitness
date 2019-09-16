package com.moufee.boilerfit.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.google.gson.Gson;
import com.moufee.boilerfit.api.MenuService;
import com.moufee.boilerfit.menus.nutrition.NutritionResponse;
import com.moufee.boilerfit.util.AppExecutors;
import com.moufee.boilerfit.util.Resource;
import com.moufee.boilerfit.menus.FullDayMenu;
import com.moufee.boilerfit.menus.UpdateMenuTask;

import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;


/**
 * Where all the menus come from
 * Abstracts the data sources from the rest of the app
 * Creates threads to retrieve and process menu data
 */
@Singleton
public class MenuRepository {

    private static final String TAG = "MenuRepository";

    private MenuService mMenuService;
    private Gson mGson;
    private Context mApplicationContext;
    private AppExecutors mAppExecutors;

    @Inject
    public MenuRepository(MenuService menuService, Gson gson, Context applicationContext, AppExecutors appExecutors) {
        mMenuService = menuService;
        mGson = gson;
        mApplicationContext = applicationContext;
        mAppExecutors = appExecutors;

    }

    public LiveData<Resource<FullDayMenu>> getMenus() {
        return getMenus(new DateTime());
    }


    public Call<NutritionResponse> nutritionItem(String foodId) {
        return mMenuService.getNutrition(foodId);
    }

    public LiveData<Resource<FullDayMenu>> getMenus(DateTime dateTime) {
        MutableLiveData<Resource<FullDayMenu>> data = new MutableLiveData<>();
        UpdateMenuTask task = new UpdateMenuTask(data, mApplicationContext, mMenuService, mGson).withDate(dateTime);
        mAppExecutors.diskIO().execute(task);
        return data;
    }


}
