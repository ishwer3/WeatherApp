package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.models.User

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val text_firstName = itemView.findViewById<TextView>(R.id.tvFirstName)
        val text_lastName = itemView.findViewById<TextView>(R.id.tvLastName)
        val text_email = itemView.findViewById<TextView>(R.id.tvEmail)
    }

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.single_user_layout,
                parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.apply {
            text_firstName.text = user.first_name
            text_lastName.text = user.last_name
            text_email.text = user.email
        }

        holder.itemView.setOnClickListener {
            onItemClickListner?.let {
                it(user)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListner: ((User) -> Unit)? = null

    fun setOnItemClickListner(listner : ((User) -> Unit)){
        onItemClickListner = listner
    }


}