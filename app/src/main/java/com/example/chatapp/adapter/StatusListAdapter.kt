package com.example.chatapp.adapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.helper.inflate
import com.example.chatapp.view.fragment.FriendsChatImagePreviewFragment
import com.example.chatapp.view.fragment.StatusList
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_status.*
import kotlinx.android.synthetic.main.status_list_item.view.*

class StatusListAdapter(private val statusList: List<StatusList>): RecyclerView.Adapter<StatusListAdapter.StatusListViewHolder>()  {

    inner class StatusListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusListViewHolder {
        return StatusListViewHolder(parent.inflate(R.layout.status_list_item))
    }
    override fun getItemCount(): Int {
        return statusList.size
    }

    override fun onBindViewHolder(holder: StatusListAdapter.StatusListViewHolder, position: Int) {
        val status = statusList[position]
        holder.itemView.apply {
            status_list_username.text = status.userName
            status_list_time.text = status.time

            PushDownAnim.setPushDownAnimTo(status_list_item_root_lay).setOnClickListener {
                val bundle = Bundle()
                bundle.putString("imageUrl", status.imageUrls)
                bundle.putString("userName", status.userName)
                findNavController().navigate(R.id.statusViewFragment, bundle)
            }
        }
    }
}