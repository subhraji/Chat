package com.example.chatapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.adapter.GroupMemberListAdapter
import com.example.chatapp.helper.gone
import com.example.chatapp.helper.loadingDialog
import com.example.chatapp.helper.visible
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.GetGroupMembersViewModel
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_group_chat.*
import kotlinx.android.synthetic.main.fragment_group_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupInfoFragment : Fragment() {

    lateinit var accessToken: String
    lateinit var groupId: String
    private var isAdmin: Boolean = false

    private val getGroupMembersViewModel: GetGroupMembersViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString("groupId").toString()
            isAdmin = it.getBoolean("isAdmin")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isAdmin){
            group_info_add_group_member_btn.gone()
            add_member_txt_below_view.gone()
        }else{
            group_info_add_group_member_btn.visible()
            add_member_txt_below_view.visible()
        }

        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()

        getGroupMembers()

        PushDownAnim.setPushDownAnimTo(group_info_add_group_member_btn).setOnClickListener {
            val bottomSheet = AddContactListFragment()
            val bundle = Bundle()
            bundle.putString("groupId", groupId)
            bottomSheet.arguments = bundle
            bottomSheet.show(requireActivity().supportFragmentManager, "AddContactList")
        }
    }

    private fun getGroupMembers(){
        val loader = requireActivity().loadingDialog()
        loader.show()
        getGroupMembersViewModel.getGroupMembers(accessToken,groupId).observe(viewLifecycleOwner, { outcome ->
            loader.dismiss()
            when(outcome){
                is Outcome.Success ->{
                    if (outcome.data.status == "success"){
                        val userList = outcome.data.group.users
                        group_info_group_name_txt.text = outcome.data.group.groupName
                        member_count_text.text = outcome.data.group.users.size.toString()+"  members"
                        GroupMemberListRecycler.adapter = GroupMemberListAdapter(userList,requireActivity())

                    }else{
                        Toast.makeText(activity,"error !!!", Toast.LENGTH_SHORT).show()
                    }
                }

                is Outcome.Failure<*> ->{
                    Toast.makeText(activity,outcome.e.message, Toast.LENGTH_SHORT).show()
                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })

    }

}