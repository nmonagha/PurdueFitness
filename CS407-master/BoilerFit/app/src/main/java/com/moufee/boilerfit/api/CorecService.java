package com.moufee.boilerfit.api;

import com.moufee.boilerfit.corec.CorecFacility;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface CorecService {

    @Headers({
            "Accept: text/json"
    })
    @GET("CurrentActivity")
    Call<List<CorecFacility>> getCurrentActivity();
}
