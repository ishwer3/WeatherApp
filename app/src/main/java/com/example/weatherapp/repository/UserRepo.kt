package com.example.weatherapp.repository

import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.db.UserDb
import com.example.weatherapp.models.User

class UserRepo(val db : UserDb) {

    suspend fun insert(user: User) = db.getUserDao().insert(user)

    fun getAllUsers() = db.getUserDao().getAllUsers()

    suspend fun delete(user: User) = db.getUserDao().delete(user)

    suspend fun getAllWeatherInfo(lat:String,long: String) =
        RetrofitInstance.api.getWeatherInfo(lat,long)

}