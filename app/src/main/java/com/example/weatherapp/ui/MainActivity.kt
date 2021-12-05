package com.example.weatherapp.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.weatherapp.R
import com.example.weatherapp.db.UserDb
import com.example.weatherapp.repository.UserRepo
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.MainViewModelProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    lateinit var mainViewModel: MainViewModel
    private lateinit var userList: MutableList<String>
    lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        auth = Firebase.auth

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()

        val repository = UserRepo(UserDb(this))
        val viewModelProviderFactory = MainViewModelProviderFactory(application,repository)

        mainViewModel = ViewModelProvider(this,viewModelProviderFactory).get(MainViewModel::class.java)

        if (auth.currentUser != null){
            val userid = auth.currentUser!!.uid
            val useremail = auth.currentUser!!.email
            editor.putString("userid",userid)
            editor.putString("useremail",useremail)
            editor.commit()
        }

    }
}