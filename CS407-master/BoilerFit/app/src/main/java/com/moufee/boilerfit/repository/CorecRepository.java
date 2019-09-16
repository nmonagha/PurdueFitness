package com.moufee.boilerfit.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.moufee.boilerfit.api.CorecService;
import com.moufee.boilerfit.corec.CorecFacility;
import com.moufee.boilerfit.util.AppExecutors;
import com.moufee.boilerfit.util.Resource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

public class CorecRepository {
    private CorecService mCorecService;
    private AppExecutors mAppExecutors;
    public static final String TAG = "CorecRepository";

    @Inject
    public CorecRepository(CorecService corecService, AppExecutors appExecutors) {
        mCorecService = corecService;
        mAppExecutors = appExecutors;
    }


    public LiveData<Resource<List<CorecFacility>>> getCurrentActivity() {
        MutableLiveData<Resource<List<CorecFacility>>> responseLiveData = new MutableLiveData<>();
        responseLiveData.setValue(Resource.loading(null));
        mAppExecutors.networkIO().execute(() -> {
            try {
                Response<List<CorecFacility>> response = mCorecService.getCurrentActivity().execute();
                List<CorecFacility> data = response.body();
                if (!response.isSuccessful()) {
                    responseLiveData.postValue(Resource.error(response.message(), null));
                } else {
                    responseLiveData.postValue(Resource.success(data));
                }
            } catch (IOException e) {
                Log.e(TAG, "getCurrentActivity: ", e);
                responseLiveData.postValue(Resource.error("Error retrieving availability.", null));
            }
        });
        return responseLiveData;
    }
}
