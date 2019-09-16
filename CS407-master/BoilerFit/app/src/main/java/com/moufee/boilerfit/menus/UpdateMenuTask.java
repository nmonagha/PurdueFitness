package com.moufee.boilerfit.menus;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moufee.boilerfit.api.MenuService;
import com.moufee.boilerfit.util.Resource;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A task to update LiveData and cache responses
 */

public class UpdateMenuTask implements Runnable {

    private MutableLiveData<Resource<FullDayMenu>> mFullMenu;
    private Context mContext;
    private DateTime mMenuDate;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final String TAG = "UpdateMenuTask";
    private static final String[] DINING_COURTS = {"Earhart", "Ford", "Wiley", "Windsor", "Hillenbrand"};
    private boolean mFetchedFromFile = false;
    private MenuService mMenuService;
    private Gson mGson;
    private ConnectivityManager mConnectivityManager;


    public UpdateMenuTask(MutableLiveData<Resource<FullDayMenu>> liveData, Context context, MenuService menuService, Gson gson) {
        this.mFullMenu = liveData;
        mContext = context;
        mMenuService = menuService;
        mMenuDate = new DateTime();
        mGson = gson;
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public UpdateMenuTask withDate(DateTime date) {
        this.mMenuDate = date;
        return this;
    }

    @Override
    public void run() {
//        mFullMenu.postValue(Resource.<FullDayMenu>loading(null));
        ArrayList<DiningCourtMenu> fileMenus = getMenusFromFile(DATE_TIME_FORMATTER.print(mMenuDate));
        if (fileMenus != null) {
            mFetchedFromFile = true;
            mFullMenu.postValue(Resource.success(new FullDayMenu(fileMenus, mMenuDate, hasLateLunch(fileMenus))));
            Log.d(TAG, "getFullMenu: Read from file!");
        } else {
            mFullMenu.postValue(Resource.<FullDayMenu>loading(null));
        }
        if (fileMenus == null || shouldFetch()) {
            fetchFromNetwork();
        }
    }

    private boolean shouldFetch() {
        //todo: check if the menus from the file are too short, have a lot of null values, etc and decide when to fetch
        return true;
//        DateTime now = new DateTime();
//        Log.d(TAG, "shouldFetch: days: "+Days.daysBetween(now, mMenuDate).getDays());
//        return Math.abs(Days.daysBetween(now, mMenuDate).getDays()) > 5;
    }

    private boolean hasLateLunch(List<DiningCourtMenu> menus) {
        for (DiningCourtMenu menu :
                menus) {
            if (menu.servesLateLunch())
                return true;
        }
        return false;
    }



    private ArrayList<DiningCourtMenu> getMenusFromFile(String formattedDate) {
        File filesDir = mContext.getCacheDir();
        File sourceFile = new File(filesDir, formattedDate + ".json");
        ArrayList<DiningCourtMenu> result;
        if (!sourceFile.exists())
            return null;
        FileReader sourceReader = null;
        try {
            sourceReader = new FileReader(sourceFile);
            Type type = new TypeToken<ArrayList<DiningCourtMenu>>() {
            }.getType();
            result = mGson.fromJson(sourceReader, type);
            sortMenus(result);
        } catch (Exception e) {
            try {
                if (sourceReader != null)
                    sourceReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        try {
            sourceReader.close();
        } catch (IOException e) {
            Log.e(TAG, "getMenusFromFile: ", e);
        }

        return result;
    }

    private void saveMenuToFile(String filename, List<DiningCourtMenu> menus) throws IOException {
        File filesDir = mContext.getCacheDir();
        File outputFile = new File(filesDir, filename);
        FileWriter writer = new FileWriter(outputFile);
        String json = mGson.toJson(menus);
        writer.write(json);
        writer.close();
    }

    private void sortMenus(List<DiningCourtMenu> menus) {

        String[] customOrder = PreferenceManager.getDefaultSharedPreferences(mContext).getString("dining_court_order", "").split(",");
        Log.d(TAG, "sortMenus custom : " + Arrays.toString(customOrder));
        if (customOrder.length  == DINING_COURTS.length)
            Collections.sort(menus, new DiningCourtComparator(Arrays.asList(customOrder)));
        else
            Collections.sort(menus, new DiningCourtComparator());
    }

    private void fetchFromNetwork() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            if (mFetchedFromFile)
                return;
            else
                mFullMenu.postValue(Resource.<FullDayMenu>error("Not connected to network", null));
            return;
        }
        //this is similar to the initial implementation from the architecture components guide
        final List<DiningCourtMenu> tempMenusList = new ArrayList<>();
        final String dateString = DATE_TIME_FORMATTER.print(mMenuDate);
        for (final String diningCourt : DINING_COURTS) {
            Call<DiningCourtMenu> menuCall = mMenuService.getMenu(diningCourt, dateString);
            menuCall.enqueue(new Callback<DiningCourtMenu>() {
                @Override
                public void onResponse(@NonNull Call<DiningCourtMenu> call, @NonNull Response<DiningCourtMenu> response) {
                    if (response.isSuccessful())
                        tempMenusList.add(response.body());

                    if (tempMenusList.size() == DINING_COURTS.length) {
                        sortMenus(tempMenusList);
                        mFullMenu.postValue(Resource.success(new FullDayMenu(tempMenusList, mMenuDate, hasLateLunch(tempMenusList))));
                        //save to json
                        try {
                            saveMenuToFile(dateString + ".json", tempMenusList);

                        } catch (IOException e) {
                            Log.e(TAG, "onResponse: error saving to file ", e);
                            mFullMenu.postValue(Resource.<FullDayMenu>error(e.getMessage() != null ? e.getMessage() : "an error occurred while saving to file", null));
                        }
                    }
                }

                @Override
                public void onFailure(Call<DiningCourtMenu> call, Throwable t) {
                    //todo: handle failure
                    Log.e(TAG, "onFailure: Network error", t);
                    if (mFullMenu.getValue() != null)
                        mFullMenu.postValue(Resource.error("Network Error", mFullMenu.getValue().data));
                    else
                        mFullMenu.postValue(Resource.<FullDayMenu>error(t.getMessage(), null));
                }
            });


        }
    }
}
