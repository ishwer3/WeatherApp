package com.example.weatherapp.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.ui.MainActivity
import com.example.weatherapp.utils.Resource
import com.example.weatherapp.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*


class WeatherFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    private var fragmentWeatherBinding: FragmentWeatherBinding? = null
    private val binding get() = fragmentWeatherBinding!!

    lateinit var mainViewModel: MainViewModel

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentWeatherBinding = FragmentWeatherBinding.inflate(inflater, container, false)
        val view = binding.root
        auth = Firebase.auth
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        fetchLocation()

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    fetchLocation()
                } else {
                    askLocationPermission()
                }
            }

        navController = Navigation.findNavController(view)


        binding.backBtnImage.setOnClickListener {
            navController.navigate(R.id.action_weatherFragment_to_dashboardFragment)
        }

        binding.btnLogout.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage("Are you sure?You want to logout")
                .setCancelable(false)
                .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, i ->
                    auth.signOut()
                    navController.navigate(R.id.action_weatherFragment_to_loginFragment)
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, i ->
                    dialog.dismiss()
                })

            val alert = dialogBuilder.create()
            alert.setTitle("Logout")
            alert.show()

        }

        mainViewModel = (activity as MainActivity).mainViewModel

        mainViewModel.weatherinfo.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { weatherResponse ->
                        binding.textTemp.text = weatherResponse.current.temp.toString()
                        binding.textHumidity.text = weatherResponse.current.humidity.toString()
                        binding.textWindSpeed.text = weatherResponse.current.wind_speed.toString()
                        binding.textWeatherType.text = weatherResponse.current.weather[0].main
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, "An Error Occurred : $message", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation()
        } else {
            askLocationPermission()
        }
    }

    private fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                //alert dialog
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setMessage("This App requires Location Permission")
                    .setCancelable(false)
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, i ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                            1
                        )
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, i ->
                        dialog.dismiss()
                    })

                val alert = dialogBuilder.create()
                alert.setTitle("Allow Permissions")
                alert.show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {

        val task = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                val latitude: String = location.latitude.toString()
                val longitude: String = location.longitude.toString()

                mainViewModel.getWeatherInfo(latitude, longitude)
            }
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentWeatherBinding = null
    }
}


