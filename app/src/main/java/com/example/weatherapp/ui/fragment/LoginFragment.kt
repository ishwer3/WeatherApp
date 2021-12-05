package com.example.weatherapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class LoginFragment : Fragment() {
    lateinit var auth: FirebaseAuth

    private var fragmentLoginBinding: FragmentLoginBinding? = null
    private val binding get() = fragmentLoginBinding!!

    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.button.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val username = binding.etUsername.text.trim().toString()
        val password = binding.etPassword.text.toString()
        if (binding.etUsername.text.isEmpty()  || binding.etPassword.text.isEmpty()){
            Toast.makeText(context,"Enter All Fields",Toast.LENGTH_SHORT).show()
        }else if (password.length < 6){
            Toast.makeText(context,"Password too short",Toast.LENGTH_SHORT).show()
        }else if (username.contains("@")){
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    auth.signInWithEmailAndPassword(username, password).addOnCompleteListener { result ->
                        if (result.isSuccessful){
                            navController.navigate(R.id.action_loginFragment_to_dashboardFragment)
                        }else{
                            Toast.makeText(context,"Invalid UserName or Password",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
        context?.let {
            FirebaseApp.initializeApp(it.applicationContext)
        }
    }

    private fun checkLoggedInState() {
        if (auth.currentUser != null) {
            navController.navigate(R.id.action_loginFragment_to_dashboardFragment)
        }
    }
}
