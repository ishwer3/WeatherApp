package com.example.weatherapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherapp.models.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)


    @Query("SELECT * FROM usertable")
    fun getAllUsers() : LiveData<List<User>>

}