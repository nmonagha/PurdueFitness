package com.moufee.boilerfit.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.moufee.boilerfit.User;
import com.moufee.boilerfit.repository.UserRepository;

import javax.inject.Inject;

public class UserProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private LiveData<User> mUser;
    private UserRepository mUserRepository;

    public LiveData<User> getUser() {
        return mUser;
    }

    @Inject
    public UserProfileViewModel(UserRepository userRepository) {
        mUserRepository = userRepository;
        mUser = mUserRepository.getUser();

    }
}
