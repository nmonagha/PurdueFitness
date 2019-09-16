package com.moufee.boilerfit.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.moufee.boilerfit.corec.CorecFacility;
import com.moufee.boilerfit.repository.CorecRepository;
import com.moufee.boilerfit.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class CorecViewModel extends ViewModel {
    // TODO: Implement the ViewModel, possibly migrate to one VM for the Activity
    private CorecRepository mCorecRepository;
    private LiveData<Resource<List<CorecFacility>>> mCurrentUsage;

    @Inject
    public CorecViewModel(CorecRepository corecRepository) {
        mCorecRepository = corecRepository;
        mCurrentUsage = mCorecRepository.getCurrentActivity();
    }

    public LiveData<Resource<List<CorecFacility>>> getCurrentUsage() {
        return mCurrentUsage;
    }


}
