package com.example.weatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usertable")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val first_name:String,
    val last_name:String,
    val email:String
)
