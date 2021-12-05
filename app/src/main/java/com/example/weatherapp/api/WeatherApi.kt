package com.example.weatherapp.api

import com.example.weatherapp.WeatherResponse
import com.example.weatherapp.utils.Constants.Companion.API_ID
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/onecall")
    suspend fun getWeatherInfo(
        @Query("lat")
        lat:String,
        @Query("lon")
        long:String,
        @Query("units")
        units:String="imperial",
        @Query("appid")
        apiId:String = API_ID
    ) : Response<WeatherResponse>
}