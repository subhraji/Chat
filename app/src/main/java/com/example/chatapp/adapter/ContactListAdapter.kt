package com.example.chatapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.helper.inflate
import com.example.chatapp.model.pojo.sync_contacts.User
import kotlinx.android.synthetic.main.contact_list_item.view.*
import kotlinx.android.synthetic.main.fragment_login_number.*

class ContactListAdapter(private val userList: List<User>, private val context: Context, private val phoneno:String): RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>()  {

    inner class ContactListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        return ContactListViewHolder(parent.inflate(R.layout.contact_list_item))
    }
    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ContactListAdapter.ContactListViewHolder, position: Int) {
        /*lateinit var navController: NavController
        navController = Navigation.findNavController(context as Activity, R.id.login_nav_host_fragment)*/

        val users = userList[position]
        holder.itemView.apply {
            user_name_txt.text = users.phoneno

            contactListItemRootLay.setOnClickListener {
                if(!phoneno.equals(users.phoneno)){
                    val bundle = bundleOf("userId" to users.id, "phoneno" to users.phoneno, "" +
                            "avatar" to users.avatar)
                    findNavController().navigate(R.id.action_contactsListFragment_to_chatFragment, bundle)
                }else{
                    Toast.makeText(context,"can not text on this no !!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}