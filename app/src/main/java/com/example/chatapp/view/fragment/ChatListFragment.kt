package com.example.chatapp.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapp.R
import com.example.chatapp.adapter.ChatUserAdapter
import com.example.chatapp.adapter.ContactListAdapter
import com.example.chatapp.model.pojo.chat_user.ChatUser
import com.example.chatapp.model.pojo.sync_contacts.User
import com.example.chatapp.viewmodel.ChatUserViewModel
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_contacts_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatListFragment : Fragment() {

    private val chatUserViewModel: ChatUserViewModel by viewModel()
    lateinit var userId: String
    private var size = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            //size = chatUserViewModel.getChatUserCount()
            withContext(Dispatchers.Main) {
                getChatUser()
            }
        }
    }

    private fun getChatUser(){
        chatUserViewModel.getChatUser().observe(requireActivity(), { users ->
            Log.d("userSize","user list size = ${users.size}")
            if (!users.isNullOrEmpty()) {
                chat_user_recycler.adapter = ChatUserAdapter(users,requireActivity())
            } else {
                Log.d("userSize","user list size => ${users.size}")
            }
        })
    }


}