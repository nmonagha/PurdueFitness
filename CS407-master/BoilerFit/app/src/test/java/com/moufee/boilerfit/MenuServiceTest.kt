package com.moufee.boilerfit

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.moufee.boilerfit.api.AllergensTypeConverter
import com.moufee.boilerfit.api.LocalTimeTypeConverter
import com.moufee.boilerfit.api.MenuService
import com.moufee.boilerfit.menus.Allergens
import org.hamcrest.CoreMatchers.notNullValue
import org.joda.time.LocalTime
import org.junit.Assert.assertThat
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import org.hamcrest.CoreMatchers.`is` as Is

class MenuServiceTest {
    private val mGson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeConverter())
            .registerTypeAdapter(Allergens::class.java, AllergensTypeConverter())
            .create()
    private val mMenuService = Retrofit.Builder()
            .baseUrl("https://api.hfs.purdue.edu")
            .addConverterFactory(GsonConverterFactory.create(mGson))
            .build()
            .create(MenuService::class.java)


    @Test
    @Throws(IOException::class)
    fun testGetNutrition() {
        val response = mMenuService.getNutrition("20710ffb-8999-40a6-acfd-d47d361aba57").execute()
        val nutritionResponse = response.body()
        assertThat(nutritionResponse, notNullValue())
        assertThat(nutritionResponse?.name, Is("Southern Style Biscuits"))
        assertThat(nutritionResponse?.nutrition, notNullValue())
        assertThat(nutritionResponse?.nutrition?.size, Is(15))
        assertThat(nutritionResponse?.isVegetarian, Is(true))
        assertThat(nutritionResponse?.ingredients, notNullValue())
    }
}
