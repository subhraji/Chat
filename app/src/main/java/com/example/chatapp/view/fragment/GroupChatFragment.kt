package com.example.chatapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_group_chat.*

class GroupChatFragment : Fragment() {

    private lateinit var groupId: String
    private lateinit var groupName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            groupId = it.getString("groupId").toString()
            groupName = it.getString("groupName").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        group_name_gr.text = groupName

        PushDownAnim.setPushDownAnimTo(chat_frg_back_arrow_gr).setOnClickListener {
            findNavController().navigateUp()
        }

        PushDownAnim.setPushDownAnimTo(add_member_btn).setOnClickListener {
            val bottomSheet = AddContactList()
            bottomSheet.show(requireActivity().supportFragmentManager, "AddContactList")
        }
    }

}