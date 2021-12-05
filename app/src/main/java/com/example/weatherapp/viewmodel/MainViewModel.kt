package com.example.weatherapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.WeatherResponse
import com.example.weatherapp.models.User
import com.example.weatherapp.repository.UserRepo
import com.example.weatherapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MainViewModel(
    app : Application,
    val userRepo: UserRepo
) : AndroidViewModel(app) {

    var weatherResponse: WeatherResponse? = null
    val weatherinfo: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()


    fun saveUser(user: User) = viewModelScope.launch {
        userRepo.insert(user)
    }

    fun deleteUser(user: User) = viewModelScope.launch {
        userRepo.delete(user)
    }

    fun getAllUsers() = userRepo.getAllUsers()

    fun getWeatherInfo(lat: String, long: String) = viewModelScope.launch {

        weatherinfo.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = userRepo.getAllWeatherInfo(lat, long)
                weatherinfo.postValue(handleWeatherResponse(response))
            } else {
                weatherinfo.postValue(Resource.Error("No Internet Connection"))
            }

        }catch (t: Throwable){
            when(t){
                is IOException -> weatherinfo.postValue(Resource.Error("Network Failure"))
                else -> weatherinfo.postValue(Resource.Error(t.localizedMessage))
            }
        }
    }

    private fun handleWeatherResponse(response: Response<WeatherResponse>): Resource<WeatherResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (weatherResponse == null) {
                    weatherResponse = resultResponse
                } else {
                }
                return Resource.Success(weatherResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<WeatherApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activityNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activityNetwork) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}