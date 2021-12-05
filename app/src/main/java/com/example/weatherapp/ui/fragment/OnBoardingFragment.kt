package com.example.weatherapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : Fragment() {

    private var fragmentOnBoardingBinding: FragmentOnBoardingBinding? = null
    private val binding get() = fragmentOnBoardingBinding!!

    private lateinit var navController: NavController

    private lateinit var sideAnim:Animation


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentOnBoardingBinding = FragmentOnBoardingBinding.inflate(inflater,container,false)
        val view = binding.root
        // Inflate the layout for this fragment

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController =Navigation.findNavController(view)
        sideAnim = AnimationUtils.loadAnimation(context,R.anim.side_anim)

        binding.imageView2.animation = sideAnim

        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
        }
    }
}