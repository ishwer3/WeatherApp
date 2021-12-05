package com.example.weatherapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.models.User

@Database(
    entities = [User::class],
    version = 1
)
abstract class UserDb : RoomDatabase() {

    abstract fun getUserDao() : UserDao

    companion object{

        @Volatile
        private var instance : UserDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                UserDb::class.java,
                "userdatabase.db"
            ).build()
    }
}