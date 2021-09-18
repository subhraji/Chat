package com.example.chatapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapp.R
import com.example.chatapp.adapter.StatusListAdapter
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_status.*

data class StatusList(
    val userName: String,
    val time: String,
    val imageUrls: String
    )

class StatusListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillRecyclerView(dummylist)

        PushDownAnim.setPushDownAnimTo(status_list_top_lay).setOnClickListener {

        }
    }

    private fun fillRecyclerView(statusList: List<StatusList>) {
        statusListRecycler.adapter = StatusListAdapter(dummylist)
    }
    private val dummylist = listOf<StatusList>(
        StatusList("Dinesh","11 minutes ago","https://images.unsplash.com/photo-1606787620651-3f8e15e00662?ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80"),
        StatusList("Jonti","20 minutes ago","https://images.unsplash.com/photo-1606787620651-3f8e15e00662?ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80"),
        StatusList("Umesh","45 minutes ago","https://images.unsplash.com/photo-1606787620651-3f8e15e00662?ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80"),
        StatusList("Dayal","12 minutes ago","https://images.unsplash.com/photo-1606787620651-3f8e15e00662?ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80"),
        StatusList("Kishan","10 minutes ago","https://images.unsplash.com/photo-1606787620651-3f8e15e00662?ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80"),
        )
}