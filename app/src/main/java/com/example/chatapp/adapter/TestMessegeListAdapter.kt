package com.example.chatapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.helper.inflate
import kotlinx.android.synthetic.main.contact_list_item.view.*

class TestMessageListAdapter(private val userList: List<Message>, private val context: Context): RecyclerView.Adapter<TestMessageListAdapter.TestMessageListViewHolder>() {

    inner class TestMessageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestMessageListViewHolder {
        return TestMessageListViewHolder(parent.inflate(R.layout.contact_list_item))
    }
    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: TestMessageListAdapter.TestMessageListViewHolder, position: Int) {
        /*lateinit var navController: NavController
        navController = Navigation.findNavController(context as Activity, R.id.login_nav_host_fragment)*/

        val messages = userList[position]
        holder.itemView.apply {
            user_name_txt.text = messages.msg
        }
    }
}