package com.moufee.boilerfit.api;

import com.moufee.boilerfit.menus.DiningCourtMenu;
import com.moufee.boilerfit.menus.nutrition.NutritionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Defines the available HTTP requests for the Menus API
 */

public interface MenuService {

    @Headers({
            "Accept: text/json"
    })
    @GET("/menus/v2/locations/{location}/{date}")
    Call<DiningCourtMenu> getMenu(@Path("location") String diningCourtName, @Path("date") String date);

    @Headers({
            "Accept: text/json"
    })
    @GET("/menus/v2/items/{itemId}")
    Call<NutritionResponse> getNutrition(@Path("itemId") String itemId);
}
