package com.example.chatapp.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.helper.inflate
import com.example.chatapp.view.fragment.ContactList
import kotlinx.android.synthetic.main.contact_list_item.view.*
data class AddedContactList(
    val number:String
)
class AddContactGroupAdapter(private val contactList: List<ContactList>): RecyclerView.Adapter<AddContactGroupAdapter.AddContactGroupViewHolder>() {
    val newList: MutableList<String> = ArrayList()
    inner class AddContactGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactGroupViewHolder {
        return AddContactGroupViewHolder(parent.inflate(R.layout.contact_list_item))
    }
    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: AddContactGroupAdapter.AddContactGroupViewHolder, position: Int) {
        val contacts = contactList[position]
        Log.i("checkClick","check => "+newList.toString())

        holder.itemView.apply {
            user_name_txt.text = contacts.name

            contactListItemRootLay.setOnClickListener {

                newList.add(contacts.name)
                Log.i("checkClick",newList.toString())

            }
        }
    }
}