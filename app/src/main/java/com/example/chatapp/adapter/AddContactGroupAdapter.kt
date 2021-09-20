package com.example.chatapp.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.helper.gone
import com.example.chatapp.helper.inflate
import com.example.chatapp.helper.visible
import com.example.chatapp.model.pojo.sync_contacts.User
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.add_contact_to_grp_item.view.*

class AddContactGroupAdapter(private val contactList: List<User>, private val context: Context, private val myPhoneno:String): RecyclerView.Adapter<AddContactGroupAdapter.AddContactGroupViewHolder>() {
    val newList: MutableList<String> = ArrayList()
    private var addClick:Boolean = false
    lateinit var sharedPreference: SharedPreferences


    inner class AddContactGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactGroupViewHolder {
        return AddContactGroupViewHolder(parent.inflate(R.layout.add_contact_to_grp_item))
    }
    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: AddContactGroupAdapter.AddContactGroupViewHolder, position: Int) {
        val contacts = contactList[position]
        sharedPreference = context.getSharedPreferences("TOKEN_PREF", Context.MODE_PRIVATE)

        holder.itemView.apply {
            if(!myPhoneno.equals(contacts.phoneno)){
                add_btn_ag.visible()
            }else{
                add_btn_ag.gone()
            }


            user_name_txt_ag.text = contacts.phoneno.toString()

            PushDownAnim.setPushDownAnimTo(add_btn_ag).setOnClickListener {
                if(addClick == false){
                    addClick=true
                    add_btn_ag.setImageResource(R.drawable.ic_chat_add_image)
                    newList.add(contacts.id)

                    //ADD list to pref
                    val editor = sharedPreference.edit()
                    val set: MutableSet<String> = HashSet()
                    set.addAll(newList)
                    editor.putStringSet("newCntList", set)
                    editor.apply()

                }else if(addClick == true){
                    addClick = false
                    add_btn_ag.setImageResource(R.drawable.ic_add_ag_image)
                    newList.remove(contacts.id)

                    val editor = sharedPreference.edit()
                    val set: MutableSet<String> = HashSet()
                    set.addAll(newList)
                    editor.putStringSet("newCntList", set)
                    editor.apply()
                    Log.i("checkClick",newList.toString())
                }
            }
        }
    }
}