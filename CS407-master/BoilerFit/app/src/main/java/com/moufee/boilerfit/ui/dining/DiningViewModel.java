package com.moufee.boilerfit.ui.dining;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v7.view.ActionMode;

import com.moufee.boilerfit.User;
import com.moufee.boilerfit.menus.DiningCourtMenu;
import com.moufee.boilerfit.menus.DiningMenuItem;
import com.moufee.boilerfit.menus.FullDayMenu;
import com.moufee.boilerfit.menus.nutrition.NutritionResponse;
import com.moufee.boilerfit.repository.MenuRepository;
import com.moufee.boilerfit.repository.UserRepository;
import com.moufee.boilerfit.util.Callback;
import com.moufee.boilerfit.util.Resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;

import retrofit2.Call;

public class DiningViewModel extends ViewModel {
    private MenuRepository mMenuRepository;
    private UserRepository mUserRepository;
    private LiveData<Resource<FullDayMenu>> mCurrentMenu;
    private MutableLiveData<Integer> mSelectedDiningCourtIndex;
    private MediatorLiveData<DiningCourtMenu> mSelectedMenu;
    private Set<DiningMenuItem> mSelectedItems;
    private LiveData<User> mUserLiveData;
    private MutableLiveData<Set<DiningMenuItem>> mSelectedItemsLiveData = new MutableLiveData<>();
    private ActionMode mActionMode;

    private HashMap<String, Boolean> temp;

    @Inject
    public DiningViewModel(MenuRepository menuRepository, UserRepository userRepository) {
        mMenuRepository = menuRepository;
        mUserRepository = userRepository;
        mUserLiveData = userRepository.getUser();
        mCurrentMenu = mMenuRepository.getMenus();
        mSelectedDiningCourtIndex = new MutableLiveData<>();
        mSelectedMenu = new MediatorLiveData<>();
        mSelectedItems = new HashSet<>();
        mSelectedItemsLiveData.setValue(mSelectedItems);
        userRepository.getHealthyMap(new Callback<HashMap<String, Boolean>>() {
            @Override
            public void accept(@Nullable HashMap<String, Boolean> stringBooleanHashMap) {
                temp = stringBooleanHashMap;
                }
                }
        );
    }

    public LiveData<Resource<FullDayMenu>> getCurrentMenu() {
        return mCurrentMenu;
    }

    public void setSelectedDiningCourtIndex(int selectedDiningCourtIndex) {
        mSelectedDiningCourtIndex.setValue(selectedDiningCourtIndex);
    }

    public LiveData<Integer> getSelectedDiningCourtIndex() {
        return mSelectedDiningCourtIndex;
    }

    public LiveData<Set<DiningMenuItem>> getSelectedItems() {
        return mSelectedItemsLiveData;
    }

    public void addSelectedItem(DiningMenuItem item) {
        mSelectedItems.add(item);
        mSelectedItemsLiveData.setValue(mSelectedItems);
    }

    public Call<NutritionResponse> getNutrition(String s) {
        return mMenuRepository.nutritionItem(s);
    }

    public void removeSelectedItem(DiningMenuItem item) {
        mSelectedItems.remove(item);
        mSelectedItemsLiveData.setValue(mSelectedItems);
    }

    public void clearSelectedItems() {
        mSelectedItems.clear();
        mSelectedItemsLiveData.setValue(mSelectedItems);
    }

    public HashMap<String, Boolean> isHealthy_res() {
        return temp;
    }
    public ActionMode getActionMode() {
        return mActionMode;
    }

    public void setActionMode(ActionMode actionMode) {
        mActionMode = actionMode;
    }

    public LiveData<User> getUser() {
        return mUserLiveData;
    }
}
