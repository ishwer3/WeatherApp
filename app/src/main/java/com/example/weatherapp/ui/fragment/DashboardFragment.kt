package com.example.weatherapp.ui.fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.adapter.UserListAdapter
import com.example.weatherapp.databinding.DialogLayoutBinding
import com.example.weatherapp.databinding.FragmentDashboardBinding
import com.example.weatherapp.models.User
import com.example.weatherapp.ui.MainActivity
import com.example.weatherapp.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class DashboardFragment : Fragment() {

    private lateinit var firstName:String
    private lateinit var lastName:String
    private lateinit var email:String

    private lateinit var navController: NavController

    private lateinit var mainViewModel: MainViewModel
    private lateinit var useradapter: UserListAdapter

    private var fragmentDashboardBinding : FragmentDashboardBinding? = null
    private val binding get() = fragmentDashboardBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDashboardBinding = FragmentDashboardBinding.inflate(inflater,container,false)
        val view = binding.root

        useradapter = UserListAdapter()

        binding.recyclerView.apply {
            adapter = useradapter
            layoutManager = LinearLayoutManager(activity)
        }

        binding.floatingActionButton.setOnClickListener {
            openDialog()
        }
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        mainViewModel = (activity as MainActivity).mainViewModel

        useradapter.setOnItemClickListner {
            navController.navigate(R.id.action_dashboardFragment_to_weatherFragment)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val user = useradapter.differ.currentList[position]
                mainViewModel.deleteUser(user)
                Snackbar.make(view,"User Deleted Successfully", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        mainViewModel.saveUser(user)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerView)
        }

        mainViewModel.getAllUsers().observe(viewLifecycleOwner, Observer { users ->
            useradapter.differ.submitList(users)
        })

    }

    private fun openDialog() {

        val dialogLayoutBinding = DialogLayoutBinding.inflate(LayoutInflater.from(context))

        val dialog = context?.let { Dialog(it) }
        dialog?.apply {
            setContentView(R.layout.dialog_layout)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            }

            dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.setCancelable(true)

            val textFirstname = findViewById<TextView>(R.id.etFirstName)
            val textLastname  = findViewById<TextView>(R.id.etLastName)
            val textEmail  = findViewById<TextView>(R.id.etEmail)
            val saveButton = findViewById<Button>(R.id.btnSave)
            val cancelButton = findViewById<Button>(R.id.btnCancel)


            saveButton.setOnClickListener {
                if (textFirstname.text.toString().isNotEmpty() && textLastname.text.toString().isNotEmpty() && textEmail.text.toString().isNotEmpty()){
                    mainViewModel.saveUser(User(0,textFirstname.text.toString(),textLastname.text.toString(),textEmail.text.toString()))
                    Snackbar.make(it,"User Saved Suceessfully",Snackbar.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"Enter All Fields",Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()

            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }

        firstName = dialogLayoutBinding.etFirstName.text.toString()
        lastName = dialogLayoutBinding.etLastName.text.toString()
        email = dialogLayoutBinding.etEmail.text.toString()
    }
}